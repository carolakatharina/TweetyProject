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

import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.principles.ExtensionbasedPrincipleEvaluator;
import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.FileDungTheoryGenerator;
import org.tweetyproject.arg.rankings.rankingbasedextension.evaluation.CsvThreshholdEvaluationWriter;
import org.tweetyproject.arg.rankings.rankingbasedextension.evaluation.LineChartDrawing;
import org.tweetyproject.arg.rankings.rankingbasedextension.evaluation.ThresholdEvaluationObject;
import org.tweetyproject.arg.rankings.rankingbasedextension.evaluation.ThresholdValuesForRBSemantics;
import org.tweetyproject.arg.rankings.rankingbasedextension.exactreasoner.ExactGeneralRankingBasedExtensionReasoner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.tweetyproject.arg.rankings.rankingbasedextension.exactreasoner.ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung.RB_ATT_ABS_STRENGTH;
import static org.tweetyproject.arg.rankings.rankingbasedextension.exactreasoner.ExactGeneralRankingBasedExtensionReasoner.RankingSemantics.*;
import static org.tweetyproject.arg.rankings.rankingbasedextension.exactreasoner.ExactGeneralRankingBasedExtensionReasoner.Vergleichsoperator.STRICT;

/**
 * Example code for evaluating weighted ranking semantics in regard
 * to postulates. Each postulate represents a single property
 * that characterizes how the semantics ranks arguments.
 *
 * @author Carola Bauer
 */
public class ThreshholdEvalution {
    private static Collection<Principle> all_principles;

    private static BigDecimal[] epsilon_values = {//BigDecimal.valueOf(0.01),BigDecimal.valueOf(0.001), //BigDecimal.valueOf(0.00001),
            BigDecimal.valueOf(0.0001)
    };


    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.Vorgehensweise> vorgehen = new ArrayList<>(
            List.of(ExactGeneralRankingBasedExtensionReasoner.Vorgehensweise.SIMPLE
                    /*
                    RankingBasedExtensionReasoner.Vorgehensweise.STRONGEST_CF,
                    RankingBasedExtensionReasoner.Vorgehensweise.INC_BUDGET,
                     */
                    //RankingBasedExtensionReasoner.Vorgehensweise.MAX_CF,
                    //RankingBasedExtensionReasoner.Vorgehensweise.CF
            ));

    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung> akzeptanzbedingungen = Arrays.asList(

            //RankingBasedExtensionReasoner.Akzeptanzbedingung.values()
            //RB_ARG_ABS_STRENGTH
            RB_ATT_ABS_STRENGTH
    );

    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.RankingSemantics> rank_semantics = new ArrayList<>(List.of(
            //MAX
            //TRUST,
            //COUNTING,
            ITS
            //CATEGORIZER
            //NSA
            /*
            ITS,
            EULER,
            MAX

             */

            //MATT_TONI,
            /* EULER,
            TRUST,
            ITS

             */



            /*,ALPHABBS_1,
             ALPHABBS_0,
            ALPHABBS_2*/

    ));

    public static void main(String[] args) throws IOException {
        all_principles = new HashSet<>();

        all_principles.add(Principle.ADMISSIBILITY);
        all_principles.add(Principle.STRONG_ADMISSIBILITY);
        all_principles.add(Principle.REDUCT_ADM);
        all_principles.add(Principle.SEMIQUAL_ADM);
        //all_principles.add(Principle.WEAK_ADMISSIBILITY);
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


        List<String[]> pathsuffixe = new ArrayList<>();
        pathsuffixe.add(new String[]{"all_withoutbigafs"});
        //new String []{"\\iccma\\", "iccma19", "\\A"},
        //new String []{"\\iccma\\", "iccma19", "\\B"},
        //new String []{"\\iccma\\", "iccma19", "\\C"},
        //new String[] {"\\iccma\\", "iccma19", "\\selected"});
        //new String []{"\\iccma\\", "iccma19", "\\E"});

        for (var rank : rank_semantics) {
            for (var bed : akzeptanzbedingungen) {
                for (String[] pathsuffix : pathsuffixe) {
                    Example(bed, rank, pathsuffix);
                }
            }
        }

    }

