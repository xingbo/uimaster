package org.shaolin.uimaster.page;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.runtime.security.IPermissionService;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.security.ComponentPermission;

/**
 * This method is used to load all the security configs configured on the higher layer of RefEntity into HTMLGenerationContext
 * When processing RefEntity,use the entityPrefix of that RefEntity 
 * to put the security controls configured on higher level entity/page into htmlcontext
 * For example, the entityPrefix is uientity2.uientity1, the method will load the security controls configured in the following two entities:
 * (1)load the security configured on uientity2
 * (2)load the security configured on uipage
 * 
 * The security controls configured on uientity1 will be processed in RefEntity using HTMLUIEntity
 */
public class UIPermissionManager implements IServiceProvider
{
    private static final Logger logger = Logger.getLogger(UIPermissionManager.class);
    
    private final IPermissionService permissionService;
    
    public UIPermissionManager(IPermissionService permissionService) {
    	this.permissionService = permissionService;
    }
    
    /**
     * 
     * This method is used to get the component permission for an ajax component
     * prefix stands for the entity/page which the security controls is configured on, the suffix stands for the widget id on that entity
     * For a widget with id like uientity1.uientity2.button, this method will get the component permission in the following three cases:
     * (1) prefix: uientity1.uientity2  suffix: button  
     *     which means get the button permission which is configured on the entity of uientity1.uientity2
     * (2) prefix: uienttity1  suffix: uientity2.button
     *     which means get the button permission which is configured on the entity of uientity1, suffix stands for the widget id of button on the entity
     * (3) prefix:  suffix: uientity1.uientity2.button
     *     which means get the button permission which is configured on the top uipage, suffix stands for the widget id of button on the page
     * 
     * @param compId the full id of widget such as uientity1.uientity2.button
     * @return
     */
    public ComponentPermission getComponentPermission(String entityName, String compId)
    {
        return new ComponentPermission();
    }
    
    /**
     * This method is used to check the whether the user has permission to edit the current page.
     * 
     * @return
     */
    public boolean checkSystemAndPageLevelEditControl()
    {
        return true;
    }
    
    public static String getSecurityExpressionValue(String expression, String prefixEntity,
            String entityName)
    {
        ExpressionType securityExpression = new ExpressionType();
        securityExpression.setExpressionString(expression);
        Map accessibleVars = AjaxActionHelper.getAjaxContext().getVariables(prefixEntity);

        DefaultParsingContext parsingContext = new DefaultParsingContext();
        Iterator it = accessibleVars.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            parsingContext.setVariableClass(key, value.getClass());
        }

        OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
        ooeeContext.setDefaultParsingContext(parsingContext);
        try
        {
            securityExpression.parse(ooeeContext);
        }
        catch (ParsingException e)
        {
            logger.error("Exception occured when parsing" + " the security expression: "
                    + securityExpression + " configured in uientity: " + entityName, e);
        }

        DefaultEvaluationContext evaluationContext = new DefaultEvaluationContext();
        it = accessibleVars.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            try
            {
                evaluationContext.setVariableValue(key, value);
            }
            catch (EvaluationException e)
            {
                logger.error("Exception occured when evaluating" + " the security expression: "
                        + securityExpression + " set the value: " + key
                        + " configured in uientity: " + entityName, e);
            }
        }

        Object result = null;
        try
        {
            result = securityExpression.evaluate(evaluationContext);
        }
        catch (EvaluationException e)
        {
            logger.error("Exception occured when evaluating" + " the security expression: "
                    + securityExpression + " configured in uientity: " + entityName, e);
        }

        return result == null ? "" : result.toString();
    }

	@Override
	public Class getServiceInterface() {
		return UIPermissionManager.class;
	}

}
