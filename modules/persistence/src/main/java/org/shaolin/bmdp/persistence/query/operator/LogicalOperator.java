package org.shaolin.bmdp.persistence.query.operator;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;

/**
 * A logical operator is used to combine the results of two component conditions
 * to produce a single result based on them
 * 
 */
public class LogicalOperator implements Serializable {

	/**
	 * name to object map
	 */
	private static HashMap<String, LogicalOperator> nameMap = new HashMap<String, LogicalOperator>(2);

	/**
	 * returns TRUE if both component conditions are TRUE; otherwise returns
	 * FALSE.
	 */
	public static final LogicalOperator AND = new LogicalOperator("AND");

	/**
	 * returns TRUE if any one of the component conditions are TRUE; otherwise
	 * returns FALSE.
	 */
	public static final LogicalOperator OR = new LogicalOperator("OR");
	
	/**
	 * The name of this logical operator
	 */
	private String type;

	/**
	 * Returns the logical operator from the specified string
	 * 
	 * @param anLogicalOperator
	 *            the string version of logical operator.
	 * @return logical operator
	 */
	public static LogicalOperator fromString(String anLogicalOperator) {
		if (anLogicalOperator != null) {
			anLogicalOperator = anLogicalOperator.toUpperCase();
		}
		LogicalOperator op = (LogicalOperator) nameMap.get(anLogicalOperator);
		if (op != null) {
			return op;
		}
		// others are not recognized
		throw new I18NRuntimeException("Unrecognized logical operator:{0}",
				new Object[] { anLogicalOperator });
	}

	/**
	 * build an Logical operator from the given string.
	 * 
	 * @param aType
	 *            the type string.
	 */
	private LogicalOperator(String aType) {
		type = aType;
		nameMap.put(type, this);
	}

	/**
	 * resolve deserialized object to one of the enum objects
	 */
	private Object readResolve() throws ObjectStreamException {
		return fromString(type);
	}
	
	public String getJavaSymbol() {
		if ("AND".equals(type)) {
			return "&&";
		} else if ("OR".equals(type)) {
			return "||";
		}
		return "&&";
	}


	/**
	 * Get the string representation.
	 * 
	 * @return the type
	 */
	public String toString() {
		return type;
	}

}
