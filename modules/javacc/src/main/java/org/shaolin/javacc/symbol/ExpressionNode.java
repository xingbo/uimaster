package org.shaolin.javacc.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.shaolin.javacc.context.BlockEvaluationContext;
import org.shaolin.javacc.context.BlockParsingContext;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.ContextStatement;
import org.shaolin.javacc.statement.Statement;
import org.shaolin.javacc.util.DefaultExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The base class for all the expression nodes in the syntax tree
 *
 */
public abstract class ExpressionNode implements Serializable
{
	static final long serialVersionUID = 0x1E974C9C3C2E5A8CL;
	
    /* type of this expression node */
    protected String type;
    
    /* parent of this node */
    protected ExpressionNode parent;
    
    /* all the child nodes of this node */
    protected List<ExpressionNode> childs;
    
    /* the class name of this node's value object */
    private String valueClassName;

    /* the class of this node's value object */
    protected transient Class<?> valueClass;

    /* this node's value object */
    //protected transient Object valueObject;
    
    /* whether this node is a assignee whose value will be set */
    protected boolean isValueAssignee;

    /* whether this node is a variable whose value can be set */
    protected boolean isVariable;
    
    /* whether this node is a constant value node */
    protected boolean isConstant;
    
    /* the node's value if isConstant is true */
    private Object constantValue;

    /* all possible exceptions names thrown by this expression node */
    private String[] thrownExceptionNames;
    
    /*The parent block of expression*/
    protected Statement parentBlock = null;
	
