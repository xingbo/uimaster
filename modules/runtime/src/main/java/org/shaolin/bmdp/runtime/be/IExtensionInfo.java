package org.shaolin.bmdp.runtime.be;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for extension field.
 * API for get and set attributes of extension field.
 */
public interface IExtensionInfo
{
    /**
     * Set extension attribute.
     * 
     * The value type must consistent with the configuration.
     * For example, if the attribute's type is boolean, the value must 
     * a instance of Boolean.
     * 
     * @param name       attribute name
     * @param value      attribute value
     * @return           old attribute value
     */
    Object setAttribute(String name, Object value);

    /**
     * Get extension attribute.
     * 
     * The attribute value type is consistent with the configuration.
     * For example, if the attribute's type is boolean, the returned value
     * is a instance of Boolean.
     * @param name       attribute name
     * @return           attribute value
     */
    Object getAttribute(String name);
    
    /**
     * Convenient method for get date attribute value.
     * 
     * @param name      attribute name
     * @return          attribute value
     */
    Date getDateAttribute(String name);
    
    /**
     * Convenient method for get list attribute value.
     * 
     * @param name      attribute name
     * @return          attribute value
     */
    List getListAttribute(String name);
    
    /**
     * Convenient method for get map attribute value.
     * 
     * @param name      attribute name
     * @return          attribute value
     */
    Map getMapAttribute(String name);
    
    /**
     * Convenient method for get boolean attribute value.
     * 
     * @param name      attribute name
     * @return          attribute value
     */
    boolean getBooleanAttribute(String name);
    
    /**
     * Convenient method for get integer attribute value.
     * 
     * @param name      attribute name
     * @return          attribute value
     */
    int getIntAttribute(String name);
    
    /**
     * Convenient method for get long attribute value.
     * 
     * @param name      attribute name
     * @return          attribute value
     */
    long getLongAttribute(String name);
    
    /**
     * Convenient method for get double attribute value.
     * 
     * @param name      attribute name
     * @return          attribute value
     */
    double getDoubleAttribute(String name);
    
    /**
     * Convenient method for set date attribute value.
     * 
     * @param name      attribute name
     * @param value     attribute value
     * @return          
     */
    void setDateAttribute(String name, Date value);
    
    /**
     * Convenient method for set list attribute value.
     * 
     * @param name      attribute name
     * @param value     attribute value
     * @return          
     */
    void setListAttribute(String name, List value);
    
    /**
     * Convenient method for set map attribute value.
     * 
     * @param name      attribute name
     * @param value     attribute value
     * @return          
     */
    void setMapAttribute(String name, Map value);
    
    /**
     * Convenient method for set boolean attribute value.
     * 
     * @param name      attribute name
     * @param value     attribute value
     * @return          
     */
    void setBooleanAttribute(String name, boolean value);
    
    /**
     * Convenient method for set integer attribute value.
     * 
     * @param name      attribute name
     * @param value     attribute value
     * @return          
     */
    void setIntAttribute(String name, int value);
    
    /**
     * Convenient method for set long attribute value.
     * 
     * @param name      attribute name
     * @param value     attribute value
     * @return          
     */
    void setLongAttribute(String name, long value);
    
    /**
     * Convenient method for set double attribute value.
     * 
     * @param name      attribute name
     * @param value     attribute value
     * @return          
     */
    void setDoubleAttribute(String name, double value);
    
    /**
     * Return a copy of all attributes, the returned map is a unmodified map.
     * 
     * @return          all attributes
     */
    Map getAttributes();

    /**
     * Set attributes.
     * 
     * @param attributes
     */
    void setAttributes(Map attributes);
    
    /**
     * Check attribute exists.
     * @param key       attribute name
     * @return                
     */    
    boolean containsAttribute(String key);
    
    /**
     * 
     * @return
     */
    boolean isEmpty();
    /**
     * Remove attribute.
     * 
     * @param name      attribute name
     * @return                  old value of the attribute
     */
    Object removeAttribute(String key);

    /**
     * Initialize extension field.
     *
     * @param owner         owner of the attribute
     */
    void initialize(Object owner);
}
