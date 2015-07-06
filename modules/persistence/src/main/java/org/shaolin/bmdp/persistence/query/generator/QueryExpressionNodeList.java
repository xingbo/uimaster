package org.shaolin.bmdp.persistence.query.generator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.persistence.query.operator.LogicalOperator;

/**
 * A list of Expressions forming a search node.
 * 
 */
public final class QueryExpressionNodeList implements Serializable {
	
	private static final long serialVersionUID = 5783202545608648651L;

	private LinkedList<Object> node = new LinkedList<Object>();

	private boolean reversed = false;

	/**
	 * Construct a node list with a root expression.
	 * 
	 * @param anExpression
	 *            the starting expression
	 */
	public QueryExpressionNodeList(QueryExpression anExpression) {
		if (anExpression == null) {
			throw new NullPointerException("expression is null");
		}
		node.add(anExpression);
	}

	/**
	 * Construct a node list with an expression node list.
	 * 
	 * @param anExpressionNodeList
	 *            the starting expression node list
	 */
	public QueryExpressionNodeList(QueryExpressionNodeList anExpressionNodeList) {
		if (anExpressionNodeList == null) {
			throw new NullPointerException("expression list is null");
		}
		node.add(anExpressionNodeList);
	}

	/**
	 * Append an expression to the node list with the specified logical
	 * operator.
	 * 
	 * @param anExpression
	 *            the expression to append.
	 * @param anOperator
	 *            the operator to use for conneting this expression to the end
	 *            of the node.
	 */
	public void append(QueryExpression anExpression, LogicalOperator anOperator) {
		if (anExpression == null) {
			throw new I18NRuntimeException("expression is null");
		}
		if (anOperator == null) {
			throw new I18NRuntimeException("logical operator is null");
		}
		node.add(anOperator);
		node.add(anExpression);
	}

	/**
	 * Append another node list to this with the specified logical operator.
	 * 
	 * @param anExpressionList
	 *            the expression list to append.
	 * @param anOperator
	 *            the operator to use for conneting this expression to the end
	 *            of the node.
	 */
	public void append(QueryExpressionNodeList anExpressionList,
			LogicalOperator anOperator) {
		if (anExpressionList == null) {
			throw new I18NRuntimeException("expressionNodeList is null");
		}
		if (anOperator == null) {
			throw new I18NRuntimeException("logical operator is null");
		}
		node.add(anOperator);
		node.add(anExpressionList);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (reversed) {
			buffer.append("!");
		}
		buffer.append("(");
		for (int i = 0, n = node.size(); i < n; i++) {
			Object obj = node.get(i);
			if (obj == LogicalOperator.AND) {
				buffer.append("&&");
			} else if (obj == LogicalOperator.OR) {
				buffer.append("||");
			} else {
				buffer.append(obj);
			}
		}
		buffer.append(")");
		return new String(buffer);
	}

	/**
	 * List the members of this expression list.
	 * 
	 * @return Iterator of Expression or ExpressionList
	 */
	public Iterator<?> list() {
		return node.listIterator(0);
	}

	/**
	 * Reverse this expression node list
	 */
	public void reverse() {
		reversed = !reversed;
	}

	/**
	 * Returns <code>true</code> if this expression node list is reversed
	 * 
	 * @return <code>true</code> if this expression node list is reversed
	 */
	public boolean isReversed() {
		return reversed;
	}

}
