/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page.od.mappings;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.datamodel.page.ExpressionParamType;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.od.ODContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionParam extends DataParam {

	private static final Logger logger = LoggerFactory
			.getLogger(ExpressionParam.class);

	private final ExpressionParamType type;

	public ExpressionParam(ExpressionParamType type) {
		super(type);
		this.type = type;
	}

	@Override
	public void parseDataToUI(OOEEContext ooeeContext)
			throws ParsingException {
		ExpressionType expression = type.getExpression();
		if (logger.isDebugEnabled())
			logger.debug("data to ui expression is: " + 
					expression.getExpressionString());
		expression.parse(ooeeContext);
	}

	@Override
	public void parseUIToData(OOEEContext ooeeContext, ComponentMappingType mapping)
			throws ParsingException {
		if (type.getUiToDataExpression() != null) {
			NameExpressionType nameExpr = type.getUiToDataExpression();
			ExpressionType expression = nameExpr.getExpression();
			if (expression != null) {
				expression.parse(ooeeContext);
				if (logger.isDebugEnabled())
					logger.debug("ui to data expression is: "
							+ expression.getExpressionString());
			}
		}
	}

	@Override
	public Object executeDataToUI(ODContext odContext) throws EvaluationException {
		if (logger.isDebugEnabled())
			logger.debug("evaluate expression is: "
					+ type.getExpression().getExpressionString());
		return type.getExpression().evaluate(odContext);
	}

	@Override
	public void executeUIToData(ODContext odContext, OOEEContext ooeeContext,
			ComponentMappingType mapping) throws EvaluationException {
		NameExpressionType nameExpr = type.getUiToDataExpression();
		if (nameExpr != null) {
			Object obj = nameExpr.getExpression().evaluate(ooeeContext);
			if (obj == null) {
				if (logger.isDebugEnabled())
					logger.debug("evalute result value is null, ui to data expression: "
							+ nameExpr.getExpression().getExpressionString());
				String localVariableName = nameExpr.getName();
				if (localVariableName != null && localVariableName.length() > 0) {
					EvaluationContext eConext = odContext
							.getEvaluationContextObject(ODContext.LOCAL_TAG);
					eConext.setVariableValue(localVariableName, null);
				}
			} else {
				if (obj.getClass() != Void.class) {
					String localVariableName = nameExpr.getName();
					if (localVariableName != null
							&& localVariableName.length() > 0) {
						EvaluationContext eConext = odContext
								.getEvaluationContextObject(ODContext.LOCAL_TAG);
						eConext.setVariableValue(localVariableName, obj);
					}
				}
			}
		}
	}

}