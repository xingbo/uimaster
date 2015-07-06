package org.shaolin.javacc.statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.symbol.Literal;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for the switch statement node
 */
public class SwitchStatement extends ContextStatement
{
	public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) 
    {
        if(evaluationContext.getEvaluationConext(this) == null)
        {
            evaluationContext.putEvaluationConext(this, new DefaultEvaluationContext());
        }
        ExecutionResult execResult = new ExecutionResult();
        Object value = new Object();
        try 
        {
            value = switchExpression.evaluate(evaluationContext);
        } 
        catch (EvaluationException e) 
        {
            execResult.setResultCode(StatementConstants.exceptionEnding);
            execResult.setCauseException(e.getCause());
            return execResult;
        }
        
        int switchValue = 0;
        
        if(value instanceof Character)
        {
            switchValue = ((Character)value).charValue();
        }
        else if(value instanceof Integer)
        {
            switchValue = ((Integer)value).intValue();
        }
        else if(value instanceof Byte)
        {
            switchValue = ((Byte)value).intValue();
        }
        else if(value instanceof Short)
        {
            switchValue = ((Short)value).intValue();
        }
        boolean foundCase = false;
        for(int i = 0; i < caseList.size(); i++)
        {   
            if(!(caseList.get(i) instanceof String))
            {
                try 
                {
                    value = ((ExpressionNode)caseList.get(i)).evaluate(evaluationContext);
                } 
                catch (EvaluationException e) 
                {
                    execResult.setResultCode(StatementConstants.exceptionEnding);
                    execResult.setCauseException(e.getCause());
                    return execResult;
                }
                int caseValue = 0;
                
                if(value  instanceof Character)
                {
                    caseValue = ((Character)value).charValue();
                }
                else if(value instanceof Integer)
                {
                    caseValue = ((Integer)value).intValue();
                }
                else if(value instanceof Byte)
                {
                    caseValue = ((Byte)value).intValue();
                }
                
                else if(value instanceof Short)
                {
                    caseValue = ((Short)value).intValue();
                }
                if(caseValue == switchValue)
                {
                    foundCase = true;
                    List switchSmt = (List) switchCase.get(caseList.get(i));
                    for(int j = 0; j < switchSmt.size(); j++)
                    {
                        execResult = ((BlockStatement)switchSmt.get(j)).execute(evaluationContext);
                        if(execResult.getResultCode() != StatementConstants.normalEnding)
                            return execResult;       
                    }
                    for(int k = i+1; k < caseList.size(); k++)
                    {
                        switchSmt = (List) switchCase.get(caseList.get(k));
                        for(int j = 0; j < switchSmt.size(); j++)
                        {
                            execResult = ((BlockStatement)switchSmt.get(j)).execute(evaluationContext);
                            if(execResult.getResultCode() != StatementConstants.normalEnding)
                                return execResult;
                        }
                    }                    
                }
            }
        }
        if(!foundCase)
        {
            if(switchCase.containsKey("default"))
            {
                List switchSmt = (List) switchCase.get("default");
                for(int j = 0; j < switchSmt.size(); j++)
                {
                    execResult = ((BlockStatement)switchSmt.get(j)).execute(evaluationContext);
                    if(execResult.getResultCode() != StatementConstants.normalEnding)
                        return execResult;       
                }
            }
            int i = caseList.indexOf("default");
            for(int k = i+1; k < caseList.size(); k++)
            {
                List switchSmt = (List) switchCase.get(caseList.get(k));
                for(int j = 0; j < switchSmt.size(); j++)
                {
                    execResult = ((BlockStatement)switchSmt.get(j)).execute(evaluationContext);
                    if(execResult.getResultCode() != StatementConstants.normalEnding)
                        return execResult;
                }
            }
            
        }        
        return execResult;
    }
    
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException 
    {
        if(parsingContext.getParsingContext(this) == null)
        {
            parsingContext.addParsingContext(this, new DefaultParsingContext());
        }
        switchExpression.parse(parsingContext);
        if(switchExpression.getValueClass() != int.class &&
                switchExpression.getValueClass() != char.class &&
                switchExpression.getValueClass() != byte.class &&
                switchExpression.getValueClass() != short.class)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_059);
          //  throw new ParsingException("The incorrect switch type");
        }
        for(int i = 0; i < caseList.size(); i++)
        {
            if(!(caseList.get(i) instanceof  Literal || caseList.get(i) instanceof String))
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_065);
               // throw new ParsingException("case expressions must be constant expressions");
            }
            if(caseList.get(i) instanceof Literal)
            {
                Literal caseValue = (Literal) caseList.get(i);
                Class valueClass = caseValue.getValueClass();               
                if(valueClass != int.class &&
                        valueClass != byte.class &&
                        valueClass != char.class &&
                        valueClass != short.class)
                {
                	throw new ParsingException(ExceptionConstants.EBOS_OOEE_072);
                   // throw new ParsingException("only int,byte,char or short literals can be case expressions");
                }
                caseValue.parse(parsingContext);
                List switchSmt = (List) switchCase.get(caseList.get(i));
                for(int j = 0; j < switchSmt.size(); j++)
                {
                    ((BlockStatement)switchSmt.get(j)).parse(parsingContext);
                }
            }
            else if(caseList.get(i) instanceof String)
            {
                String caseName = (String) caseList.get(i);
                if(!("default").equals(caseName))
                	throw new ParsingException(ExceptionConstants.EBOS_OOEE_057,new Object[]{caseName});
                   // throw new ParsingException("Syntax error on token \""+caseName+"\", default expected");
                List switchSmt = (List) switchCase.get(caseName);
                for(int j = 0; j < switchSmt.size(); j++)
                {
                    ((BlockStatement)switchSmt.get(j)).parse(parsingContext);
                }
            }
            
        }
        
    }
    
    public void traverse(Traverser traverser)
    {
       if (switchExpression != null)
       {
           switchExpression.traverse(traverser);
       }
      
       for(int i = 0; i < caseList.size(); i++)
       {
          
           if(caseList.get(i) instanceof Literal)
           {
               Literal caseValue = (Literal) caseList.get(i);
               caseValue.traverse(traverser);
               List switchSmt = (List) switchCase.get(caseValue);
               for(int j = 0; j < switchSmt.size(); j++)
               {
                   ((BlockStatement)switchSmt.get(j)).traverse(traverser);
               }
           }
           else if(caseList.get(i) instanceof String)
           {
               String caseName = (String) caseList.get(i);
              
               List switchSmt = (List) switchCase.get(caseName);
               for(int j = 0; j < switchSmt.size(); j++)
               {
                   ((BlockStatement) switchSmt.get(j)).traverse(traverser);
               }
           }
           
       }
    }

	public ExpressionNode getSwitchExpression()
	{
	    return switchExpression;
	}
	
	public void setSwitchExpression(ExpressionNode switchExp)
	{
	    this.switchExpression = switchExp;
	}
	
	public List getBlockSmtList()
	{
	    return blockSmtList;
	}
	
	public void setBlockSmtList(List blockSmt)
	{
	    this.blockSmtList = blockSmt;        
	}
	
	public HashMap getSwitchCase()
	{		
	    return switchCase;
	}
	
	public void setSwitchCase(HashMap newSwitchCase)
	{
	    this.switchCase = newSwitchCase;
	}
	
    public List getCaseValueList()
    {
        return caseValueList;
    }
    
    public void setCaseValueList(List newList)
    {
        this.caseValueList = newList;
    }
    
    public OOEEContext getOOEEContext()
    {
        return ooeeContext;
    }
    
    public void setOOEEContext(OOEEContext newContext)
    {
        this.ooeeContext = newContext;
    }
    
    public List getCaseList()
    {
        return caseList;
    }
    
    public void setCaseList(List newList)
    {
        this.caseList = newList;
    }
    
    /*The context of the block*/
    protected OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
    
	/*The condition expression in the switch statement*/
    private ExpressionNode switchExpression;
    
    /*The block statement list for each switch case*/
    private List blockSmtList;
    
    /*The map for each switch case, with the block statement list*/
    private HashMap switchCase = new HashMap();
    
    /*The case list in the case statement*/
    private List caseValueList = new ArrayList();
    
    /*The case list of the switch statement*/
    private List caseList = new ArrayList();
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;
    
}
