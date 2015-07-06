package org.shaolin.bmdp.persistence.query.generator;

import java.io.Serializable;

import org.shaolin.bmdp.persistence.query.operator.Operator;

/**
 * An element in the ExpressionNode.
 * <p>
 * An Expression defines the criteria against a single attribute. It is built
 * using three components.
 * <UL>
 * <LI>attribute
 * <LI>operator
 * <LI>value
 * </UL>
 * <p>
 * 
 * Attribute can be an oql expression. Operator can be any valid oql operator.
 * Value can be one of the following.
 * <UL>
 * <LI><code>null</code> (when operator is Operator.IS_NULL)
 * <LI>an object value (static eg, new Integer(10), "Tim"...)
 * <LI>a list of objects (when operator is Operator.IN or Operator.BETWEEN or
 * Operator.COLLECTION_CONTAINS)
 * <LI>a search query (sub query)
 * <LI>an oql expression (as in attribute)
 * </UL>
 * <p>
 * 
 * examples are given below. <CODE>
 * <p>
 * // select where last name is Smith <br>
 * new QueryExpression("lastName", Operator.EQUALS, "Smith");
 * 
 * <P>
 * // more than half of the income from salary<br>
 * new QueryExpression("salary", Operator.GREATER_THAN, income/2);
 * 
 * <P>
 * // who works in Taylor's department<br>
 * 
 * // first find Taylor's dept<br>
 * ISearchQuery aSubQuery = searchService.createObjectQuery("module.Employee");<br>
 * aSubQuery.setSelect("deptNo");                           <br>
 * QueryExpression aNameExpr = new QueryExpression("name", Operator.EQUALS, "Taylor");<br>
 * aSubQuery.setFilter(aNameExpr);                              <br>
 * 
 * //now find who works in Taylor's department<br> 
 * new QueryExpression("deptNo", Operator.EQUALS, aSubQuery);  <br>
 * 
 * </CODE>
 */
public final class QueryExpression implements Serializable {
	
	private static final long serialVersionUID = 0x49CF8C6BBFE81C22L;

	private String attribute = null;
	private Operator opr = null;
	private Object value = null;
	private boolean isExpression = false;
	private boolean reversed = false;

	private boolean valueAsParam = false;
	private boolean _has_valueAsParam = false;

	/**
	 * Constructs a simple expression with an oql expression
	 * 
	 * @param anAttribute
	 *            oql expression
	 */
	public QueryExpression(String anAttribute) {
		this(anAttribute, null);
	}

	/**
	 * Construct a simple expression with an unary operator such as
	 * {@link Operator#IS_NULL}.
	 * 
	 * @param anAttribute
	 *            the attribute name
	 * @param anOperator
	 *            the operator
	 */
	public QueryExpression(String anAttribute, Operator anOperator) {
		this(anAttribute, anOperator, null);
	}

	/**
	 * Construct a simple expression with an operator.
	 * 
	 * @param anAttribute
	 *            the attribute name
	 * @param anOperator
	 *            the operator
	 * @param aValue
	 *            the value
	 */
	public QueryExpression(String anAttribute, Operator anOperator,
			Object aValue) {
		this(anAttribute, anOperator, aValue, false);
	}

	/**
	 * Construct an expression.
	 * 
	 * @param anAttribute
	 *            the oql expression
	 * @param anOperator
	 *            the operator
	 * @param aValue
	 *            the value
	 * @param anIsExpression
	 *            <code>true</code> if the value is an expression.
	 */
	public QueryExpression(String anAttribute, Operator anOperator,
			Object aValue, boolean anIsExpression) {
		attribute = anAttribute;
		opr = anOperator;
		value = aValue;
		isExpression = anIsExpression;
	}

	/**
	 * Returns whether the value is an oql expression
	 * 
	 * @return <code>true</code> if the value is an oql expression
	 *         <code>false</code> otherwise
	 */
	public boolean isExpressionValue() {
		return isExpression;
	}

	/**
	 * Acts as bind in java.sql.PreparedStatement
	 * 
	 * @param value
	 *            value to bind
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Reverse this expression
	 */
	public void reverse() {
		reversed = !reversed;
	}

	/**
	 * Returns <code>true</code> if this expression is reversed
	 * 
	 * @return <code>true</code> if this expression is reversed
	 */
	public boolean isReversed() {
		return reversed;
	}

	/**
	 * Get the search attribute of this expression
	 * 
	 * @return the search attribute of this expression
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * Get the operator of this expression
	 * 
	 * @return the operator of this expression
	 */
	public Operator getOperator() {
		return opr;
	}

	/**
	 * Returns whether the value will be bound as a parameter
	 * 
	 * @return <code>true</code> if the value should be bound as a parameter,
	 *         <code>false</code> if the value should be in sql directly
	 */
	public boolean getValueAsParam() {
		return valueAsParam;
	}

	/**
	 * Returns whether the valueAsParam option has been set
	 * 
	 * @return <code>true</code> if the valueAsParam option has been set,
	 *         <code>false</code> otherwise
	 */
	public boolean hasValueAsParam() {
		return _has_valueAsParam;
	}

	/**
	 * Set the valueAsParam option
	 * 
	 * @param valueAsParam
	 *            <code>true</code> if the value should be bound as a parameter,
	 *            <code>false</code> if the value should be in sql directly
	 */
	public void setValueAsParam(boolean valueAsParam) {
		_has_valueAsParam = true;
		this.valueAsParam = valueAsParam;
	}

	/**
	 * Get the value to be checked in this expression
	 * 
	 * @return the value to be checked in this expression
	 */
	public Object getValue() {
		return value;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (reversed) {
			buffer.append("!");
		}
		buffer.append("(");
		buffer.append(attribute);
		buffer.append(" ");
		buffer.append(opr);
		buffer.append(" ");
		buffer.append(value);
		buffer.append(")");

		return new String(buffer);
	}
}
