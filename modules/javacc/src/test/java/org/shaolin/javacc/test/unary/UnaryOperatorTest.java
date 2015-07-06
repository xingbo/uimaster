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
