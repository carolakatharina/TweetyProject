package org.tweetyproject.arg.rankings.rankingbasedextension;

import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.reasoner.*;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.FileDungTheoryGenerator;
import org.tweetyproject.commons.Parser;

import java.io.File;

import java.io.IOException;
import java.util.stream.Collectors;

public class ReasonerExample {
    File[] apxFiles = new File(
                    "C:\\TweetyProject\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data_results")
                    .listFiles(new ApxFilenameFilter());
    FileDungTheoryGenerator dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);

    public static void main(String[] args) throws IOException {
        File[] apxFiles = new File(
                "C:\\TweetyProject\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\resources\\ex")
                .listFiles(new ApxFilenameFilter());
        FileDungTheoryGenerator dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);

        Parser<DungTheory,?> parser = new ApxParser();

        DungTheory bbase = parser.parseBeliefBaseFromFile(apxFiles[0].getAbsolutePath());
        System.out.println("classic");
        System.out.println(new SimpleAdmissibleReasoner().getModels(bbase));
        System.out.println(new SimpleGroundedReasoner().getModels(bbase));
        System.out.println(new SimplePreferredReasoner().getModels(bbase));
        System.out.println(new SimpleCompleteReasoner().getModels(bbase));
        System.out.println(new SimpleStableReasoner().getModels(bbase));

        System.out.println("weak");
        System.out.println(new WeaklyAdmissibleReasoner().getModels(bbase));
        System.out.println(new WeaklyCompleteReasoner().getModels(bbase));
        System.out.println(new WeaklyGroundedReasoner().getModels(bbase));
        System.out.println(new WeaklyPreferredReasoner().getModels(bbase));

        System.out.println("naive");
        System.out.println(new SimpleNaiveReasoner().getModels(bbase));
        System.out.println(new SimpleStageReasoner().getModels(bbase));
        System.out.println(new Stage2Reasoner().getModels(bbase));
        System.out.println(new SccCF2Reasoner().getModels(bbase));


        //prudent
        System.out.println("prudent");
        System.out.println(new SimpleGroundedReasoner().getModels(bbase).stream().flatMap(ext -> ext.stream().filter(arg ->
                ext.stream().noneMatch(arg2 -> bbase.isIndirectAttack(arg2, arg)))).collect(Collectors.toList()));
        System.out.println(new SimplePreferredReasoner().getModels(bbase).stream().flatMap(ext -> ext.stream().filter(arg ->
                ext.stream().noneMatch(arg2 -> bbase.isIndirectAttack(arg2, arg)))).collect(Collectors.toList()));
        System.out.println(new SimpleCompleteReasoner().getModels(bbase).stream().flatMap(ext -> ext.stream().filter(arg ->
                ext.stream().noneMatch(arg2 -> bbase.isIndirectAttack(arg2, arg)))).collect(Collectors.toList()));
        System.out.println(new SimpleStableReasoner().getModels(bbase).stream().flatMap(ext -> ext.stream().filter(arg ->
                ext.stream().noneMatch(arg2 -> bbase.isIndirectAttack(arg2, arg)))).collect(Collectors.toList()));



    }
}
