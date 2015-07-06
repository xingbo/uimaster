package org.shaolin.bmdp.runtime.security;

import java.util.List;

import org.shaolin.bmdp.runtime.ce.IConstantEntity;

/**
 * Data Permission Service
 * 
 * @author wushaol
 * 
 */
public interface IPermissionService {

	// these static variables are referred to org.shaolin.vogerp.commonmodel.ce.PermissionType.
	public static final int NOT_SPECIFIED = -1;
	
	public static final int ACCEPTABLE = 0;

	public static final int DENIABLE = 1;

	public static final int VIEWABLE = 2;

	public static final int EDITABLE = 3;

	public static final int SHOW = 4;

	public static final int HIDDEN = 5;

	/**
	 * 
	 * @param chunkName
	 * @param nodeName
	 * @param role login user role
	 * @return
	 */
	int checkModule(String chunkName, String nodeName, IConstantEntity role);
	
	int checkModule(String chunkName, String nodeName, List<IConstantEntity> roles);

	/**
	 * 
	 * 
	 * @param pageName access page
	 * @param widgetId access ui widget.
	 * @param role login user role
	 * @return
	 */
	int checkUIWidget(String pageName, String widgetId, IConstantEntity role);

	int checkUIWidget(String pageName, String widgetId, List<IConstantEntity> roles);

	/**
	 * 
	 * @param pageName
	 * @param widgetId
	 * @param columnId
	 * @param role
	 * @return
	 */
	int checkUITableWidget(String pageName, String widgetId, String columnId,
			IConstantEntity role);

	int checkUITableWidget(String pageName, String widgetId, String columnId,
			List<IConstantEntity> roles);
	
	/**
	 * 
	 * @param beName
	 * @param field
	 * @param role
	 * @return
	 */
	int checkBEDate(String beName, String field, IConstantEntity role);

	int checkBEDate(String beName, String field, List<IConstantEntity> roles);

}
