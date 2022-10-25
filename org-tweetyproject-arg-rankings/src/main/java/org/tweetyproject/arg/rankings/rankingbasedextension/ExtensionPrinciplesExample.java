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

import org.tweetyproject.arg.dung.principles.Principle;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.rankings.reasoner.*;
import org.tweetyproject.commons.postulates.PostulateEvaluator;

import java.util.Collection;
import java.util.HashSet;

/**
 * Example code for evaluating weighted ranking semantics in regard
 * to postulates. Each postulate represents a single property
 * that characterizes how the semantics ranks arguments.
 * 
 * @author Carola Bauer
 */
public class ExtensionPrinciplesExample {
	private static Collection<Principle> all_principles;

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


		//CountingExample_RB_ARG_ABS_STRENGTH();
		//CountingExample_RB_ARG_STRENGTH();
		//CountingExample_RB_ATT_STRENGTH();


		//MaxExample_RB_ARG_ABS_STRENGTH();
		//MaxExample_RB_ARG_STRENGTH();
		//MaxExample_RB_ATT_STRENGTH();


		AlphaBbsExample_RB_ARG_ABS_STRENGTH();
		AlphaBbsExample_RB_ARG_STRENGTH();
		AlphaBbsExample_RB_ATT_STRENGTH();



		//CategorizerExample_RB_ARG_ABS_STRENGTH();
		//CategorizerExample_RB_ARG_STRENGTH();
		//CategorizerExample_RB_ATT_STRENGTH();


	}

	public static void CategorizerExample_RB_ARG_ABS_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ARG_ABS_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.CATEGORIZER));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}

	public static void CategorizerExample_RB_ARG_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ARG_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.CATEGORIZER));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}
	public static void CategorizerExample_RB_ATT_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ATT_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.CATEGORIZER));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}

	public static void CountingExample_RB_ARG_ABS_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ARG_ABS_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.COUNTING));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}

	public static void CountingExample_RB_ARG_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ARG_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.COUNTING));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}
	public static void CountingExample_RB_ATT_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ATT_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.COUNTING));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}

	public static void MaxExample_RB_ARG_ABS_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ARG_ABS_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.MAX));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}

	public static void MaxExample_RB_ARG_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ARG_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.MAX));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}
	public static void MaxExample_RB_ATT_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ATT_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.MAX));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}


	public static void AlphaBbsExample_RB_ARG_ABS_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ARG_ABS_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.BURDEN));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}

	public static void AlphaBbsExample_RB_ARG_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ARG_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.BURDEN));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}
	public static void AlphaBbsExample_RB_ATT_STRENGTH() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new RankingBasedExtensionReasoner(Semantics.RB_ATT_STRENGTH,
						RankingBasedExtensionReasoner.RankingSemantics.BURDEN));
		evaluator.addAllPostulates(all_principles);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}





}
