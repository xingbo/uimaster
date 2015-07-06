package org.shaolin.javacc.statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for the try statement node
 * 
 */
public class TryStatement extends Statement
{

    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        execResult = this.tryBlock.execute(evaluationContext);
        if (execResult.getResultCode() == StatementConstants.exceptionEnding)
        {
            if (this.catchBlock != null)
            {
                for (int i = 0; i < catchClassList.size(); i++)
                {
                    Class catchClass = (Class) catchClassList.get(i);
                    if (catchClass.isAssignableFrom(execResult.getCauseException().getClass()))
                    {
                        FormalParameter catchPara = (FormalParameter) catchValueList.get(i);
                        String name = catchPara.getParameterID();
                        try
                        {
                            DefaultEvaluationContext deConext = new DefaultEvaluationContext();
                            Statement stmt = (Statement)catchBlock.get(catchValueList.get(i));
                            deConext.setVariableValue(name, execResult.getCauseException());
                            evaluationContext.putEvaluationConext(stmt, deConext);
                        }
                        catch (EvaluationException e)
                        {
                            execResult.setResultCode(StatementConstants.exceptionEnding);
                            execResult.setCauseException(e.getCause());
                            return execResult;
                        }
                        Block catchBlock = (Block) this.catchBlock.get(catchValueList.get(i));
                        execResult = catchBlock.execute(evaluationContext);
                        break;
                    }

                }
            }
            if (this.finalBlock != null)
            {
                ExecutionResult newResult = new ExecutionResult();
                newResult = finalBlock.execute(evaluationContext);
                if (newResult.getResultCode() != StatementConstants.normalEnding)
                    return newResult;
                else
                    return execResult;
            }
            return execResult;
        }
        else if (this.finalBlock != null)
        {
            ExecutionResult newResult = new ExecutionResult();
            newResult = finalBlock.execute(evaluationContext);
            if (newResult.getResultCode() != StatementConstants.normalEnding)
                return newResult;
            else
                return execResult;
        }
        else
            return execResult;
    }

    /**
     * The parse of the try-catch statement
     */
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException
    {
        this.tryBlock.parse(parsingContext);
        if (this.catchBlock == null && this.finalBlock == null)
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_058);
           // throw new ParsingException("Syntax error, insert \"Finally\" to complete TryStatement");
        if (this.catchBlock != null)
        {
            for (int i = 0; i < catchValueList.size(); i++)
            {
                Block catchBlock = (Block) this.catchBlock.get(catchValueList.get(i));
                FormalParameter catchPara = (FormalParameter) catchValueList.get(i);
                String name = catchPara.getParameterID();
                Class varClass = ExpressionUtil.findClass(catchPara.getType().getChilds().get(0)
                        .toString(), parsingContext);
                DefaultParsingContext dpContext = new DefaultParsingContext();
                dpContext.setVariableClass(name, varClass);
                parsingContext.addParsingContext(catchBlock, dpContext);
                checkParsingContext(name, parsingContext);
                catchClassList.add(varClass);
                catchBlock.parse(parsingContext);
            }
        }
        if (this.finalBlock != null)
        {
            finalBlock.parse(parsingContext);
        }

    }

    public void traverse(Traverser traverser)
    {
        if (tryBlock != null)
        {
            tryBlock.traverse(traverser);
        }
        if (this.catchBlock != null)
        {
            for (int i = 0; i < catchValueList.size(); i++)
            {
                Block catchBlock = (Block) this.catchBlock.get(catchValueList.get(i));
                catchBlock.traverse(traverser);
            }
        }
        if (this.finalBlock != null)
        {
            finalBlock.traverse(traverser);
        }
    }

    /**
     * To check if the variable is already defined in the context
     * 
     * @param name
     * @throws Exception
     */
    public void checkParsingContext(String name, OOEESmtParsingContext parsingContext)
            throws ParsingException
    {
        Statement smt = this.getParentBlock();
        boolean alreadyDefined = false;
        while (smt != null)
        {

            if (parsingContext.getParsingContext(smt).getAllVariableNames().contains(name))
            {
                alreadyDefined = true;
                break;
            }
            smt = smt.getParentBlock();

        }
        if (smt == null)
        {
            if (parsingContext.getParsingContext().getAllVariableNames().contains(name))
                alreadyDefined = true;
        }
        if (alreadyDefined)
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_026,new Object[]{name});
          //  throw new ParsingException("Already defined " + name);
    }

    public Block getTryBlock()
    {
        return tryBlock;
    }

    public void setTryBlock(Block newTryBlock)
    {
        this.tryBlock = newTryBlock;
    }

    public HashMap getCatchBlock()
    {
        return catchBlock;
    }

    public void setCatchBlock(HashMap newCatchBlock)
    {
        this.catchBlock = newCatchBlock;
    }

    public Block getFinalBlock()
    {
        return finalBlock;
    }

    public void setFinalBlock(Block newFinalBlock)
    {
        this.finalBlock = newFinalBlock;
    }

    public List getCatchValueList()
    {
        return catchValueList;
    }

    public void setCatchValueList(List newList)
    {
        this.catchValueList = newList;
    }

    /* The try block */
    private Block tryBlock = null;

    /* The catch block map, with the corresponding formal parameter */
    private HashMap catchBlock = new HashMap();

    /* The final block */
    private Block finalBlock = null;

    /* The catch block list in catch statement */
    private List catchValueList = new ArrayList();

    private List catchClassList = new ArrayList();

    /* The serial ID */
    private static final long serialVersionUID = 1357924680L;

}
