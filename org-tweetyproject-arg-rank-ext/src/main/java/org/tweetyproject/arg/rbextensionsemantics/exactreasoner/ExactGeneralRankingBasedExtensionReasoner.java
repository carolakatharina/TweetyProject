package org.tweetyproject.arg.rbextensionsemantics.exactreasoner;


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
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.ExactNumericalPartialOrder;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class that creates extension semantics based on gradual semantics.
 *
 * @author Carola Bauer
 */
public class ExactGeneralRankingBasedExtensionReasoner extends AbstractExtensionReasoner {


    RankingSemantics rankingSemantics;
    Approach approach;
    BigDecimal threshhold;
    BigDecimal epsilon;

    AcceptanceCondition acceptanceCondition;

    private Map<Argument, BigDecimal> ranking;


    private final AbstractExactNumericalPartialOrderRankingReasoner reasoner;


    /**
     * Determines whether the extensions based on a gradual semantics tau
     * are computed without additional acceptance criteria
     * or if with the additional acceptance criteria based on admissibility (Ar-tau^ad).
     */
    public enum Approach {
        ADMISSIBLE, SIMPLE

    }



    /**
     * Determines the conditions for acceptance.
     */
    public enum AcceptanceCondition {
        // Arguments are accepted based on absolute argument strength
        RB_ARG_ABS_STRENGTH,

        // Arguments are accepted based on relative argument strength
        RB_ARG_REL_STRENGTH,

        // Arguments are accepted based on absolute attack strength
        RB_ATT_ABS_STRENGTH,

    }


    /**
     * Determines the ranking semantics used as a basis for a new extension semantics.
     */
    public enum RankingSemantics {
        CATEGORIZER,

        COUNTING,

        MAX,
        TRUST, NSA, EULER, ITS, MATT_TONI

    }


    /**
     * Creates a new extension semantics based on a gradual semantics
     * @param acceptanceCondition determines whether absolute/relative argument strength or absolute attack strength is used
     * @param semantics the gradual semantics
     * @param approach determines whether additional acceptance criteria are used
     * @param threshhold determines which threshold is used for acceptance
     * @param epsilon parameter that determines the number of iterations for the fixed point technique
     */
    public ExactGeneralRankingBasedExtensionReasoner(AcceptanceCondition acceptanceCondition,
                                                     RankingSemantics semantics,
                                                     Approach approach,
                                                     BigDecimal threshhold,
                                                     BigDecimal epsilon) {

        this.rankingSemantics = semantics;
        this.acceptanceCondition = acceptanceCondition;
        this.approach = approach;
        this.threshhold = threshhold;

        this.epsilon = epsilon;
        this.reasoner = getReasoner(semantics);

    }


    private AbstractExactNumericalPartialOrderRankingReasoner getReasoner(RankingSemantics rankingSemantics) {
        return switch (rankingSemantics) {
            case CATEGORIZER -> new ExactCategorizerRankingReasoner(epsilon);
            case EULER -> new ExactEulerMaxBasedRankingReasoner(epsilon);
            case ITS -> new ExactIterativeSchemaRankingReasoner(epsilon);
            case COUNTING -> new ExactCountingRankingReasoner(BigDecimal.valueOf(0.9), epsilon);
            case MAX -> new ExactMaxBasedRankingReasoner(epsilon);
            case TRUST -> new ExactTrustBasedRankingReasoner(epsilon);
            case NSA -> new ExactNsaReasoner(epsilon);
            case MATT_TONI -> new ExactStrategyBasedRankingReasoner();
        };
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {

        return getModels(bbase).stream().findFirst().orElse(null);
    }


    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        ranking = getRanking(bbase);
        return switch (this.approach) {
            case ADMISSIBLE -> getAdmissibleExt(bbase);
            case SIMPLE -> getExtensionsForSemantics_Simple(ranking, bbase);
        };

    }


    private HashSet<Extension<DungTheory>> getAdmissibleExt(DungTheory bbase) {

        HashSet<Extension<DungTheory>> exts = new HashSet<>();
        var rankingOrdered = this.sortByValue(new HashMap<>(ranking));
        Collections.reverse(rankingOrdered);
        List<Argument> candidates = new ArrayList<>();
        Extension<DungTheory> ext = new Extension<>();


        var rankingValues = rankingOrdered.stream().map(Map.Entry::getValue).distinct().collect(Collectors.toList());


        for (var val : rankingValues) {
            var args = rankingOrdered.stream()
                    .filter(arg -> Objects.equals(arg.getValue(), val)).map(Map.Entry::getKey).collect(Collectors.toList());

            candidates
                    .addAll(args);

            ext = new Extension<>(candidates);
            if (!bbase.isAdmissable(ext)) {
                candidates
                        .removeAll(args);
                ext = new Extension<>(candidates);
                exts.add(ext);
                return exts;
            }


        }

        exts.add(ext);


        return exts;

    }


    /**
     * Get ranking of the gradual semantics used for a given argumentation framework.
     * @param bbase the argumentation framework
     * @return the ranking
     */
    public ExactNumericalPartialOrder<Argument, DungTheory> getRanking(DungTheory bbase) {
        return reasoner.getModel(bbase);
    }


    private List<Map.Entry<Argument, BigDecimal>> sortByValue(Map<Argument, BigDecimal> map) {
        List<Map.Entry<Argument, BigDecimal>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        return list;

    }


    private Collection<Extension<DungTheory>> getExtensionsForSemantics_Simple(Map<Argument, BigDecimal> ranking,
                                                                               DungTheory bbase) {
        Collection<Extension<DungTheory>> finalAllExtensions = new HashSet<>();

        finalAllExtensions.add(getSetForSemantics(ranking, bbase, bbase));


        return finalAllExtensions;
    }


    private Extension<DungTheory> getSetForSemantics(Map<Argument, BigDecimal> ranking, Collection<Argument> e,
                                                     DungTheory bbase) {


        switch (this.acceptanceCondition) {

            // absolute argument strength
            case RB_ARG_ABS_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg ->
                {
                    BigDecimal value = ranking.get(arg);


                    return value.compareTo(threshhold) > 0;


                }).collect(Collectors.toSet()));


            }

            // relative argument strength
            case RB_ARG_REL_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return attackers.stream().allMatch(att -> {
                        BigDecimal value = ranking.get(arg);


                        return value.compareTo(ranking.get(att)) > 0;


                    });
                }).collect(Collectors.toSet()));

            }

            // absolute attack strength
            case RB_ATT_ABS_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg -> {
                    var attackers = bbase.getAttackers(arg);
                    return attackers.stream().allMatch(att -> {
                        BigDecimal att1 = ranking.get(att);

                        return att1.compareTo(threshhold) < 0;


                    });
                }).collect(Collectors.toSet()));
            }


        }

        return null;
    }


}

