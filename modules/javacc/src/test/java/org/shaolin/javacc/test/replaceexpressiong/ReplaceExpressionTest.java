package org.shaolin.javacc.test.replaceexpressiong;

import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.BlockCompilationUnit;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;


public class ReplaceExpressionTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(ReplaceExpressionTest.class);
    }
    public void testCase1() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("expressions/expression1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        
        String trimString = ((BlockCompilationUnit)unit).rebuildExpression();
        Object value1 = StatementEvaluator.evaluate(unit);
        
        statementString = removeImport(trimString);
        
        unit = StatementParser.parse(statementString);
        Object value2 = StatementEvaluator.evaluate(unit);
        assertEquals(value1, value2);
        CloseUtil.close(path);
    }
    
    
    public void testCase2() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("expressions/expression2.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        
        String trimString = ((BlockCompilationUnit)unit).rebuildExpression();
        Object value1 = StatementEvaluator.evaluate(unit);
        
        statementString = removeImport(trimString);
        
        unit = StatementParser.parse(statementString);
        Object value2 = StatementEvaluator.evaluate(unit);
        assertEquals(value1, value2);
        CloseUtil.close(path);
    }
    
    
    // just remove string before first "{"
    private String removeImport(String expressionString)
    {
        int index = 0;
        int len = expressionString.length();
        for (int i = 0; i < len; i++)
        {
            char c = expressionString.charAt(i);
            if (c == ';')
            {
                index = i;
            }
            if (c == '{')
            {
                break;
            }
        }
        
        return expressionString.substring(index + 1);
    }
}
