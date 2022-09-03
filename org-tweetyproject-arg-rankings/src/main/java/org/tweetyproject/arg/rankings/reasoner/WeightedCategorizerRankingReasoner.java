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

import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements the argument ranking approach of [Besnard, Hunter. A logic-based theory of deductive arguments. 2001]:.
 * <p>
 * This approach ranks arguments iteratively by considering an argument's basic
 * strength as well as the strength of all its attackers.
 *
 * @author Carola Bauer
 */

public class WeightedCategorizerRankingReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {

    private double epsilon=0.0001;


    @Override
    public Collection<NumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
        Collection<NumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<NumericalPartialOrder<Argument, DungTheory>>();
        ranks.add(this.getModel(bbase));
        return ranks;
    }

    @Override
    public NumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
        NumericalPartialOrder<Argument, DungTheory> ranking = new NumericalPartialOrder<Argument, DungTheory>();
        ranking.setSortingType(NumericalPartialOrder.SortingType.DESCENDING);

        WeightedDungTheoryWithSelfWeight valuations = new WeightedDungTheoryWithSelfWeight(kb, 1.0); // Stores values of the current iteration
        WeightedDungTheoryWithSelfWeight valuationsOld = new WeightedDungTheoryWithSelfWeight(kb, 1.0); // Stores values of the previous iteration
       double distanceOld;
       double distanceNew;
        do {
            distanceOld = getDistance(valuationsOld.getWeights(), valuations.getWeights())/ valuations.getNumberOfNodes();
            for (var argument : valuations) {
                setArgumentWeight(valuations, valuationsOld, argument);
            }
            distanceNew = getDistance(valuationsOld.getWeights(), valuations.getWeights())/valuations.getNumberOfNodes();
        }while(Math.abs(distanceNew-distanceOld)>epsilon);

        for (Argument arg : (valuations)) {
            ranking.put(arg, valuations.getWeight(arg));
        }
        return ranking;
    }

    private void setArgumentWeight(WeightedDungTheoryWithSelfWeight valuations, WeightedDungTheoryWithSelfWeight valuationsOld,
                                   Argument argument) {
        var attackers = valuationsOld.getAttackers(argument);
        if (attackers.size() == 0) {
            //donothing

        } else {
            double sumAttacks = 0;
            for (var att : attackers) {
                sumAttacks = sumAttacks + valuationsOld.getWeight(att);
            }
            var newWeight = getNewWeight(valuationsOld.getWeight(argument), sumAttacks);
            valuationsOld.setWeight(argument, valuations.getWeight(argument));

            valuations.setWeight(argument, newWeight);


        }
    }


    /**
     * Calculates the new weight.
     *
     * @return new weight of the argument
     */
    private double getNewWeight(Double arg, Double sumAttacks) {

        return arg / (1 + sumAttacks);
    }


    /**
     * Computes the Euclidean distance between to the given arrays.
     * @param vOld first array
     * @param v second array
     * @return distance between v and vOld
     */
    private double getDistance(Double[] vOld, Double[] v) {
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