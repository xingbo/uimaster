package org.shaolin.bmdp.runtime.ce;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For the dynamic created constant.
 * 
 * @author wushaol
 *
 */
public class DynamicConstant extends AbstractConstant {

	private static final long serialVersionUID = 1L;

	private String entityName;

	private String i18nBundle;

	public DynamicConstant(String entityName, long recordId) {
		this.entityName = entityName;
		this.dynamicItems = new ArrayList<IConstantEntity>();
		this.addConstant(new DynamicConstant(0, AbstractConstant.CONSTANT_DEFAULT_VALUE, -1, 
				AbstractConstant.CONSTANT_DEFAULT_VALUE, AbstractConstant.CONSTANT_DEFAULT_VALUE));
	}

	public DynamicConstant(long recordId, String value, int intValue, String i18nKey, String description) {
		super(recordId, value, intValue, i18nKey, description);
	}

	public DynamicConstant(String value, int intValue, String i18nKey,
			String description, Date effTime, Date expTime) {
		super(value, intValue, i18nKey, description, effTime, expTime);
	}

	public Map<Integer, String> getAllConstants(boolean includedSpecific) {
		Map<Integer, String> constants = new HashMap<Integer, String>();
		for (IConstantEntity item : dynamicItems) {
			if (!includedSpecific && item.getIntValue() == -1) {
				continue;
			}
			constants.put(item.getIntValue(), item.getDisplayName());
		}
		return constants;
	}

	public void addConstant(DynamicConstant item) {
		dynamicItems.add(item);
	}

	@Override
	public Map<Integer, String> getConstants() {
		return getAllConstants(true);
	}

	@Override
	public List<IConstantEntity> getConstantList() {
		return new ArrayList<IConstantEntity>(dynamicItems);
	}

	public IConstantEntity deleteConstant(int intValue) {
		if (dynamicItems == null) {
			return this.getUnspecifiedItem();
		}

		for (IConstantEntity item : dynamicItems) {
			if (intValue == item.getIntValue()) {
				dynamicItems.remove(item);
				return item;
			}
		}
		return this.getUnspecifiedItem();
	}

	@Override
	public String getI18nBundle() {
		return i18nBundle;
	}

	@Override
	public void setI18nBundle(String bundle) {
		this.i18nBundle = bundle;
	}

	@Override
	protected AbstractConstant __create(String value, int intValue,
			String i18nKey, String description, Date effTime, Date expTime) {
		return new DynamicConstant(value, intValue, i18nKey, description,
				effTime, expTime);
	}

	@Override
	protected String getTypeEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

}
