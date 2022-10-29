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
        EULER_MB, TRUST, BURDEN, SERIALIZABLE

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
            case TRUST -> new TrustBasedRankingReasoner().getModel(bbase);
            case EULER_MB -> new EulerMaxBasedRankingReasoner().getModel(bbase);
            case SERIALIZABLE -> new SerialisabilityRankingReasoner(extensionSemantics).getModel(bbase);
            case BURDEN -> new AlphaBurdenBasedRankingReasoner().getModel(bbase);
        });

        return getExtensionsForSemantics(ranking, bbase, this.extensionSemantics);


    }

    /**
     * computes all maximal conflict-free sets of bbase
     *
     * @param bbase      an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Collection<Extension<DungTheory>> getMaximalConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        Collection<Extension<DungTheory>> cfSubsets = new HashSet<Extension<DungTheory>>();
        if (candidates.size() == 0 || bbase.size() == 0) {
            cfSubsets.add(new Extension<DungTheory>());
        } else {
            for (Argument element : candidates) {
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


    private Collection<Extension<DungTheory>> getExtensionsForSemantics(Map<Argument, Double> ranking,
                                                                        DungTheory bbase, Semantics extensionsemantic) {
        Collection<Extension<DungTheory>> finalAllExtensions = new ArrayList<>();
        DungTheory restrictedTheory = new DungTheory((DungTheory)bbase);
        // remove all self-attacking arguments
        for (Argument argument: (DungTheory)bbase) {
            if (restrictedTheory.isAttackedBy(argument, argument)) {
                restrictedTheory.remove(argument);
            }
        }
        Collection<Extension<DungTheory>> allMaximalConflictFreeSets = this.getMaximalConflictFreeSets(bbase, restrictedTheory);
        for (Extension<DungTheory> e : allMaximalConflictFreeSets) {
            finalAllExtensions.add(getMaxConflictfreeForSemantics(ranking, e, bbase, extensionsemantic));
        }

        return finalAllExtensions;
    }

    private Extension<DungTheory> getMaxConflictfreeForSemantics(Map<Argument, Double> ranking, Extension<DungTheory> e,
                                                                 DungTheory bbase, Semantics extensionSemantics) {


        switch (extensionSemantics) {

            // threshold argument strength
            case RB_ARG_ABS_STRENGTH: {
                return new Extension<>(e.stream().filter(arg ->
                        ranking.get(arg) > getThresholdSingle()).collect(Collectors.toList()));


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
            default: {
                System.out.println("Default");
                return new Extension<>();
            }
        }

    }


    private double getThresholdSingle() {
        switch (this.rankingSemantics) {

            case CATEGORIZER -> {

                return 0.5;
            }

            case MAX -> {
                return 0.5;
            }

            case BURDEN -> {
                return 0.5;
            }
            case EULER_MB -> {
                return 0.5;
            }

            case TRUST -> {
                //vermutlich löschen/anpassen funktioniert nicht richtig
                return 0.5;
            }

            case COUNTING -> {
                //vermutlich löschen/anpassen funktioniert nicht richtig
                return 0.5;
            }
            case SERIALIZABLE -> {
                return 0.5;
            }
            default -> {
                return 0.5;
            }
        }
    }


}

