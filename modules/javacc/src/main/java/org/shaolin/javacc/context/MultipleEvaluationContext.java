package org.shaolin.javacc.context;

//imports

/**
 * The multiple evaluation context interface for evaluation
 *
 * @author Xiao Yi
 */
public interface MultipleEvaluationContext extends EvaluationContext
{
    /**
     *  Set the evaluation context object of the specified prefix tag
     *  
     *  @param  tag     the prefix tag of the context
     *  @param  evaluationContext the context object
     */
    public void setEvaluationContextObject(String tag, EvaluationContext evaluationContext);

    /**
     *  Get the evaluation context object of the specified prefix tag
     *  
     *  @param  tag     the prefix tag of the context
     *  @return EvaluationContext the context object
     */
    public EvaluationContext getEvaluationContextObject(String tag);

    /**
     *  Remove the evaluation context object of the specified prefix tag
     *  
     *  @param  tag     the prefix tag of the context
     *  @return the context object
     */
    public EvaluationContext removeEvaluationContextObject(String tag);

}
