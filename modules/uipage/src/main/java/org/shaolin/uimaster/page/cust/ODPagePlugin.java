package org.shaolin.uimaster.page.cust;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.od.ODPageContext;

public class ODPagePlugin implements IODPagePlugin
{
	private IODPagePlugin uiCust = null;
	
	public ODPagePlugin()
	{
		uiCust = new UICustomizPagePlugin();
	}
	
	public void postInExecute(ODPageContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException {
		uiCust.postInExecute(odContext, htmlContext);
	}

	public void postOutExecute(ODPageContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException {
		uiCust.postOutExecute(odContext, htmlContext);
	}

}
