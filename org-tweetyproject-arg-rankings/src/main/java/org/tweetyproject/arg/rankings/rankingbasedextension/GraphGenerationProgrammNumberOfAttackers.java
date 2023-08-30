package org.tweetyproject.arg.rankings.rankingbasedextension;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.ApxWriter;

import java.io.File;
import java.io.IOException;

public class GraphGenerationProgrammNumberOfAttackers {

    public static void main (String args [])  {


            var writer = new ApxWriter();

            DungTheory dt = new DungTheory();
            Argument a = new Argument("a");
            dt.add(a);

            Argument b = new Argument("b");
            dt.add(b);
            Argument e = new Argument("e");
            dt.add(e);
            Argument d = new Argument("d");
            dt.add(d);

            dt.add(new Attack(e,d));
            dt.add(new Attack(d,a));

            for (int i = 1; i < 104; i=i+1) {
                var i1=i;
                Argument ax = new Argument("c" + i1);
                dt.add(ax);

                dt.add(new Attack(b, ax));
                dt.add(new Attack(ax, e));
            }



            try {
                writer.write(dt, new File(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\data_shifting_threshold\\EXAMPLETHESISx.apx"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        }
    }



