
package org.shaolin.javacc.test.StatementTest.dosmt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;
import org.shaolin.javacc.util.traverser.VariableLister;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DowhileSmtTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(DowhileSmtTest.class);
    }


    public void testCase1() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("dowhile/dowhiletest1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        VariableLister lister = new VariableLister();
        unit.traverse(lister);
        assertEquals(2, lister.getAllVariables().length);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(2), result);
    }

    public void testCase2() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("dowhile/dowhiletest2.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(3), result);

    }

    public void testCase3() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("dowhile/dowhiletest3.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        VariableLister lister = new VariableLister();
        unit.traverse(lister);
        assertEquals(3, lister.getAllVariables().length);
        Object result = StatementEvaluator.evaluate(unit);
        List list = new ArrayList();
        list.add("str1");
        list.add("str2");
        list.add("str4");
        assertEquals(list, result);

    }

}
