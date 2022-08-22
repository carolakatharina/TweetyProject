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


        // nur Argumente über bestimmten Thresshold für Extensionen berücksichtigen
        Map<Argument, Double> akzeptableArgumente = new HashMap<>();
        for (Argument arg : ranking.keySet()) {
            System.out.println(arg.getName() + ": " + ranking.get(arg));
            System.out.println("Cycle" + bbase.containsCycle());


            if (ranking.get(arg) > getThresholdSingle()) {
                akzeptableArgumente.put(arg, ranking.get(arg));
            }
        }


        Map<Extension<DungTheory>, Double> allSums = new HashMap<>();

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

                if (bbase.isConflictFree(e)) {


                    allExtensions.add(e);
                    allSums.put(e, sum);

                }


            }


        }


        return getExtensionsForSemantics(ranking, allSums, allExtensions);
    }

    private Collection<Extension<DungTheory>> getExtensionsForSemantics(Map<Argument, Double> ranking, Map<Extension<DungTheory>, Double> allSums,
                                                                        Collection<Extension<DungTheory>> extensions) {


        Collection<Extension<DungTheory>> allExtensionsFinal = new HashSet<>();
        System.out.println("Konfliktfreie Extensions:" + extensions);

        for (Extension<DungTheory> e : extensions) {


            if (getConditionForSemantics(ranking, allSums, extensions, e)) {
                allExtensionsFinal.add(e);
            }
        }
        Collection<Extension<DungTheory>> finalAllExtensionsFinalTemp = allExtensionsFinal;

        System.out.println("extensionen nach 1. Filter: " + allExtensionsFinal);

        allExtensionsFinal = getFinalExtensions(ranking, allSums,
                finalAllExtensionsFinalTemp);
        System.out.println("extensionen nach 2. Filter: " + allExtensionsFinal);

        return allExtensionsFinal;
    }

    private boolean getConditionForSemantics(Map<Argument, Double> ranking, Map<Extension<DungTheory>, Double> allSums,
                                             Collection<Extension<DungTheory>> extensions, Extension<DungTheory> e) {
        AtomicReference<Double> sumGesamt = new AtomicReference<>(0.0);
        allSums.values().stream().forEach(value -> {
            sumGesamt.set(sumGesamt.get() + value);
        });

        switch (extensionSemantics) {
            case PR: {
                List<Extension<DungTheory>> bessereExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext ->
                                        !ext.equals(e)
                                                && (ext.stream().anyMatch(
                                                arg -> ranking.get(arg).doubleValue() == 1
                                                        && !e.contains(arg))
                                                || e.stream().allMatch(ext::contains)
                                                && ext.size() > e.size())).collect(Collectors.toList()));
                System.out.println("Extension:" + e);
                System.out.println("BESSERE Extensions:" + bessereExtensions);

                return bessereExtensions.size() == 0;

            }
            case ST: {
                return (sumGesamt.get() - allSums.get(e)) / (ranking.size() - e.size()) < this.getThresholdSingle();

            }
            case GR: {

                List<Extension<DungTheory>> bessereExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext ->
                                        !ext.equals(e)
                                                && ext.size() < e.size()
                                ).collect(Collectors.toList()));
                return bessereExtensions.size() == 0;


            }
            case CO:
                List<Extension<DungTheory>> bessereExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext ->
                                        !ext.equals(e)
                                                && (ext.stream().anyMatch(
                                                arg -> ranking.get(arg).doubleValue() == 1
                                                        && !e.contains(arg))
                                                || ext.containsAll(e)
                                                && ext.size() > e.size()
                                                || (ext.size() > e.size() && allSums.get(ext) > allSums.get(e)
                                        ))).collect(Collectors.toList()));
                return bessereExtensions.size() == 0;


            default: {
                System.out.println("Default");
                return true;
            }
        }

    }


    private Collection<Extension<DungTheory>> getFinalExtensions(Map<Argument, Double> ranking, Map<Extension<DungTheory>, Double> allSums,
                                       Collection<Extension<DungTheory>> extensions) {

        //rausfiltern schlechterer Extensions mit gleichen Elementen
        Collection<Extension<DungTheory>> finalExtensions = new HashSet<>();
        Map<Extension<DungTheory>, List<Extension<DungTheory>>> alledoppelteExtensions = new HashMap<>();

        extensions.stream()
                .forEach(ext -> {
                    var list = new ArrayList<>(extensions.stream().filter(e->
                            !e.equals(ext) && e.stream().anyMatch(arg ->
                            ranking.get(arg).doubleValue() != 1 && ext.contains(arg))
                    ).collect(Collectors.toList()));
                    alledoppelteExtensions.put(ext, list);
                });


        for (Extension<DungTheory> e: extensions) {


            if (alledoppelteExtensions.get(e).size()==0) {
                finalExtensions.add(e);
            } else {
                System.out.println("doppelte Extensions"+alledoppelteExtensions +"von "+e+" mit Wert"+allSums.get(e)
                +alledoppelteExtensions.get(e).stream().allMatch(ext1 ->
                        allSums.get(e) > allSums.get(ext1)));

                if (alledoppelteExtensions.get(e).stream().allMatch(ext1 ->
                        allSums.get(e) > allSums.get(ext1))
                || (alledoppelteExtensions.get(e).stream().noneMatch(ext2
                -> alledoppelteExtensions.get(ext2).stream().allMatch(
                        ext3-> allSums.get(ext2) > allSums.get(ext3))))) {
                    finalExtensions.add(e);
                }
            }

        }
        return finalExtensions;
        }




    private double getThresholdSingle() {
        switch (this.rankingSemantics) {

            case CATEGORIZER -> {
                System.out.println("getThresholdSingle for CAT");

                return 0.005;
            }

            case MAX -> {
                return 0.1;
            }
            case EULER_MB -> {
                return 0.2;
            }
            case TRUST -> {
                return 0.2;
            }
            default -> {
                return 0.5;
            }
        }
    }

    private double getMaxDiff(Map<Argument, Double> ranking, Extension<DungTheory> e) {
        if (e.size() == 1 || e.stream().allMatch(arg -> ranking.get(arg) == null)) {
            return 0;
        }
        var werteListe = ranking.entrySet().stream().filter(entry -> e.contains(entry.getKey()))
                .collect(Collectors.toList());

        double max = 0.;
        double min = 10.;

        for (var entry : werteListe) {
            var newValue = entry.getValue();
            if (newValue > max) {
                max = newValue;
            }
            if (newValue < min) {
                min = newValue;
            }
        }
        System.out.println("Diff: " + (max - min));
        return Math.abs(max - min);

    }


}

