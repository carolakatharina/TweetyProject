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
package org.tweetyproject.arg.rankings.reasoner;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.NumericalPartialOrder;
import org.tweetyproject.math.matrix.Matrix;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements the "h-categorizer" argument ranking approach that was 
 * originally proposed by [Besnard, Hunter. A logic-based theory of deductive arguments. 2001]
 * for deductive logics. It uses the Fixed-point algorithm of 
 * [Pu, Zhang, Luo, Luo. Argument Ranking with Categoriser Function. KSEM 2014]
 * which allows for cycles in argumentation graphs. TODO: EXception for self-attacking Arguments
 * 
 * @see org.tweetyproject.arg.deductive.categorizer.HCategorizer
 * 
 * @author Carola Bauer
 */
public class NsaReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {

	private double epsilon;

	/**
	 * Create a new CountingRankingReasoner with default
	 * parameters.
	 */
	public NsaReasoner() {
		this.epsilon = 0.001;
	}

	/**
	 * Create a new CategorizerRankingReasoner with the given
	 * parameters.
	 *
	 * @param epsilon TODO add description
	 */
	public NsaReasoner(double epsilon) {
		this.epsilon = epsilon;
	}
	
	@Override
	public Collection<NumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<NumericalPartialOrder<Argument, DungTheory>> ranks 
			= new HashSet<NumericalPartialOrder<Argument, DungTheory>>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public NumericalPartialOrder<Argument, DungTheory> getModel(DungTheory base) {
		Matrix directAttackMatrix = ((DungTheory)base).getAdjacencyMatrix().transpose(); //The matrix of direct attackers
		int n = directAttackMatrix.getXDimension();
		double valuations[] = new double[n];	 //Stores valuations of the current iteration
		double valuationsOld[] = new double[n]; //Stores valuations of the last iteration
		
		//Keep computing valuations until the values stop changing much or converge 
		do {
			valuationsOld = valuations.clone();
			for (int i = 0; i < n; i++) 
				valuations[i] = calculateCategorizerFunction(valuationsOld,directAttackMatrix,i);
		} while (getDistance(valuationsOld, valuations) > this.epsilon);
	
		//Use computed valuations as values for argument ranking
		//Note: The order of valuations v[i] is the same as the order of DungTheory.iterator()
		NumericalPartialOrder<Argument, DungTheory> ranking = new NumericalPartialOrder<Argument, DungTheory>();
		ranking.setSortingType(NumericalPartialOrder.SortingType.DESCENDING);
		int i = 0;
		for (Argument a : ((DungTheory)base)) {
			if (directAttackMatrix.getEntry(i,i).doubleValue()!=0.){
				ranking.put(a, 0.);
				i++;
			} else {
				ranking.put(a, valuations[i++]);
			}
		}

		return ranking;
	}

	/**
	 * Computes the h-Categorizer function.
	 * @param vOld array of double valuations that were computed in the previous iteration
	 * @param directAttackMatrix complete matrix of direct attacks
	 * @param i row of the attack matrix that will be used in the calculation
	 * @return categorizer valuation
	 */
	private double calculateCategorizerFunction(double[] vOld, Matrix directAttackMatrix, int i) {
		double c = 1.0;


		for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
			c += vOld[j] * directAttackMatrix.getEntry(i,j).doubleValue();
		}
		return (1.0 / c);
		
	}

	/**
	 * Computes the Euclidean distance between to the given arrays.
	 * @param vOld first array
	 * @param v second array
	 * @return distance between v and vOld
	 */
	private double getDistance(double[] vOld, double[] v) {
		double sum = 0.0;
		for (int i = 0; i < v.length; i++) {
			sum += Math.pow(v[i]-vOld[i],2.0);
		}
		return Math.sqrt(sum);
	}
	
	/**natively installed*/
	@Override
	public boolean isInstalled() {
		return true;
	}

}
