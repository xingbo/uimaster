//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.20 at 04:07:50 PM CST 
//


package org.shaolin.bmdp.datamodel.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.shaolin.bmdp.datamodel.common.EntityType;
import org.shaolin.bmdp.datamodel.common.ExpressionType;


/**
 * the UIPageType extends the UIEntityType, and also have the mapping of dataEntity and UIEntity. Note: the UIPage shouldn't have reconfigurableProperty
 * 
 * <p>Java class for UIPageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UIPageType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bmdp.shaolin.org/datamodel/Common}EntityType">
 *       &lt;sequence>
 *         &lt;element name="UIEntity" type="{http://bmdp.shaolin.org/datamodel/Page}UIBaseType"/>
 *         &lt;element name="ODMapping" type="{http://bmdp.shaolin.org/datamodel/Page}PageODMappingType"/>
 *         &lt;element name="in" type="{http://bmdp.shaolin.org/datamodel/Page}PageInType" minOccurs="0"/>
 *         &lt;element name="out" type="{http://bmdp.shaolin.org/datamodel/Page}PageOutType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="finalize" type="{http://bmdp.shaolin.org/datamodel/Common}ExpressionType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isAjaxHandlingAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UIPageType", propOrder = {
    "uiEntity",
    "odMapping",
    "in",
    "outs",
    "finalize"
})
@XmlRootElement(name = "UIPage")
public class UIPage
    extends EntityType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "UIEntity", required = true)
    protected UIBaseType uiEntity;
    @XmlElement(name = "ODMapping", required = true)
    protected PageODMappingType odMapping;
    protected PageInType in;
    @XmlElement(name = "out")
    protected List<PageOutType> outs;
    @XmlElement(required = true)
    protected ExpressionType finalize;
    @XmlAttribute(name = "isAjaxHandlingAllowed")
    protected Boolean isAjaxHandlingAllowed;

    /**
     * Gets the value of the uiEntity property.
     * 
     * @return
     *     possible object is
     *     {@link UIBaseType }
     *     
     */
    public UIBaseType getUIEntity() {
        return uiEntity;
    }

    /**
     * Sets the value of the uiEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link UIBaseType }
     *     
     */
    public void setUIEntity(UIBaseType value) {
        this.uiEntity = value;
    }

    /**
     * Gets the value of the odMapping property.
     * 
     * @return
     *     possible object is
     *     {@link PageODMappingType }
     *     
     */
    public PageODMappingType getODMapping() {
        return odMapping;
    }

    /**
     * Sets the value of the odMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link PageODMappingType }
     *     
     */
    public void setODMapping(PageODMappingType value) {
        this.odMapping = value;
    }

    /**
     * Gets the value of the in property.
     * 
     * @return
     *     possible object is
     *     {@link PageInType }
     *     
     */
    public PageInType getIn() {
        return in;
    }

    /**
     * Sets the value of the in property.
     * 
     * @param value
     *     allowed object is
     *     {@link PageInType }
     *     
     */
    public void setIn(PageInType value) {
        this.in = value;
    }

    /**
     * Gets the value of the outs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOuts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PageOutType }
     * 
     * 
     */
    public List<PageOutType> getOuts() {
        if (outs == null) {
            outs = new ArrayList<PageOutType>();
        }
        return this.outs;
    }

    /**
     * Gets the value of the finalize property.
     * 
     * @return
     *     possible object is
     *     {@link ExpressionType }
     *     
     */
    public ExpressionType getFinalize() {
        return finalize;
    }

    /**
     * Sets the value of the finalize property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressionType }
     *     
     */
    public void setFinalize(ExpressionType value) {
        this.finalize = value;
    }

    /**
     * Gets the value of the isAjaxHandlingAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsAjaxHandlingAllowed() {
        if (isAjaxHandlingAllowed == null) {
            return false;
        } else {
            return isAjaxHandlingAllowed;
        }
    }

    /**
     * Sets the value of the isAjaxHandlingAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAjaxHandlingAllowed(Boolean value) {
        this.isAjaxHandlingAllowed = value;
    }

}