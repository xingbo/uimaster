package org.shaolin.uimaster.page.cust;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.od.ODEntityContext;

public class ODEntityPlugin implements IODEntityPlugin {
	private IODEntityPlugin uiCust = null;

	public ODEntityPlugin() {
		uiCust = new UICustomizeODPlugin();
	}

	public void postData2UIExecute(ODEntityContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException {
		uiCust.postData2UIExecute(odContext, htmlContext);
	}

	public void postUI2DataExecute(ODEntityContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException {
		uiCust.postUI2DataExecute(odContext, htmlContext);
	}

	public void preData2UIExecute(ODEntityContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException {
		uiCust.preData2UIExecute(odContext, htmlContext);
	}

	public void preUI2DataExecute(ODEntityContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException {
		uiCust.preUI2DataExecute(odContext, htmlContext);
	}

}
