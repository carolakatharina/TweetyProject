package org.tweetyproject.arg.rbextensionsemantics.evaluation;
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


import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.util.FileDungTheoryGenerator;
import org.tweetyproject.arg.rbextensionsemantics.evaluation.util.DetailedRankingExtensionbasedEvaluator;
import org.tweetyproject.arg.rbextensionsemantics.exactreasoner.ExactGeneralRankingBasedExtensionReasoner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.tweetyproject.arg.rbextensionsemantics.exactreasoner.ExactGeneralRankingBasedExtensionReasoner.RankingSemantics.*;


/**
 * Evaluation Program for evaluating ranking-based extension semantics with regard
 * to principles.
 *
 * @author Carola Bauer
 */
public class DetailedAnalysisOfNewSemantics {

    private static Collection<Principle> all_principles;

    private static final BigDecimal[] epsilon_values = {
            BigDecimal.valueOf(0.0001)
    };


    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.Vorgehensweise> vorgehen = new ArrayList<>(
            List.of(ExactGeneralRankingBasedExtensionReasoner.Vorgehensweise.SIMPLE,
                    ExactGeneralRankingBasedExtensionReasoner.Vorgehensweise.ADMISSIBLE

            ));

    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung> akzeptanzbedingungen = Arrays.asList(

            ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung.RB_ARG_ABS_STRENGTH,

            ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung.RB_ARG_REL_STRENGTH,
            ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung.RB_ATT_ABS_STRENGTH


    );

    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.RankingSemantics> rank_semantics = new ArrayList<>(List.of(
            CATEGORIZER,
            NSA,
            COUNTING,
            MATT_TONI,
            MAX,
            EULER,
            TRUST

    ));

    public static void main(String[] args) throws IOException {
        all_principles = new HashSet<>();
        all_principles.add(Principle.REINSTATEMENT);
        all_principles.add(Principle.WEAK_REINSTATEMENT);
        all_principles.add(Principle.CF_REINSTATEMENT);
        all_principles.add(Principle.ADMISSIBILITY);
        all_principles.add(Principle.STRONG_ADMISSIBILITY);
        all_principles.add(Principle.REDUCT_ADM);
        all_principles.add(Principle.SEMIQUAL_ADM);
        all_principles.add(Principle.CONFLICT_FREE);
        all_principles.add(Principle.DEFENCE);
        all_principles.add(Principle.NAIVETY);
        all_principles.add(Principle.I_MAXIMALITY);
        all_principles.add(Principle.REINSTATEMENT);
        all_principles.add(Principle.WEAK_REINSTATEMENT);
        all_principles.add(Principle.CF_REINSTATEMENT);
        all_principles.add(Principle.INRA);
        all_principles.add(Principle.MODULARIZATION);
        all_principles.add(Principle.SCC_RECURSIVENESS);
        all_principles.add(Principle.DIRECTIONALITY);


        for (var rank : rank_semantics) {
            for (var bed : akzeptanzbedingungen) {
                evaluate(bed, rank);
            }
        }

    }

    public static void evaluate(ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung akzeptanzbedingung, ExactGeneralRankingBasedExtensionReasoner.RankingSemantics rankingSemantics) throws IOException {
        var threshholds = ThresholdValuesForRBSemantics.getThresholdForSemantics(rankingSemantics, akzeptanzbedingung);


        File[] apxFiles = new File(
                ".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rbextensionsemantics\\resources\\detailed_evaluation")
                .listFiles(new ApxFilenameFilter());
        for (var vorg : vorgehen) {
            for (var epsilon : epsilon_values) {

                var dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);

                ExactGeneralRankingBasedExtensionReasoner reasoner;


                for (BigDecimal thresh : threshholds) {


                    reasoner = new ExactGeneralRankingBasedExtensionReasoner(akzeptanzbedingung,
                            rankingSemantics, vorg, thresh, epsilon);
                    DetailedRankingExtensionbasedEvaluator evaluator = new DetailedRankingExtensionbasedEvaluator(dg,
                            reasoner, all_principles);


                    var ev = evaluator.evaluatePrinciples(Objects.requireNonNull(apxFiles).length, true);


                    System.out.println(ev.prettyPrint());
                    ev.printForSimple(rankingSemantics, akzeptanzbedingung);


                }

            }

        }


    }
}











