package org.shaolin.bmdp.runtime.spi;

/**
 * Process the event to workflow engine.
 * 
 * @author wushaol
 *
 */
public interface EventProcessor {
    public void process(Event evt);
}
