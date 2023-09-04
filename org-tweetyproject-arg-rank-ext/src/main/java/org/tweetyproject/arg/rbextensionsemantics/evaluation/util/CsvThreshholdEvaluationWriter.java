package org.tweetyproject.arg.rbextensionsemantics.evaluation.util;
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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.tweetyproject.arg.dung.principles.Principle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Program for documenting results of a threshold evaluation.
 *
 * @author Carola Bauer
 */
public class CsvThreshholdEvaluationWriter {

    final String title;
    final String x_axis;
    final String y_axis;
    final List<ThresholdEvaluationObject> data;


    public CsvThreshholdEvaluationWriter(String title, String x_axis, String y_axis, List<ThresholdEvaluationObject> data) {

        this.title = title;
        this.data = data;
        this.y_axis = y_axis;
        this.x_axis = x_axis;


    }


    /**
     * Creates a csv documenting the results of a threshold evaluation wrt. the principles fulfilled
     * @throws IOException if Csv cannot be created
     */
    public void createCsv() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(".\\org-tweetyproject-arg-rank-ext\\src\\main\\java\\org\\tweetyproject\\arg\\rbextensionsemantics\\evaluation\\results\\threshold"+title+".csv"));

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("Bezeichnung", x_axis, y_axis, "principles_fulfilled", "principles_not_fulfilled", "node_ext_percentage")
                .build();


        try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            data.stream().forEach(obj -> {
                        var bezeichnung = obj.getName();
                        var thresholds = obj.getThresholds();
                        var principlesFulfilled = obj.getPrinziplesFulfilled();
                        var principlesNotFulfilled = obj.getPrinziplesNotFulfilled();
                        for (var i = 0; i < obj.getThresholds().size(); i++) {
                            try {
                                printer.printRecord(bezeichnung, thresholds.get(i), principlesFulfilled.get(i).size(),
                                        getPrinciples(principlesFulfilled.get(i)),
                                        getPrinciples(principlesNotFulfilled.get(i)),
                                        obj.getNumberOfNodes());


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            printer.flush();
        }


    }


    /**
     * Creates a CSV with detailed information about which principles are fulfilled for each threshold.
     * @throws IOException if Csv cannot be created
     */
    public void createCsvForChart() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(".\\org-tweetyproject-arg-rank-ext\\src\\main\\java\\org\\tweetyproject\\arg\\rbextensionsemantics\\evaluation\\results\\threshold\\"+title+".csv"));
        var bezeichnungen = data.stream().map(obj -> obj.getName()).distinct().collect(Collectors.joining(",","",""));
        bezeichnungen = bezeichnungen.replaceAll("\"", "");
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(x_axis, bezeichnungen)
                .build();


        try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            List<BigDecimal> thresholds = data.get(0).getThresholds();
            for (var i =0; i< thresholds.size(); i++) {
                List<Integer> principlesList = new ArrayList<>();
                final int index = i;

                data.stream().forEach(obj -> {
                    principlesList.add((obj.getPrinziplesFulfilled().get(index)).size());

                });
                String s = principlesList.stream().map(number -> number.toString()).collect(Collectors.joining(",", "", ""));
                s= s.replaceAll("\"", "");
                printer.printRecord(thresholds.get(index),s);
            }

            printer.flush();
        }


    }


    private static String getPrinciples(List<Principle> principles) {
        return principles.stream()
                .map(princ -> princ.getName()).collect(Collectors.joining(" / ", "{", "}"));
    }


}
