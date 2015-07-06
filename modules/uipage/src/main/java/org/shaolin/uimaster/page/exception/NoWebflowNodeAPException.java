package org.shaolin.uimaster.page.exception;

import org.shaolin.uimaster.page.flow.nodes.WebNode;

public class NoWebflowNodeAPException extends WebFlowException
{
    /**
     * Constructs a NoWebflowNodeAPException with a given exception reason
     * 
     * @param reason
     */
    public NoWebflowNodeAPException(String reason)
    {
        super(reason);
    }
    
    /**
     * Constructs a NoWebflowNodeAPException with a given exception reason and webflow node
     * 
     * @param reason
     * @param node
     */
    public NoWebflowNodeAPException(String reason, WebNode node)
    {
        super(reason, new Object[]{node});
    }

}
