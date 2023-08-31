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


public class ExactGeneralRankingBasedExtensionReasoner extends AbstractExtensionReasoner {


    RankingSemantics rankingSemantics;

    Vorgehensweise vorgehensweise;
    BigDecimal threshhold;

    BigDecimal epsilon;



    Akzeptanzbedingung akzeptanzbedingung;

    private Map<Argument, BigDecimal> ranking;



    private final AbstractExactNumericalPartialOrderRankingReasoner reasoner;


    public enum Vorgehensweise {
        ADMISSIBLE, SIMPLE

    }



    public enum Akzeptanzbedingung {
        RB_ARG_ABS_STRENGTH,
        RB_ARG_REL_STRENGTH,

        RB_ATT_ABS_STRENGTH,

    }


    public enum RankingSemantics {
        CATEGORIZER,

        COUNTING,

        MAX,
        TRUST, NSA, EULER, ITS, MATT_TONI

    }

    public ExactGeneralRankingBasedExtensionReasoner(Akzeptanzbedingung akzeptanzbedingung,
                                                     RankingSemantics semantics, Vorgehensweise vorgehensweise, BigDecimal threshhold,
                                                     BigDecimal epsilon) {

        this.rankingSemantics = semantics;
        this.akzeptanzbedingung=akzeptanzbedingung;
        this.vorgehensweise = vorgehensweise;
        this.threshhold = threshhold;

        this.epsilon=epsilon;
        this.reasoner=getReasoner(semantics);

    }





    public AbstractExactNumericalPartialOrderRankingReasoner getReasoner(RankingSemantics rankingSemantics) {
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

        return null;
    }


    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        ranking = getRanking(bbase);


        return switch (this.vorgehensweise) {
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


        switch (this.akzeptanzbedingung) {

            // absolute argument strength
            case RB_ARG_ABS_STRENGTH -> {
                return new Extension<>(e.stream().filter(arg ->
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


        }

        return null;
    }


    private boolean useThresholdArg(BigDecimal value, BigDecimal thresh) {


        return value.compareTo(thresh) > 0;



    }




    private boolean useThresholdAtt(BigDecimal att, BigDecimal thresh) {

    return att.compareTo(thresh) < 0;




    }


}

