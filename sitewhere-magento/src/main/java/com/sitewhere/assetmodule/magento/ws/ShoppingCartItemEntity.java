
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shoppingCartItemEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shoppingCartItemEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="item_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="product_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parent_item_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_virtual" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sku" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="applied_rule_ids" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additional_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="free_shipping" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_qty_decimal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="no_discount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="qty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_price" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="custom_price" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="discount_percent" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="discount_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_discount_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="tax_percent" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="tax_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="row_total" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_row_total" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="row_total_with_discount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="row_weight" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="product_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_before_discount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="tax_before_discount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="original_custom_price" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_cost" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="price_incl_tax" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_price_incl_tax" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="row_total_incl_tax" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_row_total_incl_tax" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gift_message_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message_available" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied_row_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_applied_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_applied_row_amount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="weee_tax_disposition" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="weee_tax_row_disposition" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_disposition" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_row_disposition" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="tax_class_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shoppingCartItemEntity", propOrder = {
    "itemId",
    "createdAt",
    "updatedAt",
    "productId",
    "storeId",
    "parentItemId",
    "isVirtual",
    "sku",
    "name",
    "description",
    "appliedRuleIds",
    "additionalData",
    "freeShipping",
    "isQtyDecimal",
    "noDiscount",
    "weight",
    "qty",
    "price",
    "basePrice",
    "customPrice",
    "discountPercent",
    "discountAmount",
    "baseDiscountAmount",
    "taxPercent",
    "taxAmount",
    "baseTaxAmount",
    "rowTotal",
    "baseRowTotal",
    "rowTotalWithDiscount",
    "rowWeight",
    "productType",
    "baseTaxBeforeDiscount",
    "taxBeforeDiscount",
    "originalCustomPrice",
    "baseCost",
    "priceInclTax",
    "basePriceInclTax",
    "rowTotalInclTax",
    "baseRowTotalInclTax",
    "giftMessageId",
    "giftMessage",
    "giftMessageAvailable",
    "weeeTaxApplied",
    "weeeTaxAppliedAmount",
    "weeeTaxAppliedRowAmount",
    "baseWeeeTaxAppliedAmount",
    "baseWeeeTaxAppliedRowAmount",
    "weeeTaxDisposition",
    "weeeTaxRowDisposition",
    "baseWeeeTaxDisposition",
    "baseWeeeTaxRowDisposition",
    "taxClassId"
})
public class ShoppingCartItemEntity {

