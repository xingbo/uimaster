package org.shaolin.bmdp.persistence.query.generator;

import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.symbol.FieldExpression;
import org.shaolin.javacc.symbol.Literal;
import org.shaolin.javacc.util.ExpressionStringBuffer;

public class OQLExpressionStringBuffer implements ExpressionStringBuffer {
	
	StringBuffer buffer = new StringBuffer();

	private FieldExpression currentFieldExpression;

	public void appendExpressionNode(ExpressionNode node) {
		node.appendToBuffer(this);
	}

	public void appendSeperator(ExpressionNode currentNode, String seperator) {
		if (SearchQueryUtil.isExpressionField(currentNode)) {
			// replace field seperator from '.' to ':'
			FieldExpression fieldExpression = (FieldExpression) currentNode;

			if (".".equals(seperator)) {
				if (currentFieldExpression != fieldExpression) {
					// only replace first '.' to ':'
					currentFieldExpression = fieldExpression;
					seperator = SearchQueryUtil.OQLFIELDSEPERATOR;
				}
			}
		} else if (currentNode instanceof Literal) {
			try {
				Class valueClass = currentNode.getValueClass();
				if (valueClass == boolean.class) {
					// replace boolean(true, false) to (0, 1)
					if ("true".equals(seperator)) {
						seperator = "1";
					} else if ("false".equals(seperator)) {
						seperator = "0";
					}
				} else if (valueClass == String.class) {
					// replace String from "" to ''
					if (seperator.startsWith("\"") && seperator.endsWith("\"")) {
						seperator = seperator.substring(1,
								seperator.length() - 1);
						seperator = "'" + seperator + "'";
					}
				}
			} catch (ParsingException ex) {
				// probably won't happen
			}
		}

		buffer.append(seperator);
	}

	public String getBufferString() {
		return buffer.toString();
	}

}
