package org.tweetyproject.arg.rankings.rankingbasedextension;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.ApxWriter;

import java.io.File;
import java.io.IOException;

public class GraphGenerationProgrammNumberOfAttackers {

    public static void main (String args [])  {

        for (int count = 0; count < 1; count=count + 100) {
            var writer = new ApxWriter();

            DungTheory dt = new DungTheory();
            Argument a0 = new Argument("a0");
            dt.add(a0);
            Argument a1 = new Argument("a1");
            dt.add(a1);
            Argument a2 = new Argument("a2");
            dt.add(a2);
            Argument a3 = new Argument("a3");
            dt.add(a3);
            dt.add(new Attack(a0,a1));
            dt.add(new Attack(a1,a2));
            dt.add(new Attack(a0,a0));
            dt.add(new Attack(a0,a3));
            /*for (int i = 3; i <= 13+count; i++) {
                Argument ax = new Argument("a" + i);
                Argument ax1 = new Argument("ax" + i);
                dt.add(ax);
                dt.add(ax1);
                dt.add(new Attack(ax, a0));
                dt.add(new Attack(ax, ax1));
                dt.add(new Attack(ax1, ax));
            }

             */

            try {
                writer.write(dt, new File(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\data_shifting_threshold\\s\\mtwithselfloop" + count + ".apx"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }


}