    public ExpressionNode(String type)
    {
        this.type = type;
        parent = null;
        childs = new ArrayList<ExpressionNode>();
        valueClass = null;
        valueClassName = null;
        //valueObject = null;
        isValueAssignee = false;
        isVariable = false;
        isConstant = false;
        constantValue = null;
        thrownExceptionNames = null;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setParent(ExpressionNode parent)
    {
        this.parent = parent;
    }
    
    public ExpressionNode getParent()
    {
        return parent;
    }
    
    public List<ExpressionNode> getChilds()
    {
        return childs;
    }

    public void addChild(ExpressionNode child)
    {
        child.setParent(this);
        childs.add(child);
    }
    
    public void addChild(int index, ExpressionNode child)
    {
        child.setParent(this);
        childs.add(index, child);
    }
    
    public void removeChild(int index)
    {
        if(index >= 0 && index < childs.size())
        {
            childs.remove(index);
        }
    }
    
    public void removeAllChildren()
    {
        childs.clear();
    }
    
    public ExpressionNode getChild(int index)
    {
        ExpressionNode child = null;

        if(index >= 0 && index < childs.size())
        {
            child = (ExpressionNode)childs.get(index);
        }
        
        return child;
    }
    
    public int getChildNum()
    {
        return childs.size();
    }
    
    public String getValueClassName()
    {
        return valueClassName;
    }
    
    public Class<?> getValueClass() throws ParsingException
    {
        if(valueClass == null)
        {
            if(valueClassName != null)
            {
                valueClass = ExpressionUtil.findClass(valueClassName);
            }
        }
        return valueClass;
    }
    

    public void setValueClass(Class<?> valueClass)
    {
        this.valueClass = valueClass;

        if(valueClass != null)
        {
            valueClassName = valueClass.getName();
        }
        else
        {
            valueClassName = null;
        }
    }
    
    /*
    public Object getValueObject()
    {
        return valueObject;
    }
    */
    
    public void setIsValueAssignee(boolean isValueAssignee)
    {
        this.isValueAssignee = isValueAssignee;
    }
    
    public boolean isValueAssignee()
    {
        return isValueAssignee;
    }
    
    public boolean isVariable()
    {
        return isVariable;
    }
    
    protected void setVariableValue(OOEEEvaluationContext context, Object variableValue) throws EvaluationException
    {
    }
    
    public Class[] getThrownExceptions()
    {
        List thrownExceptions = new ArrayList();    

        if(thrownExceptionNames != null)
        {
            for(int i = 0, n = thrownExceptionNames.length; i < n; i++)
            {
                try
                {
                    Class exceptionClass = ExpressionUtil.findClass(thrownExceptionNames[i]);
                    thrownExceptions.add(exceptionClass);
                }
                catch(ParsingException e)
                {
                }
            }
        }
        
        return (Class[])thrownExceptions.toArray(new Class[]{});
    }
    
    protected void setThrownExceptions(Class[] thrownExceptions)
    {
        if(thrownExceptions != null)
        {
            List exceptionNames = new ArrayList();
            for(int i = 0, n = thrownExceptions.length; i < n; i++)
            {
                exceptionNames.add(thrownExceptions[i].getName());
            }
            
            thrownExceptionNames = (String[])exceptionNames.toArray(new String[]{});
        }
        else
        {
            thrownExceptionNames = null;
        }
    }

    public boolean isConstant()
    {
        return isConstant;
    }
    
    protected void setConstantValue(Object constantValue)
    {
        this.constantValue = constantValue;
    }
    
    public Object getConstantValue()
    {
        return constantValue;
    }
    
    public void setParentBlock(Statement newBlock)
    {
        this.parentBlock = newBlock;
    }
    
    public Statement getParentBlock()
    {
        return parentBlock;
    }

    public void appendToBuffer(ExpressionStringBuffer buffer)
    {
        buffer.appendSeperator(this, type);
    }
        
    public String toString()
    {
        DefaultExpressionStringBuffer buffer = new DefaultExpressionStringBuffer();
        appendToBuffer(buffer);
        return buffer.getBufferString();
    }

    /*
    public String toString()
    {
        return type;
    }
    */
    
    public void traverse(Traverser traverser)
    {
        traverser.traverse(this);
        
        for(int i = 0, n = childs.size(); i < n; i++)
        {
            ExpressionNode child = (ExpressionNode)childs.get(i);
            
            child.traverse(traverser);
        }
    }

    /**
     *  Use this method to doing type check in expression
     *
     *  @return     the value class type of this node
     */
    public abstract Class<?> checkType(OOEEParsingContext context) throws ParsingException;
    
    
    /**
     *  Use this method to evaluate the value of an expression node
     *
     *  @param      context     the evaluation context
     *  @return     the evaluation result of this node
     */    
    public void evaluate(OOEEEvaluationContext context) throws EvaluationException
    {
        if(isConstant)
        {
            context.stackPush(constantValue);            
        }
        else
        {
            evaluateNode(context);
        }
    }
    
    

    /**
     *  Use this method to evaluate the value of an expression node
     *
     *  @param      context     the evaluation context
     *  @return     the evaluation result of this node
     */    
    protected abstract void evaluateNode(OOEEEvaluationContext context) throws EvaluationException;

    
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException
    {
        Statement parentSmt = this.getParentBlock();
        DefaultParsingContext tempParsingCtx = new BlockParsingContext((ContextStatement) parentSmt,parsingContext);
        OOEEParsingContext ooeeParsingCtx = new OOEEParsingContext(parsingContext);
        ooeeParsingCtx.setParsingContext(tempParsingCtx);
        checkType(ooeeParsingCtx);
    }
    
    public Object evaluate(OOEESmtEvaluationContext evaluationContext) throws EvaluationException
    {
        Statement parentSmt = this.getParentBlock();
        DefaultEvaluationContext tempEvaluationCtx = new BlockEvaluationContext((ContextStatement) parentSmt, evaluationContext);
        
        OOEEEvaluationContext ooeeEvaluationCxt = new OOEEEvaluationContext();

        ooeeEvaluationCxt.setEvaluationContext(tempEvaluationCtx);
        
        evaluate(ooeeEvaluationCxt);
        
        return ooeeEvaluationCxt.stackPeek();
    }
    
}
