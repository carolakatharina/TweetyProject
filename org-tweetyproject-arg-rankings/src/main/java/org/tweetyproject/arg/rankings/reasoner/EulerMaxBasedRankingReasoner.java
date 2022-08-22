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
import java.util.Set;

import static java.lang.StrictMath.exp;

/**
 * This class implements the argument ranking approach of [Amgoud, Doder.
 * Gradual Semantics Accounting for Varied-Strength
 * Attacks. 2019]:.
 * <p>
 * This approach ranks arguments iteratively by considering an argument's basic
 * strength as well as the strength of its strongest attacker.
 *
 * @author Carola Bauer
 */
public class EulerMaxBasedRankingReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {

    private double epsilon=0.5;
    //TODO: überprüfem


    @Override
    public Collection<NumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
        Collection<NumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<>();
        ranks.add(this.getModel(bbase));
        return ranks;
    }

    @Override
    public NumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
        NumericalPartialOrder<Argument, DungTheory> ranking = new NumericalPartialOrder<Argument, DungTheory>();
        ranking.setSortingType(NumericalPartialOrder.SortingType.DESCENDING);

        WeightedDungTheoryWithSelfWeight valuations = new WeightedDungTheoryWithSelfWeight(kb, 1.0); // Stores values of the current iteration
        WeightedDungTheoryWithSelfWeight valuationsOld = new WeightedDungTheoryWithSelfWeight(kb, 1.0); // Stores values of the current iteration

        double distanceOld;
        double distanceNew;
        do {
            distanceOld = getDistance(valuationsOld.getWeights(), valuations.getWeights());

            for (var argument : valuations) {
                setArgumentWeight(valuations, valuationsOld, argument);
            }
            distanceNew = getDistance(valuationsOld.getWeights(), valuations.getWeights());

        }while(Math.abs(distanceOld-distanceNew)>epsilon);

        for (Argument arg : (valuations)) {
            ranking.put(arg, valuations.getWeight(arg));
        }
        return ranking;
        }



    private void setArgumentWeight(WeightedDungTheoryWithSelfWeight valuations,
                                   WeightedDungTheoryWithSelfWeight valuationsOld, Argument argument) {
        var attackers = valuationsOld.getAttackers(argument);
        if (attackers.size() == 0) {
            //donothing

        } else {
            Argument max = getMax(valuationsOld, attackers);
            var newValue = getNewWeight(valuationsOld.getWeight(argument),
                    valuationsOld.getWeight(max));
            valuationsOld.setWeight(argument, valuations.getWeight(argument));
            valuations.setWeight(argument, newValue);

        }
    }

    private Argument getMax(WeightedDungTheoryWithSelfWeight valuations, Set<Argument> attackers) {
        Argument max = attackers.iterator().next();
        //find the strongest attacker of argument
        for (var att : attackers) {
            if (valuations.getWeight(att) > valuations.getWeight(max)) {
                max = att;
            }
        }
        return max;
    }


    /**
     * Calculates the new weight.
     *
     * @return new weight of the argument
     */
    private static double getNewWeight(Double arg, Double max) {

        return arg * exp(-max);
    }


    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
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
        System.out.println("Distanz"+Math.sqrt(sum));

        return Math.sqrt(sum);
    }

}