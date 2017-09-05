
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for salesOrderPaymentEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="salesOrderPaymentEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="increment_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parent_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_active" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amount_ordered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_amount_ordered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="po_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_number_enc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_last4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_exp_month" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_exp_year" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_ss_start_month" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_ss_start_year" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="payment_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "salesOrderPaymentEntity", propOrder = {
    "incrementId",
    "parentId",
    "createdAt",
    "updatedAt",
    "isActive",
    "amountOrdered",
    "shippingAmount",
    "baseAmountOrdered",
    "baseShippingAmount",
    "method",
    "poNumber",
    "ccType",
    "ccNumberEnc",
    "ccLast4",
    "ccOwner",
    "ccExpMonth",
    "ccExpYear",
    "ccSsStartMonth",
    "ccSsStartYear",
    "paymentId"
})
public class SalesOrderPaymentEntity {

    @XmlElement(name = "increment_id")
    protected String incrementId;
    @XmlElement(name = "parent_id")
    protected String parentId;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "updated_at")
    protected String updatedAt;
    @XmlElement(name = "is_active")
    protected String isActive;
    @XmlElement(name = "amount_ordered")
    protected String amountOrdered;
    @XmlElement(name = "shipping_amount")
    protected String shippingAmount;
    @XmlElement(name = "base_amount_ordered")
    protected String baseAmountOrdered;
    @XmlElement(name = "base_shipping_amount")
    protected String baseShippingAmount;
    protected String method;
    @XmlElement(name = "po_number")
    protected String poNumber;
    @XmlElement(name = "cc_type")
    protected String ccType;
    @XmlElement(name = "cc_number_enc")
    protected String ccNumberEnc;
    @XmlElement(name = "cc_last4")
    protected String ccLast4;
    @XmlElement(name = "cc_owner")
    protected String ccOwner;
    @XmlElement(name = "cc_exp_month")
    protected String ccExpMonth;
    @XmlElement(name = "cc_exp_year")
    protected String ccExpYear;
    @XmlElement(name = "cc_ss_start_month")
    protected String ccSsStartMonth;
    @XmlElement(name = "cc_ss_start_year")
    protected String ccSsStartYear;
    @XmlElement(name = "payment_id")
    protected String paymentId;

    /**
     * Gets the value of the incrementId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncrementId() {
        return incrementId;
    }

    /**
     * Sets the value of the incrementId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncrementId(String value) {
        this.incrementId = value;
    }

    /**
     * Gets the value of the parentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Sets the value of the parentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentId(String value) {
        this.parentId = value;
    }

    /**
     * Gets the value of the createdAt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the value of the createdAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedAt(String value) {
        this.createdAt = value;
    }

    /**
     * Gets the value of the updatedAt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the value of the updatedAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedAt(String value) {
        this.updatedAt = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsActive(String value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the amountOrdered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountOrdered() {
        return amountOrdered;
    }

    /**
     * Sets the value of the amountOrdered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountOrdered(String value) {
        this.amountOrdered = value;
    }

    /**
     * Gets the value of the shippingAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingAmount() {
        return shippingAmount;
    }

    /**
     * Sets the value of the shippingAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingAmount(String value) {
        this.shippingAmount = value;
    }

    /**
     * Gets the value of the baseAmountOrdered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseAmountOrdered() {
        return baseAmountOrdered;
    }

    /**
     * Sets the value of the baseAmountOrdered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseAmountOrdered(String value) {
        this.baseAmountOrdered = value;
    }

    /**
     * Gets the value of the baseShippingAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingAmount() {
        return baseShippingAmount;
    }

    /**
     * Sets the value of the baseShippingAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingAmount(String value) {
        this.baseShippingAmount = value;
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
     * Gets the value of the ccNumberEnc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcNumberEnc() {
        return ccNumberEnc;
    }

    /**
     * Sets the value of the ccNumberEnc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcNumberEnc(String value) {
        this.ccNumberEnc = value;
    }

    /**
     * Gets the value of the ccLast4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcLast4() {
        return ccLast4;
    }

    /**
     * Sets the value of the ccLast4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcLast4(String value) {
        this.ccLast4 = value;
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
     * Gets the value of the ccSsStartMonth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcSsStartMonth() {
        return ccSsStartMonth;
    }

    /**
     * Sets the value of the ccSsStartMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcSsStartMonth(String value) {
        this.ccSsStartMonth = value;
    }

    /**
     * Gets the value of the ccSsStartYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcSsStartYear() {
        return ccSsStartYear;
    }

    /**
     * Sets the value of the ccSsStartYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcSsStartYear(String value) {
        this.ccSsStartYear = value;
    }

    /**
     * Gets the value of the paymentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the value of the paymentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentId(String value) {
        this.paymentId = value;
    }

}
