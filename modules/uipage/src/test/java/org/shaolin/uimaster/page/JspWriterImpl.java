package org.shaolin.uimaster.page;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class JspWriterImpl extends JspWriter {

	private StringBuffer writer = new StringBuffer();

	protected JspWriterImpl() {
		super(1024, true);
	}
	
	@Override
	public void write(String str) throws IOException {
		writer.append(str);
    }
	
	@Override
	public void newLine() throws IOException {
		writer.append("\n");
	}

	@Override
	public void print(boolean b) throws IOException {
		writer.append(b);
	}

	@Override
	public void print(char c) throws IOException {
		writer.append(c);
	}

	@Override
	public void print(int i) throws IOException {
		writer.append(i);
		
	}

	@Override
	public void print(long l) throws IOException {
		writer.append(l);
	}

	@Override
	public void print(float f) throws IOException {
		writer.append(f);
	}

	@Override
	public void print(double d) throws IOException {
		writer.append(d);
	}

	@Override
	public void print(char[] s) throws IOException {
		writer.append(s);
	}

	@Override
	public void print(String s) throws IOException {
		writer.append(s);		
	}

	@Override
	public void print(Object obj) throws IOException {
		writer.append(obj.toString());	
	}

	@Override
	public void println() throws IOException {
		writer.append("\n");
	}

	@Override
	public void println(boolean x) throws IOException {
		writer.append("\n").append(x);
	}

	@Override
	public void println(char x) throws IOException {
		writer.append("\n").append(x);
	}

	@Override
	public void println(int x) throws IOException {
		writer.append("\n").append(x);
	}

	@Override
	public void println(long x) throws IOException {
		writer.append("\n").append(x);
	}

	@Override
	public void println(float x) throws IOException {
		writer.append("\n").append(x);
	}

	@Override
	public void println(double x) throws IOException {
		writer.append("\n").append(x);
	}

	@Override
	public void println(char[] x) throws IOException {
		writer.append("\n").append(x);
	}

	@Override
	public void println(String x) throws IOException {
		writer.append("\n").append(x);		
	}

	@Override
	public void println(Object x) throws IOException {
		writer.append("\n").append(x);
	}

	@Override
	public void clear() throws IOException {
		writer.setLength(0);
	}

	@Override
	public void clearBuffer() throws IOException {
		writer.setLength(0);
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public int getRemaining() {
		return 0;
	}

	@Override
	public void write(char[] arg0, int arg1, int arg2) throws IOException {
		
	}

	public String toString(){
		return writer.toString();
	}
	
}
