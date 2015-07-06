package org.shaolin.javacc.test.StatementTest.switchsmt;

import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;

public class SwitchSmtTest extends TestCase {
	public static TestSuite suite() {
		return new TestSuite(SwitchSmtTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testCase1() throws Throwable {
		InputStream path = ClassLoader
				.getSystemResourceAsStream("switchtest/switchtest1.jav");
		String statementString = FileReaderUtil.readFile(path);
		CompilationUnit unit = StatementParser.parse(statementString);
		Object result = StatementEvaluator.evaluate(unit);
		assertEquals(new Integer(6), result);
	}

	public void testCase2() throws Throwable {
		InputStream path = ClassLoader
				.getSystemResourceAsStream("switchtest/switchtest2.jav");
		String statementString = FileReaderUtil.readFile(path);
		CompilationUnit unit = StatementParser.parse(statementString);
		Object result = StatementEvaluator.evaluate(unit);
		assertEquals(new Character('C'), result);
	}

}