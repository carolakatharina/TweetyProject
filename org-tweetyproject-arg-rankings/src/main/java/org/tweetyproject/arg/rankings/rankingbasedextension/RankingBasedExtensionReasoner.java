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
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.*;
import org.tweetyproject.math.probability.Probability;

import java.util.*;
import java.util.stream.Collectors;

public class RankingBasedExtensionReasoner extends AbstractExtensionReasoner {
    RankingSemantics rankingSemantics;
    Semantics extensionSemantics;
    Vorgehensweise vorgehensweise;
    boolean withoutSelfattacking = false;

    public enum Vorgehensweise {
        SCC,
        CONFLICTFREE,
        SIMPLE

    }


    public enum RankingSemantics {
        CATEGORIZER,
        STRATEGY,
        SAF,

        COUNTING,
        PROBABILISTIC,
        MAX,
        EULER_MB, TRUST, BURDEN, DISCUSSION, TUPLES, ALPHABBS, ITS

    }

    public RankingBasedExtensionReasoner(Semantics extensionSemantics,
                                         RankingSemantics semantics, Vorgehensweise vorgehensweise) {

        System.out.println(semantics);
        this.rankingSemantics = semantics;
        this.extensionSemantics = extensionSemantics;
        this.vorgehensweise=vorgehensweise;
    }


    public RankingBasedExtensionReasoner(Semantics extensionSemantics,
                                         RankingSemantics semantics, Vorgehensweise vorgehensweise, boolean withoutSelfattacking) {

        System.out.println(semantics);
        this.rankingSemantics = semantics;
        this.extensionSemantics = extensionSemantics;
        this.vorgehensweise=vorgehensweise;
        this.withoutSelfattacking=withoutSelfattacking;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {

        return null;
    }


    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Map<Argument, Double> ranking;
        ranking = getRanking(bbase);

        return switch(this.vorgehensweise) {
            case SCC -> getExtensionsForSemantics_withSCCs(ranking, bbase);
            case CONFLICTFREE -> getExtensionsForSemantics_Conflictfree(ranking, bbase);
            case SIMPLE -> getExtensionsForSemantics_Simple(ranking, bbase, this.extensionSemantics);
        };

    }

    private Map<Argument, Double> getRanking(DungTheory bbase) {

        if (!this.withoutSelfattacking) {
            return new HashMap<>(switch (this.rankingSemantics) {
                case CATEGORIZER -> new CategorizerRankingReasoner().getModel(bbase);
                case STRATEGY -> new StrategyBasedRankingReasoner().getModel(bbase);
                case SAF -> new SAFRankingReasoner().getModel(bbase);
                case COUNTING -> new CountingRankingReasoner().getModel(bbase);
                case MAX -> new MaxBasedRankingReasoner().getModel(bbase);
                case TRUST -> new TrustBasedRankingReasoner().getModel(bbase);
                case EULER_MB -> new EulerMaxBasedRankingReasoner().getModel(bbase);
                case ALPHABBS -> new AlphaBurdenBasedRankingReasoner().getModel(bbase);
                case ITS -> new IterativeSchemaRankingReasoner().getModel(bbase);
                case PROBABILISTIC ->
                        new ProbabilisticRankingReasoner(Semantics.GROUNDED_SEMANTICS, new Probability(0.5), true).getModel(bbase);
                default -> null;
            });
        }


        return new HashMap<>(switch (this.rankingSemantics) {
            case CATEGORIZER -> new CategorizerRankingReasoner_Without_SelfAttacking().getModel(bbase);
            //case COUNTING -> new CountingRankingReasoner().getModel(bbase);
            case MAX -> new MaxBasedRankingReasoner_Without_SelfAttacking().getModel(bbase);
            case TRUST -> new TrustBasedRankingReasoner_Without_Selfattacking().getModel(bbase);
            case EULER_MB -> new EulerMaxBasedRankingReasoner_Without_SelfAttacking().getModel(bbase);
            case ALPHABBS -> new AlphaBurdenBasedRankingReasoner_Without_Selfattacking().getModel(bbase);
            case ITS -> new IterativeSchemaRankingReasoner_Without_Selfattacking().getModel(bbase);
            default -> null;
        });
    }