    @XmlElement(name = "item_id")
    protected String itemId;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "updated_at")
    protected String updatedAt;
    @XmlElement(name = "product_id")
    protected String productId;
    @XmlElement(name = "store_id")
    protected String storeId;
    @XmlElement(name = "parent_item_id")
    protected String parentItemId;
    @XmlElement(name = "is_virtual")
    protected Integer isVirtual;
    protected String sku;
    protected String name;
    protected String description;
    @XmlElement(name = "applied_rule_ids")
    protected String appliedRuleIds;
    @XmlElement(name = "additional_data")
    protected String additionalData;
    @XmlElement(name = "free_shipping")
    protected String freeShipping;
    @XmlElement(name = "is_qty_decimal")
    protected String isQtyDecimal;
    @XmlElement(name = "no_discount")
    protected String noDiscount;
    protected Double weight;
    protected Double qty;
    protected Double price;
    @XmlElement(name = "base_price")
    protected Double basePrice;
    @XmlElement(name = "custom_price")
    protected Double customPrice;
    @XmlElement(name = "discount_percent")
    protected Double discountPercent;
    @XmlElement(name = "discount_amount")
    protected Double discountAmount;
    @XmlElement(name = "base_discount_amount")
    protected Double baseDiscountAmount;
    @XmlElement(name = "tax_percent")
    protected Double taxPercent;
    @XmlElement(name = "tax_amount")
    protected Double taxAmount;
    @XmlElement(name = "base_tax_amount")
    protected Double baseTaxAmount;
    @XmlElement(name = "row_total")
    protected Double rowTotal;
    @XmlElement(name = "base_row_total")
    protected Double baseRowTotal;
    @XmlElement(name = "row_total_with_discount")
    protected Double rowTotalWithDiscount;
    @XmlElement(name = "row_weight")
    protected Double rowWeight;
    @XmlElement(name = "product_type")
    protected String productType;
    @XmlElement(name = "base_tax_before_discount")
    protected Double baseTaxBeforeDiscount;
    @XmlElement(name = "tax_before_discount")
    protected Double taxBeforeDiscount;
    @XmlElement(name = "original_custom_price")
    protected Double originalCustomPrice;
    @XmlElement(name = "base_cost")
    protected Double baseCost;
    @XmlElement(name = "price_incl_tax")
    protected Double priceInclTax;
    @XmlElement(name = "base_price_incl_tax")
    protected Double basePriceInclTax;
    @XmlElement(name = "row_total_incl_tax")
    protected Double rowTotalInclTax;
    @XmlElement(name = "base_row_total_incl_tax")
    protected Double baseRowTotalInclTax;
    @XmlElement(name = "gift_message_id")
    protected String giftMessageId;
    @XmlElement(name = "gift_message")
    protected String giftMessage;
    @XmlElement(name = "gift_message_available")
    protected String giftMessageAvailable;
    @XmlElement(name = "weee_tax_applied")
    protected Double weeeTaxApplied;
    @XmlElement(name = "weee_tax_applied_amount")
    protected Double weeeTaxAppliedAmount;
    @XmlElement(name = "weee_tax_applied_row_amount")
    protected Double weeeTaxAppliedRowAmount;
    @XmlElement(name = "base_weee_tax_applied_amount")
    protected Double baseWeeeTaxAppliedAmount;
    @XmlElement(name = "base_weee_tax_applied_row_amount")
    protected Double baseWeeeTaxAppliedRowAmount;
    @XmlElement(name = "weee_tax_disposition")
    protected Double weeeTaxDisposition;
    @XmlElement(name = "weee_tax_row_disposition")
    protected Double weeeTaxRowDisposition;
    @XmlElement(name = "base_weee_tax_disposition")
    protected Double baseWeeeTaxDisposition;
    @XmlElement(name = "base_weee_tax_row_disposition")
    protected Double baseWeeeTaxRowDisposition;
    @XmlElement(name = "tax_class_id")
    protected String taxClassId;

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
     * Gets the value of the storeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     * Sets the value of the storeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreId(String value) {
        this.storeId = value;
    }

    /**
     * Gets the value of the parentItemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentItemId() {
        return parentItemId;
    }

    /**
     * Sets the value of the parentItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentItemId(String value) {
        this.parentItemId = value;
    }

    /**
     * Gets the value of the isVirtual property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsVirtual() {
        return isVirtual;
    }

    /**
     * Sets the value of the isVirtual property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsVirtual(Integer value) {
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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeight(Double value) {
        this.weight = value;
    }

    /**
     * Gets the value of the qty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getQty() {
        return qty;
    }

    /**
     * Sets the value of the qty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setQty(Double value) {
        this.qty = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPrice(Double value) {
        this.price = value;
    }

    /**
     * Gets the value of the basePrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBasePrice() {
        return basePrice;
    }

    /**
     * Sets the value of the basePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBasePrice(Double value) {
        this.basePrice = value;
    }

    /**
     * Gets the value of the customPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCustomPrice() {
        return customPrice;
    }

    /**
     * Sets the value of the customPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCustomPrice(Double value) {
        this.customPrice = value;
    }

    /**
     * Gets the value of the discountPercent property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Sets the value of the discountPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDiscountPercent(Double value) {
        this.discountPercent = value;
    }

    /**
     * Gets the value of the discountAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sets the value of the discountAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDiscountAmount(Double value) {
        this.discountAmount = value;
    }

    /**
     * Gets the value of the baseDiscountAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseDiscountAmount() {
        return baseDiscountAmount;
    }

    /**
     * Sets the value of the baseDiscountAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseDiscountAmount(Double value) {
        this.baseDiscountAmount = value;
    }

    /**
     * Gets the value of the taxPercent property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTaxPercent() {
        return taxPercent;
    }

    /**
     * Sets the value of the taxPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTaxPercent(Double value) {
        this.taxPercent = value;
    }

    /**
     * Gets the value of the taxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTaxAmount() {
        return taxAmount;
    }

    /**
     * Sets the value of the taxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTaxAmount(Double value) {
        this.taxAmount = value;
    }

    /**
     * Gets the value of the baseTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseTaxAmount() {
        return baseTaxAmount;
    }

    /**
     * Sets the value of the baseTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseTaxAmount(Double value) {
        this.baseTaxAmount = value;
    }

    /**
     * Gets the value of the rowTotal property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRowTotal() {
        return rowTotal;
    }

    /**
     * Sets the value of the rowTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRowTotal(Double value) {
        this.rowTotal = value;
    }

    /**
     * Gets the value of the baseRowTotal property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseRowTotal() {
        return baseRowTotal;
    }

    /**
     * Sets the value of the baseRowTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseRowTotal(Double value) {
        this.baseRowTotal = value;
    }

    /**
     * Gets the value of the rowTotalWithDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRowTotalWithDiscount() {
        return rowTotalWithDiscount;
    }

    /**
     * Sets the value of the rowTotalWithDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRowTotalWithDiscount(Double value) {
        this.rowTotalWithDiscount = value;
    }

    /**
     * Gets the value of the rowWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRowWeight() {
        return rowWeight;
    }

    /**
     * Sets the value of the rowWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRowWeight(Double value) {
        this.rowWeight = value;
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
     * Gets the value of the baseTaxBeforeDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseTaxBeforeDiscount() {
        return baseTaxBeforeDiscount;
    }

    /**
     * Sets the value of the baseTaxBeforeDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseTaxBeforeDiscount(Double value) {
        this.baseTaxBeforeDiscount = value;
    }

    /**
     * Gets the value of the taxBeforeDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTaxBeforeDiscount() {
        return taxBeforeDiscount;
    }

    /**
     * Sets the value of the taxBeforeDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTaxBeforeDiscount(Double value) {
        this.taxBeforeDiscount = value;
    }

    /**
     * Gets the value of the originalCustomPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getOriginalCustomPrice() {
        return originalCustomPrice;
    }

    /**
     * Sets the value of the originalCustomPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setOriginalCustomPrice(Double value) {
        this.originalCustomPrice = value;
    }

    /**
     * Gets the value of the baseCost property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseCost() {
        return baseCost;
    }

    /**
     * Sets the value of the baseCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseCost(Double value) {
        this.baseCost = value;
    }

    /**
     * Gets the value of the priceInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPriceInclTax() {
        return priceInclTax;
    }

    /**
     * Sets the value of the priceInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPriceInclTax(Double value) {
        this.priceInclTax = value;
    }

    /**
     * Gets the value of the basePriceInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBasePriceInclTax() {
        return basePriceInclTax;
    }

    /**
     * Sets the value of the basePriceInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBasePriceInclTax(Double value) {
        this.basePriceInclTax = value;
    }

    /**
     * Gets the value of the rowTotalInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRowTotalInclTax() {
        return rowTotalInclTax;
    }

    /**
     * Sets the value of the rowTotalInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRowTotalInclTax(Double value) {
        this.rowTotalInclTax = value;
    }

    /**
     * Gets the value of the baseRowTotalInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseRowTotalInclTax() {
        return baseRowTotalInclTax;
    }

    /**
     * Sets the value of the baseRowTotalInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseRowTotalInclTax(Double value) {
        this.baseRowTotalInclTax = value;
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
     * Gets the value of the weeeTaxApplied property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeeeTaxApplied() {
        return weeeTaxApplied;
    }

    /**
     * Sets the value of the weeeTaxApplied property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeeeTaxApplied(Double value) {
        this.weeeTaxApplied = value;
    }

    /**
     * Gets the value of the weeeTaxAppliedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeeeTaxAppliedAmount() {
        return weeeTaxAppliedAmount;
    }

    /**
     * Sets the value of the weeeTaxAppliedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeeeTaxAppliedAmount(Double value) {
        this.weeeTaxAppliedAmount = value;
    }

    /**
     * Gets the value of the weeeTaxAppliedRowAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeeeTaxAppliedRowAmount() {
        return weeeTaxAppliedRowAmount;
    }

    /**
     * Sets the value of the weeeTaxAppliedRowAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeeeTaxAppliedRowAmount(Double value) {
        this.weeeTaxAppliedRowAmount = value;
    }

    /**
     * Gets the value of the baseWeeeTaxAppliedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseWeeeTaxAppliedAmount() {
        return baseWeeeTaxAppliedAmount;
    }

    /**
     * Sets the value of the baseWeeeTaxAppliedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseWeeeTaxAppliedAmount(Double value) {
        this.baseWeeeTaxAppliedAmount = value;
    }

    /**
     * Gets the value of the baseWeeeTaxAppliedRowAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseWeeeTaxAppliedRowAmount() {
        return baseWeeeTaxAppliedRowAmount;
    }

    /**
     * Sets the value of the baseWeeeTaxAppliedRowAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseWeeeTaxAppliedRowAmount(Double value) {
        this.baseWeeeTaxAppliedRowAmount = value;
    }

    /**
     * Gets the value of the weeeTaxDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeeeTaxDisposition() {
        return weeeTaxDisposition;
    }

    /**
     * Sets the value of the weeeTaxDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeeeTaxDisposition(Double value) {
        this.weeeTaxDisposition = value;
    }

    /**
     * Gets the value of the weeeTaxRowDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeeeTaxRowDisposition() {
        return weeeTaxRowDisposition;
    }

    /**
     * Sets the value of the weeeTaxRowDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeeeTaxRowDisposition(Double value) {
        this.weeeTaxRowDisposition = value;
    }

    /**
     * Gets the value of the baseWeeeTaxDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseWeeeTaxDisposition() {
        return baseWeeeTaxDisposition;
    }

    /**
     * Sets the value of the baseWeeeTaxDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseWeeeTaxDisposition(Double value) {
        this.baseWeeeTaxDisposition = value;
    }

    /**
     * Gets the value of the baseWeeeTaxRowDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseWeeeTaxRowDisposition() {
        return baseWeeeTaxRowDisposition;
    }

    /**
     * Sets the value of the baseWeeeTaxRowDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseWeeeTaxRowDisposition(Double value) {
        this.baseWeeeTaxRowDisposition = value;
    }

    /**
     * Gets the value of the taxClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxClassId() {
        return taxClassId;
    }

    /**
     * Sets the value of the taxClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxClassId(String value) {
        this.taxClassId = value;
    }

}
