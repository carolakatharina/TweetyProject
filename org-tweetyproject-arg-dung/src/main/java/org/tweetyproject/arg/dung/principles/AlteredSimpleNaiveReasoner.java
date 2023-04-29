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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.principles;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Reasoner for naive extensions. naive extensions are maximal conflict-free sets
 *
 * @author Lars Bengel
 */
public class AlteredSimpleNaiveReasoner extends AbstractExtensionReasoner {

    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        DungTheory restrictedTheory = new DungTheory((DungTheory)bbase);
        // remove all self-attacking arguments
        for (Argument argument: (DungTheory)bbase) {
            if (restrictedTheory.isAttackedBy(argument, argument)) {
                restrictedTheory.remove(argument);
            }
        }
        return this.getMaximalConflictFreeSets((DungTheory)bbase, restrictedTheory);
    }

    public Extension<DungTheory> getModel(DungTheory bbase) {
        Collection<Extension<DungTheory>> extensions = this.getModels(bbase);
        return extensions.iterator().next();
    }

    /**
     * computes all maximal conflict-free sets of bbase
     * @param bbase an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Collection<Extension<DungTheory>> getMaximalConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        var maxcfsets = new HashSet<Extension<DungTheory>>();

        maxcfsets.addAll(new SimpleGroundedReasoner().getModels((DungTheory) candidates));
        maxcfsets.addAll(new SimplePreferredReasoner().getModels((DungTheory) candidates).stream().filter(ext ->
                !maxcfsets.contains(ext)).collect(Collectors.toList()));
        maxcfsets.addAll(new SimpleStableReasoner().getModels((DungTheory) candidates).stream().filter(ext ->
                !maxcfsets.contains(ext)).collect(Collectors.toList()));

        return maxcfsets;
    }




    }
