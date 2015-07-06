package org.shaolin.javacc.test.StatementTest.importDeclare;

import java.io.InputStream;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ImportDeclareTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(ImportDeclareTest.class);
    }
    
    protected void setUp()
    {
    }
    
    protected void tearDown()
    {
    }
    
    public void testCase1() throws Throwable
    {
        System.out.println("in testCase1");
        InputStream path = ClassLoader.getSystemResourceAsStream("imports/importDeclare1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(4950), result);
    }
    
    
}