/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.javacc.test.numeric;

//imports
//junit
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.shaolin.javacc.Expression;
import org.shaolin.javacc.ExpressionEvaluator;
import org.shaolin.javacc.ExpressionParser;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;

//ooee

/**
 * This test case test numeric operation for numbers
 *
 */
public class NumericTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(NumericTest.class);
    }
    
    protected void setUp()
    {
    }
    
    protected void tearDown()
    {
    }

    /**
     *  Test Add
     *
     */
    public void testCase1() throws Exception
    {
    	String expressionStr = "1 + 2";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(3), expressionValue);
    }
    
    /**
     *  Test Minus
     */
    public void testCase2() throws Exception
    {
    	String expressionStr = "1 - 2";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(-1), expressionValue);
    }
    
    /**
     *  Test Multiply
     */
    public void testCase3() throws Exception
    {
    	String expressionStr = "1 * 2";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(2), expressionValue);
    }

    /**
     *  Test Divide
     */
    public void testCase4() throws Exception
    {
    	String expressionStr = "3 / 2";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(1), expressionValue);
    }
    
    /**
     *  Test Mix Up
     */
    public void testCase5() throws Exception
    {
    	String expressionStr = " 1 + 3 / 2 + 4 * 5 - 6";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(16), expressionValue);
    }

    /**
     *  Test With Parenthesis
     */
    public void testCase6() throws Exception
    {
    	String expressionStr = " (1 + 3) / 2 + 4 * (5 - 6)";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(-2), expressionValue);
    } 
    
    /**
     *  Test With Lang primitive
     */
    public void testCase7() throws Exception
    {
    	String expressionStr = "{int i = 0; Integer b = new Integer(1); i=b; b=i; System.out.println(i +\" \"+b);}";
    	CompilationUnit expression = StatementParser.parse(expressionStr);
    	StatementEvaluator.evaluate(expression);
    }
    
    /**
     *  Test With Lang primitive
     */
    public void testCase8() throws Exception
    {
    	String expressionStr = "{long i = 0; Long b = new Long(1); i=b; b=i; System.out.println(i +\" \"+b);}";
    	CompilationUnit expression = StatementParser.parse(expressionStr);
    	StatementEvaluator.evaluate(expression);
    }
    
    /**
     *  Test With Lang primitive
     */
    public void testCase9() throws Exception
    {
    	String expressionStr = "{float i = 2; Float b = new Float(2.0); i=b; b=i; System.out.println(i +\" \"+b);}";
    	CompilationUnit expression = StatementParser.parse(expressionStr);
    	StatementEvaluator.evaluate(expression);
    }
    
    /**
     *  Test With Lang primitive
     */
    public void testCase10() throws Exception
    {
    	String expressionStr = "{double i = 3; Double b = new Double(3.0); i=b; b=i; System.out.println(i +\" \"+b);}";
    	CompilationUnit expression = StatementParser.parse(expressionStr);
    	StatementEvaluator.evaluate(expression);
    }
    
    public void testCase11() throws Exception
    {
    	String expressionStr = "{boolean i = true;  Boolean b = new Boolean(true); i=b; b=i; System.out.println(i +\" \"+b);}";
    	CompilationUnit expression = StatementParser.parse(expressionStr);
    	StatementEvaluator.evaluate(expression);
    }
}
