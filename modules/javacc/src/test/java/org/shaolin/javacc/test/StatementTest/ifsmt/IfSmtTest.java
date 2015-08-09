
package org.shaolin.javacc.test.StatementTest.ifsmt;

import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;
import org.shaolin.javacc.util.traverser.VariableLister;

public class IfSmtTest extends TestCase
{
    InputStream path = null;

    public static TestSuite suite()
    {
        return new TestSuite(IfSmtTest.class);
    }

    protected void setUp()
    {
    }

    protected void tearDown()
    {
        CloseUtil.close(path);
    }

    public void testCase1() throws Throwable
    {
        path = ClassLoader.getSystemResourceAsStream("iftest/iftest1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(10), result);
    }

    public void testCase2() throws Throwable
    {
        path =  ClassLoader.getSystemResourceAsStream("iftest/iftest2.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        VariableLister lister = new VariableLister();
        unit.traverse(lister);
        assertEquals(1, lister.getAllVariables().length);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Double(1.11), result);
    }

    public void testCase3() throws Throwable
    {
        path =  ClassLoader.getSystemResourceAsStream("iftest/iftest3.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        VariableLister lister = new VariableLister();
        unit.traverse(lister);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(0, result);
    }
    
    public void testCase4() throws Throwable
    {
        path =  ClassLoader.getSystemResourceAsStream("iftest/iftest4.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        VariableLister lister = new VariableLister();
        unit.traverse(lister);
        Object result = StatementEvaluator.evaluate(unit);
        assertNull(result);
    }
}
