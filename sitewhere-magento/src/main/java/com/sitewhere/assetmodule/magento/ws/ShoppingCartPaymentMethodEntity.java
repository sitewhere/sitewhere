
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shoppingCartPaymentMethodEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shoppingCartPaymentMethodEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="po_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_cid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_exp_year" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_exp_month" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shoppingCartPaymentMethodEntity", propOrder = {
    "poNumber",
    "method",
    "ccCid",
    "ccOwner",
    "ccNumber",
    "ccType",
    "ccExpYear",
    "ccExpMonth"
})
public class ShoppingCartPaymentMethodEntity {

    @XmlElement(name = "po_number")
    protected String poNumber;
    protected String method;
    @XmlElement(name = "cc_cid")
    protected String ccCid;
    @XmlElement(name = "cc_owner")
    protected String ccOwner;
    @XmlElement(name = "cc_number")
    protected String ccNumber;
    @XmlElement(name = "cc_type")
    protected String ccType;
    @XmlElement(name = "cc_exp_year")
    protected String ccExpYear;
    @XmlElement(name = "cc_exp_month")
    protected String ccExpMonth;

    /**
     * Gets the value of the poNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPoNumber() {
        return poNumber;
    }

    /**
     * Sets the value of the poNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPoNumber(String value) {
        this.poNumber = value;
    }

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethod(String value) {
        this.method = value;
    }

    /**
     * Gets the value of the ccCid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcCid() {
        return ccCid;
    }

    /**
     * Sets the value of the ccCid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcCid(String value) {
        this.ccCid = value;
    }

    /**
     * Gets the value of the ccOwner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcOwner() {
        return ccOwner;
    }

    /**
     * Sets the value of the ccOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcOwner(String value) {
        this.ccOwner = value;
    }

    /**
     * Gets the value of the ccNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcNumber() {
        return ccNumber;
    }

    /**
     * Sets the value of the ccNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcNumber(String value) {
        this.ccNumber = value;
    }

    /**
     * Gets the value of the ccType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcType() {
        return ccType;
    }

    /**
     * Sets the value of the ccType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcType(String value) {
        this.ccType = value;
    }

    /**
     * Gets the value of the ccExpYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcExpYear() {
        return ccExpYear;
    }

    /**
     * Sets the value of the ccExpYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcExpYear(String value) {
        this.ccExpYear = value;
    }

    /**
     * Gets the value of the ccExpMonth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcExpMonth() {
        return ccExpMonth;
    }

    /**
     * Sets the value of the ccExpMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcExpMonth(String value) {
        this.ccExpMonth = value;
    }

}
