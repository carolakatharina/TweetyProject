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
import org.tweetyproject.arg.rankings.reasoner.WeightedCategorizerRankingReasoner;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RankingBasedExtensionReasonerWeightedRankingSemanticsCat extends AbstractExtensionReasoner {
    RankingSemantics rankingSemantics;
    Semantics extensionSemantics;

    //TODO fragen klären: GIBT ES eine Moeglichkeit, die admissible sets über die Werte zu identifizieren?
    // dann wie complete set herausfinden über Werte

    public enum RankingSemantics {
        //TODO: gibt es Strategien, die nicht so komplex/rekursiv, die funktionieren?

        CATEGORIZER

    }

    public RankingBasedExtensionReasonerWeightedRankingSemanticsCat(Semantics extensionSemantics,
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
        ranking = getRanking(bbase);

        Collection<Extension<DungTheory>> allExtensions = new HashSet<>();
        Collection<Extension<DungTheory>> potenzielleExtensions = new HashSet<>();
        System.out.println("Gewichte aller " + ranking.entrySet());
        System.out.println("Zahl aller Attacker gesamt " + bbase.getAttackers(bbase.getNodes()).size());
        System.out.println("Zahl aller Attacken gesamt " + bbase.getAttacks().size());
        System.out.println("Zahl aller Attackierten gesamt " + bbase.getAttacked(bbase.getNodes()).size());

        System.out.println("Zahl aller Knoten gesamt " + bbase.getNodes().size());

        AtomicReference<Double> weightAttackersAll = new AtomicReference<>(0.);
        AtomicReference<Double> weightAll = new AtomicReference<>(0.);
        AtomicReference<Double> weightAttacked = new AtomicReference<>(0.);
        ranking.entrySet().stream().forEach(
                entry -> {
                    if (bbase.getAttackers(bbase.getNodes()).contains(entry.getKey())
                    ) {
                        weightAttackersAll.set(weightAttackersAll.get() + entry.getValue() * bbase.getAttacked(entry.getKey()).size());
                    }
                    if (bbase.getAttacked(bbase.getNodes()).contains(entry.getKey())
                    ) {
                        weightAttacked.set(weightAttacked.get() + entry.getValue());
                    }

                    weightAll.set(weightAll.get() + entry.getValue());
                });


        // nur Argumente über bestimmten Threshold für Extensionen berücksichtigen
        var thresholdIn = getThresholdIn(
        );


        var thresholdOut = getThresholdOut(
        );

        Map<Argument, Double> akzeptableArgumente = getAkzeptableArgumenteCred(bbase);

        System.out.println("akzeptable Argumente" + akzeptableArgumente + " mit Threshold Out " + thresholdOut + " und Threshold In " + thresholdIn);


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
                potenzielleExtensions.add(e);
                //Zu Labelling-Semantik
                // Alle Attacker sind legally out = legally in
                // Mindestens 1 In-Attacker: legally out
                // kein Attacker mit label in und mindestens 1 Attacker mit undecided = legally undecided

                //admissible: alle in-Argumente sind legally in und alle out-Argumente sind legally out:
                //complete: alle Argumente legally gelabelt
                // grounded: complete mit minimalen Argumenten, die legally in
                // preferred: complete mit maximalen Argumenten, die legally in
                // stable: complete ohne undec Argumente

                if (isAdmissible(bbase, ranking, e)) {


                    allExtensions.add(e);
                    allSums.put(e, sum);

                }


            }

        }
        var ext = new Extension<DungTheory>();
        allExtensions.add(ext);
        allSums.put(ext, 0.);

        System.out.println("potenzielle Extensions:" + allExtensions);


        return getExtensionsForSemantics(ranking, allSums, allExtensions, bbase);
    }

    private Map<Argument, Double> getRanking(DungTheory bbase) {
        Map<Argument, Double> ranking;
        ranking = new HashMap<>(switch (this.rankingSemantics) {
            case CATEGORIZER -> new WeightedCategorizerRankingReasoner().getModel(bbase);
        });
        return ranking;
    }

    public Map<Argument, Double> getAkzeptableArgumenteCred(DungTheory bbase) {
        return new HashMap<>(getRanking(bbase)
                .entrySet().stream().filter(entry -> entry.getValue() > getThresholdOut())
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
    }


    private Map<Argument, Double> getAkzeptableArgumenteScept(DungTheory bbase) {
        Map<Argument, Double> akzeptableArgumente = new HashMap<>(getRanking(bbase)
                .entrySet().stream().filter(entry -> entry.getValue() ==1)
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
        return akzeptableArgumente;
    }

    private static boolean isAdmissible(DungTheory bbase, Map<Argument, Double> ranking, Extension<DungTheory> e) {

        if (!isConflictFree(bbase, e)) {
            return false;
        }
        AtomicReference<Map<Argument, Double>> attacksums = new AtomicReference<>(new HashMap<>());
        AtomicReference<Map<Argument, Double>> defendsums = new AtomicReference<>(new HashMap<>());
        AtomicReference<Map<Argument, Integer>> defendcounts = new AtomicReference<>(new HashMap<>());

        e.stream().forEach(arg -> {
            if (bbase.getAttackers(arg).isEmpty()) {
                attacksums.get().put(arg, 0.);
                defendsums.get().put(arg, 0.);
                defendcounts.get().put(arg, 0);
            } else {
                attacksums.get().put(arg,
                        bbase.getAttackers(arg).stream().map(att -> ranking.get(att)).mapToDouble(Double::doubleValue).sum());

                defendsums.get().put(arg,
                        bbase.getAttackers(arg).stream().map(att -> {
                            return bbase.getAttackers(att).isEmpty() ? 0. : bbase.getAttackers(att).stream().filter(att2 -> e.contains(att2))
                                    .map(att3 -> ranking.get(att3)).mapToDouble(Double::doubleValue).sum();
                        }).mapToDouble(Double::doubleValue).sum());
                defendcounts.get().put(arg,
                        bbase.getAttackers(arg).stream().map(att -> {
                            return (int) bbase.getAttackers(att).stream().filter(att2 -> e.contains(att2)).count();
                        }).mapToInt(Integer::intValue).sum());
            }
        });


        //Admissible: Zahl der Angriffe auf Argument muss kleiner sein als Gewicht der Angriffe der Extension auf Angreifer

        return e.stream().allMatch(arg -> defendcounts.get().get(arg) >= bbase.getAttackers(arg).size());


    }


    private static boolean isConflictFree(DungTheory bbase, Extension<DungTheory> e) {
        return bbase.isConflictFree(e);
    }

    private Collection<Extension<DungTheory>> getExtensionsForSemantics(Map<Argument, Double> ranking, Map<Extension<DungTheory>, Double> allSums,
                                                                        Collection<Extension<DungTheory>> extensions,
                                                                        DungTheory bbase) {


        Collection<Extension<DungTheory>> allExtensionsFinal = new HashSet<>();
        var printext = extensions.stream().map(
                ext -> ext + " " + allSums.get(ext)).collect(Collectors.toList());
        //System.out.println("alle admissible Extensions mit Summe: " + printext);

        for (Extension<DungTheory> e : extensions) {


            if (getConditionForSemantics(ranking, allSums, extensions, e, bbase)) {
                allExtensionsFinal.add(e);
            }
        }


        return allExtensionsFinal;
    }

    private boolean getConditionForSemantics(Map<Argument, Double> ranking, Map<Extension<DungTheory>, Double> allSums,
                                             Collection<Extension<DungTheory>> extensions, Extension<DungTheory> e,
                                             DungTheory bbase) {
        AtomicReference<Double> sumGesamt = new AtomicReference<>(0.0);
        ranking.values().stream().forEach(value -> sumGesamt.set(sumGesamt.get() + value));
        AtomicReference<Map<Argument, Double>> attacksums = new AtomicReference<>(new HashMap<>());
        AtomicReference<Map<Argument, Double>> defendsums = new AtomicReference<>(new HashMap<>());


        e.stream().forEach(arg -> {
            if (bbase.getAttackers(arg).isEmpty()) {
                attacksums.get().put(arg, 0.);
                defendsums.get().put(arg, 0.);
            } else {
                attacksums.get().put(arg,
                        bbase.getAttackers(arg).stream().map(att -> ranking.get(att)).mapToDouble(Double::doubleValue).sum());

                defendsums.get().put(arg,
                        bbase.getAttackers(arg).stream().map(att -> {
                            return bbase.getAttackers(att).isEmpty() ? 0. : bbase.getAttackers(att).stream().filter(att2 -> e.contains(att2))
                                    .map(att3 -> ranking.get(att3)).mapToDouble(Double::doubleValue).sum();
                        }).mapToDouble(Double::doubleValue).sum());
            }
        });

        var neueCondition1 = e.stream().allMatch(arg -> attacksums.get().get(arg) == 0. || defendsums.get().get(arg) > attacksums.get().get(arg));
        var neueCondition2 = attacksums.get().entrySet().stream().map(entry -> entry.getValue()).mapToDouble(Double::doubleValue).sum() <
                defendsums.get().entrySet().stream().map(entry -> entry.getValue()).mapToDouble(Double::doubleValue).sum() || bbase.getAttackers(e).isEmpty();

        switch (extensionSemantics) {
            //maximale complete extensions
            case PR: {

                List<Extension<DungTheory>> bessereExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext ->
                                        !ext.equals(e)
                                                && ext.containsAll(e)
                                                && allSums.get(ext) > allSums.get(e)).collect(Collectors.toList()));


                return bessereExtensions.size() == 0;


            }
            // preferred extensions, die alle Argumente, die nicht dazugehören attackieren
            case ST: {

                List<Extension<DungTheory>> bessereExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext ->
                                        !ext.equals(e)
                                                && ext.containsAll(e)
                                                && (ranking.entrySet().stream().allMatch(entry -> ext.contains(entry.getKey())
                                                || bbase.getAttacked(e).contains(entry.getKey())))
                                                && allSums.get(ext) > allSums.get(e)).collect(Collectors.toList()));


                return bessereExtensions.size() == 0 && ranking.entrySet().stream().allMatch(entry -> e.contains(entry.getKey())
                        || bbase.getAttacked(e).contains(entry.getKey()));

            }
            case GR: {
                //minimale complete extensions


                //nur eine, miminales Subset von complete, in allen complete extensions enthalten!

                List<Extension<DungTheory>> allCompleteExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext -> extensions.stream().noneMatch(ext2 -> !ext2.equals(ext)
                                        && (ext2.stream().anyMatch(
                                        arg ->  !ext.contains(arg)
                                                && (ranking.get(arg) == 1
                                                || bbase.getAttacked(ext).containsAll(bbase.getAttackers(arg))))))).collect(Collectors.toList()));


                return allCompleteExtensions.contains(e) &&
                        allCompleteExtensions.stream().allMatch(ext -> ext.containsAll(e));


            }
            case CO: {
                // Extension ist konfliktfrei und verteidigt alle seine Elemente (=admissible) und es gibt kein Argument,
                // dass es verteidigt, aber dass nicht in E ist
                //System.out.println("complete"+e+" weights");

                //Extensions haben kein Argument, dass sie verteidigen aber dass nicht in E

                List<Extension<DungTheory>> bessereExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext ->
                                        !ext.equals(e)
                                                && (ext.stream().anyMatch(
                                                arg ->  !e.contains(arg)
                                                        && (ranking.get(arg) == 1
                                                        || bbase.getAttackers(arg).stream()
                                                        .allMatch(attacker ->
                                                                bbase.getAttacked(e).contains(attacker)))
                                        )))
                                .collect(Collectors.toList()));



                List<Extension<DungTheory>> alleCompleteExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext -> extensions.stream().noneMatch( ext2 ->
                                        !ext.equals(ext2)
                                                && bbase.getAttackers(ext).stream().anyMatch(attacker ->
                                                bbase.getAttackers(attacker).stream().anyMatch(att -> !ext.contains(att)
                                        && ext2.contains(att)))))
                                .collect(Collectors.toList()));
                System.out.println(alleCompleteExtensions);

                return
                        bessereExtensions.size() == 0 &&
                                alleCompleteExtensions.contains(e);
            }

            default: {
                System.out.println("Default");
                return true;
            }
        }

    }


    private double getThresholdOut() {
        switch (this.rankingSemantics) {

            case CATEGORIZER -> {

                return 0.06;
            }

            default -> {
                return 0.05;
            }
        }
    }


    private double getThresholdIn() {
        switch (this.rankingSemantics) {

            case CATEGORIZER -> {


                return 0.25;
            }

            default -> {
                return 0.5;
            }
        }
    }


}

