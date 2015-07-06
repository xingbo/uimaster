package org.shaolin.javacc.test.StatementTest.localvardeclaration;

import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;

public class LocalVarTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(LocalVarTest.class);
    }
    
    protected void setUp()
    {
    }
    
    protected void tearDown()
    {

    }
    
    public void testCase1() throws Throwable
    {
        InputStream path = ClassLoader.getSystemResourceAsStream("localtest/localtest1.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        Object result = StatementEvaluator.evaluate(unit);
        assertEquals(new Integer(3), result);
    }
    
    public void testCase2() throws Throwable
    {
		InputStream path = ClassLoader.getSystemResourceAsStream("localtest/localtest2.jav");
        String statementString = FileReaderUtil.readFile(path);	
        DefaultParsingContext dpc =new DefaultParsingContext();
        dpc.setVariableClass("d", int.class);
        DefaultEvaluationContext dec = new DefaultEvaluationContext();
        dec.setVariableValue("d", new Integer("5"));
        OOEEContext oe = OOEEContextFactory.createOOEEContext();
        oe.setEvaluationContextObject("@", dec);
        oe.setParsingContextObject("@", dpc);             
        CompilationUnit unit = StatementParser.parse(statementString,oe);
        Object result = StatementEvaluator.evaluate(unit,oe);
        assertEquals(new Integer(50), oe.getEvaluationContextObject("@").getVariableValue("d"));
    }
    

    
}
