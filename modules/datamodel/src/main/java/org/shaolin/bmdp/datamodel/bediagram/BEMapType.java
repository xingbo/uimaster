//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.27 at 02:34:46 PM CST 
//


package org.shaolin.bmdp.datamodel.bediagram;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BEMapType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BEMapType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bmdp.shaolin.org/datamodel/BEDiagram}BECollectionType">
 *       &lt;sequence>
 *         &lt;element name="keyType" type="{http://bmdp.shaolin.org/datamodel/BEDiagram}BEType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BEMapType", propOrder = {
    "keyType"
})
public class BEMapType
    extends BECollectionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected BEType keyType;

    /**
     * Gets the value of the keyType property.
     * 
     * @return
     *     possible object is
     *     {@link BEType }
     *     
     */
    public BEType getKeyType() {
        return keyType;
    }

    /**
     * Sets the value of the keyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BEType }
     *     
     */
    public void setKeyType(BEType value) {
        this.keyType = value;
    }

}