package org.shaolin.bmdp.persistence.query.generator;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ExpressionFieldValueType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FieldValueType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SimpleFieldValueType;
import org.shaolin.bmdp.persistence.query.operator.LogicalOperator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.statement.ExpressionCompilationUnit;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.symbol.FieldExpression;
import org.shaolin.javacc.symbol.FieldName;

public class SearchQueryUtil {

	public static final String OQLFIELDSEPERATOR = ":";
	public static final String VERSIONFIELD = "_version";

	public static CompilationUnit getToFieldExpression(String toDataFieldPath,
			SearchQueryParsingContext parsingContext) throws ParsingException {
		return StatementParser.parse(toDataFieldPath, parsingContext);
	}

	public static CompilationUnit getFieldValueExpression(
			FieldValueType fieldValue, SearchQueryParsingContext parsingContext)
			throws ParsingException {
		String fromExpressionString = null;

		if (fieldValue instanceof SimpleFieldValueType) {
			fromExpressionString = ((SimpleFieldValueType) fieldValue)
					.getValueFieldPath();
		} else if (fieldValue instanceof ExpressionFieldValueType) {
			ExpressionType fromExpression = ((ExpressionFieldValueType) fieldValue)
					.getExpression();
			if (fromExpression != null) {
				fromExpressionString = fromExpression.getExpressionString();
			}
		}

		if (fromExpressionString != null) {
			return StatementParser.parse(fromExpressionString, parsingContext);
		} else {
			return null;
		}
	}

	public static CompilationUnit parseExpression(ExpressionType expression,
			SearchQueryParsingContext parsingContext) throws ParsingException {
		String expressionString = null;

		if (expression != null) {
			expressionString = expression.getExpressionString();
		}

		if (expressionString != null) {
			return StatementParser.parse(expressionString, parsingContext);
		} else {
			return null;
		}
	}

	public static String getOQLExpressionString(CompilationUnit unit) {
		String OQLExpressionString = null;

		if (unit != null) {
			OQLExpressionString = getOQLString(((ExpressionCompilationUnit) unit)
					.getExpressionNode());
		}

		return OQLExpressionString;
	}

	public static String getOQLString(ExpressionNode node) {
		OQLExpressionStringBuffer buffer = new OQLExpressionStringBuffer();
		node.appendToBuffer(buffer);
		return buffer.getBufferString();
	}

	public static boolean isExpressionField(ExpressionNode node) {
		boolean isField = false;

		if (node instanceof FieldExpression) {
			FieldExpression fieldExpression = (FieldExpression) node;
			int childNum = fieldExpression.getChildNum();
			ExpressionNode lastChildNode = fieldExpression
					.getChild(childNum - 1);
			if (lastChildNode instanceof FieldName) {
				FieldName fieldNode = (FieldName) lastChildNode;
				if (fieldNode.isField() || fieldNode.isCustomField()
						|| fieldNode.isVariableNode()) {
					isField = true;
				}
			}
		}

		return isField;
	}

	public static QueryExpressionNodeList appendExpressionNodeList(
			QueryExpressionNodeList parentExpressionNodeList,
			QueryExpression childExpression, LogicalOperator operator) {
		if (childExpression != null) {
			if (parentExpressionNodeList == null) {
				parentExpressionNodeList = new QueryExpressionNodeList(
						childExpression);
			} else {
				parentExpressionNodeList.append(childExpression, operator);
			}
		}
		return parentExpressionNodeList;
	}

	public static QueryExpressionNodeList appendExpressionNodeList(
			QueryExpressionNodeList parentExpressionNodeList,
			QueryExpressionNodeList childExpressionNodeList, LogicalOperator operator) {
		if (childExpressionNodeList != null) {
			if (parentExpressionNodeList == null) {
				parentExpressionNodeList = new QueryExpressionNodeList(
						childExpressionNodeList);
			} else {
				parentExpressionNodeList.append(childExpressionNodeList,
						operator);
			}
		}
		return parentExpressionNodeList;
	}

}
