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
import org.tweetyproject.comparator.ExactNumericalPartialOrder;
import org.tweetyproject.math.matrix.Matrix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements the "alpha-Bbs" argument ranking approach that was
 * originally proposed by [Amgoud et.al., Ranking arguments with compensation-based semantics, 2016.]
 * for deductive logics.
 *
 * @author Carola Bauer
 */
public class ExactAlphaBurdenBasedRankingReasoner extends AbstractRankingReasoner<ExactNumericalPartialOrder<Argument, DungTheory>> {

    private final BigDecimal epsilon;
    private final BigDecimal alpha;


    /**
     * Create a new AlphaBurdenBasedRankingReasoner with the given
     * parameters.
     *
     * @param epsilon
     * @param alpha   TODO add description
     */
    public ExactAlphaBurdenBasedRankingReasoner(BigDecimal epsilon, BigDecimal alpha) {
        this.epsilon = epsilon;
        this.alpha = alpha;
    }

    @Override
    public Collection<ExactNumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
        Collection<ExactNumericalPartialOrder<Argument, DungTheory>> ranks
                = new HashSet<ExactNumericalPartialOrder<Argument, DungTheory>>();
        ranks.add(this.getModel(bbase));
        return ranks;
    }

    @Override
    public ExactNumericalPartialOrder<Argument, DungTheory> getModel(DungTheory base) {
        Matrix directAttackMatrix = ((DungTheory) base).getAdjacencyMatrix().transpose(); //The matrix of direct attackers
        int n = directAttackMatrix.getXDimension();
        BigDecimal valuations[] = new BigDecimal[n];     //Stores valuations of the current iteration
        BigDecimal valuationsOld[] = new BigDecimal[n]; //Stores valuations of the last iteration

        //Keep computing valuations until the values stop changing much or converge
        do {
            valuationsOld = valuations.clone();
            for (int i = 0; i < n; i++) {
                valuations[i] = calculateBurdenBasedFunction(valuationsOld, directAttackMatrix, i);

            }

        } while (getDistance(valuationsOld, valuations).compareTo(epsilon) > 0);

        //Use computed valuations as values for argument ranking
        //Note: The order of valuations v[i] is the same as the order of DungTheory.iterator()
        ExactNumericalPartialOrder<Argument, DungTheory> ranking = new ExactNumericalPartialOrder<Argument, DungTheory>();
        ranking.setSortingType(ExactNumericalPartialOrder.SortingType.DESCENDING);
        int i = 0;
        for (Argument a : ((DungTheory) base)) {

            ranking.put(a, (BigDecimal.valueOf(1.0).divide(valuations[i], MathContext.DECIMAL128)));

            i++;
        }

        return ranking;
    }

    /**
     * Computes the alpha-burdenbased function.
     *
     * @param vOld               array of BigDecimal valuations that were computed in the previous iteration
     * @param directAttackMatrix complete matrix of direct attacks
     * @param i                  row of the attack matrix that will be used in the calculation
     * @return categorizer valuation
     */
    private BigDecimal calculateBurdenBasedFunction(BigDecimal[] vOld, Matrix directAttackMatrix, int i) {
        BigDecimal c = BigDecimal.valueOf(0.0);
        if (directAttackMatrix.getXDimension() == 0) {
            return BigDecimal.valueOf(1.0);
        }
        for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
            var mult = vOld[j].multiply(directAttackMatrix.getEntry(i, j).bigDecimalValue());
            var zaehler = BigDecimal.valueOf(Math.pow(mult.doubleValue(), alpha.doubleValue()));
            if (zaehler.compareTo(BigDecimal.valueOf(0.)) != 1) {
                c = c.add(BigDecimal.valueOf(1.0).divide(zaehler, MathContext.DECIMAL128));
            }
        }
        return (BigDecimal.valueOf(1.0).add(BigDecimal.valueOf(Math.pow(c.doubleValue(), (BigDecimal.valueOf(1.0).divide(alpha, MathContext.DECIMAL128).doubleValue())))));

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
            sum = sum.add(distance.pow(2));
        }

        BigDecimal result = sum.sqrt(MathContext.DECIMAL128);
        return result;
    }


    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }

}