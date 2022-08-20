package org.tweetyproject.arg.rankings.rankingbasedextension;


/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.*;
import org.tweetyproject.math.probability.Probability;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

// vermutlich so eher für gradual semantics, noch einen fuer reine ranking-semantics bauen
public class RankingBasedExtensionReasoner extends AbstractExtensionReasoner {
    RankingSemantics rankingSemantics;
    Semantics extensionSemantics;


    public enum RankingSemantics {

        CATEGORIZER,
        STRATEGY,
        SAF,

        COUNTING,
        PROBABILISTIC,
        MAX,
        EULER_MB, TRUST

    }

    public RankingBasedExtensionReasoner(Semantics extensionSemantics,
                                         RankingSemantics semantics) {

        System.out.println(semantics);
        this.rankingSemantics = semantics;
        this.extensionSemantics = extensionSemantics;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {

        return null;
    }


    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Map<Argument, Double> ranking;
        ranking = new HashMap<>(switch (this.rankingSemantics) {
            case CATEGORIZER -> new WeightedCategorizerRankingReasoner().getModel(bbase);
            case STRATEGY -> new StrategyBasedRankingReasoner().getModel(bbase);
            case SAF -> new SAFRankingReasoner().getModel(bbase);
            case COUNTING -> new CountingRankingReasoner().getModel(bbase);
            case PROBABILISTIC ->
                    new ProbabilisticRankingReasoner(extensionSemantics, new Probability(0.5), false).getModel(bbase);
            case MAX -> new MaxBasedRankingReasoner().getModel(bbase);
            case TRUST -> new TrustBasedRategorizerRankingReasoner().getModel(bbase);
            case EULER_MB -> new EulerMaxBasedRankingReasoner().getModel(bbase);
        });

        Collection<Extension<DungTheory>> allExtensions = new HashSet<>();
        Collection<Extension<DungTheory>> allExtensionsFinal = new HashSet<>();


        // nur Argumente über bestimmten Thresshold für Extensionen berücksichtigen
        Map<Argument, Double> akzeptableArgumente = new HashMap<>();
        int sumrejected = 0;
        for (Argument arg : ranking.keySet()) {
            System.out.println(arg.getName() + ": " + ranking.get(arg));
            System.out.println("Cycle" + bbase.containsCycle());


            if (ranking.get(arg) > getThresholdSingle()) {
                akzeptableArgumente.put(arg, ranking.get(arg));
            }
        }


        Map<Extension, Double> allSums = new HashMap<>();

        for (int k = akzeptableArgumente.size(); k > 0; k--) {

            Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(akzeptableArgumente.size(), k);


            while (iterator.hasNext()) {
                final int[] combination = iterator.next();
                Collection<Argument> arguments = new HashSet<>();
                double sum = 0;
                for (int index : combination) {

                    var argument = (Argument) (akzeptableArgumente.keySet().toArray()[index]);
                    sum = sum + (Double) (akzeptableArgumente.values().toArray()[index]);
                    arguments.add(argument);

                }

                Extension<DungTheory> e = new Extension<>(arguments);


                allExtensions.add(e);
                allSums.put(e, sum);


            }


        }

        for (Extension e : allExtensions) {

            if (getConditionForSemantics(ranking, allSums, bbase, allExtensions, e)) {
                allExtensionsFinal.add(e);
            }
        }

        return allExtensionsFinal;
    }

    private boolean getConditionForSemantics(Map<Argument, Double> ranking, Map<Extension, Double> allSums, DungTheory bbase,
                                             Collection<Extension<DungTheory>> extensions,
                                             Extension e) {
        AtomicReference<Double> sumGesamt = new AtomicReference<>(0.0);
        ranking.values().stream().forEach(arg -> {
            sumGesamt.set(sumGesamt.get() + arg.doubleValue());
        });

        for (Argument arg: ranking.keySet()) {

            if (!e.contains(arg) && ranking.get(arg).doubleValue()==1) {
                return false;
            }

            if (e.contains(arg) && ranking.get(arg).doubleValue()< getThresholdSingle()) {
                return false;
            }

        }
        if (extensionSemantics == Semantics.STABLE_SEMANTICS) {
            //TODO: Logische Überlegungen welche Bedingung



            return ((sumGesamt.get() - allSums.get(e)) / (ranking.size() - e.size()) < getThresholdSingle()
            );

        }
        if ( extensionSemantics == Semantics.PREFERRED_SEMANTICS) {
            //TODO: Logische Überlegungen welche Bedingung


            List<Extension<DungTheory>> bessereExtensions = extensions.stream()
                    .filter(ext ->
                            ((ext.size()> e.size()
                                    &&  allSums.get(ext) > allSums.get(e))

                            ))
                    .collect(Collectors.toList());

            return (bessereExtensions.size() == 0) && getMaxDiff(ranking.entrySet().stream().filter(entry -> e
                    .contains(entry.getKey()) && entry.getValue()!=1).collect(Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue)), e) ==0;


        }

        System.out.println("Stop!");
        return true;
    }

    private double getThresholdSingle() {
        if ((this.rankingSemantics == RankingSemantics.MAX) && extensionSemantics == Semantics.STABLE_SEMANTICS) {
            return 0.1;
        }
        if ((this.rankingSemantics == RankingSemantics.MAX) && extensionSemantics == Semantics.PREFERRED_SEMANTICS) {

            return 0.1;


        }

        if ((this.rankingSemantics == RankingSemantics.CATEGORIZER) && extensionSemantics == Semantics.PREFERRED_SEMANTICS) {

            return 0.00;


        }

        if ((this.rankingSemantics == RankingSemantics.TRUST) && extensionSemantics == Semantics.PREFERRED_SEMANTICS) {

            return 0.49;


        }

        if ((this.rankingSemantics == RankingSemantics.EULER_MB) && extensionSemantics == Semantics.PREFERRED_SEMANTICS) {

            return 0.04;


        }
        return 0.0;
    }

    private double getMaxDiff(Map<Argument, Double> ranking, Extension e) {
        if (e.size()==1) {
            return 0;
        }
        var werteListe = ranking.entrySet().stream().filter(entry -> e.contains(entry.getKey()))
                .collect(Collectors.toList());

        double max=0.;
        double min = 10.;

        for (var entry: werteListe) {
            var newValue = entry.getValue();
            if (newValue> max) {
                max = newValue;
            }
            if (newValue< min) {
                min = newValue;
            }
        }
        System.out.println("Diff: "+(max-min));
        return Math.abs(max-min);

    }


}

