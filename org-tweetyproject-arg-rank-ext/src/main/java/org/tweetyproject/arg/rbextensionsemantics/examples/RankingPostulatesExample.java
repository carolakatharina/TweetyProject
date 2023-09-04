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
 *  along with this program. If not, see <http:www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http:tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rbextensionsemantics.examples;

import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.FileDungTheoryGenerator;
import org.tweetyproject.arg.rankings.postulates.RankingPostulate;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.arg.rbextensionsemantics.exactreasoner.*;
import org.tweetyproject.commons.postulates.PostulateEvaluator;
import org.tweetyproject.comparator.ExactNumericalPartialOrder;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.tweetyproject.arg.rbextensionsemantics.exactreasoner.ExactGeneralRankingBasedExtensionReasoner.RankingSemantics.*;

/**
 * Example code for evaluating ranking semantics with regard
 * to postulates. Each postulate represents a single property
 * that characterizes how the semantics ranks arguments.
 *
 * @author Carola Bauer
 */
public class RankingPostulatesExample {
    private static Collection<RankingPostulate> all_postulates;
    private static BigDecimal epsilon = BigDecimal.valueOf(0.0001);

    private static final Collection<ExactGeneralRankingBasedExtensionReasoner.RankingSemantics> rank_semantics = new ArrayList<>(List.of(

            CATEGORIZER,
            COUNTING,
            EULER,  ITS, TRUST, MAX, NSA,
            MATT_TONI

    ));


    public static void main(String[] args) {
        all_postulates = new HashSet<>();

        all_postulates.add(RankingPostulate.INCREASEOFATTACKBRANCH);
        all_postulates.add(RankingPostulate.INCREASEOFDEFENSEBRANCH);

        all_postulates.add(RankingPostulate.STRICTCOUNTERTRANSITIVITY);

        all_postulates.add(RankingPostulate.ABSTRACTION);
        all_postulates.add(RankingPostulate.ADDITIONOFATTACKBRANCH);
        all_postulates.add(RankingPostulate.ADDITIONOFDEFENSEBRANCH);


         
        all_postulates.add(RankingPostulate.ATTACKVSFULLDEFENSE);
        all_postulates.add(RankingPostulate.CARDINALITYPRECEDENCE);
        all_postulates.add(RankingPostulate.COUNTERTRANSITIVITY);
        all_postulates.add(RankingPostulate.STRICTCOUNTERTRANSITIVITY);


        all_postulates.add(RankingPostulate.COUNTING);
        all_postulates.add(RankingPostulate.DEFENSEPRECEDENCE);
        all_postulates.add(RankingPostulate.DISTDEFENSEPRECEDENCE);


        all_postulates.add(RankingPostulate.INCREASEOFATTACKBRANCH);
        all_postulates.add(RankingPostulate.INCREASEOFDEFENSEBRANCH);


        all_postulates.add(RankingPostulate.INDEPENDENCE);

        all_postulates.add(RankingPostulate.NONATTACKEDEQUIVALENCE);
        all_postulates.add(RankingPostulate.QUALITYPRECEDENCE);
        all_postulates.add(RankingPostulate.SELFCONTRADICTION);
        all_postulates.add(RankingPostulate.STRICTADDITIONOFDEFENSEBRANCH);
        all_postulates.add(RankingPostulate.STRICTCOUNTERTRANSITIVITY);
        all_postulates.add(RankingPostulate.TOTAL);
        all_postulates.add(RankingPostulate.VOIDPRECEDENCE);




        File[] apxFiles;
        apxFiles = new File(
                ".\\org-tweetyproject-arg-rank-ext\\src\\main\\resources\\detailed_evaluation")
                .listFiles(new ApxFilenameFilter());


        for (var rank : rank_semantics) {

            Example(rank, apxFiles);

        }


    }


    public static void Example(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics rankingSemantics, File[] apxFiles) {

        var dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);
        PostulateEvaluator<Argument, DungTheory> evaluator;
        evaluator = new PostulateEvaluator<>(dg,
                getReasoner(
                        rankingSemantics));
        evaluator.addAllPostulates(all_postulates);
        System.out.println(evaluator.evaluate(apxFiles.length, false).prettyPrint());

         


    }

    public static AbstractRankingReasoner<ExactNumericalPartialOrder<Argument, DungTheory>> getReasoner(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics sem) {
        return switch (sem) {
            case CATEGORIZER -> new ExactCategorizerRankingReasoner(epsilon);
            case EULER -> new ExactEulerMaxBasedRankingReasoner(epsilon);
            case ITS -> new ExactIterativeSchemaRankingReasoner(epsilon);
            case COUNTING -> new ExactCountingRankingReasoner(BigDecimal.valueOf(0.9), epsilon);
            case MAX -> new ExactMaxBasedRankingReasoner(epsilon);
            case TRUST -> new ExactTrustBasedRankingReasoner(epsilon);
            case NSA -> new ExactNsaReasoner(epsilon);
            case MATT_TONI -> new ExactStrategyBasedRankingReasoner();
        };
    }




}
