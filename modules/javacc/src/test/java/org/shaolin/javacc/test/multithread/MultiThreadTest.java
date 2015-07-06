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
package org.shaolin.javacc.test.multithread;

//imports
//junit
import org.shaolin.javacc.*;
import org.shaolin.javacc.context.*;
import org.shaolin.javacc.exception.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

//ooee

/**
 * This test case test variable operation for numbers
 *
 */
public class MultiThreadTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(MultiThreadTest.class);
    }
    
    private static Expression expression;
    private static int count = 0;
    
    private DefaultEvaluationContext evaluationContext;
    
    private int aValue;
    
    static
    {
		DefaultParsingContext parsingContext = new DefaultParsingContext();
		parsingContext.setVariableClass("a", MultiThreadTestUtil.class);
		
    	String expressionString = "a.getNo()";
		
		try
		{
    		expression = ExpressionParser.parse(expressionString, parsingContext);
    	}
    	catch(Exception ex)
    	{
    	}
    }
        
    protected void setUp()
    {
    	try
    	{
	    	evaluationContext = new DefaultEvaluationContext();
	    	aValue = count++;
	    	evaluationContext.initVariable("a");
	    	evaluationContext.setVariableValue("a", new MultiThreadTestUtil(aValue));
	    }
	    catch(Exception ex)
	    {
	    }
    }
    
    protected void tearDown()
    {
    }
    
    /**
     *  Test array initialization
     */
    public void testCase1() throws Exception
    {
    	Object expressionValue = ExpressionEvaluator.evaluate(expression, evaluationContext);
    	assertEquals(new Integer(aValue), expressionValue);
    }
    

}
