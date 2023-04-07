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

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.*;

import java.util.*;
import java.util.stream.Collectors;


public class RankingBasedExtensionReasoner extends AbstractExtensionReasoner {
    RankingSemantics rankingSemantics;
    Semantics extensionSemantics;
    Vorgehensweise vorgehensweise;
    double thresh;


    public enum Vorgehensweise {
        STRONGEST_CF, MAX_CF, CF, INC_BUDGET, SIMPLE

    }


    public enum RankingSemantics {
        CATEGORIZER,

        COUNTING,

        MAX,
        TRUST, NSA, ALPHABBS_1, ALPHABBS_2, MATT_TONI

    }

    public RankingBasedExtensionReasoner(Semantics extensionSemantics,
                                         RankingSemantics semantics, Vorgehensweise vorgehensweise, double thresh) {

        System.out.println(semantics);
        this.rankingSemantics = semantics;
        this.extensionSemantics = extensionSemantics;
        this.vorgehensweise = vorgehensweise;
        this.thresh=thresh;
    }


    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {

        return null;
    }


    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Map<Argument, Double> ranking;
        ranking = getRanking(bbase);

        //TODO: Carola implement missing branches
        return switch (this.vorgehensweise) {
            case STRONGEST_CF -> null;
            case MAX_CF -> getExtensionsForSemantics_MaxConflictfree(ranking, bbase);
            case CF -> getExtensionsForSemantics_Conflictfree(ranking, bbase);
            case INC_BUDGET -> null;
            case SIMPLE -> getExtensionsForSemantics_Simple(ranking, bbase);
        };

    }

    private Map<Argument, Double> getRanking(DungTheory bbase) {


        return new HashMap<>(switch (this.rankingSemantics) {
            case CATEGORIZER -> new CategorizerRankingReasoner().getModel(bbase);
            case COUNTING -> new CountingRankingReasoner().getModel(bbase);
            case MAX -> new MaxBasedRankingReasoner().getModel(bbase);
            case TRUST -> new TrustBasedRankingReasoner().getModel(bbase);
            case NSA -> new CategorizerRankingReasoner_Without_SelfAttacking().getModel(bbase);
            case ALPHABBS_1 -> new AlphaBurdenBasedRankingReasoner(0.3).getModel(bbase);
            case ALPHABBS_2 -> new AlphaBurdenBasedRankingReasoner(10).getModel(bbase);
            case MATT_TONI -> new StrategyBasedRankingReasoner().getModel(bbase);
        });

    }

    /**
     * computes all maximal conflict-free sets of bbase
     *
     * @param bbase      an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Collection<Extension<DungTheory>> getMaximalConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        Collection<Extension<DungTheory>> cfSubsets = new HashSet<>();
        if (candidates.size() == 0 || bbase.size() == 0) {
            cfSubsets.add(new Extension<>());
        } else {
            for (Argument element : candidates) {

                DungTheory remainingTheory = new DungTheory(bbase);
                remainingTheory.remove(element);
                remainingTheory.removeAll(bbase.getAttacked(element));

                Set<Argument> remainingCandidates = new HashSet<>(candidates);
                remainingCandidates.remove(element);
                remainingCandidates.removeAll(bbase.getAttacked(element));
                remainingCandidates.removeAll(bbase.getAttackers(element));

                Collection<Extension<DungTheory>> subsubsets = this.getMaximalConflictFreeSets(remainingTheory, remainingCandidates);

                for (Extension<DungTheory> subsubset : subsubsets) {
                    //cfSubsets.add(new Extension(subsubset));
                    subsubset.add(element);
                    cfSubsets.add(new Extension<>(subsubset));
                }
            }
        }
        return cfSubsets;
    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Simple(Map<Argument, Double> ranking,
                                                                               DungTheory bbase) {
        Collection<Extension<DungTheory>> finalAllExtensions = new ArrayList<>();



        var maxExtension = getSetForSemantics(ranking, bbase, bbase, this.extensionSemantics);

        /*
        System.out.println(bbase);
        System.out.println(ranking);

        System.out.println(finalAllExtensions);

         */
        finalAllExtensions.add(maxExtension);
        return finalAllExtensions;
    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_MaxConflictfree(Map<Argument, Double> ranking,
                                                                                     DungTheory bbase) {


        var restrictedtheory = new DungTheory(bbase);



        //alle Kandidaten erhalten, die Rankingkriterien erfüllen:
        var candidates = getSetForSemantics(ranking, restrictedtheory, bbase, extensionSemantics);


        return this.getMaximalConflictFreeSets(bbase, ranking.keySet().stream()
                .filter(candidates::contains).collect(Collectors.toList()));

    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Conflictfree(Map<Argument, Double> ranking,
                                                                                        DungTheory bbase) {


        var restrictedtheory = new DungTheory(bbase);



        //alle Kandidaten erhalten, die Rankingkriterien erfüllen:
        var candidates = getSetForSemantics(ranking, restrictedtheory, bbase, extensionSemantics);


        return this.getConflictFreeSets(bbase, ranking.keySet().stream()
                .filter(candidates::contains).collect(Collectors.toList()));

    }


    /**
     * computes all conflict-free sets of bbase, that contain only arguments in candidates
     * @param bbase an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Collection<Extension<DungTheory>> getConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        Collection<Extension<DungTheory>> subsets = new HashSet<>();
        if (candidates.size() == 0 || bbase.size() == 0) {
            subsets.add(new Extension<>());
        } else {
            for (Argument element: candidates) {
                DungTheory remainingTheory = new DungTheory(bbase);
                remainingTheory.remove(element);
                remainingTheory.removeAll(bbase.getAttacked(element));

                Set<Argument> remainingCandidates = new HashSet<>(candidates);
                remainingCandidates.remove(element);
                remainingCandidates.removeAll(bbase.getAttacked(element));
                remainingCandidates.removeAll(bbase.getAttackers(element));

                Collection<Extension<DungTheory>> subsubsets = this.getConflictFreeSets(remainingTheory, remainingCandidates);

                for (Extension<DungTheory> subsubset: subsubsets) {
                    subsets.add(new Extension<>(subsubset));
                    subsubset.add(element);
                    subsets.add(new Extension<>(subsubset));
                }
            }
        }

        return subsets;
    }







    private Extension<DungTheory> getSetForSemantics(Map<Argument, Double> ranking, Collection<Argument> e,
                                                     DungTheory bbase, Semantics extensionSemantics) {


            switch (extensionSemantics) {

                // threshold argument strength
                case RB_ARG_ABS_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg ->
                            useThresholdArg(ranking.get(arg),thresh)).collect(Collectors.toList()));


                }
                // only acceptable if strength higher than any of its attackers
                case RB_ARG_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),ranking.get(arg)));
                    }).collect(Collectors.toList()));

                }

                // threshold attacker strength
                case RB_ATT_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh));
                    }).collect(Collectors.toList()));
                }

                case RB_ATT_STRENGTH_ARG_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return useThresholdArg(ranking.get(arg),thresh) &&
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh));
                    }).collect(Collectors.toList()));

                }

                case RB_ATT_STRENGTH_ARG_STRENGTH_ABS_AND_REL_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return useThresholdArg(ranking.get(arg),thresh) &&
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh)
                                        && useThresholdAtt(ranking.get(att),ranking.get(arg))
                                );
                    }).collect(Collectors.toList()));
                }

                case RB_ARG_STRENGTH_ABS_AND_REL_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return useThresholdArg(ranking.get(arg),thresh) &&
                                attackers.stream().allMatch(att ->
                                        useThresholdAtt(ranking.get(att),ranking.get(arg))
                                );
                    }).collect(Collectors.toList()));
                }
                case RB_ATT_STRENGTH_ABS_AND_REL_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh)
                                        && useThresholdAtt(ranking.get(att),ranking.get(arg))
                                );
                    }).collect(Collectors.toList()));
                }

                case RB_ATT_STRENGTH_ABS_OR_REL_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh)
                                        || useThresholdAtt(ranking.get(att),ranking.get(arg))
                                );
                    }).collect(Collectors.toList()));
                }

                case RB_ATT_STRENGTH_ARG_STRENGTH_ABS_or_REL_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return useThresholdArg(ranking.get(arg),thresh) ||
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh)
                                        || useThresholdAtt(ranking.get(att),ranking.get(arg))
                                );
                    }).collect(Collectors.toList()));
                }

                case RB_ARG_STRENGTH_ABS_OR_REL_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return useThresholdArg(ranking.get(arg),thresh) ||
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),ranking.get(arg))
                                );
                    }).collect(Collectors.toList()));
                }

                case RB_ATT_STRENGTH_OR_ARG_STRENGTH: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return useThresholdArg(ranking.get(arg),thresh) ||
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh)

                                );
                    }).collect(Collectors.toList()));
                }


                case RB_ATT_ABS_AND_REL_STRENGTH_OR_ARG_STRENGTH_ABS: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return useThresholdArg(ranking.get(arg),thresh) ||
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh)
                                        && useThresholdAtt(ranking.get(att),ranking.get(arg))
                                );
                    }).collect(Collectors.toList()));
                }


                case RB_ATT_ABS_OR_REL_STRENGTH_AND_ARG_STRENGTH_ABS: {
                    return new Extension<>(e.stream().filter(arg -> {
                        var attackers = bbase.getAttackers(arg);
                        return useThresholdArg(ranking.get(arg),thresh) &&
                                attackers.stream().allMatch(att -> useThresholdAtt(ranking.get(att),thresh)
                                        || useThresholdAtt(ranking.get(att),ranking.get(arg))
                                );
                    }).collect(Collectors.toList()));
                }

                default: {
                    System.out.println("Default");
                    return new Extension<>();
                }

        }

    }




    private boolean useThresholdArg(double value, double thresh) {


        return switch(this.rankingSemantics) {
            case MAX, NSA, CATEGORIZER, MATT_TONI, COUNTING, TRUST-> value >= thresh;
            case ALPHABBS_1, ALPHABBS_2-> value < thresh;


        };


    }



    private boolean useThresholdAtt(double value, double thresh) {


        return switch(this.rankingSemantics) {
            case MAX, NSA, CATEGORIZER, MATT_TONI, COUNTING, TRUST-> value < thresh;
            case ALPHABBS_1, ALPHABBS_2-> value > thresh;


        };


    }


}

