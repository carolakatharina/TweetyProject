package org.tweetyproject.arg.rankings.rankingbasedextension.exactreasoner;


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

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleNaiveReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.ExactNumericalPartialOrder;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


public class ExactGeneralRankingBasedExtensionReasoner extends AbstractExtensionReasoner {


    RankingSemantics rankingSemantics;
    RankingSemantics rankingSemanticsHelp;

    Vorgehensweise vorgehensweise;
    BigDecimal threshhold;

    BigDecimal epsilon;

    Vergleichsoperator vergleichsoperator;

    Akzeptanzbedingung akzeptanzbedingung;

    private Map<Argument, BigDecimal> ranking;

    private AbstractExactNumericalPartialOrderRankingReasoner reasoner;
    private Collection<Extension<DungTheory>> finalAllExtensions;


    public enum Vorgehensweise {
        STRONGEST_CF, MAX_CF, CF, INC_BUDGET, ADMISSIBLE, SIMPLE

    }

    public enum Vergleichsoperator {
        STRICT, NOT_STRICT
    }

    public enum Akzeptanzbedingung {
        RB_ARG_ABS_STRENGTH,
        RB_ARG_REL_STRENGTH,
        RB_ARG_ABS_AND_ATT_REL_STRENGTH,
        RB_ARG_ABS_OR_ARG_REL_STRENGTH,
        RB_ATT_ABS_AND_REL_STRENGTH_OR_ARG_STRENGTH_ABS,
        RB_ATT_ABS_OR_REL_STRENGTH_AND_ARG_STRENGTH_ABS,
        RB_ATT_STRENGTH_ARG_STRENGTH_ABS_AND_REL_STRENGTH,
        RB_ATT_ABS_OR_ARG_ABS_OR_ARG_REL_STRENGTH,
        RB_ATT_ABS_STRENGTH,
        RB_ATT_ABS_ARG_ABS_STRENGTH,
        RB_ATT_ABS_OR_REL_ARG_STRENGTH,
        RB_ATT_ABS_AND_REL_ARG_STRENGTH,
        RB_ATT_ABS_AND_REL_STRENGTH_AND_ARG_STRENGTH_ABS, RB_ATT_ABS_OR_ARG_ABS_STRENGTH
    }


    public enum RankingSemantics {
        CATEGORIZER,

        COUNTING,

        MAX, MAX_NSA,
        TRUST, NSA, ALPHABBS_0, ALPHABBS_1, ALPHABBS_2, EULER, ITS, MATT_TONI

    }

    public ExactGeneralRankingBasedExtensionReasoner(Akzeptanzbedingung akzeptanzbedingung,
                                                     RankingSemantics semantics, Vorgehensweise vorgehensweise, BigDecimal threshhold,
                                                     Vergleichsoperator vergleichsoperator, BigDecimal epsilon) {

        this.rankingSemantics = semantics;
        this.akzeptanzbedingung=akzeptanzbedingung;
        this.vorgehensweise = vorgehensweise;
        this.threshhold = threshhold;
        this.vergleichsoperator=vergleichsoperator;
        this.epsilon=epsilon;
        this.reasoner=getReasoner(semantics);

        System.out.println("Ranking Semantic: "+this.rankingSemantics);
        System.out.println("Vorgehen: "+this.vorgehensweise);
        System.out.println("Akzeptanzbedingung: "+this.akzeptanzbedingung);
        System.out.println("Schwellwert: "+this.threshhold);
        System.out.println("Vergleichsoperator: "+this.vergleichsoperator);
        System.out.println("Epsilon: "+this.epsilon);
    }


    public ExactGeneralRankingBasedExtensionReasoner(Akzeptanzbedingung akzeptanzbedingung,
                                                     Vorgehensweise vorgehensweise,
                                                     Vergleichsoperator vergleichsoperator, BigDecimal epsilon,
                                                     BigDecimal threshhold, BigDecimal threshholdHelper, RankingSemantics semantics,
                                                     RankingSemantics rankingSemanticsHelp) {


        this.rankingSemantics = semantics;
        this.akzeptanzbedingung = akzeptanzbedingung;
        this.vorgehensweise = vorgehensweise;
        this.threshhold = threshhold;
        this.vergleichsoperator = vergleichsoperator;
        this.epsilon = epsilon;
        this.reasoner = getReasoner(semantics);
        this.rankingSemanticsHelp = rankingSemanticsHelp;





        System.out.println("Ranking Semantic: " + this.rankingSemantics);
        System.out.println("Vorgehen: " + this.vorgehensweise);
        System.out.println("Akzeptanzbedingung: " + this.akzeptanzbedingung);
        System.out.println("Schwellwert: " + this.threshhold);
        System.out.println("Vergleichsoperator: " + this.vergleichsoperator);
        System.out.println("Epsilon: " + this.epsilon);
    }


