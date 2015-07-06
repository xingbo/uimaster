package org.shaolin.uimaster.page.flow.nodes;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.pagediagram.ConditionalOutType;
import org.shaolin.bmdp.datamodel.pagediagram.InvokeWorkflowOpsType;
import org.shaolin.bmdp.datamodel.pagediagram.LogicNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.NextType;
import org.shaolin.bmdp.datamodel.pagediagram.OutType;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.EventProcessor;
import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.exception.WebFlowException;
import org.shaolin.uimaster.page.flow.ProcessHelper;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.flow.error.WebflowError;
import org.shaolin.uimaster.page.flow.error.WebflowErrorUtil;
import org.shaolin.uimaster.page.javacc.WebFlowContext;
import org.shaolin.uimaster.page.javacc.WebFlowContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicNode extends WebNode {

	private static Logger logger = LoggerFactory.getLogger(LogicNode.class);

	private final LogicNodeType type;

	protected WebFlowContext inContext;

	public LogicNode(LogicNodeType type) {
		super(type);
		this.type = type;
	}

	public WebFlowContext getWebFlowContext() {
		return inContext;
	}

	// create logicNode with default out
	public static LogicNodeType createLogicNode() {
		LogicNodeType logicNode = new LogicNodeType();
		ConditionalOutType defaultOut = new ConditionalOutType();
		defaultOut.setConditionExpression(null);
		defaultOut.setName("defaultOut");
		logicNode.getOuts().add(defaultOut);
		return logicNode;
	}

	/**
	 * execute the node 1. parse node if need 2. prepare input data 3. execute
	 * operations 4. find out, if dynamic out, forward directly 5. prepare
	 * output data 6. forward
	 * 
	 * @return if the out is dynamic(desturl), return null; else return next
	 *         node
	 */
	public WebNode execute(HttpServletRequest request,
			HttpServletResponse response) throws WebFlowException {
		if (logger.isInfoEnabled()) {
			logger.info("execute() logic node " + toString());
		}
		inContext.setRequest(request, response);
		WebNode nextNode = null;
		try {
			if (!isParsed) {
				parse();
			}
			if (type.getOperation() != null) {
				prepareInputData(request);
				type.getOperation().evaluate(inContext);
				if (type.isNeedTransaction() && inContext.isInTransaction()) {
					if (logger.isInfoEnabled())
						logger.info("commit transation");
					inContext.commitTransaction();
				}
			}
			InvokeWorkflowOpsType invokeworkflowOps = type.getInvokeworkflowOps();
			if (invokeworkflowOps != null) {
				Boolean passed = (Boolean)invokeworkflowOps.getCondition().evaluate(inContext);
				if (passed) {
					FlowEvent e = new FlowEvent(invokeworkflowOps.getEventProducer());
					List<NameExpressionType> nameExprs = invokeworkflowOps.getOutDataMappingToNodes();
					for (NameExpressionType nameExpr : nameExprs) {
						e.setAttribute(nameExpr.getName(), nameExpr.getExpression().evaluate(inContext));
					}
					EventProcessor processor = AppContext.get().getService(EventProcessor.class);
					processor.process(e);
				}
			}
		} catch (Exception ex) {
			rollbackTransaction(inContext);
			if (ex instanceof ServletException) {
				WebFlowException wfe = ProcessHelper
						.transformServletException((ServletException) ex);
				throw new WebFlowException("Servlet error when execute logic node {0} -- {1}",
						wfe.getNestedThrowable(), new Object[] { toString(),
								wfe.getMessage() });
			} else {
				throw new WebFlowException("Error when execute logic node {0}", ex,
						new Object[] { toString() });
			}
		}

		// prepare out
		ProcessHelper.processPreForward(this, request);

		// find out:
		// OpSetDynamicOut: _destnodename or _desturl
		// is dynamice out?
		if (logger.isDebugEnabled())
			logger.debug("check if dynamic out");
		String destnodename = (String) request
				.getAttribute(WebflowConstants.DEST_NODE_NAME);
		if (destnodename != null) {// it is dynamic out
			String destchunkname = (String) request
					.getAttribute(WebflowConstants.DEST_CHUNK_NAME);
			if (destchunkname == null || destchunkname.equals("")) {
				destchunkname = getChunk().getEntityName();
				request.setAttribute(WebflowConstants.DEST_CHUNK_NAME,
						destchunkname);
			}
			if (logger.isInfoEnabled())
				logger.info("it is dynamic out, destChunkName=" + destchunkname
						+ ", destNodeName=" + destnodename);
			nextNode = manager.findWebNode(destchunkname, destnodename);
			if (nextNode == null) {
				throw new WebFlowException("Cannot find web node: chunkname={0}, nodename={1} for dynamice out of node{2}",
						new Object[] { destchunkname, destnodename, toString() });
			} else {
				request.setAttribute(WebflowConstants.DEST_NODE_NAME, null);
				request.setAttribute(WebflowConstants.DEST_CHUNK_NAME, null);
				return nextNode;
			}

		}
		String desturl = (String) request
				.getAttribute(WebflowConstants.DEST_URL);
		if (desturl != null) {// it is dynamic out

			if (logger.isInfoEnabled())
				logger.info("it is dynamic out, destURL=" + desturl);
			// forward
			ProcessHelper.processDirectForward(desturl, request, response);
			return null;
		}

		// find Conditional Out
		OutType out = null;
		try {
			List<OutType> outs = type.getOuts();
			for (OutType o : outs) {
				if (!(o instanceof ConditionalOutType)) {
					logger.warn(
							"not ConditionalOutTye--this is a default out, logic node {}, out={}",
							new Object[] { toString(), o.getName() });
					continue;
				}

				ConditionalOutType conditionalOut = (ConditionalOutType) o;
				if (conditionalOut.getConditionExpression() == null) {
					if (logger.isInfoEnabled())
						logger.info(
								"ConditionalOutType--this is a default out, node {}, out={}",
								new Object[] { toString(),
										conditionalOut.getName() });
					out = conditionalOut;
					continue;
				}

				Object flag = conditionalOut.getConditionExpression().evaluate(
						inContext);
				if (((Boolean) flag).booleanValue()) {
					if (logger.isDebugEnabled())
						logger.debug("the out is selected, node" + toString()
								+ " , out=" + conditionalOut.getName());
					out = conditionalOut;
					break;
				}
			}
		} catch (EvaluationException ex4) {
			throw new WebFlowException("EvaluationException when find Out, execute logic node {0}", ex4,
					new Object[] { toString() });
		}
		// forward to out
		if (out != null) {
			try {
				if (out.getNext() == null) {
					logger.error("the next is null, out={} logicnode {}",
							new Object[] { out.getName(), toString() });

					WebflowErrorUtil.addError(request, type.getName()
							+ ".out.error", new WebflowError(
							"the next is null, out= " + out.getName()
									+ " logicnode " + toString()));
					ProcessHelper.processForwardError(this, request, response);
					nextNode = null;
				} else {
					prepareOutputData(request, out);
					nextNode = manager.findNextWebNode(this, out.getNext());
					if (nextNode == null) {
						throw new WebFlowException("Cannot find web node: chunkname={0}, nodename={1} for the out {2}", 
								new Object[] {
										this.getChunk().getEntityName(),
										out.getNext().getDestNode(),
										out.getName(), toString() });
					}
				}
			} catch (EvaluationException ex2) {
				throw new WebFlowException("Evaluation error when process datamapping for out[{0}],execute logicnode {1}", 
						ex2, new Object[] { out.getName(), type.toString() });
			} catch (ParsingException ex2) {
				throw new WebFlowException("ParsingException when process datamapping for out{0},execute logicnode{1}", 
						ex2, new Object[] { out.getName(), type.toString() });
			}

		} else {
			logger.error(
					"no matched out(the value of conditionExpression is true) found, logicnode {}",
					toString());
			WebflowErrorUtil.addError(request, type.getName() + ".out.error",
					new WebflowError("can not find the out, logicnode "
							+ toString()));

			ProcessHelper.processForwardError(this, request, response);
			nextNode = null;

		}

		return nextNode;
	}

	/**
	 * parse current node: variables, operations, outs
	 * 
	 * @param request
	 * @throws ParsingException
	 */
	public void parse() throws ParsingException {
		if (isParsed)
			return;

		if (logger.isInfoEnabled())
			logger.info("parse LogicNode: " + toString());

		inContext = WebFlowContextHelper.getWebFlowContext(this,
				type.getVariables());
		// parse variables
		ProcessHelper.parseVariables(type.getVariables(), inContext);

		// parse the operations
		if (type.getOperation() != null) {
			type.getOperation().parse(inContext);
		}

		// parse outs
		for (Iterator<OutType> i = type.getOuts().iterator(); i.hasNext();) {
			OutType out = i.next();
			if (out instanceof ConditionalOutType) {
				ConditionalOutType conditionOut = (ConditionalOutType) out;
				if (conditionOut.getConditionExpression() != null) {
					conditionOut.getConditionExpression().parse(
							inContext);
				}
				NextType next = conditionOut.getNext();
				if (next == null) {
					throw new IllegalStateException("the next is null! out="
							+ out.getName() + ", node " + toString());
				}
				for (NameExpressionType type: next.getOutDataMappingToNodes()) {
					type.getExpression().parse(inContext);
				}
			} 
			if (out.getMappings() != null) {
				for (NameExpressionType type: out.getMappings()) {
					type.getExpression().parse(inContext);
				}
			}
		}

		isParsed = true;

	}

	/**
	 * prepare InputData before execute the node 1. prepare global variables of
	 * chunk 2. get the datas of previous node's dataMappingtoNode from request,
	 * set for current node, if variable not set value, set default value or
	 * null
	 * 
	 * @param context
	 * @param variables
	 *            the default value expression should be parsed
	 */
	public void prepareInputData(HttpServletRequest request)
			throws ParsingException, EvaluationException {
		if (logger.isDebugEnabled())
			logger.debug("prepareInputData():" + toString());

		// prepare global variables of chunk
		this.getChunk().prepareGlobalVariables(request, inContext);

		// get datamappingToNode of previous node
		Map datas = (Map) request
				.getAttribute(WebflowConstants.OUTDATA_MAPPING2NODE_KEY);
		request.removeAttribute(WebflowConstants.OUTDATA_MAPPING2NODE_KEY);
		// if(datas == null) datas = new HashMap();//can be null
		this.setLocalVariables(inContext, type.getVariables(), datas);
	}

	/**
	 * prepare Output data 1. evaluate NameExpressions outDataMappingToNode, and
	 * set result in request 2. evaluate NameExpressions outDataMappingToChunk,
	 * and set result in session 3. remove outputdata of the node from request;
	 * 4. (Postpone) remove global variables from session only when destchunk is
	 * different from current chunk
	 * 
	 * @param context
	 * @param variables
	 *            the default value expression should be parsed
	 */
	public void prepareOutputData(HttpServletRequest request, OutType out)
			throws ParsingException, EvaluationException {
		if (logger.isDebugEnabled())
			logger.debug("prepareOutputData():" + toString());
		if (out == null) {
			logger.error("prepareOutputData():the out is null, node"
					+ toString());
			return;
		}
		NextType next = out.getNext();
		// 1. evaluate NameExpressions outDataMappingToNode, and set result in
		// request
		Map datas = ProcessHelper.evaluateNameExpressions(inContext,
				next.getOutDataMappingToNodes());
		if (datas == null || datas.size() == 0) {
			request.setAttribute(WebflowConstants.OUTDATA_MAPPING2NODE_KEY,
					null);
		} else {
			request.setAttribute(WebflowConstants.OUTDATA_MAPPING2NODE_KEY,
					datas);
		}
		// 2.evaluate NameExpressions outDataMappingToChunk, and set result in
		// session
		HttpSession session = request.getSession(true);
		datas = ProcessHelper.evaluateNameExpressions(inContext,
				next.getOutDataMappingToNodes());
		if (datas == null || datas.size() == 0) {
			request.setAttribute(WebflowConstants.OUTDATA_MAPPING2CHUNK_KEY,
					null);
		} else {
			request.setAttribute(WebflowConstants.OUTDATA_MAPPING2CHUNK_KEY,
					datas);
		}
		// 3. remove outputdata of the node from request;
		for (Iterator i = type.getVariables().iterator(); i.hasNext();) {
			request.removeAttribute(((VariableType) i.next()).getName());
		}
	}
	
	@Override
	public void prepareOutputData(HttpServletRequest request,
			HttpServletResponse response) throws EvaluationException,
			ParsingException {
		
	}

	/**
	 * check the user's permission
	 */
	protected void checkPermission(String userId)// throws NoSuchUserException,
													// NoPermissionException,
													// NoSuchPermissionException
	{
		if (logger.isDebugEnabled())
			logger.debug("checkPermission()");

		// init permission
		// if(ppk == null)
		// {
		// initPermission();
		// }
		//
		// if(userId == null)
		// {
		// SecurityUtil.getSecurityManager().isEveryonePermission(ppk);
		// logger.debug("is everyone permission");
		// return;
		// }
		// logger.debug("userId:" + userId);
		//
		// SecurityUtil.getSecurityManager().checkPermission(userId, ppk);

	}

	// rollback when exception occurs
	private void rollbackTransaction(WebFlowContext wfcontext) {
		if (wfcontext == null) {
			if (logger.isInfoEnabled())
				logger.info("the WebFlowContext is null");
			return;
		}
		if (logger.isInfoEnabled())
			logger.info("rollbackTransaction() if has transaction");
		try {
			if (wfcontext.isInTransaction()) {
				wfcontext.rollbackTransaction();
			}
		} catch (Exception e) {
			logger.error(
					"error when rollback the user transaction, execute logic node "
							+ toString(), e);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" [");
		if (this.getChunk() != null) {
			sb.append("chunkname=");
			sb.append(this.getChunk().getEntityName());
			sb.append(", ");
		}
		sb.append("nodename=");
		sb.append(type.getName());
		sb.append(", type=LogicNodeType]");
		return sb.toString();
	}

}
