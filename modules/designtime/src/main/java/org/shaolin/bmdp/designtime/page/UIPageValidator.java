package org.shaolin.bmdp.designtime.page;

import java.util.ArrayList;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.cache.PageCacheManager;

public class UIPageValidator implements IEntityEventListener<UIPage, DiagramType> {
	
	protected GeneratorOptions option = null;
	
	private ArrayList<String> uipages = new ArrayList<String>();

	public UIPageValidator(GeneratorOptions option) {
		this.option = option;
	}

	@Override
	public void setEntityManager(EntityManager entityManager) {
		
	}

	@Override
	public void notify(EntityAddedEvent<UIPage, DiagramType> event) {
		
	}

	@Override
	public void notify(EntityUpdatedEvent<UIPage, DiagramType> event) {
		String uiForm = event.getNewEntity().getEntityName();
		if (uiForm.indexOf(option.getBundleName()) == -1) {
			return;
		}
		uipages.add(uiForm);
		
	}

	@Override
	public void notifyLoadFinish(DiagramType diagram) {
		
	}

	@Override
	public void notifyAllLoadFinish() {
		for (String uiForm: uipages) {
			try {
				PageCacheManager.getODPageEntityObject(uiForm);
				PageCacheManager.getUIPageObject(uiForm);
			} catch (ParsingException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		uipages.clear();
	}

	@Override
	public Class<UIPage> getEventType() {
		return UIPage.class;
	}
}
