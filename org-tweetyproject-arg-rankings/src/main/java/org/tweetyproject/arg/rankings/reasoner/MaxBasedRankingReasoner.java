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
package org.tweetyproject.arg.rankings.reasoner;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.NumericalPartialOrder;
import org.tweetyproject.math.matrix.Matrix;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements the argument ranking approach of [Amgoud
 * et al. Acceptability Semantics for Weighted Argumentation
 * Frameworks. 2019]:.
 * <p>
 * This approach ranks arguments iteratively by considering an argument's basic
 * strength as well as the strength of its strongest attacker.
 *
 * @author Carola Bauer
 */

public class MaxBasedRankingReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {


    @Override
    public Collection<NumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
        Collection<NumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<>();
        ranks.add(this.getModel(bbase));
        return ranks;
    }

    @Override
    public NumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
             double distanceOld;
        double distanceNew;

        Matrix directAttackMatrix = kb.getAdjacencyMatrix().transpose(); //The matrix of direct attackers
        int n = directAttackMatrix.getXDimension();
        double[] valuations = new double[n];	 //Stores valuations of the current iteration
        for (int i=0; i<n; i++) {
            valuations[i]=1.;
        }
        double[] valuationsOld; //Stores valuations of the last iteration

        //Keep computing valuations until the values stop changing much or converge
        double epsilon = 0.1;
        do {
            valuationsOld = valuations.clone();
            distanceOld = getDistance(valuationsOld, valuations) / kb.getNumberOfNodes();

            for (int i = 0; i < n; i++)
                valuations[i] = calculateMaxBasedFunction(valuationsOld, directAttackMatrix, i);
            distanceNew = (getDistance(valuationsOld, valuations) / kb.getNumberOfNodes());
        } while (getDistance(valuationsOld, valuations) > epsilon);

        //Use computed valuations as values for argument ranking
        //Note: The order of valuations v[i] is the same as the order of DungTheory.iterator()
        NumericalPartialOrder<Argument, DungTheory> ranking = new NumericalPartialOrder<>();
        ranking.setSortingType(NumericalPartialOrder.SortingType.DESCENDING);
        int i = 0;
        for (Argument a : kb)
            ranking.put(a, valuations[i++]);
        return ranking;
    }

    /**
     * Computes the maxbased function.
     * @param vOld array of double valuations that were computed in the previous iteration
     * @param directAttackMatrix complete matrix of direct attacks
     * @param i row of the attack matrix that will be used in the calculation
     * @return categorizer valuation
     */
    private double calculateMaxBasedFunction(double[] vOld, Matrix directAttackMatrix, int i) {
        double max = 0.;


        for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
            double attacker= vOld[j] * directAttackMatrix.getEntry(i,j).doubleValue();
            if (attacker>max) {
                max = attacker;
            }
        }

        return (vOld[i] / (1.+max));

    }

    /**
     * Computes maxdist between to the given arrays.
     * @param vOld first array
     * @param v second array
     * @return distance between v and vOld
     */
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