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
package org.tweetyproject.lp.asp.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.FunctionalTerm;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.NumberTerm;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.lp.asp.syntax.ASPAtom;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.ASPOperator;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.AggregateAtom;
import org.tweetyproject.lp.asp.syntax.AggregateElement;
import org.tweetyproject.lp.asp.syntax.ArithmeticTerm;
import org.tweetyproject.lp.asp.syntax.ChoiceHead;
import org.tweetyproject.lp.asp.syntax.ComparativeAtom;
import org.tweetyproject.lp.asp.syntax.DefaultNegation;
import org.tweetyproject.lp.asp.syntax.OptimizationStatement;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.syntax.StrictNegation;

/**
 * This class tests all important functions of the ASP-Core-2 Parser in
 * combination with InstantiateVisitor, which is responsible for walking through
 * the parse-tree and generating in-memory classes of the parsed ASP program.
 *
 * 
 * @author Anna Gessler
 * @author Tim Janus
 *
 */
public class ASPParserTest {

	/**
	 * parser
	 */
	static ASPParser parser;
	/**
	 * visitor
	 */
	static InstantiateVisitor visitor;
	/**
	 * default timeout
	 */
	public static final int DEFAULT_TIMEOUT = 5000;

	/**
	 * initializes values
	 */
	@BeforeClass
	public static void init() {
		visitor = new InstantiateVisitor();
		parser = new ASPParser(new StringReader(""));
	}

