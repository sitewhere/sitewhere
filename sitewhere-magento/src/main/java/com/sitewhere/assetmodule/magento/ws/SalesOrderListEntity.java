
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for salesOrderListEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="salesOrderListEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="increment_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="store_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 *         &lt;element name="coupon_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protect_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_discount_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_discount_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_discount_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_tax_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_subtotal_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_subtotal_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_subtotal_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_tax_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_invoiced_cost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_tax_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtotal_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtotal_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtotal_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_canceled" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="can_ship_partially" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="can_ship_partially_item" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="edit_increment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="forced_do_shipment_with_invoice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="payment_authorization_expiration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paypal_ipn_customer_notified" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quote_address_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adjustment_negative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adjustment_positive" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_adjustment_negative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_adjustment_positive" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_subtotal_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_total_due" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="payment_authorization_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_discount_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtotal_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_due" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_dob" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_middlename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_prefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_suffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_taxvat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discount_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ext_customer_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ext_order_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hold_before_state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hold_before_status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="original_increment_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="relation_child_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="relation_child_real_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="relation_parent_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="relation_parent_real_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="x_forwarded_for" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="total_item_count" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_gender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_hidden_tax_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hidden_tax_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_hidden_tax_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hidden_tax_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_hidden_tax_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipping_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_shipping_incl_tax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_customer_balance_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_balance_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_customer_balance_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_balance_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_customer_balance_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_balance_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_customer_balance_total_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customer_balance_total_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_cards" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_gift_cards_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_cards_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_gift_cards_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_cards_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_gift_cards_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_cards_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_points_balance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_reward_currency_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_currency_amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_reward_currency_amount_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_currency_amount_invoiced" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base_reward_currency_amount_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_currency_amount_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_points_balance_refunded" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_points_balance_to_refund" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reward_salesrule_points" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postcode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "salesOrderListEntity", propOrder = {
    "incrementId",
    "storeId",
    "createdAt",
    "updatedAt",
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
    "couponCode",
    "protectCode",
    "baseDiscountCanceled",
    "baseDiscountInvoiced",
    "baseDiscountRefunded",
    "baseShippingCanceled",
    "baseShippingInvoiced",
    "baseShippingRefunded",
    "baseShippingTaxAmount",
    "baseShippingTaxRefunded",
    "baseSubtotalCanceled",
    "baseSubtotalInvoiced",
    "baseSubtotalRefunded",
    "baseTaxCanceled",
    "baseTaxInvoiced",
    "baseTaxRefunded",
    "baseTotalInvoicedCost",
    "discountCanceled",
    "discountInvoiced",
    "discountRefunded",
    "shippingCanceled",
    "shippingInvoiced",
    "shippingRefunded",
    "shippingTaxAmount",
    "shippingTaxRefunded",
    "subtotalCanceled",
    "subtotalInvoiced",
    "subtotalRefunded",
    "taxCanceled",
    "taxInvoiced",
    "taxRefunded",
    "canShipPartially",
    "canShipPartiallyItem",
    "editIncrement",
    "forcedDoShipmentWithInvoice",
    "paymentAuthorizationExpiration",
    "paypalIpnCustomerNotified",
    "quoteAddressId",
    "adjustmentNegative",
    "adjustmentPositive",
    "baseAdjustmentNegative",
    "baseAdjustmentPositive",
    "baseShippingDiscountAmount",
    "baseSubtotalInclTax",
    "baseTotalDue",
    "paymentAuthorizationAmount",
    "shippingDiscountAmount",
    "subtotalInclTax",
    "totalDue",
    "customerDob",
    "customerMiddlename",
    "customerPrefix",
    "customerSuffix",
    "customerTaxvat",
    "discountDescription",
    "extCustomerId",
    "extOrderId",
    "holdBeforeState",
    "holdBeforeStatus",
    "originalIncrementId",
    "relationChildId",
    "relationChildRealId",
    "relationParentId",
    "relationParentRealId",
    "xForwardedFor",
    "customerNote",
    "totalItemCount",
    "customerGender",
    "hiddenTaxAmount",
    "baseHiddenTaxAmount",
    "shippingHiddenTaxAmount",
    "baseShippingHiddenTaxAmount",
    "hiddenTaxInvoiced",
    "baseHiddenTaxInvoiced",
    "hiddenTaxRefunded",
    "baseHiddenTaxRefunded",
    "shippingInclTax",
    "baseShippingInclTax",
    "baseCustomerBalanceAmount",
    "customerBalanceAmount",
    "baseCustomerBalanceInvoiced",
    "customerBalanceInvoiced",
    "baseCustomerBalanceRefunded",
    "customerBalanceRefunded",
    "baseCustomerBalanceTotalRefunded",
    "customerBalanceTotalRefunded",
    "giftCards",
    "baseGiftCardsAmount",
    "giftCardsAmount",
    "baseGiftCardsInvoiced",
    "giftCardsInvoiced",
    "baseGiftCardsRefunded",
    "giftCardsRefunded",
    "rewardPointsBalance",
    "baseRewardCurrencyAmount",
    "rewardCurrencyAmount",
    "baseRewardCurrencyAmountInvoiced",
    "rewardCurrencyAmountInvoiced",
    "baseRewardCurrencyAmountRefunded",
    "rewardCurrencyAmountRefunded",
    "rewardPointsBalanceRefunded",
    "rewardPointsBalanceToRefund",
    "rewardSalesrulePoints",
    "firstname",
    "lastname",
    "telephone",
    "postcode"
})
public class SalesOrderListEntity {

