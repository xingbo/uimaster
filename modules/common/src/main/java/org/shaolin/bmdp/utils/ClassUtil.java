package org.shaolin.bmdp.utils;

/**
 * Load class from the previously set class loader
 */
public class ClassUtil {
	/**
	 * Load class from the previously set class loader. If not set, use default
	 * class loader
	 * 
	 * @param className
	 *            class name
	 * @return the loaded class
	 */
	public static Class<?> loadClass(String className)
			throws ClassNotFoundException {
		if (currentClassLoader != null) {
			try {
				return Class.forName(className, false, currentClassLoader);
			} catch (Throwable t) {
				if (t instanceof ClassNotFoundException) {
					throw (ClassNotFoundException) t;
				}
			}
		}

		return Class
				.forName(className, false, ClassUtil.class.getClassLoader());
	}

	/**
	 * Set the class loader
	 * 
	 * @param classLoader
	 *            the class loader
	 */
	public static void setCurrentClassLoader(ClassLoader classLoader) {
		currentClassLoader = classLoader;
	}

	public static boolean isIdentifier(String str) {
		if (str.length() == 0) {
			return false;
		}
		if (!Character.isJavaIdentifierStart(str.charAt(0))) {
			return false;
		}
		for (int i = 1, n = str.length(); i < n; i++) {
			if (!Character.isJavaIdentifierPart(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isQualifiedIdentifier(String str) {
		boolean isStart = true;
		for (int i = 0, n = str.length(); i < n; i++) {
			char c = str.charAt(i);
			if (isStart) {
				if (!Character.isJavaIdentifierStart(c)) {
					return false;
				}
				isStart = false;
			} else {
				if (c == '.') {
					isStart = true;
				} else if (!Character.isJavaIdentifierPart(c)) {
					return false;
				}
			}
		}
		return !isStart;
	}

	public static boolean isQualifiedEntityName(String str) {
		boolean isStart = true;
		for (int i = 0, n = str.length(); i < n; i++) {
			char c = str.charAt(i);
			if (isStart) {
				if (!Character.isJavaIdentifierPart(c) && c != '-') {
					return false;
				}
				isStart = false;
			} else {
				if (c == '.') {
					isStart = true;
				} else if (!Character.isJavaIdentifierPart(c) && c != '-') {
					return false;
				}
			}
		}
		return !isStart;
	}

	public static String getClassName(String className) {
		int index = className.lastIndexOf('.');
		if (index != -1) {
			return className.substring(index + 1);
		}
		return className;
	}

	public static String getPackageName(String className) {
		int index = className.lastIndexOf('.');
		if (index != -1) {
			return className.substring(0, index);
		}
		return null;
	}

	public static String getFullClassName(String packageName, String className) {
		if (packageName == null || packageName.length() == 0) {
			return className;
		}
		return packageName + "." + className;
	}

	private static volatile ClassLoader currentClassLoader = null;

}
