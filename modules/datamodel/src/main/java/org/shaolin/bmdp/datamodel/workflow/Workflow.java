//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.19 at 12:56:16 PM CST 
//


package org.shaolin.bmdp.datamodel.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.shaolin.bmdp.datamodel.common.EntityType;


/**
 * 
 * 				App is a real time application like load balancer.
 * 				It contains a variable definition and some workflows
 * 				definition.
 * 			
 * 
 * <p>Java class for WorkflowType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WorkflowType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bmdp.shaolin.org/datamodel/Common}EntityType">
 *       &lt;sequence>
 *         &lt;element name="conf" type="{http://bmdp.shaolin.org/datamodel/Workflow}confType" minOccurs="0"/>
 *         &lt;element name="import" type="{http://bmdp.shaolin.org/datamodel/Workflow}flowImportType" maxOccurs="unbounded"/>
 *         &lt;element name="flow" type="{http://bmdp.shaolin.org/datamodel/Workflow}flowType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkflowType", propOrder = {
    "conf",
    "imports",
    "flows"
})
@XmlRootElement(name = "Workflow")
public class Workflow
    extends EntityType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected ConfType conf;
    @XmlElement(name = "import", required = true)
    protected List<FlowImportType> imports;
    @XmlElement(name = "flow", required = true)
    protected List<FlowType> flows;

    /**
     * Gets the value of the conf property.
     * 
     * @return
     *     possible object is
     *     {@link ConfType }
     *     
     */
    public ConfType getConf() {
        return conf;
    }

    /**
     * Sets the value of the conf property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfType }
     *     
     */
    public void setConf(ConfType value) {
        this.conf = value;
    }

    /**
     * Gets the value of the imports property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imports property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImports().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FlowImportType }
     * 
     * 
     */
    public List<FlowImportType> getImports() {
        if (imports == null) {
            imports = new ArrayList<FlowImportType>();
        }
        return this.imports;
    }

    /**
     * Gets the value of the flows property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flows property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlows().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FlowType }
     * 
     * 
     */
    public List<FlowType> getFlows() {
        if (flows == null) {
            flows = new ArrayList<FlowType>();
        }
        return this.flows;
    }

}