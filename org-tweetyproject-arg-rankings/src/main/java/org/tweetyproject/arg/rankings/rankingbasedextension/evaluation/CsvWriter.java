package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.tweetyproject.arg.dung.principles.Principle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CsvWriter {

    final String title;
    final String x_axis;
    final String y_axis;
    final List<ThresholdEvaluationObject> data;


    public CsvWriter(String title, String x_axis, String y_axis, List<ThresholdEvaluationObject> data) {

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

    private static String getPrinciples(List<Principle> principles) {
        return principles.stream()
                .map(princ -> princ.getName()).collect(Collectors.joining(" / ", "{", "}"));
    }


}
