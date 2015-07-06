package org.shaolin.uimaster.page.flow.nodes;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shaolin.bmdp.datamodel.pagediagram.DisplayNodeType;
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

public class DisplayNode extends WebNode {

	private static Logger logger = LoggerFactory.getLogger(DisplayNode.class);
	
	private final DisplayNodeType type;
	
	protected WebFlowContext inContext;
	
	public DisplayNode(DisplayNodeType type) {
		super(type);
		this.type = type;
	}
	
	public WebFlowContext getWebFlowContext() {
		return inContext;
	}
	
	/**
     * execute the node
     * always return null
     *
     *@return
     */
    public WebNode execute(HttpServletRequest request, HttpServletResponse response) 
    		throws WebFlowException
    {
		if (logger.isInfoEnabled())
			logger.info("execute():" + toString());

		try {
			// parse node
			if (!isParsed)
				parse();

			inContext.setRequest(request, response);
			// prepare input data
			prepareInputData(request);

			if (!processDirectForward(request, response)) {
				// forward error
				return null;
			}
			try {
				if (type.getOperation() != null) {
					// begine transaction if needed
					type.getOperation().evaluate(inContext);
				}
			} catch (Exception ex) {
				rollbackTransaction(inContext);
				throw new WebFlowException("Error when execute operations of display node {0}",
						ex, new Object[] { toString() });
			}
		} catch (ParsingException ex1) {
			throw new WebFlowException("ParsingException when execute display node {0}", ex1,
					new Object[] { toString() });
		} catch (EvaluationException ex1) {
			throw new WebFlowException("EvaluationException when execute display node {0}", ex1,
					new Object[] { toString() });
		}

		return null;
    }

    /**
     * parse current node: input datas, operations, outs
     * @param request
     * @throws ParsingException
     */
    public void parse() throws ParsingException
    {
        if (logger.isInfoEnabled())
            logger.info("parse Node: " + toString());

        if(isParsed) return;

        inContext = WebFlowContextHelper.getWebFlowContext(this, type.getVariables());
        //parse input datas
        ProcessHelper.parseVariables(type.getVariables(), inContext);

        //parse the operations
        if (type.getOperation() != null) {
        	type.getOperation().parse(inContext);
        }
        // the out is unsupported in the pure display node.
        isParsed = true;
    }

    /**
     * prepare InputData before execute the node
     * 1. prepare global variables of chunk
     * 2. get the datas of previous node's dataMappingtoNode from request, set for current node,
     *  if inputdata not set value, set value of defaultExpression or default value
     * @param context
     * @param variables the default value expression should be parsed
     */
    public void prepareInputData(HttpServletRequest request)
        throws ParsingException, EvaluationException
    {
        if (logger.isDebugEnabled())
            logger.debug("prepareInputData():" + toString());

        //prepare global variables of chunk
        this.getChunk().prepareGlobalVariables(request, inContext);

        //get datamappingToNode of previous node
        Map datas = (Map) request.getAttribute(WebflowConstants.OUTDATA_MAPPING2NODE_KEY);
        request.removeAttribute(WebflowConstants.OUTDATA_MAPPING2NODE_KEY);
        //if(datas == null) datas = new HashMap();//can be null
        this.setLocalVariables(inContext, type.getVariables(), datas);

    }


    /**
     * prepare Output data
     *
     * @param context
     * @param variables the default value expression should be parsed
     */
    public void prepareOutputData(HttpServletRequest request, HttpServletResponse response)
        throws //UIConvertException,
            EvaluationException,ParsingException

    {
        if (logger.isInfoEnabled())
            logger.info("execute DisplayNode.prepareOutputData(HttpServletRequest): " + toString());
        if(!isParsed) {
        	parse();
        }
        //1. init chunk
        inContext.setRequest(request, response);
        this.getChunk().prepareGlobalVariables(request, inContext);
    }

    //rollback when exception occurs
    //move to a util class?
    private void rollbackTransaction(WebFlowContext wfcontext)
    {
		if (logger.isInfoEnabled())
			logger.info("rollbackTransaction() if has transaction");
		try {
			if (wfcontext.isInTransaction()) {
				wfcontext.rollbackTransaction();
			}
		} catch (Exception e) {
			logger.error(
					"error when rollback the user transaction, execute node "
							+ toString(), e);
		}
    }

    /**
     * Forward to the specified path directly
     *
     * @param path the path that should be forwarded
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public boolean processDirectForward(HttpServletRequest request,
            HttpServletResponse response)
    {
        String path = type.getPath();
        boolean fileExisted = ProcessHelper.checkFileExisted(request, path);
        if (!fileExisted)
        {
            logger.error("cannot find the file " + path + toString());
            ProcessHelper.processResponseSendError(response,
                             HttpServletResponse.SC_NOT_FOUND,
                             "Cannot find the file " + path + toString());
            return false;
        }


        if (logger.isInfoEnabled())
            logger.info("processDirectForward(): forward to path: " + path);
        ProcessHelper.processPreForward(this, request);

        try
        {
            if (path == null || path.equals(""))
            {
                logger.error(
                    "Cannot get request dispatcher, the path is empty!" + toString());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Cannot get request dispatcher, the path is empty!"+ toString());
                return false;
            }

            //forward
            RequestDispatcher rd = request.getRequestDispatcher(path);
            if (rd == null)
            {
                logger.error("Cannot get request dispatcher for path " + path + toString());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                   "Cannot get request dispatcher for path"
                                   + path + toString());
                return false;
            }
			try {
				rd.forward(request, response);
			} catch (java.net.SocketException e) {
				logger.error("Request dispatcher forward error.", e);
			}
            return true;
        }
        catch (Exception ex)
        {//ServletException & IOException
            String msg = "Exception occurs when forward to the path "
                + path + toString();
            if(ex instanceof ServletException)
            {
                ex = ProcessHelper.transformServletException((ServletException)ex);
            }
            logger.error(msg, ex);
            
            WebflowErrorUtil.addError(request, type.getName() + ".uipage.error",
                       new WebflowError(ex.getMessage(), ex));
            ProcessHelper.processForwardError(this, request, response);
            return false;
        }
    }
	
	public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(" [");
        if(this.getChunk() != null)
        {
            sb.append("chunkname=");
            sb.append(this.getChunk().getEntityName());
            sb.append(", ");
        }
        sb.append("nodename=");
        sb.append(type.getName());
        sb.append(", type=DisplayNodeType]");
        return sb.toString();

    }
	
}
