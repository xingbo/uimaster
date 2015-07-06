package org.shaolin.bmdp.persistence.query.generator;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;

/**
 * Type of a field
 * 
 */
public class FieldType implements Serializable {
	/**
	 * type to object map
	 */
	private static HashMap<Integer, FieldType> typeMap 
		= new HashMap<Integer, FieldType>(8);

	// int types
	public static final int BINARY_TYPE = 0;
	public static final int BOOLEAN_TYPE = 1;
	public static final int DATETIME_TYPE = 2;
	public static final int DOUBLE_TYPE = 3;
	public static final int INT_TYPE = 4;
	public static final int LONG_TYPE = 5;
	public static final int OBJ_REF_TYPE = 6;
	public static final int STRING_TYPE = 7;

	// all field types
	public static final FieldType BINARY = new FieldType(BINARY_TYPE, "BINARY");
	public static final FieldType BOOLEAN = new FieldType(BOOLEAN_TYPE,
			"BOOLEAN");
	public static final FieldType DATETIME = new FieldType(DATETIME_TYPE,
			"DATETIME");
	public static final FieldType DOUBLE = new FieldType(DOUBLE_TYPE, "DOUBLE");
	public static final FieldType INT = new FieldType(INT_TYPE, "INT");
	public static final FieldType LONG = new FieldType(LONG_TYPE, "LONG");
	public static final FieldType OBJ_REF = new FieldType(OBJ_REF_TYPE,
			"OBJ_REF");
	public static final FieldType STRING = new FieldType(STRING_TYPE, "STRING");

	/**
	 * Get the string representation.
	 * 
	 * @return the type string
	 */
	public String toString() {
		return typeStr;
	}

	/**
	 * Returns the FieldType from the specified type
	 * 
	 * @param aType
	 *            the int version of field type.
	 * @return field type
	 */
	public static FieldType fromInt(int aType) {
		FieldType fieldType = typeMap.get(new Integer(aType));
		if (fieldType != null) {
			return fieldType;
		}
		// others are not recognized
		throw new I18NRuntimeException("Unrecognized field type :{0}",
				new Object[] { new Integer(aType) });
	}

	/**
	 * Get int version of this field type
	 * 
	 * @return int version of this field type
	 */
	public int getType() {
		return type;
	}

	/**
	 * build a field type from the given type and type string.
	 * 
	 * @param aType
	 *            the int version of field type
	 * @param aTypeStr
	 *            the type string.
	 */
	private FieldType(int aType, String aTypeStr) {
		type = aType;
		typeStr = aTypeStr;
		typeMap.put(new Integer(type), this);
	}

	/**
	 * resolve deserialized object to one of the enum objects
	 */
	private Object readResolve() throws ObjectStreamException {
		return fromInt(type);
	}

	/**
	 * The type of this field type
	 */
	private int type;

	/**
	 * The string represantation
	 */
	private transient String typeStr;

}
