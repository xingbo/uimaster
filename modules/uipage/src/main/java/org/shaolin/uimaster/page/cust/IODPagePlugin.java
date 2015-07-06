package org.shaolin.uimaster.page.cust;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.od.ODPageContext;

public interface IODPagePlugin {

	public void postInExecute(ODPageContext odContext, HTMLSnapshotContext htmlContext)
			throws ODProcessException;
	
	public void postOutExecute(ODPageContext odContext, HTMLSnapshotContext htmlContext)
			throws ODProcessException;
	
}
