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
package org.shaolin.javacc.test.field;

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
public class FieldTest extends TestCase
{
    public static FieldTestMember testMember;

    public static TestSuite suite()
    {
        return new TestSuite(FieldTest.class);
    }

    protected void setUp()
    {
        testMember = new FieldTestMember();
        testMember.memberNo = 0;
    }

    protected void tearDown()
    {
    }

    /**
     *  Test static field
     */
    public void testCase1() throws Exception
    {
        String expressionStr = FieldTest.class.getName() + ".testMember";

        Expression expression = ExpressionParser.parse(expressionStr);
        Class expressionClass = expression.getValueClass();
        assertEquals(FieldTestMember.class, expressionClass);

        Object expressionValue = ExpressionEvaluator.evaluate(expression);
        assertEquals(testMember, expressionValue);

        expressionStr = FieldTest.class.getName() + ".testMember = null";

        expression = ExpressionParser.parse(expressionStr);
        expressionClass = expression.getValueClass();
        assertEquals(FieldTestMember.class, expressionClass);

        expressionValue = ExpressionEvaluator.evaluate(expression);
        assertEquals(testMember, null);
    }


    /**
     *  Test normal field
     */
    public void testCase2() throws Exception
    {
        String expressionStr = FieldTest.class.getName() + ".testMember.memberNo";

        Expression expression = ExpressionParser.parse(expressionStr);
        Class expressionClass = expression.getValueClass();
        assertEquals(int.class, expressionClass);

        Object expressionValue = ExpressionEvaluator.evaluate(expression);
        assertEquals(new Integer(0), expressionValue);

        expressionStr = FieldTest.class.getName() + ".testMember.memberNo = 2";

        expression = ExpressionParser.parse(expressionStr);
        expressionClass = expression.getValueClass();
        assertEquals(int.class, expressionClass);

        expressionValue = ExpressionEvaluator.evaluate(expression);

        assertEquals(2, testMember.memberNo);
    }

    /**
     *  Test class literal
     */
    public void testCase3() throws Exception
    {
        String expressionStr = FieldTest.class.getName() + ".class.getName()";
        Expression expression = ExpressionParser.parse(expressionStr);

        Class expressionClass = expression.getValueClass();
        assertEquals(String.class, expressionClass);

        Object expressionValue = ExpressionEvaluator.evaluate(expression);
        assertEquals(FieldTest.class.getName(), expressionValue);
    }

}

