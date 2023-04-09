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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.rankingbasedextension;

import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.commons.postulates.PostulateEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.tweetyproject.arg.rankings.rankingbasedextension.RankingBasedExtensionReasoner.Akzeptanzbedingung.RB_ARG_ABS_STRENGTH;
import static org.tweetyproject.arg.rankings.rankingbasedextension.RankingBasedExtensionReasoner.Akzeptanzbedingung.RB_ATT_STRENGTH;
import static org.tweetyproject.arg.rankings.rankingbasedextension.RankingBasedExtensionReasoner.RankingSemantics.*;
import static org.tweetyproject.arg.rankings.rankingbasedextension.RankingBasedExtensionReasoner.Vergleichsoperator.NOT_STRICT;
import static org.tweetyproject.arg.rankings.rankingbasedextension.RankingBasedExtensionReasoner.Vergleichsoperator.STRICT;

/**
 * Example code for evaluating weighted ranking semantics in regard
 * to postulates. Each postulate represents a single property
 * that characterizes how the semantics ranks arguments.
 *
 * @author Carola Bauer
 */
public class ExtensionPrinciplesExample {
    private static Collection<Principle> all_principles;


    private static final Collection<RankingBasedExtensionReasoner.Vorgehensweise> vorgehen = new ArrayList<>(
            List.of(RankingBasedExtensionReasoner.Vorgehensweise.SIMPLE
                    /*
                    RankingBasedExtensionReasoner.Vorgehensweise.STRONGEST_CF,
                    RankingBasedExtensionReasoner.Vorgehensweise.INC_BUDGET,
                     */
                    //RankingBasedExtensionReasoner.Vorgehensweise.MAX_CF,
                    //RankingBasedExtensionReasoner.Vorgehensweise.CF
            ));

    private static final Collection<RankingBasedExtensionReasoner.Akzeptanzbedingung> akzeptanzbedingungen = new ArrayList<RankingBasedExtensionReasoner.Akzeptanzbedingung>(List.of(
            RB_ARG_ABS_STRENGTH,
            //RB_ARG_STRENGTH,
            //RB_ARG_STRENGTH_ABS_AND_REL_STRENGTH,
            //RB_ARG_STRENGTH_ABS_OR_REL_STRENGTH,
            //RB_ATT_ABS_AND_REL_STRENGTH_OR_ARG_STRENGTH_ABS,
            //RB_ATT_ABS_OR_REL_STRENGTH_AND_ARG_STRENGTH_ABS,
            //RB_ATT_STRENGTH_ARG_STRENGTH_ABS_AND_REL_STRENGTH,
            //RB_ATT_STRENGTH_ARG_STRENGTH_ABS_or_REL_STRENGTH,
            RB_ATT_STRENGTH
            //RB_ATT_STRENGTH_ARG_STRENGTH,
            //RB_ATT_STRENGTH_ABS_OR_REL_STRENGTH,
            //RB_ATT_STRENGTH_ABS_AND_REL_STRENGTH

    ));
    private static final Collection<RankingBasedExtensionReasoner.RankingSemantics> rank_semantics = new ArrayList<>(List.of(
            MAX,
            CATEGORIZER,
            NSA,
            TRUST,
            COUNTING,
            ALPHABBS_0,
            MATT_TONI,
            EULER,
            ITS
            /*,ALPHABBS_1,
            ALPHABBS_2*/

    ));

    public static void main(String[] args) {
        all_principles = new HashSet<>();
        all_principles.add(Principle.ADMISSIBILITY);
        all_principles.add(Principle.STRONG_ADMISSIBILITY);
        all_principles.add(Principle.REDUCT_ADM);
        all_principles.add(Principle.SEMIQUAL_ADM);
        all_principles.add(Principle.WEAK_ADMISSIBILITY);
        all_principles.add(Principle.CONFLICT_FREE);
        all_principles.add(Principle.DEFENCE);
        all_principles.add(Principle.NAIVETY);
        all_principles.add(Principle.I_MAXIMALITY);
        all_principles.add(Principle.REINSTATEMENT);
        all_principles.add(Principle.WEAK_REINSTATEMENT);
        all_principles.add(Principle.CF_REINSTATEMENT);
        all_principles.add(Principle.DIRECTIONALITY);
        all_principles.add(Principle.DIRECTIONALITY);
        all_principles.add(Principle.INRA);
        all_principles.add(Principle.MODULARIZATION);
        all_principles.add(Principle.SCC_RECURSIVENESS);
        all_principles.add(Principle.SCC_DECOMPOSABILITY);

        for (var rank : rank_semantics) {
            for (var bed : akzeptanzbedingungen) {
                Example(bed, rank);
            }
        }

    }

    public static void Example(RankingBasedExtensionReasoner.Akzeptanzbedingung akzeptanzbedingung, RankingBasedExtensionReasoner.RankingSemantics rankingSemantics) {



        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        for (var vorg : vorgehen) {
            for (double thresh : getThresholds(rankingSemantics)) {

                PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                        new RankingBasedExtensionReasoner(akzeptanzbedingung,
                                rankingSemantics, vorg, thresh, STRICT));
                evaluator.addAllPostulates(all_principles);

                System.out.println(evaluator.evaluate(10000, true).prettyPrint());


                evaluator = new PostulateEvaluator<>(dg,
                        new RankingBasedExtensionReasoner(akzeptanzbedingung,
                                rankingSemantics, vorg, thresh, NOT_STRICT));
                evaluator.addAllPostulates(all_principles);

                System.out.println(evaluator.evaluate(10000, true).prettyPrint());

            }
        }


    }


    private static double[] getThresholds(RankingBasedExtensionReasoner.RankingSemantics rankingSemantics) {


        return switch (rankingSemantics) {
            case MAX, NSA, CATEGORIZER, EULER, ITS -> new double[]{0.5, (1. / ((1. + Math.sqrt(5.)) / 2.))};
            case ALPHABBS_1 -> new double[]{10.};
            case ALPHABBS_2 -> new double[]{2.};
            case ALPHABBS_0 -> new double[]{1.};
            case MATT_TONI, COUNTING, TRUST -> new double[]{0.5};


        };


    }



        /*

        //Tests für DP/DDP
        File[] apxFiles = new File("C:\\Users\\Carola\\Desktop\\TweetyProject\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\resources")
                .listFiles(new ApxFilenameFilter());


        var dg2 = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);
        System.out.println(rankingSemantics);
        System.out.println(semantics);


        for (var vorg : vorgehen) {
            System.out.println(vorg);
            PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
                    new RankingBasedExtensionReasoner(semantics,
                            rankingSemantics, vorg));
            evaluator2.addAllPostulates(all_principles);
            System.out.println(evaluator2.evaluate(apxFiles.length, true).prettyPrint());
        }



         */


}