    /**
     * computes all maximal conflict-free sets of bbase
     * @param bbase an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Collection<Extension<DungTheory>> getMaximalConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        Collection<Extension<DungTheory>> cfSubsets = new HashSet<Extension<DungTheory>>();
        if (candidates.size() == 0 || bbase.size() == 0) {
            cfSubsets.add(new Extension<DungTheory>());
        } else {
            for (Argument element: candidates) {

                DungTheory remainingTheory = new DungTheory(bbase);
                remainingTheory.remove(element);
                remainingTheory.removeAll(bbase.getAttacked(element));

                Set<Argument> remainingCandidates = new HashSet<Argument>(candidates);
                remainingCandidates.remove(element);
                remainingCandidates.removeAll(bbase.getAttacked(element));
                remainingCandidates.removeAll(bbase.getAttackers(element));

                Collection<Extension<DungTheory>> subsubsets = this.getMaximalConflictFreeSets(remainingTheory, remainingCandidates);

                for (Extension<DungTheory> subsubset : subsubsets) {
                    //cfSubsets.add(new Extension(subsubset));
                    subsubset.add(element);
                    cfSubsets.add(new Extension<DungTheory>(subsubset));
                }
            }
        }
        return cfSubsets;
    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Simple(Map<Argument, Double> ranking,
                                                                                     DungTheory bbase, Semantics extensionsemantic) {
        Collection<Extension<DungTheory>> finalAllExtensions = new ArrayList<>();

        var maxExtension = getSetForSemantics(ranking, bbase, bbase, extensionsemantic);

        /*
        System.out.println(bbase);
        System.out.println(ranking);

        System.out.println(finalAllExtensions);

         */
        finalAllExtensions.add(maxExtension);
        return finalAllExtensions;
    }



    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Conflictfree(Map<Argument, Double> ranking,
                                                                                     DungTheory bbase) {


        var restrictedtheory = new DungTheory(bbase);


        // remove all self-attacking arguments
        for (Argument argument : bbase) {
            if (bbase.isAttackedBy(argument, argument)) {
               restrictedtheory.remove(argument);
            }
        }

        //alle Kandidaten erhalten, die Rankingkriterien erfüllen:
        var candidates = getSetForSemantics(ranking, restrictedtheory, bbase, extensionSemantics);


        return this.getMaximalConflictFreeSets(bbase, ranking.entrySet().stream()
                .filter(entry -> candidates.contains(entry.getKey())).map(entry -> entry.getKey()).collect(Collectors.toList()));

    }





    private Collection<Extension<DungTheory>> getExtensionsForSemantics_withSCCs(Map<Argument, Double> ranking,
                                                                                 DungTheory bbase) {


        return this.computeExtensionsViaSccs(ranking, bbase, getSccOrdered(bbase), 0, new HashSet<>(), new HashSet<>(), new HashSet<>());


    }

    private Set<Extension<DungTheory>> computeExtensionsViaSccs(Map<Argument, Double> ranking, DungTheory theory, List<Collection<Argument>> sccs,
                                                                int idx, Collection<Argument> in, Collection<Argument> out, Collection<Argument> undec) {
        if (idx >= sccs.size()) {
            Set<Extension<DungTheory>> result = new HashSet<>();
            result.add(new Extension<>(in));
            return result;
        }
        var scc = sccs.get(idx);

        DungTheory subTheory = (DungTheory) theory.getRestriction(scc);

        // remove all out arguments
        subTheory.removeAll(out);
        // for all arguments that are attacked by an already undecided argument outside the scc, add attack
        // from an auxiliary self-attacking argument
        Argument aux = new Argument("_aux_argument8937");
        subTheory.add(aux);
        subTheory.add(new Attack(aux,aux));

        for(Argument a: subTheory)
            if(theory.isAttacked(a, new Extension<DungTheory>(undec)))
                subTheory.add(new Attack(aux,a));

        // compute extensions of sub theory
        Collection<Extension<DungTheory>> subExt = List.of(this.getSetForSemantics(getRanking(subTheory), subTheory, subTheory, extensionSemantics));
        Set<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
        Collection<Argument> in_neu, out_neu, undec_neu, attacked;
        for (Extension<DungTheory> ext : subExt) {
            in_neu = new ArrayList<> (in);
            undec_neu = new ArrayList<> (undec);
            out_neu = new ArrayList<> (out);
            attacked= new HashSet<Argument>();

            in_neu.addAll(ext);
            for(Argument a: ext)
                attacked.addAll(theory.getAttacked(a));
            out_neu.addAll(attacked);
            for(Argument a: subTheory)
                if(a != aux && !ext.contains(a) && !attacked.contains(a))
                    undec_neu.add(a);

            result.addAll(this.computeExtensionsViaSccs(ranking, theory, sccs, idx + 1, in_neu, out_neu, undec_neu));

        }
        return result;
    }

    public List<Collection<Argument>> getSccOrdered(DungTheory bbase) {
        List<Collection<Argument>> sccs = new ArrayList<Collection<Argument>>(((DungTheory) bbase).getStronglyConnectedComponents());
        // order SCCs in a DAG
        boolean[][] dag = new boolean[sccs.size()][sccs.size()];
        for (int i = 0; i < sccs.size(); i++) {
            dag[i] = new boolean[sccs.size()];
            Arrays.fill(dag[i], false);
        }
        for (int i = 0; i < sccs.size(); i++)
            for (int j = 0; j < sccs.size(); j++)
                if (i != j)
                    if (((DungTheory) bbase).isAttacked(new Extension<DungTheory>(sccs.get(i)), new Extension<DungTheory>(sccs.get(j))))
                        dag[i][j] = true;
        // order SCCs topologically
        List<Collection<Argument>> sccs_ordered = new ArrayList<Collection<Argument>>();
        while (sccs_ordered.size() < sccs.size()) {
            for (int i = 0; i < sccs.size(); i++) {
                if (sccs_ordered.contains(sccs.get(i)))
                    continue;
                boolean isNull = true;
                for (int j = 0; j < sccs.size(); j++)
                    if (dag[i][j]) {
                        isNull = false;
                        break;
                    }
                if (isNull) {
                    sccs_ordered.add(sccs.get(i));
                    for (int j = 0; j < sccs.size(); j++)
                        dag[j][i] = false;
                }
            }
        }
        return sccs_ordered;
    }


    private Extension<DungTheory> getSetForSemantics(Map<Argument, Double> ranking, Collection<Argument> e,
                                                     DungTheory bbase, Semantics extensionSemantics) {
        // weitere Idee: einfach erst einmal Argumente rausfiltern
        // und dann erst schauen, welche davon in welcher Kombi konfliktfrei

        switch (extensionSemantics) {

            // threshold argument strength
            case RB_ARG_ABS_STRENGTH: {
                return new Extension<>(e.stream().filter(arg ->
                        ranking.get(arg) >= getThresholdSingle()).collect(Collectors.toList()));


            }
            // only acceptable if strength higher than any of its attackers
            case RB_ARG_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return attackers.stream().allMatch(att -> ranking.get(att) < ranking.get(arg));
                }).collect(Collectors.toList()));

            }

            // threshold attacker strength
            case RB_ATT_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle());
                }).collect(Collectors.toList()));
            }

            case RB_ATT_STRENGTH_ARG_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return ranking.get(arg) >= getThresholdSingle() &&
                            attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle());
                }).collect(Collectors.toList()));

            }

            case RB_ATT_STRENGTH_ARG_STRENGTH_ABS_AND_REL_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return ranking.get(arg) >= getThresholdSingle() &&
                            attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle()
                                    && ranking.get(att) < ranking.get(arg)
                            );
                }).collect(Collectors.toList()));
            }

            case RB_ARG_STRENGTH_ABS_AND_REL_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return ranking.get(arg) >= getThresholdSingle() &&
                            attackers.stream().allMatch(att ->
                                    ranking.get(att) < ranking.get(arg)
                            );
                }).collect(Collectors.toList()));
            }
            case RB_ATT_STRENGTH_ABS_AND_REL_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return
                            attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle()
                                    && ranking.get(att) < ranking.get(arg)
                            );
                }).collect(Collectors.toList()));
            }

            case RB_ATT_STRENGTH_ABS_OR_REL_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return
                            attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle()
                                    || ranking.get(att) < ranking.get(arg)
                            );
                }).collect(Collectors.toList()));
            }

            case RB_ATT_STRENGTH_ARG_STRENGTH_ABS_or_REL_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return ranking.get(arg) >= getThresholdSingle() ||
                            attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle()
                                    || ranking.get(att) < ranking.get(arg)
                            );
                }).collect(Collectors.toList()));
            }

            case RB_ARG_STRENGTH_ABS_OR_REL_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return ranking.get(arg) >= getThresholdSingle() ||
                            attackers.stream().allMatch(att -> ranking.get(att) < ranking.get(arg)
                            );
                }).collect(Collectors.toList()));
            }

            case RB_ATT_STRENGTH_OR_ARG_STRENGTH: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return ranking.get(arg) >= getThresholdSingle() ||
                            attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle()

                            );
                }).collect(Collectors.toList()));
            }


            case RB_ATT_ABS_AND_REL_STRENGTH_OR_ARG_STRENGTH_ABS: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return ranking.get(arg) >= getThresholdSingle() ||
                            attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle()
                                    && ranking.get(att) < ranking.get(arg)
                            );
                }).collect(Collectors.toList()));
            }


            case RB_ATT_ABS_OR_REL_STRENGTH_AND_ARG_STRENGTH_ABS: {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return ranking.get(arg) >= getThresholdSingle() &&
                            attackers.stream().allMatch(att -> ranking.get(att) < getThresholdSingle()
                                    || ranking.get(att) < ranking.get(arg)
                            );
                }).collect(Collectors.toList()));
            }

            default: {
                System.out.println("Default");
                return new Extension<>();
            }
        }

    }


    private double getThresholdSingle() {


        return switch(this.rankingSemantics) {
            case SAF -> 0.25;
            case MAX -> 0.5;
            case EULER_MB -> 0.5;
            case STRATEGY -> 0.5;

            default -> 0.5;
        };


    }


}

