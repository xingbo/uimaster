package org.shaolin.bmdp.persistence.query.operator;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;

/**
 * An operator used in a search expression.
 */
public class Operator implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * operator id.
	 */
	private String id;

	/**
	 * <code>true</code> if this is a reversed operator
	 */
	private boolean isReversed;

	/**
	 * Reversed operator of this operator
	 */
	private Operator reverseOp = null;
	
	/**
	 * name to object map
	 */
	private static HashMap<String, Operator> nameMap = new HashMap<String, Operator>(14);

	/**
	 * compares an expression for null value.
	 */
	public static final Operator IS_NULL = new Operator("IS NULL");

	/**
	 * compares two expressions for equality (case sensitive). Requires a value
	 * for the expression to compare against.
	 * <p>
	 * To check whether the value is null, <b>DO NOT</b> use this operator, use
	 * {@link #IS_NULL} instead.
	 */
	public static final Operator EQUALS = new Operator("=");

	/**
	 * compares two string expressions for equality (case insensitive). Requires
	 * a value for the expression to compare against.
	 */
	public static final Operator EQUALS_IGNORE_CASE = new Operator("=IC");

	/**
	 * Checks whether the attribute is less than the expression value.
	 */
	public static final Operator LESS_THAN = new Operator("<");

	/**
	 * Checks whether the attribute is greater than the expression value.
	 */
	public static final Operator GREATER_THAN = new Operator(">");

	/**
	 * Checks whether the attribute is less than or equals to the expression
	 * value.
	 */
	public static final Operator LESS_THAN_OR_EQUALS = new Operator("<=");

	/**
	 * Checks whether the attribute is greater than or equals to the expression
	 * value.
	 */
	public static final Operator GREATER_THAN_OR_EQUALS = new Operator(">=");

	/**
	 * Checks whether the attribute is start with the expression value (case
	 * sensitive)
	 */
	public static final Operator START_WITH = new Operator("LIKE %%");

	/**
	 * Checks whether the attribute is start with the left expression value.
	 */
	public static final Operator START_WITH_LEFT = new Operator("LIKE *%");

	/**
	 * Checks whether the attribute is start with the right expression value.
	 */
	public static final Operator START_WITH_RIGHT = new Operator("LIKE %*");
	
	/**
	 * Checks whether the attribute is start with the expression value (case
	 * insensitive)
	 */
	public static final Operator START_WITH_IGNORE_CASE = new Operator(
			"START WITH IC");

	/**
	 * Complete word search (case sensitive). Only words separated by wild
	 * characters are compared against.
	 */
	public static final Operator CONTAINS_WORD = new Operator("CONTAINS");

	/**
	 * Complete word search (case insensitive). Only words separated by wild
	 * characters are compared against.
	 */
	public static final Operator CONTAINS_WORD_IGNORE_CASE = new Operator(
			"CONTAINS IC");

	/**
	 * Partial word search (case sensitive).
	 */
	public static final Operator CONTAINS_PARTIAL = new Operator(
			"CONTAINS PARTIAL");

	/**
	 * Partial word search (case insensitive).
	 */
	public static final Operator CONTAINS_PARTIAL_IGNORE_CASE = new Operator(
			"CONTAINS PARTIAL IC");

	/**
	 * Collection contains one of the values.
	 */
	public static final Operator COLLECTION_CONTAINS = new Operator(
			"COLLECTION CONTAINS");

	/**
	 * Is the attribute one of the specified values
	 */
	public static final Operator IN = new Operator("IN");

	/**
	 * Is the attribute between the values specified
	 */
	public static final Operator BETWEEN = new Operator("BETWEEN");

	/**
	 * Is the sub query's result not empty
	 */
	public static final Operator EXISTS = new Operator("EXISTS");

	/**
	 * Get the string version of the operator.
	 * 
	 * @return String format of the operator.
	 */
	public String toString() {
		return isReversed ? "NOT " + id : id;
	}

	/**
	 * Returns the operator from the specified string
	 * 
	 * @param anOperator
	 *            the string version of operator.
	 * @return operator
	 */
	public static Operator fromString(String anOperator) {
		if (anOperator != null) {
			anOperator = anOperator.toUpperCase();
		}
		Operator op = (Operator) nameMap.get(anOperator);
		if (op != null) {
			return op;
		}
		// others are not recognized
		throw new I18NRuntimeException("Unrecognized operator:{0}",
				new Object[] { anOperator });
	}

	/**
	 * Reverse this operator
	 * 
	 * @return the reversed operator of this operator
	 */
	public Operator reverse() {
		return reverseOp;
	}

	/**
	 * Construct an operator with operator id.
	 * 
	 * @param anId
	 *            operator id.
	 */
	private Operator(String anId) {
		this(anId, false);
	}

	/**
	 * Construct an operator with operator id and isReversed.
	 * 
	 * @param anId
	 *            operator id.
	 * @param anIsReversed
	 *            <code>true</code> if this is a reversed operator
	 */
	private Operator(String anId, boolean anIsReversed) {
		id = anId;
		isReversed = anIsReversed;

		if (!anIsReversed) {
			// create reversed operator
			reverseOp = new Operator(id, true);
			reverseOp.reverseOp = this;
			nameMap.put(id, this);
		}
	}

	/**
	 * resolve deserialized object to one of the enum objects
	 */
	private Object readResolve() throws ObjectStreamException {
		Operator op = fromString(id);
		return isReversed ? op.reverseOp : op;
	}

	
}// Operator
