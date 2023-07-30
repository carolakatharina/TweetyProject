package org.tweetyproject.arg.rankings.rankingbasedextension;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;
import org.tweetyproject.arg.dung.principles.Principle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvEvaluationTransformer {

    final String path;
    final static List<Principle> all_principles = List.of(Principle.ADMISSIBILITY, Principle.STRONG_ADMISSIBILITY, Principle.REDUCT_ADM, Principle.SEMIQUAL_ADM, Principle.CONFLICT_FREE, Principle.DEFENCE

            , Principle.NAIVETY, Principle.I_MAXIMALITY, Principle.REINSTATEMENT, Principle.WEAK_REINSTATEMENT, Principle.CF_REINSTATEMENT, Principle.INRA, Principle.MODULARIZATION, Principle.SCC_RECURSIVENESS, Principle.DIRECTIONALITY);


    public CsvEvaluationTransformer(String path) {
        this.path = path;


    }

    public static void main(String args[]) throws IOException {
        readAndWriteCsv();
    }

    public static void readAndWriteCsv() throws IOException {
        File[] files = new File(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\results\\neuegrafiken\\").listFiles();
        for (var file : files) {

            var filename = file.getName();
            if (filename.contains("korrigall") || filename.contains("simple33all") || filename.contains("simple2all")) {
                BufferedReader reader = Files.newBufferedReader(Paths.get(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\results\\neuegrafiken\\" + filename));
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\results\\neuegrafiken\\grafik\\"+ "GRAFIK" + filename));
                var headers = new String[all_principles.size() + 1];
                headers[0] = "threshold delta";


                for (int i = 0; i < all_principles.size(); i++) {
                    var name = all_principles.get(i).getName();
                    if (name.contains("Conflict-Free")) {
                        name = "Conflict-Freeness";
                    }
                    if (name.contains("SCC Decomposability")) {
                        name = "SCC Recursiveness";
                    }

                    headers[i + 1] = name;
                }
                var csvFormat = CSVFormat.DEFAULT.builder().setHeader(headers);

                var settings = new CsvParserSettings();
                settings.setDelimiterDetectionEnabled(true);
                settings.setLineSeparatorDetectionEnabled(true);
                settings.detectFormatAutomatically();
                settings.setMaxCharsPerColumn(100000);

                CsvParser parser = new CsvParser(settings);
                //System.out.println(parser.parseAllRecords(reader));
                try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat.build())) {
                    for (var line : parser.parseAllRecords(reader)) {
                        System.out.println(line);
                        if (line.getString(0).contains("mit Epsilon=0.00010")) {
                            var record = new ArrayList<String>();
                            record.add(line.getString(1));
                            for (int i = 0; i < all_principles.size(); i++) {
                                if (line.getString(3).contains("/ "+all_principles.get(i).getName())||
                                line.getString(3).contains("{"+all_principles.get(i).getName())) {
                                    record.add("" + 1);
                                } else {
                                    record.add("" + 0);
                                }
                            }

                            printer.printRecord(record);

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

