package org.shaolin.javacc.statement;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for the return statement node
 * 
 */
public class ReturnStatement extends Statement
{

    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        if (returnExpNode != null)
        {
            try
            {
                Object result = returnExpNode.evaluate(evaluationContext);
                execResult.setReturnResult(result);
                execResult.setResultCode(StatementConstants.returnEnding);
            }
            catch (EvaluationException e)
            {
                execResult.setResultCode(StatementConstants.exceptionEnding);
                execResult.setCauseException(e.getCause());
            }
        } else {
        	execResult.setResultCode(StatementConstants.returnEnding);
        }
        return execResult;
    }

    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException
    {
        if (returnExpNode != null)
        {
            returnExpNode.parse(parsingContext);
        }
        if (hasDefineReturnType)
        {
            Class returnClass = null;
            OOEEParsingContext ctx = new OOEEParsingContext(parsingContext);
            Class declareClass =  declareReturnType.checkType(ctx);
            if (returnExpNode == null)
            {
            	returnClass = void.class;            
            }
            else
            {
            	returnClass = returnExpNode.getValueClass();
            }
            if (!ExpressionUtil.isAssignableFrom(declareClass, returnClass))
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_025,new Object[]{declareReturnType,returnClass.getName()});
            }
        }
    }

    public void traverse(Traverser traverser)
    {
        if (returnExpNode != null)
        {
            returnExpNode.traverse(traverser);
        }
    }

    public ExpressionNode getReturnExp()
    {
        return returnExpNode;
    }

    public void setReturnExp(ExpressionNode newReturnExp)
    {
        this.returnExpNode = newReturnExp;
    }

    public void setHasDefineReturnType(boolean hasDefineReturnType)
    {
        this.hasDefineReturnType = hasDefineReturnType;
    }

    public void setDeclareReturnType(ExpressionNode declareReturnType)
    {
        this.declareReturnType = declareReturnType;
    }
    

    /* The return expression in the return statement */
    private ExpressionNode returnExpNode = null;

    private boolean hasDefineReturnType = false;

    private ExpressionNode declareReturnType = null;

    /* The serial ID */
    private static final long serialVersionUID = 1357924680L;

}
