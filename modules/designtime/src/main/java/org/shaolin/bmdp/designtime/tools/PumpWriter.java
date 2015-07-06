package org.shaolin.bmdp.designtime.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.bmdp.utils.StringUtil;

/**
 * Pump adapter writer
 */
public class PumpWriter extends PrintWriter {
	public PumpWriter() {
		this(new File("."));
	}

	public PumpWriter(String outDirName) {
		this(new File(outDirName));
	}

	public PumpWriter(File outDir) {
		this(new AdapterWriter(), outDir);
	}

	private PumpWriter(AdapterWriter adapterWriter, File outDir) {
		super(adapterWriter, true); // with auto flush
		adapter = adapterWriter;
		adapter.setPumpWriter(this);
		outputDir = outDir;
	}

	public void setOutputDir(String outDirPath) {
		outputDir = new File(outDirPath);
	}

	public void setOutputDir(File outDir) {
		outputDir = outDir;
	}

	public void setEncoding(String enc) {
		encoding = enc;
	}

	public void setQuiet(boolean q) {
		quiet = q;
	}

	public void setFilter(IPumpFilter filter) {
		this.filter = filter;
	}

	public void finish() {
		adapter.finish();
		printInfo();
	}

	public void close() {
		super.close();
		printInfo();
	}

	protected void finalize() {
		close();
	}

	private void printInfo() {
		if (!quiet && pumped > 0) {
			System.out.println("Pumped " + pumped + " file(s)");
		}
		pumped = 0;
	}

	private AdapterWriter adapter = null;
	private File outputDir = null;
	private String encoding = StringUtil.DEFAULT_ENCODING;
	private boolean quiet = false;
	private IPumpFilter filter = null;

	private int pumped = 0;

	private static final String FILE_TAG = "!!!!file ";
	private static final String JAVA_FILE_TAG = "!!!!javafile ";

	private static final String FILE_APPEND_TAG = "!!!!file+ ";
	private static final String JAVA_FILE_APPEND_TAG = "!!!!javafile+ ";

	private static final String DOT_JAVA = ".java";

	private static class AdapterWriter extends Writer {
		public void write(char[] cbuf, int off, int len) {
			for (int i = off, n = off + len; i < n; i++) {
				char c = cbuf[i];
				switch (c) {
				case '\r':
					skipLF = true;
					processLine();
					break;
				case '\n':
					if (skipLF) {
						skipLF = false;
					} else {
						processLine();
					}
					break;
				default:
					skipLF = false;
					buffer.append(c);
				}
			}
		}

		public void flush() {
		}

		public void close() {
			finish();
		}

		protected void finalize() {
			close();
		}

		private void processLine() {
			String line = new String(buffer);
			buffer = new StringBuffer(); // reset buffer

			IPumpFilter filter = pWriter.filter;
			if (filter != null) {
				line = filter.filter(line);
				if (line == null) {
					return;
				}
			}

			String oneLine = line.trim();
			if (oneLine.startsWith(FILE_TAG)) {
				reset(oneLine.substring(9), false);
			} else if (oneLine.startsWith(JAVA_FILE_TAG)) {
				reset(oneLine.substring(13).replace('.', File.separatorChar)
						+ DOT_JAVA, false);
			} else if (oneLine.startsWith(FILE_APPEND_TAG)) {
				reset(oneLine.substring(10), true);
			} else if (oneLine.startsWith(JAVA_FILE_APPEND_TAG)) {
				reset(oneLine.substring(14).replace('.', File.separatorChar)
						+ DOT_JAVA, true);
			} else if (currentWriter != null) {
				try {
					if (firstLine) {
						firstLine = false;
					} else {
						currentWriter.newLine();
					}
					currentWriter.write(line);
				} catch (IOException e) {
					reset(null, false);
					System.out.println("Ignored file:" + file.getAbsolutePath()
							+ ". Caused by:");
					e.printStackTrace();
				}
			}
		}

		private void finish() {
			processLine();
			reset(null, false);
		}

		private void reset(String fileName, boolean append) {
			CloseUtil.close(currentWriter);
			currentWriter = null;

			if (fileName == null) {
				return;
			}
			file = new File(fileName);
			if (!file.isAbsolute()) {
				file = new File(pWriter.outputDir, fileName);
			}
			if (file.exists() && !file.canWrite()) {
				System.out.println("Ignored read only file: "
						+ file.getAbsolutePath());
				return;
			}

			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file, append);
				currentWriter = new BufferedWriter(new OutputStreamWriter(fos,
						pWriter.encoding));
			} catch (IOException e) {
				System.out.println("Ignored file:" + file.getAbsolutePath()
						+ ". Caused by:");
				e.printStackTrace();
				return;
			} finally {
				if (currentWriter == null) {
					CloseUtil.close(fos);
				}
			}
			firstLine = !append;
			pWriter.pumped++;
		}

		private void setPumpWriter(PumpWriter pWriter) {
			this.pWriter = pWriter;
		}

		private PumpWriter pWriter = null;

		private StringBuffer buffer = new StringBuffer();
		private boolean skipLF = false;

		private BufferedWriter currentWriter = null;
		private boolean firstLine = true;
		private File file = null;

	}
}
