
package org.shaolin.uimaster.page.cust;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.od.ODEntityContext;

public class UICustomizeODPlugin implements IODEntityPlugin
{

    /**
     * Doesn't support AJAX code.
     */
    public void postData2UIExecute(ODEntityContext odContext, HTMLSnapshotContext htmlContext)
            throws ODProcessException
    {
        
    }

    public void postUI2DataExecute(ODEntityContext odContext, HTMLSnapshotContext htmlContext)
            throws ODProcessException
    {
    	/**
        try
        {
            IListenerService service = ListenerService.getInstance();
            String ui2dataType = "od-ui2data:" + odContext.getOdEntityName();
            if (service.hasListener(ui2dataType))
            {
                ODEntityObject odEntityObject = ODCacheContext.getODEntityObject(odContext
                        .getOdEntityName());
                DefaultParsingContext pContext = (DefaultParsingContext)odEntityObject
                        .getLocalPContext();
                DefaultEvaluationContext eContext = (DefaultEvaluationContext)odContext
                        .getEvaluationContextObject(ODContext.LOCAL_TAG);
                DefaultEvaluationContext eGlobalContext = (DefaultEvaluationContext)odContext
                        .getEvaluationContextObject(ODContext.GLOBAL_TAG);
                Map tPMap = pContext.getVariableTypes();
                Map tValueMap = eContext.getVariableObjects();
                tPMap.put("request", HttpServletRequest.class);
                tValueMap.put("request", odContext.getRequest());
                tPMap.put(ODContext.AJAX_UICOMP_NAME, ODContext.class);
                tValueMap.put(ODContext.AJAX_UICOMP_NAME, eGlobalContext.getVariableValue(ODContext.AJAX_UICOMP_NAME));
                
                BAInvocationEvent baEvent = new BAInvocationEvent(ui2dataType, tPMap, tValueMap);
                Map valueMap = service.fireEvent(baEvent);
                Iterator iterator = valueMap.entrySet().iterator();
                Map.Entry entry = (Map.Entry)iterator.next();
                valueMap = (Map)entry.getValue();
                if( valueMap == null )
                {
                    throw new RuntimeException("Return value is null after calling BA component when data to ui.");
                }
                iterator = valueMap.keySet().iterator();
                while (iterator.hasNext())
                {
                    String name = (String)iterator.next();
                    if(!ODContext.AJAX_UICOMP_NAME.equals(name))
                    {
                        eContext.setVariableValue(name, valueMap.get(name));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            throw new UIExtExecuteBAException(
                    "Error occurred when ui to data call the BA of UI cusomization.", ex);
        }
        */
    }

    public void preData2UIExecute(ODEntityContext odContext, HTMLSnapshotContext htmlContext)
            throws ODProcessException
    {
    	/**
        try
        {
            IListenerService service = ListenerService.getInstance();
            String data2uiType = "od-data2ui:" + odContext.getOdEntityName();
            if (service.hasListener(data2uiType))
            {
                ODEntityObject odEntityObject = ODCacheContext.getODEntityObject(odContext
                        .getOdEntityName());
                DefaultParsingContext pContext = (DefaultParsingContext)odEntityObject
                        .getLocalPContext();
                DefaultEvaluationContext eContext = (DefaultEvaluationContext)odContext
                        .getEvaluationContextObject(ODContext.LOCAL_TAG);
                Map tPMap = pContext.getVariableTypes();
                Map tValueMap = eContext.getVariableObjects();
                tPMap.put("request", HttpServletRequest.class);
                tValueMap.put("request", odContext.getRequest());

                BAInvocationEvent baEvent = new BAInvocationEvent(data2uiType, tPMap, tValueMap);
                Map valueMap = service.fireEvent(baEvent);
                Iterator iterator = valueMap.entrySet().iterator();
                Map.Entry entry = (Map.Entry)iterator.next();
                valueMap = (Map)entry.getValue();
                if( valueMap == null )
                {
                    throw new RuntimeException("Return value is null after calling BA component when data to ui.");
                }
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
                    "Error occurred when data to ui call the BA of UI cusomization.", ex);
        }
        */
    }

    public void preUI2DataExecute(ODEntityContext odContext, HTMLSnapshotContext htmlContext)
            throws ODProcessException
    {

    }

}
