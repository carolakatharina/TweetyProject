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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashSet;

import static java.lang.StrictMath.exp;

/**
 * This class implements the argument ranking approach of [Amgoud, Doder.
 * Gradual Semantics Accounting for Varied-Strength
 * Attacks. 2019]:.
 * <p>
 * This approach ranks arguments iteratively by considering an argument's basic
 * strength as well as the strength of its strongest attacker.
 * It uses BigDecimal for more precision.
 *
 * @author Carola Bauer
 */
public class ExactEulerMaxBasedRankingReasoner extends AbstractExactNumericalPartialOrderRankingReasoner {

    final BigDecimal epsilon;

    public ExactEulerMaxBasedRankingReasoner(BigDecimal epsilon) {
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

        //Keep computing valuations until the values stop changing much or converge
        do {
            valuationsOld = valuations.clone();

            for (int i = 0; i < n; i++)
                valuations[i] = calculateEulerMaxBasedFunction(valuationsOld, directAttackMatrix, i);
        } while (getDistance(valuationsOld, valuations).compareTo(epsilon)>0);

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
        return BigDecimal.valueOf(0.);
    }

    public static BigDecimal getMaximalValue() {
        return BigDecimal.valueOf(1.);
    }

    /**
     * Computes the maxbased function.
     * @param vOld array of BigDecimal valuations that were computed in the previous iteration
     * @param directAttackMatrix complete matrix of direct attacks
     * @param i row of the attack matrix that will be used in the calculation
     * @return categorizer valuation
     */
    private BigDecimal calculateEulerMaxBasedFunction(BigDecimal[] vOld, Matrix directAttackMatrix, int i) {
        BigDecimal max = BigDecimal.valueOf(0.);

        for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
            BigDecimal attacker= vOld[j].multiply(directAttackMatrix.getEntry(i,j).bigDecimalValue(), MathContext.DECIMAL128);
            if (attacker.compareTo(max)>0) {
                max = attacker;
            }
        }
        return  BigDecimal.valueOf(1.).multiply(BigDecimal.valueOf(exp(-max.doubleValue())), MathContext.DECIMAL128);

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

        return sum.sqrt(MathContext.DECIMAL64);
    }
    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }


}