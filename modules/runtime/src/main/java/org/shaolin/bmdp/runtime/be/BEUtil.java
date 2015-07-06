package org.shaolin.bmdp.runtime.be;

import java.util.HashMap;

import org.shaolin.bmdp.datamodel.bediagram.BEListType;
import org.shaolin.bmdp.datamodel.bediagram.BEMapType;
import org.shaolin.bmdp.datamodel.bediagram.BEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.BESetType;
import org.shaolin.bmdp.datamodel.bediagram.BEType;
import org.shaolin.bmdp.datamodel.bediagram.BinaryType;
import org.shaolin.bmdp.datamodel.bediagram.BooleanType;
import org.shaolin.bmdp.datamodel.bediagram.CEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.DateTimeType;
import org.shaolin.bmdp.datamodel.bediagram.DoubleType;
import org.shaolin.bmdp.datamodel.bediagram.FileType;
import org.shaolin.bmdp.datamodel.bediagram.IntType;
import org.shaolin.bmdp.datamodel.bediagram.JavaObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.LongType;
import org.shaolin.bmdp.datamodel.bediagram.StringType;
import org.shaolin.bmdp.datamodel.bediagram.TimeType;
import org.shaolin.bmdp.datamodel.common.TargetJavaType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.utils.ClassUtil;

public class BEUtil {

	/** boolean */
	public static final String BOOLEAN = "boolean";
	/** integer */
	public static final String INTEGER = "int";
	/** long */
	public static final String LONG = "long";
	/** double */
	public static final String DOUBLE = "double";
	/** string */
	public static final String STRING = "String";
	/** binary */
	public static final String BINARY = "binary";
	/** time */
	public static final String TIME = "time";
	/** datetime */
	public static final String DATETIME = "datetime";
	/** file */
	public static final String FILE = "file";
	/** set */
	public static final String SET = "set";
	/** list */
	public static final String LIST = "list";
	/** map */
	public static final String MAP = "map";

	private static HashMap<String, Class> primitiveInterfaceMap = new HashMap<String, Class>();
	private static HashMap<String, Class> primitiveClassMap = new HashMap<String, Class>();
	private static HashMap<String, String> primitiveBEMap = new HashMap<String, String>();
	private static HashMap<Class, String> primitiveBETypeMap = new HashMap<Class, String>();
	
	static {
		primitiveInterfaceMap.put(BOOLEAN, boolean.class);
		primitiveInterfaceMap.put(INTEGER, int.class);
		primitiveInterfaceMap.put(LONG, long.class);
		primitiveInterfaceMap.put(DOUBLE, double.class);
		primitiveInterfaceMap.put(BINARY, byte[].class);
		primitiveInterfaceMap.put(STRING, String.class);
		primitiveInterfaceMap.put(FILE, java.io.File.class);
		primitiveInterfaceMap.put(DATETIME, java.util.Date.class);
		primitiveInterfaceMap.put(TIME, java.sql.Time.class);
		primitiveInterfaceMap.put(SET, java.util.Set.class);
		primitiveInterfaceMap.put(LIST, java.util.List.class);
		primitiveInterfaceMap.put(MAP, java.util.Map.class);

		primitiveClassMap.put(BOOLEAN, boolean.class);
		primitiveClassMap.put(INTEGER, int.class);
		primitiveClassMap.put(LONG, long.class);
		primitiveClassMap.put(DOUBLE, double.class);
		primitiveClassMap.put(BINARY, byte[].class);
		primitiveClassMap.put(STRING, String.class);
		primitiveClassMap.put(FILE, java.io.File.class);
		primitiveClassMap.put(DATETIME, java.util.Date.class);
		primitiveClassMap.put(TIME, java.sql.Time.class);
		primitiveClassMap.put(SET, java.util.HashSet.class);
		primitiveClassMap.put(LIST, java.util.ArrayList.class);
		primitiveClassMap.put(MAP, java.util.HashMap.class);

		primitiveBEMap.put("boolean", BOOLEAN);
		primitiveBEMap.put("int", INTEGER);
		primitiveBEMap.put("long", LONG);
		primitiveBEMap.put("double", DOUBLE);
		primitiveBEMap.put("byte[]", BINARY);
		primitiveBEMap.put("java.lang.String", STRING);
		primitiveBEMap.put("java.io.File", FILE);
		primitiveBEMap.put("java.util.Date", DATETIME);
		primitiveBEMap.put("java.sql.Time", TIME);
		primitiveBEMap.put("java.util.Set", SET);
		primitiveBEMap.put("java.util.HashSet", SET);
		primitiveBEMap.put("java.util.List", LIST);
		primitiveBEMap.put("java.util.ArrayList", LIST);
		primitiveBEMap.put("java.util.Map", MAP);
		primitiveBEMap.put("java.util.HashMap", MAP);

		primitiveBETypeMap.put(BooleanType.class, BOOLEAN);
		primitiveBETypeMap.put(IntType.class, INTEGER);
		primitiveBETypeMap.put(LongType.class, LONG);
		primitiveBETypeMap.put(DoubleType.class, DOUBLE);
		primitiveBETypeMap.put(BinaryType.class, BINARY);
		primitiveBETypeMap.put(StringType.class, STRING);
		primitiveBETypeMap.put(FileType.class, FILE);
		primitiveBETypeMap.put(DateTimeType.class, DATETIME);
		primitiveBETypeMap.put(TimeType.class, TIME);
		primitiveBETypeMap.put(BESetType.class, LIST);
		primitiveBETypeMap.put(BEListType.class, LIST);
		primitiveBETypeMap.put(BEMapType.class, MAP);
	
	}

