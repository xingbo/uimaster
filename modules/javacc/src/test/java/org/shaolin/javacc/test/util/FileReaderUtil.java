package org.shaolin.javacc.test.util;

import java.io.*;

public class FileReaderUtil {
	
	public static String readFile(String file) {
		FileReader rd = null;
		try {
			rd = new FileReader(file);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(rd);
		String text = new String();
		String statementString = new String();
		while (text != null) {
			try {
				text = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (text != null) {
				statementString = statementString + "\n" + text;
			}
		}
		return statementString;
	}

	public static String readFile(InputStream in) {
		InputStreamReader rd = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(rd);
		String text = new String();
		String statementString = new String();
		while (text != null) {
			try {
				text = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (text != null) {
				statementString = statementString + "\n" + text;
			}
		}
		return statementString;
	}
}
