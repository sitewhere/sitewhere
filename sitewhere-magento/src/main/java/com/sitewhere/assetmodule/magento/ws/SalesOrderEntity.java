
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for salesOrderEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="salesOrderEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="increment_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parent_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_active" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="grand_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_paid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_qty_ordered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_online_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_offline_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_subtotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_grand_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_paid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_qty_ordered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_online_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_offline_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billing_address_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billing_firstname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billing_lastname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_address_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_firstname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_lastname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billing_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_to_base_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_to_order_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_to_global_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_to_order_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="remote_ip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="applied_rule_ids" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="global_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="order_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_firstname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_lastname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quote_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_virtual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_group_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_note_notify" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_is_guest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email_sent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="order_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_address" type="{urn:Magento}salesOrderAddressEntity" minOccurs="0"/>
 *         &lt;element name="billing_address" type="{urn:Magento}salesOrderAddressEntity" minOccurs="0"/>
 *         &lt;element name="items" type="{urn:Magento}salesOrderItemEntityArray" minOccurs="0"/>
 *         &lt;element name="payment" type="{urn:Magento}salesOrderPaymentEntity" minOccurs="0"/>
 *         &lt;element name="status_history" type="{urn:Magento}salesOrderStatusHistoryEntityArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "salesOrderEntity", propOrder = {
    "incrementId",
    "parentId",
    "storeId",
    "createdAt",
    "updatedAt",
    "isActive",
    "customerId",
    "taxAmount",
    "shippingAmount",
    "discountAmount",
    "subtotal",
    "grandTotal",
    "totalPaid",
    "totalRefunded",
    "totalQtyOrdered",
    "totalCanceled",
    "totalInvoiced",
    "totalOnlineRefunded",
    "totalOfflineRefunded",
    "baseTaxAmount",
    "baseShippingAmount",
    "baseDiscountAmount",
    "baseSubtotal",
    "baseGrandTotal",
    "baseTotalPaid",
    "baseTotalRefunded",
    "baseTotalQtyOrdered",
    "baseTotalCanceled",
    "baseTotalInvoiced",
    "baseTotalOnlineRefunded",
    "baseTotalOfflineRefunded",
    "billingAddressId",
    "billingFirstname",
    "billingLastname",
    "shippingAddressId",
    "shippingFirstname",
    "shippingLastname",
    "billingName",
    "shippingName",
    "storeToBaseRate",
    "storeToOrderRate",
    "baseToGlobalRate",
    "baseToOrderRate",
    "weight",
    "storeName",
    "remoteIp",
    "status",
    "state",
    "appliedRuleIds",
    "globalCurrencyCode",
    "baseCurrencyCode",
    "storeCurrencyCode",
    "orderCurrencyCode",
    "shippingMethod",
    "shippingDescription",
    "customerEmail",
    "customerFirstname",
    "customerLastname",
    "quoteId",
    "isVirtual",
    "customerGroupId",
    "customerNoteNotify",
    "customerIsGuest",
    "emailSent",
    "orderId",
    "giftMessageId",
    "giftMessage",
    "shippingAddress",
    "billingAddress",
    "items",
    "payment",
    "statusHistory"
})
public class SalesOrderEntity {

