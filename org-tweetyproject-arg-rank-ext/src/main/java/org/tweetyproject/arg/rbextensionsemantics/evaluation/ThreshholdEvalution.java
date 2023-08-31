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
package org.tweetyproject.arg.rbextensionsemantics.evaluation;

import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.principles.ExtensionbasedPrincipleEvaluator;
import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.util.FileDungTheoryGenerator;
import org.tweetyproject.arg.rbextensionsemantics.evaluation.util.CsvThreshholdEvaluationWriter;
import org.tweetyproject.arg.rbextensionsemantics.evaluation.util.ThresholdEvaluationObject;
import org.tweetyproject.arg.rbextensionsemantics.exactreasoner.ExactGeneralRankingBasedExtensionReasoner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.tweetyproject.arg.rbextensionsemantics.exactreasoner.ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung.*;
import static org.tweetyproject.arg.rbextensionsemantics.exactreasoner.ExactGeneralRankingBasedExtensionReasoner.RankingSemantics.*;

/**
 * Evaluation program to determine optimum threshold for the
 * new extension semantics based on gradual semantics.
 *
 * @author Carola Bauer
 */
public class ThreshholdEvalution {
    private static Collection<Principle> all_principles;

    private static final BigDecimal[] epsilon_values = {
            BigDecimal.valueOf(0.01),BigDecimal.valueOf(0.001),
            BigDecimal.valueOf(0.0001)
            , BigDecimal.valueOf(0.00001)

    };


    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.Vorgehensweise> vorgehen = new ArrayList<>(
            List.of(
                    ExactGeneralRankingBasedExtensionReasoner.Vorgehensweise.ADMISSIBLE,

                    ExactGeneralRankingBasedExtensionReasoner.Vorgehensweise.SIMPLE

            ));

    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung> akzeptanzbedingungen = Arrays.asList(

            RB_ARG_ABS_STRENGTH,
            RB_ATT_ABS_STRENGTH,
            RB_ARG_REL_STRENGTH
    );

    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.RankingSemantics> rank_semantics = new ArrayList<>(List.of(
            MAX,
            TRUST,
            COUNTING,
            MATT_TONI,
            CATEGORIZER,
            NSA,
            ITS,
            EULER
    ));

    public static void main(String[] args) throws IOException {
        all_principles = new HashSet<>();
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
        var threshholds = ThresholdValuesForRBSemantics.getThresholdValues(rankingSemantics);


        File[] apxFiles;
        List<ThresholdEvaluationObject> data = new ArrayList<>();
        apxFiles = new File(
                "C:\\TweetyProject\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rbextensionsemantics\\resources\\threshold_evaluation")
                .listFiles(new ApxFilenameFilter());



        for (var vorg : vorgehen) {
            for (var epsilon : epsilon_values) {

                var dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);
                String bezeichnung = rankingSemantics + " mit Epsilon=" + epsilon;
                List<List<Principle>> principles_fulfilled = new ArrayList<>();
                List<List<Principle>> principles_not_fulfilled = new ArrayList<>();
                List<Double> percentage_of_ext_nodes = new ArrayList<>();
                ExactGeneralRankingBasedExtensionReasoner reasoner;


                for (BigDecimal thresh : threshholds) {

                        reasoner = new ExactGeneralRankingBasedExtensionReasoner(akzeptanzbedingung,
                                rankingSemantics, vorg, thresh, epsilon);
                        ExtensionbasedPrincipleEvaluator evaluator = new ExtensionbasedPrincipleEvaluator(dg,
                                reasoner, all_principles);

                        List<Principle> principlesNotFulfilled = new ArrayList<>();

                        var ev = evaluator.evaluate(
                                apxFiles.length,
                                true);
                        List<Principle> principlesFulfilled = new ArrayList<>();


                        for (var princ : all_principles) {

                            if (ev.getNegativeInstances(princ).size() > 0) {
                                principlesNotFulfilled.add(princ);
                            } else {
                                principlesFulfilled.add(princ);

                            }
                        }
                        System.out.println(ev.prettyPrint());

                        principles_fulfilled.add(principlesFulfilled);
                        principles_not_fulfilled.add(principlesNotFulfilled);
                        percentage_of_ext_nodes.add(ev.getPercentagesNodes().stream().mapToDouble(perc -> (double) perc).sum() / Double.valueOf(ev.getPercentagesNodes().size()));


                        System.out.println(evaluator.evaluate(1000, true).prettyPrint());

                }
                data.add(new ThresholdEvaluationObject(bezeichnung, principles_fulfilled, principles_not_fulfilled, threshholds, percentage_of_ext_nodes));

            }
        }

        new CsvThreshholdEvaluationWriter("Threshold_evaluation_principles_detail_for_" + rankingSemantics + "_" + akzeptanzbedingung + "_" + Math.random(), "Value for threshold delta", "Number of Principles fulfilled", data).createCsvForChart();
        new CsvThreshholdEvaluationWriter("Threshold_evaluation_principles_number_for_" + rankingSemantics + "_" + akzeptanzbedingung + "_" + Math.random(), "Value for threshold delta", "Number of Principles fulfilled", data).createCsv();

    }
}






