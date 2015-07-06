package org.shaolin.uimaster.page.flow.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.pagediagram.DisplayNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.LogicNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.PageNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.WebNodeType;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.OpExecuteContext;
import org.shaolin.uimaster.page.flow.ProcessHelper;
import org.shaolin.uimaster.page.flow.WebFlowUtil;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.javacc.WebFlowContext;
import org.shaolin.uimaster.page.javacc.WebFlowContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebChunk implements java.io.Serializable {

	private static final long serialVersionUID = -8696724029040136397L;

	private static Logger logger = LoggerFactory.getLogger(WebChunk.class);
	
	private final org.shaolin.bmdp.datamodel.pagediagram.WebChunk type;

	private final List<WebNode> webNodes;
	
	public WebChunk(org.shaolin.bmdp.datamodel.pagediagram.WebChunk type) throws ParsingException {
		this.type = type;
		this.webNodes = new ArrayList<WebNode>(type.getWebNodes().size());
		
		for (Iterator<WebNodeType> it = type.getWebNodes().iterator(); it.hasNext();) {
			addWebNode0(it.next());
		}
	}

	private void addWebNode0(WebNodeType webNode) throws ParsingException {
		if (webNode instanceof PageNodeType) {
			this.webNodes.add(new UIPageNode((PageNodeType)webNode));
		} else if (webNode instanceof DisplayNodeType) {
			this.webNodes.add(new DisplayNode((DisplayNodeType)webNode));
		} else if (webNode instanceof LogicNodeType) {
			this.webNodes.add(new LogicNode((LogicNodeType)webNode));
		} else {
			throw new ParsingException("Unsupported web node: " + webNode.toString());
		}
	}
	
	public List<WebNode> getWebNodes(){
		return this.webNodes;
	}
	
	public String getEntityName() {
		return type.getEntityName();
	}

	public String getErrorHandler() {
        return type.getErrorHandler();
    } 
	
	public List<ParamType> getGlobalVariable() {
		return type.getGlobalVariables();
	}
	
	public void removeWebNode(String nodeName) {
		synchronized (type.getWebNodes()) {
			for (Iterator<WebNodeType> it = type.getWebNodes().iterator(); it
					.hasNext();) {
				WebNodeType node = it.next();
				if (node.getName().equals(nodeName)) {
					it.remove();
				}
			}
		}
	}

    /**
     *  init all the WebNodes in this WebChunk and parse the chunk
     *
     */
    public void initChunk()
    {
		if (logger.isInfoEnabled()) {
			logger.info("Load page flow:" + type.getEntityName());
		}
		try {
			parse(); // parse the chunk
		} catch (ParsingException ex) {
			logger.error("ParsingError when parse global variables in chunk"
					+ type.getEntityName(), ex);
		}

		for (WebNode chunk : webNodes) {
			chunk.initWebNode(this);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Loaded page flow:" + type.getEntityName());
		}
    }

    /**
     * find the specified WebNode in this WebChunk
     *
     * @param name the name of the WebNode
     * @return the specified WebNode Object
    **/
    public WebNode findWebNode(String name)
    {
		for (WebNode node : webNodes) {
			if (name.equals(node.getName())) {
				return node;
			}
		}
        logger.error("can not find web node \"{}\" in webchunk {}",
        		new Object[]{name, type.getEntityName()});
        return null;
    }

    private boolean isParsed = false;

    /**
     * parse the chunk
     * parse the default Expression of Global Variables
     * @param context
     * @throws ParsingException
     */
    private void parse() throws ParsingException
    {
        if(isParsed) return;

        if (logger.isDebugEnabled())
            logger.debug("parse WebChunk " + type.getEntityName());

        List<ParamType> variables = Collections.emptyList();
        OpExecuteContext context = WebFlowContextHelper.getOpParsingContext(
        		variables, type.getGlobalVariables());

        //parse
        ProcessHelper.parseVariables(type.getGlobalVariables(), context);

        isParsed = true;
    }

    /**
     *  prepare global variables
     * 1. get the datas of previous chunk's dataMappingtoChunk from session, set for current chunk,
     * 2. set current chunkname
     * 3. if need init but the mapping doesnot contain the variable
     *    a set value of default Expression
     *    b. if has no default Expression, set default value
     *
     * @param request
     * @param context
     * @throws EvaluationException
     */
    public void prepareGlobalVariables(HttpServletRequest request,
                                       WebFlowContext context) throws
        ParsingException, EvaluationException
    {
        if (!isParsed)
        { //parse
            parse();
        }

        if (logger.isDebugEnabled())
            logger.debug("prepareGlobalVariables():" + type.getEntityName());
        HttpSession session = request.getSession(true);

        //init each chunk only once
        initGlobalVariables(context, session);

        //get datamappingToChunk of previous chunk
        Map datas = (Map)request.getAttribute(WebflowConstants.
                                              OUTDATA_MAPPING2CHUNK_KEY);
        //for performance we use null not empty map.
        //if(datas == null) datas = new HashMap();
        request.removeAttribute(WebflowConstants.OUTDATA_MAPPING2CHUNK_KEY);

        //data mapping to chunk
        if(datas == null)
            return;
        for(Iterator it = datas.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if(logger.isDebugEnabled())
                logger.debug("prepareGlobalVariables(): set mapping data for global variable, name= "
                         + key + ", value=" + value);
            context.setVariableValue(WebflowConstants.SESSION_PARSING_CONTEXT_PREFIX
                                     + key, value);
        }
    }

    /**
     *  init each chunk only Once:
     *    a set value of default Expression
     *    b. if has no default Expression, set default value
     *
     * @param context
     * @param session
     * @throws EvaluationException
     */
    private void initGlobalVariables(WebFlowContext context, HttpSession session)
        throws ParsingException, EvaluationException
    {
        List list = (List)session.getAttribute(
            WebflowConstants.INITIATED_CHUNKS_KEY);
        if (list == null)
        {
            list = new ArrayList();
            session.setAttribute(WebflowConstants.INITIATED_CHUNKS_KEY,
                                 list);
        }
        //init each chunk only Once
        if(list.contains(type.getEntityName())) return;
        list.add(type.getEntityName());

        if (logger.isDebugEnabled()) {
            logger.debug("init global variables for webchunk:" + type.getEntityName());
        }

        for (Iterator i = type.getGlobalVariables().iterator(); i.hasNext(); )
        {
            VariableType variable = (VariableType)i.next();
            String varName = WebflowConstants.
                SESSION_PARSING_CONTEXT_PREFIX + variable.getName();
            if (WebFlowUtil.isBuildInVariables(variable.getName()))
            { //donot init build-in variables
                continue;
            }
            else if (context.getVariableValue(varName) != null)
            { //
                logger.warn("the global variable " + varName +
                            " is not null in session before init, do not init it!" );
                continue;
            }
            else
            { //set default when need init chunk
                ExpressionType defaultValueExpr = variable.getDefault();
                if (defaultValueExpr != null)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("prepareGlobalVariables(): set defaultValue "
                                 + defaultValueExpr.getExpressionString()
                                 + " for variable " + variable.getName());
                    }
                    Object varDefaultValue = defaultValueExpr.evaluate(
                        context);
                    context.setVariableValue(varName, varDefaultValue);
                }
                else
                { //no default valueExpression, set default value
                    if (logger.isDebugEnabled())
                        logger.debug("set default value with BEUtil.getDefaultValueOfPrimitiveType() since has no default valueExpression "
                                     + varName);
                    context.setVariableValue(varName,
                                             BEUtil.getDefaultValueOfPrimitiveType(variable.getType().getEntityName()));
                }

            } 
        } 
    }

}
