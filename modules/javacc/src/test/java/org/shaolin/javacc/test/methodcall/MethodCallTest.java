package org.shaolin.javacc.test.methodcall;

import org.shaolin.javacc.Expression;
import org.shaolin.javacc.ExpressionEvaluator;
import org.shaolin.javacc.ExpressionParser;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This test case test methodcall operation
 * 
 */
public class MethodCallTest extends TestCase {
	public static TestSuite suite() {
		return new TestSuite(MethodCallTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	/**
	 * Test Normal Method Call
	 * 
	 */
	public void testCase1() throws Exception {
		String expressionStr = "(Integer.valueOf(\"3\")).floatValue()";
		Expression expression = ExpressionParser.parse(expressionStr);
		Class expressionClass = expression.getValueClass();
		assertEquals(float.class, expressionClass);

		Object expressionValue = ExpressionEvaluator.evaluate(expression);
		assertEquals(new Float(3), expressionValue);
	}

	/**
	 * Test Static Method Call
	 */
	public void testCase2() throws Exception {
		String expressionStr = MethodCallTest.class.getName() + ".callStaticMethod()";
		Expression expression = ExpressionParser.parse(expressionStr);
		Class expressionClass = expression.getValueClass();
		assertEquals(String.class, expressionClass);

		Object expressionValue = ExpressionEvaluator.evaluate(expression);
		assertEquals("success in call static", expressionValue);
	}

	public static String callStaticMethod() {
		return "success in call static";
	}

	/**
	 * Test Polymorphism Method Call
	 */
	public void testCase3() throws Exception {
		String expressionStr = MethodCallTest.class.getName() + ".callTest(1, 2.0)";
		Expression expression = ExpressionParser.parse(expressionStr);
		Class expressionClass = expression.getValueClass();
		assertEquals(float.class, expressionClass);

		Object expressionValue = ExpressionEvaluator.evaluate(expression);
		assertEquals(new Float(2.0), expressionValue);
	}

	/**
	 * Test Constructor call
	 */
	public void testCase4() throws Exception {
		String expressionStr = "new " + MethodCallTest.class.getName() + "().getClass().getName()";
		Expression expression = ExpressionParser.parse(expressionStr);
		Class expressionClass = expression.getValueClass();
		assertEquals(String.class, expressionClass);

		Object expressionValue = ExpressionEvaluator.evaluate(expression);
		assertEquals("org.shaolin.javacc.test.methodcall.MethodCallTest",
				expressionValue);
	}

	public void testCase5() throws Exception {
		try {
			String expressionStr = "void{String str = null; org.shaolin.uimaster.javacc.test.methodcall.MethodCallTest.callNullPointerTest(str);}";
			CompilationUnit unit = StatementParser.parse(expressionStr);
			StatementEvaluator.evaluate(unit);
			fail("Must be NPE issue!");
		} catch (Exception e) {
		}
	}

	public static int callTest(int i, int j) {
		return i + j;
	}

	public static float callTest(int i, double j) {
		return (float) (i * j);
	}

	public static double callTest(double i, double j) {
		return i / j;
	}

	public static Object callNullPointerTest(Object obj) {
		obj = null;
		obj.toString();
		return obj;
	}
}
