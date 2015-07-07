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
