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

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.NumericalPartialOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the argument ranking approach of [Bluemel, Thimm: A Ranking Semantics for Abstract
 * Argumentation based on Serialisability]:.
 * <p>
 * This approach ranks arguments by considering if they are part of an initial set.
 *
 * @author Carola Bauer
 */
public class SerialisabilityRankingReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {



    private final Semantics semantics;
    public SerialisabilityRankingReasoner(Semantics s)

    {
        this.semantics = s;
    }

    @Override
    public Collection<NumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
        Collection<NumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<>();
        ranks.add(this.getModel(bbase));
        return ranks;
    }

    @Override
    public NumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
        NumericalPartialOrder<Argument, DungTheory> ranking = new NumericalPartialOrder<>();
        ranking.setSortingType(NumericalPartialOrder.SortingType.DESCENDING);

        WeightedDungTheoryWithSelfWeight valuations = new WeightedDungTheoryWithSelfWeight(kb, 0.0); // Stores values of the current iteration

        var minimalinitialesetsValuations = getMinimalInitialSetRanking(kb, semantics, valuations);

            for (Argument arg : (minimalinitialesetsValuations)) {
                System.out.println("minsets"+minimalinitialesetsValuations.getWeight(arg));
                if (minimalinitialesetsValuations.getWeight(arg)>0) {
                    ranking.put(arg, minimalinitialesetsValuations.getWeight(arg));
                }
            }

            return ranking;

    }


    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }




    private WeightedDungTheoryWithSelfWeight getMinimalInitialSetRanking(DungTheory bbase, Semantics s, WeightedDungTheoryWithSelfWeight valuations) {

        Collection<Extension<DungTheory>> minimalextensions = SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(
                s).getModels(bbase);
        System.out.println(minimalextensions);
        List<Argument> arguments = new ArrayList<>();


                minimalextensions.stream().forEach(ext -> { arguments.addAll(ext);});

                arguments.stream().forEach(arg -> {
                    var min = minimalextensions.stream().filter(
                            order -> order.contains(arg) &&
                                    minimalextensions.stream().noneMatch(order2 -> order2.contains(arg)
                                            && order2.size() <
                                            order.size())).collect(Collectors.toList());
                    if (!min.isEmpty()) {
                        System.out.println("minext" + min.get(0) + "weight" + min.get(0).size());
                        valuations.setWeight(arg, 1.0 / min.get(0).size());
                    }
                });


        return valuations;
    }
}