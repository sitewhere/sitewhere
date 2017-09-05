
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for salesOrderInvoiceItemEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="salesOrderInvoiceItemEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="increment_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parent_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_active" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="row_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_row_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_applied_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_applied_row_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_applied_row_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weee_tax_row_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_weee_tax_row_disposition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sku" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="order_item_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="product_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="item_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "salesOrderInvoiceItemEntity", propOrder = {
    "incrementId",
    "parentId",
    "createdAt",
    "updatedAt",
    "isActive",
    "weeeTaxApplied",
    "qty",
    "cost",
    "price",
    "taxAmount",
    "rowTotal",
    "basePrice",
    "baseTaxAmount",
    "baseRowTotal",
    "baseWeeeTaxAppliedAmount",
    "baseWeeeTaxAppliedRowAmount",
    "weeeTaxAppliedAmount",
    "weeeTaxAppliedRowAmount",
    "weeeTaxDisposition",
    "weeeTaxRowDisposition",
    "baseWeeeTaxDisposition",
    "baseWeeeTaxRowDisposition",
    "sku",
    "name",
    "orderItemId",
    "productId",
    "itemId"
})
public class SalesOrderInvoiceItemEntity {

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
    @XmlElement(name = "weee_tax_applied")
    protected String weeeTaxApplied;
    protected String qty;
    protected String cost;
    protected String price;
    @XmlElement(name = "tax_amount")
    protected String taxAmount;
    @XmlElement(name = "row_total")
    protected String rowTotal;
    @XmlElement(name = "base_price")
    protected String basePrice;
    @XmlElement(name = "base_tax_amount")
    protected String baseTaxAmount;
    @XmlElement(name = "base_row_total")
    protected String baseRowTotal;
    @XmlElement(name = "base_weee_tax_applied_amount")
    protected String baseWeeeTaxAppliedAmount;
    @XmlElement(name = "base_weee_tax_applied_row_amount")
    protected String baseWeeeTaxAppliedRowAmount;
    @XmlElement(name = "weee_tax_applied_amount")
    protected String weeeTaxAppliedAmount;
    @XmlElement(name = "weee_tax_applied_row_amount")
    protected String weeeTaxAppliedRowAmount;
    @XmlElement(name = "weee_tax_disposition")
    protected String weeeTaxDisposition;
    @XmlElement(name = "weee_tax_row_disposition")
    protected String weeeTaxRowDisposition;
    @XmlElement(name = "base_weee_tax_disposition")
    protected String baseWeeeTaxDisposition;
    @XmlElement(name = "base_weee_tax_row_disposition")
    protected String baseWeeeTaxRowDisposition;
    protected String sku;
    protected String name;
    @XmlElement(name = "order_item_id")
    protected String orderItemId;
    @XmlElement(name = "product_id")
    protected String productId;
    @XmlElement(name = "item_id")
    protected String itemId;

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

}
