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
import org.tweetyproject.arg.dung.reasoner.AbstractDungReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.*;
import org.tweetyproject.arg.rankings.postulates.RankingPostulate;
import org.tweetyproject.arg.rankings.reasoner.*;
import org.tweetyproject.commons.postulates.PostulateEvaluator;
import org.tweetyproject.math.probability.Probability;

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
public class RankingPostulatesExample {
	private static Collection<RankingPostulate> all_postulates;

	private static final Collection<RankingBasedExtensionReasoner.RankingSemantics> rank_semantics = new ArrayList<>(List.of(
			TRUST
	));

	public static void main(String[] args) {
		all_postulates = new HashSet<>();

		all_postulates.add(RankingPostulate.ABSTRACTION);
		all_postulates.add(RankingPostulate.ADDITIONOFATTACKBRANCH);
		all_postulates.add(RankingPostulate.ADDITIONOFDEFENSEBRANCH);
		all_postulates.add(RankingPostulate.ATTACKVSFULLDEFENSE);
		all_postulates.add(RankingPostulate.CARDINALITYPRECEDENCE);
		all_postulates.add(RankingPostulate.COUNTERTRANSITIVITY);


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




		for (var rank: rank_semantics) {

				Example(rank);

		}


	}


	public static void Example(RankingBasedExtensionReasoner.RankingSemantics rankingSemantics) {


		var params = new DungTheoryGenerationParameters();
		params.attackProbability=0.9;
		params.enforceTreeShape=true;
		params.avoidSelfAttacks=false;
		params.numberOfArguments=4;
		DungTheoryGenerator dg = new DefaultDungTheoryGenerator(params);
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				getReasoner(
						rankingSemantics));
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());


		params = new DungTheoryGenerationParameters();
		params.attackProbability=0.5;
		params.enforceTreeShape=false;
		params.avoidSelfAttacks=false;
		params.numberOfArguments=5;
		dg = new DefaultDungTheoryGenerator(params);
		evaluator = new PostulateEvaluator<>(dg,
				getReasoner(
						rankingSemantics));
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

		params = new DungTheoryGenerationParameters();
		params.attackProbability=0.5;
		params.enforceTreeShape=false;
		params.avoidSelfAttacks=true;
		params.numberOfArguments=10;
		dg = new DefaultDungTheoryGenerator(params);
		evaluator = new PostulateEvaluator<>(dg,
				getReasoner(
						rankingSemantics));
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());


		params = new DungTheoryGenerationParameters();
		params.attackProbability=0.2;
		params.enforceTreeShape=true;
		params.avoidSelfAttacks=true;
		params.numberOfArguments=22;
		dg = new DefaultDungTheoryGenerator(params);
		evaluator = new PostulateEvaluator<>(dg,
				getReasoner(
						rankingSemantics));
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());


		//Tests für DP/DDP
		File[] apxFiles = new File("C:\\Users\\Carola\\Desktop\\TweetyProject\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension").listFiles(new ApxFilenameFilter());


		dg = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);


		evaluator = new PostulateEvaluator<>(dg,
				getReasoner(
						rankingSemantics));
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(apxFiles.length, true).prettyPrint());



	}

	public static AbstractRankingReasoner getReasoner(RankingBasedExtensionReasoner.RankingSemantics sem)  {
		return switch (sem) {
			case CATEGORIZER -> new CategorizerRankingReasoner();
			case STRATEGY -> new StrategyBasedRankingReasoner();
			case SAF -> new SAFRankingReasoner();
			case COUNTING -> new CountingRankingReasoner();
			case MAX -> new MaxBasedRankingReasoner();
			case TRUST -> new TrustBasedRankingReasoner();
			case EULER_MB -> new EulerMaxBasedRankingReasoner();
			case BURDEN -> new BurdenBasedRankingReasoner();
			case ALPHABBS -> new AlphaBurdenBasedRankingReasoner();
			case TUPLES -> new TuplesRankingReasoner();
			case ITS -> new IterativeSchemaRankingReasoner();
			case DISCUSSION -> new DiscussionBasedRankingReasoner();
			default -> {
				System.out.println(sem);
				throw new RuntimeException();
			}
		};
	}


	public static void CategorizerExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator() {
		};
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new CategorizerRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());
		/*
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new CategorizerRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());

		 */

	}


	public static void CountingExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new CountingRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new CountingRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());

	}


	public static void MaxExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new MaxBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new MaxBasedRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());

	}


	public static void AlphaBbsExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new AlphaBurdenBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new AlphaBurdenBasedRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());

	}



	public static void EulerMaxBasedExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new EulerMaxBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, false).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new EulerMaxBasedRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());
	}



	public static void TrustBasedExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new TrustBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, false).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new TrustBasedRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());
	}


	public static void  IterativeSchemaExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new IterativeSchemaRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, false).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new IterativeSchemaRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());
	}

	public static void BurdenExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new BurdenBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(100, false).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new BurdenBasedRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());
	}

	public static void DiscussionExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new DiscussionBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(2000, false).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new DiscussionBasedRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());
	}

	public static void TuplesExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new TuplesRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, false).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new TuplesRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());
	}

	public static void StrategyBasedExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new StrategyBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(10, false).prettyPrint());
	}

	public static void SAFExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new SAFRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(2000, false).prettyPrint());
		DungTheoryGenerator dg2 = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument, DungTheory> evaluator2 = new PostulateEvaluator<>(dg2,
				new SAFRankingReasoner());
		evaluator2.addAllPostulates(all_postulates);
		System.out.println(evaluator2.evaluate(4000, true).prettyPrint());
	}

	public static void PropagationExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PropagationRankingReasoner propagation_reasoner = new PropagationRankingReasoner(0.75, false, PropagationRankingReasoner.PropagationSemantics.PROPAGATION1);
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				propagation_reasoner);
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(2000, false).prettyPrint());
	}




}
