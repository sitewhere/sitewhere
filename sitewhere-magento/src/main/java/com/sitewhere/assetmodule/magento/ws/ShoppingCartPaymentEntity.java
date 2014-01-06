
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shoppingCartPaymentEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shoppingCartPaymentEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="payment_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_number_enc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_last4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_cid_enc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_exp_month" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_exp_year" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_ss_owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_ss_start_month" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_ss_start_year" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cc_ss_issue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="po_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additional_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additional_information" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shoppingCartPaymentEntity", propOrder = {
    "paymentId",
    "createdAt",
    "updatedAt",
    "method",
    "ccType",
    "ccNumberEnc",
    "ccLast4",
    "ccCidEnc",
    "ccOwner",
    "ccExpMonth",
    "ccExpYear",
    "ccSsOwner",
    "ccSsStartMonth",
    "ccSsStartYear",
    "ccSsIssue",
    "poNumber",
    "additionalData",
    "additionalInformation"
})
public class ShoppingCartPaymentEntity {

    @XmlElement(name = "payment_id")
    protected String paymentId;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "updated_at")
    protected String updatedAt;
    protected String method;
    @XmlElement(name = "cc_type")
    protected String ccType;
    @XmlElement(name = "cc_number_enc")
    protected String ccNumberEnc;
    @XmlElement(name = "cc_last4")
    protected String ccLast4;
    @XmlElement(name = "cc_cid_enc")
    protected String ccCidEnc;
    @XmlElement(name = "cc_owner")
    protected String ccOwner;
    @XmlElement(name = "cc_exp_month")
    protected String ccExpMonth;
    @XmlElement(name = "cc_exp_year")
    protected String ccExpYear;
    @XmlElement(name = "cc_ss_owner")
    protected String ccSsOwner;
    @XmlElement(name = "cc_ss_start_month")
    protected String ccSsStartMonth;
    @XmlElement(name = "cc_ss_start_year")
    protected String ccSsStartYear;
    @XmlElement(name = "cc_ss_issue")
    protected String ccSsIssue;
    @XmlElement(name = "po_number")
    protected String poNumber;
    @XmlElement(name = "additional_data")
    protected String additionalData;
    @XmlElement(name = "additional_information")
    protected String additionalInformation;

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
     * Gets the value of the ccCidEnc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcCidEnc() {
        return ccCidEnc;
    }

    /**
     * Sets the value of the ccCidEnc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcCidEnc(String value) {
        this.ccCidEnc = value;
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
     * Gets the value of the ccSsOwner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcSsOwner() {
        return ccSsOwner;
    }

    /**
     * Sets the value of the ccSsOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcSsOwner(String value) {
        this.ccSsOwner = value;
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
     * Gets the value of the ccSsIssue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcSsIssue() {
        return ccSsIssue;
    }

    /**
     * Sets the value of the ccSsIssue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcSsIssue(String value) {
        this.ccSsIssue = value;
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
     * Gets the value of the additionalData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalData() {
        return additionalData;
    }

    /**
     * Sets the value of the additionalData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalData(String value) {
        this.additionalData = value;
    }

    /**
     * Gets the value of the additionalInformation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * Sets the value of the additionalInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalInformation(String value) {
        this.additionalInformation = value;
    }

}
