package org.shaolin.javacc.context;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionUtil;

/**
 */
public class DefaultCacheableContext implements ICacheableContext {
	
	private static final long serialVersionUID = 1L;
	
	private Map classObjectCache = new HashMap();
	private static final Object NOT_FOUND = new Object();
	private Set importedClass = new HashSet();

	public DefaultCacheableContext(Map map) {
		if (map == null) {
			throw new I18NRuntimeException(ExceptionConstants.EBOS_OOEE_024);
		}
		this.classObjectCache = map;
	}

	public DefaultCacheableContext() {
		this(new HashMap());
	}

	public void addImportList(Collection c) {
		importedClass.addAll(c);
	}

	public void putClassObject(String className, Class classObject) {
		if (classObject == null) {
			classObjectCache.put(className, NOT_FOUND);
			return;
		}
		classObjectCache.put(className, classObject);
	}

	public Class getClassObject(String className) throws ParsingException {
		Class result = null;
		if (className.endsWith("[]")) {
			result = getClassObject(className.substring(0,
					className.length() - 2));
			if (result != null) {
				Object arrayObject = Array.newInstance(result, 0);
				result = arrayObject.getClass();
			}
			putClassObject(className, result);
		} else {
			result = loadClass(className);
		}

		return result;
	}

	public Class loadClass(String className) throws ParsingException {
		Object result = classObjectCache.get(className);
		if (NOT_FOUND == result) {
			return null;
		}
		if (result != null) {
			return (Class) result;
		}
		Class foundClass = null;
		try {
			foundClass = ExpressionUtil.loadClass(className);
		} catch (ClassNotFoundException e) {
			// find in import list
			foundClass = getClassObjectFromImport(className);
		}

		putClassObject(className, foundClass);
		return foundClass;
	}

	private Class getClassObjectFromImport(String className)
			throws ParsingException {
		if (className.indexOf(".") != -1) {
			return null;
		}
		Class foundClass = null;
		for (Iterator i = importedClass.iterator(); i.hasNext();) {
			String packageName = (String) i.next();
			if (packageName.endsWith(".*")) {
				packageName = packageName
						.substring(0, packageName.length() - 1);
				packageName += className;
				try {
					Class lateFoundClass = ExpressionUtil
							.loadClass(packageName);
					if (foundClass == null) {
						foundClass = lateFoundClass;
					} else {
						throw new ParsingException(
								ExceptionConstants.EBOS_OOEE_056, new Object[] {
										className, foundClass.getName(),
										lateFoundClass.getName() });
					}
				} catch (ClassNotFoundException e) {
				}
			} else if (packageName.endsWith("." + className)) {
				try {
					Class lateFoundClass = ExpressionUtil
							.loadClass(packageName);
					if (foundClass == null) {
						foundClass = lateFoundClass;
					} else {
						throw new ParsingException(
								ExceptionConstants.EBOS_OOEE_056, new Object[] {
										className, foundClass.getName(),
										lateFoundClass.getName() });
					}
				} catch (ClassNotFoundException e) {
				}
			}
		}
		return foundClass;
	}
}
