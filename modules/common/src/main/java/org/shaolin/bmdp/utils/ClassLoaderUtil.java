package org.shaolin.bmdp.utils;

/**
 * Load class from the previously set class loader
 */
public class ClassLoaderUtil
{
	private static ClassLoader currentClassLoader;
	
    /**
     * Load class from the previously set class loader. If not set, use
     * default class loader
     *
     * @param       className       class name
     * @return      the loaded class
     */
	public static Class<?> loadClass(String className)
			throws ClassNotFoundException {
		ClassLoader loader = currentClassLoader;
		if (loader == null) {
			loader = Thread.currentThread().getContextClassLoader();
		}
		try {
			return Class.forName(className, false, loader);
		} catch (ClassNotFoundException e) {
			ClassLoader contextLoader = Thread.currentThread()
					.getContextClassLoader();
			if (contextLoader != loader) {
				return Class.forName(className, false, contextLoader);
			}
			throw e;
		}
	}

    /**
     * Set the class loader
     *
     * @param       classLoader     the class loader
     */
	public static void setCurrentClassLoader(ClassLoader classLoader) {
		currentClassLoader = classLoader;
	}

}
