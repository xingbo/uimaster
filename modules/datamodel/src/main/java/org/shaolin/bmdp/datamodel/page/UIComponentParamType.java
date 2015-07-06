//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.20 at 04:07:50 PM CST 
//


package org.shaolin.bmdp.datamodel.page;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.shaolin.bmdp.datamodel.common.ExpressionType;


/**
 * <p>Java class for UIComponentParamType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UIComponentParamType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bmdp.shaolin.org/datamodel/Page}DataParamType">
 *       &lt;sequence>
 *         &lt;element name="implExpression" type="{http://bmdp.shaolin.org/datamodel/Common}ExpressionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="componentPath" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UIComponentParamType", propOrder = {
    "implExpression"
})
public class UIComponentParamType
    extends DataParamType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected ExpressionType implExpression;
    @XmlAttribute(name = "componentPath", required = true)
    protected String componentPath;

    /**
     * Gets the value of the implExpression property.
     * 
     * @return
     *     possible object is
     *     {@link ExpressionType }
     *     
     */
    public ExpressionType getImplExpression() {
        return implExpression;
    }

    /**
     * Sets the value of the implExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressionType }
     *     
     */
    public void setImplExpression(ExpressionType value) {
        this.implExpression = value;
    }

    /**
     * Gets the value of the componentPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponentPath() {
        return componentPath;
    }

    /**
     * Sets the value of the componentPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponentPath(String value) {
        this.componentPath = value;
    }

}