package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

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


    public void createCsv() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\results\\"+title+".csv"));

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("Bezeichnung", x_axis, y_axis, "principles_fulfilled", "principles_not_fulfilled")
                .build();


        try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            data.stream().forEach(obj -> {
                        var bezeichnung = obj.getBezeichnung();
                        var thresholds = obj.getThresholds();
                        var principlesFulfilled = obj.getPrinziplesFulfilled();
                        var principlesNotFulfilled = obj.getPrinziplesNotFulfilled();
                        for (var i = 0; i < obj.getThresholds().size(); i++) {
                            try {
                                printer.printRecord(bezeichnung, thresholds.get(i), principlesFulfilled.get(i).size(),
                                        getPrinciples(principlesFulfilled.get(i)),
                                        getPrinciples(principlesNotFulfilled.get(i)));


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            printer.flush();
        }


    }




    public void createCsvForChart() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\results\\"+title+".csv"));
        var bezeichnungen = data.stream().map(obj -> obj.getBezeichnung()).distinct().collect(Collectors.joining(",","",""));
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