	/**
	 * Test wheter the entity is primitive type
	 * 
	 * @param entityName
	 *            type entity name
	 * @return <code>true</code> if entity is primitive type <code>false</code>
	 *         if entity is not primitive type
	 */
	public static boolean isPrimitiveType(String entityName) {
		Class<?> javaType = primitiveInterfaceMap.get(entityName);
		if (javaType != null)
			return true;
		else
			return false;
	}

	/**
	 * Get the interface of entity
	 * 
	 * @param dataType
	 *            type entity name
	 * @return the interface of entity
	 */
	public static Class<?> getPrimitiveInterfaceClass(String dataType) {
		Class<?> javaType = null;
		try {
			javaType = primitiveInterfaceMap.get(dataType);
		} catch (Exception e) {
			throw new I18NRuntimeException(ExceptionConstants.EBOS_BE_008, e,
					new Object[] { dataType });
		}

		return javaType;
	}

	/**
	 * Get the class of entity
	 * 
	 * @param dataType
	 *            type entity name
	 * @return the class of entity
	 */
	public static Class<?> getPrimitiveImplementClass(String dataType) {
		Class<?> javaType = null;
		try {
			javaType = primitiveClassMap.get(dataType);
		} catch (Exception e) {
			throw new I18NRuntimeException(ExceptionConstants.EBOS_BE_007, e,
					new Object[] { dataType });
		}

		return javaType;
	}

	/**
	 * Get an object of the specified entity
	 * 
	 * @param dataType
	 *            type entity name
	 * @return object of the specified entity
	 */
	public static Object createPrimitiveObject(String dataType) {
		if (BOOLEAN.equals(dataType)) {
			return Boolean.FALSE;
		}

		if (INTEGER.equals(dataType)) {
			return new Integer(0);
		}

		if (LONG.equals(dataType)) {
			return new Long(0);
		}

		if (DOUBLE.equals(dataType)) {
			return new Double(0.0);
		}

		if (BINARY.equals(dataType)) {
			return new byte[] {};
		}

		if (STRING.equals(dataType)) {
			return "";
		}

		if (FILE.equals(dataType)) {
			return null;
		}

		if (DATETIME.equals(dataType)) {
			return new java.util.Date(0);
		}

		if (TIME.equals(dataType)) {
			return new java.sql.Time(0);
		}

		if (SET.equals(dataType)) {
			return new java.util.HashSet();
		}

		if (LIST.equals(dataType)) {
			return new java.util.ArrayList();
		}

		if (MAP.equals(dataType)) {
			return new java.util.HashMap();
		}

		return null;
	}

	/**
	 * Get the entity name of class
	 * 
	 * @param implementClassName
	 *            class name
	 * @return entity name of the class
	 */
	public static String getBENameByPrimitiveClassName(String implementClassName) {
		return (String) primitiveBEMap.get(implementClassName);
	}

	/**
	 * Returns the default value of primitive type
	 * 
	 */
	public static Object getDefaultValueOfPrimitiveType(String dataType) {
		if (BOOLEAN.equals(dataType)) {
			return Boolean.FALSE;
		}

		if (INTEGER.equals(dataType)) {
			return new Integer(0);
		}

		if (LONG.equals(dataType)) {
			return new Long(0);
		}

		if (DOUBLE.equals(dataType)) {
			return new Double(0.0);
		}

		if (BINARY.equals(dataType)) {
			return new byte[] {};
		}

		return null;
	}

	public static Object createBEObject(String entityName) throws EntityNotFoundException {
		try {
			return Class.forName(entityName + "Impl", false, 
					Thread.currentThread().getContextClassLoader()).newInstance();
		} catch (Exception e) {
			throw new EntityNotFoundException(
					ExceptionConstants.EBOS_COMMON_002, e,
					new Object[] { entityName });
		}
	}
	
