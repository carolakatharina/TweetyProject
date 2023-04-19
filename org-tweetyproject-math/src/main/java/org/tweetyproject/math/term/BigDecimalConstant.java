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
package org.tweetyproject.math.term;

import java.math.BigDecimal;

/**
 * This class encapsulates a float as a term.
 * @author Carola Bauer
 */
public class BigDecimalConstant extends Constant {

	/**
	 * the actual float.
	 */
	private BigDecimal f;

	/**
	 * Creates a new BigDecimal.
	 * @param f a BigDecimal.
	 */
	public BigDecimalConstant(BigDecimal f){
		this.f = f;
	}


	
	/**
	 * Get the value of this float.
	 * @return the value of this float.
	 */
	public BigDecimal getValue(){
		return this.f;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger(){
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toString()
	 */
	@Override
	public String toString(){
		return String.valueOf(this.f);
	}
}
