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
 * Evaluation Program for evaluating extension semantics based on ranking-based semantics with regard
 * to principles.
 *
 * @author Carola Bauer
 */
public class DetailedAnalysisOfNewSemantics {

    private static Collection<Principle> all_principles;

    private static final BigDecimal[] epsilon_values = {
            BigDecimal.valueOf(0.0001)
    };


    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.Approach> approach = new ArrayList<>(
            List.of(ExactGeneralRankingBasedExtensionReasoner.Approach.SIMPLE,
                    ExactGeneralRankingBasedExtensionReasoner.Approach.ADMISSIBLE

            ));

    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.AcceptanceCondition> AKZEPTANZBEDINGUNGEN = Arrays.asList(

            ExactGeneralRankingBasedExtensionReasoner.AcceptanceCondition.RB_ARG_ABS_STRENGTH,
            ExactGeneralRankingBasedExtensionReasoner.AcceptanceCondition.RB_ARG_REL_STRENGTH,
            ExactGeneralRankingBasedExtensionReasoner.AcceptanceCondition.RB_ATT_ABS_STRENGTH
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
        all_principles.add(Principle.DEFENSE);
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
            for (var bed : AKZEPTANZBEDINGUNGEN) {
                evaluate(bed, rank);
            }
        }

    }

    private static void evaluate(ExactGeneralRankingBasedExtensionReasoner.AcceptanceCondition acceptanceCondition, ExactGeneralRankingBasedExtensionReasoner.RankingSemantics rankingSemantics) throws IOException {
        var threshholds = ThresholdValuesForRBSemantics.getThresholdForSemantics(rankingSemantics, acceptanceCondition);


        File[] apxFiles = new File(
                ".\\org-tweetyproject-arg-rank-ext\\src\\main\\resources\\test_data_complete")
                .listFiles(new ApxFilenameFilter());
        for (var vorg : approach) {
            for (var epsilon : epsilon_values) {

                var dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);

                ExactGeneralRankingBasedExtensionReasoner reasoner;


                for (BigDecimal thresh : threshholds) {


                    reasoner = new ExactGeneralRankingBasedExtensionReasoner(acceptanceCondition,
                            rankingSemantics, vorg, thresh, epsilon);
                    DetailedRankingExtensionbasedEvaluator evaluator = new DetailedRankingExtensionbasedEvaluator(dg,
                            reasoner, all_principles);


                    var ev = evaluator.evaluatePrinciples(Objects.requireNonNull(apxFiles).length, true);


                    System.out.println(ev.prettyPrint());
                    ev.printCsv(rankingSemantics, acceptanceCondition, vorg);


                }

            }

        }


    }
}











