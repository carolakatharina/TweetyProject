package org.tweetyproject.arg.rbextensionsemantics.exactreasoner;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.comparator.ExactNumericalPartialOrder;

import java.util.Collection;

public abstract class AbstractExactNumericalPartialOrderRankingReasoner extends AbstractRankingReasoner<ExactNumericalPartialOrder<Argument, DungTheory>> {
    @Override
    public abstract Collection<ExactNumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase);

    @Override
    public abstract ExactNumericalPartialOrder<Argument, DungTheory> getModel(DungTheory base);

}