    @XmlElement(name = "increment_id")
    protected String incrementId;
    @XmlElement(name = "parent_id")
    protected String parentId;
    @XmlElement(name = "store_id")
    protected String storeId;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "updated_at")
    protected String updatedAt;
    @XmlElement(name = "is_active")
    protected String isActive;
    @XmlElement(name = "customer_id")
    protected String customerId;
    @XmlElement(name = "tax_amount")
    protected String taxAmount;
    @XmlElement(name = "shipping_amount")
    protected String shippingAmount;
    @XmlElement(name = "discount_amount")
    protected String discountAmount;
    protected String subtotal;
    @XmlElement(name = "grand_total")
    protected String grandTotal;
    @XmlElement(name = "total_paid")
    protected String totalPaid;
    @XmlElement(name = "total_refunded")
    protected String totalRefunded;
    @XmlElement(name = "total_qty_ordered")
    protected String totalQtyOrdered;
    @XmlElement(name = "total_canceled")
    protected String totalCanceled;
    @XmlElement(name = "total_invoiced")
    protected String totalInvoiced;
    @XmlElement(name = "total_online_refunded")
    protected String totalOnlineRefunded;
    @XmlElement(name = "total_offline_refunded")
    protected String totalOfflineRefunded;
    @XmlElement(name = "base_tax_amount")
    protected String baseTaxAmount;
    @XmlElement(name = "base_shipping_amount")
    protected String baseShippingAmount;
    @XmlElement(name = "base_discount_amount")
    protected String baseDiscountAmount;
    @XmlElement(name = "base_subtotal")
    protected String baseSubtotal;
    @XmlElement(name = "base_grand_total")
    protected String baseGrandTotal;
    @XmlElement(name = "base_total_paid")
    protected String baseTotalPaid;
    @XmlElement(name = "base_total_refunded")
    protected String baseTotalRefunded;
    @XmlElement(name = "base_total_qty_ordered")
    protected String baseTotalQtyOrdered;
    @XmlElement(name = "base_total_canceled")
    protected String baseTotalCanceled;
    @XmlElement(name = "base_total_invoiced")
    protected String baseTotalInvoiced;
    @XmlElement(name = "base_total_online_refunded")
    protected String baseTotalOnlineRefunded;
    @XmlElement(name = "base_total_offline_refunded")
    protected String baseTotalOfflineRefunded;
    @XmlElement(name = "billing_address_id")
    protected String billingAddressId;
    @XmlElement(name = "billing_firstname")
    protected String billingFirstname;
    @XmlElement(name = "billing_lastname")
    protected String billingLastname;
    @XmlElement(name = "shipping_address_id")
    protected String shippingAddressId;
    @XmlElement(name = "shipping_firstname")
    protected String shippingFirstname;
    @XmlElement(name = "shipping_lastname")
    protected String shippingLastname;
    @XmlElement(name = "billing_name")
    protected String billingName;
    @XmlElement(name = "shipping_name")
    protected String shippingName;
    @XmlElement(name = "store_to_base_rate")
    protected String storeToBaseRate;
    @XmlElement(name = "store_to_order_rate")
    protected String storeToOrderRate;
    @XmlElement(name = "base_to_global_rate")
    protected String baseToGlobalRate;
    @XmlElement(name = "base_to_order_rate")
    protected String baseToOrderRate;
    protected String weight;
    @XmlElement(name = "store_name")
    protected String storeName;
    @XmlElement(name = "remote_ip")
    protected String remoteIp;
    protected String status;
    protected String state;
    @XmlElement(name = "applied_rule_ids")
    protected String appliedRuleIds;
    @XmlElement(name = "global_currency_code")
    protected String globalCurrencyCode;
    @XmlElement(name = "base_currency_code")
    protected String baseCurrencyCode;
    @XmlElement(name = "store_currency_code")
    protected String storeCurrencyCode;
    @XmlElement(name = "order_currency_code")
    protected String orderCurrencyCode;
    @XmlElement(name = "shipping_method")
    protected String shippingMethod;
    @XmlElement(name = "shipping_description")
    protected String shippingDescription;
    @XmlElement(name = "customer_email")
    protected String customerEmail;
    @XmlElement(name = "customer_firstname")
    protected String customerFirstname;
    @XmlElement(name = "customer_lastname")
    protected String customerLastname;
    @XmlElement(name = "quote_id")
    protected String quoteId;
    @XmlElement(name = "is_virtual")
    protected String isVirtual;
    @XmlElement(name = "customer_group_id")
    protected String customerGroupId;
    @XmlElement(name = "customer_note_notify")
    protected String customerNoteNotify;
    @XmlElement(name = "customer_is_guest")
    protected String customerIsGuest;
    @XmlElement(name = "email_sent")
    protected String emailSent;
    @XmlElement(name = "order_id")
    protected String orderId;
    @XmlElement(name = "gift_message_id")
    protected String giftMessageId;
    @XmlElement(name = "gift_message")
    protected String giftMessage;
    @XmlElement(name = "shipping_address")
    protected SalesOrderAddressEntity shippingAddress;
    @XmlElement(name = "billing_address")
    protected SalesOrderAddressEntity billingAddress;
    protected SalesOrderItemEntityArray items;
    protected SalesOrderPaymentEntity payment;
    @XmlElement(name = "status_history")
    protected SalesOrderStatusHistoryEntityArray statusHistory;

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
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerId(String value) {
        this.customerId = value;
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
     * Gets the value of the subtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the value of the subtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtotal(String value) {
        this.subtotal = value;
    }

    /**
     * Gets the value of the grandTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrandTotal() {
        return grandTotal;
    }

    /**
     * Sets the value of the grandTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrandTotal(String value) {
        this.grandTotal = value;
    }

    /**
     * Gets the value of the totalPaid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalPaid() {
        return totalPaid;
    }

    /**
     * Sets the value of the totalPaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalPaid(String value) {
        this.totalPaid = value;
    }

    /**
     * Gets the value of the totalRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalRefunded() {
        return totalRefunded;
    }

    /**
     * Sets the value of the totalRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalRefunded(String value) {
        this.totalRefunded = value;
    }

    /**
     * Gets the value of the totalQtyOrdered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalQtyOrdered() {
        return totalQtyOrdered;
    }

    /**
     * Sets the value of the totalQtyOrdered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalQtyOrdered(String value) {
        this.totalQtyOrdered = value;
    }

    /**
     * Gets the value of the totalCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalCanceled() {
        return totalCanceled;
    }

    /**
     * Sets the value of the totalCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalCanceled(String value) {
        this.totalCanceled = value;
    }

    /**
     * Gets the value of the totalInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalInvoiced() {
        return totalInvoiced;
    }

    /**
     * Sets the value of the totalInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalInvoiced(String value) {
        this.totalInvoiced = value;
    }

    /**
     * Gets the value of the totalOnlineRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalOnlineRefunded() {
        return totalOnlineRefunded;
    }

    /**
     * Sets the value of the totalOnlineRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalOnlineRefunded(String value) {
        this.totalOnlineRefunded = value;
    }

    /**
     * Gets the value of the totalOfflineRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalOfflineRefunded() {
        return totalOfflineRefunded;
    }

    /**
     * Sets the value of the totalOfflineRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalOfflineRefunded(String value) {
        this.totalOfflineRefunded = value;
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
     * Gets the value of the baseSubtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseSubtotal() {
        return baseSubtotal;
    }

    /**
     * Sets the value of the baseSubtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseSubtotal(String value) {
        this.baseSubtotal = value;
    }

    /**
     * Gets the value of the baseGrandTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseGrandTotal() {
        return baseGrandTotal;
    }

    /**
     * Sets the value of the baseGrandTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseGrandTotal(String value) {
        this.baseGrandTotal = value;
    }

    /**
     * Gets the value of the baseTotalPaid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalPaid() {
        return baseTotalPaid;
    }

    /**
     * Sets the value of the baseTotalPaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalPaid(String value) {
        this.baseTotalPaid = value;
    }

    /**
     * Gets the value of the baseTotalRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalRefunded() {
        return baseTotalRefunded;
    }

    /**
     * Sets the value of the baseTotalRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalRefunded(String value) {
        this.baseTotalRefunded = value;
    }

    /**
     * Gets the value of the baseTotalQtyOrdered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalQtyOrdered() {
        return baseTotalQtyOrdered;
    }

    /**
     * Sets the value of the baseTotalQtyOrdered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalQtyOrdered(String value) {
        this.baseTotalQtyOrdered = value;
    }

    /**
     * Gets the value of the baseTotalCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalCanceled() {
        return baseTotalCanceled;
    }

    /**
     * Sets the value of the baseTotalCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalCanceled(String value) {
        this.baseTotalCanceled = value;
    }

    /**
     * Gets the value of the baseTotalInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalInvoiced() {
        return baseTotalInvoiced;
    }

    /**
     * Sets the value of the baseTotalInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalInvoiced(String value) {
        this.baseTotalInvoiced = value;
    }

    /**
     * Gets the value of the baseTotalOnlineRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalOnlineRefunded() {
        return baseTotalOnlineRefunded;
    }

    /**
     * Sets the value of the baseTotalOnlineRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalOnlineRefunded(String value) {
        this.baseTotalOnlineRefunded = value;
    }

    /**
     * Gets the value of the baseTotalOfflineRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalOfflineRefunded() {
        return baseTotalOfflineRefunded;
    }

    /**
     * Sets the value of the baseTotalOfflineRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalOfflineRefunded(String value) {
        this.baseTotalOfflineRefunded = value;
    }

    /**
     * Gets the value of the billingAddressId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingAddressId() {
        return billingAddressId;
    }

    /**
     * Sets the value of the billingAddressId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingAddressId(String value) {
        this.billingAddressId = value;
    }

    /**
     * Gets the value of the billingFirstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingFirstname() {
        return billingFirstname;
    }

    /**
     * Sets the value of the billingFirstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingFirstname(String value) {
        this.billingFirstname = value;
    }

    /**
     * Gets the value of the billingLastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingLastname() {
        return billingLastname;
    }

    /**
     * Sets the value of the billingLastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingLastname(String value) {
        this.billingLastname = value;
    }

    /**
     * Gets the value of the shippingAddressId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingAddressId() {
        return shippingAddressId;
    }

    /**
     * Sets the value of the shippingAddressId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingAddressId(String value) {
        this.shippingAddressId = value;
    }

    /**
     * Gets the value of the shippingFirstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingFirstname() {
        return shippingFirstname;
    }

    /**
     * Sets the value of the shippingFirstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingFirstname(String value) {
        this.shippingFirstname = value;
    }

    /**
     * Gets the value of the shippingLastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingLastname() {
        return shippingLastname;
    }

    /**
     * Sets the value of the shippingLastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingLastname(String value) {
        this.shippingLastname = value;
    }

    /**
     * Gets the value of the billingName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingName() {
        return billingName;
    }

    /**
     * Sets the value of the billingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingName(String value) {
        this.billingName = value;
    }

    /**
     * Gets the value of the shippingName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingName() {
        return shippingName;
    }

    /**
     * Sets the value of the shippingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingName(String value) {
        this.shippingName = value;
    }

    /**
     * Gets the value of the storeToBaseRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreToBaseRate() {
        return storeToBaseRate;
    }

    /**
     * Sets the value of the storeToBaseRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreToBaseRate(String value) {
        this.storeToBaseRate = value;
    }

    /**
     * Gets the value of the storeToOrderRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreToOrderRate() {
        return storeToOrderRate;
    }

    /**
     * Sets the value of the storeToOrderRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreToOrderRate(String value) {
        this.storeToOrderRate = value;
    }

    /**
     * Gets the value of the baseToGlobalRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseToGlobalRate() {
        return baseToGlobalRate;
    }

    /**
     * Sets the value of the baseToGlobalRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseToGlobalRate(String value) {
        this.baseToGlobalRate = value;
    }

    /**
     * Gets the value of the baseToOrderRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseToOrderRate() {
        return baseToOrderRate;
    }

    /**
     * Sets the value of the baseToOrderRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseToOrderRate(String value) {
        this.baseToOrderRate = value;
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
     * Gets the value of the storeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * Sets the value of the storeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreName(String value) {
        this.storeName = value;
    }

    /**
     * Gets the value of the remoteIp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteIp() {
        return remoteIp;
    }

    /**
     * Sets the value of the remoteIp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteIp(String value) {
        this.remoteIp = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
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
     * Gets the value of the globalCurrencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlobalCurrencyCode() {
        return globalCurrencyCode;
    }

    /**
     * Sets the value of the globalCurrencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlobalCurrencyCode(String value) {
        this.globalCurrencyCode = value;
    }

    /**
     * Gets the value of the baseCurrencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    /**
     * Sets the value of the baseCurrencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseCurrencyCode(String value) {
        this.baseCurrencyCode = value;
    }

    /**
     * Gets the value of the storeCurrencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreCurrencyCode() {
        return storeCurrencyCode;
    }

    /**
     * Sets the value of the storeCurrencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreCurrencyCode(String value) {
        this.storeCurrencyCode = value;
    }

    /**
     * Gets the value of the orderCurrencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderCurrencyCode() {
        return orderCurrencyCode;
    }

    /**
     * Sets the value of the orderCurrencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderCurrencyCode(String value) {
        this.orderCurrencyCode = value;
    }

    /**
     * Gets the value of the shippingMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingMethod() {
        return shippingMethod;
    }

    /**
     * Sets the value of the shippingMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingMethod(String value) {
        this.shippingMethod = value;
    }

    /**
     * Gets the value of the shippingDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingDescription() {
        return shippingDescription;
    }

    /**
     * Sets the value of the shippingDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingDescription(String value) {
        this.shippingDescription = value;
    }

    /**
     * Gets the value of the customerEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Sets the value of the customerEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerEmail(String value) {
        this.customerEmail = value;
    }

    /**
     * Gets the value of the customerFirstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerFirstname() {
        return customerFirstname;
    }

    /**
     * Sets the value of the customerFirstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerFirstname(String value) {
        this.customerFirstname = value;
    }

    /**
     * Gets the value of the customerLastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerLastname() {
        return customerLastname;
    }

    /**
     * Sets the value of the customerLastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerLastname(String value) {
        this.customerLastname = value;
    }

    /**
     * Gets the value of the quoteId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuoteId() {
        return quoteId;
    }

    /**
     * Sets the value of the quoteId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuoteId(String value) {
        this.quoteId = value;
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
     * Gets the value of the customerGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerGroupId() {
        return customerGroupId;
    }

    /**
     * Sets the value of the customerGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerGroupId(String value) {
        this.customerGroupId = value;
    }

    /**
     * Gets the value of the customerNoteNotify property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerNoteNotify() {
        return customerNoteNotify;
    }

    /**
     * Sets the value of the customerNoteNotify property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerNoteNotify(String value) {
        this.customerNoteNotify = value;
    }

    /**
     * Gets the value of the customerIsGuest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerIsGuest() {
        return customerIsGuest;
    }

    /**
     * Sets the value of the customerIsGuest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerIsGuest(String value) {
        this.customerIsGuest = value;
    }

    /**
     * Gets the value of the emailSent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailSent() {
        return emailSent;
    }

    /**
     * Sets the value of the emailSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailSent(String value) {
        this.emailSent = value;
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
     * Gets the value of the shippingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrderAddressEntity }
     *     
     */
    public SalesOrderAddressEntity getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the value of the shippingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrderAddressEntity }
     *     
     */
    public void setShippingAddress(SalesOrderAddressEntity value) {
        this.shippingAddress = value;
    }

    /**
     * Gets the value of the billingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrderAddressEntity }
     *     
     */
    public SalesOrderAddressEntity getBillingAddress() {
        return billingAddress;
    }

    /**
     * Sets the value of the billingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrderAddressEntity }
     *     
     */
    public void setBillingAddress(SalesOrderAddressEntity value) {
        this.billingAddress = value;
    }

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrderItemEntityArray }
     *     
     */
    public SalesOrderItemEntityArray getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrderItemEntityArray }
     *     
     */
    public void setItems(SalesOrderItemEntityArray value) {
        this.items = value;
    }

    /**
     * Gets the value of the payment property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrderPaymentEntity }
     *     
     */
    public SalesOrderPaymentEntity getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrderPaymentEntity }
     *     
     */
    public void setPayment(SalesOrderPaymentEntity value) {
        this.payment = value;
    }

    /**
     * Gets the value of the statusHistory property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrderStatusHistoryEntityArray }
     *     
     */
    public SalesOrderStatusHistoryEntityArray getStatusHistory() {
        return statusHistory;
    }

    /**
     * Sets the value of the statusHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrderStatusHistoryEntityArray }
     *     
     */
    public void setStatusHistory(SalesOrderStatusHistoryEntityArray value) {
        this.statusHistory = value;
    }

}
