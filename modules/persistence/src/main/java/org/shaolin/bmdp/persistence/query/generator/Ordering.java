package org.shaolin.bmdp.persistence.query.generator;

//imports
import java.io.Serializable;

/**
 * Ordering of the search results.
 * 
 * <P>
 * examples are given below.<br>
 * <CODE>
 * // in the ascending order of the last name  <br>
 * aQuery.addOrdering(new Ordering("lastName")); <br>
 * 
 * <P>
 * // in the descending order of the age   <br>
 * aQuery.addOrdering(new Ordering("age", false));
 * </CODE>
 */
public class Ordering implements Serializable {
	/**
	 * Constructs an order by clause by an expression and order setting.
	 * <p>
	 * Order by expression can be field names described in {@link QueryExpression}
	 * <p>
	 * 
	 * @param anExpression
	 *            the expression to order by.
	 * @param anIsAscending
	 *            <code>true</code> if ascending, <code>false</code> otherwise.
	 */
	public Ordering(String anExpression, boolean anIsAscending) {
		this.expression = anExpression;
		this.isAscending = anIsAscending;
	}

	/**
	 * Constructs an ascending order by clause for the specified expression.
	 * <p>
	 * Order by expression can be field names described in Expression
	 * <p>
	 * 
	 * @param anExpression
	 *            the expression to order by.
	 */
	public Ordering(String anExpression) {
		this(anExpression, true);
	}

	/**
	 * Returns the order expression
	 * 
	 * @return the order expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Returns order setting(ascending or descending)
	 * 
	 * @return <code>true</code> if ascending, <code>false</code> if descending
	 */
	public boolean isAscending() {
		return isAscending;
	}

	/**
	 * set whether is asc/desc, false is descending.
	 * 
	 * @param isAscending
	 */
	public void setIsAscending(boolean isAscending) {
		this.isAscending = isAscending;
	}

	public String toString() {
		StringBuffer aBuffer = new StringBuffer(expression);
		if (isAscending) {
			aBuffer.append(ASC);
		} else {
			aBuffer.append(DESC);
		}
		return new String(aBuffer);
	}

	private String expression = null;
	private boolean isAscending = true;

	public static final String ASC = " ASC";
	public static final String DESC = " DESC";

}// Ordering