    public AbstractExactNumericalPartialOrderRankingReasoner getReasoner(RankingSemantics rankingSemantics) {
        return switch (rankingSemantics) {
            case CATEGORIZER -> new ExactCategorizerRankingReasoner(epsilon);
            case EULER -> new ExactEulerMaxBasedRankingReasoner(epsilon);
            case MAX_NSA -> new ExactMaxBasedRankingReasoner_NSA(epsilon);
            case ITS -> new ExactIterativeSchemaRankingReasoner(epsilon);
            case COUNTING -> new ExactCountingRankingReasoner(BigDecimal.valueOf(0.9), epsilon);
            case MAX -> new ExactMaxBasedRankingReasoner(epsilon);
            case TRUST -> new ExactTrustBasedRankingReasoner(epsilon);
            case NSA -> new ExactNsaReasoner(epsilon);
            case ALPHABBS_0 -> new ExactAlphaBurdenBasedRankingReasoner(epsilon, BigDecimal.valueOf(1.));
            case ALPHABBS_1 -> new ExactAlphaBurdenBasedRankingReasoner(epsilon, BigDecimal.valueOf(0.3));
            case ALPHABBS_2 -> new ExactAlphaBurdenBasedRankingReasoner(epsilon, BigDecimal.valueOf(10.));
            case MATT_TONI -> new ExactStrategyBasedRankingReasoner();
        };
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {

        return null;
    }


    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        ranking = getRanking(bbase);
        System.out.println(ranking);

        var exts = switch (this.vorgehensweise) {
            case INC_BUDGET -> null;
            case MAX_CF -> getExtensionsForSemantics_MaxConflictfree(ranking, bbase);
            case CF -> getExtensionsForSemantics_Conflictfree(ranking, bbase);
            case ADMISSIBLE -> getAdmissibleExt(bbase);
            case SIMPLE -> getExtensionsForSemantics_Simple(ranking, bbase);
            case STRONGEST_CF -> getStrongest(ranking, getExtensionsForSemantics_MaxConflictfree(ranking, bbase));
        };



        return exts;

    }



    private HashSet<Extension<DungTheory>> getAdmissibleExt(DungTheory bbase) {

        HashSet<Extension<DungTheory>> exts = new HashSet<>();
        var rankingOrdered = this.sortByValue(new HashMap<>(ranking));
        Collections.reverse(rankingOrdered);
        var candidates = new ArrayList<>();
        Extension<DungTheory> ext = new Extension();

        var rankingValues = rankingOrdered.stream().map(Map.Entry::getValue).distinct().collect(Collectors.toList());

            for (var val : rankingValues) {

                    candidates
                            .addAll(rankingOrdered.stream()
                                    .filter(arg -> Objects.equals(arg.getValue(), val)).map(Map.Entry::getKey).collect(Collectors.toList()));

                    ext = new Extension(candidates);
                    if (!bbase.isAdmissable(ext)) {
                        candidates
                                .removeAll(rankingOrdered.stream()
                                        .filter(arg -> Objects.equals(arg.getValue(), val)).map(Map.Entry::getKey).collect(Collectors.toList()));
                        ext = new Extension(candidates);
                        break;
                    }


            }

            exts.add(ext);




        if (exts.size()==0) {
            exts.add(new Extension<DungTheory>());
        }



        return exts;

    }

    private Collection<Extension<DungTheory>> getStrongest(Map<Argument, BigDecimal> ranking, Collection<Extension<DungTheory>> extensionsForSemanticsMaxConflictfree) {
        var strongestExt = extensionsForSemanticsMaxConflictfree.stream()
                .findFirst().get();
        var sumStrongest = strongestExt.stream().mapToDouble(arg -> ranking.get(arg).doubleValue()).sum();
        for (var ext : extensionsForSemanticsMaxConflictfree) {
            var sum = ext.stream().mapToDouble(arg -> ranking.get(arg).doubleValue()).sum();
            if (sum > sumStrongest) {
                strongestExt = ext;
            }

        }
        return List.of(strongestExt);
    }



