package org.shaolin.bmdp.workflow.internal;

import org.shaolin.bmdp.datamodel.workflow.NodeType;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.workflow.spi.EventProducer;

public final class BuiltInAttributeConstant {
    public static final String KEY_PRODUCER_NAME = EventProducer.class.getName() + "_NAME";
    public static final String KEY_ORIGINAL_EVENT = Event.class.getName() + "_original";
    public static final String KEY_NODE = NodeType.class.getName();
    public static final String KEY_RUNTIME = FlowRuntimeContext.class.getName();
    public static final String KEY_FLOWCONTEXT = FlowContext.class.getName();
    public static final String KEY_VARIABLECONTEXT = FlowVariableContext.class.getName();

    private BuiltInAttributeConstant() {
    }
}
