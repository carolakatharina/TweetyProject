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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rbextensionsemantics.exactreasoner;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.ExactNumericalPartialOrder;
import org.tweetyproject.math.matrix.Matrix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements the no self-attacked"h-categorizer" argument ranking approach that was
 * proposed by [Vivien Beuselinck, Jérôme Delobelle, Srdjan Vesic.
 * On Restricting the Impact of Self-Attacking Arguments
 * in Gradual Semantics. 2021].
 * It uses the Fixed-point algorithm of
 * [Pu, Zhang, Luo, Luo. Argument Ranking with Categoriser Function. KSEM 2014]
 * which allows for cycles in argumentation graphs.
 * It uses BigDecimal for more precision.
 *
 * @author Carola Bauer
 */
public class ExactNsaReasoner extends AbstractExactNumericalPartialOrderRankingReasoner {

	private final BigDecimal epsilon;

	/**
	 * Create a new CountingRankingReasoner with default
	 * parameters.
	 */
	public ExactNsaReasoner() {
		this.epsilon = BigDecimal.valueOf(0.001);
	}

	/**
	 * Create a new CategorizerRankingReasoner with the given
	 * parameters.
	 *
	 * @param epsilon determines number of iterations
	 */
	public ExactNsaReasoner(BigDecimal epsilon) {
		this.epsilon = epsilon;
	}
	
	@Override
	public Collection<ExactNumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<ExactNumericalPartialOrder<Argument, DungTheory>> ranks 
			= new HashSet<>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public ExactNumericalPartialOrder<Argument, DungTheory> getModel(DungTheory base) {
		Matrix directAttackMatrix = base.getAdjacencyMatrix().transpose(); //The matrix of direct attackers
		int n = directAttackMatrix.getXDimension();
		BigDecimal[] valuations = new BigDecimal[n];	 //Stores valuations of the current iteration
		BigDecimal[] valuationsOld; //Stores valuations of the last iteration

		for (int i=0; i<n; i++) {
			if (directAttackMatrix.getEntry(i,i).doubleValue()>0.) {
				valuations[i] =BigDecimal.valueOf(0.);
			} else {
				valuations[i] = BigDecimal.valueOf(1.);
			}
		}

		//Keep computing valuations until the values stop changing much or converge
		do {
			valuationsOld = valuations.clone();
			for (int i = 0; i < n; i++) 
				valuations[i] = calculateCategorizerFunction(valuationsOld,directAttackMatrix,i);
		} while (getDistance(valuationsOld, valuations).compareTo(this.epsilon)>0.);
	
		//Use computed valuations as values for argument ranking
		//Note: The order of valuations v[i] is the same as the order of DungTheory.iterator()
		ExactNumericalPartialOrder<Argument, DungTheory> ranking = new ExactNumericalPartialOrder<>();
		ranking.setSortingType(ExactNumericalPartialOrder.SortingType.DESCENDING);
		int i = 0;
		for (Argument a : base) {
				ranking.put(a, valuations[i++]);
		}

		return ranking;
	}

	public static BigDecimal getMinimalValue() {

		return BigDecimal.valueOf(0.0);
	}

	public static BigDecimal getMaximalValue() {
		return BigDecimal.valueOf(1.);
	}

	/**
	 * Computes the h-Categorizer function.
	 * @param vOld array of BigDecimal valuations that were computed in the previous iteration
	 * @param directAttackMatrix complete matrix of direct attacks
	 * @param i row of the attack matrix that will be used in the calculation
	 * @return categorizer valuation
	 */
	private BigDecimal calculateCategorizerFunction(BigDecimal[] vOld, Matrix directAttackMatrix, int i) {
		BigDecimal c = BigDecimal.valueOf(1.0);

		if (directAttackMatrix.getEntry(i,i).doubleValue()>0.) {
			return BigDecimal.valueOf(0.);
		}

		if (directAttackMatrix.getXDimension()==1 &&
				directAttackMatrix.getEntry(i, 0).doubleValue()==0.) {
			return BigDecimal.valueOf(vOld[i].doubleValue());
		}

		for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
				c = c.add(vOld[j].multiply(BigDecimal.valueOf(directAttackMatrix.getEntry(i, j).doubleValue()), MathContext.DECIMAL128));

		}


		return (BigDecimal.valueOf(1.0).divide(c, MathContext.DECIMAL128));

	}

	/**
	 * Computes the Euclidean distance between to the given arrays.
	 *
	 * @param vOld first array
	 * @param v    second array
	 * @return distance between v and vOld
	 */
	private BigDecimal getDistance(BigDecimal[] vOld, BigDecimal[] v) {
		var sum = BigDecimal.valueOf(0.0);
		for (int i = 0; i < v.length; i++) {
			var distance = v[i].subtract(vOld[i]);
			sum = sum.add(distance.pow(2), MathContext.DECIMAL128);
		}

		return sum.sqrt(MathContext.DECIMAL128);
	}


	/**natively installed*/
	@Override
	public boolean isInstalled() {
		return true;
	}

}
