package org.tweetyproject.arg.rankings.reasoner;

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


import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.NumericalPartialOrder;
import org.tweetyproject.math.matrix.Matrix;

import java.util.Collection;
import java.util.HashSet;


/**
 * This class implements the argument ranking approach of [Gabbay, D. M., and Rodrigues, O. 2015. Equilibrium states
 * in numerical argumentation networks, 2015]
 * <p>
 *
 *
 * @author Carola Bauer
 */
public class IterativeSchemaRankingReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {

    private double epsilon=0.001;
    @Override
    public Collection<NumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
        Collection<NumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<>();
        ranks.add(this.getModel(bbase));
        return ranks;
    }

    @Override
    public NumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {

        Matrix directAttackMatrix = kb.getAdjacencyMatrix().transpose(); //The matrix of direct attackers
        int n = directAttackMatrix.getXDimension();
        double[] valuations = new double[n];	 //Stores valuations of the current iteration
        for (int i=0; i<n; i++) {
            valuations[i]=1.;
        }
        double[] valuationsOld; //Stores valuations of the last iteration

        //Keep computing valuations until the values stop changing
        do {
            valuationsOld = valuations.clone();

            for (int i = 0; i < n; i++)
                valuations[i] = calculateFunction(valuationsOld, directAttackMatrix, i);
        } while (getDistance(valuationsOld, valuations) > this.epsilon);


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
     * Computes the weightfunction.
     * @param vOld array of double valuations that were computed in the previous iteration
     * @param directAttackMatrix complete matrix of direct attacks
     * @param i row of the attack matrix that will be used in the calculation
     * @return value
     */
    private double calculateFunction(double[] vOld, Matrix directAttackMatrix, int i) {
        double max = 0.;

        for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
            double attacker= vOld[j] * directAttackMatrix.getEntry(i,j).doubleValue();
            if (attacker>max) {
                max = attacker;
            }
        }


        return ((1-vOld[i])*Math.min(0.5, (1. - max))+Math.max(0.5, (1. - max)));

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


    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }


}