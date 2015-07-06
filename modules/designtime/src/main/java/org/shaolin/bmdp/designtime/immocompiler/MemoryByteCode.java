package org.shaolin.bmdp.designtime.immocompiler;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class MemoryByteCode extends SimpleJavaFileObject {

	/**
	 * Field oStream.
	 */
	private ByteArrayOutputStream oStream;
	/**
	 * Field className.
	 */
	private final String className;

	/**
	 * Constructor for MemoryByteCode.
	 * 
	 * @param className
	 *            String
	 * @param uri
	 *            URI
	 */
	public MemoryByteCode(String className, URI uri) {
		super(uri, Kind.CLASS);
		this.className = className;
	}

	/**
	 * Method openOutputStream.
	 * 
	 * @return OutputStream * @see javax.tools.FileObject#openOutputStream()
	 */
	@Override
	public OutputStream openOutputStream() {
		oStream = new ByteArrayOutputStream();
		return oStream;
	}

	/**
	 * Method getBytes.
	 * 
	 * @return byte[]
	 */
	public byte[] getBytes() {
		return oStream.toByteArray();
	}

	/**
	 * Method getName.
	 * 
	 * @return String * @see javax.tools.FileObject#getName()
	 */
	@Override
	public String getName() {
		return className;
	}

}
