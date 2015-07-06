package org.shaolin.bmdp.workflow.internal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.ILifeCycleProvider;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.workflow.be.FlowEntityImpl;
import org.shaolin.bmdp.workflow.dao.WorkflowModel;
import org.shaolin.bmdp.workflow.internal.cache.FlowObject;
import org.shaolin.bmdp.workflow.spi.IWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowLifecycleServiceImpl implements ILifeCycleProvider, IServiceProvider, IWorkflowService {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkflowLifecycleServiceImpl.class);
	
	private final FlowContainer flowContainer;

	public class WorkflowService implements IServiceProvider {

		WorkflowLifecycleServiceImpl impl;
		
		public WorkflowService(WorkflowLifecycleServiceImpl impl) {
			this.impl = impl;
		}
		
		@Override
		public Class getServiceInterface() {
			return null;
		}
		
	}

	public WorkflowLifecycleServiceImpl() {
		this.flowContainer = new FlowContainer(AppContext.get().getAppName());
	}
	
	@Override
	public void startService() {
		IEntityManager entityManager = AppContext.get().getEntityManager();

		FlowEntityImpl searchCriteria = new FlowEntityImpl();
		List<FlowEntityImpl> allFlowEntities = WorkflowModel.INSTANCE.searchFlowEntities(searchCriteria, null, 0, 1);
		for (FlowEntityImpl wf : allFlowEntities) {
			try {
				Workflow workflow = EntityUtil.unmarshaller(Workflow.class, new StringReader(wf.getContent()));
				// add the customized workflow to current application entity manager.
				entityManager.appendEntity(workflow);
			} catch (JAXBException e) {
				e.printStackTrace();
				continue;
			}
		}
		
		final List<Workflow> allFlows = new ArrayList<Workflow>();
		entityManager.executeListener(new IEntityEventListener<Workflow, DiagramType>() {
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(EntityAddedEvent<Workflow, DiagramType> event) {
				allFlows.add(event.getEntity());
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
			public Class<Workflow> getEventType() {
				return Workflow.class;
			}
		});
		
		AppContext.get().register(this);
		
		flowContainer.startService(allFlows);
	}

	@Override
	public void reload() {
		
	}
	
	@Override
	public boolean readyToStop() {
		return true;
	}

	@Override
	public void stopService() {
		
	}

	@Override
	public int getRunLevel() {
		return 10;
	}
	
	public Workflow getWorkflowEntity(String name) {
		IEntityManager entityManager = AppContext.get().getEntityManager();
		return entityManager.getEntity(name, Workflow.class);
	}
	
	/**
	 * Import data from data base, all constant entities can be overrided.
	 * 
	 * @param constants
	 */
	public void importData(Workflow[] constants) {
		for (Workflow ce: constants) {
			logger.info("Add constant entity: " + ce.getEntityName() + " from DB data.");
		}
	}

	@Override
	public Class getServiceInterface() {
		return IWorkflowService.class;
	}

	@Override
	public FlowObject getFlowObject(String flowName) {
		return flowContainer.getFlowObject(flowName);
	}
}
