
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shoppingCartInfoEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shoppingCartInfoEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="store_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="converted_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quote_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_active" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_virtual" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_multi_shipping" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="items_count" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="items_qty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="orig_order_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_to_base_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_to_quote_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quote_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="grand_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_grand_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="checkout_method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_tax_class_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_group_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="customer_email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_prefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_firstname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_middlename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_lastname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_suffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_note_notify" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_is_guest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="applied_rule_ids" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reserved_order_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password_hash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="coupon_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="global_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_to_global_rate" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_to_quote_rate" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="customer_taxvat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_gender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtotal" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_subtotal" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="subtotal_with_discount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_subtotal_with_discount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="ext_shipping_info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_balance_amount_used" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="base_customer_balance_amount_used" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="use_customer_balance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_cards_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_gift_cards_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_cards_amount_used" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="use_reward_points" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_points_balance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_reward_currency_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_currency_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_address" type="{urn:Magento}shoppingCartAddressEntity" minOccurs="0"/>
 *         &lt;element name="billing_address" type="{urn:Magento}shoppingCartAddressEntity" minOccurs="0"/>
 *         &lt;element name="items" type="{urn:Magento}shoppingCartItemEntityArray" minOccurs="0"/>
 *         &lt;element name="payment" type="{urn:Magento}shoppingCartPaymentEntity" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shoppingCartInfoEntity", propOrder = {
    "storeId",
    "createdAt",
    "updatedAt",
    "convertedAt",
    "quoteId",
    "isActive",
    "isVirtual",
    "isMultiShipping",
    "itemsCount",
    "itemsQty",
    "origOrderId",
    "storeToBaseRate",
    "storeToQuoteRate",
    "baseCurrencyCode",
    "storeCurrencyCode",
    "quoteCurrencyCode",
    "grandTotal",
    "baseGrandTotal",
    "checkoutMethod",
    "customerId",
    "customerTaxClassId",
    "customerGroupId",
    "customerEmail",
    "customerPrefix",
    "customerFirstname",
    "customerMiddlename",
    "customerLastname",
    "customerSuffix",
    "customerNote",
    "customerNoteNotify",
    "customerIsGuest",
    "appliedRuleIds",
    "reservedOrderId",
    "passwordHash",
    "couponCode",
    "globalCurrencyCode",
    "baseToGlobalRate",
    "baseToQuoteRate",
    "customerTaxvat",
    "customerGender",
    "subtotal",
    "baseSubtotal",
    "subtotalWithDiscount",
    "baseSubtotalWithDiscount",
    "extShippingInfo",
    "giftMessageId",
    "giftMessage",
    "customerBalanceAmountUsed",
    "baseCustomerBalanceAmountUsed",
    "useCustomerBalance",
    "giftCardsAmount",
    "baseGiftCardsAmount",
    "giftCardsAmountUsed",
    "useRewardPoints",
    "rewardPointsBalance",
    "baseRewardCurrencyAmount",
    "rewardCurrencyAmount",
    "shippingAddress",
    "billingAddress",
    "items",
    "payment"
})
public class ShoppingCartInfoEntity {

