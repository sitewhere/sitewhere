
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductAttributeOptionEntityToAdd complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductAttributeOptionEntityToAdd">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="label" type="{urn:Magento}catalogProductAttributeOptionLabelArray"/>
 *         &lt;element name="order" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="is_default" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductAttributeOptionEntityToAdd", propOrder = {
    "label",
    "order",
    "isDefault"
})
public class CatalogProductAttributeOptionEntityToAdd {

    @XmlElement(required = true)
    protected CatalogProductAttributeOptionLabelArray label;
    protected int order;
    @XmlElement(name = "is_default")
    protected int isDefault;

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductAttributeOptionLabelArray }
     *     
     */
    public CatalogProductAttributeOptionLabelArray getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductAttributeOptionLabelArray }
     *     
     */
    public void setLabel(CatalogProductAttributeOptionLabelArray value) {
        this.label = value;
    }

    /**
     * Gets the value of the order property.
     * 
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     * 
     */
    public void setOrder(int value) {
        this.order = value;
    }

    /**
     * Gets the value of the isDefault property.
     * 
     */
    public int getIsDefault() {
        return isDefault;
    }

    /**
     * Sets the value of the isDefault property.
     * 
     */
    public void setIsDefault(int value) {
        this.isDefault = value;
    }

}
