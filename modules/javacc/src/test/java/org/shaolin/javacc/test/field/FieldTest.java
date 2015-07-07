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

