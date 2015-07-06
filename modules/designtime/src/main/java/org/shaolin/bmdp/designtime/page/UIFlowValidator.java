package org.shaolin.bmdp.designtime.page;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.pagediagram.WebChunk;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.cache.UIFlowCacheManager;

public class UIFlowValidator implements IEntityEventListener<WebChunk, DiagramType> {
	
	protected GeneratorOptions option = null;

	public UIFlowValidator(GeneratorOptions option) {
		this.option = option;
	}

	@Override
	public void setEntityManager(EntityManager entityManager) {
		
	}

	@Override
	public void notify(EntityAddedEvent<WebChunk, DiagramType> event) {
		//new UIPageObject(event.getEntity().getEntityName());
	}

	@Override
	public void notify(EntityUpdatedEvent<WebChunk, DiagramType> event) {
		String webflow = event.getNewEntity().getEntityName();
		if (webflow.indexOf(option.getBundleName()) == -1) {
			return;
		}
		try {
			UIFlowCacheManager.getInstance().addChunk(event.getNewEntity());
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyLoadFinish(DiagramType diagram) {
		
	}

	@Override
	public void notifyAllLoadFinish() {
		
	}

	@Override
	public Class<WebChunk> getEventType() {
		return WebChunk.class;
	}
}
