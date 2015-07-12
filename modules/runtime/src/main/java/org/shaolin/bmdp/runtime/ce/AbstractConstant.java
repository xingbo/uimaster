package org.shaolin.bmdp.runtime.ce;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.i18n.ResourceUtil;

public abstract class AbstractConstant implements IConstantEntity {

	protected static final long serialVersionUID = 0x811b9115811b9115L;
	
	private long recordId;
	protected String value = null;
	private int intValue = 0;
	private String i18nKey = null;
	private String description = null;
	private Date effTime = null;
	private Date expTime = null;
	private boolean isPassivated = false;
	private int priority = 65535;
	private boolean isEnabled = true;
	
	public static final int INEFFICACY_PRIORITY = 65535;

	protected List<IConstantEntity> constantList = new ArrayList<IConstantEntity>();

	protected List<IConstantEntity> dynamicItems;
	
	private ConstantDecorator decorator;
	
	public AbstractConstant() {}
	
	protected AbstractConstant(String value, int intValue) {
		this(value, intValue, null);
	}

	protected AbstractConstant(String value, int intValue, String i18nKey) {
		this(value, intValue, i18nKey, null, null, null);
	}
	
	/**
	 * Dynamic constant item support.
	 * 
	 * @param recordId
	 * @param value
	 * @param intValue
	 * @param i18nKey
	 */
	protected AbstractConstant(long recordId, String value, int intValue, String i18nKey, String description) {
		this(value, intValue, i18nKey, description, null, null);
		this.recordId = recordId;
	}

	protected AbstractConstant(String value, int intValue, String i18nKey,
			String description, Date effTime, Date expTime) {
		this.value = value;
		this.intValue = intValue;

		setI18nKey(i18nKey);
		this.description = description;
		this.effTime = effTime;
		this.expTime = expTime;
	}

	protected AbstractConstant(String value, int intValue, String i18nKey,
			String description, Date effTime, Date expTime, boolean isPassivated) {
		this(value, intValue, i18nKey, description, effTime, expTime);
		this.isPassivated = isPassivated;
	}

	protected AbstractConstant(String value, int intValue, String i18nKey,
			String description, Date effTime, Date expTime,
			boolean isPassivated, int priority) {
		this(value, intValue, i18nKey, description, effTime, expTime,
				isPassivated);
		setPriority(priority);
	}
	
	public void setEntityInfo(String description, String i18nInfo, String icon) {
		decorator = new ConstantDecorator(description, i18nInfo, icon);
		for (IConstantEntity item : constantList) {
			((AbstractConstant)item).decorator = decorator;
		}
		if (dynamicItems != null) {
			for (IConstantEntity item : dynamicItems) {
				((AbstractConstant)item).decorator = decorator;
			}
		}
	}
	
	public void addConstant(IConstantEntity item) {
		if (dynamicItems == null) {
			dynamicItems = new ArrayList<IConstantEntity>();
		}
		dynamicItems.add(item);
	}
	
	public IConstantEntity getUnspecifiedItem() {
		for (IConstantEntity item : constantList) {
			if (CONSTANT_DEFAULT_VALUE.equals(item.getValue())) {
				return item;
			}
		}
		throw new IllegalStateException("Unable to load the default item: " + CONSTANT_DEFAULT_VALUE);
	}
	
	public IConstantEntity get(String _value) {
		for (IConstantEntity item : constantList) {
			if (_value.equals(item.getValue())) {
				return item;
			}
		}
		if (dynamicItems == null) {
			return getUnspecifiedItem();
		}
		for (IConstantEntity item : dynamicItems) {
			if (_value.equals(item.getValue())) {
				return item;
			}
		}
		return getUnspecifiedItem();
	}
	
	public IConstantEntity getByIntValue(int _intValue) {
		for (IConstantEntity item : constantList) {
			if (_intValue == item.getIntValue()) {
				return item;
			}
		}
		if (dynamicItems == null) {
			return getUnspecifiedItem();
		}
		for (IConstantEntity item : dynamicItems) {
			if (_intValue == item.getIntValue()) {
				return item;
			}
		}
		return getUnspecifiedItem();
	}

