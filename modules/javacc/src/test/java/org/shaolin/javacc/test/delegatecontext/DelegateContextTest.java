package org.shaolin.javacc.test.delegatecontext;

import org.shaolin.bmdp.utils.SerializeUtil;
import org.shaolin.javacc.*;
import org.shaolin.javacc.context.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

//ooee

/**
 * This test case test delegatecontext operation
 * 
 */
public class DelegateContextTest extends TestCase {
	public static TestSuite suite() {
		return new TestSuite(DelegateContextTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	/**
	 * Test Delegate Method Call
	 * 
	 */
	public void testCase1() throws Exception {
		ContextA contextA = new ContextA();
		ContextB contextB = new ContextB();

		OOEEContext context = OOEEContextFactory.createOOEEContext();

		context.setParsingContextObject("$", contextA);
		context.setEvaluationContextObject("$", contextA);

		context.setParsingContextObject("#", contextB);
		context.setEvaluationContextObject("#", contextB);

		String expressionStr = "$setValue(\"a\")";
		Expression expression = ExpressionParser.parse(expressionStr, context);
		ExpressionEvaluator.evaluate(expression, context);

		expressionStr = "#setValue(\"b\")";
		expression = ExpressionParser.parse(expressionStr, context);
		ExpressionEvaluator.evaluate(expression, context);

		expressionStr = "$var";
		expression = ExpressionParser.parse(expressionStr, context);
		Object value = ExpressionEvaluator.evaluate(expression, context);
		assertEquals("a", value);

		expressionStr = "#var";
		expression = ExpressionParser.parse(expressionStr, context);
		value = ExpressionEvaluator.evaluate(expression, context);
		assertEquals("b", value);
		
		SerializeUtil.serializeData(expression);
		SerializeUtil.serializeData(context);
	}

	public void testCase2() throws Exception {
		ContextA contextA = new ContextA();

		OOEEContext context = OOEEContextFactory.createOOEEContext();

		context.setParsingContextObject("$", contextA);
		context.setEvaluationContextObject("$", contextA);

		String expressionStr = "$setValue(\"a\")";
		Expression expression = ExpressionParser.parse(expressionStr, context);
		ExpressionEvaluator.evaluate(expression, context);

		expressionStr = "$var\"aa\"";
		try {
			expression = ExpressionParser.parse(expressionStr, context);
			fail("no exception happend");
		} catch (Exception e) {
			assertTrue(true);
		}
	}

}
