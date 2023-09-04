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
package org.tweetyproject.arg.rbextensionsemantics.exactreasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.comparator.ExactNumericalPartialOrder;
import org.tweetyproject.math.equation.Equation;
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.opt.problem.OptimizationProblem;
import org.tweetyproject.math.opt.solver.ApacheCommonsSimplex;
import org.tweetyproject.math.term.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * This class implements the argument ranking approach of [Matt, Toni. A
 * game-theoretic measure of argument strength for abstract argumentation. JELIA
 * 2008]. In this approach, the strength of an argument is computed using a
 * two-person zero-sum strategic game in which the strategies of players are
 * sets of arguments.
 * It uses BigDecimal for more precision.
 * 
 * @author Anna Gessler, Carola Bauer
 */
public class ExactStrategyBasedRankingReasoner extends AbstractExactNumericalPartialOrderRankingReasoner {

	@Override
	public Collection<ExactNumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<ExactNumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<ExactNumericalPartialOrder<Argument, DungTheory>>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public ExactNumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
		ExactNumericalPartialOrder<Argument, DungTheory> ranking = new ExactNumericalPartialOrder<Argument, DungTheory>();
		ranking.setSortingType(ExactNumericalPartialOrder.SortingType.DESCENDING);
		Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(((DungTheory)kb).getNodes());
		for (Argument a : ((DungTheory)kb)) 
			ranking.put(a, computeStrengthOfArgument(a, ((DungTheory)kb), subsets)); 
		return ranking;
	}

	public static BigDecimal getMinimalValue() {
		return BigDecimal.valueOf(0.0);
	}

	public static BigDecimal getMaximalValue() {
		return BigDecimal.valueOf(1.);
	}

	/**
	 * Computes the value of the zero-sum game for the given argument.
	 * 
	 * @param a  an Argument
	 * @param kb DungTheory
	 * @param subsets all subsets of the knowledge base
	 * @return strength value of the given argument
	 */
	public BigDecimal computeStrengthOfArgument(Argument a, DungTheory kb, Set<Set<Argument>> subsets) {
		/* 
		 * The value of the game is the solution to a linear optimization problem
		*/
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MAXIMIZE);
		Variable targetVar = new BigDecimalVariable("PMAX");
		problem.setTargetFunction(targetVar);
		
		//Generate strategies of the proponent and opponent of the zero-sum strategic game
		Set<Collection<Argument>> proponentStrategies = new HashSet<Collection<Argument>>();
		Set<Collection<Argument>> opponentStrategies = new HashSet<Collection<Argument>>();
		for (Set<Argument> p : subsets) {
			opponentStrategies.add(p);
			if (p.contains(a)) 
				proponentStrategies.add(p);
		}

		/*
		 * Add linear inequality constraints to problem
		 * See [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation.
		 * JELIA 2008] for details
		 */
		List<BigDecimalVariable> probabilityVariables = new ArrayList<>();
		for (int count = 1; count <= proponentStrategies.size(); count++) { 
			BigDecimalVariable pI = new BigDecimalVariable("P" + count);
			probabilityVariables.add(pI);
			problem.add(new Inequation(pI, new BigDecimalConstant(0.0), Inequation.GREATER_EQUAL));
		}
		problem.add(new Inequation(targetVar, new BigDecimalConstant(0.0), Inequation.GREATER_EQUAL));
		
		for (Collection<Argument> j : opponentStrategies) {
			List<Term> products = new ArrayList<Term>(); 
			int pi = 0;
			for (Collection<Argument> i : proponentStrategies) {
				BigDecimalConstant rewardsIj = new BigDecimalConstant(computeRewards(i, j, kb));
				products.add(new Product(rewardsIj, probabilityVariables.get(pi++)));
			}
			problem.add( new Inequation(new Sum(products).minus(targetVar), new BigDecimalConstant(0.0),
					Inequation.GREATER_EQUAL));
		}
		problem.add(new Equation(new Sum(probabilityVariables), new BigDecimalConstant(1.0)));

		/*
		 * Solve problem with simplex algorithm
		 */
		ApacheCommonsSimplex solver = new ApacheCommonsSimplex();
		solver.onlyPositive = true;
		try {
			Map<Variable, Term> solution = solver.solve(problem);
			return solution.get(targetVar).bigDecimalValue();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(problem);
		}
		return BigDecimal.valueOf(-1.0);
	}

	/**
	 * Computes the rewards of the given strategy (set of arguments).
	 * 
	 * @param A  set of arguments
	 * @param B  set of arguments
	 * @param kb knowledge base containing the relations between A and B
	 * @return rewards of A
	 */
	public BigDecimal computeRewards(Collection<Argument> A, Collection<Argument> B, DungTheory kb) {
		if (kb.isAttacked(new Extension<DungTheory>(A), new Extension<DungTheory>(A)))
			return BigDecimal.valueOf(0.0);
		else if (kb.isAttacked(new Extension<DungTheory>(A), new Extension<DungTheory>(B)))
			return computeDegreeOfAcceptability(A, B, kb);
		return BigDecimal.valueOf(1.0);
	}

	/**
	 * Computes the degree of acceptability of the strategy A wrt. strategy B.
	 * 
	 * @param A  set of arguments
	 * @param B  set of arguments
	 * @param kb knowledge base containing the relations between A and B
	 * @return degree of acceptability of A wrt. B.
	 */
	public BigDecimal computeDegreeOfAcceptability(Collection<Argument> A, Collection<Argument> B, DungTheory kb) {
		int attacksFromAtoB = 0;
		for (Argument b : B) {
			Set<Argument> attacks = kb.getAttackers(b);
			attacks.retainAll(A);
			attacksFromAtoB += attacks.size();
		}
		int attacksFromBtoA = 0;
		for (Argument a : A) {
			Set<Argument> attacks = kb.getAttackers(a);
			attacks.retainAll(B);
			attacksFromBtoA += attacks.size();
		}

		double result = 1.0;
		result += 1.0 - (1.0 / (attacksFromAtoB + 1.0));
		result -= 1.0 - (1.0 / (attacksFromBtoA + 1.0));
		return BigDecimal.valueOf(0.5 * result);
	}
	
	/**natively installed*/
	@Override
	public boolean isInstalled() {
		return true;
	}

}