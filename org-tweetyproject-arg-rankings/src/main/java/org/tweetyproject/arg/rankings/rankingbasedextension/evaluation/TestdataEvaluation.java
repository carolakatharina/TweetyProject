package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DefaultGraph;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestdataEvaluation {
    public static void main(String args[]) throws IOException {
        File[] apxFiles = new File("C:\\Users\\Carola\\OneDrive\\Desktop\\TweetyProject\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\all_withoutbigafs")
                .listFiles(new ApxFilenameFilter());

        List<DataEvaluationObject> theories=new ArrayList<>();

        var parser = new ApxParser();
        DungTheory theory;
        for (File f: apxFiles) {
            try {
                System.out.println(f.getName());
                theory = parser.parseBeliefBase(new FileReader(f));
                theories.add(new DataEvaluationObject(f.getName(), theory.countSelfLoops(), theory.containsCycle(), theory.containsOddCycle(),theory.getNumberOfNodes(), theory.countAttacks(), theory.countStronglyConnectedComponents()));
            } catch (Exception e) {
                System.out.println(f.getName());
                throw new RuntimeException(e);
            }

        }
        new CsvTestdataEvaluationWriter("all_withoutbigafs", theories).createCsv();

    }
}
