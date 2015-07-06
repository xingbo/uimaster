package org.shaolin.javacc.test.StatementTest.whileSmt;

import java.io.InputStream;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class WhileSmtTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(WhileSmtTest.class);
    }
    
    protected void setUp()
    {
    }
    
    protected void tearDown()
    {
    }
    
    public void testCase1() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("whiletest/Whiletest1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(4950), result);
    }
    
    public void testCase2() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("whiletest/Whiletest2.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(4851), result);
    }
    
    public void testCase3() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("whiletest/Whiletest3.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(4900), result);
    }
    
    public void testCase4() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("whiletest/Whiletest4.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(295), result);
    }
    
    public void testCase5() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("whiletest/Whiletest5.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
    }
    
}