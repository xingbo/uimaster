package org.shaolin.bmdp.workflow.dao;

import org.junit.Test;
import org.shaolin.bmdp.utils.SerializeUtil;
import org.shaolin.bmdp.workflow.internal.FlowRuntimeContext;


public class WorkflowModelTest {

    @Test
    public void testsearchFlows() throws Exception {
    	
    	FlowRuntimeContext context  = new FlowRuntimeContext();
    	
    	byte[] bytes = SerializeUtil.serializeData(context);
    	
    	FlowRuntimeContext context1 = SerializeUtil.readData(bytes, FlowRuntimeContext.class);
    }

}

