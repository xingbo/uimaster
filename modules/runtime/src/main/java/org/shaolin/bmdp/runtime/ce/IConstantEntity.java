package org.shaolin.bmdp.runtime.ce;

import java.io.Serializable;
import java.util.*;

/**
 * this interface is the base class of all ConstantEntity, every constant entity must
 * implement it.
 */
public interface IConstantEntity extends Serializable
{

    /**
     * each constant type must have an instance whose value is "_NOT_SPECIFIED",
     * this constant instance should be the default value of the constant type.
     *
     */
    public final static String CONSTANT_DEFAULT_VALUE = "_NOT_SPECIFIED";
    
    /**
     * Returns the constant entity name.
     * 
     * @return
     */
    public String getEntityName();
    
    /**
     * Returns the i18n name of current constant entity.
     * 
     * @return
     */
    public String getI18nEntityName();
    
    /**
     * Returns the constant decorator info.
     * 
     * @return
     */
    public ConstantDecorator getConstantDecorator();
    
    /**
     * Get the record id in DB table record.
     * @return
     */
    public long getRecordId();
    
    /**
     * Returns the value of this constant entity object
     * @return the string value of ce
     */
    public String getValue();

    /**
     * Returns the int value of this constant entity object if defined.
     * the default-constant intValue is 0.
     * @return the int value of ce
     */
    public int getIntValue();

    /**
     * Returns the default display name of this constant entity object
     * @return the default display name
     */
    public String getDisplayName();

    /**
     * Returns the display name of this constant entity object
     * @param aLocale       the locale to set display name for.
     * @return the display name
     */
    public String getDisplayName(String aLocale);

    /**
     * Activates this constant
     *
     */
    public void activate();

    /**
     * Passivates this constant
     *
     */
    public void passivate();

    /**
     * decides whether this constant is passivated or not
     * @return whether this constant is passivated or not
     */
    public boolean isPassivated();

    /**
     * Returns a string representation of this constant object, i.e. the display name of default locale
     * @return string representation of this constant object, i.e. the display name of default locale
     */
    public String toString();

    /**
     * Returns the i18nKey
     * @return the i18nKey
     */
    public String getI18nKey();
    
    /**
     * Returns the i18nBundle
     * @return the i18nBundle
     */
    public String getI18nBundle();
    
    /**
     * set the i18nBundle 
     * @param bund the new i18nBundle
     */
    public void setI18nBundle(String bund);

    /**
     * Returns the description of the ce
     * @return the description
     */
    public String getDescription();
    
    /**
     * Set the description of the ce
     * @param desc   the new description
     */
    public void setDescription(String desc);
    
    /**
     * Returns the effect time of the ce
     * @return the effect time
     */
    public Date getEffTime();
    
    /**
     * set the effect time of the ce
     * @param time the new effect time
     */
    public void setEffTime(Date time);
    
    /**
     * Returns the expire time of the ce
     * @return the expire time of the ce
     */
    public Date getExpTime();
    
    /**
     * Set the expire time of the ce
     * @param time the new expire time
     */
    public void setExpTime(Date time);
    
    /**
     * Returns the priority of the ce
     * @return the priority
     */
    public int getPriority();

    /**
     * Get all the defined constants.
     * 
     * @return intValue, display name
     */
	public Map<Integer, String> getConstants();
	
	/**
	 * 
	 * @param includedSpecific
	 * @return
	 */
	public Map<Integer, String> getAllConstants(boolean includedSpecific);

	/**
	 * Get all the defined constants.
	 * 
	 * @return constant items
	 */
	public List<IConstantEntity> getConstantList();
	
	public IConstantEntity get(String _value);
	
	public IConstantEntity getByIntValue(int _intValue);
	
	public IConstantEntity getByDisplayName(String description);
}
