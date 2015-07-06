//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.27 at 02:36:42 PM CST 
//


package org.shaolin.bmdp.datamodel.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.CompilationUnit;


/**
 * <p>Java class for ExpressionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExpressionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://bmdp.shaolin.org/datamodel/Common}ItemRefType">
 *       &lt;sequence>
 *         &lt;element name="expressionString" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExpressionType", propOrder = {
    "expressionString"
})
public class ExpressionType
    extends ItemRefType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String expressionString;

    /**
     * Gets the value of the expressionString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpressionString() {
        return expressionString;
    }

    /**
     * Sets the value of the expressionString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpressionString(String value) {
        this.expressionString = value;
    }
    
    @XmlTransient
	private Object unit = null;

	public void parse(ParsingContext context) throws ParsingException {
		if (unit == null) {
			unit = StatementParser.parse(getExpressionString(), context);
		}
	}

	public boolean isParsed() {
		return unit == null ? false : true;
	}

	public Class<?> getValueClass() throws ParsingException {
		if (unit == null) {
			return null;
		} else {
			return ((CompilationUnit)unit).getValueClass();
		}
	}

	public Object evaluate(EvaluationContext context)
			throws EvaluationException {
		return StatementEvaluator.evaluate((CompilationUnit)unit, context);
	}

}