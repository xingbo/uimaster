
package org.shaolin.javacc.statement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultCacheableContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.javacc.util.traverser.Traverser;


public class BlockCompilationUnit implements CompilationUnit
{
    private Block block = null;
    private String expressString = null;
    private Class returnType = null;
    private ExpressionNode returnNode = null;
    private List importList = null;
    private Map classMap = null;

    public BlockCompilationUnit(String expressString, Block block, ExpressionNode returnNode,
            List importList)
    {
        this.expressString = expressString;
        this.block = block;
        this.returnNode = returnNode;
        this.importList = importList;
       
    }

    public Object execute(EvaluationContext evaluationContext) throws EvaluationException
    {
        OOEESmtEvaluationContext context = new OOEESmtEvaluationContext(evaluationContext);
        ExecutionResult execResult = block.execute(context);
        if (execResult.getResultCode() == StatementConstants.exceptionEnding)
        {
        	throw new EvaluationException(ExceptionConstants.EBOS_000, execResult.getCauseException());
        }
        else if (execResult.getResultCode() == StatementConstants.returnEnding)
        {
            return execResult.getReturnResult();
        }
        return null;
    }

    public String getExpressionString()
    {
        return expressString;
    }

    public Class getValueClass() throws ParsingException
    {
        return this.returnType;
    }

    public void parse(ParsingContext parsingContext) throws ParsingException
    {
        this.classMap = new HashMap();
        DefaultCacheableContext cacheableCtx = new DefaultCacheableContext(classMap);
        cacheableCtx.addImportList(this.importList);
        if (returnNode != null)
        {
            OOEEParsingContext pasingCtx = new OOEEParsingContext(cacheableCtx);
            returnType = returnNode.checkType(pasingCtx);
        }

        OOEESmtParsingContext context = new OOEESmtParsingContext(parsingContext, cacheableCtx);

        if (this.block != null)
        {
            this.block.parse(context);
        }
    }

    public void traverse(Traverser traverser)
    {
        if (this.block != null)
        {
            block.traverse(traverser);
        }
    }

    public Block getBlock()
    {
        return this.block;
    }

    public String toString()
    {
        return "Block Expression Value is: " + getExpressionString();
    }

    /**
     * rebuild input expression string
     * replace all short class name with full class name that contain package name
     * Warning: if variable name is equal to a class name which in class cache
     * the variable name also replaced by class name
     */
    public String rebuildExpression() throws ParsingException
    {
        String expression = expressString;
        Map classMap = Collections.unmodifiableMap(this.classMap);
        if (classMap != null && !classMap.isEmpty())
        {
           expression = ExpressionUtil.trimExpression(expression, classMap);
        }

        return expression;
    }
    
}
