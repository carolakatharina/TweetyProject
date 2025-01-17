package org.tweetyproject.arg.rbextensionsemantics.exactreasoner;

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
import org.tweetyproject.comparator.ExactNumericalPartialOrder;
import org.tweetyproject.math.matrix.Matrix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashSet;


/**
 * This class implements the argument ranking approach of [Gabbay, D. M., and Rodrigues, O. 2015. Equilibrium states
 * in numerical argumentation networks, 2015]
 * <p>
 * It uses BigDecimal for more precision.
 *
 * @author Carola Bauer
 */
public class ExactIterativeSchemaRankingReasoner extends AbstractExactNumericalPartialOrderRankingReasoner {

    private final BigDecimal epsilon;

    public ExactIterativeSchemaRankingReasoner(BigDecimal epsilon) {
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

        Matrix directAttackMatrix = kb.getAdjacencyMatrix().transpose(); //The matrix of direct attackers
        int n = directAttackMatrix.getXDimension();
        BigDecimal[] valuations = new BigDecimal[n];	 //Stores valuations of the current iteration
        for (int i=0; i<n; i++) {
            valuations[i]= BigDecimal.valueOf(1.);
        }
        BigDecimal[] valuationsOld; //Stores valuations of the last iteration
        var distance = BigDecimal.valueOf(0.);

        //Keep computing valuations until the values stop changing
        do {
            valuationsOld = valuations.clone();

            for (int i = 0; i < n; i++)
                valuations[i] = calculateFunction(valuationsOld, directAttackMatrix, i);
            distance = getDistance(valuationsOld, valuations);
        } while (distance.compareTo(epsilon)>0);


        //Use computed valuations as values for argument ranking
        //Note: The order of valuations v[i] is the same as the order of DungTheory.iterator()
        ExactNumericalPartialOrder<Argument, DungTheory> ranking = new ExactNumericalPartialOrder<>();
        ranking.setSortingType(ExactNumericalPartialOrder.SortingType.DESCENDING);
        int i = 0;
        for (Argument a : kb)
            ranking.put(a, valuations[i++]);

        return ranking;
    }

    public static BigDecimal getMinimalValue() {
        return BigDecimal.valueOf(0.0001);
    }

    public static BigDecimal getMaximalValue() {
        return BigDecimal.valueOf(1.);
    }

    /**
     * Computes the weightfunction.
     * @param vOld array of BigDecimal valuations that were computed in the previous iteration
     * @param directAttackMatrix complete matrix of direct attacks
     * @param i row of the attack matrix that will be used in the calculation
     * @return value
     */
    private BigDecimal calculateFunction(BigDecimal[] vOld, Matrix directAttackMatrix, int i) {
        BigDecimal max = BigDecimal.valueOf(0.);

        for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
            BigDecimal attacker= vOld[j].multiply(directAttackMatrix.getEntry(i,j).bigDecimalValue(), MathContext.DECIMAL32);
            if (attacker.compareTo(max)>0) {
                max = attacker;
            }
        }
        var var1 = BigDecimal.valueOf(1.).subtract(vOld[i]);
        var var2 = BigDecimal.valueOf(1.).subtract(max);


        return (var1.multiply(var2.min(BigDecimal.valueOf(0.5)))
                .add(vOld[i].multiply(var2.max(BigDecimal.valueOf(0.5))), MathContext.DECIMAL32));

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
            sum = sum.add(distance.pow(2), MathContext.DECIMAL32);
        }

        return sum.sqrt(MathContext.DECIMAL32);
    }

    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }


}