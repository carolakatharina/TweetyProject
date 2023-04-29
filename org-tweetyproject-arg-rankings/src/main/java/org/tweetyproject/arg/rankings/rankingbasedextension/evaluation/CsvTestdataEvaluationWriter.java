package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CsvTestdataEvaluationWriter {


    final List<DataEvaluationObject> data;
    final String title;



    public CsvTestdataEvaluationWriter(String title,  List<DataEvaluationObject> data) {
        this.title=title;

        this.data = data;
    }


    public void createCsv() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\testdata_evaluation_"+title+".csv"));

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("Number of Arguments", "Number of Attacks", "has Cycles", "has odd cycles", "Number of Selfloops", "Number of SCCs")
                .build();


        try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            data.stream().forEach(obj -> {

                        try {
                            printer.printRecord(obj.graphname, obj.getNumberOfArguments(), obj.getNumberOfAttacks(), obj.isHasCycles(), obj.isHasOddCycles(), obj.getNumberOfSelfLoops(), obj.getNumberSCCs());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            printer.flush();
        }


    }





}
