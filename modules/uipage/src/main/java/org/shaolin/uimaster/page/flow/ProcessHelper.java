package org.shaolin.uimaster.page.flow;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.pagediagram.OutType;
import org.shaolin.bmdp.datamodel.pagediagram.WebNodeType;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.utils.SerializeUtil;
import org.shaolin.javacc.context.MultipleEvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.OpExecuteContext;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.exception.AjaxException;
import org.shaolin.uimaster.page.exception.WebFlowException;
import org.shaolin.uimaster.page.flow.nodes.WebNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessHelper
{
    //log4j
    private static Logger logger = LoggerFactory.getLogger(ProcessHelper.class);

    /**
     * Identify and return the path component (from the request URI) that
     * we will use to select an WebNode to dispatch with.  If no such
     * path can be identified, return <code>null</code>.
     *
     * @param request The servlet request we are processing
     */
    public static String processPath(HttpServletRequest request)
    {
        String path = null;

        // For prefix matching, we want to match on the path info (if any)
        path = (String) request.getAttribute("javax.servlet.include.path_info");
        if (path == null)
            path = request.getPathInfo();
        if ((path == null) || (path.length() == 0))
        {

            // For extension matching, we want to strip the extension (if any)
            path = (String) request.getAttribute("javax.servlet.include.servlet_path");
            if (path == null)
                path = request.getServletPath();
        }

        if (logger.isDebugEnabled())
            logger.debug("the path info:" + path);

        return path;
    }

    /**
     *
     * this method is not used!!!
     * Call the uihtml <code>RequestValidator</code> method of the specified
     * uihtml entity,
     * and forward back to the WebflowError page if there are any errors.  Return
     * <code>true</code> if we should continue processing (and call the
     * <code>IWebFlowAction</code> class <code>perform()</code> method), or return
     * <code>false</code> if we have already forwarded control back to the
     *  WebflowError page.
     *
     * @param srcNode the source WebNode, Display Node
     * @param destNode the destination WebNode
     * @param request The servlet request we are processing
     * @param response The servlet response we are processing
     *
     * @return true if validation is ok, otherwise false.
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public static boolean processValidate(WebNodeType srcNode,
            WebNodeType destNode,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
//        //if srcNode is logic node, not need validate
//        if(srcNode instanceof LogicNodeType) return true;
//
//        if (srcNode == null)
//            return (true);
//        logger.debug(" Validating input data");
//
//        //get the Out in source node which is associated with the destinaiton node
//        OutType out = srcNode.findOut(destNode);
//        if(out == null) return true;
//
//        WebflowErrors errors = null;
//
//        OutDataMapping[] mappings = out.getOutDataMapping();
//        for (int i = 0; i < mappings.length; i ++ )
//        {
//            //get the outputdata type of source node and the associated inputdata
//            // type of the destination node
//            PassingDataType tempOutData = srcNode.findOutputData(
//                                mappings[i].getOutDataRef());
//            DisplayOutputDataType outData = null;
//            if(tempOutData instanceof DisplayOutputDataType)
//            {
//                outData = (DisplayOutputDataType)tempOutData;
//            }
//            else
//            {
//                continue;
//            }
//           if(outData == null) continue;
//
//           if (outData.getValidate()) // need validate
//           {
//
//               logger.debug("validate:" + "type=" +
//                                      outData.getHtmlentityType() + "," + "name=" +
//                                      outData.getHtmlentityName());
//
//               //get the validator
//               RequestValidator validator =
//                       RequestValidatorFactory.createValidator(
//                       outData.getHtmlentityType(), outData.getHtmlentityName());
//
//               if (validator != null)
//               {
//                   //validate the request
//                   errors = validator.validateRequest(request);
//               }
//               else
//               {
//                   logger.error("validator is not found");
//                   errors.add("validator", new WebflowError("validatorError",
//                           "validator is not found"));
//               }
//
//
//               //merge all validat errors
//               addError(request, errors);
//
//           }
//
//        }
//
//        // is validated
//        errors = (WebflowErrors) request.getAttribute(ERROR_KEY);
//        if ((errors == null) || errors.empty())
//        {
//            logger.debug("  No errors detected, accepting input");
//            return (true);
//        }
//
//
//        //if there are errors, forward to validation error page
//        request.setAttribute(ERROR_KEY, errors);
//
//        return (false);
        return true;

    }

    /**
     * validate and convert the source node outputdata to business entity data,
     *   and store the data according to the destination node input
     *  data
     * @param srcNode the source WebNode, Display WebNode
     * @param destNode the destination WebNode
     * @param request The servlet request we are processing
     * @return the collection of business entity objects
     */
//    public static HashMap processConvert(WebNodeType srcNode,
//            WebNodeType destNode,
//            HttpServletRequest request)
//            throws UIConvertException,
//    bmiasia.ebos.odmapper.exception.ValidationException
//    {
//        if(srcNode instanceof DisplayNodeType)
//        {
//            DisplayNodeType dn = (DisplayNodeType)srcNode;
//            if(dn.getSourceEntity() != null)
//                return processConvertD((DisplayNodeType)srcNode, destNode, request);
//        }
//        return processConvertL(srcNode, destNode, request);
//    }

    /**
     * the expression should have been parsed
     * process the data mapping between the current node outdata and
     * dest node inputdata or dest chunk global variables
     * @param srcNode
     * @param out
     * @param request
     * @return
     */
    public static Map evaluateNameExpressions(MultipleEvaluationContext context,
                                              List<NameExpressionType> nes) 
         throws EvaluationException
    {
		if (nes == null) {
			logger.warn("evaluateNameExpressions(): expressions is null");
			return new HashMap();
		}
		if (context == null) {
			logger.warn("evaluateNameExpressions(): WebFlowContext is null");
			return new HashMap();
		}

        Map result = new HashMap();
        for (NameExpressionType ne: nes)
        {
            String name = ne.getName();
            ExpressionType expression = ne.getExpression();
            if(expression == null)
            {
                logger.warn("the expression is null in NameExpressionType: " +
                            name);
                continue;
            }
            
            Object value = expression.evaluate(context);
            if(name != null && !name.equals(""))
            {
                result.put(name, value);
            }

            if(logger.isDebugEnabled())
                logger.debug("evaluateNameExpressions(): name=" + name + ", expression=" +
                             expression.getExpressionString() + ", value=" + value) ;
        }

        return result;
    }

    public static void parseVariables(List<ParamType> variables, OpExecuteContext context) throws
            ParsingException
    {
        if(variables == null || context == null)
        {
            logger.warn("parseVariables(): the variables or context is null ");
            return;
        }
        //parse
        for (VariableType variable : variables)
        {
            //logger.debug("variable:" + varialbe.getName());
            ExpressionType defaultValueExpr = variable.getDefault();
            if(defaultValueExpr != null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("parse variable :" + variable.getName());
                Class varClass = VariableUtil.getVariableClass(variable);
                defaultValueExpr.parse(context);
                Class defaultValueClass = defaultValueExpr.getValueClass();
                if(!ExpressionUtil.isAssignableFrom(varClass, defaultValueClass))
                {
                    throw new ParsingException("variable " + variable.getName() +
                                               "(class " + varClass.getName() + "), default value expression " +
                                               defaultValueExpr.getExpressionString() +
                                               "(class " + defaultValueClass.getName() + ") is not compatible");
                }
            }

        }

    }

    /**
    *
    * perform some processes before forward, like: set attributes of source node.
    * @param node the source node
    * @param request the servlet request we are processing
    */
   public static void processPreForward(WebNode node, HttpServletRequest request)
   {
       if(node == null) return;
       request.setAttribute(WebflowConstants.SOURCE_CHUNK_NAME,
                            node.getChunk().getEntityName());
       request.setAttribute(WebflowConstants.SOURCE_NODE_NAME, node.getName());
       request.setAttribute(WebflowConstants.OUT_NAME, null);
   }
    
    /**
     * Forward to the specified destination, by the specified mechanism,
     * if an <code>OutType</code> instance was returned by the
     * <code>IWebFlowAction</code>.
     *
     * @param outname The out name returned by our action
     * @param node the current logic node
     * @param forms the collection of business entities we are processing
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public static void processActionForward(OutType out,
                                            WebNode node,
                                            HttpServletRequest request,
                                            HttpServletResponse response)
    //throws IOException, ServletException
    {

        if (logger.isDebugEnabled())
            logger.debug("processActionForward()");

        processPreForward(node, request);
        if (out != null)
            request.setAttribute(WebflowConstants.OUT_NAME, out.getName());
        else
            request.setAttribute(WebflowConstants.OUT_NAME, null); //""--null 2003/03/26

        String actionPath = WebConfig.replaceWebContext(WebConfig.getActionPath());
        try
        {
            if (actionPath == null || actionPath.equals(""))
            {
                logger.error("****actionPath is not set!!!, please check the webflow engine servlet is initiated or parameter \"actionPath\" is set in web.xml!!!");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                   "****actionPath is not set!!!, please check the webflow engine servlet is initiated or parameter \"actionPath\" is set in web.xml!!!");
                return;

            }

            if (logger.isInfoEnabled())
                logger.info("forward to path: " + actionPath);
            //forward
            RequestDispatcher rd =
                request.getRequestDispatcher(actionPath);
            if (rd == null)
            {
                logger.error("Cannot get request dispatcher for path"
                             + actionPath);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                   "Cannot get request dispatcher for path"
                                   + actionPath);
                return;
            }
            rd.forward(request, response);
        }
        catch (Exception ex)
        {//ServletException & IOException


            String msg = "Exception occurs when forward to the action path "
                + actionPath + node;
            if(ex instanceof ServletException)
            {
                ex = ProcessHelper.transformServletException((ServletException)ex);
            }

            logger.error(msg, ex);

            processResponseSendError(response,
                                     HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                     msg);
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
    public static void processDirectForward(
            String path,
            HttpServletRequest request,
            HttpServletResponse response)
            //throws IOException, ServletException
    {
        if (logger.isInfoEnabled())
            logger.info("processDirectForward(): forward to path: " + path);

        try
        {
            if (path == null || path.equals(""))
            {
                logger.error(
                    "Cannot get request dispatcher, the path is empty!");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Cannot get request dispatcher, the path is empty!");
                return;

            }

            if (!checkFileExisted(request, path))
            { //not existed
                logger.error("Cannot find the file " + path);
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                   "Cannot find the file " + path);
                return;
            }

            //forward
            RequestDispatcher rd =
                request.getRequestDispatcher(path);
            if (rd == null)
            {
                logger.error("Cannot get request dispatcher for path " + path);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                   "Cannot get request dispatcher for path"
                                   + path);
                return;
            }
            rd.forward(request, response);
        }
        catch (Exception ex)
        {//ServletException & IOException

            String msg = "Exception occurs when forward to the path "
                + path;
            if(ex instanceof ServletException)
            {
                ex = ProcessHelper.transformServletException((ServletException)ex);
            }

            logger.error(msg, ex);

            processResponseSendError(response,
                                     HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                     msg);
        }

    }
    
    public static void processResponseSendError(HttpServletResponse response,
			int sc, String msg) {
		try {
			response.reset();
			response.sendError(sc, msg);
		} catch (Exception ex1) {
			if (ex1 instanceof java.lang.IllegalStateException) {
				logger.warn(
						"IllegalStateException occurs when call response.sendError()",
						ex1);
			} else {
				logger.error("Error occurs when call response.sendError()", ex1);
			}
		}
	}

    /**
     * Note: this method does not convert the output data of source node or check
     * the InputData of destNode, and forward directly.
     * so the OuputData and OutDataMapping should be empty.
     *
     * forward the request to the WebflowError page of the node.
     * @param node the source node
     * @param request the servlet request we are processing
     * @param response the servlet response we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     *
     */
    public static void processForwardError(WebNode srcNode, HttpServletRequest request,
            HttpServletResponse response)
    {
        if (logger.isDebugEnabled())
            logger.debug("processForwardError()");

        //set some attributes
        ProcessHelper.processPreForward(srcNode, request);

        //there are some errors, remove the dirty data in request and session
        request.removeAttribute(WebflowConstants.OUTDATA_MAPPING2NODE_KEY);
        request.removeAttribute(WebflowConstants.OUTDATA_MAPPING2CHUNK_KEY);

            //if there are errors, forward to validation error page
        String uri = srcNode.getChunk().getErrorHandler();
        try
        {
            if (uri == null || uri.equals(""))
            {
                uri = WebConfig.replaceWebContext(WebConfig.getErrorPage());
            }
            if (uri == null || uri.equals(""))
            {
                logger.error("cannot find the error handler, "
                             + srcNode.toString());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                   "cannot find the error handler of chunk "
                                   + srcNode.toString());
                return;
            }

            if (!ProcessHelper.checkFileExisted(request, uri))
            {//not existed
                logger.error("Cannot find the error handler file " + uri
                             + ", node info:"
                             + srcNode.toString());
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                   "Cannot find the error handler file " + uri +
                                   ", node info:" + srcNode.toString());
                return;
            }

            // Save our error messages and return to the input webnode if possible
            if (logger.isInfoEnabled())
                logger.info(" error(s) occur, redirecting to: " + uri);

            RequestDispatcher rd = request.getRequestDispatcher(uri);
            if (rd == null)
            {
                logger.error("Cannot get request dispatcher for path " + uri);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                   "Cannot get request dispatcher for path "
                                   + uri);
                return;
            }
            rd.forward(request, response);
        }
        catch (Exception ex)
        {//IOException & ServletException
            String msg = "Exception occurs when forward to the ErrorHandler, node "
                + srcNode.toString() + ", ErrorHandler is " + uri;
            if(ex instanceof ServletException)
            {
                ex = ProcessHelper.transformServletException((ServletException)ex);
            }

            logger.error(msg, ex);
            ProcessHelper.processResponseSendError(response,
                                     HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                     msg);
        }

    }

    /**
     * get the chunk name.If not specified, then means in the same web chunk.
     * @param destChunkName
     * @param node
     * @return
     */
	public static String getChunkName(TargetEntityType destChunkName,
			WebNode node) {
		String chunkName = null;
		if (destChunkName != null) {
			chunkName = destChunkName.getEntityName();
		}
		if (chunkName == null || chunkName.equals("")) {
			chunkName = node.getChunk().getEntityName();
		}
		return chunkName;

	}


    public static boolean checkFileExisted(HttpServletRequest request, String url)
    {
        if(url == null || url.equals(""))
        {
            logger.warn(" the url is empty when checkFileExisted()");
            return false;
        }
        if (logger.isDebugEnabled())
            logger.debug("checkFileExisted:" + url);
        int i = url.indexOf("?");
        if(i != -1)
            url = url.substring(0, i);
        if(url == null || url.equals("")) {
        	return false;
        }

        if(url.endsWith(".jsp") || url.endsWith(".htm") || url.endsWith(".html"))
        {
            String realPath = request.getRealPath(url);
            if(realPath == null) {
            	return true;
            }
            //when web application is a war, the realPath is null!

            File f = new File(realPath);
            boolean fileExisted = f.exists();

            if(!fileExisted)
            {
                logger.error("***********the file path " + url + "(real path:"
                    + realPath + ") does not exist! ");
            }

            return fileExisted;
        }
        return true;
    }

    public static WebFlowException transformServletException(ServletException se)
    {
        StringBuffer msg = new StringBuffer();
        boolean first = true;
        Throwable t = se;
        while (se.getRootCause() != null)
        {
            if (first)
                first = false;
            else
                msg.append("--");
            msg.append("ServletException(transformed):");
            msg.append(se.getMessage());
            t = se.getRootCause();
            if (!(t instanceof ServletException))
                break;
            se = (ServletException)t;
        }
        return new WebFlowException(new String(msg), t);
    }

    /**
     * convert the parameters to attrbutes,
     * Note: only convert the parameter(i.e. webnode variable) whose type is String
     * @param resquest
     * @param node
     */
    public static void convertParameter2Attribute(HttpServletRequest request, WebNodeType node)
    {

        if (logger.isDebugEnabled()) {
            logger.debug("convertParameter2Attribute");
        }
        List<ParamType> variables = node.getVariables();
		for (ParamType var : variables) {
			TargetEntityType type = var.getType();
			if (type == null)
				continue;
			if (BEUtil.STRING.equals(type.getEntityName())) {
				String name = var.getName();
				String value = request.getParameter(name);
				if (value != null) {
					if (logger.isDebugEnabled())
						logger.debug("convertParameter2Attribute: name={}, value={}", 
								new Object[] {name, value});
					request.setAttribute(name, value);
				}
			}
		}

    }
    
    /**
     * Process all attributes changes
     * @param request
     */
    public static void processSyncValues(HttpServletRequest request) throws  AjaxException 
    {
        String syncData = request.getParameter("_sync");
        if (syncData != null && !syncData.trim().isEmpty())
        {
            try{
                AjaxContext ajaxContext = new AjaxContext(AjaxActionHelper.getAjaxWidgetMap(request.getSession()), null);
                ajaxContext.setHttpRequest(request);
                AjaxActionHelper.createAjaxContext(ajaxContext);
                JSONArray syncSets = new JSONArray(syncData);
                for (int i = 0; i < syncSets.length(); i++)
                {
                    JSONObject attr = syncSets.getJSONObject(i);
                    String uiid = attr.getString("_uiid");
                    String valueName = attr.getString("_valueName");
                    String value = attr.getString("_value");
                    String framePrefix = attr.getString("_framePrefix");
                    framePrefix = (framePrefix == null || framePrefix.equals("null")) ? "" : framePrefix;
                    Widget component = ajaxContext.getElementByAbsoluteId(uiid,framePrefix);
                    if (component == null) {
                        logger.warn("Component not found in synchronizing values: uiid=" + uiid +
                                     ", framePrefix=" + framePrefix);
                    } else {
                        component.addAttribute(valueName,value,false);
                        // constraint check is necessary.
                        component.checkConstraint();
                    }
                }
            }
            catch(JSONException ex)
            {
                throw new AjaxException("Synchronizing attributes are not working correctly. Error thrown: " + ex.getMessage(), ex);
            }
            finally
            {
                AjaxActionHelper.removeAjaxContext();
            }
        }
    }

    /**
     * Clone all the serialized object in session, only without ajax map.
     * 
     */
    public static Map backupUserObjects(HttpSession session)
    {
    	//long start = System.nanoTime();
    	Map backupValues = new HashMap();
    	Enumeration e = session.getAttributeNames();
    	while (e.hasMoreElements()) 
    	{
			String key = (String) e.nextElement();
			if(!WebflowConstants.AJAX_COMP_MAP.equals(key))
			{
			    backupValues.put(key, session.getAttribute(key));
			}
		}
    	try 
    	{
    	    return (Map)SerializeUtil.serializeClone(backupValues);
    	} 
    	catch (Exception exception) 
    	{
    	    logger.warn("This request cannot be serialized for Ajax Submit!", exception);
    	}
    	//long executedTime = (System.nanoTime() - start);
    	//System.out.println("Backup Objects executed Time: "+executedTime+", Object size: "+backupValues.size());
    	return Collections.EMPTY_MAP;
    }
    
    public static void restoreUserObjects(HttpSession session, Map backupValues)
    {
    	if(backupValues != null && backupValues.size() > 0)
    	{
    		Set set = backupValues.entrySet();
    		for (Iterator iterator = set.iterator(); iterator.hasNext();) 
    		{
    			Map.Entry entry = (Map.Entry) iterator.next();
    			session.setAttribute((String)entry.getKey(), entry.getValue());
			}
    	}
    }
    
}
