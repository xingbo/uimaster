package org.shaolin.uimaster.page.flow.nodes;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.pagediagram.OutType;
import org.shaolin.bmdp.datamodel.pagediagram.WebNodeType;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.cache.UIFlowCacheManager;
import org.shaolin.uimaster.page.exception.WebFlowException;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.javacc.WebFlowContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WebNode implements java.io.Serializable {

	private static final long serialVersionUID = 6935413842854453753L;

	private static Logger logger = LoggerFactory.getLogger(WebNode.class);

	protected boolean isParsed = false;

	protected List<ParamType> EMPTY_Param = Collections.emptyList();

	protected UIFlowCacheManager manager = UIFlowCacheManager.getInstance();

	private WebNodeType type;

	/**
	 * the WebChunk reference which contains this WebNode.
	 */
	protected WebChunk chunk = null;

	public WebNode(WebNodeType type) {
		this.type = type;
	}

	public String getName() {
		return type.getName();
	}

	public WebNodeType getType() {
		return type;
	}

	public WebFlowContext getWebFlowContext() {
		throw new UnsupportedOperationException(
				"this method should not be called, it should be overrided!");
	}

	/**
	 * get the chunk value
	 * 
	 * @return the WebChunk reference which contains this WebNode.
	 */
	public WebChunk getChunk() {
		return chunk;
	}

	public abstract void prepareInputData(HttpServletRequest request) throws ParsingException,
			EvaluationException;

	public abstract void prepareOutputData(HttpServletRequest request,
			HttpServletResponse response) throws EvaluationException,
			ParsingException;

	/**
	 * execute the node, should be overided by subclasses return the next
	 * webnode, 1. if current node is DisplayNodeType, return null 2. if current
	 * node out is dynamic(desturl), return null
	 * 
	 * @return
	 */
	public abstract WebNode execute(HttpServletRequest request,
			HttpServletResponse response) throws WebFlowException;

	/**
	 * parse current node: variables, outs, operations
	 * 
	 * @param request
	 * @throws ParsingException
	 */
	public abstract void parse() throws ParsingException;

	/**
	 * init the WebNode
	 * 
	 * @param chunk
	 *            the WebChunk reference which contains this WebNode.
	 */
	public void initWebNode(WebChunk chunk) {
		this.chunk = chunk;
		try {
			parse();
		} catch (Throwable t) {
			logger.error("Error when parse node " + toString(), t);
		}
	}

	/**
	 * set local variables:
	 * 1. if data contains the value of variable, set this value.
	 * 2. else set value of default Expression.
	 * 3. if has no default Expression, set default value.
	 * 
	 * @param context
	 * @param variables
	 * @param data
	 *            (can be null)
	 * @throws EvaluationException
	 */
	protected void setLocalVariables(WebFlowContext context,
			List<ParamType> variables, Map datas) throws EvaluationException {
		// set variable value or evaluate the defaultvalue Expressions of
		// Variables
		for (ParamType variable : variables) {
			String varName = WebflowConstants.REQUEST_PARSING_CONTEXT_PREFIX
					+ variable.getName();
			if (datas != null && datas.containsKey(variable.getName())) {
				// set mapping data
				if (logger.isDebugEnabled())
					logger.debug("set data: varName=" + varName + ", value="
							+ datas.get(variable.getName()));
				context.setVariableValue(varName, datas.get(variable.getName()));
			} else if (context.getVariableValue(varName) != null) {
				continue;
			} else {// does not contain, set default

				ExpressionType defaultValueExpr = variable.getDefault();
				if (defaultValueExpr != null) {

					if (logger.isDebugEnabled()) {
						logger.debug("set defaultValue "
								+ defaultValueExpr.getExpressionString()
								+ " for variable " + variable.getName());
					}
					Object varDefaultValue = defaultValueExpr.evaluate(context);
					context.setVariableValue(varName, varDefaultValue);
				} else { // no default value, set null
					if (logger.isDebugEnabled())
						logger.debug("set null since has no default value "
								+ varName);
					context.setVariableValue(varName, BEUtil
							.getDefaultValueOfPrimitiveType(variable.getType()
									.getEntityName()));
				}

			}// does not contain, set default
		}
	}

	/**
	 * find the specified Out in this WebNode
	 * 
	 * @param name
	 *            the name of the Out
	 * @return the specified OutType Object
	 **/
	public OutType findOut(String name) {
		if (name == null) {
			logger.error("the out name is null! " + toString());
			return null; // it's an error
		}

		for (Iterator<OutType> it = type.getOuts().iterator(); it.hasNext();) {
			OutType out = it.next();
			if (logger.isTraceEnabled()) {
				logger.trace("matching out name {}", out.getName());
			}
			if (name.equals(out.getName())) {
				return out;
			}
		}
		logger.warn("can't find the out {} in node {}", new Object[] { name,
				toString() });
		return null;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" [");
		if (chunk != null) {
			sb.append("chunkname=");
			sb.append(chunk.getEntityName());
			sb.append(", ");
		}
		sb.append("nodename=");
		sb.append(type.getName());
		sb.append("] ");
		return sb.toString();
	}

}
