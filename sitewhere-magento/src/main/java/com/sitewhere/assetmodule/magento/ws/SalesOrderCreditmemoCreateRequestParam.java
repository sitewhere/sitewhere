
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="orderIncrementId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="creditmemoData" type="{urn:Magento}salesOrderCreditmemoData" minOccurs="0"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notifyCustomer" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="includeComment" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="refundToStoreCreditAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sessionId",
    "orderIncrementId",
    "creditmemoData",
    "comment",
    "notifyCustomer",
    "includeComment",
    "refundToStoreCreditAmount"
})
@XmlRootElement(name = "salesOrderCreditmemoCreateRequestParam")
public class SalesOrderCreditmemoCreateRequestParam {

    @XmlElement(required = true)
    protected String sessionId;
    @XmlElement(required = true)
    protected String orderIncrementId;
    protected SalesOrderCreditmemoData creditmemoData;
    protected String comment;
    protected Integer notifyCustomer;
    protected Integer includeComment;
    protected String refundToStoreCreditAmount;

    /**
     * Gets the value of the sessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the orderIncrementId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderIncrementId() {
        return orderIncrementId;
    }

    /**
     * Sets the value of the orderIncrementId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderIncrementId(String value) {
        this.orderIncrementId = value;
    }

    /**
     * Gets the value of the creditmemoData property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrderCreditmemoData }
     *     
     */
    public SalesOrderCreditmemoData getCreditmemoData() {
        return creditmemoData;
    }

    /**
     * Sets the value of the creditmemoData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrderCreditmemoData }
     *     
     */
    public void setCreditmemoData(SalesOrderCreditmemoData value) {
        this.creditmemoData = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the notifyCustomer property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNotifyCustomer() {
        return notifyCustomer;
    }

    /**
     * Sets the value of the notifyCustomer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNotifyCustomer(Integer value) {
        this.notifyCustomer = value;
    }

    /**
     * Gets the value of the includeComment property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIncludeComment() {
        return includeComment;
    }

    /**
     * Sets the value of the includeComment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIncludeComment(Integer value) {
        this.includeComment = value;
    }

    /**
     * Gets the value of the refundToStoreCreditAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefundToStoreCreditAmount() {
        return refundToStoreCreditAmount;
    }

    /**
     * Sets the value of the refundToStoreCreditAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefundToStoreCreditAmount(String value) {
        this.refundToStoreCreditAmount = value;
    }

}
