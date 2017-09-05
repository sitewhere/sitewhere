
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
 *         &lt;element name="itemsQty" type="{urn:Magento}orderItemIdQtyArray"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="includeComment" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "itemsQty",
    "comment",
    "email",
    "includeComment"
})
@XmlRootElement(name = "salesOrderShipmentCreateRequestParam")
public class SalesOrderShipmentCreateRequestParam {

    @XmlElement(required = true)
    protected String sessionId;
    @XmlElement(required = true)
    protected String orderIncrementId;
    @XmlElement(required = true)
    protected OrderItemIdQtyArray itemsQty;
    protected String comment;
    protected int email;
    protected int includeComment;

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
     * Gets the value of the itemsQty property.
     * 
     * @return
     *     possible object is
     *     {@link OrderItemIdQtyArray }
     *     
     */
    public OrderItemIdQtyArray getItemsQty() {
        return itemsQty;
    }

    /**
     * Sets the value of the itemsQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderItemIdQtyArray }
     *     
     */
    public void setItemsQty(OrderItemIdQtyArray value) {
        this.itemsQty = value;
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
     * Gets the value of the email property.
     * 
     */
    public int getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     */
    public void setEmail(int value) {
        this.email = value;
    }

    /**
     * Gets the value of the includeComment property.
     * 
     */
    public int getIncludeComment() {
        return includeComment;
    }

    /**
     * Sets the value of the includeComment property.
     * 
     */
    public void setIncludeComment(int value) {
        this.includeComment = value;
    }

}