    public ExactNumericalPartialOrder<Argument, DungTheory> getRanking(DungTheory bbase) {


        return reasoner.getModel(bbase);


    }



        public List<Map.Entry<Argument, BigDecimal>> sortByValue(Map<Argument, BigDecimal> map) {

            List<Map.Entry<Argument, BigDecimal>> list = new ArrayList<>(map.entrySet());
            list.sort(Map.Entry.comparingByValue());



            return list;

    }



    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Simple(Map<Argument, BigDecimal> ranking,
                                                                               DungTheory bbase) {
        finalAllExtensions =  new HashSet<Extension<DungTheory>>();


        finalAllExtensions.add(getSetForSemantics(ranking, bbase, bbase));



        return finalAllExtensions;
    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_MaxConflictfree(Map<Argument, BigDecimal> ranking,
                                                                                        DungTheory bbase) {

        var simplereasoner = new SimpleNaiveReasoner();


        //alle Kandidaten erhalten, die Rankingkriterien erfüllen:
        var candidates = getSetForSemantics(ranking, bbase, bbase);
        Set<Argument> candidatesWithouSA = new HashSet<>(candidates);
        for (Argument argument : bbase) {
            if (bbase.isAttackedBy(argument, argument)) {
                candidatesWithouSA.remove(argument);
            }
        }

        return simplereasoner.getMaximalConflictFreeSets(bbase, candidatesWithouSA);

    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Conflictfree(Map<Argument, BigDecimal> ranking,
                                                                                     DungTheory bbase) {


        var restrictedtheory = new DungTheory(bbase);


        //alle Kandidaten erhalten, die Rankingkriterien erfüllen:
        var candidates = getSetForSemantics(ranking, restrictedtheory, bbase);


        return this.getConflictFreeSets(bbase, ranking.keySet().stream()
                .filter(candidates::contains).collect(Collectors.toList()));

    }


    /**
     * computes all conflict-free sets of bbase, that contain only arguments in candidates
     *
     * @param bbase      an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Collection<Extension<DungTheory>> getConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        Set<Argument> candidatesWithouSA = new HashSet<>(candidates);
        for (Argument argument : bbase) {
            if (bbase.isAttackedBy(argument, argument)) {
                candidatesWithouSA.remove(argument);
            }
        }
        return (Set<Extension<DungTheory>>) new WeaklyAdmissibleReasoner().getConflictFreeSets(bbase, candidatesWithouSA)
                .stream().map(ext -> new Extension<DungTheory>(ext)).collect(Collectors.toSet());

    }


    private Extension<DungTheory> getSetForSemantics(Map<Argument, BigDecimal> ranking, Collection<Argument> e,
                                                     DungTheory bbase) {


        switch (this.akzeptanzbedingung) {

            // absolute argument strength
            case RB_ARG_ABS_STRENGTH -> {
                return new Extension<DungTheory>(e.stream().filter(arg ->
                        useThresholdArg(ranking.get(arg), threshhold)).collect(Collectors.toSet()));


            }

            // relative argument strength
            case RB_ARG_REL_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return attackers.stream().allMatch(att -> useThresholdArg(ranking.get(arg), ranking.get(att)));
                }).collect(Collectors.toSet()));

            }

            // absolute attack strength
            case RB_ATT_ABS_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold));
                }).collect(Collectors.toSet()));
            }
            // absolute argument strength and absolute attack strength
            case RB_ATT_ABS_ARG_ABS_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return useThresholdArg(ranking.get(arg), threshhold) &&
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold));
                }).collect(Collectors.toList()));

            }
            //absolute argument and relative argument strength

            case RB_ARG_ABS_AND_ATT_REL_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return useThresholdArg(ranking.get(arg), threshhold) &&
                            attackers.stream().allMatch(att ->
                                    useThresholdAtt(ranking.get(att), ranking.get(arg))
                            );
                }).collect(Collectors.toList()));
            }

            //absolute attack and relative argument strength
            case RB_ATT_ABS_AND_REL_ARG_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold)
                                    && useThresholdAtt(ranking.get(att), ranking.get(arg))
                            );
                }).collect(Collectors.toList()));
            }

            //absolute attack or relative argument strength
            case RB_ATT_ABS_OR_REL_ARG_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold)
                                    || useThresholdAtt(ranking.get(att), ranking.get(arg))
                            );
                }).collect(Collectors.toList()));
            }

            //absolute attack or absolute argument strength
            case RB_ATT_ABS_OR_ARG_ABS_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return useThresholdArg(ranking.get(arg), threshhold) ||
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold)

                            );
                }).collect(Collectors.toList()));
            }

            //absolute argument or relative argument strength

            case RB_ARG_ABS_OR_ARG_REL_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return useThresholdArg(ranking.get(arg), threshhold) ||
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), ranking.get(arg))
                            );
                }).collect(Collectors.toList()));

            }

            //absolute ARGUMENT or absolute ATTACK or relative argument strength

            case RB_ATT_ABS_OR_ARG_ABS_OR_ARG_REL_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return useThresholdArg(ranking.get(arg), threshhold) ||
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold)
                                    || useThresholdAtt(ranking.get(att), ranking.get(arg))
                            );
                }).collect(Collectors.toList()));
            }

            //absolute ARGUMENT OR absolute ATTACK AND relative argument strength
            case RB_ATT_ABS_AND_REL_STRENGTH_OR_ARG_STRENGTH_ABS -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return useThresholdArg(ranking.get(arg), threshhold) ||
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold)
                                    && useThresholdAtt(ranking.get(att), ranking.get(arg))
                            );
                }).collect(Collectors.toList()));
            }
            //absolute ARGUMENT AND absolute ATTACK STRENGTH or relative argument strength
            case RB_ATT_ABS_OR_REL_STRENGTH_AND_ARG_STRENGTH_ABS -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return useThresholdArg(ranking.get(arg), threshhold) &&
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold)
                                    || useThresholdAtt(ranking.get(att), ranking.get(arg))
                            );
                }).collect(Collectors.toList()));
            }

            //absolute ARGUMENT AND absolute ATTACK STRENGTH or relative argument strength
            case RB_ATT_ABS_AND_REL_STRENGTH_AND_ARG_STRENGTH_ABS -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return useThresholdArg(ranking.get(arg), threshhold) &&
                            attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold)
                                    && useThresholdAtt(ranking.get(att), ranking.get(arg))
                            );
                }).collect(Collectors.toList()));
            }

        }

        return null;
    }


    private boolean useThresholdArg(BigDecimal value, BigDecimal thresh) {


        return switch (this.rankingSemantics) {
            case MATT_TONI, COUNTING, NSA, CATEGORIZER, TRUST, MAX, MAX_NSA, EULER, ITS, ALPHABBS_0 ->
                    vergleichsoperator == Vergleichsoperator.STRICT ? value.compareTo(thresh) > 0 : value.compareTo(thresh) >= 0;
            case ALPHABBS_1, ALPHABBS_2 ->
                    vergleichsoperator == Vergleichsoperator.STRICT ? value.compareTo(thresh) == -1
                            : value.compareTo(thresh) <= 0;


        };


    }

    private boolean useThresholdArg_Helper(BigDecimal value, BigDecimal thresh) {


        return switch (this.rankingSemanticsHelp) {
            case MATT_TONI, COUNTING, NSA, CATEGORIZER, TRUST, MAX, MAX_NSA, EULER, ITS, ALPHABBS_0 ->
                    vergleichsoperator == Vergleichsoperator.STRICT ? value.compareTo(thresh) > 0 : value.compareTo(thresh) >= 0;
            case ALPHABBS_1, ALPHABBS_2 ->
                    vergleichsoperator == Vergleichsoperator.STRICT ? value.compareTo(thresh) == -1
                            : value.compareTo(thresh) <= 0;


        };


    }


    private boolean useThresholdAtt(BigDecimal att, BigDecimal thresh) {


        return switch (this.rankingSemantics) {
            case NSA, CATEGORIZER, TRUST, MAX, MAX_NSA, MATT_TONI, COUNTING, EULER, ITS ->
                    vergleichsoperator == Vergleichsoperator.STRICT ? att.compareTo(thresh) < 0 : att.compareTo(thresh) <= 0;
            case ALPHABBS_1, ALPHABBS_2, ALPHABBS_0 ->
                    vergleichsoperator == Vergleichsoperator.STRICT ? att.compareTo(thresh) >= 0
                            : att.compareTo(thresh) > 0;

        };


    }


}

