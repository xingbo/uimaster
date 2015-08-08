package org.shaolin.bmdp.workflow.ui;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.workflow.FlowType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.uimaster.page.ajax.TreeItem;

public class PageUtil {

	public static ArrayList<TreeItem> loadWorkflowTree() {
        
		final List<Workflow> allWorkflows = new ArrayList<Workflow>();
		AppContext.get().getEntityManager().executeListener(new IEntityEventListener<Workflow, DiagramType>(){
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}
			@Override
			public void notify(EntityAddedEvent<Workflow, DiagramType> event) {
				allWorkflows.add(event.getEntity());
			}
			@Override
			public void notify(EntityUpdatedEvent<Workflow, DiagramType> event) {
			}
			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}
			@Override
			public void notifyAllLoadFinish() {
			}
			@Override
			public Class getEventType() {
				return Workflow.class;
			}
		});
		
		if (allWorkflows.size() == 0) {
			// add a fake work flow here.
			Workflow wf = new Workflow();
			wf.setEntityName("FakeWorkflow");
			allWorkflows.add(wf);
		}
		
		ArrayList<TreeItem> result = new ArrayList<TreeItem>();
        for (int i=0;i<allWorkflows.size();i++) {
        	Workflow workflow = allWorkflows.get(i);
            
            TreeItem gitem = new TreeItem();
            gitem.setId(workflow.getEntityName().replace(' ', '_'));
            gitem.setText(workflow.getDescription() != null? workflow.getDescription():workflow.getEntityName());
            gitem.setState(new org.shaolin.uimaster.page.ajax.TreeItem.State());
            gitem.setA_attr(new org.shaolin.uimaster.page.ajax.TreeItem.LinkAttribute("#"));
            result.add(gitem);
    
            int count = 0;
            List<FlowType> flows = workflow.getFlows();
	        for (FlowType flow : flows) {
	            TreeItem item = new TreeItem();
	            //TODO: make unique id here.
	            item.setId(gitem.getId() + "." + flow.getName());
	            item.setText(flow.getName());
	            item.setA_attr(new org.shaolin.uimaster.page.ajax.TreeItem.LinkAttribute("#"));
	            
	            gitem.getChildren().add(item);
	        } 
        }
        
        return result;
	}
	
	public static void createWorkflow() {
		Workflow wf = new Workflow();
		wf.setEntityName("NewWorkflow_" + (int)(Math.random() * 100000));
	}
	
}
