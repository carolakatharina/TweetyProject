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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.rankings.postulates.RankingPostulate;
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
public class RankingPostulatesWeightedExample {
	private static Collection<RankingPostulate> all_postulates;

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
		
		 

		WeightedCategorizerExample();

	}

	public static void WeightedCategorizerExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<>(dg,
				new WeightedCategorizerRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, true).prettyPrint());

	}


}
