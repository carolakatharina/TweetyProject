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
package net.sf.tweety.arg.bipolar.reasoner.evidential;

import net.sf.tweety.arg.bipolar.syntax.ArgumentSet;
import net.sf.tweety.arg.bipolar.syntax.EvidentialArgumentationFramework;
import java.util.*;

/**
 * the grounded extension of bbase is the least fixed point of the characteristic function.
 *
 * @author Lars Bengel
 *
 */
public class GroundedReasoner {

    public Collection<ArgumentSet> getModels(EvidentialArgumentationFramework bbase) {
        Collection<ArgumentSet> extensions = new HashSet<>();
        extensions.add(this.getModel(bbase));
        return extensions;
    }

    public ArgumentSet getModel(EvidentialArgumentationFramework bbase) {
        ArgumentSet ext = new ArgumentSet();
        int size;
        do{
            size = ext.size();
            ext = bbase.faf(ext);
        }while(size!=ext.size());
        return ext;
    }
}
