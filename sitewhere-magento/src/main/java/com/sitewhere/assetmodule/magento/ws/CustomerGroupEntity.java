
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customerGroupEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customerGroupEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customer_group_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="customer_group_code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customerGroupEntity", propOrder = {
    "customerGroupId",
    "customerGroupCode"
})
public class CustomerGroupEntity {

    @XmlElement(name = "customer_group_id")
    protected int customerGroupId;
    @XmlElement(name = "customer_group_code", required = true)
    protected String customerGroupCode;

    /**
     * Gets the value of the customerGroupId property.
     * 
     */
    public int getCustomerGroupId() {
        return customerGroupId;
    }

    /**
     * Sets the value of the customerGroupId property.
     * 
     */
    public void setCustomerGroupId(int value) {
        this.customerGroupId = value;
    }

    /**
     * Gets the value of the customerGroupCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerGroupCode() {
        return customerGroupCode;
    }

    /**
     * Sets the value of the customerGroupCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerGroupCode(String value) {
        this.customerGroupCode = value;
    }

}
