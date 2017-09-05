
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for salesOrderItemEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="salesOrderItemEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="item_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="order_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quote_item_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="product_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="product_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="product_options" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_virtual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sku" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="applied_rule_ids" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="free_shipping" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_qty_decimal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="no_discount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty_ordered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty_shipped" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="original_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_original_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_percent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_percent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_discount_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amount_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_amount_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="row_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_row_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="row_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_row_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="row_weight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message_available" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_before_discount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_before_discount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied_row_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_applied_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_applied_row_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_row_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_row_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "salesOrderItemEntity", propOrder = {
    "itemId",
    "orderId",
    "quoteItemId",
    "createdAt",
    "updatedAt",
    "productId",
    "productType",
    "productOptions",
    "weight",
    "isVirtual",
    "sku",
    "name",
    "appliedRuleIds",
    "freeShipping",
    "isQtyDecimal",
    "noDiscount",
    "qtyCanceled",
    "qtyInvoiced",
    "qtyOrdered",
    "qtyRefunded",
    "qtyShipped",
    "cost",
    "price",
    "basePrice",
    "originalPrice",
    "baseOriginalPrice",
    "taxPercent",
    "taxAmount",
    "baseTaxAmount",
    "taxInvoiced",
    "baseTaxInvoiced",
    "discountPercent",
    "discountAmount",
    "baseDiscountAmount",
    "discountInvoiced",
    "baseDiscountInvoiced",
    "amountRefunded",
    "baseAmountRefunded",
    "rowTotal",
    "baseRowTotal",
    "rowInvoiced",
    "baseRowInvoiced",
    "rowWeight",
    "giftMessageId",
    "giftMessage",
    "giftMessageAvailable",
    "baseTaxBeforeDiscount",
    "taxBeforeDiscount",
    "weeeTaxApplied",
    "weeeTaxAppliedAmount",
    "weeeTaxAppliedRowAmount",
    "baseWeeeTaxAppliedAmount",
    "baseWeeeTaxAppliedRowAmount",
    "weeeTaxDisposition",
    "weeeTaxRowDisposition",
    "baseWeeeTaxDisposition",
    "baseWeeeTaxRowDisposition"
})
public class SalesOrderItemEntity {

