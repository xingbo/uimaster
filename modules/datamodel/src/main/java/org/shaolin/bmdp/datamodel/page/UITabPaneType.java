//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.12.28 at 02:29:54 PM CST 
//


package org.shaolin.bmdp.datamodel.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UITabPaneType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UITabPaneType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bmdp.shaolin.org/datamodel/Page}UIComponentType">
 *       &lt;sequence>
 *         &lt;element name="ajaxLoad" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="tabSelected" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tab" type="{http://bmdp.shaolin.org/datamodel/Page}UITabPaneItemType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UITabPaneType", propOrder = {
    "ajaxLoad",
    "tabSelected",
    "tabs"
})
public class UITabPaneType
    extends UIComponentType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(defaultValue = "false")
    protected boolean ajaxLoad;
    protected int tabSelected;
    @XmlElement(name = "tab")
    protected List<UITabPaneItemType> tabs;

    /**
     * Gets the value of the ajaxLoad property.
     * 
     */
    public boolean isAjaxLoad() {
        return ajaxLoad;
    }

    /**
     * Sets the value of the ajaxLoad property.
     * 
     */
    public void setAjaxLoad(boolean value) {
        this.ajaxLoad = value;
    }

    /**
     * Gets the value of the tabSelected property.
     * 
     */
    public int getTabSelected() {
        return tabSelected;
    }

    /**
     * Sets the value of the tabSelected property.
     * 
     */
    public void setTabSelected(int value) {
        this.tabSelected = value;
    }

    /**
     * Gets the value of the tabs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tabs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTabs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UITabPaneItemType }
     * 
     * 
     */
    public List<UITabPaneItemType> getTabs() {
        if (tabs == null) {
            tabs = new ArrayList<UITabPaneItemType>();
        }
        return this.tabs;
    }

}