package org.shaolin.uimaster.page.javacc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.flow.WebflowConstants;

public class HttpSessionParsingContext extends DefaultParsingContext
{
    private static Logger logger = Logger.getLogger(HttpSessionParsingContext.class);

    public HttpSessionParsingContext()
    {
        super();
        initBuildInVariable();
    }

    public HttpSessionParsingContext(HashMap variableTypes)
    {
        super(variableTypes);
        initBuildInVariable();
    }

    private void initBuildInVariable()
    {
        super.setVariableClass(WebflowConstants.USER_SESSION_KEY,
                               Object.class);
        super.setVariableClass("QuitActionFlag",
        					   BEUtil.getPrimitiveImplementClass(BEUtil.BOOLEAN));
    }

    /**
         *  Find a method with the the specified name and specified argument type classes
     *  There's no custom method support in DefaultParsingContext, so it always throws ParsingException
     *
     *  @param      name    the method name
     *  @param      argClasses     list of argument type classes
     *  @return     the found method
     *  @throws     ParsingException     if can't find a proper method
     */
    public Method findMethod(String name, List argClasses) throws
        ParsingException
    {
        if (logger.isDebugEnabled())
            logger.debug("findMethod: " + name);
        try
        {

            List argClassList = new ArrayList();
            argClassList.add(HttpSessionEvaluationContext.class);
            argClassList.addAll(argClasses);

            return WebflowContextFunction.class.getMethod(name,
                (Class[])argClassList.toArray(new Class[argClassList.size()]));
        }
        catch (NoSuchMethodException ex)
        {
            throw new ParsingException(
                "plugin method " + name
                + " not support in HttpSessionEvaluationContext", ex);
        }
    }

}
