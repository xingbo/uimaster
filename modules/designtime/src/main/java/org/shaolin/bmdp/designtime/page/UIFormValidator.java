package org.shaolin.bmdp.designtime.page;

import java.util.ArrayList;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.cache.PageCacheManager;

public class UIFormValidator implements IEntityEventListener<UIEntity, DiagramType> {

	protected GeneratorOptions option = null;
	
	private ArrayList<String> uiforms = new ArrayList<String>();
	
	public UIFormValidator(GeneratorOptions option) {
		this.option = option;
	}

	@Override
	public void setEntityManager(EntityManager entityManager) {
		
	}

	@Override
	public void notifyLoadFinish(DiagramType diagram) {
		
	}

	@Override
	public void notifyAllLoadFinish() {
		for (String uiForm: uiforms) {
			try {
				PageCacheManager.getODFormObject(uiForm);
				PageCacheManager.getUIFormObject(uiForm);
			} catch (ParsingException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		uiforms.clear();
	}

	@Override
	public void notify(EntityAddedEvent<UIEntity, DiagramType> event) {
		 
	}

	@Override
	public void notify(EntityUpdatedEvent<UIEntity, DiagramType> event) {
		String uiForm = event.getNewEntity().getEntityName();
		if (uiForm.indexOf(option.getBundleName()) == -1) {
			return;
		}
		uiforms.add(uiForm);
	}

	@Override
	public Class<UIEntity> getEventType() {
		return UIEntity.class;
	}
}
