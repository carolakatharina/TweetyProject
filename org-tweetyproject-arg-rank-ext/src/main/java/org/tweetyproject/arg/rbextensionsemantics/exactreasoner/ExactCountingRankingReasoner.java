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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.ExactNumericalPartialOrder;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.term.BigDecimalConstant;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements the argument ranking approach of [Pu, Zhang, G.Luo,
 * J.Luo. Attacker and Defender Counting Approach for Abstract Argumentation.
 * CoRR 2015].
 * This approach ranks arguments by counting the number of their attackers and
 * defenders in form of a dialogue game where proponents are defenders and
 * opponents are attackers.
 * It was adapted to use BigDecimal, for more precision.
 * 
 * @author Anna Gessler, Carola Bauer
 */
public class ExactCountingRankingReasoner extends AbstractExactNumericalPartialOrderRankingReasoner {

	/**
	 * This parameter influences whether shorter/longer attackers/defender lines are
	 * preferred. As shown in [Pu, G.Luo, J.Luo. Some Supplementaries to The Counting Semantics 
	 * for Abstract Argumentation. CoRR 2015], for most applications it is best to choose a 
	 * value in [0.9, 0.98]
	 */
	BigDecimal dampingFactor;

	/**
	 * The algorithm terminates when the change between two iterations is below this
	 * tolerance parameter.
	 */
	final BigDecimal epsilon;


	/**
	 * Create a new CountingRankingReasoner with the given parameters.
	 * 
	 * @param damping_factor must be in (0,1)
	 * @param epsilon parameter that determined number of iterations
	 */
	public ExactCountingRankingReasoner(BigDecimal damping_factor, BigDecimal epsilon) {
		this.dampingFactor = damping_factor;
		this.epsilon = epsilon;
	}


	@Override
	public Collection<ExactNumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<ExactNumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public ExactNumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
		Matrix adjacencyMatrix = kb.getAdjacencyMatrix();

		var normfactor = getInfiniteNormalizationFactor(adjacencyMatrix);

		if (normfactor.compareTo(BigDecimal.ZERO)!=0) {
		// Apply matrix norm to guarantee that the argument strength scale is bounded
		adjacencyMatrix = adjacencyMatrix.mult(BigDecimal.valueOf(1.0).divide(normfactor,  MathContext.DECIMAL128));
		}

		// Apply damping factor
		adjacencyMatrix = adjacencyMatrix.mult(this.dampingFactor).simplify();
		
		int n = kb.getNumberOfNodes();
		Matrix valuations = new Matrix(1, n); // Stores values of the current iteration
		Matrix valuationsOld = new Matrix(1, n); // Stores values of the last iteration
		
		Matrix e = new Matrix(1, n); // column vector of all ones
		for (int i = 0; i < n; i++) {
			e.setEntry(0, i, new BigDecimalConstant(BigDecimal.valueOf(1.0)));
		}
		// the ranking for step 0 is 1.0 for all arguments
		valuations = e; 
		
		do {
			valuationsOld = valuations;
			valuations = e.minus(adjacencyMatrix.mult(valuationsOld)).simplify();
		} while (getDistance(valuationsOld, valuations).compareTo(epsilon)>0);
		
		ExactNumericalPartialOrder<Argument, DungTheory> ranking = new ExactNumericalPartialOrder<Argument, DungTheory>();
		ranking.setSortingType(ExactNumericalPartialOrder.SortingType.DESCENDING);
		int i = 0;
		for (Argument a : ((DungTheory)kb)) 
			ranking.put(a, valuations.getEntry(0, i++).bigDecimalValue());

		return ranking;
	}

	public static BigDecimal getMinimalValue() {
		return BigDecimal.valueOf(0.);
	}

	public static BigDecimal getMaximalValue() {
		return BigDecimal.valueOf(1.);
	}

	/**
	 * Calculates the infinite matrix norm of the given matrix (i.e. the maximum
	 * absolute row sum).
	 * 
	 * @param matrix the given matrix
	 * @return infinite matrix norm of the matrix
	 */
	private BigDecimal getInfiniteNormalizationFactor(Matrix matrix) {
		BigDecimal maxSum = BigDecimal.valueOf(0.0);
		for (int y = 0; y < matrix.getXDimension(); y++) {
			BigDecimal sum = BigDecimal.valueOf(0.0);
			for (int x = 0; x < matrix.getYDimension(); x++) {
				sum = sum.add(matrix.getEntry(x, y).bigDecimalValue());

			}

			if (sum.compareTo(maxSum)>0) {
				maxSum = sum;

			}
		}
		return maxSum;
	}


	/**
	 * Computes the Euclidean distance between to the given column vectors.
	 *
	 * @param vOld first column vector
	 * @param v     second column vector
	 * @return distance between v and v_old
	 */
	private BigDecimal getDistance(Matrix vOld, Matrix v) {
		BigDecimal sum = BigDecimal.valueOf(0.0);
		for (int i = 0; i < v.getYDimension(); i++) {
			var val = v.getEntry(0, i).bigDecimalValue().subtract(vOld.getEntry(0, i).bigDecimalValue());
			sum = sum.add(val.pow(2), MathContext.DECIMAL128);
		}
		return sum.sqrt(MathContext.DECIMAL128);
	}

	
	/**natively installed*/
	@Override
	public boolean isInstalled() {
		return true;
	}

}