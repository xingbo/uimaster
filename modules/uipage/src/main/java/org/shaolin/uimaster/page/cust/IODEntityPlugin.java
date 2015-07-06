package org.shaolin.uimaster.page.cust;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.od.ODEntityContext;

/**
 * for the extension requirement, OD engine can able to be added insert-point to
 * execute extended logic.
 * 
 * @author swu
 * 
 */
public interface IODEntityPlugin {

	public void preData2UIExecute(ODEntityContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException;

	public void postData2UIExecute(ODEntityContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException;

	public void preUI2DataExecute(ODEntityContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException;

	public void postUI2DataExecute(ODEntityContext odContext,
			HTMLSnapshotContext htmlContext) throws ODProcessException;
}
