
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for salesOrderCreditmemoData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="salesOrderCreditmemoData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="qtys" type="{urn:Magento}orderItemIdQtyArray" minOccurs="0"/>
 *         &lt;element name="shipping_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="adjustment_positive" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="adjustment_negative" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "salesOrderCreditmemoData", propOrder = {
    "qtys",
    "shippingAmount",
    "adjustmentPositive",
    "adjustmentNegative"
})
public class SalesOrderCreditmemoData {

    protected OrderItemIdQtyArray qtys;
    @XmlElement(name = "shipping_amount")
    protected Double shippingAmount;
    @XmlElement(name = "adjustment_positive")
    protected Double adjustmentPositive;
    @XmlElement(name = "adjustment_negative")
    protected Double adjustmentNegative;

    /**
     * Gets the value of the qtys property.
     * 
     * @return
     *     possible object is
     *     {@link OrderItemIdQtyArray }
     *     
     */
    public OrderItemIdQtyArray getQtys() {
        return qtys;
    }

    /**
     * Sets the value of the qtys property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderItemIdQtyArray }
     *     
     */
    public void setQtys(OrderItemIdQtyArray value) {
        this.qtys = value;
    }

    /**
     * Gets the value of the shippingAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getShippingAmount() {
        return shippingAmount;
    }

    /**
     * Sets the value of the shippingAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setShippingAmount(Double value) {
        this.shippingAmount = value;
    }

    /**
     * Gets the value of the adjustmentPositive property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAdjustmentPositive() {
        return adjustmentPositive;
    }

    /**
     * Sets the value of the adjustmentPositive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAdjustmentPositive(Double value) {
        this.adjustmentPositive = value;
    }

    /**
     * Gets the value of the adjustmentNegative property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAdjustmentNegative() {
        return adjustmentNegative;
    }

    /**
     * Sets the value of the adjustmentNegative property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAdjustmentNegative(Double value) {
        this.adjustmentNegative = value;
    }

}
