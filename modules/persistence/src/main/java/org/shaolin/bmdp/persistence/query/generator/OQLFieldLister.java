package org.shaolin.bmdp.persistence.query.generator;

import java.util.HashMap;
import java.util.Map;

import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.symbol.FieldExpression;
import org.shaolin.javacc.symbol.FieldName;
import org.shaolin.javacc.util.traverser.Traverser;

/**
 * The util class to get all possible exceptions thrown by an expression
 * 
 */
public class OQLFieldLister implements Traverser {

	private Map<String, String> fieldToVarNameMap;
	
	public OQLFieldLister() {
		fieldToVarNameMap = new HashMap<String, String>();
	}

	public void traverse(ExpressionNode node) {
		if (node instanceof FieldExpression) {
			FieldExpression fieldExpression = (FieldExpression) node;
			int index;
			int childNum = fieldExpression.getChildNum();

			StringBuffer fieldBuffer = new StringBuffer();
			StringBuffer OQLFieldBuffer = new StringBuffer();
			for (index = 0; index < childNum; index++) {
				ExpressionNode childNode = fieldExpression.getChild(index);
				if (childNode instanceof FieldName) {
					FieldName fieldNode = (FieldName) childNode;
					if (fieldNode.isField() || fieldNode.isCustomField()
							|| fieldNode.isVariableNode()) {
						// replace first "." to ":", others remain "."
						if (index == 1) {
							fieldBuffer.append(".");
							OQLFieldBuffer
									.append(SearchQueryUtil.OQLFIELDSEPERATOR);
						} else if (index > 0) {
							fieldBuffer.append(".");
							OQLFieldBuffer.append(".");
						}
						fieldBuffer.append(fieldNode.toString());
						OQLFieldBuffer.append(fieldNode.toString());
					} else {
						break;
					}
				} else {
					break;
				}
			}

			if (index > 0) {
				// replace the field with a oql field variable
				String fieldString = fieldBuffer.toString();
				String OQLFieldString = OQLFieldBuffer.toString();
				Class varClass = null;
				try {
					ExpressionNode childNode = fieldExpression
							.getChild(index - 1);
					varClass = childNode.getValueClass();
				} catch (ParsingException ex) {
					ex.printStackTrace();
					// probably won't happen
				}

				String varName;
				if (!fieldToVarNameMap.containsKey(OQLFieldString)) {
					varName = "var_" + fieldToVarNameMap.size();
					fieldToVarNameMap.put(OQLFieldString, varName);
				} else {
					varName = (String) fieldToVarNameMap.get(OQLFieldString);
				}

				OQLFieldVariable oqlVariable = new OQLFieldVariable(fieldString, varName,
						varClass);
				for (int i = 0; i < index; i++) {
					fieldExpression.removeChild(0);
				}
				fieldExpression.addChild(0, oqlVariable);
			}
		}
	}

	public Map<String, String> getFieldToVariableNameMap() {
		return fieldToVarNameMap;
	}

}
