//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.08 at 02:15:27 PM CST 
//


package org.shaolin.bmdp.datamodel.pagediagram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.shaolin.bmdp.datamodel.common.EntityType;
import org.shaolin.bmdp.datamodel.common.ParamType;


/**
 * A collection of Display Nodes and Logic Nodes, which indicates a complete web process or a web application module.  the chunk is a entity so we can store in or get from component library.
 * 
 * <p>Java class for WebChunkType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WebChunkType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bmdp.shaolin.org/datamodel/Common}EntityType">
 *       &lt;sequence>
 *         &lt;element name="webNode" type="{http://bmdp.shaolin.org/datamodel/PageDiagram}WebNodeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="globalVariable" type="{http://bmdp.shaolin.org/datamodel/Common}ParamType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="errorHandler" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accessPermission" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebChunkType", propOrder = {
    "webNodes",
    "globalVariables",
    "errorHandler",
    "accessPermissions"
})
@XmlRootElement(name = "WebChunk")
public class WebChunk
    extends EntityType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "webNode")
    protected List<WebNodeType> webNodes;
    @XmlElement(name = "globalVariable")
    protected List<ParamType> globalVariables;
    @XmlElement(required = true)
    protected String errorHandler;
    @XmlElement(name = "accessPermission")
    protected List<String> accessPermissions;

    /**
     * Gets the value of the webNodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the webNodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWebNodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WebNodeType }
     * 
     * 
     */
    public List<WebNodeType> getWebNodes() {
        if (webNodes == null) {
            webNodes = new ArrayList<WebNodeType>();
        }
        return this.webNodes;
    }

    /**
     * Gets the value of the globalVariables property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalVariables property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalVariables().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParamType }
     * 
     * 
     */
    public List<ParamType> getGlobalVariables() {
        if (globalVariables == null) {
            globalVariables = new ArrayList<ParamType>();
        }
        return this.globalVariables;
    }

    /**
     * Gets the value of the errorHandler property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorHandler() {
        return errorHandler;
    }

    /**
     * Sets the value of the errorHandler property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorHandler(String value) {
        this.errorHandler = value;
    }

    /**
     * Gets the value of the accessPermissions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessPermissions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessPermissions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAccessPermissions() {
        if (accessPermissions == null) {
            accessPermissions = new ArrayList<String>();
        }
        return this.accessPermissions;
    }

}