	public static Class<?> getFieldType(String entityName, String fieldName) {
		try {
			Class<?> clazz = Class.forName(entityName + "Impl", false, 
					Thread.currentThread().getContextClassLoader());
			return clazz.getDeclaredField(fieldName).getType();
		} catch (Exception e) {
			throw new EntityNotFoundException(
					ExceptionConstants.EBOS_COMMON_002, e,
					new Object[] { entityName });
		}
	}
	
	public static String getBEInterfaceClassName(String beName) {
		if (beName.lastIndexOf(".") == -1) {
			return "I" + beName;
		} else {
			String packageName = beName.substring(0, beName.lastIndexOf("."));
			String name = beName.substring(beName.lastIndexOf(".") + 1);
			return packageName + ".I" + name;
		}
	}

	public static String getBEInterfaceOnlyName(String entityName) {
		if (entityName.lastIndexOf(".") == -1) {
			return "I" + entityName;
		}
		
		String name = entityName.substring(entityName.lastIndexOf(".") + 1);
		return "I" + name;
	}
	
	public static String getBEOnlyName(String entityName) {
		if (entityName.lastIndexOf(".") == -1) {
			return entityName;
		}
		
		return entityName.substring(entityName.lastIndexOf(".") + 1);
	}
	
	public static Class getBEImplementClass(String entityName) throws ClassNotFoundException {
		String clazz = getBEImplementClassName(entityName); 
		return Class.forName(clazz, false, Thread.currentThread().getContextClassLoader());
	}
	
	public static String getBEImplementClassName(String entityName) {
		String clsName = entityName + "Impl";
		return clsName;
	}

	public static String getBEImplementOnlyName(String entityName) {
		if (entityName.lastIndexOf(".") == -1) {
			return entityName + "Impl";
		}
		
		String name = entityName.substring(entityName.lastIndexOf(".") + 1);
		return name + "Impl";
	}
	
	public static String getConstantEntityClassName(String ceName) {
		String clsName = ceName;
		return clsName;
	}
	
	public static String getJavaClassName(TargetJavaType targetJava) {
		String className = targetJava.getName();
		String packageName = targetJava.getPackageName();
		return ClassUtil.getFullClassName(packageName, className);
	}
	
	/**
	 * Get the entity name of be type
	 * 
	 * @param beType
	 *            be type
	 * @return entity name of the be type
	 */
	public static String getPrimitiveTypeEntityName(BEType beType) {
		if (beType == null)
			return null;
		return (String) primitiveBETypeMap.get(beType.getClass());
	}
	
	public static String getGenericTypes(BEType... types) {
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		for (BEType t: types) {
			String type = getGenericType(t);
			sb.append(type.substring(1, type.length()-1));
			sb.append(",");
		}
		sb.deleteCharAt(sb.length());
		sb.append(">");
		return sb.toString();
	}
	
	public static String getGenericType(BEType type) {
		if (type instanceof BEObjRefType) {
			String name = ((BEObjRefType) type).getTargetEntity().getEntityName();
			return "<" + getBEInterfaceClassName(name)+ ">";
		} else if (type instanceof CEObjRefType) {
			return "<" + ((CEObjRefType) type).getTargetEntity().getEntityName()+ ">";
		} else if (type instanceof JavaObjRefType) {
			return "<" + ((JavaObjRefType) type).getTargetJava().getName()+ ">";
		} else if (type instanceof BooleanType) {
			return "<Boolean>";
		} else if (type instanceof IntType) {
			return "<Integer>";
		} else if (type instanceof LongType) {
			return "<Long>";
		} else if (type instanceof DoubleType) {
			return "<Double>";
		} else if (type instanceof StringType) {
			return "<String>";
		} else if (type instanceof DateTimeType) {
			return "<java.util.Date>";
		} 
		return "<?>";
	}
	
	public static VariableCategoryType covert(BEType type)
			throws EntityNotFoundException {
		if (type instanceof BEObjRefType) {
			return VariableCategoryType.BUSINESS_ENTITY;
		} else if (type instanceof CEObjRefType) {
			return VariableCategoryType.CONSTANT_ENTITY;
		} else if (type instanceof JavaObjRefType) {
			return VariableCategoryType.JAVA_CLASS;
		} 
//		else if (type instanceof BooleanType) {
//			return VariableCategoryType.JAVA_PRIMITIVE;
//		} else if (type instanceof IntType) {
//			return "<Integer>";
//		} else if (type instanceof LongType) {
//			return "<Long>";
//		} else if (type instanceof DoubleType) {
//			return "<Double>";
//		} else if (type instanceof StringType) {
//			return "<String>";
//		} else if (type instanceof DateTimeType) {
//			return "<java.util.Date>";
//		} 
		return VariableCategoryType.JAVA_PRIMITIVE;
	}
}
