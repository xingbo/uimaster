package org.shaolin.uimaster.page.flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.datamodel.page.OpType;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.uimaster.page.WebConfig;

public class WebFlowUtil
{

    private static Logger logger = Logger.getLogger(WebFlowUtil.class);
    
    public static List<OpType> addTransactionOperations(List<OpType> ops)
    {
        if (logger.isDebugEnabled())
            logger.debug("addTransactionOperations");
        List<OpType> list = new ArrayList(Arrays.asList(ops));

        return list;
    }

    //should be improved
    public static boolean isBuildInVariables(String varName)
    {
        return (WebflowConstants.USER_SESSION_KEY.equals(varName) ||
                WebflowConstants.USER_LOCALE_KEY.equals(varName) ||
                WebflowConstants.INDEX_PAGE_VISITED.equals(varName) ||
                WebflowConstants.QUIT_ACTION_FLAG_KEY.equals(varName));
    }

    private static InitialContext context ;
    
    public static UserTransaction getUserTransaction() throws NamingException
    {
    	if (context == null) {
    		context = new InitialContext(WebConfig.getInitialContext());
    	}
    	
        return (UserTransaction)context.lookup(WebflowConstants.JNDI_USERTRANSACTION);
    }

    public static boolean isEmpty(String s)
    {
        return (s == null) || s.equals("");
    }

    public static void parseNameExpression(NameExpressionType[] nes,
                                    ParsingContext expressionParsingContext,
                                    ParsingContext nameParsingContext)
     throws ParsingException
    {
        for(int i = 0, n = nes.length; i < n; i ++)
        {
            parseNameExpression(nes[i], expressionParsingContext,
                                nameParsingContext);
        }

    }

    public static void parseNameExpression(NameExpressionType nameExpression,
                                    ParsingContext expressionParsingContext,
                                    ParsingContext nameParsingContext)
        throws ParsingException
    {
        String name = nameExpression.getName();
        ExpressionType expression = nameExpression.getExpression();
        Class exprClass = parseExpression(expression, expressionParsingContext);

        if(name != null && nameParsingContext != null)
        {
            Class nameClass = nameParsingContext.getVariableClass(name);
            if(!ExpressionUtil.isAssignableFrom(nameClass, exprClass))
            {
                throw new ParsingException("Can't assign var \"" + name +
                                           "\"[" + nameClass.getName() +
                                           "] with expression \"" +
                                           expression.getExpressionString() +
                                           "\"[" + exprClass.getName() + "]");
            }
        }
    }

    public static Class parseExpression(ExpressionType expression,
                                 ParsingContext parsingContext) throws ParsingException
    {
        expression.parse(parsingContext);
        return expression.getValueClass();
    }

    /**
     * the NameExpressions should be parsed
     * @param nes
     * @param expressionContext
     * @param nameContext
     * @throws EvaluationException
     */
    public static void evaluateNameExpression(NameExpressionType[] nes,
                                    EvaluationContext expressionContext,
                                    EvaluationContext nameContext)
     throws EvaluationException
    {
        for(int i = 0, n = nes.length; i < n; i ++)
        {
            evaluateNameExpression(nes[i], expressionContext,
                                expressionContext);
        }

    }

    /**
     * the NameExpression should be parsed
     * @param nameExpression
     * @param expressionParsingContext
     * @param nameParsingContext
     * @throws ParsingException
     */
    public static void evaluateNameExpression(NameExpressionType nameExpression,
                                    EvaluationContext expressionContext,
                                    EvaluationContext nameContext)
        throws EvaluationException
    {
        String name = nameExpression.getName();
        ExpressionType expression = nameExpression.getExpression();

        Object value = evaluateExpression(expression, expressionContext);
        if(name != null)
        {
            nameContext.setVariableValue(name, value);
        }

    }

    /**
     * the Expression should be parsed
     * @param expression
     * @param parsingContext
     * @return
     * @throws ParsingException
     */
    public static Object evaluateExpression(ExpressionType expression,
                                 EvaluationContext evaluationContext)
        throws EvaluationException
    {
        return expression.evaluate(evaluationContext);
    }

}
