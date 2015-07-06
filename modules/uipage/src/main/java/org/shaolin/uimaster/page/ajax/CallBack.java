package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

/**
 * This interface is purposed for all the ajax call back actions.
 * Refer to RefForm.openInWindows and closeIfinWindows as an example.
 * 
 * @author wushaol
 *
 */
public interface CallBack extends Serializable {

	void execute();
}
