
package org.shaolin.uimaster.page.cust;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.od.ODPageContext;

public class UICustomizPagePlugin implements IODPagePlugin
{

    public void postInExecute(ODPageContext odContext, HTMLSnapshotContext htmlContext)
            throws ODProcessException
    {

    }

    public void postOutExecute(ODPageContext odContext, HTMLSnapshotContext htmlContext)
            throws ODProcessException
    {
    	/**
        try
        {
            IListenerService service = ListenerService.getInstance();
            String outType = "page-out:" + odContext.getOdEntityName() + ":" + odContext.getOutName();
            String outTypeOld = "out:" + odContext.getOdEntityName() + ":" + odContext.getOutName();
            boolean found = false;
            if (service.hasListener(outType))
            {
                found = true;
            }
            else if (service.hasListener(outTypeOld))
            {
                found = true;
                outType = outTypeOld;
            }
            if (found)
            {
                ODPageEntityObject odPageEntityObject = ODCacheContext
                        .getODPageEntityObject(odContext.getUiEntityName());
                DefaultParsingContext pContext = (DefaultParsingContext)odPageEntityObject
                        .getOutPContext(odContext.getOutName());
                DefaultEvaluationContext eContext = (DefaultEvaluationContext)odContext
                        .getEvaluationContextObject(ODPageContext.GLOBAL_TAG);
                Map tPMap = pContext.getVariableTypes();
                Map tValueMap = eContext.getVariableObjects();
                tPMap.put("request", HttpServletRequest.class);
                tValueMap.put("request", odContext.getRequest());
                tPMap.put(ODContext.AJAX_UICOMP_NAME, ODContext.class);
                tValueMap.put(ODContext.AJAX_UICOMP_NAME, eContext.getVariableValue(ODContext.AJAX_UICOMP_NAME));
                
                BAInvocationEvent baEvent = new BAInvocationEvent(outType, tPMap, tValueMap);
                Map valueMap = service.fireEvent(baEvent);
                Iterator iterator = valueMap.entrySet().iterator();
                Map.Entry entry = (Map.Entry)iterator.next();
                valueMap = (Map)entry.getValue();
                iterator = valueMap.keySet().iterator();
                while (iterator.hasNext())
                {
                    String name = (String)iterator.next();
                    eContext.setVariableValue(name, valueMap.get(name));
                }
            }
        }
        catch (Exception ex)
        {
            throw new UIExtExecuteBAException(
                    "Error occurred when call the BA of UI cusomization.", ex);
        }
        */
    }
}
