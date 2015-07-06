package org.shaolin.javacc.test.StatementTest.comment;

import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;

public class CommentTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(CommentTest.class);
    }
    
    protected void setUp()
    {
    }
    
    protected void tearDown()
    {
    }
    
    public void testCase1() throws Throwable
    {
        
        InputStream path = ClassLoader.getSystemResourceAsStream("comments/commenttest1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        StatementEvaluator.evaluate(unit);
    }
    
    public void testCase2() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("comments/commenttest2.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        StatementEvaluator.evaluate(unit);
    }
    
    public void testCase3() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("comments/commenttest3.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        StatementEvaluator.evaluate(unit);
    }

}