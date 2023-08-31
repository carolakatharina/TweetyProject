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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.SAFRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.StrategyBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.TuplesRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.BurdenBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import org.tweetyproject.comparator.LatticePartialOrder;
import org.tweetyproject.comparator.NumericalPartialOrder;

/**
 * Test class for checking counterexamples for some postulates.
 * 
 * @author Anna Gessler
 */
public class RankingReasonerTest {

	public static final int DEFAULT_TIMEOUT = 50000;

	@Test(timeout = DEFAULT_TIMEOUT)
	//Counterexample for QP (and DP) for the Tuples* reasoner
	public void DPandQPCounterexampleTest() throws Exception {
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument a1 = new Argument("a1");
		Argument a2 = new Argument("a2");
		Argument b = new Argument("b");
		Argument b1 = new Argument("b1");
		Argument b2 = new Argument("b2");
		Argument b3 = new Argument("b3");
		Argument b4 = new Argument("b4");
		Argument b5 = new Argument("b5");
		Argument b6 = new Argument("b6");
		Argument b7 = new Argument("b7");
		dt.add(a);
		dt.add(b);
		dt.add(a1);
		dt.add(a2);
		dt.add(b1);
		dt.add(b2);
		dt.add(b3);
		dt.add(b4);
		dt.add(b5);
		dt.add(b6);
		dt.add(b7);
		dt.add(new Attack(a1,a));
		dt.add(new Attack(a2,a));
		dt.add(new Attack(b2,b1));
		dt.add(new Attack(b1,b));
		dt.add(new Attack(b6,b4));
		dt.add(new Attack(b5,b4));
		dt.add(new Attack(b7,b4));
		dt.add(new Attack(b4,b3));
		dt.add(new Attack(b3,b)); 
		
		TuplesRankingReasoner reasonerTuples = new TuplesRankingReasoner();
		LatticePartialOrder<Argument, DungTheory> ranking = reasonerTuples.getModel(dt);
		assertFalse(ranking.isStrictlyMoreAcceptableThan(b, a));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	//Counterexample for DDP for the Categorizer, Tuples, Discussion and SAF reasoner
	public void DDPCounterexampleTest() throws Exception {
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument a1 = new Argument("a1");
		Argument a2 = new Argument("a2");
		Argument a3 = new Argument("a3");
		Argument a4 = new Argument("a4");
		Argument a5 = new Argument("a5");
		Argument a6 = new Argument("a6");
		Argument b = new Argument("b");
		Argument b1 = new Argument("b1");
		Argument b2 = new Argument("b2");
		Argument b3 = new Argument("b3");
		Argument b4 = new Argument("b4");
		dt.add(a);
		dt.add(b);
		dt.add(a1);
		dt.add(a2);
		dt.add(a3);
		dt.add(a4);
		dt.add(a5);
		dt.add(a6);
		dt.add(b1);
		dt.add(b2);
		dt.add(b3);
		dt.add(b4);
		dt.add(new Attack(a6, a5));
		dt.add(new Attack(a5, a4));
		dt.add(new Attack(a4, a));
		dt.add(new Attack(a3, a2));
		dt.add(new Attack(a2, a1));
		dt.add(new Attack(a1, a));
		dt.add(new Attack(b3, b2));
		dt.add(new Attack(b2, b));
		dt.add(new Attack(b4, b2));
		dt.add(new Attack(b1, b));
		
		TuplesRankingReasoner reasonerTuples = new TuplesRankingReasoner();
		SAFRankingReasoner reasonerSaf = new SAFRankingReasoner();
		BurdenBasedRankingReasoner reasonerBurden = new BurdenBasedRankingReasoner();
		DiscussionBasedRankingReasoner reasonerDiscussion = new DiscussionBasedRankingReasoner();
		CategorizerRankingReasoner reasonerCat = new CategorizerRankingReasoner();
		StrategyBasedRankingReasoner reasonerMt = new StrategyBasedRankingReasoner();
		LatticePartialOrder<Argument, DungTheory> ranking = reasonerTuples.getModel(dt);
		NumericalPartialOrder<Argument, DungTheory> ranking2 = reasonerSaf.getModel(dt);
		LatticePartialOrder<Argument, DungTheory> ranking3 = reasonerBurden.getModel(dt);
		LatticePartialOrder<Argument, DungTheory> ranking4 = reasonerDiscussion.getModel(dt);
		NumericalPartialOrder<Argument, DungTheory> ranking5 = reasonerCat.getModel(dt);
//		NumericalArgumentRanking ranking6 = reasonerMt.getModel(dt); //causes timeout
		assertFalse(ranking.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking2.isStrictlyMoreAcceptableThan(a, b));
		assertTrue(ranking3.isStrictlyMoreAcceptableThan(a, b)); //Bbs satisfies DDP
		assertFalse(ranking4.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking5.isStrictlyMoreAcceptableThan(a, b));
//		assertFalse(ranking6.isStrictlyMoreAcceptableThan(a, b));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	//Counterexample for AvsFD for the SAF reasoner
	public void AvsFDCounterexampleTest() throws Exception {
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument a1 = new Argument("a1");
		Argument a2 = new Argument("a2");
		Argument a3 = new Argument("a3");
		Argument a4 = new Argument("a4");
		Argument a5 = new Argument("a5");
		Argument a6 = new Argument("a6");
		Argument b = new Argument("b");
		Argument b1 = new Argument("b1");
		dt.add(a);
		dt.add(b);
		dt.add(a1);
		dt.add(a2);
		dt.add(a3);
		dt.add(a4);
		dt.add(a5);
		dt.add(a6);
		dt.add(b1);
		
		dt.add(new Attack(a4,a1));
		dt.add(new Attack(a1,a));
		dt.add(new Attack(a5,a2));
		dt.add(new Attack(a2,a));
		dt.add(new Attack(a6,a3));
		dt.add(new Attack(a3,a));
		dt.add(new Attack(b1,b));
		
		SAFRankingReasoner reasonerSaf = new SAFRankingReasoner();
		NumericalPartialOrder<Argument, DungTheory> ranking = reasonerSaf.getModel(dt);
		assertFalse(ranking.isStrictlyMoreAcceptableThan(a, b));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	//Test that shows a difference between Bbs and Dbs
	public void DiscussionBurdenExample() throws Exception {
		BurdenBasedRankingReasoner reasonerBurden = new BurdenBasedRankingReasoner();
		DiscussionBasedRankingReasoner reasonerDiscussion = new DiscussionBasedRankingReasoner();
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument g = new Argument("g");
		Argument h = new Argument("h");
		Argument i = new Argument("i");
		Argument j = new Argument("j");
		Argument k = new Argument("k");
		Argument l = new Argument("l");
		dt.add(a);
		dt.add(b);
		dt.add(c);
		dt.add(d);
		dt.add(e);
		dt.add(g);
		dt.add(h);
		dt.add(i);
		dt.add(j);
		dt.add(k);
		dt.add(l);
		dt.add(new Attack(h,c));
		dt.add(new Attack(c,a));
		dt.add(new Attack(g,d));
		dt.add(new Attack(d,a));
		dt.add(new Attack(l,h));
		dt.add(new Attack(e,j));
		dt.add(new Attack(k,j));
		dt.add(new Attack(j,b));
		dt.add(new Attack(i,b));
	
		LatticePartialOrder<Argument, DungTheory> ranking_burden = reasonerBurden.getModel(dt);
		LatticePartialOrder<Argument, DungTheory> ranking_discussion = reasonerDiscussion.getModel(dt);
		assertTrue(ranking_burden.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking_discussion.isStrictlyMoreAcceptableThan(a,b));
	}
	
}
