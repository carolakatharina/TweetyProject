package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.IsoSafeEnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.dung.writer.ApxWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphGenerator {


    public static void main (String[]args) throws IOException {
        List<DungTheory> theory = new ArrayList<>();
        var generator = new IsoSafeEnumeratingDungTheoryGenerator();
        for (int i=0; i<1000; i++) {
            theory.add(generator.next());
        }
        var writer = new ApxWriter();
        for (int i=0; i<theory.size(); i++) {
            if (theory.get(i).containsCycle()) {
                if (theory.get(i).hasSelfLoops()) {
                    writer.write(theory.get(i), new File(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\selfgenerated\\withcycle\\withselfloop\\graph"+i+".apx"));
                }
                else {
                    writer.write(theory.get(i), new File(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\selfgenerated\\withcycle\\withoutselfloop\\graph"+i+".apx"));

                }
            } else {
                writer.write(theory.get(i), new File(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\selfgenerated\\withoutcycle\\graph"+i+".apx"));

            }
        }

    }
}
