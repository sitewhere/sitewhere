
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for giftMessageAssociativeProductsEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="giftMessageAssociativeProductsEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="product" type="{urn:Magento}shoppingCartProductEntity"/>
 *         &lt;element name="message" type="{urn:Magento}giftMessageEntity"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "giftMessageAssociativeProductsEntity", propOrder = {
    "product",
    "message"
})
public class GiftMessageAssociativeProductsEntity {

    @XmlElement(required = true)
    protected ShoppingCartProductEntity product;
    @XmlElement(required = true)
    protected GiftMessageEntity message;

    /**
     * Gets the value of the product property.
     * 
     * @return
     *     possible object is
     *     {@link ShoppingCartProductEntity }
     *     
     */
    public ShoppingCartProductEntity getProduct() {
        return product;
    }

    /**
     * Sets the value of the product property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShoppingCartProductEntity }
     *     
     */
    public void setProduct(ShoppingCartProductEntity value) {
        this.product = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link GiftMessageEntity }
     *     
     */
    public GiftMessageEntity getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link GiftMessageEntity }
     *     
     */
    public void setMessage(GiftMessageEntity value) {
        this.message = value;
    }

}
