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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColorPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorPropertyType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bmdp.shaolin.org/datamodel/Page}PropertyValueType">
 *       &lt;sequence>
 *         &lt;element name="red" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="green" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="blue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorPropertyType", propOrder = {
    "red",
    "green",
    "blue"
})
public class ColorPropertyType
    extends PropertyValueType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String red;
    @XmlElement(required = true)
    protected String green;
    @XmlElement(required = true)
    protected String blue;

    /**
     * Gets the value of the red property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRed() {
        return red;
    }

    /**
     * Sets the value of the red property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRed(String value) {
        this.red = value;
    }

    /**
     * Gets the value of the green property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGreen() {
        return green;
    }

    /**
     * Sets the value of the green property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGreen(String value) {
        this.green = value;
    }

    /**
     * Gets the value of the blue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlue() {
        return blue;
    }

    /**
     * Sets the value of the blue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlue(String value) {
        this.blue = value;
    }

}
