package org.shaolin.bmdp.designtime.immocompiler;

import java.util.HashMap;
import java.util.Map;

public class MemoryClassLoader extends ClassLoader {

	/**
	 * Field classes.
	 */
	private final Map<String, MemoryByteCode> classes = new HashMap<String, MemoryByteCode>();
	/**
	 * Field loaded.
	 */
	private final Map<String, MemoryByteCode> loaded = new HashMap<String, MemoryByteCode>();

	
	public MemoryClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	/**
	 * Method findClass.
	 * 
	 * @param name
	 *            String
	 * @return Class<?> * @throws ClassNotFoundException
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		MemoryByteCode mbc = classes.get(name);
		if (mbc == null) {
			mbc = classes.get(name);
			if (mbc == null) {
				return super.findClass(name);
			}
		}
		return defineClass(name, mbc.getBytes(), 0, mbc.getBytes().length);
	}

	/**
	 * Method addClass.
	 * 
	 * @param mbc
	 *            MemoryByteCode
	 */
	public void addClass(MemoryByteCode mbc) {
		classes.put(mbc.getName(), mbc);
		loaded.put(mbc.getName(), mbc);
	}

	/**
	 * Method getClass.
	 * 
	 * @param name
	 *            String
	 * @return MemoryByteCode
	 */
	public MemoryByteCode getClass(String name) {
		return classes.get(name);
	}

	/**
	 * Method getLoadedClasses.
	 * 
	 * @return String[]
	 */
	public String[] getLoadedClasses() {
		return loaded.keySet().toArray(new String[loaded.size()]);
	}

	/**
	 * Method clear.
	 */
	public void clear() {
		loaded.clear();
	}

}
