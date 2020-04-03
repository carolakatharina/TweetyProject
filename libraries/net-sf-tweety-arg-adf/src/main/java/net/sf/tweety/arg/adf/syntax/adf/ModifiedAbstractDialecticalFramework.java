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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.syntax.adf;

import java.util.Set;

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;

/**
 * @author Mathias Hofer
 *
 */
public final class ModifiedAbstractDialecticalFramework implements AbstractDialecticalFramework {

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#getArguments()
	 */
	@Override
	public Set<Argument> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#links()
	 */
	@Override
	public Set<Link> links() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#link(net.sf.tweety.arg.adf.syntax.Argument, net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Link link(Argument parent, Argument child) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#linksTo(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Link> linksTo(Argument child) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#linksFrom(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Link> linksFrom(Argument parent) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#parents(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Argument> parents(Argument child) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#children(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Argument> children(Argument parent) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#contains(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public boolean contains(Argument arg) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#getAcceptanceCondition(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public AcceptanceCondition getAcceptanceCondition(Argument argument) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#kBipolar()
	 */
	@Override
	public int kBipolar() {
		// TODO Auto-generated method stub
		return 0;
	}



}
