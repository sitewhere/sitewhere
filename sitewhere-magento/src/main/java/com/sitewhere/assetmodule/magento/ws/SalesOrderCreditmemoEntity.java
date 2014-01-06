
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for salesOrderCreditmemoEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="salesOrderCreditmemoEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="increment_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transaction_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="global_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="order_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_currency_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cybersource_token" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="invoice_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billing_address_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_address_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="creditmemo_status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email_sent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="order_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_adjustment_positive" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_grand_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adjustment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_subtotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_adjustment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_to_global_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_to_base_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adjustment_negative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtotal_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_subtotal_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_adjustment_negative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="grand_total" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_to_order_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_to_order_rate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adjustment_positive" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_hidden_tax_amnt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_customer_balance_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_balance_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bs_customer_bal_total_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_bal_total_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_gift_cards_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_cards_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_base_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_items_base_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_items_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_card_base_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_card_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_items_base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_items_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_card_base_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gw_card_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_reward_currency_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_currency_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_points_balance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_points_balance_refund" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="creditmemo_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="items" type="{urn:Magento}salesOrderCreditmemoItemEntityArray" minOccurs="0"/>
 *         &lt;element name="comments" type="{urn:Magento}salesOrderCreditmemoCommentEntityArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "salesOrderCreditmemoEntity", propOrder = {
    "updatedAt",
    "createdAt",
    "incrementId",
    "transactionId",
    "globalCurrencyCode",
    "baseCurrencyCode",
    "orderCurrencyCode",
    "storeCurrencyCode",
    "cybersourceToken",
    "invoiceId",
    "billingAddressId",
    "shippingAddressId",
    "state",
    "creditmemoStatus",
    "emailSent",
    "orderId",
    "taxAmount",
    "shippingTaxAmount",
    "baseTaxAmount",
    "baseAdjustmentPositive",
    "baseGrandTotal",
    "adjustment",
    "subtotal",
    "discountAmount",
    "baseSubtotal",
    "baseAdjustment",
    "baseToGlobalRate",
    "storeToBaseRate",
    "baseShippingAmount",
    "adjustmentNegative",
    "subtotalInclTax",
    "shippingAmount",
    "baseSubtotalInclTax",
    "baseAdjustmentNegative",
    "grandTotal",
    "baseDiscountAmount",
    "baseToOrderRate",
    "storeToOrderRate",
    "baseShippingTaxAmount",
    "adjustmentPositive",
    "storeId",
    "hiddenTaxAmount",
    "baseHiddenTaxAmount",
    "shippingHiddenTaxAmount",
    "baseShippingHiddenTaxAmnt",
    "shippingInclTax",
    "baseShippingInclTax",
    "baseCustomerBalanceAmount",
    "customerBalanceAmount",
    "bsCustomerBalTotalRefunded",
    "customerBalTotalRefunded",
    "baseGiftCardsAmount",
    "giftCardsAmount",
    "gwBasePrice",
    "gwPrice",
    "gwItemsBasePrice",
    "gwItemsPrice",
    "gwCardBasePrice",
    "gwCardPrice",
    "gwBaseTaxAmount",
    "gwTaxAmount",
    "gwItemsBaseTaxAmount",
    "gwItemsTaxAmount",
    "gwCardBaseTaxAmount",
    "gwCardTaxAmount",
    "baseRewardCurrencyAmount",
    "rewardCurrencyAmount",
    "rewardPointsBalance",
    "rewardPointsBalanceRefund",
    "creditmemoId",
    "items",
    "comments"
})
public class SalesOrderCreditmemoEntity {

