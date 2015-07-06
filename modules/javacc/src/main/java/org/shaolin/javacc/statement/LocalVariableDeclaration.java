package org.shaolin.javacc.statement;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.*;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.*;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.javacc.util.traverser.Traverser;

import java.io.Serializable;

/**
 * The class for the local variable declaration
 * 
 */
public class LocalVariableDeclaration implements Serializable
{    
    /**
     * The execution of the local variable declaration
     * @throws Exception 
     *
     */
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        ContextStatement contextSmt = (ContextStatement)this.getParentBlock();
        for(int i = 0; i < varName.size(); i++)
        {
            String name = (String)varName.get(i);
            Object value = null;
            if(this.getValueMap().get(name) != null)
            {
                try
                {
                    this.initVariable(name,evaluationContext);
                    value = ((ExpressionNode)this.getValueMap().get(name)).evaluate(evaluationContext);
                }
                catch(EvaluationException e)
                {
                    execResult.setCauseException(e.getCause());
                    execResult.setResultCode(StatementConstants.exceptionEnding);
                    return execResult;
                }
                try
                {
                    if(ExpressionUtil.isNumeric(varClass))
                        value = ExpressionUtil.getNumericReturnObject(value, varClass);
                }
                catch(ParsingException e)
                {
                    execResult.setCauseException(e.getCause());
                    execResult.setResultCode(StatementConstants.exceptionEnding);
                    return execResult;
                }
                try
                {
                    evaluationContext.getEvaluationConext(contextSmt).setVariableValue(name, value);
                }
                catch (EvaluationException e)
                {
                    //won't happen
                }
            }
            else if(ExpressionUtil.isPrimitiveClass(varClass.toString()))
            {
                if(ExpressionUtil.isNumeric(varClass))
                {
                    try
                    {    Object numericValue = ExpressionUtil.getNumericReturnObject(new Integer(0), varClass);
                         evaluationContext.getEvaluationConext(contextSmt).setVariableValue(name, numericValue);
                    }
                    catch(EvaluationException e)
                    {
                        execResult.setCauseException(e.getCause());
                        execResult.setResultCode(StatementConstants.exceptionEnding);
                        
                        return execResult;
                    }
                    catch (ParsingException e) {
                       execResult.setCauseException(e.getCause());
                       execResult.setResultCode(StatementConstants.exceptionEnding);
                       return execResult;
                    }
                }
                else 
                {
                    try
                    {
                        evaluationContext.getEvaluationConext(contextSmt).setVariableValue(name, Boolean.FALSE); 
                    }
                    catch(EvaluationException e)
                    {
                        execResult.setCauseException(e.getCause());
                        execResult.setResultCode(StatementConstants.exceptionEnding);
                        return execResult;
                    }
                }                                
            }
            else
            {
                try
                {
                    evaluationContext.getEvaluationConext(contextSmt).setVariableValue(name, value);
                }
                catch(EvaluationException e)
                {
                    execResult.setCauseException(e.getCause());
                    execResult.setResultCode(StatementConstants.exceptionEnding);
                    return execResult;
                }
            }
            
        }        
        return execResult;
        
    }
    
    /**
     * To check if the variable is already defined in the context
     * 
     * @param name
     * @throws Exception
     */
    public void checkParsingContext(String name, OOEESmtParsingContext parsingContext) throws ParsingException
    {
        Statement smt = this.getParentBlock();
        boolean alreadyDefined = false;
        while(smt != null)
        {

            if(parsingContext.getParsingContext(smt).getAllVariableNames().contains(name))
            {
                alreadyDefined = true;
                break;
            }
            smt = smt.getParentBlock();    

        }
        if(smt == null)
        {
            if(parsingContext.getParsingContext().getAllVariableNames().contains(name))
                alreadyDefined = true;  
        }             
        if(alreadyDefined)throw new ParsingException(ExceptionConstants.EBOS_OOEE_026,new Object[]{name});
          //  throw new ParsingException("Already defined " + name);
    }
    
    /**
     * Parse the local variable declaration
     * 
     * @throws Exception
     */
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException 
    {
        ContextStatement contextSmt = (ContextStatement)this.getParentBlock();
        OOEEParsingContext ooeeParsingCtx = new OOEEParsingContext(parsingContext);
        varClass = getTypeNode().checkType(ooeeParsingCtx);
        for(int i = 0; i < varName.size(); i++)
        {
            String name = (String)varName.get(i);
            checkParsingContext(name,parsingContext);
            parsingContext.getParsingContext(contextSmt).setVariableClass(name, varClass);
            if(this.getValueMap().get(name) != null)
            {
                FieldExpression fieldExp = new FieldExpression();
                FieldName fieldName = new FieldName();
                fieldName.setFieldName(name);
                fieldExp.addChild(fieldName);
                AssignmentOperator assign = new AssignmentOperator("=");
                assign.addChild(fieldExp);
                assign.addChild((ExpressionNode)this.getValueMap().get(name));
                assign.setParentBlock(this.parentBlock);
                assign.parse(parsingContext);
                assignMap.put(name,assign);
            }
            else
            {
                FieldExpression fieldExp = new FieldExpression();
                FieldName fieldName = new FieldName();
                fieldName.setFieldName(name);
                fieldExp.addChild(fieldName);
                fieldExp.setParentBlock(this.parentBlock);
                fieldExp.parse(parsingContext);
                assignMap.put(name,fieldExp);                
            }
        }
    }
    
    public void traverse(Traverser traverser)
    {
        if (typeNode != null)
        {
            getTypeNode().traverse(traverser);
        }
        
        for (Iterator i = getValueMap().values().iterator(); i.hasNext();)
        {
            ExpressionNode node = (ExpressionNode) i.next();
            node.traverse(traverser);
        }
        for (Iterator i = assignMap.values().iterator(); i.hasNext();)
        {
            ExpressionNode node = (ExpressionNode) i.next();
            node.traverse(traverser);
        }
    }
    
    
    /**
     * Paint the ast tree of the local variable declaration
     * 
     * @author LQZ
     */
    public void paintAST(DefaultMutableTreeNode root)
    {
        root.add(new DefaultMutableTreeNode("LocalVariableDeclaration"));
    }
    
    
    public void initVariable(String name,OOEESmtEvaluationContext evaluationContext) throws EvaluationException
    {
        ContextStatement contextSmt = (ContextStatement)this.getParentBlock();
        evaluationContext.getEvaluationConext(contextSmt).setVariableValue(name, null);
    }
    
    public void setNextStatement(Statement next)
    {
        this.nextStatement=next;
    }
    
    public Statement getNextStatement()
    {
        return nextStatement;
    }
    
    public void setParentBlock(Statement newBlock)
    {
        this.parentBlock = newBlock;
    }
    
    public Statement getParentBlock()
    {
        return parentBlock;
    }
    
    public ExpressionNode getTypeNode()
    {
        return typeNode;
    }
    
    public void setTypeNode(ExpressionNode newTypeNode)
    {
        this.typeNode = newTypeNode;
    }
    
    public Map getValueMap()
    {
        return valueMap;
    }
    
    public void setValueMap(Map newValueMap)
    {
        this.valueMap = newValueMap;
        
    }
    
    public List getVarName()
    {
        return varName;
    }
    
    public void setVarName(List newList)
    {
        this.varName = newList;
    }
    
    /* The current local variable declaration's next statement*/
    private Statement nextStatement = null;
    
    /*The parent block of local variable declaration*/
    private Statement parentBlock = null;
    
    /*The type definition of the declaration*/
    private ExpressionNode typeNode = null;
    
    /*The value map of the local variable declaration*/
    private Map valueMap = new HashMap();
    
    /*The expression list of the local variable declaration*/
    private Map assignMap = new HashMap();
    
    /*The list of the name in the statement*/
    private List varName = new ArrayList();
    
    /*The class type of the local variable declaration*/
    private Class varClass = null;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
