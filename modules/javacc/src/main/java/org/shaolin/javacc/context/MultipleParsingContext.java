package org.shaolin.javacc.context;

import java.util.Collection;

/**
 * The multiple parsing context interface for parsing
 *
 * @author Xiao Yi
 */
public interface MultipleParsingContext extends ParsingContext
{
    /**
     *  Get all prefix tags of nested parsing context
     *  
     *  @return the prefix tags, list of String
     */
	public Collection getAllContextTags();
	
    /**
     *  Set the parsing context object of the specified prefix tag
     *  
     *  @param  tag     the prefix tag of the context
     *  @param  parsingContext the context object
     */
    public void setParsingContextObject(String tag, ParsingContext parsingContext);

    /**
     *  Get the parsing context object of the specified prefix tag
     *  
     *  @param  tag     the prefix tag of the context
     *  @return     the context object
     */
    public ParsingContext getParsingContextObject(String tag);
    
    /**
     *  Remove the parsing context object of the specified prefix tag
     *  
     *  @param  tag     the prefix tag of the context
     *  @return     the context object
     */
    public ParsingContext removeParsingContextObject(String tag);

}
