package org.shaolin.javacc;

//imports
import java.io.Serializable;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultCacheableContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.ICacheableContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.parser.Parser;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.ExceptionTraverser;


/**
 * The class for ooee expression
 * 
 * @author Xiao Yi
 */

public class Expression implements Serializable
{
	static final long serialVersionUID = 0x84FF998A4F2C60CCL;
	
    /* the syntax tree root node of this expression */
    private ExpressionNode root;

    /* the expression string */
    private String expressionString;
	
    /**
     * Parse and construct an expression using the default OOEEContext as the
     * Parsing Context
     * 
     * @param expressionString
     *            the string represents the expression
     * @throws ParsingException
     *             if the expression string is invalid
     */
    public Expression(String expressionString) throws ParsingException
    {
        this(expressionString, OOEEContextFactory.createOOEEContext(),
                new DefaultCacheableContext());
    }

    /**
     * Parse and construct an expression using the given Parsing Context
     * 
     * @param expressionString
     *            the string represents the expression
     * @param context
     *            the parsing context used in type checking
     * 
     * @throws ParsingException
     *             if the expression string is invalid
     */
    public Expression(String expressionString, ParsingContext context,
            ICacheableContext cacheableContext) throws ParsingException
    {
        this.expressionString = expressionString;
        try
        {
            root = Parser.parseExpression(expressionString);
        }
        catch (Exception e)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_039, e, new Object[]{e.getMessage()});
        }
        OOEEParsingContext parsingContext = new OOEEParsingContext(cacheableContext);
        parsingContext.setParsingContext(context);
        root.checkType(parsingContext);
    }

    /**
     * Get the type class of this expression
     * 
     * @return type class of this expression
     */
    public Class getValueClass() throws ParsingException
    {
        return root.getValueClass();
    }

    /**
     * Evaluate this expression using default OOEEContext as the Evaluation
     * Context
     * 
     * @return the evaluation result object
     * @throws EvaluationException
     *             if something error in evaluation
     */
    public Object evaluate() throws EvaluationException
    {
        return evaluate(OOEEContextFactory.createOOEEContext());
    }

    /**
     * Evaluate this expression using the given Evaluation Context
     * 
     * @return the evaluation result object
     * @throws EvaluationException
     *             if something error in evaluation
     */
    public Object evaluate(EvaluationContext context) throws EvaluationException
    {
        OOEEEvaluationContext evaluationContext = new OOEEEvaluationContext();

        evaluationContext.setEvaluationContext(context);

        root.evaluate(evaluationContext);

        return evaluationContext.stackPeek();
    }

    /**
     * Get all possible thrown exception classes while executing this expression
     * 
     * @return all exception classes
     */
    public Class[] getThrownExceptions()
    {
        ExceptionTraverser traverser = new ExceptionTraverser();

        root.traverse(traverser);

        return traverser.getExceptions();
    }

    /**
     * Get the syntax tree root node of this expression
     * 
     * @return the root node of the syntax tree
     */
    public ExpressionNode getRoot()
    {
        return root;
    }

    /**
     * Get the expression string, the same as toString()
     * 
     * @return the expression string
     */
    public String getExpressionString()
    {
        return expressionString;
    }

    public String toString()
    {
        if (root != null)
        {
            return root.toString();
        }
        else
        {
            return expressionString;
        }
    }

}
