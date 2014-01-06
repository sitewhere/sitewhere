
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filters">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filter" type="{urn:Magento}associativeArray" minOccurs="0"/>
 *         &lt;element name="complex_filter" type="{urn:Magento}complexFilterArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filters", propOrder = {
    "filter",
    "complexFilter"
})
public class Filters {

    protected AssociativeArray filter;
    @XmlElement(name = "complex_filter")
    protected ComplexFilterArray complexFilter;

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link AssociativeArray }
     *     
     */
    public AssociativeArray getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssociativeArray }
     *     
     */
    public void setFilter(AssociativeArray value) {
        this.filter = value;
    }

    /**
     * Gets the value of the complexFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ComplexFilterArray }
     *     
     */
    public ComplexFilterArray getComplexFilter() {
        return complexFilter;
    }

    /**
     * Sets the value of the complexFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComplexFilterArray }
     *     
     */
    public void setComplexFilter(ComplexFilterArray value) {
        this.complexFilter = value;
    }

}
