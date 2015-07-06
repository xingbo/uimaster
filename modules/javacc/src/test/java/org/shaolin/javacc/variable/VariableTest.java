/*
 * Copyright 2000-2003 by BMI Asia, Inc.,
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of BMI Asia, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BMI Asia.
 */

//package

package org.shaolin.javacc.variable;

import org.shaolin.bmdp.utils.SerializeUtil;
import org.shaolin.javacc.Expression;
import org.shaolin.javacc.ExpressionEvaluator;
import org.shaolin.javacc.ExpressionParser;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.util.traverser.VariableLister;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This test case test variable operation for numbers
 * 
 */
public class VariableTest extends TestCase {
	public static TestSuite suite() {
		return new TestSuite(VariableTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	/**
	 * Test Set Variable Value
	 */
	public void testCase1() throws Exception {
		DefaultParsingContext parsingContext = new DefaultParsingContext();
		DefaultEvaluationContext evaluationContext = new DefaultEvaluationContext();

		parsingContext.setVariableClass("a", int.class);
		evaluationContext.initVariable("a");

		String expressionStr = "a = 1";
		Expression expression = ExpressionParser.parse(expressionStr,
				parsingContext);
		
		Class expressionClass = expression.getValueClass();
		assertEquals(int.class, expressionClass);

		Object expressionValue = ExpressionEvaluator.evaluate(expression,
				evaluationContext);
		assertEquals(new Integer(1), expressionValue);

		parsingContext.setVariableClass("b", double.class);
		evaluationContext.initVariable("b");

		expressionStr = "b = a + 1";
		expression = ExpressionParser.parse(expressionStr, parsingContext);
		VariableLister lister = new VariableLister();
		expression.getRoot().traverse(lister);
		System.out.println(lister.getAllVariables().length);
		expressionClass = expression.getValueClass();
		assertEquals(double.class, expressionClass);
		
		SerializeUtil.serializeData(expression);
		SerializeUtil.serializeData(parsingContext);

		expressionValue = ExpressionEvaluator.evaluate(expression,
				evaluationContext);
		assertEquals(new Double(2), expressionValue);
		
	}

	/**
	 * Test Set Variable Value of variable whose name has "." in it
	 */
	public void testCase2() throws Exception {
		DefaultParsingContext parsingContext = new DefaultParsingContext();
		DefaultEvaluationContext evaluationContext = new DefaultEvaluationContext();

		parsingContext.setVariableClass("a.b.c", int.class);
		evaluationContext.initVariable("a.b.c");

		parsingContext.setVariableClass("d.e", int.class);
		evaluationContext.initVariable("d.e");
		evaluationContext.setVariableValue("d.e", new Integer(3));

		String expressionStr = "a.b.c = d.e + 1";
		Expression expression = ExpressionParser.parse(expressionStr,
				parsingContext);
		Class expressionClass = expression.getValueClass();
		assertEquals(int.class, expressionClass);

		SerializeUtil.serializeData(expression);
		SerializeUtil.serializeData(parsingContext);
		
		Object expressionValue = ExpressionEvaluator.evaluate(expression,
				evaluationContext);
		assertEquals(new Integer(4), expressionValue);
	}

	public void testCase3() throws Exception {
		String expressionStr = "\"aa\" \"bb\"";
		try {
			CompilationUnit expression = StatementParser.parse(expressionStr);

			Object expressionValue = StatementEvaluator.evaluate(expression);
			assertEquals("aabb", expressionValue);
			fail("no exception happend");
		} catch (Throwable e) {
			assertTrue(true);
		}

	}

	public void testCase4() throws Exception {
		String expressionStr = "System.out.println(\"afbcd\");;";
		try {
			CompilationUnit expression = StatementParser.parse(expressionStr);
			Object expressionValue = StatementEvaluator.evaluate(expression);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("exception happend");
		}

	}
}
