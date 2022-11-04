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
import org.tweetyproject.math.probability.Probability;

import java.util.*;
import java.util.stream.Collectors;

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
        EULER_MB, TRUST, BURDEN, DISCUSSION, TUPLES, ALPHABBS, ITS

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
        ranking = getRanking(bbase);

        return getExtensionsForSemantics_withSCCs(ranking, bbase, this.extensionSemantics);


    }

    private Map<Argument, Double> getRanking(DungTheory bbase) {
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
            case BURDEN -> new AlphaBurdenBasedRankingReasoner().getModel(bbase);
            case ITS -> new IterativeSchemaRankingReasoner().getModel(bbase);
            default -> null;
        });
        return ranking;
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
                    cfSubsets.add(new Extension<DungTheory>(subsubset));
                }
            }
        }
        return cfSubsets;

    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Conflictfree(Map<Argument, Double> ranking,
                                                                                     DungTheory bbase, Semantics extensionsemantic) {
        Collection<Extension<DungTheory>> finalAllExtensions = new ArrayList<>();
        DungTheory restrictedTheory = new DungTheory((DungTheory) bbase);
        // remove all self-attacking arguments
        /*
        for (Argument argument: (DungTheory)bbase) {
            if (restrictedTheory.isAttackedBy(argument, argument)) {
                var min = ranking.values().stream().min(Double::compare);
                ranking.replace(argument, min.get()-min.get()/2.);

            }
        }

         */
        Collection<Extension<DungTheory>> allMaximalConflictFreeSets = this.getMaximalConflictFreeSets(bbase, restrictedTheory);
        for (Extension<DungTheory> e : allMaximalConflictFreeSets) {
            var extneu = getSetForSemantics(ranking, e.stream().collect(Collectors.toList()), bbase, extensionsemantic);
            if (!finalAllExtensions.contains(extneu))
                finalAllExtensions.add(extneu);
        }
        /*
        System.out.println(bbase);
        System.out.println(ranking);

        System.out.println(finalAllExtensions);

         */
        return finalAllExtensions;
    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics(Map<Argument, Double> ranking,
                                                                        DungTheory bbaseOrig, DungTheory red, Semantics extensionsemantic) {
        Collection<Extension<DungTheory>> finalAllExtensions = new ArrayList<>();

        var extneu = getSetForSemantics(ranking, red, bbaseOrig, extensionsemantic);

        if (!finalAllExtensions.contains(extneu))
            finalAllExtensions.add(extneu);
        /*System.out.println(bbaseOrig+""+red);
        System.out.println(ranking);

        System.out.println(finalAllExtensions);*/
        return finalAllExtensions;
    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_withSCCs(Map<Argument, Double> ranking,
                                                                                 DungTheory bbase, Semantics extensionsemantic) {


        return this.computeExtensionsViaSccs(ranking, bbase, getSccOrdered(bbase), 0, new HashSet<>());


    }

    private Set<Extension<DungTheory>> computeExtensionsViaSccs(Map<Argument, Double> ranking, DungTheory theory, List<Collection<Argument>> sccs,
                                                                int idx, Collection<Argument> in) {
        if (idx >= sccs.size()) {
            Set<Extension<DungTheory>> result = new HashSet<>();
            result.add(new Extension<>(in));
            return result;
        }
        var scc = sccs.get(idx);

        DungTheory subTheory = (DungTheory) theory.getRestriction(scc);


        // compute extensions of sub theory
        Collection<Extension<DungTheory>> subExt = this.getExtensionsForSemantics(ranking, theory, subTheory, extensionSemantics);
        Set<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
        for (Extension<DungTheory> ext : subExt) {
            var in_neu = new ArrayList<> (in);
            in_neu.addAll(ext);

            result.addAll(this.computeExtensionsViaSccs(ranking, theory, sccs, idx + 1, in_neu));

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


        return 0.5;


    }


}