	/**
	 * test 1 
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ProgramTest() throws ParseException {
		String pstr = "motive(harry). \n" + "motive(sally). \n" + "guilty(harry). \n"
				+ "innocent(Suspect) :- motive(Suspect), not guilty(Suspect). \n";
		Program p1 = ASPParser.parseProgram(pstr);
		assertEquals(p1.size(), 4);
		assertFalse(p1.hasQuery());

		String pstr2 = "motive(harry). \n" + "motive(sally). \n" + "guilty(harry). \n"
				+ "innocent(Suspect) :- motive(Suspect), not guilty(Suspect). \n guilty(sally)?";
		Program p2 = ASPParser.parseProgram(pstr2);
		assertTrue(p2.hasQuery());
	}
	
	/**
	 * cling test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ClingoTest() throws ParseException {
		String pstr = "motive(harry). \n" + "motive(sally). \n" + "guilty(harry). \n"
				+ "innocent(Suspect) :- motive(Suspect), not guilty(Suspect)."
				+ "#show innocent/1."
				+ "#show test/2.";
		Program p = ASPParser.parseProgram(pstr);
		Set<Predicate> pwl = p.getOutputWhitelist();
		assertEquals(pwl.size(),2);
		
		String pstr2 = "motive(harry)."
				+ "#show test/3.";
		Program p2 = ASPParser.parseProgram(pstr2);
		Predicate wp = p2.getOutputWhitelist().iterator().next();
		assertEquals(wp.getArity(),3);
	}
	
	/**
	 * dlv test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void DLVTest() throws ParseException {
		String pstr = ":- <=(harry,sally).";
		Program p = ASPParser.parseProgram(pstr);
		ASPRule r = p.iterator().next();
		assertTrue(r.getPremise().get(0) instanceof ComparativeAtom);	
		
		parser.ReInit(new StringReader("23==23"));
		ComparativeAtom at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.EQ);
	}

	/**
	 * asp rules test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleASPRulesTest() throws ParseException {
		ASPRule fact = ASPParser.parseRule("motive(harry).");
		ASPRule negFact = ASPParser.parseRule("-guilty(sally).");
		ASPRule nafConstraint = ASPParser.parseRule(":- not guilty(sally), guilty(harry).");
		ASPRule simpleASPRule = ASPParser.parseRule("innocent(Suspect) :- motive(Suspect), not guilty(Suspect).");

		ASPLiteral l1 = fact.getLiterals().iterator().next();
		Constant c = (Constant) l1.getArguments().iterator().next();
		assertEquals(l1.getName(), "motive");
		assertEquals(c.get(), "harry");
		assertTrue(fact.isFact());
		assertTrue(negFact.isFact());
		assertTrue((negFact.getLiterals().iterator().next()) instanceof StrictNegation);
		assertTrue(nafConstraint.isConstraint());
		assertTrue((nafConstraint.getPremise().iterator().next()) instanceof DefaultNegation);
		assertFalse(simpleASPRule.isFact());
		assertEquals(simpleASPRule.getPremise().size(), 2);
	}

	/**
	 * aggregation test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void AggregateTest() throws ParseException {
		parser.ReInit(new StringReader("1<=#count{:guilty(jon),guilty(eva),guilty(sam)}<=1."));
		AggregateAtom a = (AggregateAtom) parser.Aggregate().jjtAccept(visitor, null);
		assertEquals(a.getFunction(), ASPOperator.AggregateFunction.COUNT);
		List<AggregateElement> elems = a.getAggregateElements();
		ASPLiteral lit = elems.get(0).getLiterals().first();
		assertEquals(elems.size(), 1);
		assertEquals(lit.getName(), "guilty");
		assertEquals(elems.get(0).getRight().size(), 3);
		
		parser.ReInit(new StringReader("1<=#min{3:price(apple); 2:price(tomato); 8:price(banana)}!=20." ));
		a = (AggregateAtom) parser.Aggregate().jjtAccept(visitor, null);
		assertEquals(a.getFunction(), ASPOperator.AggregateFunction.MIN);
		assertEquals(a.getLeftOperator(), ASPOperator.BinaryOperator.LEQ);
		assertEquals(a.getRightOperator(), ASPOperator.BinaryOperator.NEQ);
		assertEquals(a.getRightGuard(), new NumberTerm(20));
		assertEquals(a.getLeftGuard(), new NumberTerm(1));
		assertEquals(a.getAggregateElements().size(), 3);
		
		parser.ReInit(new StringReader("1<=#max{3:price(apple); 2:price(tomato); 8:price(banana)}!=20." ));
		a = (AggregateAtom) parser.Aggregate().jjtAccept(visitor, null);
		assertEquals(a.getFunction(), ASPOperator.AggregateFunction.MAX);
		assertEquals(a.getAggregateElements().size(), 3);
		
		parser.ReInit(new StringReader("1<=#sum{3:price(apple); 2:price(tomato); 8:price(banana)}!=20." ));
		a = (AggregateAtom) parser.Aggregate().jjtAccept(visitor, null);
		assertEquals(a.getFunction(), ASPOperator.AggregateFunction.SUM);
		assertEquals(a.getAggregateElements().size(), 3);
	}
	/**
	 * optimization statement test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void OptimizationStatementTest() throws ParseException {
		parser.ReInit(new StringReader("#minimize { Y@1, X: hotel(X), star(X,Y) } .\n"));
		ASPRule o = (ASPRule) parser.Optimize().jjtAccept(visitor, null);
		OptimizationStatement o2 = (OptimizationStatement) o.getBody().get(0);
		assertEquals(o2.getOptimizeFunction(),ASPOperator.OptimizeFunction.MINIMIZE);
		assertTrue(o2.getLiterals().contains(new ASPAtom(new Predicate("hotel",1),new Variable("X"))));
		assertTrue(o2.getLiterals().contains(new ASPAtom(new Predicate("star",2),new Variable("X"),new Variable("Y"))));
		assertEquals(o2.getElements().get(0).getWeight(),new Variable("Y"));
		assertEquals(o2.getElements().get(0).getLevel(),new NumberTerm(1));

		parser.ReInit(new StringReader("#maximize { Y@1, X: hotel(X), star(X,Y); Z,X: hotel(X), recommends(Z,X) } .\n"));
		o = (ASPRule) parser.Optimize().jjtAccept(visitor, null);
		o2 = (OptimizationStatement) o.getBody().get(0);
		assertEquals(o2.getOptimizeFunction(),ASPOperator.OptimizeFunction.MAXIMIZE);
		assertEquals(o2.getElements().size(),2);
	}
	
	/**
	 * choice test
	 * @throws ParseException üarsing test
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ChoiceTest() throws ParseException {
		ASPRule r1 = ASPParser.parseRule("{a; b}.");
		assertTrue(r1.getHead() instanceof ChoiceHead);
		ChoiceHead h1 = (ChoiceHead) r1.getHead();
		assertEquals(h1.getElements().size(),2);
		assertEquals(h1.getAtoms().size(),2);
		assertTrue(h1.getAtoms().contains(new ASPAtom("a")));
		assertTrue(h1.getAtoms().contains(new ASPAtom("b")));
		ASPRule r2 = ASPParser.parseRule("{p(a) : not b}.");
		assertTrue(r2.getHead() instanceof ChoiceHead);
		ChoiceHead h2 = (ChoiceHead) r2.getHead();
		assertEquals(h2.getElements().size(),1);
		assertEquals(h2.getAtoms().size(),2);
		ASPRule r3 = ASPParser.parseRule("{a : c; b} :- d.");
		System.out.println(r3);
		assertTrue(r3.getHead() instanceof ChoiceHead);
		ChoiceHead h3 = (ChoiceHead) r3.getHead();
		assertEquals(h3.getElements().size(),2);
		assertEquals(h3.getAtoms().size(),3);
	}

	/**
	 * weight at level test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void WeightAtLevelTest() throws ParseException {
		ASPRule r1 = ASPParser.parseRule(":~ guilty(sally). [4@2,23,X,-2]");
		NumberTerm w = (NumberTerm) r1.getWeight();
		NumberTerm l = (NumberTerm) r1.getLevel();
		List<Term<?>> terms = r1.getConstraintTerms();
		assertEquals(w.get().intValue(), 4);
		assertEquals(l.get().intValue(), 2);
		assertEquals(terms.size(), 3);
	}

	/**
	 * term test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleTermTest() throws ParseException {
		parser.ReInit(new StringReader("42"));
		Term<?> t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof NumberTerm);
		assertEquals(((NumberTerm) t).get().intValue(), 42);

		parser.ReInit(new StringReader("constant"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof Constant);

		parser.ReInit(new StringReader("functor(constant)"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof FunctionalTerm);

		parser.ReInit(new StringReader("Variable"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof Variable);

		parser.ReInit(new StringReader("\"String\""));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof Constant);

		parser.ReInit(new StringReader("_"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof Variable);

		parser.ReInit(new StringReader("-34"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);

		parser.ReInit(new StringReader("-X"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);

		parser.ReInit(new StringReader("( -X )"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);

		parser.ReInit(new StringReader("(5+5)/6"));
		t = (Term<?>) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);
		ArithmeticTerm at = (ArithmeticTerm) t;
		assertEquals(((ArithmeticTerm) at.getLeft()).getOperator(), ASPOperator.ArithmeticOperator.PLUS);
		assertEquals(at.getOperator(), ASPOperator.ArithmeticOperator.DIV);
		assertTrue(at.getRight() instanceof NumberTerm);
	}

	/**
	 * term test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComplexTermTest() throws ParseException {
		parser.ReInit(new StringReader("(age(peter) - V)*(12-23)"));
		ArithmeticTerm t = (ArithmeticTerm) parser.Term().jjtAccept(visitor, null);
		assertTrue(t instanceof ArithmeticTerm);
		assertEquals(t.getOperator(), ASPOperator.ArithmeticOperator.TIMES);
		assertTrue(((ArithmeticTerm) t.getLeft()).getRight() instanceof Variable);
		assertEquals(((ArithmeticTerm) t.getLeft()).getOperator(), ASPOperator.ArithmeticOperator.MINUS);
	}

	/**
	 * comparative test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComparativeTest() throws ParseException {
		parser.ReInit(new StringReader("23=23"));
		ComparativeAtom at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.EQ);
		
		parser.ReInit(new StringReader("2<>23"));
		at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.NEQ);

		parser.ReInit(new StringReader("Var!=23"));
		at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.NEQ);

		parser.ReInit(new StringReader("2<Var"));
		at = (ComparativeAtom) parser.BuiltinAtom().jjtAccept(visitor, null);
		assertEquals(at.getOperator(), ASPOperator.BinaryOperator.LT);
	}

	/**
	 * literal test
	 * @throws ParseException parsing exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void LiteralTest() throws ParseException {
		parser.ReInit(new StringReader("a"));
		ASPAtom at = (ASPAtom) parser.ClassicalLiteral().jjtAccept(visitor, null);
		assertEquals(at.getName(), "a");
		parser.ReInit(new StringReader("guilty(sally)"));
		at = (ASPAtom) parser.ClassicalLiteral().jjtAccept(visitor, null);
		assertEquals(at.getName(), "guilty");
		parser.ReInit(new StringReader("-guilty(sally)"));
		StrictNegation neg = (StrictNegation) parser.ClassicalLiteral().jjtAccept(visitor, null);
		assertEquals(neg.getName(), "guilty");
		assertEquals(neg.getAtom().getName(), at.getName());

		parser.ReInit(new StringReader("not -guilty(sally)"));
		DefaultNegation naf_at = (DefaultNegation) parser.NAFLiteral().jjtAccept(visitor, null);
		StrictNegation neg2 = (StrictNegation) naf_at.getLiteral();
		assertEquals(neg.getAtom().getName(), neg2.getAtom().getName());
	}
}
