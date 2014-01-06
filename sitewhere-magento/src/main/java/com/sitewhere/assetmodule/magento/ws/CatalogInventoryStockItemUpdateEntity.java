
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogInventoryStockItemUpdateEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogInventoryStockItemUpdateEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="qty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_in_stock" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="manage_stock" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="use_config_manage_stock" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="min_qty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="use_config_min_qty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="min_sale_qty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="use_config_min_sale_qty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="max_sale_qty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="use_config_max_sale_qty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_qty_decimal" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="backorders" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="use_config_backorders" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="notify_stock_qty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="use_config_notify_stock_qty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogInventoryStockItemUpdateEntity", propOrder = {
    "qty",
    "isInStock",
    "manageStock",
    "useConfigManageStock",
    "minQty",
    "useConfigMinQty",
    "minSaleQty",
    "useConfigMinSaleQty",
    "maxSaleQty",
    "useConfigMaxSaleQty",
    "isQtyDecimal",
    "backorders",
    "useConfigBackorders",
    "notifyStockQty",
    "useConfigNotifyStockQty"
})
public class CatalogInventoryStockItemUpdateEntity {

    protected String qty;
    @XmlElement(name = "is_in_stock")
    protected Integer isInStock;
    @XmlElement(name = "manage_stock")
    protected Integer manageStock;
    @XmlElement(name = "use_config_manage_stock")
    protected Integer useConfigManageStock;
    @XmlElement(name = "min_qty")
    protected Integer minQty;
    @XmlElement(name = "use_config_min_qty")
    protected Integer useConfigMinQty;
    @XmlElement(name = "min_sale_qty")
    protected Integer minSaleQty;
    @XmlElement(name = "use_config_min_sale_qty")
    protected Integer useConfigMinSaleQty;
    @XmlElement(name = "max_sale_qty")
    protected Integer maxSaleQty;
    @XmlElement(name = "use_config_max_sale_qty")
    protected Integer useConfigMaxSaleQty;
    @XmlElement(name = "is_qty_decimal")
    protected Integer isQtyDecimal;
    protected Integer backorders;
    @XmlElement(name = "use_config_backorders")
    protected Integer useConfigBackorders;
    @XmlElement(name = "notify_stock_qty")
    protected Integer notifyStockQty;
    @XmlElement(name = "use_config_notify_stock_qty")
    protected Integer useConfigNotifyStockQty;

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
     * Gets the value of the isInStock property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsInStock() {
        return isInStock;
    }

    /**
     * Sets the value of the isInStock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsInStock(Integer value) {
        this.isInStock = value;
    }

    /**
     * Gets the value of the manageStock property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getManageStock() {
        return manageStock;
    }

    /**
     * Sets the value of the manageStock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setManageStock(Integer value) {
        this.manageStock = value;
    }

    /**
     * Gets the value of the useConfigManageStock property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUseConfigManageStock() {
        return useConfigManageStock;
    }

    /**
     * Sets the value of the useConfigManageStock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUseConfigManageStock(Integer value) {
        this.useConfigManageStock = value;
    }

    /**
     * Gets the value of the minQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinQty() {
        return minQty;
    }

    /**
     * Sets the value of the minQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinQty(Integer value) {
        this.minQty = value;
    }

    /**
     * Gets the value of the useConfigMinQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUseConfigMinQty() {
        return useConfigMinQty;
    }

    /**
     * Sets the value of the useConfigMinQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUseConfigMinQty(Integer value) {
        this.useConfigMinQty = value;
    }

    /**
     * Gets the value of the minSaleQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinSaleQty() {
        return minSaleQty;
    }

    /**
     * Sets the value of the minSaleQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinSaleQty(Integer value) {
        this.minSaleQty = value;
    }

    /**
     * Gets the value of the useConfigMinSaleQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUseConfigMinSaleQty() {
        return useConfigMinSaleQty;
    }

    /**
     * Sets the value of the useConfigMinSaleQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUseConfigMinSaleQty(Integer value) {
        this.useConfigMinSaleQty = value;
    }

    /**
     * Gets the value of the maxSaleQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxSaleQty() {
        return maxSaleQty;
    }

    /**
     * Sets the value of the maxSaleQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxSaleQty(Integer value) {
        this.maxSaleQty = value;
    }

    /**
     * Gets the value of the useConfigMaxSaleQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUseConfigMaxSaleQty() {
        return useConfigMaxSaleQty;
    }

    /**
     * Sets the value of the useConfigMaxSaleQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUseConfigMaxSaleQty(Integer value) {
        this.useConfigMaxSaleQty = value;
    }

    /**
     * Gets the value of the isQtyDecimal property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsQtyDecimal() {
        return isQtyDecimal;
    }

    /**
     * Sets the value of the isQtyDecimal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsQtyDecimal(Integer value) {
        this.isQtyDecimal = value;
    }

    /**
     * Gets the value of the backorders property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBackorders() {
        return backorders;
    }

    /**
     * Sets the value of the backorders property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBackorders(Integer value) {
        this.backorders = value;
    }

    /**
     * Gets the value of the useConfigBackorders property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUseConfigBackorders() {
        return useConfigBackorders;
    }

    /**
     * Sets the value of the useConfigBackorders property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUseConfigBackorders(Integer value) {
        this.useConfigBackorders = value;
    }

    /**
     * Gets the value of the notifyStockQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNotifyStockQty() {
        return notifyStockQty;
    }

    /**
     * Sets the value of the notifyStockQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNotifyStockQty(Integer value) {
        this.notifyStockQty = value;
    }

    /**
     * Gets the value of the useConfigNotifyStockQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUseConfigNotifyStockQty() {
        return useConfigNotifyStockQty;
    }

    /**
     * Sets the value of the useConfigNotifyStockQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUseConfigNotifyStockQty(Integer value) {
        this.useConfigNotifyStockQty = value;
    }

}
