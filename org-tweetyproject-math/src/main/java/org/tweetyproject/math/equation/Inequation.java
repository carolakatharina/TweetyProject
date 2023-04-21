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
package org.tweetyproject.math.equation;

import org.tweetyproject.math.term.*;

import java.math.BigDecimal;

/**
 * This class models an inequation of two terms.
 * @author Matthias Thimm
 */
public class Inequation extends Statement{

	/**less*/
	public static final int LESS = 0;
	/**less or equal*/
	public static final int LESS_EQUAL = 1;
	/**greater*/
	public static final int GREATER = 2;
	/**greater or equal*/
	public static final int GREATER_EQUAL = 3;
	/**unequal*/
	public static final int UNEQUAL = 4;
	
	private int type;
	
	/**
	 * Creates a new inequation of the given type with the two terms.
	 * @param leftTerm a term.
	 * @param rightTerm a term.
	 * @param type the type of the inequality, one of Inequation.LESS, Inequation.LESS_EQUAL
	 * 		Inequation.GREATER, Inequation.GREATER_EQUAL, Inequation.UNEQUAL.
	 */
	public Inequation(Term leftTerm, Term rightTerm, int type){
		super(leftTerm,rightTerm);
		if(type < Inequation.LESS || type > Inequation.UNEQUAL)
			throw new IllegalArgumentException("Wrong type for inequation. Expected one of Inequation.LESS, Inequation.LESS_EQUAL, Inequation.GREATER, Inequation.GREATER_EQUAL, Inequation.UNEQUAL.");
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.equation.Statement#replaceTerm(org.tweetyproject.math.term.Term, org.tweetyproject.math.term.Term)
	 */
	@Override
	public Statement replaceTerm(Term toSubstitute, Term substitution){
		return new Inequation(this.getLeftTerm().replaceTerm(toSubstitute, substitution),this.getRightTerm().replaceTerm(toSubstitute, substitution),this.type);
	}
	
	/**
	 * Returns the type of this inequation.
	 * @return the type of this inequation.
	 */
	public int getType(){
		return this.type;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.equation.Statement#isNormalized()
	 */
	@Override
	public boolean isNormalized(){
		if(this.type == Inequation.GREATER || this.type == Inequation.GREATER_EQUAL || this.type == Inequation.UNEQUAL)
			if(this.getRightTerm() instanceof Constant){
				if(this.getRightTerm() instanceof FloatConstant){
					if(((FloatConstant)this.getRightTerm()).getValue() == 0)
						return true;
				}
				if(this.getRightTerm() instanceof BigDecimalConstant){
					if(((BigDecimalConstant)this.getRightTerm()).getValue() == BigDecimal.valueOf(0))
						return true;
				}
				if(this.getRightTerm() instanceof IntegerConstant){
					if(((IntegerConstant)this.getRightTerm()).getValue() == 0)
						return true;
				}
			}	
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.equation.Statement#toNormalizedForm()
	 */
	@Override
	public Statement toNormalizedForm(){
		// Check whether it is already normalized
		if(this.isNormalized()) return this;	
		// rearrange the terms
		Term term = this.getLeftTerm().minus(this.getRightTerm());
		
		if(this.type == Inequation.GREATER || this.type == Inequation.GREATER_EQUAL || this.type == Inequation.UNEQUAL)
			return new Inequation(term,new IntegerConstant(0),this.type);			
		else{
			int type = this.type;
			if(type == Inequation.LESS) type = Inequation.GREATER;
			if(type == Inequation.LESS_EQUAL) type = Inequation.GREATER_EQUAL;				
			return new Inequation(term.mult(new IntegerConstant(-1)),new IntegerConstant(0),type);
		}	
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.equation.Statement#toLinearForm()
	 */
	@Override
	public Statement toLinearForm(){
		Term left = this.getLeftTerm().toLinearForm();
		Term right = (this.isNormalized())?(this.getRightTerm()):(this.getRightTerm().toLinearForm());
		return new Inequation(left,right,this.type);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.math.equation.Statement#getRelationSymbol()
	 */
	@Override
	public String getRelationSymbol(){
		if(this.type == Inequation.LESS)
			return "<";
		if(this.type == Inequation.LESS_EQUAL)
			return "<=";
		if(this.type == Inequation.UNEQUAL)
			return "<>";
		if(this.type == Inequation.GREATER)
			return ">";
		if(this.type == Inequation.GREATER_EQUAL)
			return ">=";
		throw new IllegalArgumentException("Inequation is of unrecognized type.");
	}

	@Override
	public boolean isValid(Statement s) {
		double left = s.getLeftTerm().doubleValue();
		double right = s.getRightTerm().doubleValue();
		if(this.type == Inequation.LESS)
			return left < right ? true:false;
		if(this.type == Inequation.LESS_EQUAL)
			return left <= right ? true:false;
		if(this.type == Inequation.UNEQUAL)
			return left != right ? true:false;
		if(this.type == Inequation.GREATER)
			return left > right ? true:false;
		if(this.type == Inequation.GREATER_EQUAL)
			return left >= right ? true:false;
		throw new IllegalArgumentException("Inequation is of unrecognized type.");
	}
	
}
