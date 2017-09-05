
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for salesOrderCreditmemoItemEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="salesOrderCreditmemoItemEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="item_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parent_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied_row_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_row_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_applied_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_row_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_row_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="row_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="price_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_price_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_cost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_applied_row_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_row_total_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="row_total_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="product_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="order_item_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additional_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sku" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "salesOrderCreditmemoItemEntity", propOrder = {
    "itemId",
    "parentId",
    "weeeTaxAppliedRowAmount",
    "basePrice",
    "baseWeeeTaxRowDisposition",
    "taxAmount",
    "baseWeeeTaxAppliedAmount",
    "weeeTaxRowDisposition",
    "baseRowTotal",
    "discountAmount",
    "rowTotal",
    "weeeTaxAppliedAmount",
    "baseDiscountAmount",
    "baseWeeeTaxDisposition",
    "priceInclTax",
    "baseTaxAmount",
    "weeeTaxDisposition",
    "basePriceInclTax",
    "qty",
    "baseCost",
    "baseWeeeTaxAppliedRowAmount",
    "price",
    "baseRowTotalInclTax",
    "rowTotalInclTax",
    "productId",
    "orderItemId",
    "additionalData",
    "description",
    "weeeTaxApplied",
    "sku",
    "name",
    "hiddenTaxAmount",
    "baseHiddenTaxAmount"
})
public class SalesOrderCreditmemoItemEntity {

    @XmlElement(name = "item_id")
    protected String itemId;
    @XmlElement(name = "parent_id")
    protected String parentId;
    @XmlElement(name = "weee_tax_applied_row_amount")
    protected String weeeTaxAppliedRowAmount;
    @XmlElement(name = "base_price")
    protected String basePrice;
    @XmlElement(name = "base_weee_tax_row_disposition")
    protected String baseWeeeTaxRowDisposition;
    @XmlElement(name = "tax_amount")
    protected String taxAmount;
    @XmlElement(name = "base_weee_tax_applied_amount")
    protected String baseWeeeTaxAppliedAmount;
    @XmlElement(name = "weee_tax_row_disposition")
    protected String weeeTaxRowDisposition;
    @XmlElement(name = "base_row_total")
    protected String baseRowTotal;
    @XmlElement(name = "discount_amount")
    protected String discountAmount;
    @XmlElement(name = "row_total")
    protected String rowTotal;
    @XmlElement(name = "weee_tax_applied_amount")
    protected String weeeTaxAppliedAmount;
    @XmlElement(name = "base_discount_amount")
    protected String baseDiscountAmount;
    @XmlElement(name = "base_weee_tax_disposition")
    protected String baseWeeeTaxDisposition;
    @XmlElement(name = "price_incl_tax")
    protected String priceInclTax;
    @XmlElement(name = "base_tax_amount")
    protected String baseTaxAmount;
    @XmlElement(name = "weee_tax_disposition")
    protected String weeeTaxDisposition;
    @XmlElement(name = "base_price_incl_tax")
    protected String basePriceInclTax;
    protected String qty;
    @XmlElement(name = "base_cost")
    protected String baseCost;
    @XmlElement(name = "base_weee_tax_applied_row_amount")
    protected String baseWeeeTaxAppliedRowAmount;
    protected String price;
    @XmlElement(name = "base_row_total_incl_tax")
    protected String baseRowTotalInclTax;
    @XmlElement(name = "row_total_incl_tax")
    protected String rowTotalInclTax;
    @XmlElement(name = "product_id")
    protected String productId;
    @XmlElement(name = "order_item_id")
    protected String orderItemId;
    @XmlElement(name = "additional_data")
    protected String additionalData;
    protected String description;
    @XmlElement(name = "weee_tax_applied")
    protected String weeeTaxApplied;
    protected String sku;
    protected String name;
    @XmlElement(name = "hidden_tax_amount")
    protected String hiddenTaxAmount;
    @XmlElement(name = "base_hidden_tax_amount")
    protected String baseHiddenTaxAmount;

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
     * Gets the value of the priceInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriceInclTax() {
        return priceInclTax;
    }

    /**
     * Sets the value of the priceInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriceInclTax(String value) {
        this.priceInclTax = value;
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
     * Gets the value of the basePriceInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBasePriceInclTax() {
        return basePriceInclTax;
    }

    /**
     * Sets the value of the basePriceInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBasePriceInclTax(String value) {
        this.basePriceInclTax = value;
    }

    /**
     * Gets the value of the qty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQty() {
        return qty;
    }

    /**
     * Sets the value of the qty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQty(String value) {
        this.qty = value;
    }

    /**
     * Gets the value of the baseCost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseCost() {
        return baseCost;
    }

    /**
     * Sets the value of the baseCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseCost(String value) {
        this.baseCost = value;
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
     * Gets the value of the baseRowTotalInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseRowTotalInclTax() {
        return baseRowTotalInclTax;
    }

    /**
     * Sets the value of the baseRowTotalInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseRowTotalInclTax(String value) {
        this.baseRowTotalInclTax = value;
    }

    /**
     * Gets the value of the rowTotalInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowTotalInclTax() {
        return rowTotalInclTax;
    }

    /**
     * Sets the value of the rowTotalInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowTotalInclTax(String value) {
        this.rowTotalInclTax = value;
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
     * Gets the value of the orderItemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderItemId() {
        return orderItemId;
    }

    /**
     * Sets the value of the orderItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderItemId(String value) {
        this.orderItemId = value;
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
     * Gets the value of the hiddenTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHiddenTaxAmount() {
        return hiddenTaxAmount;
    }

    /**
     * Sets the value of the hiddenTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHiddenTaxAmount(String value) {
        this.hiddenTaxAmount = value;
    }

    /**
     * Gets the value of the baseHiddenTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseHiddenTaxAmount() {
        return baseHiddenTaxAmount;
    }

    /**
     * Sets the value of the baseHiddenTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseHiddenTaxAmount(String value) {
        this.baseHiddenTaxAmount = value;
    }

}
