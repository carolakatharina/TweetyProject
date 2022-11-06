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
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.rankingbasedextension;

import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.FileDungTheoryGenerator;
import org.tweetyproject.commons.postulates.PostulateEvaluator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.tweetyproject.arg.rankings.rankingbasedextension.RankingBasedExtensionReasoner.RankingSemantics.*;

/**
 * Example code for evaluating weighted ranking semantics in regard
 * to postulates. Each postulate represents a single property
 * that characterizes how the semantics ranks arguments.
 *
 * @author Carola Bauer
 */
public class ExtensionPrinciplesExample {
    private static Collection<Principle> all_principles;

    private static final Collection<RankingBasedExtensionReasoner.Vorgehensweise> vorgehen = new ArrayList<>(
            List.of(RankingBasedExtensionReasoner.Vorgehensweise.SCC
            //, RankingBasedExtensionReasoner.Vorgehensweise.CONFLICTFREE
            ));

    private static final Collection<Semantics> ext_semantics = new ArrayList<>(List.of(Semantics.RB_ARG_STRENGTH_ABS_OR_REL_STRENGTH));
    private static final Collection<RankingBasedExtensionReasoner.RankingSemantics> rank_semantics = new ArrayList<>(List.of(
            MAX, COUNTING, ITS,  CATEGORIZER, TRUST, EULER_MB, ALPHABBS, SAF
    ));

    public static void main(String[] args) {
        all_principles = new HashSet<>();
        all_principles.add(Principle.CONFLICT_FREE);
        all_principles.add(Principle.ADMISSIBILITY);
        all_principles.add(Principle.NAIVETY);
        all_principles.add(Principle.STRONG_ADMISSIBILITY);
        all_principles.add(Principle.I_MAXIMALITY);
        all_principles.add(Principle.REINSTATEMENT);
        all_principles.add(Principle.WEAK_REINSTATEMENT);
        all_principles.add(Principle.CF_REINSTATEMENT);
        all_principles.add(Principle.DIRECTIONALITY);
        all_principles.add(Principle.INRA);
        all_principles.add(Principle.MODULARIZATION);
        all_principles.add(Principle.REDUCT_ADM);
        all_principles.add(Principle.SEMIQUAL_ADM);
        all_principles.add(Principle.SCC_DECOMPOSABILITY);
        all_principles.add(Principle.SCC_DECOMPOSABILITY);

        for (var rank: rank_semantics) {
            for (var sem: ext_semantics) {
                Example(sem, rank);
            }
        }

    }

    public static void Example(Semantics semantics, RankingBasedExtensionReasoner.RankingSemantics rankingSemantics) {
        System.out.println(rankingSemantics);
        System.out.println(semantics);

        DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
        for (var vorg: vorgehen) {
            PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                    new RankingBasedExtensionReasoner(semantics,
                            rankingSemantics, vorg));
            evaluator.addAllPostulates(all_principles);
            System.out.println(evaluator.evaluate(4000, true).prettyPrint());
        }



        //Tests für DP/DDP
        File[] apxFiles = new File("C:\\Users\\Carola\\Desktop\\TweetyProject\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension")
                .listFiles(new ApxFilenameFilter());


        dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);

        for (var vorg: vorgehen) {
            PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
                    new RankingBasedExtensionReasoner(semantics,
                            rankingSemantics, vorg));
            evaluator.addAllPostulates(all_principles);
            System.out.println(evaluator.evaluate(apxFiles.length, true).prettyPrint());
        }



    }
}
