package org.shaolin.bmdp.designtime.immocompiler;

import java.net.URI;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class MemoryJavaFileManager extends
		ForwardingJavaFileManager<StandardJavaFileManager> {
	/**
	 * Field cl.
	 */
	private final MemoryClassLoader cl;

	/**
	 * Constructor for MemoryJavaFileManager.
	 * 
	 * @param sjfm
	 *            StandardJavaFileManager
	 * @param xcl
	 *            MemoryClassLoader
	 */
	public MemoryJavaFileManager(StandardJavaFileManager sjfm,
			MemoryClassLoader xcl) {
		super(sjfm);
		cl = xcl;
	}

	/**
	 * Method getJavaFileForOutput.
	 * 
	 * @param location
	 *            Location
	 * @param className
	 *            String
	 * @param kind
	 *            Kind
	 * @param sibling
	 *            FileObject
	 * @return JavaFileObject * @see
	 *         javax.tools.JavaFileManager#getJavaFileForOutput(Location,
	 *         String, Kind, FileObject)
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) {
		MemoryByteCode mbc = new MemoryByteCode(className.replace('/', '.')
				.replace('\\', '.'), URI.create("file:///"
				+ className.replace('.', '/').replace('\\', '/')
				+ kind.extension));
		cl.addClass(mbc);
		return mbc;
	}

	/**
	 * Method getClassLoader.
	 * 
	 * @param location
	 *            Location
	 * @return ClassLoader * @see
	 *         javax.tools.JavaFileManager#getClassLoader(Location)
	 */
	@Override
	public ClassLoader getClassLoader(Location location) {
		return cl;
	}
}