    @XmlElement(name = "store_id")
    protected String storeId;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "updated_at")
    protected String updatedAt;
    @XmlElement(name = "converted_at")
    protected String convertedAt;
    @XmlElement(name = "quote_id")
    protected Integer quoteId;
    @XmlElement(name = "is_active")
    protected Integer isActive;
    @XmlElement(name = "is_virtual")
    protected Integer isVirtual;
    @XmlElement(name = "is_multi_shipping")
    protected Integer isMultiShipping;
    @XmlElement(name = "items_count")
    protected Double itemsCount;
    @XmlElement(name = "items_qty")
    protected Double itemsQty;
    @XmlElement(name = "orig_order_id")
    protected String origOrderId;
    @XmlElement(name = "store_to_base_rate")
    protected String storeToBaseRate;
    @XmlElement(name = "store_to_quote_rate")
    protected String storeToQuoteRate;
    @XmlElement(name = "base_currency_code")
    protected String baseCurrencyCode;
    @XmlElement(name = "store_currency_code")
    protected String storeCurrencyCode;
    @XmlElement(name = "quote_currency_code")
    protected String quoteCurrencyCode;
    @XmlElement(name = "grand_total")
    protected String grandTotal;
    @XmlElement(name = "base_grand_total")
    protected String baseGrandTotal;
    @XmlElement(name = "checkout_method")
    protected String checkoutMethod;
    @XmlElement(name = "customer_id")
    protected String customerId;
    @XmlElement(name = "customer_tax_class_id")
    protected String customerTaxClassId;
    @XmlElement(name = "customer_group_id")
    protected Integer customerGroupId;
    @XmlElement(name = "customer_email")
    protected String customerEmail;
    @XmlElement(name = "customer_prefix")
    protected String customerPrefix;
    @XmlElement(name = "customer_firstname")
    protected String customerFirstname;
    @XmlElement(name = "customer_middlename")
    protected String customerMiddlename;
    @XmlElement(name = "customer_lastname")
    protected String customerLastname;
    @XmlElement(name = "customer_suffix")
    protected String customerSuffix;
    @XmlElement(name = "customer_note")
    protected String customerNote;
    @XmlElement(name = "customer_note_notify")
    protected String customerNoteNotify;
    @XmlElement(name = "customer_is_guest")
    protected String customerIsGuest;
    @XmlElement(name = "applied_rule_ids")
    protected String appliedRuleIds;
    @XmlElement(name = "reserved_order_id")
    protected String reservedOrderId;
    @XmlElement(name = "password_hash")
    protected String passwordHash;
    @XmlElement(name = "coupon_code")
    protected String couponCode;
    @XmlElement(name = "global_currency_code")
    protected String globalCurrencyCode;
    @XmlElement(name = "base_to_global_rate")
    protected Double baseToGlobalRate;
    @XmlElement(name = "base_to_quote_rate")
    protected Double baseToQuoteRate;
    @XmlElement(name = "customer_taxvat")
    protected String customerTaxvat;
    @XmlElement(name = "customer_gender")
    protected String customerGender;
    protected Double subtotal;
    @XmlElement(name = "base_subtotal")
    protected Double baseSubtotal;
    @XmlElement(name = "subtotal_with_discount")
    protected Double subtotalWithDiscount;
    @XmlElement(name = "base_subtotal_with_discount")
    protected Double baseSubtotalWithDiscount;
    @XmlElement(name = "ext_shipping_info")
    protected String extShippingInfo;
    @XmlElement(name = "gift_message_id")
    protected String giftMessageId;
    @XmlElement(name = "gift_message")
    protected String giftMessage;
    @XmlElement(name = "customer_balance_amount_used")
    protected Double customerBalanceAmountUsed;
    @XmlElement(name = "base_customer_balance_amount_used")
    protected Double baseCustomerBalanceAmountUsed;
    @XmlElement(name = "use_customer_balance")
    protected String useCustomerBalance;
    @XmlElement(name = "gift_cards_amount")
    protected String giftCardsAmount;
    @XmlElement(name = "base_gift_cards_amount")
    protected String baseGiftCardsAmount;
    @XmlElement(name = "gift_cards_amount_used")
    protected String giftCardsAmountUsed;
    @XmlElement(name = "use_reward_points")
    protected String useRewardPoints;
    @XmlElement(name = "reward_points_balance")
    protected String rewardPointsBalance;
    @XmlElement(name = "base_reward_currency_amount")
    protected String baseRewardCurrencyAmount;
    @XmlElement(name = "reward_currency_amount")
    protected String rewardCurrencyAmount;
    @XmlElement(name = "shipping_address")
    protected ShoppingCartAddressEntity shippingAddress;
    @XmlElement(name = "billing_address")
    protected ShoppingCartAddressEntity billingAddress;
    protected ShoppingCartItemEntityArray items;
    protected ShoppingCartPaymentEntity payment;

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
     * Gets the value of the convertedAt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConvertedAt() {
        return convertedAt;
    }

    /**
     * Sets the value of the convertedAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConvertedAt(String value) {
        this.convertedAt = value;
    }

    /**
     * Gets the value of the quoteId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQuoteId() {
        return quoteId;
    }

    /**
     * Sets the value of the quoteId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQuoteId(Integer value) {
        this.quoteId = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsActive(Integer value) {
        this.isActive = value;
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
     * Gets the value of the isMultiShipping property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsMultiShipping() {
        return isMultiShipping;
    }

    /**
     * Sets the value of the isMultiShipping property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsMultiShipping(Integer value) {
        this.isMultiShipping = value;
    }

    /**
     * Gets the value of the itemsCount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getItemsCount() {
        return itemsCount;
    }

    /**
     * Sets the value of the itemsCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setItemsCount(Double value) {
        this.itemsCount = value;
    }

    /**
     * Gets the value of the itemsQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getItemsQty() {
        return itemsQty;
    }

    /**
     * Sets the value of the itemsQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setItemsQty(Double value) {
        this.itemsQty = value;
    }

    /**
     * Gets the value of the origOrderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigOrderId() {
        return origOrderId;
    }

    /**
     * Sets the value of the origOrderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigOrderId(String value) {
        this.origOrderId = value;
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
     * Gets the value of the storeToQuoteRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreToQuoteRate() {
        return storeToQuoteRate;
    }

    /**
     * Sets the value of the storeToQuoteRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreToQuoteRate(String value) {
        this.storeToQuoteRate = value;
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
     * Gets the value of the quoteCurrencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuoteCurrencyCode() {
        return quoteCurrencyCode;
    }

    /**
     * Sets the value of the quoteCurrencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuoteCurrencyCode(String value) {
        this.quoteCurrencyCode = value;
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
     * Gets the value of the checkoutMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckoutMethod() {
        return checkoutMethod;
    }

    /**
     * Sets the value of the checkoutMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckoutMethod(String value) {
        this.checkoutMethod = value;
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
     * Gets the value of the customerTaxClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerTaxClassId() {
        return customerTaxClassId;
    }

    /**
     * Sets the value of the customerTaxClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerTaxClassId(String value) {
        this.customerTaxClassId = value;
    }

    /**
     * Gets the value of the customerGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCustomerGroupId() {
        return customerGroupId;
    }

    /**
     * Sets the value of the customerGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCustomerGroupId(Integer value) {
        this.customerGroupId = value;
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
     * Gets the value of the customerPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerPrefix() {
        return customerPrefix;
    }

    /**
     * Sets the value of the customerPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerPrefix(String value) {
        this.customerPrefix = value;
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
     * Gets the value of the customerMiddlename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerMiddlename() {
        return customerMiddlename;
    }

    /**
     * Sets the value of the customerMiddlename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerMiddlename(String value) {
        this.customerMiddlename = value;
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
     * Gets the value of the customerSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerSuffix() {
        return customerSuffix;
    }

    /**
     * Sets the value of the customerSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerSuffix(String value) {
        this.customerSuffix = value;
    }

    /**
     * Gets the value of the customerNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerNote() {
        return customerNote;
    }

    /**
     * Sets the value of the customerNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerNote(String value) {
        this.customerNote = value;
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
     * Gets the value of the reservedOrderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReservedOrderId() {
        return reservedOrderId;
    }

    /**
     * Sets the value of the reservedOrderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReservedOrderId(String value) {
        this.reservedOrderId = value;
    }

    /**
     * Gets the value of the passwordHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the value of the passwordHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordHash(String value) {
        this.passwordHash = value;
    }

    /**
     * Gets the value of the couponCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * Sets the value of the couponCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCouponCode(String value) {
        this.couponCode = value;
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
     * Gets the value of the baseToGlobalRate property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseToGlobalRate() {
        return baseToGlobalRate;
    }

    /**
     * Sets the value of the baseToGlobalRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseToGlobalRate(Double value) {
        this.baseToGlobalRate = value;
    }

    /**
     * Gets the value of the baseToQuoteRate property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseToQuoteRate() {
        return baseToQuoteRate;
    }

    /**
     * Sets the value of the baseToQuoteRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseToQuoteRate(Double value) {
        this.baseToQuoteRate = value;
    }

    /**
     * Gets the value of the customerTaxvat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerTaxvat() {
        return customerTaxvat;
    }

    /**
     * Sets the value of the customerTaxvat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerTaxvat(String value) {
        this.customerTaxvat = value;
    }

    /**
     * Gets the value of the customerGender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerGender() {
        return customerGender;
    }

    /**
     * Sets the value of the customerGender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerGender(String value) {
        this.customerGender = value;
    }

    /**
     * Gets the value of the subtotal property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the value of the subtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSubtotal(Double value) {
        this.subtotal = value;
    }

    /**
     * Gets the value of the baseSubtotal property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseSubtotal() {
        return baseSubtotal;
    }

    /**
     * Sets the value of the baseSubtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseSubtotal(Double value) {
        this.baseSubtotal = value;
    }

    /**
     * Gets the value of the subtotalWithDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSubtotalWithDiscount() {
        return subtotalWithDiscount;
    }

    /**
     * Sets the value of the subtotalWithDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSubtotalWithDiscount(Double value) {
        this.subtotalWithDiscount = value;
    }

    /**
     * Gets the value of the baseSubtotalWithDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseSubtotalWithDiscount() {
        return baseSubtotalWithDiscount;
    }

    /**
     * Sets the value of the baseSubtotalWithDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseSubtotalWithDiscount(Double value) {
        this.baseSubtotalWithDiscount = value;
    }

    /**
     * Gets the value of the extShippingInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtShippingInfo() {
        return extShippingInfo;
    }

    /**
     * Sets the value of the extShippingInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtShippingInfo(String value) {
        this.extShippingInfo = value;
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
     * Gets the value of the customerBalanceAmountUsed property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCustomerBalanceAmountUsed() {
        return customerBalanceAmountUsed;
    }

    /**
     * Sets the value of the customerBalanceAmountUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCustomerBalanceAmountUsed(Double value) {
        this.customerBalanceAmountUsed = value;
    }

    /**
     * Gets the value of the baseCustomerBalanceAmountUsed property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseCustomerBalanceAmountUsed() {
        return baseCustomerBalanceAmountUsed;
    }

    /**
     * Sets the value of the baseCustomerBalanceAmountUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseCustomerBalanceAmountUsed(Double value) {
        this.baseCustomerBalanceAmountUsed = value;
    }

    /**
     * Gets the value of the useCustomerBalance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUseCustomerBalance() {
        return useCustomerBalance;
    }

    /**
     * Sets the value of the useCustomerBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUseCustomerBalance(String value) {
        this.useCustomerBalance = value;
    }

    /**
     * Gets the value of the giftCardsAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftCardsAmount() {
        return giftCardsAmount;
    }

    /**
     * Sets the value of the giftCardsAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftCardsAmount(String value) {
        this.giftCardsAmount = value;
    }

    /**
     * Gets the value of the baseGiftCardsAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseGiftCardsAmount() {
        return baseGiftCardsAmount;
    }

    /**
     * Sets the value of the baseGiftCardsAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseGiftCardsAmount(String value) {
        this.baseGiftCardsAmount = value;
    }

    /**
     * Gets the value of the giftCardsAmountUsed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftCardsAmountUsed() {
        return giftCardsAmountUsed;
    }

    /**
     * Sets the value of the giftCardsAmountUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftCardsAmountUsed(String value) {
        this.giftCardsAmountUsed = value;
    }

    /**
     * Gets the value of the useRewardPoints property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUseRewardPoints() {
        return useRewardPoints;
    }

    /**
     * Sets the value of the useRewardPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUseRewardPoints(String value) {
        this.useRewardPoints = value;
    }

    /**
     * Gets the value of the rewardPointsBalance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRewardPointsBalance() {
        return rewardPointsBalance;
    }

    /**
     * Sets the value of the rewardPointsBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRewardPointsBalance(String value) {
        this.rewardPointsBalance = value;
    }

    /**
     * Gets the value of the baseRewardCurrencyAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseRewardCurrencyAmount() {
        return baseRewardCurrencyAmount;
    }

    /**
     * Sets the value of the baseRewardCurrencyAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseRewardCurrencyAmount(String value) {
        this.baseRewardCurrencyAmount = value;
    }

    /**
     * Gets the value of the rewardCurrencyAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRewardCurrencyAmount() {
        return rewardCurrencyAmount;
    }

    /**
     * Sets the value of the rewardCurrencyAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRewardCurrencyAmount(String value) {
        this.rewardCurrencyAmount = value;
    }

    /**
     * Gets the value of the shippingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link ShoppingCartAddressEntity }
     *     
     */
    public ShoppingCartAddressEntity getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the value of the shippingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShoppingCartAddressEntity }
     *     
     */
    public void setShippingAddress(ShoppingCartAddressEntity value) {
        this.shippingAddress = value;
    }

    /**
     * Gets the value of the billingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link ShoppingCartAddressEntity }
     *     
     */
    public ShoppingCartAddressEntity getBillingAddress() {
        return billingAddress;
    }

    /**
     * Sets the value of the billingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShoppingCartAddressEntity }
     *     
     */
    public void setBillingAddress(ShoppingCartAddressEntity value) {
        this.billingAddress = value;
    }

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link ShoppingCartItemEntityArray }
     *     
     */
    public ShoppingCartItemEntityArray getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShoppingCartItemEntityArray }
     *     
     */
    public void setItems(ShoppingCartItemEntityArray value) {
        this.items = value;
    }

    /**
     * Gets the value of the payment property.
     * 
     * @return
     *     possible object is
     *     {@link ShoppingCartPaymentEntity }
     *     
     */
    public ShoppingCartPaymentEntity getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShoppingCartPaymentEntity }
     *     
     */
    public void setPayment(ShoppingCartPaymentEntity value) {
        this.payment = value;
    }

}
