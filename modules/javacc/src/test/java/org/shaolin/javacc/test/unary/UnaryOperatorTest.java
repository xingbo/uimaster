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
package org.shaolin.javacc.test.unary;

//imports
//junit
import org.shaolin.javacc.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

//ooee

/**
 * This test case test numeric operation for numbers
 *
 */
public class UnaryOperatorTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(UnaryOperatorTest.class);
    }
    
    protected void setUp()
    {
    }
    
    protected void tearDown()
    {
    }

    /**
     *  Test !
     *
     */
    public void testCase1() throws Exception
    {
    	String expressionStr = "!true";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(boolean.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(Boolean.FALSE, expressionValue);
    }
    
    /**
     *  Test ~
     */
    public void testCase2() throws Exception
    {
    	String expressionStr = "~111";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(-112), expressionValue);
    }
    
    /**
     *  Test +
     */
    public void testCase3() throws Exception
    {
    	String expressionStr = "+12";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(12), expressionValue);
    }

    /**
     *  Test -
     */
    public void testCase4() throws Exception
    {
    	String expressionStr = "-11";
    	Expression expression = ExpressionParser.parse(expressionStr);
    	Class expressionClass = expression.getValueClass();
    	assertEquals(int.class, expressionClass);
    	
    	Object expressionValue = ExpressionEvaluator.evaluate(expression);
    	assertEquals(new Integer(-11), expressionValue);
    }
}
