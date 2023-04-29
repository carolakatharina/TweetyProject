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
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.comparator.ExactNumericalPartialOrder;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


public class ExactGeneralRankingBasedExtensionReasoner extends AbstractExtensionReasoner {
    RankingSemantics rankingSemantics;

    Vorgehensweise vorgehensweise;
    BigDecimal threshhold;

    BigDecimal epsilon;

    Vergleichsoperator vergleichsoperator;

    Akzeptanzbedingung akzeptanzbedingung;

    private Map<Argument, BigDecimal> ranking;

    private AbstractExactNumericalPartialOrderRankingReasoner reasoner;
    private Collection<Extension<DungTheory>> finalAllExtensions;


    public enum Vorgehensweise {
        STRONGEST_CF, MAX_CF, CF, INC_BUDGET, SIMPLE

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

        MAX,
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
        this.reasoner=getReasoner();

        System.out.println("Ranking Semantic: "+this.rankingSemantics);
        System.out.println("Vorgehen: "+this.vorgehensweise);
        System.out.println("Akzeptanzbedingung: "+this.akzeptanzbedingung);
        System.out.println("Schwellwert: "+this.threshhold);
        System.out.println("Vergleichsoperator: "+this.vergleichsoperator);
        System.out.println("Epsilon: "+this.epsilon);
    }


    public AbstractExactNumericalPartialOrderRankingReasoner getReasoner() {
        return switch (this.rankingSemantics) {
            case CATEGORIZER -> new ExactCategorizerRankingReasoner(epsilon);
            case EULER -> new ExactEulerMaxBasedRankingReasoner(epsilon);
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

        //TODO: Carola implement missing branches
        return switch (this.vorgehensweise) {
            case STRONGEST_CF, INC_BUDGET -> null;
            case MAX_CF -> getExtensionsForSemantics_MaxConflictfree(ranking, bbase);
            case CF -> getExtensionsForSemantics_Conflictfree(ranking, bbase);
            case SIMPLE -> getExtensionsForSemantics_Simple(ranking, bbase);
        };

    }

    private Map<Argument, BigDecimal> getRanking(DungTheory bbase) {

        return new HashMap<>(reasoner.getModel(bbase));


    }

    /**
     * computes all maximal conflict-free sets of bbase
     *
     * @param bbase      an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Collection<Extension<DungTheory>> getMaximalConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        Set<Argument> candidatesWithouSA = new HashSet<>(candidates);
        for (Argument argument : bbase) {
            if (bbase.isAttackedBy(argument, argument)) {
                candidatesWithouSA.remove(argument);
            }
        }

        Collection<Extension<DungTheory>> cfSubsets = new HashSet<>();
        if (candidatesWithouSA.size() == 0 || bbase.size() == 0) {
            cfSubsets.add(new Extension<>());
        } else {
            for (Argument element : candidatesWithouSA) {
                DungTheory remainingTheory = new DungTheory(bbase);
                remainingTheory.remove(element);
                remainingTheory.removeAll(bbase.getAttacked(element));

                Set<Argument> remainingCandidates = new HashSet<>(candidatesWithouSA);
                remainingCandidates.remove(element);
                remainingCandidates.removeAll(bbase.getAttacked(element));
                remainingCandidates.removeAll(bbase.getAttackers(element));

                Collection<Extension<DungTheory>> subsubsets = this.getMaximalConflictFreeSets(remainingTheory, remainingCandidates);

                for (Extension<DungTheory> subsubset : subsubsets) {
                    subsubset.add(element);
                    cfSubsets.add(new Extension<>(subsubset));

                }
            }
        }
        return cfSubsets;
    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Simple(Map<Argument, BigDecimal> ranking,
                                                                               DungTheory bbase) {
        finalAllExtensions = new ArrayList<>();



        finalAllExtensions.add(getSetForSemantics(ranking, bbase, bbase));


        //var grounded = new SimpleGroundedReasoner().getModel(bbase);
        /*
        if (!grounded.equals(maxExtension) && !maxExtension.stream().anyMatch(arg -> arg.getName().startsWith("_aux"))) {
            System.out.println(bbase+"------- "+ranking+"------- "+maxExtension+"-------"+grounded);
        }

         */

        return finalAllExtensions;
    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_MaxConflictfree(Map<Argument, BigDecimal> ranking,
                                                                                        DungTheory bbase) {


        var restrictedtheory = new DungTheory(bbase);


        //alle Kandidaten erhalten, die Rankingkriterien erfüllen:
        var candidates = getSetForSemantics(ranking, restrictedtheory, bbase);


        return this.getMaximalConflictFreeSets(bbase, ranking.keySet().stream()
                .filter(candidates::contains).collect(Collectors.toList()));

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

        Collection<Extension<DungTheory>> subsets = new HashSet<>();
        if (candidatesWithouSA.size() == 0 || bbase.size() == 0) {
            subsets.add(new Extension<>());
        } else {
            for (Argument element : candidatesWithouSA) {
                DungTheory remainingTheory = new DungTheory(bbase);
                remainingTheory.remove(element);
                remainingTheory.removeAll(bbase.getAttacked(element));

                Set<Argument> remainingCandidates = new HashSet<>(candidatesWithouSA);
                remainingCandidates.remove(element);
                remainingCandidates.removeAll(bbase.getAttacked(element));
                remainingCandidates.removeAll(bbase.getAttackers(element));

                Collection<Extension<DungTheory>> subsubsets = this.getConflictFreeSets(remainingTheory, remainingCandidates);

                for (Extension<DungTheory> subsubset : subsubsets) {
                    subsets.add(new Extension<>(subsubset));
                    subsubset.add(element);
                    subsets.add(new Extension<>(subsubset));
                }
            }
        }

        return subsets;
    }


    private Extension<DungTheory> getSetForSemantics(Map<Argument, BigDecimal> ranking, Collection<Argument> e,
                                                     DungTheory bbase) {


        switch (this.akzeptanzbedingung) {

            // absolute argument strength
            case RB_ARG_ABS_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg ->
                        useThresholdArg(ranking.get(arg), threshhold)).collect(Collectors.toList()));


            }

            // relative argument strength
            case RB_ARG_REL_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return attackers.stream().allMatch(att -> useThresholdArg(ranking.get(arg), ranking.get(att)));
                }).collect(Collectors.toList()));

            }

            // absolute attack strength
            case RB_ATT_ABS_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att), threshhold));
                }).collect(Collectors.toList()));
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
            case MATT_TONI, COUNTING, NSA, CATEGORIZER, TRUST, MAX, EULER, ITS, ALPHABBS_0->
                    vergleichsoperator== Vergleichsoperator.STRICT? value.compareTo(thresh)>0:value.compareTo(thresh)>=0;
            case ALPHABBS_1, ALPHABBS_2 -> vergleichsoperator == Vergleichsoperator.STRICT? value.compareTo(thresh)==-1
            : value.compareTo(thresh)<=0;


        };


    }


    private boolean useThresholdAtt(BigDecimal value, BigDecimal thresh) {


        return switch (this.rankingSemantics) {
            case NSA, CATEGORIZER, TRUST, MAX, MATT_TONI, COUNTING, EULER, ITS ->
                    vergleichsoperator == Vergleichsoperator.STRICT ? value.compareTo(thresh) <= 0 : value.compareTo(thresh)<0;
            case ALPHABBS_1, ALPHABBS_2,  ALPHABBS_0 -> vergleichsoperator == Vergleichsoperator.STRICT? value.compareTo(thresh) >= 0
            :value.compareTo(thresh)>0;

        };


    }


}