    public static void Example(ExactGeneralRankingBasedExtensionReasoner.Akzeptanzbedingung akzeptanzbedingung, ExactGeneralRankingBasedExtensionReasoner.RankingSemantics rankingSemantics, String[] pathsuffix) throws IOException {
        var threshholds = ThresholdValuesForRBSemantics.getThresholdValues(rankingSemantics);


        File[] apxFiles;
        List<ThresholdEvaluationObject> data = new ArrayList<>();
        apxFiles = new File(
                "C:\\TweetyProject\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\all_withoutbigafs")
                .listFiles(new ApxFilenameFilter());
        for (var vorg : vorgehen) {
            for (var epsilon : epsilon_values) {

                var dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);
                String bezeichnung = rankingSemantics + " mit Epsilon=" + epsilon;
                List<List<Principle>> principles_fulfilled = new ArrayList<>();
                List<List<Principle>> principles_not_fulfilled = new ArrayList<>();
                List<List<List<Integer>>> number_of_nodes = new ArrayList<>();
                for (BigDecimal thresh : threshholds) {
                    for (var vergleichsop : List.of(STRICT)) {

                        ExactGeneralRankingBasedExtensionReasoner reasoner = new ExactGeneralRankingBasedExtensionReasoner(akzeptanzbedingung,
                                rankingSemantics, vorg, thresh, vergleichsop, epsilon);
                        ExtensionbasedPrincipleEvaluator evaluator = new ExtensionbasedPrincipleEvaluator(dg,
                                reasoner, all_principles);

                        List<Principle> principlesNotFulfilled = new ArrayList<>();
                        List<List<Integer>> numberOfNodes = new ArrayList<>();
                        var ev = evaluator.evaluate(apxFiles.length, true);
                        List<Principle> principlesFulfilled = new ArrayList<>();


                        for (var princ : all_principles) {

                            if (ev.getNegativeInstances(princ).size() > 0) {
                                principlesNotFulfilled.add(princ);
                            } else {
                                principlesFulfilled.add(princ);
                                final AtomicInteger all_nodes = new AtomicInteger(0);
                                final AtomicInteger ext_nodes = new AtomicInteger(0);

                                ev.getPositiveInstances(princ)
                                       .stream().forEach(
                                                inst -> {
                                                    all_nodes.set(all_nodes.get()+((DungTheory) inst).getNumberOfNodes());
                                                    ext_nodes.set(ext_nodes.get()+(reasoner.getModels((DungTheory) inst).stream()
                                                            .findFirst().get().size()));
                                                }

                                       );
                                numberOfNodes.add(List.of(all_nodes.get(), ext_nodes.get()));
                            }
                        }
                        System.out.println(ev.prettyPrint());

                        principles_fulfilled.add(principlesFulfilled);
                        principles_not_fulfilled.add(principlesNotFulfilled);
                        number_of_nodes.add(numberOfNodes);


                        //System.out.println(evaluator.evaluate(1000, true).prettyPrint());
                    }
                }
                data.add(new ThresholdEvaluationObject(bezeichnung, principles_fulfilled, principles_not_fulfilled, threshholds, number_of_nodes));


            }
        }

        new LineChartDrawing("Threshold_evaluation_for_" + rankingSemantics + "_" + akzeptanzbedingung + "_" + pathsuffix[0] + "detailed5", "Value for threshold delta", "Number of Principles fulfilled", data);
        new CsvThreshholdEvaluationWriter("Threshold_evaluation_for_" + rankingSemantics + "_" + akzeptanzbedingung + "_" + pathsuffix[0] + "detailed_numberextension", "Value for threshold delta", "Number of Principles fulfilled", data).createCsvForChart();
        new CsvThreshholdEvaluationWriter("Threshold_evaluation_for_" + rankingSemantics + "_" + akzeptanzbedingung + "_" + pathsuffix[0] + "detailed_numberextension", "Value for threshold delta", "Number of Principles fulfilled", data).createCsv();

        //csv: givenDataArray_whenConvertToCSV_thenOutputCreated("Threshold_evaluation_" + rankingSemantics + "_absolute_argument_strength", "Value for threshold delta", "Number of Principles fulfilled", data);
    }
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



