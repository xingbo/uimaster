package org.shaolin.uimaster.page.javacc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.cache.UIFlowCacheManager;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.flow.nodes.WebChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * plugin method for WebFlowContext
 */
public class WebflowContextFunction
{
    private static Logger logger = LoggerFactory.getLogger(WebflowContextFunction.class);

    public static HttpSession getSession(HttpSessionEvaluationContext context)
    {
        return context.getSession();
    }

    public static HttpServletRequest getRequest(HttpSessionEvaluationContext context)
    {
        return context.getRequest();
    }
    
    public static HttpServletResponse getResponse(HttpSessionEvaluationContext context)
    {
        return context.getResponse();   
    }

    public static void saveChunkData(HttpSessionEvaluationContext context,
                                     String key,
                                     String chunkname) throws ParsingException,
        EvaluationException
    {
        if (logger.isInfoEnabled())
            logger.info("saveChunkData(): key=" + key + ", chunkname=" + chunkname);

        WebChunk chunk = UIFlowCacheManager.getInstance().get(chunkname);

        WebFlowContext webflowContext = getWebFlowContext(chunk, context);

        List<ParamType> vars = chunk.getGlobalVariable();
        Map<String, Object> datas = new HashMap<String, Object>(vars.size());
        for (ParamType var: vars)
        {
            String varName = var.getName();
            Object value = webflowContext.getVariableValue(
                WebflowConstants.SESSION_PARSING_CONTEXT_PREFIX + varName);

            datas.put(varName, value);

            if (logger.isDebugEnabled())
            {
                logger.debug("**** varName=" + varName + ", value=" + value);
            }
        }

        //save into session
        context.getSession().setAttribute(key, datas);

    }

    public static void loadChunkData(HttpSessionEvaluationContext context,
                                     String key,
                                     String chunkname,
                                     boolean needRemove) throws
        ParsingException, EvaluationException
    {
        if (logger.isInfoEnabled())
            logger.info("loadChunkData(): key=" + key + ", chunkname=" + chunkname
                        + ", needRemove=" + needRemove);

        WebChunk chunk = UIFlowCacheManager.getInstance().get(chunkname);

        WebFlowContext webflowContext = getWebFlowContext(chunk, context);
        Map datas = (Map)context.getSession().getAttribute(key);
        if (datas == null)
        {
            logger.warn("******cannot find chunkdata: key={}, chunkname={}", 
            		new Object[] {key, chunkname});
            return;
        }

        List<ParamType> vars = chunk.getGlobalVariable();
        for (ParamType var: vars)
        {
            String varName = var.getName();
            Object value = datas.get(varName);

            webflowContext.setVariableValue(
                WebflowConstants.SESSION_PARSING_CONTEXT_PREFIX + varName,
                value);

            if (logger.isDebugEnabled())
            {
                logger.debug("**** varName={}, value={}",
                		new Object[]{varName, value});
            }
        }

        //need remove
        if (needRemove)
        {
            if (logger.isInfoEnabled())
                logger.info("remove the chunkdata: key=" + key + ", chunkname="
                            + chunkname);
            context.getSession().removeAttribute(key);
            datas.clear();
        }

    }

    private static WebFlowContext getWebFlowContext(WebChunk chunk,
        HttpSessionEvaluationContext context) throws ParsingException,
        EvaluationException
    {
    	//TODO:
        return null;

    }

}