	public IConstantEntity getByDisplayName(String displayName) {
		for (IConstantEntity item : constantList) {
			if (displayName.equals(item.getDisplayName())) {
				return item;
			}
		}
		if (dynamicItems == null) {
			return getUnspecifiedItem();
		}
		for (IConstantEntity item : dynamicItems) {
			if (displayName.equals(item.getDisplayName())) {
				return item;
			}
		}
		return getUnspecifiedItem();
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public void setI18nKey(String key) {
		if (key == null) {
			this.i18nKey = getValueEntityName();
		} else {
			this.i18nKey = key;
		}
	}
	
	public long getRecordId() {
		return recordId;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#getValue()
	 */
	public String getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#getIntValue()
	 */
	public int getIntValue() {
		return intValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#setIntValue(int)
	 */
	public void setIntValue(int i) {
		intValue = i;
	}

	public String getDisplayName() {
		return getDisplayName(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * bmiasia.ebos.constant.IConstantEntity#getDisplayName(java.lang.String)
	 */
	public String getDisplayName(String aLocale) {
		String displayName = ResourceUtil.getResource(aLocale, getI18nBundle(),
				i18nKey);
		if (displayName != null) {
			return displayName;
		}
		if (description != null && description.length() > 0) {
			return description;
		}
		return value;
	}

	public boolean isEnabled() {
		return isEnabled;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#activate()
	 */
	public void activate() {
		isPassivated = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#passivate()
	 */
	public void passivate() {
		isPassivated = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#isPassivated()
	 */
	public boolean isPassivated() {
		return isPassivated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#getI18nKey()
	 */
	public String getI18nKey() {
		return i18nKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * bmiasia.ebos.constant.IConstantEntity#setDescription(java.lang.String)
	 */
	public void setDescription(String desc) {
		description = desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#getEffTime()
	 */
	public Date getEffTime() {
		return effTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#setEffTime(java.util.Date)
	 */
	public void setEffTime(Date time) {
		effTime = time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#getExpTime()
	 */
	public Date getExpTime() {
		return expTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#setExpTime(java.util.Date)
	 */
	public void setExpTime(Date time) {
		expTime = time;
	}

	/*
	 * (non-javadoc)
	 * 
	 * @see bmiasia.ebos.constant.IConstantEntity#getPriority()
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * set the priority of the ce
	 * 
	 * @param priority
	 *            the priority
	 */
	void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean hasPriority() {
		return priority != INEFFICACY_PRIORITY;
	}

	public boolean equals(Object anO) {
		if (this == anO)
			return true;

		if (!(anO instanceof AbstractConstant))
			return false;
		AbstractConstant ce = (AbstractConstant) anO;
		return getValueEntityName().equals(ce.getValueEntityName());
	}

	public int hashCode() {
		return getValueEntityName().hashCode();
	}

	public String toString() {
		return getDisplayName();
	}

	@Override
	public String getEntityName() {
		return getTypeEntityName();
	}
	
	@Override
	public String getI18nEntityName() {
		if (decorator != null) {
			return decorator.getI18nValue();
		}
		return getEntityName();
	}
	
	@Override
	public ConstantDecorator getConstantDecorator() {
		return this.decorator;
	}
	
	protected String getValueEntityName() {
		return getTypeEntityName() + "." + value;
	}
	
	@Override
	public Map<Integer, String> getConstants() {
		return getAllConstants(true);
	}

	@Override
	public Map<Integer, String> getAllConstants(boolean includedSpecific) {
		Map<Integer, String> constants = new HashMap<Integer, String>();
		for (IConstantEntity item : constantList) {
			if (!includedSpecific && item.getIntValue() == -1) {
				continue;
			}
			constants.put(item.getIntValue(), item.getDisplayName());
		}
		if (dynamicItems != null) {
			for (IConstantEntity item : dynamicItems) {
				if (!includedSpecific && item.getIntValue() == -1) {
					continue;
				}
				constants.put(item.getIntValue(), item.getDisplayName());
			}
		}
		return constants;
	}

	@Override
	public List<IConstantEntity> getConstantList() {
		if (dynamicItems != null) {
			return new ArrayList<IConstantEntity>(dynamicItems);
		}
		return new ArrayList<IConstantEntity>(constantList);
	}


	public abstract String getI18nBundle();

	public abstract void setI18nBundle(String bundle);

	protected abstract AbstractConstant __create(String value, int intValue,
			String i18nKey, String description, Date effTime, Date expTime);

	protected abstract String getTypeEntityName();

}
