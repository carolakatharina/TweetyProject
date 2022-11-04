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

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.NumericalPartialOrder;
import org.tweetyproject.math.matrix.Matrix;

/**
 * This class implements the "alpha-Bbs" argument ranking approach that was
 * originally proposed by [Amgoud et.al., Ranking arguments with compensation-based semantics, 2016.]
 * for deductive logics.
 *
 * @author Carola Bauer
 */
public class AlphaBurdenBasedRankingReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {

    private double epsilon;
    private double alpha;

    /**
     * Create a new CountingRankingReasoner with default
     * parameters.
     */
    public AlphaBurdenBasedRankingReasoner() {
        this.epsilon = 0.1;
        this.alpha = 0.5;
    }

    /**
     * Create a new AlphaBurdenBasedRankingReasoner with the given
     * parameters.
     *
     * @param epsilon TODO add description
     * @param alpha   TODO add description
     */
    public AlphaBurdenBasedRankingReasoner(double epsilon, double alpha) {
        this.epsilon = epsilon;
        this.alpha = alpha;
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
        Matrix directAttackMatrix = ((DungTheory) base).getAdjacencyMatrix().transpose(); //The matrix of direct attackers
        int n = directAttackMatrix.getXDimension();
        double valuations[] = new double[n];     //Stores valuations of the current iteration
        double valuationsOld[] = new double[n]; //Stores valuations of the last iteration

        //Keep computing valuations until the values stop changing much or converge
        do {
            valuationsOld = valuations.clone();
            for (int i = 0; i < n; i++) {
				valuations[i] = calculateBurdenBasedFunction(valuationsOld, directAttackMatrix, i);

			}

		} while (getDistance(valuationsOld, valuations) > this.epsilon);

        //Use computed valuations as values for argument ranking
        //Note: The order of valuations v[i] is the same as the order of DungTheory.iterator()
        NumericalPartialOrder<Argument, DungTheory> ranking = new NumericalPartialOrder<Argument, DungTheory>();
        ranking.setSortingType(NumericalPartialOrder.SortingType.DESCENDING);
        int i = 0;
        for (Argument a : ((DungTheory) base)) {
			if (valuations[i]==0.0) {
				ranking.put(a, 0.0);
			} else {
				ranking.put(a, (1.0 / valuations[i]));
			}
			i++;
        }

        return ranking;
    }

    /**
     * Computes the alpha-burdenbased function.
     *
     * @param vOld               array of double valuations that were computed in the previous iteration
     * @param directAttackMatrix complete matrix of direct attacks
     * @param i                  row of the attack matrix that will be used in the calculation
     * @return categorizer valuation
     */
    private double calculateBurdenBasedFunction(double[] vOld, Matrix directAttackMatrix, int i) {
        double c = 0.0;
        if (directAttackMatrix.getXDimension() == 0) {
            return 1.0;
        }
        for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
            var zaehler = Math.pow(vOld[j] * directAttackMatrix.getEntry(i, j).doubleValue(), alpha);
            if (zaehler!=0) {
                c += (1.0 / zaehler);
            }
        }
        return (1.0 + Math.pow(c, (1.0 / alpha)));

    }


    private double getDistance(double[] vOld, double[] v) {
        double maxdist = 0.0;
        for (int i = 0; i < v.length; i++) {
            var dist= Math.pow(v[i]-vOld[i],2.0);
            if (dist> maxdist) {
                maxdist=dist;
            }
        }
        return Math.sqrt(maxdist);
    }

    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }

}