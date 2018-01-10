/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * JUnit Test class for PlParser.
 * 
 *  @author Matthias Thimm
 *  @author Anna Gessler
 */

public class PlParserTest {
	PlParser parser;
			
	@Before
	public void initParser() throws ParserException, IOException {
		parser = new PlParser();	
	}
	
	@Test
	public void PropositionTest() throws ParserException, IOException {
		PropositionalFormula f = (PropositionalFormula) parser.parseFormula("a");
		Proposition p = new Proposition("a");
		assertTrue(f.equals(p));
		
		f = (PropositionalFormula) parser.parseFormula("abba");
		p = new Proposition("abba");
		assertTrue(f.equals(p));
		
		f = (PropositionalFormula) parser.parseFormula("(a)");
		p = new Proposition("a");
		assertTrue(f.equals(p));
	}
	
	@Test
	public void NegationTest() throws ParserException, IOException {
		PropositionalFormula f = (PropositionalFormula) parser.parseFormula("!a");
		Negation n = new Negation(new Proposition("a"));
		assertTrue(f.equals(n));
	}
	
	@Test
	public void DisjunctionTest() throws ParserException, IOException {
		PropositionalFormula f = (PropositionalFormula) parser.parseFormula("a || b");
		Disjunction d = new Disjunction(new Proposition("a"), new Proposition("b"));
		assertTrue(f.equals(d));
	}
	
	@Test
	public void ConjunctionTest() throws ParserException, IOException {
		PropositionalFormula f = (PropositionalFormula) parser.parseFormula("a && b");
		Conjunction c = new Conjunction(new Proposition("a"), new Proposition("b"));
		assertTrue(f.equals(c));
	}
	
	@Test
	public void NestedFormulaTest() throws ParserException, IOException {
		parser.parseFormula("!((a && b) || (c || !d))");
	}
	
	@Test
	public void SpecialCharactersTest() throws ParserException, IOException {
		PropositionalFormula f = (PropositionalFormula) parser.parseFormula("a && î || !(!@$$f)");
		PropositionalSignature sig = f.getSignature();
		PropositionalSignature sig2 = new PropositionalSignature();
		sig2.add(new Proposition("a"));
		sig2.add(new Proposition("î"));
		sig2.add(new Proposition("@$$f"));
		assertEquals(sig,sig2);
	}
	
	@Test
	public void ParseBeliefBaseTest() throws ParserException, IOException {
		PlBeliefSet beliefSet = new PlBeliefSet();
		beliefSet = parser.parseBeliefBase("a || b || c \n !a || b \n !b || c \n !c || (!a && !b && !c && !d)");
		assertEquals(beliefSet.size(),4);
		
		Signature sig = beliefSet.getSignature();
		PropositionalSignature sig2 = new PropositionalSignature();
		sig2.add(new Proposition("a"));
		sig2.add(new Proposition("b"));
		sig2.add(new Proposition("c"));
		sig2.add(new Proposition("d"));
		assertEquals(sig,sig2);
	}
	
	@Test
	public void ParseBeliefBaseFromFileTest() throws ParserException, IOException {
		PlBeliefSet beliefSet = new PlBeliefSet();
		beliefSet = parser.parseBeliefBaseFromFile("testbeliefbase.proplogic");
		assertEquals(beliefSet.size(),4);
		
		Signature sig = beliefSet.getSignature();
		PropositionalSignature sig2 = new PropositionalSignature();
		sig2.add(new Proposition("a"));
		sig2.add(new Proposition("b"));
		sig2.add(new Proposition("c"));
		sig2.add(new Proposition("d"));
		assertEquals(sig,sig2);
	}
	
	@Test(expected = ParserException.class) 
	public void EmptyDisjunctionTest() throws ParserException, IOException {
		parser.parseFormula(" || ");
	}
	
	@Test(expected = ParserException.class) 
	public void EmptyConjunctionTest() throws ParserException, IOException {
		parser.parseFormula(" && ");
	}
	
	@Test(expected = ParserException.class) 
	public void IncompleteConjunctionTest() throws ParserException, IOException {
		parser.parseFormula("a && ");
	}
	
	@Test(expected = ParserException.class) 
	public void IncompleteDisjunctionTest() throws ParserException, IOException {
		parser.parseFormula(" || a");
	}
	
	//Propositions should consist only of letters, numbers and/or "_".
	@Test(expected = ParserException.class)
	public void IllegalCharacterTest() throws ParserException, IOException{
		parser.parseFormula(" ");
	}
	
	@Test(expected = ParserException.class) 
	public void BrokenParenthesesTest() throws ParserException, IOException {
		parser.parseFormula("( a");
	}
	
	@Test(expected = ParserException.class) 
	public void BrokenParenthesesTest2() throws ParserException, IOException {
		parser.parseFormula(" a )");
	}
	
	@Test(expected = ParserException.class) 
	public void BrokenParenthesesTest3() throws ParserException, IOException {
		parser.parseFormula(") a (");
	}
	
	@Test(expected = ParserException.class) 
	public void EmptyParenthesesTest() throws ParserException, IOException {
		parser.parseFormula("a || () || b");
	}
}
