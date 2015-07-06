package org.shaolin.javacc.test.array;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.shaolin.javacc.Expression;
import org.shaolin.javacc.ExpressionEvaluator;
import org.shaolin.javacc.ExpressionParser;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;
import org.slf4j.LoggerFactory;

//ooee

/**
 * This test case test variable operation for numbers
 * 
 */
public class ArrayTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(ArrayTest.class);
    }

    protected void setUp()
    {
    }

    protected void tearDown()
    {
    }

    /**
     * Test array initialization
     */
    public void testCase1() throws Exception
    {
        String expressionStr = "(new int[]{2, 3, 4}).length";
        CompilationUnit expression = StatementParser.parse(expressionStr);
        Class expressionClass = expression.getValueClass();
        assertEquals(int.class, expressionClass);

        Object expressionValue = StatementEvaluator.evaluate(expression);
        assertEquals(new Integer(3), expressionValue);

        expressionStr = "(new double[]{5.0, 6.0, 7.0})[1]";
        expression = StatementParser.parse(expressionStr);
        // expressionClass = expression.getExp().getValueClass();
        // assertEquals(double.class, expressionClass);

        expressionValue = StatementEvaluator.evaluate(expression);
        assertEquals(new Double(6), expressionValue);

        expressionStr = "(new double[][]{{5.0, 6.0, 7.0}, {8.0, 9.0}})[1][1]";
        expression = StatementParser.parse(expressionStr);
        // expressionClass = expression.getExp().getValueClass();
        // assertEquals(double.class, expressionClass);

        expressionValue = StatementEvaluator.evaluate(expression);
        assertEquals(new Double(9.0), expressionValue);
        
    }

    /**
     * Test array assignment
     */
    public void testCase2() throws Exception
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("arrays/arraytest2.jav");
        String statementString = FileReaderUtil.readFile(path);
        Expression expression1 = ExpressionParser.parse(statementString);
        Class expressionClass1 = expression1.getValueClass();

        Object expressionValue1 = ExpressionEvaluator.evaluate(expression1);
        assertEquals(new Double(5.0), expressionValue1);
    }
    
    public void testCase3() throws Exception
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("arrays/arraytest1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        assertEquals(List[][].class, unit.getValueClass());
    }
    
    public void testCase4() throws Exception
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("arrays/arraytest3.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        unit.getExpressionString();
        
        Object value = StatementEvaluator.evaluate(unit);
        assertEquals(value, "Wu");
    }
}
