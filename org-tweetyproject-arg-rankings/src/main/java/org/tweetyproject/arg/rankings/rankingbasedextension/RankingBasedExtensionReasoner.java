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
        MAX

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
            case CATEGORIZER -> new CategorizerRankingReasoner().getModel(bbase);
            case STRATEGY -> new StrategyBasedRankingReasoner().getModel(bbase);
            case SAF -> new SAFRankingReasoner().getModel(bbase);
            case COUNTING -> new CountingRankingReasoner().getModel(bbase);
            case PROBABILISTIC ->
                    new ProbabilisticRankingReasoner(extensionSemantics, new Probability(0.5), false).getModel(bbase);
            case MAX -> new MaxBasedRankingReasoner().getModel(bbase);
        });
        Collection<Extension<DungTheory>> allExtensions = new HashSet<>();


        // nur Argumente über bestimmten Thresshold für Extensionen berücksichtigen
        Map<Argument, Double> akzeptableArgumente = new HashMap<>();
        int sumrejected=0;
        for (Argument arg : ranking.keySet()) {
            System.out.println(arg.getName() + ": " + ranking.get(arg));
            System.out.println("Cycle"+bbase.containsCycle());


            if (ranking.get(arg) > getThresholdSingle()) {
                akzeptableArgumente.put(arg, ranking.get(arg));
            } else {
                sumrejected = sumrejected +1;

            }
        }

        System.out.println("rejected"+sumrejected);

        for (int k = akzeptableArgumente.size(); k > 0; k--) {

            Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(akzeptableArgumente.size(), k);


            while (iterator.hasNext()) {
                final int[] combination = iterator.next();
                Collection<Argument> arguments = new HashSet<>();
                double sum = 0;
                for (int index : combination) {

                    var argument = (Argument) (akzeptableArgumente.keySet().toArray()[index]);
                    sum = sum + (Double)(akzeptableArgumente.values().toArray()[index]) ;
                    arguments.add(argument);

                }

                System.out.println("Summe"+sum);
                Extension<DungTheory> e = new Extension<>(arguments);

                if (getConditionForSemantics(ranking, sum,  bbase, e)) {
                    System.out.println("added");
                    allExtensions.add(e);
                }


            }


        }

        return allExtensions;
    }

    private boolean getConditionForSemantics(Map<Argument, Double> ranking, double sum, DungTheory bbase, Extension e) {
        if ((this.rankingSemantics == RankingSemantics.MAX) && extensionSemantics == Semantics.STABLE_SEMANTICS) {
            AtomicReference<Double> sumGesamt = new AtomicReference<>(0.0);
            ranking.values().stream().forEach(arg -> {
                sumGesamt.set(sumGesamt.get() + arg.doubleValue());
            });

            return  ((sumGesamt.get() - sum) / (ranking.size() - e.size()) < getThresholdSingle());

        } return true;
    }

    private double getThresholdSingle() {
        if ((this.rankingSemantics == RankingSemantics.MAX) && extensionSemantics==Semantics.STABLE_SEMANTICS) {
            return 0.021;
        }
        return 0.0;

    }


}

