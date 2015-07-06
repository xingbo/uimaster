package org.shaolin.javacc.test.StatementTest.forsmt;

import java.io.InputStream;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ForSmtTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(ForSmtTest.class);
    }
    
    protected void setUp()
    {
    }
    
    protected void tearDown()
    {
    }
    
    public void testCase1() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("fortest/fortest1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(100000000), result);
    }
    
    public void testCase2() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("fortest/fortest2.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(100000), result);        
    }

    
}
