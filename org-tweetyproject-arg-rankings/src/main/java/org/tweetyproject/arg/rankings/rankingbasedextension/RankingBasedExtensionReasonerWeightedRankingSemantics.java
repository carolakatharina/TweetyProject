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

import static java.lang.StrictMath.exp;

public class RankingBasedExtensionReasonerWeightedRankingSemantics extends AbstractExtensionReasoner {
    RankingSemantics rankingSemantics;
    Semantics extensionSemantics;


    public enum RankingSemantics {
        CATEGORIZER,
        STRATEGY,
        SAF,

        COUNTING,
        PROBABILISTIC,
        MAX,
        EULER_MB, TRUST, SERIALIZABLE

    }

    public RankingBasedExtensionReasonerWeightedRankingSemantics(Semantics extensionSemantics,
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
            case TRUST -> new TrustBasedRankingReasoner().getModel(bbase);
            case EULER_MB -> new EulerMaxBasedRankingReasoner().getModel(bbase);
            case SERIALIZABLE -> new SerialisabilityRankingReasoner(extensionSemantics).getModel(bbase);
        });

        Collection<Extension<DungTheory>> allExtensions = new HashSet<>();
        Collection<Extension<DungTheory>> potenzielleExtensions = new HashSet<>();
        System.out.println("Gewichte aller "+ranking.entrySet());
        System.out.println("Zahl aller Attacker gesamt "+bbase.getAttackers(bbase.getNodes()).size());
        System.out.println("Zahl aller Attacken gesamt "+bbase.getAttacks().size());
        System.out.println("Zahl aller Attackierten gesamt "+bbase.getAttacked(bbase.getNodes()).size());

        System.out.println("Zahl aller Knoten gesamt "+bbase.getNodes().size());

        AtomicReference<Double> weightAttackersAll = new AtomicReference<>(0.);
        AtomicReference<Double> weightAll = new AtomicReference<>(0.);
        AtomicReference<Double> weightAttacked = new AtomicReference<>(0.);
        ranking.entrySet().stream().forEach(
                entry ->{
                    if (bbase.getAttackers(bbase.getNodes()).contains(entry.getKey())
                    ) {
                        weightAttackersAll.set(weightAttackersAll.get() + entry.getValue());
                    }
                    if (bbase.getAttacked(bbase.getNodes()).contains(entry.getKey())
                    ) {
                        weightAttacked.set(weightAttacked.get() + entry.getValue());
                    }

                        weightAll.set(weightAll.get()+entry.getValue());
                    });


        // nur Argumente über bestimmten Threshold für Extensionen berücksichtigen
        Map<Argument, Double> akzeptableArgumente = new HashMap<>(ranking
                .entrySet().stream().filter(entry -> entry.getValue()> getThresholdSingle(weightAll.get(), weightAttackersAll.get(), weightAttacked.get(), bbase.getNodes().size(),
                        bbase.getAttackers(bbase.getNodes()).size(), bbase.getAttacks().size(), bbase.getAttacked(bbase.getNodes()).size()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));

        System.out.println("akzeptable Argumente" + akzeptableArgumente + " mit Threshold "+getThresholdSingle(weightAll.get(), weightAttackersAll.get(), weightAttacked.get(), bbase.getNodes().size(),
                bbase.getAttackers(bbase.getNodes()).size(),bbase.getAttacks().size(), bbase.getAttacked(bbase.getNodes()).size()));


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
                if (bbase.isAdmissable(e)) {


                    allExtensions.add(e);
                    allSums.put(e, sum);

                }


            }


        }

        //System.out.println("potenzielle Extensions:"+potenzielleExtensions);



        return getExtensionsForSemantics(ranking, allSums, allExtensions, bbase);
    }

    private Collection<Extension<DungTheory>> getExtensionsForSemantics(Map<Argument, Double> ranking, Map<Extension<DungTheory>, Double> allSums,
                                                                        Collection<Extension<DungTheory>> extensions,
                                                                        DungTheory bbase) {


        Collection<Extension<DungTheory>> allExtensionsFinal = new HashSet<>();
        var printext = extensions.stream().map(
                ext -> ext+ " "+allSums.get(ext)).collect(Collectors.toList());
        //System.out.println("alle admissible Extensions mit Summe: " + printext);

        for (Extension<DungTheory> e : extensions) {


            if (getConditionForSemantics(ranking, allSums, extensions, e, bbase)) {
                allExtensionsFinal.add(e);
            }
        }
        Collection<Extension<DungTheory>> finalAllExtensionsFinalTemp = allExtensionsFinal;


        allExtensionsFinal = getFinalExtensions(ranking, allSums,
                finalAllExtensionsFinalTemp, extensions);

        return allExtensionsFinal;
    }

    private boolean getConditionForSemantics(Map<Argument, Double> ranking, Map<Extension<DungTheory>, Double> allSums,
                                             Collection<Extension<DungTheory>> extensions, Extension<DungTheory> e,
                                             DungTheory bbase) {
        AtomicReference<Double> sumGesamt = new AtomicReference<>(0.0);
        allSums.values().stream().forEach(value -> sumGesamt.set(sumGesamt.get() + value));

        //System.out.println("Alle Attacker"+bbase.getAttackers(e));
        //System.out.println("Alle Attackierten"+bbase.getAttacked(e));
        //System.out.println("Alle bidirektionalen Attacken"+bbase.getBidirectionalAttacks());
        //System.out.println("Attackiert alle Argumente"+bbase.isAttackingAllOtherArguments(e));



        //System.out.println("Alle scc"+bbase.getStronglyConnectedComponents());
        //System.out.println("cycle"+bbase.containsCycle());

        switch (extensionSemantics) {
            //maximale complete extensions
            case PR: {
                //System.out.println("preferred"+e+" weights");
                e.stream().forEach(arg ->
                        System.out.println(ranking.get(arg)));

                List<Extension<DungTheory>> bessereExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext ->
                                        !ext.equals(e)
                                                && (ext.stream().anyMatch(
                                                arg -> ranking.get(arg) == 1
                                                        && !e.contains(arg))
                                                || ext.containsAll(e)
                                                && ext.size() > e.size())).collect(Collectors.toList()));
                //System.out.println("Extension:" + e);
                //System.out.println("BESSERE Extensions:" + bessereExtensions);

                return bessereExtensions.size() == 0;


            }
            // preferred extensions, die alle Argumente, die nicht dazugehören attackieren
            case ST: {

                System.out.println("stable"+e+" weights");
                e.stream().forEach(arg ->
                        System.out.println(ranking.get(arg)));
                return bbase.isAttackingAllOtherArguments(e);

            }
            case GR: {
                //minimale complete extensions
                /*System.out.println("grounded"+e+" weights");
                e.stream().forEach(arg ->
                        System.out.println(ranking.get(arg)));*/

                //System.out.println("IN"+e.getArgumentsOfStatus(ArgumentStatus.IN));

                //nur eine, miminales Subset von complete
                List<Extension<DungTheory>> bessereExtensions = new ArrayList<>(
                        extensions.stream()
                                .filter(ext ->
                                        !ext.equals(e)
                                                && (ext.stream().anyMatch(
                                                arg -> ranking.get(arg) == 1
                                                        && !e.contains(arg))
                                                || ext.containsAll(e)
                                                && ext.size() > e.size())).collect(Collectors.toList()));
                //System.out.println("Extension:" + e);
                //System.out.println("BESSERE Extensions:" + bessereExtensions);

                return bessereExtensions.size() == 0;


            }
            case CO: {
                // Extension ist konfliktfrei und verteidigt alle seine Elemente (=admissible) und es gibt kein Argument,
                // dass es verteidigt, aber dass nicht in E ist
                //System.out.println("complete"+e+" weights");
                e.stream().forEach(arg ->
                        System.out.println(ranking.get(arg)));
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
                //System.out.println("BESSERE Extensions:" + bessereExtensions);
               return
                       bessereExtensions.size()==0;

            }

            default: {
                System.out.println("Default");
                return true;
            }
        }

    }


    private Collection<Extension<DungTheory>> getFinalExtensions(Map<Argument, Double> ranking, Map<Extension<DungTheory>, Double> allSums,
                                                                 Collection<Extension<DungTheory>> extensions, Collection<Extension<DungTheory>> conflictfreeExtensions) {

        switch (extensionSemantics) {


            case PR: {
                //rausfiltern schlechterer Extensions mit gleichen Elementen
                Collection<Extension<DungTheory>> finalExtensions = new HashSet<>();
                Map<Extension<DungTheory>, List<Extension<DungTheory>>> alledoppelteExtensions = new HashMap<>();

                extensions.stream()
                        .forEach(ext -> {
                            var list = new ArrayList<>(extensions.stream().filter(e ->
                                    !e.equals(ext) && e.stream().anyMatch(arg ->
                                            ranking.get(arg) != 1 && ext.contains(arg))
                            ).collect(Collectors.toList()));
                            alledoppelteExtensions.put(ext, list);
                        });


                for (Extension<DungTheory> e : extensions) {


                    if (alledoppelteExtensions.get(e).size() == 0) {
                        finalExtensions.add(e);
                    } else {
                        System.out.println("doppelte Extensions" + alledoppelteExtensions + "von " + e + " mit Wert" + allSums.get(e)
                                + alledoppelteExtensions.get(e).stream().allMatch(ext1 ->
                                allSums.get(e) > allSums.get(ext1)));

                        if (alledoppelteExtensions.get(e).stream().allMatch(ext1 ->
                                allSums.get(e) > allSums.get(ext1))
                                || (alledoppelteExtensions.get(e).stream().noneMatch(ext2
                                -> alledoppelteExtensions.get(ext2).stream().allMatch(
                                ext3 -> allSums.get(ext2) > allSums.get(ext3))))) {
                            finalExtensions.add(e);
                        }
                    }

                }
                return finalExtensions;
            }
            case GR: {
                //minimale cmplete extensions


                //nur Extensions, die in allen gültigen preferredExtensions enthalten
                Collection<Extension<DungTheory>> finalExtensions;

                //ermittle preferred extensions
                //rausfiltern schlechterer Extensions mit gleichen Elementen
                Collection<Extension<DungTheory>> preferredExtensions = new HashSet<>();
                Map<Extension<DungTheory>, List<Extension<DungTheory>>> alledoppelteExtensions = new HashMap<>();

                extensions
                        .forEach(ext -> {
                            var list = new ArrayList<>(extensions.stream().filter(e ->
                                    !e.equals(ext) && e.stream().anyMatch(arg ->
                                            ranking.get(arg) != 1 && ext.contains(arg))
                            ).collect(Collectors.toList()));
                            alledoppelteExtensions.put(ext, list);
                        });


                for (Extension<DungTheory> e : extensions) {


                    if (alledoppelteExtensions.get(e).size() == 0) {
                        preferredExtensions.add(e);
                    } else {

                        if (alledoppelteExtensions.get(e).stream().allMatch(ext1 ->
                                allSums.get(e) > allSums.get(ext1))
                                || (alledoppelteExtensions.get(e).stream().noneMatch(ext2
                                -> alledoppelteExtensions.get(ext2).stream().allMatch(
                                ext3 -> allSums.get(ext2) > allSums.get(ext3))))) {
                            preferredExtensions.add(e);
                        }
                    }

                }
                finalExtensions = conflictfreeExtensions.stream().filter(ext ->
                                preferredExtensions.stream().allMatch(extpr ->
                                        extpr.containsAll(ext))
                        && preferredExtensions.stream().noneMatch(extpr ->
                                        extpr.stream().anyMatch(arg ->
                                                ranking.get(arg) == 1 && !ext.contains(arg))))
                        .collect(Collectors.toSet());

                Collection<Extension<DungTheory>> finalExtensions1 = finalExtensions;

                //System.out.print("temp"+finalExtensions1);
                return finalExtensions.stream()
                        .filter(ext ->
                            finalExtensions1.stream().noneMatch(e ->
                                    !e.equals(ext) && ext.stream().allMatch(arg ->
                                            e.contains(arg)
                                            && e.size() > ext.size()
                                    )
                                    ))
                            .collect(Collectors.toSet());
            }
            default: return extensions;
        }
    }




    private double getThresholdSingle(double weightAll, double weightAttacks, double weightAttacked, int numberAll, int numberAttacks,
                                      int numberAttacked, int numberattackers) {
        switch (this.rankingSemantics) {

            case CATEGORIZER -> {

                return (weightAttacked/(1*numberAttacks+(weightAttacks*numberAttacks)))/numberAttacked;
            }

            case MAX -> {
                var gewichtAttackierten = (weightAttacked);
                var gewichtAttacke = (weightAttacks);
                var attackenZahl = numberAttacks;
                return ((gewichtAttackierten/numberAttacked)/(1+(gewichtAttacke/attackenZahl)));
            }
            case EULER_MB -> {
                var gewichtAttackierten = (weightAttacked);
                var gewichtAttacke = (weightAttacks);
                var attackenZahl = numberAttacks;
                return ((gewichtAttackierten/numberAttacked) * exp(-(gewichtAttacke/attackenZahl)));
            }

            case TRUST -> {
                //vermutlich löschen/anpassen funktioniert nicht richtig
                return 0.2;
            }

            case COUNTING -> {
                //vermutlich löschen/anpassen funktioniert nicht richtig
                return 0.1;
            }
            case SERIALIZABLE -> { return 0.0;}
            default -> {
                return 0.5;
            }
        }
    }



}

