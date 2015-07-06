
package org.shaolin.javacc.statement;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.javacc.context.DefaultCacheableContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.ICacheableContext;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.Traverser;

public class ExpressionCompilationUnit implements CompilationUnit
{

    private String expressString = null;
    
    private ExpressionNode root = null;
    
    private List importList = null;

    public ExpressionCompilationUnit(String expressString, ExpressionNode node, List importList)
    {
        this.expressString = expressString;
        this.root = node;
        this.importList = importList;
    }

    public Object execute(EvaluationContext evaluationContext) throws EvaluationException
    {
        OOEEEvaluationContext context = new OOEEEvaluationContext();

        context.setEvaluationContext(evaluationContext);

        root.evaluate(context);

        return context.stackPeek();
    }

    public String getExpressionString()
    {
        return this.expressString;
    }

    public Class getValueClass() throws ParsingException
    {
        return root.getValueClass();
    }

    public void parse(ParsingContext parsingContext) throws ParsingException
    {
        parse(parsingContext, new DefaultCacheableContext());
    }
    
    public void parse(ParsingContext parsingContext, ICacheableContext cacheableContext) throws ParsingException
    {
        OOEEParsingContext context = new OOEEParsingContext(cacheableContext);
        context.setParsingContext(parsingContext);
        root.checkType(context);
    }

    public void traverse(Traverser tranverser)
    {
        root.traverse(tranverser);
    }
    
    /**
     * get root expression node of this expression
     * 
     */
    public ExpressionNode getExpressionNode()
    {
        return root;
    }
    
    public String toString()
    {
        return "Expression Value is: " + getExpressionString();
    }

}
