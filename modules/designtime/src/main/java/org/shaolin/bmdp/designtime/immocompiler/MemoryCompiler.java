package org.shaolin.bmdp.designtime.immocompiler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryCompiler {

	/**
	 * Field _log.
	 */
	static final Logger _log = LoggerFactory.getLogger(MemoryCompiler.class);
	/**
	 * Field javac.
	 */
	private static final JavaCompiler javac = ToolProvider
			.getSystemJavaCompiler();
	/**
	 * Field listener.
	 */
	private final DiagnosticListener<JavaFileObject> listener = new DefaultDiagnosticListener();
	/**
	 * Field fileManager.
	 */
	private final StandardJavaFileManager fileManager;
	/**
	 * Field memClassLoader.
	 */
	private final MemoryClassLoader memClassLoader;
	/**
	 * Field memFileManager.
	 */
	private final MemoryJavaFileManager memFileManager;

	public MemoryCompiler() {
		this.fileManager = javac.getStandardFileManager(null,
				Locale.getDefault(), Charset.defaultCharset());
		this.memClassLoader = new MemoryClassLoader(Thread.currentThread()
				.getContextClassLoader());
		this.memFileManager = new MemoryJavaFileManager(fileManager,
				memClassLoader);
	}

	/**
	 * Method compile.
	 * 
	 * @param files
	 *            File[]
	 * @return boolean
	 */
	public boolean compile(String classpath, String destDir, File... files) {
		List<String> options = new ArrayList<String>();
		options.add("-cp");
		options.add(classpath);
		options.add("-d");
		options.add(destDir);
		
		Writer writer = new StringWriter();
		JavaCompiler.CompilationTask compile = javac.getTask(writer,
				memFileManager, listener, options, null,
				fileManager.getJavaFileObjects(files));
		if (compile.call()) {
			return true;
		}
		return false;
	}

	public boolean compileToDisk(String classpath, String destDir, File... files) {
		List<String> options = new ArrayList<String>();
		options.add("-cp");
		options.add(classpath);
		options.add("-d");
		options.add(destDir);
		
		Writer writer = new StringWriter();
		JavaCompiler.CompilationTask compile = javac.getTask(writer,
				fileManager, listener, options, null,
				fileManager.getJavaFileObjects(files));
		if (compile.call()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method getClassLoader.
	 * 
	 * @return MemoryClassLoader
	 */
	public MemoryClassLoader getClassLoader() {
		return memClassLoader;
	}

	public void close() {
		try {
			fileManager.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 */
	private class DefaultDiagnosticListener implements
			DiagnosticListener<JavaFileObject> {
		/**
		 * Constructor for DefaultDiagnosticListener.
		 */
		public DefaultDiagnosticListener() {
		}

		/**
		 * Method report.
		 * 
		 * @param diagnostic
		 *            Diagnostic<? extends JavaFileObject>
		 * @see javax.tools.DiagnosticListener#report(Diagnostic<? extends
		 *      JavaFileObject>)
		 */
		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			throw new IllegalStateException(diagnostic.getSource().getName()
					+ (diagnostic.getPosition() == Diagnostic.NOPOS ? "" : ":"
							+ diagnostic.getLineNumber() + ","
							+ diagnostic.getColumnNumber()) + ": "
					+ diagnostic.getMessage(Locale.getDefault()));
		}
	}

}
