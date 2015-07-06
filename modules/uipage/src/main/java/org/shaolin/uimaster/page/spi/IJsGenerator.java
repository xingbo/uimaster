package org.shaolin.uimaster.page.spi;

import org.shaolin.bmdp.datamodel.page.UIPanelType;

public interface IJsGenerator {

	public String gen(String pageName, String prefix, UIPanelType panel);
	
}