    @XmlElement(name = "updated_at")
    protected String updatedAt;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "increment_id")
    protected String incrementId;
    @XmlElement(name = "transaction_id")
    protected String transactionId;
    @XmlElement(name = "global_currency_code")
    protected String globalCurrencyCode;
    @XmlElement(name = "base_currency_code")
    protected String baseCurrencyCode;
    @XmlElement(name = "order_currency_code")
    protected String orderCurrencyCode;
    @XmlElement(name = "store_currency_code")
    protected String storeCurrencyCode;
    @XmlElement(name = "cybersource_token")
    protected String cybersourceToken;
    @XmlElement(name = "invoice_id")
    protected String invoiceId;
    @XmlElement(name = "billing_address_id")
    protected String billingAddressId;
    @XmlElement(name = "shipping_address_id")
    protected String shippingAddressId;
    protected String state;
    @XmlElement(name = "creditmemo_status")
    protected String creditmemoStatus;
    @XmlElement(name = "email_sent")
    protected String emailSent;
    @XmlElement(name = "order_id")
    protected String orderId;
    @XmlElement(name = "tax_amount")
    protected String taxAmount;
    @XmlElement(name = "shipping_tax_amount")
    protected String shippingTaxAmount;
    @XmlElement(name = "base_tax_amount")
    protected String baseTaxAmount;
    @XmlElement(name = "base_adjustment_positive")
    protected String baseAdjustmentPositive;
    @XmlElement(name = "base_grand_total")
    protected String baseGrandTotal;
    protected String adjustment;
    protected String subtotal;
    @XmlElement(name = "discount_amount")
    protected String discountAmount;
    @XmlElement(name = "base_subtotal")
    protected String baseSubtotal;
    @XmlElement(name = "base_adjustment")
    protected String baseAdjustment;
    @XmlElement(name = "base_to_global_rate")
    protected String baseToGlobalRate;
    @XmlElement(name = "store_to_base_rate")
    protected String storeToBaseRate;
    @XmlElement(name = "base_shipping_amount")
    protected String baseShippingAmount;
    @XmlElement(name = "adjustment_negative")
    protected String adjustmentNegative;
    @XmlElement(name = "subtotal_incl_tax")
    protected String subtotalInclTax;
    @XmlElement(name = "shipping_amount")
    protected String shippingAmount;
    @XmlElement(name = "base_subtotal_incl_tax")
    protected String baseSubtotalInclTax;
    @XmlElement(name = "base_adjustment_negative")
    protected String baseAdjustmentNegative;
    @XmlElement(name = "grand_total")
    protected String grandTotal;
    @XmlElement(name = "base_discount_amount")
    protected String baseDiscountAmount;
    @XmlElement(name = "base_to_order_rate")
    protected String baseToOrderRate;
    @XmlElement(name = "store_to_order_rate")
    protected String storeToOrderRate;
    @XmlElement(name = "base_shipping_tax_amount")
    protected String baseShippingTaxAmount;
    @XmlElement(name = "adjustment_positive")
    protected String adjustmentPositive;
    @XmlElement(name = "store_id")
    protected String storeId;
    @XmlElement(name = "hidden_tax_amount")
    protected String hiddenTaxAmount;
    @XmlElement(name = "base_hidden_tax_amount")
    protected String baseHiddenTaxAmount;
    @XmlElement(name = "shipping_hidden_tax_amount")
    protected String shippingHiddenTaxAmount;
    @XmlElement(name = "base_shipping_hidden_tax_amnt")
    protected String baseShippingHiddenTaxAmnt;
    @XmlElement(name = "shipping_incl_tax")
    protected String shippingInclTax;
    @XmlElement(name = "base_shipping_incl_tax")
    protected String baseShippingInclTax;
    @XmlElement(name = "base_customer_balance_amount")
    protected String baseCustomerBalanceAmount;
    @XmlElement(name = "customer_balance_amount")
    protected String customerBalanceAmount;
    @XmlElement(name = "bs_customer_bal_total_refunded")
    protected String bsCustomerBalTotalRefunded;
    @XmlElement(name = "customer_bal_total_refunded")
    protected String customerBalTotalRefunded;
    @XmlElement(name = "base_gift_cards_amount")
    protected String baseGiftCardsAmount;
    @XmlElement(name = "gift_cards_amount")
    protected String giftCardsAmount;
    @XmlElement(name = "gw_base_price")
    protected String gwBasePrice;
    @XmlElement(name = "gw_price")
    protected String gwPrice;
    @XmlElement(name = "gw_items_base_price")
    protected String gwItemsBasePrice;
    @XmlElement(name = "gw_items_price")
    protected String gwItemsPrice;
    @XmlElement(name = "gw_card_base_price")
    protected String gwCardBasePrice;
    @XmlElement(name = "gw_card_price")
    protected String gwCardPrice;
    @XmlElement(name = "gw_base_tax_amount")
    protected String gwBaseTaxAmount;
    @XmlElement(name = "gw_tax_amount")
    protected String gwTaxAmount;
    @XmlElement(name = "gw_items_base_tax_amount")
    protected String gwItemsBaseTaxAmount;
    @XmlElement(name = "gw_items_tax_amount")
    protected String gwItemsTaxAmount;
    @XmlElement(name = "gw_card_base_tax_amount")
    protected String gwCardBaseTaxAmount;
    @XmlElement(name = "gw_card_tax_amount")
    protected String gwCardTaxAmount;
    @XmlElement(name = "base_reward_currency_amount")
    protected String baseRewardCurrencyAmount;
    @XmlElement(name = "reward_currency_amount")
    protected String rewardCurrencyAmount;
    @XmlElement(name = "reward_points_balance")
    protected String rewardPointsBalance;
    @XmlElement(name = "reward_points_balance_refund")
    protected String rewardPointsBalanceRefund;
    @XmlElement(name = "creditmemo_id")
    protected String creditmemoId;
    protected SalesOrderCreditmemoItemEntityArray items;
    protected SalesOrderCreditmemoCommentEntityArray comments;

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
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
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
     * Gets the value of the cybersourceToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCybersourceToken() {
        return cybersourceToken;
    }

    /**
     * Sets the value of the cybersourceToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCybersourceToken(String value) {
        this.cybersourceToken = value;
    }

    /**
     * Gets the value of the invoiceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     * Sets the value of the invoiceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceId(String value) {
        this.invoiceId = value;
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
     * Gets the value of the creditmemoStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditmemoStatus() {
        return creditmemoStatus;
    }

    /**
     * Sets the value of the creditmemoStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditmemoStatus(String value) {
        this.creditmemoStatus = value;
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
     * Gets the value of the shippingTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingTaxAmount() {
        return shippingTaxAmount;
    }

    /**
     * Sets the value of the shippingTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingTaxAmount(String value) {
        this.shippingTaxAmount = value;
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
     * Gets the value of the baseAdjustmentPositive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseAdjustmentPositive() {
        return baseAdjustmentPositive;
    }

    /**
     * Sets the value of the baseAdjustmentPositive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseAdjustmentPositive(String value) {
        this.baseAdjustmentPositive = value;
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
     * Gets the value of the adjustment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdjustment() {
        return adjustment;
    }

    /**
     * Sets the value of the adjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdjustment(String value) {
        this.adjustment = value;
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
     * Gets the value of the baseAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseAdjustment() {
        return baseAdjustment;
    }

    /**
     * Sets the value of the baseAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseAdjustment(String value) {
        this.baseAdjustment = value;
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
     * Gets the value of the adjustmentNegative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdjustmentNegative() {
        return adjustmentNegative;
    }

    /**
     * Sets the value of the adjustmentNegative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdjustmentNegative(String value) {
        this.adjustmentNegative = value;
    }

    /**
     * Gets the value of the subtotalInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtotalInclTax() {
        return subtotalInclTax;
    }

    /**
     * Sets the value of the subtotalInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtotalInclTax(String value) {
        this.subtotalInclTax = value;
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
     * Gets the value of the baseSubtotalInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseSubtotalInclTax() {
        return baseSubtotalInclTax;
    }

    /**
     * Sets the value of the baseSubtotalInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseSubtotalInclTax(String value) {
        this.baseSubtotalInclTax = value;
    }

    /**
     * Gets the value of the baseAdjustmentNegative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseAdjustmentNegative() {
        return baseAdjustmentNegative;
    }

    /**
     * Sets the value of the baseAdjustmentNegative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseAdjustmentNegative(String value) {
        this.baseAdjustmentNegative = value;
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
     * Gets the value of the baseShippingTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingTaxAmount() {
        return baseShippingTaxAmount;
    }

    /**
     * Sets the value of the baseShippingTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingTaxAmount(String value) {
        this.baseShippingTaxAmount = value;
    }

    /**
     * Gets the value of the adjustmentPositive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdjustmentPositive() {
        return adjustmentPositive;
    }

    /**
     * Sets the value of the adjustmentPositive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdjustmentPositive(String value) {
        this.adjustmentPositive = value;
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

    /**
     * Gets the value of the shippingHiddenTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingHiddenTaxAmount() {
        return shippingHiddenTaxAmount;
    }

    /**
     * Sets the value of the shippingHiddenTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingHiddenTaxAmount(String value) {
        this.shippingHiddenTaxAmount = value;
    }

    /**
     * Gets the value of the baseShippingHiddenTaxAmnt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingHiddenTaxAmnt() {
        return baseShippingHiddenTaxAmnt;
    }

    /**
     * Sets the value of the baseShippingHiddenTaxAmnt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingHiddenTaxAmnt(String value) {
        this.baseShippingHiddenTaxAmnt = value;
    }

    /**
     * Gets the value of the shippingInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingInclTax() {
        return shippingInclTax;
    }

    /**
     * Sets the value of the shippingInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingInclTax(String value) {
        this.shippingInclTax = value;
    }

    /**
     * Gets the value of the baseShippingInclTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingInclTax() {
        return baseShippingInclTax;
    }

    /**
     * Sets the value of the baseShippingInclTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingInclTax(String value) {
        this.baseShippingInclTax = value;
    }

    /**
     * Gets the value of the baseCustomerBalanceAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseCustomerBalanceAmount() {
        return baseCustomerBalanceAmount;
    }

    /**
     * Sets the value of the baseCustomerBalanceAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseCustomerBalanceAmount(String value) {
        this.baseCustomerBalanceAmount = value;
    }

    /**
     * Gets the value of the customerBalanceAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerBalanceAmount() {
        return customerBalanceAmount;
    }

    /**
     * Sets the value of the customerBalanceAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerBalanceAmount(String value) {
        this.customerBalanceAmount = value;
    }

    /**
     * Gets the value of the bsCustomerBalTotalRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBsCustomerBalTotalRefunded() {
        return bsCustomerBalTotalRefunded;
    }

    /**
     * Sets the value of the bsCustomerBalTotalRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBsCustomerBalTotalRefunded(String value) {
        this.bsCustomerBalTotalRefunded = value;
    }

    /**
     * Gets the value of the customerBalTotalRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerBalTotalRefunded() {
        return customerBalTotalRefunded;
    }

    /**
     * Sets the value of the customerBalTotalRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerBalTotalRefunded(String value) {
        this.customerBalTotalRefunded = value;
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
     * Gets the value of the gwBasePrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwBasePrice() {
        return gwBasePrice;
    }

    /**
     * Sets the value of the gwBasePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwBasePrice(String value) {
        this.gwBasePrice = value;
    }

    /**
     * Gets the value of the gwPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwPrice() {
        return gwPrice;
    }

    /**
     * Sets the value of the gwPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwPrice(String value) {
        this.gwPrice = value;
    }

    /**
     * Gets the value of the gwItemsBasePrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwItemsBasePrice() {
        return gwItemsBasePrice;
    }

    /**
     * Sets the value of the gwItemsBasePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwItemsBasePrice(String value) {
        this.gwItemsBasePrice = value;
    }

    /**
     * Gets the value of the gwItemsPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwItemsPrice() {
        return gwItemsPrice;
    }

    /**
     * Sets the value of the gwItemsPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwItemsPrice(String value) {
        this.gwItemsPrice = value;
    }

    /**
     * Gets the value of the gwCardBasePrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwCardBasePrice() {
        return gwCardBasePrice;
    }

    /**
     * Sets the value of the gwCardBasePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwCardBasePrice(String value) {
        this.gwCardBasePrice = value;
    }

    /**
     * Gets the value of the gwCardPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwCardPrice() {
        return gwCardPrice;
    }

    /**
     * Sets the value of the gwCardPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwCardPrice(String value) {
        this.gwCardPrice = value;
    }

    /**
     * Gets the value of the gwBaseTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwBaseTaxAmount() {
        return gwBaseTaxAmount;
    }

    /**
     * Sets the value of the gwBaseTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwBaseTaxAmount(String value) {
        this.gwBaseTaxAmount = value;
    }

    /**
     * Gets the value of the gwTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwTaxAmount() {
        return gwTaxAmount;
    }

    /**
     * Sets the value of the gwTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwTaxAmount(String value) {
        this.gwTaxAmount = value;
    }

    /**
     * Gets the value of the gwItemsBaseTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwItemsBaseTaxAmount() {
        return gwItemsBaseTaxAmount;
    }

    /**
     * Sets the value of the gwItemsBaseTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwItemsBaseTaxAmount(String value) {
        this.gwItemsBaseTaxAmount = value;
    }

    /**
     * Gets the value of the gwItemsTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwItemsTaxAmount() {
        return gwItemsTaxAmount;
    }

    /**
     * Sets the value of the gwItemsTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwItemsTaxAmount(String value) {
        this.gwItemsTaxAmount = value;
    }

    /**
     * Gets the value of the gwCardBaseTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwCardBaseTaxAmount() {
        return gwCardBaseTaxAmount;
    }

    /**
     * Sets the value of the gwCardBaseTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwCardBaseTaxAmount(String value) {
        this.gwCardBaseTaxAmount = value;
    }

    /**
     * Gets the value of the gwCardTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGwCardTaxAmount() {
        return gwCardTaxAmount;
    }

    /**
     * Sets the value of the gwCardTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGwCardTaxAmount(String value) {
        this.gwCardTaxAmount = value;
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
     * Gets the value of the rewardPointsBalanceRefund property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRewardPointsBalanceRefund() {
        return rewardPointsBalanceRefund;
    }

    /**
     * Sets the value of the rewardPointsBalanceRefund property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRewardPointsBalanceRefund(String value) {
        this.rewardPointsBalanceRefund = value;
    }

    /**
     * Gets the value of the creditmemoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditmemoId() {
        return creditmemoId;
    }

    /**
     * Sets the value of the creditmemoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditmemoId(String value) {
        this.creditmemoId = value;
    }

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrderCreditmemoItemEntityArray }
     *     
     */
    public SalesOrderCreditmemoItemEntityArray getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrderCreditmemoItemEntityArray }
     *     
     */
    public void setItems(SalesOrderCreditmemoItemEntityArray value) {
        this.items = value;
    }

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrderCreditmemoCommentEntityArray }
     *     
     */
    public SalesOrderCreditmemoCommentEntityArray getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrderCreditmemoCommentEntityArray }
     *     
     */
    public void setComments(SalesOrderCreditmemoCommentEntityArray value) {
        this.comments = value;
    }

}