    @XmlElement(name = "increment_id")
    protected String incrementId;
    @XmlElement(name = "store_id")
    protected String storeId;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "updated_at")
    protected String updatedAt;
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
    @XmlElement(name = "coupon_code")
    protected String couponCode;
    @XmlElement(name = "protect_code")
    protected String protectCode;
    @XmlElement(name = "base_discount_canceled")
    protected String baseDiscountCanceled;
    @XmlElement(name = "base_discount_invoiced")
    protected String baseDiscountInvoiced;
    @XmlElement(name = "base_discount_refunded")
    protected String baseDiscountRefunded;
    @XmlElement(name = "base_shipping_canceled")
    protected String baseShippingCanceled;
    @XmlElement(name = "base_shipping_invoiced")
    protected String baseShippingInvoiced;
    @XmlElement(name = "base_shipping_refunded")
    protected String baseShippingRefunded;
    @XmlElement(name = "base_shipping_tax_amount")
    protected String baseShippingTaxAmount;
    @XmlElement(name = "base_shipping_tax_refunded")
    protected String baseShippingTaxRefunded;
    @XmlElement(name = "base_subtotal_canceled")
    protected String baseSubtotalCanceled;
    @XmlElement(name = "base_subtotal_invoiced")
    protected String baseSubtotalInvoiced;
    @XmlElement(name = "base_subtotal_refunded")
    protected String baseSubtotalRefunded;
    @XmlElement(name = "base_tax_canceled")
    protected String baseTaxCanceled;
    @XmlElement(name = "base_tax_invoiced")
    protected String baseTaxInvoiced;
    @XmlElement(name = "base_tax_refunded")
    protected String baseTaxRefunded;
    @XmlElement(name = "base_total_invoiced_cost")
    protected String baseTotalInvoicedCost;
    @XmlElement(name = "discount_canceled")
    protected String discountCanceled;
    @XmlElement(name = "discount_invoiced")
    protected String discountInvoiced;
    @XmlElement(name = "discount_refunded")
    protected String discountRefunded;
    @XmlElement(name = "shipping_canceled")
    protected String shippingCanceled;
    @XmlElement(name = "shipping_invoiced")
    protected String shippingInvoiced;
    @XmlElement(name = "shipping_refunded")
    protected String shippingRefunded;
    @XmlElement(name = "shipping_tax_amount")
    protected String shippingTaxAmount;
    @XmlElement(name = "shipping_tax_refunded")
    protected String shippingTaxRefunded;
    @XmlElement(name = "subtotal_canceled")
    protected String subtotalCanceled;
    @XmlElement(name = "subtotal_invoiced")
    protected String subtotalInvoiced;
    @XmlElement(name = "subtotal_refunded")
    protected String subtotalRefunded;
    @XmlElement(name = "tax_canceled")
    protected String taxCanceled;
    @XmlElement(name = "tax_invoiced")
    protected String taxInvoiced;
    @XmlElement(name = "tax_refunded")
    protected String taxRefunded;
    @XmlElement(name = "can_ship_partially")
    protected String canShipPartially;
    @XmlElement(name = "can_ship_partially_item")
    protected String canShipPartiallyItem;
    @XmlElement(name = "edit_increment")
    protected String editIncrement;
    @XmlElement(name = "forced_do_shipment_with_invoice")
    protected String forcedDoShipmentWithInvoice;
    @XmlElement(name = "payment_authorization_expiration")
    protected String paymentAuthorizationExpiration;
    @XmlElement(name = "paypal_ipn_customer_notified")
    protected String paypalIpnCustomerNotified;
    @XmlElement(name = "quote_address_id")
    protected String quoteAddressId;
    @XmlElement(name = "adjustment_negative")
    protected String adjustmentNegative;
    @XmlElement(name = "adjustment_positive")
    protected String adjustmentPositive;
    @XmlElement(name = "base_adjustment_negative")
    protected String baseAdjustmentNegative;
    @XmlElement(name = "base_adjustment_positive")
    protected String baseAdjustmentPositive;
    @XmlElement(name = "base_shipping_discount_amount")
    protected String baseShippingDiscountAmount;
    @XmlElement(name = "base_subtotal_incl_tax")
    protected String baseSubtotalInclTax;
    @XmlElement(name = "base_total_due")
    protected String baseTotalDue;
    @XmlElement(name = "payment_authorization_amount")
    protected String paymentAuthorizationAmount;
    @XmlElement(name = "shipping_discount_amount")
    protected String shippingDiscountAmount;
    @XmlElement(name = "subtotal_incl_tax")
    protected String subtotalInclTax;
    @XmlElement(name = "total_due")
    protected String totalDue;
    @XmlElement(name = "customer_dob")
    protected String customerDob;
    @XmlElement(name = "customer_middlename")
    protected String customerMiddlename;
    @XmlElement(name = "customer_prefix")
    protected String customerPrefix;
    @XmlElement(name = "customer_suffix")
    protected String customerSuffix;
    @XmlElement(name = "customer_taxvat")
    protected String customerTaxvat;
    @XmlElement(name = "discount_description")
    protected String discountDescription;
    @XmlElement(name = "ext_customer_id")
    protected String extCustomerId;
    @XmlElement(name = "ext_order_id")
    protected String extOrderId;
    @XmlElement(name = "hold_before_state")
    protected String holdBeforeState;
    @XmlElement(name = "hold_before_status")
    protected String holdBeforeStatus;
    @XmlElement(name = "original_increment_id")
    protected String originalIncrementId;
    @XmlElement(name = "relation_child_id")
    protected String relationChildId;
    @XmlElement(name = "relation_child_real_id")
    protected String relationChildRealId;
    @XmlElement(name = "relation_parent_id")
    protected String relationParentId;
    @XmlElement(name = "relation_parent_real_id")
    protected String relationParentRealId;
    @XmlElement(name = "x_forwarded_for")
    protected String xForwardedFor;
    @XmlElement(name = "customer_note")
    protected String customerNote;
    @XmlElement(name = "total_item_count")
    protected String totalItemCount;
    @XmlElement(name = "customer_gender")
    protected String customerGender;
    @XmlElement(name = "hidden_tax_amount")
    protected String hiddenTaxAmount;
    @XmlElement(name = "base_hidden_tax_amount")
    protected String baseHiddenTaxAmount;
    @XmlElement(name = "shipping_hidden_tax_amount")
    protected String shippingHiddenTaxAmount;
    @XmlElement(name = "base_shipping_hidden_tax_amount")
    protected String baseShippingHiddenTaxAmount;
    @XmlElement(name = "hidden_tax_invoiced")
    protected String hiddenTaxInvoiced;
    @XmlElement(name = "base_hidden_tax_invoiced")
    protected String baseHiddenTaxInvoiced;
    @XmlElement(name = "hidden_tax_refunded")
    protected String hiddenTaxRefunded;
    @XmlElement(name = "base_hidden_tax_refunded")
    protected String baseHiddenTaxRefunded;
    @XmlElement(name = "shipping_incl_tax")
    protected String shippingInclTax;
    @XmlElement(name = "base_shipping_incl_tax")
    protected String baseShippingInclTax;
    @XmlElement(name = "base_customer_balance_amount")
    protected String baseCustomerBalanceAmount;
    @XmlElement(name = "customer_balance_amount")
    protected String customerBalanceAmount;
    @XmlElement(name = "base_customer_balance_invoiced")
    protected String baseCustomerBalanceInvoiced;
    @XmlElement(name = "customer_balance_invoiced")
    protected String customerBalanceInvoiced;
    @XmlElement(name = "base_customer_balance_refunded")
    protected String baseCustomerBalanceRefunded;
    @XmlElement(name = "customer_balance_refunded")
    protected String customerBalanceRefunded;
    @XmlElement(name = "base_customer_balance_total_refunded")
    protected String baseCustomerBalanceTotalRefunded;
    @XmlElement(name = "customer_balance_total_refunded")
    protected String customerBalanceTotalRefunded;
    @XmlElement(name = "gift_cards")
    protected String giftCards;
    @XmlElement(name = "base_gift_cards_amount")
    protected String baseGiftCardsAmount;
    @XmlElement(name = "gift_cards_amount")
    protected String giftCardsAmount;
    @XmlElement(name = "base_gift_cards_invoiced")
    protected String baseGiftCardsInvoiced;
    @XmlElement(name = "gift_cards_invoiced")
    protected String giftCardsInvoiced;
    @XmlElement(name = "base_gift_cards_refunded")
    protected String baseGiftCardsRefunded;
    @XmlElement(name = "gift_cards_refunded")
    protected String giftCardsRefunded;
    @XmlElement(name = "reward_points_balance")
    protected String rewardPointsBalance;
    @XmlElement(name = "base_reward_currency_amount")
    protected String baseRewardCurrencyAmount;
    @XmlElement(name = "reward_currency_amount")
    protected String rewardCurrencyAmount;
    @XmlElement(name = "base_reward_currency_amount_invoiced")
    protected String baseRewardCurrencyAmountInvoiced;
    @XmlElement(name = "reward_currency_amount_invoiced")
    protected String rewardCurrencyAmountInvoiced;
    @XmlElement(name = "base_reward_currency_amount_refunded")
    protected String baseRewardCurrencyAmountRefunded;
    @XmlElement(name = "reward_currency_amount_refunded")
    protected String rewardCurrencyAmountRefunded;
    @XmlElement(name = "reward_points_balance_refunded")
    protected String rewardPointsBalanceRefunded;
    @XmlElement(name = "reward_points_balance_to_refund")
    protected String rewardPointsBalanceToRefund;
    @XmlElement(name = "reward_salesrule_points")
    protected String rewardSalesrulePoints;
    protected String firstname;
    protected String lastname;
    protected String telephone;
    protected String postcode;

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
     * Gets the value of the protectCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtectCode() {
        return protectCode;
    }

    /**
     * Sets the value of the protectCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtectCode(String value) {
        this.protectCode = value;
    }

    /**
     * Gets the value of the baseDiscountCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseDiscountCanceled() {
        return baseDiscountCanceled;
    }

    /**
     * Sets the value of the baseDiscountCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseDiscountCanceled(String value) {
        this.baseDiscountCanceled = value;
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
     * Gets the value of the baseDiscountRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseDiscountRefunded() {
        return baseDiscountRefunded;
    }

    /**
     * Sets the value of the baseDiscountRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseDiscountRefunded(String value) {
        this.baseDiscountRefunded = value;
    }

    /**
     * Gets the value of the baseShippingCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingCanceled() {
        return baseShippingCanceled;
    }

    /**
     * Sets the value of the baseShippingCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingCanceled(String value) {
        this.baseShippingCanceled = value;
    }

    /**
     * Gets the value of the baseShippingInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingInvoiced() {
        return baseShippingInvoiced;
    }

    /**
     * Sets the value of the baseShippingInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingInvoiced(String value) {
        this.baseShippingInvoiced = value;
    }

    /**
     * Gets the value of the baseShippingRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingRefunded() {
        return baseShippingRefunded;
    }

    /**
     * Sets the value of the baseShippingRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingRefunded(String value) {
        this.baseShippingRefunded = value;
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
     * Gets the value of the baseShippingTaxRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingTaxRefunded() {
        return baseShippingTaxRefunded;
    }

    /**
     * Sets the value of the baseShippingTaxRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingTaxRefunded(String value) {
        this.baseShippingTaxRefunded = value;
    }

    /**
     * Gets the value of the baseSubtotalCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseSubtotalCanceled() {
        return baseSubtotalCanceled;
    }

    /**
     * Sets the value of the baseSubtotalCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseSubtotalCanceled(String value) {
        this.baseSubtotalCanceled = value;
    }

    /**
     * Gets the value of the baseSubtotalInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseSubtotalInvoiced() {
        return baseSubtotalInvoiced;
    }

    /**
     * Sets the value of the baseSubtotalInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseSubtotalInvoiced(String value) {
        this.baseSubtotalInvoiced = value;
    }

    /**
     * Gets the value of the baseSubtotalRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseSubtotalRefunded() {
        return baseSubtotalRefunded;
    }

    /**
     * Sets the value of the baseSubtotalRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseSubtotalRefunded(String value) {
        this.baseSubtotalRefunded = value;
    }

    /**
     * Gets the value of the baseTaxCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTaxCanceled() {
        return baseTaxCanceled;
    }

    /**
     * Sets the value of the baseTaxCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTaxCanceled(String value) {
        this.baseTaxCanceled = value;
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
     * Gets the value of the baseTaxRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTaxRefunded() {
        return baseTaxRefunded;
    }

    /**
     * Sets the value of the baseTaxRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTaxRefunded(String value) {
        this.baseTaxRefunded = value;
    }

    /**
     * Gets the value of the baseTotalInvoicedCost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalInvoicedCost() {
        return baseTotalInvoicedCost;
    }

    /**
     * Sets the value of the baseTotalInvoicedCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalInvoicedCost(String value) {
        this.baseTotalInvoicedCost = value;
    }

    /**
     * Gets the value of the discountCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountCanceled() {
        return discountCanceled;
    }

    /**
     * Sets the value of the discountCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountCanceled(String value) {
        this.discountCanceled = value;
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
     * Gets the value of the discountRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountRefunded() {
        return discountRefunded;
    }

    /**
     * Sets the value of the discountRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountRefunded(String value) {
        this.discountRefunded = value;
    }

    /**
     * Gets the value of the shippingCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingCanceled() {
        return shippingCanceled;
    }

    /**
     * Sets the value of the shippingCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingCanceled(String value) {
        this.shippingCanceled = value;
    }

    /**
     * Gets the value of the shippingInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingInvoiced() {
        return shippingInvoiced;
    }

    /**
     * Sets the value of the shippingInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingInvoiced(String value) {
        this.shippingInvoiced = value;
    }

    /**
     * Gets the value of the shippingRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingRefunded() {
        return shippingRefunded;
    }

    /**
     * Sets the value of the shippingRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingRefunded(String value) {
        this.shippingRefunded = value;
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
     * Gets the value of the shippingTaxRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingTaxRefunded() {
        return shippingTaxRefunded;
    }

    /**
     * Sets the value of the shippingTaxRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingTaxRefunded(String value) {
        this.shippingTaxRefunded = value;
    }

    /**
     * Gets the value of the subtotalCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtotalCanceled() {
        return subtotalCanceled;
    }

    /**
     * Sets the value of the subtotalCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtotalCanceled(String value) {
        this.subtotalCanceled = value;
    }

    /**
     * Gets the value of the subtotalInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtotalInvoiced() {
        return subtotalInvoiced;
    }

    /**
     * Sets the value of the subtotalInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtotalInvoiced(String value) {
        this.subtotalInvoiced = value;
    }

    /**
     * Gets the value of the subtotalRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtotalRefunded() {
        return subtotalRefunded;
    }

    /**
     * Sets the value of the subtotalRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtotalRefunded(String value) {
        this.subtotalRefunded = value;
    }

    /**
     * Gets the value of the taxCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxCanceled() {
        return taxCanceled;
    }

    /**
     * Sets the value of the taxCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxCanceled(String value) {
        this.taxCanceled = value;
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
     * Gets the value of the taxRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxRefunded() {
        return taxRefunded;
    }

    /**
     * Sets the value of the taxRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxRefunded(String value) {
        this.taxRefunded = value;
    }

    /**
     * Gets the value of the canShipPartially property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCanShipPartially() {
        return canShipPartially;
    }

    /**
     * Sets the value of the canShipPartially property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCanShipPartially(String value) {
        this.canShipPartially = value;
    }

    /**
     * Gets the value of the canShipPartiallyItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCanShipPartiallyItem() {
        return canShipPartiallyItem;
    }

    /**
     * Sets the value of the canShipPartiallyItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCanShipPartiallyItem(String value) {
        this.canShipPartiallyItem = value;
    }

    /**
     * Gets the value of the editIncrement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEditIncrement() {
        return editIncrement;
    }

    /**
     * Sets the value of the editIncrement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEditIncrement(String value) {
        this.editIncrement = value;
    }

    /**
     * Gets the value of the forcedDoShipmentWithInvoice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForcedDoShipmentWithInvoice() {
        return forcedDoShipmentWithInvoice;
    }

    /**
     * Sets the value of the forcedDoShipmentWithInvoice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForcedDoShipmentWithInvoice(String value) {
        this.forcedDoShipmentWithInvoice = value;
    }

    /**
     * Gets the value of the paymentAuthorizationExpiration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentAuthorizationExpiration() {
        return paymentAuthorizationExpiration;
    }

    /**
     * Sets the value of the paymentAuthorizationExpiration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentAuthorizationExpiration(String value) {
        this.paymentAuthorizationExpiration = value;
    }

    /**
     * Gets the value of the paypalIpnCustomerNotified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaypalIpnCustomerNotified() {
        return paypalIpnCustomerNotified;
    }

    /**
     * Sets the value of the paypalIpnCustomerNotified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaypalIpnCustomerNotified(String value) {
        this.paypalIpnCustomerNotified = value;
    }

    /**
     * Gets the value of the quoteAddressId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuoteAddressId() {
        return quoteAddressId;
    }

    /**
     * Sets the value of the quoteAddressId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuoteAddressId(String value) {
        this.quoteAddressId = value;
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
     * Gets the value of the baseShippingDiscountAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingDiscountAmount() {
        return baseShippingDiscountAmount;
    }

    /**
     * Sets the value of the baseShippingDiscountAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingDiscountAmount(String value) {
        this.baseShippingDiscountAmount = value;
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
     * Gets the value of the baseTotalDue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTotalDue() {
        return baseTotalDue;
    }

    /**
     * Sets the value of the baseTotalDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTotalDue(String value) {
        this.baseTotalDue = value;
    }

    /**
     * Gets the value of the paymentAuthorizationAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentAuthorizationAmount() {
        return paymentAuthorizationAmount;
    }

    /**
     * Sets the value of the paymentAuthorizationAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentAuthorizationAmount(String value) {
        this.paymentAuthorizationAmount = value;
    }

    /**
     * Gets the value of the shippingDiscountAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingDiscountAmount() {
        return shippingDiscountAmount;
    }

    /**
     * Sets the value of the shippingDiscountAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingDiscountAmount(String value) {
        this.shippingDiscountAmount = value;
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
     * Gets the value of the totalDue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalDue() {
        return totalDue;
    }

    /**
     * Sets the value of the totalDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalDue(String value) {
        this.totalDue = value;
    }

    /**
     * Gets the value of the customerDob property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerDob() {
        return customerDob;
    }

    /**
     * Sets the value of the customerDob property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerDob(String value) {
        this.customerDob = value;
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
     * Gets the value of the discountDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountDescription() {
        return discountDescription;
    }

    /**
     * Sets the value of the discountDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountDescription(String value) {
        this.discountDescription = value;
    }

    /**
     * Gets the value of the extCustomerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtCustomerId() {
        return extCustomerId;
    }

    /**
     * Sets the value of the extCustomerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtCustomerId(String value) {
        this.extCustomerId = value;
    }

    /**
     * Gets the value of the extOrderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtOrderId() {
        return extOrderId;
    }

    /**
     * Sets the value of the extOrderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtOrderId(String value) {
        this.extOrderId = value;
    }

    /**
     * Gets the value of the holdBeforeState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoldBeforeState() {
        return holdBeforeState;
    }

    /**
     * Sets the value of the holdBeforeState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoldBeforeState(String value) {
        this.holdBeforeState = value;
    }

    /**
     * Gets the value of the holdBeforeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoldBeforeStatus() {
        return holdBeforeStatus;
    }

    /**
     * Sets the value of the holdBeforeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoldBeforeStatus(String value) {
        this.holdBeforeStatus = value;
    }

    /**
     * Gets the value of the originalIncrementId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalIncrementId() {
        return originalIncrementId;
    }

    /**
     * Sets the value of the originalIncrementId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalIncrementId(String value) {
        this.originalIncrementId = value;
    }

    /**
     * Gets the value of the relationChildId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationChildId() {
        return relationChildId;
    }

    /**
     * Sets the value of the relationChildId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationChildId(String value) {
        this.relationChildId = value;
    }

    /**
     * Gets the value of the relationChildRealId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationChildRealId() {
        return relationChildRealId;
    }

    /**
     * Sets the value of the relationChildRealId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationChildRealId(String value) {
        this.relationChildRealId = value;
    }

    /**
     * Gets the value of the relationParentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationParentId() {
        return relationParentId;
    }

    /**
     * Sets the value of the relationParentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationParentId(String value) {
        this.relationParentId = value;
    }

    /**
     * Gets the value of the relationParentRealId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationParentRealId() {
        return relationParentRealId;
    }

    /**
     * Sets the value of the relationParentRealId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationParentRealId(String value) {
        this.relationParentRealId = value;
    }

    /**
     * Gets the value of the xForwardedFor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXForwardedFor() {
        return xForwardedFor;
    }

    /**
     * Sets the value of the xForwardedFor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXForwardedFor(String value) {
        this.xForwardedFor = value;
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
     * Gets the value of the totalItemCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalItemCount() {
        return totalItemCount;
    }

    /**
     * Sets the value of the totalItemCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalItemCount(String value) {
        this.totalItemCount = value;
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
     * Gets the value of the baseShippingHiddenTaxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseShippingHiddenTaxAmount() {
        return baseShippingHiddenTaxAmount;
    }

    /**
     * Sets the value of the baseShippingHiddenTaxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseShippingHiddenTaxAmount(String value) {
        this.baseShippingHiddenTaxAmount = value;
    }

    /**
     * Gets the value of the hiddenTaxInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHiddenTaxInvoiced() {
        return hiddenTaxInvoiced;
    }

    /**
     * Sets the value of the hiddenTaxInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHiddenTaxInvoiced(String value) {
        this.hiddenTaxInvoiced = value;
    }

    /**
     * Gets the value of the baseHiddenTaxInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseHiddenTaxInvoiced() {
        return baseHiddenTaxInvoiced;
    }

    /**
     * Sets the value of the baseHiddenTaxInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseHiddenTaxInvoiced(String value) {
        this.baseHiddenTaxInvoiced = value;
    }

    /**
     * Gets the value of the hiddenTaxRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHiddenTaxRefunded() {
        return hiddenTaxRefunded;
    }

    /**
     * Sets the value of the hiddenTaxRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHiddenTaxRefunded(String value) {
        this.hiddenTaxRefunded = value;
    }

    /**
     * Gets the value of the baseHiddenTaxRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseHiddenTaxRefunded() {
        return baseHiddenTaxRefunded;
    }

    /**
     * Sets the value of the baseHiddenTaxRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseHiddenTaxRefunded(String value) {
        this.baseHiddenTaxRefunded = value;
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
     * Gets the value of the baseCustomerBalanceInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseCustomerBalanceInvoiced() {
        return baseCustomerBalanceInvoiced;
    }

    /**
     * Sets the value of the baseCustomerBalanceInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseCustomerBalanceInvoiced(String value) {
        this.baseCustomerBalanceInvoiced = value;
    }

    /**
     * Gets the value of the customerBalanceInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerBalanceInvoiced() {
        return customerBalanceInvoiced;
    }

    /**
     * Sets the value of the customerBalanceInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerBalanceInvoiced(String value) {
        this.customerBalanceInvoiced = value;
    }

    /**
     * Gets the value of the baseCustomerBalanceRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseCustomerBalanceRefunded() {
        return baseCustomerBalanceRefunded;
    }

    /**
     * Sets the value of the baseCustomerBalanceRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseCustomerBalanceRefunded(String value) {
        this.baseCustomerBalanceRefunded = value;
    }

    /**
     * Gets the value of the customerBalanceRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerBalanceRefunded() {
        return customerBalanceRefunded;
    }

    /**
     * Sets the value of the customerBalanceRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerBalanceRefunded(String value) {
        this.customerBalanceRefunded = value;
    }

    /**
     * Gets the value of the baseCustomerBalanceTotalRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseCustomerBalanceTotalRefunded() {
        return baseCustomerBalanceTotalRefunded;
    }

    /**
     * Sets the value of the baseCustomerBalanceTotalRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseCustomerBalanceTotalRefunded(String value) {
        this.baseCustomerBalanceTotalRefunded = value;
    }

    /**
     * Gets the value of the customerBalanceTotalRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerBalanceTotalRefunded() {
        return customerBalanceTotalRefunded;
    }

    /**
     * Sets the value of the customerBalanceTotalRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerBalanceTotalRefunded(String value) {
        this.customerBalanceTotalRefunded = value;
    }

    /**
     * Gets the value of the giftCards property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftCards() {
        return giftCards;
    }

    /**
     * Sets the value of the giftCards property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftCards(String value) {
        this.giftCards = value;
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
     * Gets the value of the baseGiftCardsInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseGiftCardsInvoiced() {
        return baseGiftCardsInvoiced;
    }

    /**
     * Sets the value of the baseGiftCardsInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseGiftCardsInvoiced(String value) {
        this.baseGiftCardsInvoiced = value;
    }

    /**
     * Gets the value of the giftCardsInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftCardsInvoiced() {
        return giftCardsInvoiced;
    }

    /**
     * Sets the value of the giftCardsInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftCardsInvoiced(String value) {
        this.giftCardsInvoiced = value;
    }

    /**
     * Gets the value of the baseGiftCardsRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseGiftCardsRefunded() {
        return baseGiftCardsRefunded;
    }

    /**
     * Sets the value of the baseGiftCardsRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseGiftCardsRefunded(String value) {
        this.baseGiftCardsRefunded = value;
    }

    /**
     * Gets the value of the giftCardsRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftCardsRefunded() {
        return giftCardsRefunded;
    }

    /**
     * Sets the value of the giftCardsRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftCardsRefunded(String value) {
        this.giftCardsRefunded = value;
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
     * Gets the value of the baseRewardCurrencyAmountInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseRewardCurrencyAmountInvoiced() {
        return baseRewardCurrencyAmountInvoiced;
    }

    /**
     * Sets the value of the baseRewardCurrencyAmountInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseRewardCurrencyAmountInvoiced(String value) {
        this.baseRewardCurrencyAmountInvoiced = value;
    }

    /**
     * Gets the value of the rewardCurrencyAmountInvoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRewardCurrencyAmountInvoiced() {
        return rewardCurrencyAmountInvoiced;
    }

    /**
     * Sets the value of the rewardCurrencyAmountInvoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRewardCurrencyAmountInvoiced(String value) {
        this.rewardCurrencyAmountInvoiced = value;
    }

    /**
     * Gets the value of the baseRewardCurrencyAmountRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseRewardCurrencyAmountRefunded() {
        return baseRewardCurrencyAmountRefunded;
    }

    /**
     * Sets the value of the baseRewardCurrencyAmountRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseRewardCurrencyAmountRefunded(String value) {
        this.baseRewardCurrencyAmountRefunded = value;
    }

    /**
     * Gets the value of the rewardCurrencyAmountRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRewardCurrencyAmountRefunded() {
        return rewardCurrencyAmountRefunded;
    }

    /**
     * Sets the value of the rewardCurrencyAmountRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRewardCurrencyAmountRefunded(String value) {
        this.rewardCurrencyAmountRefunded = value;
    }

    /**
     * Gets the value of the rewardPointsBalanceRefunded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRewardPointsBalanceRefunded() {
        return rewardPointsBalanceRefunded;
    }

    /**
     * Sets the value of the rewardPointsBalanceRefunded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRewardPointsBalanceRefunded(String value) {
        this.rewardPointsBalanceRefunded = value;
    }

    /**
     * Gets the value of the rewardPointsBalanceToRefund property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRewardPointsBalanceToRefund() {
        return rewardPointsBalanceToRefund;
    }

    /**
     * Sets the value of the rewardPointsBalanceToRefund property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRewardPointsBalanceToRefund(String value) {
        this.rewardPointsBalanceToRefund = value;
    }

    /**
     * Gets the value of the rewardSalesrulePoints property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRewardSalesrulePoints() {
        return rewardSalesrulePoints;
    }

    /**
     * Sets the value of the rewardSalesrulePoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRewardSalesrulePoints(String value) {
        this.rewardSalesrulePoints = value;
    }

    /**
     * Gets the value of the firstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the value of the firstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstname(String value) {
        this.firstname = value;
    }

    /**
     * Gets the value of the lastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the value of the lastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastname(String value) {
        this.lastname = value;
    }

    /**
     * Gets the value of the telephone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Sets the value of the telephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephone(String value) {
        this.telephone = value;
    }

    /**
     * Gets the value of the postcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * Sets the value of the postcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostcode(String value) {
        this.postcode = value;
    }

}