    @XmlElement(name = "item_id")
    protected String itemId;
    @XmlElement(name = "order_id")
    protected String orderId;
    @XmlElement(name = "quote_item_id")
    protected String quoteItemId;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "updated_at")
    protected String updatedAt;
    @XmlElement(name = "product_id")
    protected String productId;
    @XmlElement(name = "product_type")
    protected String productType;
    @XmlElement(name = "product_options")
    protected String productOptions;
    protected String weight;
    @XmlElement(name = "is_virtual")
    protected String isVirtual;
    protected String sku;
    protected String name;
    @XmlElement(name = "applied_rule_ids")
    protected String appliedRuleIds;
    @XmlElement(name = "free_shipping")
    protected String freeShipping;
    @XmlElement(name = "is_qty_decimal")
    protected String isQtyDecimal;
    @XmlElement(name = "no_discount")
    protected String noDiscount;
    @XmlElement(name = "qty_canceled")
    protected String qtyCanceled;
    @XmlElement(name = "qty_invoiced")
    protected String qtyInvoiced;
    @XmlElement(name = "qty_ordered")
    protected String qtyOrdered;
    @XmlElement(name = "qty_refunded")
    protected String qtyRefunded;
    @XmlElement(name = "qty_shipped")
    protected String qtyShipped;
    protected String cost;
    protected String price;
    @XmlElement(name = "base_price")
    protected String basePrice;
    @XmlElement(name = "original_price")
    protected String originalPrice;
    @XmlElement(name = "base_original_price")
    protected String baseOriginalPrice;
    @XmlElement(name = "tax_percent")
    protected String taxPercent;
    @XmlElement(name = "tax_amount")
    protected String taxAmount;
    @XmlElement(name = "base_tax_amount")
    protected String baseTaxAmount;
    @XmlElement(name = "tax_invoiced")
    protected String taxInvoiced;
    @XmlElement(name = "base_tax_invoiced")
    protected String baseTaxInvoiced;
    @XmlElement(name = "discount_percent")
    protected String discountPercent;
    @XmlElement(name = "discount_amount")
    protected String discountAmount;
    @XmlElement(name = "base_discount_amount")
    protected String baseDiscountAmount;
    @XmlElement(name = "discount_invoiced")
    protected String discountInvoiced;
    @XmlElement(name = "base_discount_invoiced")
    protected String baseDiscountInvoiced;
    @XmlElement(name = "amount_refunded")
    protected String amountRefunded;
    @XmlElement(name = "base_amount_refunded")
    protected String baseAmountRefunded;
    @XmlElement(name = "row_total")
    protected String rowTotal;
    @XmlElement(name = "base_row_total")
    protected String baseRowTotal;
    @XmlElement(name = "row_invoiced")
    protected String rowInvoiced;
    @XmlElement(name = "base_row_invoiced")
    protected String baseRowInvoiced;
    @XmlElement(name = "row_weight")
    protected String rowWeight;
    @XmlElement(name = "gift_message_id")
    protected String giftMessageId;
    @XmlElement(name = "gift_message")
    protected String giftMessage;
    @XmlElement(name = "gift_message_available")
    protected String giftMessageAvailable;
    @XmlElement(name = "base_tax_before_discount")
    protected String baseTaxBeforeDiscount;
    @XmlElement(name = "tax_before_discount")
    protected String taxBeforeDiscount;
    @XmlElement(name = "weee_tax_applied")
    protected String weeeTaxApplied;
    @XmlElement(name = "weee_tax_applied_amount")
    protected String weeeTaxAppliedAmount;
    @XmlElement(name = "weee_tax_applied_row_amount")
    protected String weeeTaxAppliedRowAmount;
    @XmlElement(name = "base_weee_tax_applied_amount")
    protected String baseWeeeTaxAppliedAmount;
    @XmlElement(name = "base_weee_tax_applied_row_amount")
    protected String baseWeeeTaxAppliedRowAmount;
    @XmlElement(name = "weee_tax_disposition")
    protected String weeeTaxDisposition;
    @XmlElement(name = "weee_tax_row_disposition")
    protected String weeeTaxRowDisposition;
    @XmlElement(name = "base_weee_tax_disposition")
    protected String baseWeeeTaxDisposition;
    @XmlElement(name = "base_weee_tax_row_disposition")
    protected String baseWeeeTaxRowDisposition;

    /**
     * Gets the value of the itemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Sets the value of the itemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemId(String value) {
        this.itemId = value;
    }

    /**
     * Gets the value of the orderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of the orderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderId(String value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the quoteItemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuoteItemId() {
        return quoteItemId;
    }

    /**
     * Sets the value of the quoteItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuoteItemId(String value) {
        this.quoteItemId = value;
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
     * Gets the value of the productId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the value of the productId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductId(String value) {
        this.productId = value;
    }

    /**
     * Gets the value of the productType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Sets the value of the productType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductType(String value) {
        this.productType = value;
    }

    /**
     * Gets the value of the productOptions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductOptions() {
        return productOptions;
    }

    /**
     * Sets the value of the productOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductOptions(String value) {
        this.productOptions = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeight(String value) {
        this.weight = value;
    }

    /**
     * Gets the value of the isVirtual property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsVirtual() {
        return isVirtual;
    }

    /**
     * Sets the value of the isVirtual property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsVirtual(String value) {
        this.isVirtual = value;
    }

    /**
     * Gets the value of the sku property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the value of the sku property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSku(String value) {
        this.sku = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the appliedRuleIds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppliedRuleIds() {
        return appliedRuleIds;
    }

    /**
     * Sets the value of the appliedRuleIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppliedRuleIds(String value) {
        this.appliedRuleIds = value;
    }

    /**
     * Gets the value of the freeShipping property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFreeShipping() {
        return freeShipping;
    }

    /**
     * Sets the value of the freeShipping property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFreeShipping(String value) {
        this.freeShipping = value;
    }

    /**
     * Gets the value of the isQtyDecimal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsQtyDecimal() {
        return isQtyDecimal;
    }

    /**
     * Sets the value of the isQtyDecimal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsQtyDecimal(String value) {
        this.isQtyDecimal = value;
    }

    /**
     * Gets the value of the noDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoDiscount() {
        return noDiscount;
    }

    /**
     * Sets the value of the noDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoDiscount(String value) {
        this.noDiscount = value;
    }

    /**
     * Gets the value of the qtyCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyCanceled() {
        return qtyCanceled;
    }

    /**
     * Sets the value of the qtyCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyCanceled(String value) {
        this.qtyCanceled = value;
    }

    /**
     * Gets the value of the qtyInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyInvoiced() {
        return qtyInvoiced;
    }

    /**
     * Sets the value of the qtyInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyInvoiced(String value) {
        this.qtyInvoiced = value;
    }

    /**
     * Gets the value of the qtyOrdered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyOrdered() {
        return qtyOrdered;
    }

    /**
     * Sets the value of the qtyOrdered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyOrdered(String value) {
        this.qtyOrdered = value;
    }

    /**
     * Gets the value of the qtyRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyRefunded() {
        return qtyRefunded;
    }

    /**
     * Sets the value of the qtyRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyRefunded(String value) {
        this.qtyRefunded = value;
    }

    /**
     * Gets the value of the qtyShipped property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyShipped() {
        return qtyShipped;
    }

    /**
     * Sets the value of the qtyShipped property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyShipped(String value) {
        this.qtyShipped = value;
    }

    /**
     * Gets the value of the cost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCost() {
        return cost;
    }

    /**
     * Sets the value of the cost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCost(String value) {
        this.cost = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrice(String value) {
        this.price = value;
    }

    /**
     * Gets the value of the basePrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBasePrice() {
        return basePrice;
    }

    /**
     * Sets the value of the basePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBasePrice(String value) {
        this.basePrice = value;
    }

    /**
     * Gets the value of the originalPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalPrice() {
        return originalPrice;
    }

    /**
     * Sets the value of the originalPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalPrice(String value) {
        this.originalPrice = value;
    }

    /**
     * Gets the value of the baseOriginalPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseOriginalPrice() {
        return baseOriginalPrice;
    }

    /**
     * Sets the value of the baseOriginalPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseOriginalPrice(String value) {
        this.baseOriginalPrice = value;
    }

    /**
     * Gets the value of the taxPercent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxPercent() {
        return taxPercent;
    }

    /**
     * Sets the value of the taxPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxPercent(String value) {
        this.taxPercent = value;
    }

    /**
     * Gets the value of the taxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxAmount() {
        return taxAmount;
    }

    /**
     * Sets the value of the taxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxAmount(String value) {
        this.taxAmount = value;
    }

    /**
     * Gets the value of the baseTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTaxAmount() {
        return baseTaxAmount;
    }

    /**
     * Sets the value of the baseTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTaxAmount(String value) {
        this.baseTaxAmount = value;
    }

    /**
     * Gets the value of the taxInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxInvoiced() {
        return taxInvoiced;
    }

    /**
     * Sets the value of the taxInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxInvoiced(String value) {
        this.taxInvoiced = value;
    }

    /**
     * Gets the value of the baseTaxInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTaxInvoiced() {
        return baseTaxInvoiced;
    }

    /**
     * Sets the value of the baseTaxInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTaxInvoiced(String value) {
        this.baseTaxInvoiced = value;
    }

    /**
     * Gets the value of the discountPercent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Sets the value of the discountPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountPercent(String value) {
        this.discountPercent = value;
    }

    /**
     * Gets the value of the discountAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sets the value of the discountAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountAmount(String value) {
        this.discountAmount = value;
    }

    /**
     * Gets the value of the baseDiscountAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseDiscountAmount() {
        return baseDiscountAmount;
    }

    /**
     * Sets the value of the baseDiscountAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseDiscountAmount(String value) {
        this.baseDiscountAmount = value;
    }

    /**
     * Gets the value of the discountInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountInvoiced() {
        return discountInvoiced;
    }

    /**
     * Sets the value of the discountInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountInvoiced(String value) {
        this.discountInvoiced = value;
    }

    /**
     * Gets the value of the baseDiscountInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseDiscountInvoiced() {
        return baseDiscountInvoiced;
    }

    /**
     * Sets the value of the baseDiscountInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseDiscountInvoiced(String value) {
        this.baseDiscountInvoiced = value;
    }

    /**
     * Gets the value of the amountRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountRefunded() {
        return amountRefunded;
    }

    /**
     * Sets the value of the amountRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountRefunded(String value) {
        this.amountRefunded = value;
    }

    /**
     * Gets the value of the baseAmountRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseAmountRefunded() {
        return baseAmountRefunded;
    }

    /**
     * Sets the value of the baseAmountRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseAmountRefunded(String value) {
        this.baseAmountRefunded = value;
    }

    /**
     * Gets the value of the rowTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowTotal() {
        return rowTotal;
    }

    /**
     * Sets the value of the rowTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowTotal(String value) {
        this.rowTotal = value;
    }

    /**
     * Gets the value of the baseRowTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseRowTotal() {
        return baseRowTotal;
    }

    /**
     * Sets the value of the baseRowTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseRowTotal(String value) {
        this.baseRowTotal = value;
    }

    /**
     * Gets the value of the rowInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowInvoiced() {
        return rowInvoiced;
    }

    /**
     * Sets the value of the rowInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowInvoiced(String value) {
        this.rowInvoiced = value;
    }

    /**
     * Gets the value of the baseRowInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseRowInvoiced() {
        return baseRowInvoiced;
    }

    /**
     * Sets the value of the baseRowInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseRowInvoiced(String value) {
        this.baseRowInvoiced = value;
    }

    /**
     * Gets the value of the rowWeight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowWeight() {
        return rowWeight;
    }

    /**
     * Sets the value of the rowWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowWeight(String value) {
        this.rowWeight = value;
    }

    /**
     * Gets the value of the giftMessageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftMessageId() {
        return giftMessageId;
    }

    /**
     * Sets the value of the giftMessageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftMessageId(String value) {
        this.giftMessageId = value;
    }

    /**
     * Gets the value of the giftMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftMessage() {
        return giftMessage;
    }

    /**
     * Sets the value of the giftMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftMessage(String value) {
        this.giftMessage = value;
    }

    /**
     * Gets the value of the giftMessageAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftMessageAvailable() {
        return giftMessageAvailable;
    }

    /**
     * Sets the value of the giftMessageAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftMessageAvailable(String value) {
        this.giftMessageAvailable = value;
    }

    /**
     * Gets the value of the baseTaxBeforeDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTaxBeforeDiscount() {
        return baseTaxBeforeDiscount;
    }

    /**
     * Sets the value of the baseTaxBeforeDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTaxBeforeDiscount(String value) {
        this.baseTaxBeforeDiscount = value;
    }

    /**
     * Gets the value of the taxBeforeDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxBeforeDiscount() {
        return taxBeforeDiscount;
    }

    /**
     * Sets the value of the taxBeforeDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxBeforeDiscount(String value) {
        this.taxBeforeDiscount = value;
    }

    /**
     * Gets the value of the weeeTaxApplied property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeeeTaxApplied() {
        return weeeTaxApplied;
    }

    /**
     * Sets the value of the weeeTaxApplied property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeeeTaxApplied(String value) {
        this.weeeTaxApplied = value;
    }

    /**
     * Gets the value of the weeeTaxAppliedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeeeTaxAppliedAmount() {
        return weeeTaxAppliedAmount;
    }

    /**
     * Sets the value of the weeeTaxAppliedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeeeTaxAppliedAmount(String value) {
        this.weeeTaxAppliedAmount = value;
    }

    /**
     * Gets the value of the weeeTaxAppliedRowAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeeeTaxAppliedRowAmount() {
        return weeeTaxAppliedRowAmount;
    }

    /**
     * Sets the value of the weeeTaxAppliedRowAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeeeTaxAppliedRowAmount(String value) {
        this.weeeTaxAppliedRowAmount = value;
    }

    /**
     * Gets the value of the baseWeeeTaxAppliedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseWeeeTaxAppliedAmount() {
        return baseWeeeTaxAppliedAmount;
    }

    /**
     * Sets the value of the baseWeeeTaxAppliedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseWeeeTaxAppliedAmount(String value) {
        this.baseWeeeTaxAppliedAmount = value;
    }

    /**
     * Gets the value of the baseWeeeTaxAppliedRowAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseWeeeTaxAppliedRowAmount() {
        return baseWeeeTaxAppliedRowAmount;
    }

    /**
     * Sets the value of the baseWeeeTaxAppliedRowAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseWeeeTaxAppliedRowAmount(String value) {
        this.baseWeeeTaxAppliedRowAmount = value;
    }

    /**
     * Gets the value of the weeeTaxDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeeeTaxDisposition() {
        return weeeTaxDisposition;
    }

    /**
     * Sets the value of the weeeTaxDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeeeTaxDisposition(String value) {
        this.weeeTaxDisposition = value;
    }

    /**
     * Gets the value of the weeeTaxRowDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeeeTaxRowDisposition() {
        return weeeTaxRowDisposition;
    }

    /**
     * Sets the value of the weeeTaxRowDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeeeTaxRowDisposition(String value) {
        this.weeeTaxRowDisposition = value;
    }

    /**
     * Gets the value of the baseWeeeTaxDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseWeeeTaxDisposition() {
        return baseWeeeTaxDisposition;
    }

    /**
     * Sets the value of the baseWeeeTaxDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseWeeeTaxDisposition(String value) {
        this.baseWeeeTaxDisposition = value;
    }

    /**
     * Gets the value of the baseWeeeTaxRowDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseWeeeTaxRowDisposition() {
        return baseWeeeTaxRowDisposition;
    }

    /**
     * Sets the value of the baseWeeeTaxRowDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseWeeeTaxRowDisposition(String value) {
        this.baseWeeeTaxRowDisposition = value;
    }

}
