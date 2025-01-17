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
import java.math.MathContext;
import java.util.*;

import org.tweetyproject.math.*;

/**
 * This class models the absolute value of the inner term.
 * 
 * @author Matthias Thimm
 */
public class AbsoluteValue extends FunctionalTerm {	
	
	/**
	 * Creates a new absolute value term with the given inner term.
	 * @param term a term
	 */
	public AbsoluteValue(Term term){
		super(term);
	}	
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#getAbsoluteValues()
	 */
	@Override
	public Set<AbsoluteValue> getAbsoluteValues(){
		Set<AbsoluteValue> avs = this.getTerm().getAbsoluteValues();
		avs.add(this);
		return avs;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#replaceTerm(org.tweetyproject.math.term.Term, org.tweetyproject.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution) {
		if(toSubstitute == this)
			return substitution;
		return new AbsoluteValue(this.getTerm().replaceTerm(toSubstitute, substitution));
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#derive(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException{
		throw new NonDifferentiableException();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		Term t = this.getTerm().simplify();
		if(t instanceof Constant)
			return new FloatConstant(Math.abs(t.doubleValue()));
		return new AbsoluteValue(t);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#toString()
	 */
	@Override
	public String toString() {
		return "abs(" + this.getTerm().toString() + ")";
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#isContinuous(org.tweetyproject.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		return this.getTerm().isContinuous(v);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.term.Term#value()
	 */
	@Override
	public Constant value() throws IllegalArgumentException {
		Constant c = this.getTerm().value();
		if(c instanceof IntegerConstant){
			if(((IntegerConstant)c).getValue() < 0)
				return new IntegerConstant(((IntegerConstant)c).getValue()*-1);
			else return c;
		}else if(c instanceof FloatConstant){
			if(((FloatConstant)c).getValue() < 0)
				return new FloatConstant(((FloatConstant)c).getValue()*-1);
			else return c;
		}else if(c instanceof BigDecimalConstant){
		if(((BigDecimalConstant)c).getValue().doubleValue() < 0)
			return new BigDecimalConstant(((BigDecimalConstant)c).getValue().multiply(BigDecimal.valueOf(-1), MathContext.DECIMAL128));
		else return c;
	}
		throw new IllegalArgumentException("Unrecognized atomic term type.");
	}

	@Override
	public List<Term> getTerms() {
		return null;
	}
}
