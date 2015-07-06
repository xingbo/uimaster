package org.shaolin.javacc.test.StatementTest.trysmt;

import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;
import org.shaolin.javacc.util.traverser.ExceptionTraverser;
import org.shaolin.javacc.util.traverser.VariableLister;

public class TryBlockTest extends TestCase {
	public static TestSuite suite() {
		return new TestSuite(TryBlockTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testCase1() throws Throwable {
		InputStream path = ClassLoader.getSystemResourceAsStream("trytest/trytest1.jav");
		String statementString = FileReaderUtil.readFile(path);
		CompilationUnit unit = StatementParser.parse(statementString);
		VariableLister lister = new VariableLister();
		unit.traverse(lister);
		assertEquals(2, lister.getAllVariables().length);
		ExceptionTraverser et = new ExceptionTraverser();
		unit.traverse(et);
		assertEquals(1, et.getExceptions().length);
	}

	public void testCase2() throws Throwable {
		InputStream path = ClassLoader.getSystemResourceAsStream("trytest/trytest2.jav");
		String statementString = FileReaderUtil.readFile(path);
		CompilationUnit unit = StatementParser.parse(statementString);
		Object result = StatementEvaluator.evaluate(unit);
		assertEquals(new String("throw exception!"), result);
	}

	public void testCase3() throws Throwable {
		InputStream path = ClassLoader.getSystemResourceAsStream("trytest/trytest3.jav");
		String statementString = FileReaderUtil.readFile(path);
		CompilationUnit unit = StatementParser.parse(statementString);
		Object result = StatementEvaluator.evaluate(unit);
		assertEquals(new String("finally return"), result);
	}

	public void testCaseForNullPointer() throws Throwable {
		try {
			InputStream path = ClassLoader.getSystemResourceAsStream("trytest/trytest4.jav");
			String statementString = FileReaderUtil.readFile(path);
			CompilationUnit unit = StatementParser.parse(statementString);
			StatementEvaluator.evaluate(unit);
		} catch (EvaluationException e) {
			System.out.println(e.getCause().getMessage());
		}
	}

}