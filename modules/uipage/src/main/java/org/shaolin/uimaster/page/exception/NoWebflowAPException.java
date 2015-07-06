package org.shaolin.uimaster.page.exception;

import org.shaolin.uimaster.page.flow.nodes.WebChunk;

public class NoWebflowAPException extends WebFlowException
{
    /**
     * Constructs a NoWebflowAPException with a given exception reason
     * 
     * @param reason
     */
    public NoWebflowAPException(String reason)
    {
        super(reason);
    }
    
    /**
     * Constructs a NoWebflowAPException with a given exception reason and webflow chunk
     * 
     * @param reason
     * @param chunk
     */
    public NoWebflowAPException(String reason, WebChunk chunk)
    {
        super(reason, new Object[]{chunk});
    }
}
