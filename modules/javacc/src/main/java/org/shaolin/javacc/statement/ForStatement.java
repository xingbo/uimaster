package org.shaolin.javacc.statement;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for the for statement node
 * 
 */
public class ForStatement extends ContextStatement
{
    /**
     * The execution of the for statement
     * @throws Exception 
     *
     */
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) 
    {
        if(evaluationContext.getEvaluationConext(this) == null)
        {
            evaluationContext.putEvaluationConext(this, new DefaultEvaluationContext());
        }
        ExecutionResult execResult = new ExecutionResult();
        if(initVarDec != null)
        {
            execResult = initVarDec.execute(evaluationContext);
            if(execResult.getResultCode() != StatementConstants.normalEnding)
                return execResult;
        }
        else if(initSmtList != null)
        {
            execResult = initSmtList.execute(evaluationContext);
            if(execResult.getResultCode() != StatementConstants.normalEnding)
                return execResult;
        }
        Object result = new Object();
        if(forExpNode != null)
        {
            try 
            {
                result = forExpNode.evaluate(evaluationContext);
            }
            catch (EvaluationException e) 
            {
                execResult.setResultCode(StatementConstants.exceptionEnding);
                execResult.setCauseException(e.getCause());
                return execResult;
            }
            while(((Boolean)result).booleanValue())
            {                    
                execResult = forSmt.execute(evaluationContext);
                if(execResult.getResultCode() == StatementConstants.exceptionEnding || execResult.getResultCode() == StatementConstants.returnEnding)
                {
                    return execResult;
                }
                else if(execResult.getResultCode() == StatementConstants.breakEnding)
                {
                    execResult.setResultCode(StatementConstants.normalEnding);
                    return execResult;
                }
                
                if(updateSmtList != null)
                {
                    execResult = updateSmtList.execute(evaluationContext);
                    if(execResult.getResultCode() != StatementConstants.normalEnding)
                        return execResult;
                }
                
                try 
                {
                    result = forExpNode.evaluate(evaluationContext);
                }
                catch (EvaluationException e) 
                {
                    execResult.setResultCode(StatementConstants.exceptionEnding);
                    execResult.setCauseException(e.getCause());
                    return execResult;
                }

     
            }
            
        }
        else
        {
            while(true)
            {
                execResult = forSmt.execute(evaluationContext);
                if(execResult.getResultCode() == StatementConstants.exceptionEnding)
                {
                    return execResult;
                }
                else if(execResult.getResultCode() == StatementConstants.breakEnding)
                {
                    execResult.setResultCode(StatementConstants.normalEnding);
                    return execResult;
                }
                if(updateSmtList != null)
                {
                    execResult = updateSmtList.execute(evaluationContext);
                    if(execResult.getResultCode() != StatementConstants.normalEnding)
                        return execResult;
                }
                
            }
            
        }
        return execResult;
        
    }
    
    /**
     * The parse of the for statement
     * 
     */
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException 
    {
        if(parsingContext.getParsingContext(this) == null)
        {
            parsingContext.addParsingContext(this, new DefaultParsingContext());
        }
        if(this.initVarDec != null)
        {
            this.initVarDec.parse(parsingContext);
        }
        else if(this.initSmtList != null)
        {
            this.initSmtList.parse(parsingContext);
        }
        if(this.forExpNode != null)
        {
            forExpNode.parse(parsingContext);
            if(forExpNode.getValueClass() != boolean.class)
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_029);
               // throw new ParsingException("Can't convert the expression to boolean");            
        }
        if(this.updateSmtList != null)
            this.updateSmtList.parse(parsingContext);
        forSmt.parse(parsingContext);
        
    }
    
    public void traverse(Traverser traverser)
    {
        if (initSmtList != null)
        {
            initSmtList.traverse(traverser);
        }
        if (forExpNode != null)
        {
            forExpNode.traverse(traverser);
        }
        if (forSmt != null)
        {
            forSmt.traverse(traverser);
        }
    }
    
    public Statement getForSmt()
    {
        return forSmt;
    }
    
    public void setForSmt(Statement newForSmt)
    {
        this.forSmt = newForSmt;
    }
    
    public ExpressionNode getForExp()
    {
        return forExpNode;
    }
   
    public void setForExp(ExpressionNode newForExp)
    {
        this.forExpNode = newForExp;
    }
    
    public StatementExpressionList getInitSmtList()
    {
        return initSmtList;
    }
    
    public void setInitSmtList(StatementExpressionList newInitSmtList)
    {
        this.initSmtList = newInitSmtList;
    }
    
    public StatementExpressionList getUpdateSmtList()
    {
        return updateSmtList;
    }
    
    public void setUpdateSmtList(StatementExpressionList newUpdateSmtList)
    {
        this.updateSmtList = newUpdateSmtList;
    }
    
    public LocalVariableDeclaration getInitVarDec()
    {
        return initVarDec;
    }
    
    public void setInitVarDec(LocalVariableDeclaration newInitVarDec)
    {
        this.initVarDec = newInitVarDec;
    }
    
    /*The executed statement in the for statement*/
    private Statement forSmt = null;
    
    /*The executed expression in the for statement*/
    private ExpressionNode forExpNode = null;
    
    /*The init statement expression list in the for statement*/
    private StatementExpressionList initSmtList = null;
    
    /*The init local variable declaration in the for statement*/
    private LocalVariableDeclaration initVarDec = null;
    
    /*The update statement expression list in the for statement*/
    private StatementExpressionList updateSmtList = null;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
