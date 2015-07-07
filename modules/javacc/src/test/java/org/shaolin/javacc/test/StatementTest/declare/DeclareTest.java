/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.javacc.test.StatementTest.declare;

import java.io.InputStream;
import java.util.List;

import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.test.util.FileReaderUtil;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author bobby chen
 * @version $Revision: 1.2 $ $Date: 2008/05/07 09:30:55 $
 */
public class DeclareTest extends TestCase
{
    public static TestSuite suite()
    {
        return new TestSuite(DeclareTest.class);
    }

    public void testCase1() throws Throwable
    {
    	InputStream path = ClassLoader.getSystemResourceAsStream("declares/declare1.jav");
        String statementString = FileReaderUtil.readFile(path);
        try
        { 
            StatementParser.parse(statementString);
            fail("no exception be thrown");
        }
        catch (ParsingException e)
        {
            assertTrue(true);
        }
    }
    public void testCase2() throws Throwable
    {
    	InputStream path = ClassLoader.getSystemResourceAsStream("declares/declare2.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        assertEquals(int.class, unit.getValueClass());
    }

    public void testCase3() throws Throwable
    {
    	InputStream path = ClassLoader.getSystemResourceAsStream("declares/declare3.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        assertEquals(List.class, unit.getValueClass());
    }
    
    public void testCase4() throws Throwable
    {
    	InputStream path = ClassLoader.getSystemResourceAsStream("declares/declare4.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        assertEquals(String.class, unit.getValueClass());
    }
    
    public void testCase5() throws Throwable
    {
    	InputStream path = ClassLoader.getSystemResourceAsStream("declares/declare5.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        assertEquals(Object.class, unit.getValueClass());
    }
    
    public void testCase6() throws Throwable
    {
    	InputStream path = ClassLoader.getSystemResourceAsStream("declares/declare6.jav");
        String statementString = FileReaderUtil.readFile(path);
        CompilationUnit unit = StatementParser.parse(statementString);
        assertEquals(void.class, unit.getValueClass());
    }
       
    public void testCase7() throws Throwable
    {
    	InputStream path = ClassLoader.getSystemResourceAsStream("declares/declare7.jav");
        String statementString = FileReaderUtil.readFile(path);
        try
        {
            StatementParser.parse(statementString);
            fail("no exception be thrown");
        }
        catch (ParsingException e)
        {
            assertTrue(true);
        }
    }
    
    public void testCase8() throws Throwable
    {
    	InputStream path = ClassLoader.getSystemResourceAsStream("declares/declare8.jav");
        String statementString = FileReaderUtil.readFile(path);
        try
        {
            StatementParser.parse(statementString);
            fail("no exception be thrown");
        }
        catch (ParsingException e)
        {
            assertTrue(true);
        }
    }

}
