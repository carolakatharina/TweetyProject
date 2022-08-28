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

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.NumericalPartialOrder;

import java.util.*;
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

        var minimalinitialesetsValuations = getMinimalInitialSetRanking(kb, valuations);

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




    private WeightedDungTheoryWithSelfWeight getMinimalInitialSetRanking(DungTheory bbase, WeightedDungTheoryWithSelfWeight valuations) {

        Collection<Extension<DungTheory>> minimalextensions = new HashSet<>();

        var initialarguments = bbase.stream()
                .filter(arg -> bbase.getAttackers(arg).isEmpty() ||
                        bbase.getAttackers(arg).stream().allMatch(
                                att -> bbase.getAttackers(att).contains(arg)
                        )).collect(Collectors.toList());

        initialarguments.forEach(arg -> minimalextensions.add(new Extension<>(List.of(arg))));
        System.out.println("initial"+initialarguments);

        var potentialArguments = bbase.stream()
                .filter(arg -> !initialarguments.contains(arg) &&
                        bbase.getAttackers(arg).stream().noneMatch(att -> bbase
                                .getAttackers(att).isEmpty())).collect(Collectors.toSet());
        System.out.println("pot"+potentialArguments);
        boolean allExtensionsFound = false;

        for (int k = 0; k <potentialArguments.size(); k++) {

            if (!allExtensionsFound) {

                Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(
                        potentialArguments.size(),k);

                while (iterator.hasNext()) {
                    final int[] combination = iterator.next();
                    Collection<Argument> arguments = new HashSet<>();
                    for (int index : combination) {
                        var argument = (Argument) (potentialArguments.toArray()[index]);
                        arguments.add(argument);

                    }
                    arguments.addAll(initialarguments);
                    var potentialextension = new Extension<DungTheory>(arguments);
                    if (bbase.isConflictFree(potentialextension)
                            && (bbase.getAttackers(potentialextension).isEmpty()) || bbase.getAttackers(potentialextension)
                            .stream().allMatch(att ->
                                    bbase.getAttackers(att).stream().anyMatch(potentialextension::contains))) {
                        minimalextensions.add(potentialextension);
                        System.out.println("ADD" + potentialextension);

                    }
                }

                allExtensionsFound = potentialArguments.stream().allMatch(arg ->
                        (minimalextensions.stream().anyMatch(ext -> ext.contains(arg))));
            }
        }

        System.out.println(minimalextensions);

            if (!minimalextensions.isEmpty()) {
                potentialArguments.stream().forEach(arg -> {
                    List<Extension<DungTheory>> minext = new ArrayList<>(minimalextensions.stream().filter(
                            ext -> ext.contains(arg) &&
                                    minimalextensions.stream().noneMatch(ext2 -> ext2.contains(arg)
                                            && ext2.size() <
                                            ext.size())).collect(Collectors.toList()));
                    System.out.println("minext"+minext);

                    if (!minext.isEmpty() && minext.get(0).size()>0) {
                        System.out.println("minext"+minext.get(0)+"weight" + minext.get(0).size());
                        valuations.setWeight(arg, 1.0 / minext.get(0).size());
                    }
                });


                initialarguments.forEach(arg2 ->
                        valuations.setWeight(arg2, 1.0));
            }


        return valuations;
    }
}