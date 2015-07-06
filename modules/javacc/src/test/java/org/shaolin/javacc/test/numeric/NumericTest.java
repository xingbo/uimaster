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
package org.shaolin.javacc.test.numeric;

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
}
