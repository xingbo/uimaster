package org.shaolin.uimaster.page.javacc;

import java.util.List;

import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.uimaster.page.OpExecuteContext;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.flow.nodes.WebNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebFlowContextHelper
{

    private static Logger logger = LoggerFactory.getLogger(WebFlowContextHelper.class);

    /**
     * Get HttpReqeustParsingContext according to the collection of variables
         * return type is DefaultParsingContext not ParsingContext, because the return
     * context will set VariableClass.
     * @param variables
     * @return
     */
    public static DefaultParsingContext getHttpRequestParsingContext(
        List<ParamType> variables)
    {
        HttpRequestParsingContext parsingContext = new HttpRequestParsingContext();
		if (variables == null) {
			return parsingContext;
		}

        for (VariableType var: variables)
        {
            Class<?> clazz = VariableUtil.getVariableClass(var);
            parsingContext.setVariableClass(var.getName(), clazz);
            if (clazz != null)
            {
                //logger.debug("variable type: " + variables[i].getType().getEntityName());
                //logger.debug("variable:" + variables[i].getName() + " is " + clazz.getName());
            }
            else
            {
                logger.warn("***********clazz is null:" + var.getName()
                            + "--type is"
                            + var.getType().getEntityName());
            }
        }
        return parsingContext;
    }

    /**
     * Get HttpSessionParsingContext according to the collection of variables
         * return type is DefaultParsingContext not ParsingContext, because the return
     * context will set VariableClass.
     * @param variables
     * @return
     */
    public static DefaultParsingContext getHttpSessionParsingContext(
    		List<ParamType> variables)
    {
        HttpSessionParsingContext parsingContext = new
            HttpSessionParsingContext();
        if (variables == null)
        {
            return parsingContext;
        }

        for (VariableType var: variables)
        {
            Class<?> clazz = VariableUtil.getVariableClass(var);
            parsingContext.setVariableClass(var.getName(), clazz);
            if (clazz == null) {
                logger.warn("***********clazz is null:" + var.getName()
                            + "--type is"
                            + var.getType().getEntityName());
            }
        }

        return parsingContext;
    }

    /**
     *
     * add @CurrentUser, external parsingContext should be MultiParsingCongtext
     * @param variables the variables of the LogicNode, or the Output Data of DisplayNode. request context
         * @param globalVariables the Global variables of the WebChunk. session context
     * @return
     */
    public static OpExecuteContext getOpParsingContext(List<ParamType> variables,
        List<ParamType> globalVariables)
    {
    	OpExecuteContext op = new OpExecuteContext();
        OOEEContext external = OOEEContextFactory.createOOEEContext();
        DefaultParsingContext global = getHttpSessionParsingContext(
            globalVariables);
        //set CurrentUser in session parsing context
        DefaultParsingContext local = getHttpRequestParsingContext(variables);
        if (logger.isDebugEnabled())
            logger.debug("local: {}", local);

        external.setParsingContextObject(WebflowConstants.
                                         REQUEST_PARSING_CONTEXT_PREFIX, local);
        external.setParsingContextObject(WebflowConstants.
                                         SESSION_PARSING_CONTEXT_PREFIX, global);

        op.setExternalParseContext(external);
        op.setParsingContextObject(WebflowConstants.
                                   REQUEST_PARSING_CONTEXT_PREFIX, local);
        op.setParsingContextObject(WebflowConstants.
                                   SESSION_PARSING_CONTEXT_PREFIX, global);

        return op;
    }

    /**
     *
     * @param request
     * @param variables the variables of the LogicNode, 
     * or the Output Data of DisplayNode. request context, for ExternalContext
     * @return
     */
    public static WebFlowContext getWebFlowContext(
        WebNode node, List<ParamType> variables)
    {
        return new WebFlowContext(node, variables);        
    }

}
