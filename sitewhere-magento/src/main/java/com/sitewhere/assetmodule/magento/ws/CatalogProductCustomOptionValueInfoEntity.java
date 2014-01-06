
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductCustomOptionValueInfoEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductCustomOptionValueInfoEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="value_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="option_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sku" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sort_order" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="default_price" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="default_price_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="store_price" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="store_price_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="price_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="default_title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="store_title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductCustomOptionValueInfoEntity", propOrder = {
    "valueId",
    "optionId",
    "sku",
    "sortOrder",
    "defaultPrice",
    "defaultPriceType",
    "storePrice",
    "storePriceType",
    "price",
    "priceType",
    "defaultTitle",
    "storeTitle",
    "title"
})
public class CatalogProductCustomOptionValueInfoEntity {

    @XmlElement(name = "value_id", required = true)
    protected String valueId;
    @XmlElement(name = "option_id", required = true)
    protected String optionId;
    @XmlElement(required = true)
    protected String sku;
    @XmlElement(name = "sort_order", required = true)
    protected String sortOrder;
    @XmlElement(name = "default_price", required = true)
    protected String defaultPrice;
    @XmlElement(name = "default_price_type", required = true)
    protected String defaultPriceType;
    @XmlElement(name = "store_price", required = true)
    protected String storePrice;
    @XmlElement(name = "store_price_type", required = true)
    protected String storePriceType;
    @XmlElement(required = true)
    protected String price;
    @XmlElement(name = "price_type", required = true)
    protected String priceType;
    @XmlElement(name = "default_title", required = true)
    protected String defaultTitle;
    @XmlElement(name = "store_title", required = true)
    protected String storeTitle;
    @XmlElement(required = true)
    protected String title;

    /**
     * Gets the value of the valueId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueId() {
        return valueId;
    }

    /**
     * Sets the value of the valueId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueId(String value) {
        this.valueId = value;
    }

    /**
     * Gets the value of the optionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionId() {
        return optionId;
    }

    /**
     * Sets the value of the optionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionId(String value) {
        this.optionId = value;
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
     * Gets the value of the sortOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the value of the sortOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortOrder(String value) {
        this.sortOrder = value;
    }

    /**
     * Gets the value of the defaultPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultPrice() {
        return defaultPrice;
    }

    /**
     * Sets the value of the defaultPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultPrice(String value) {
        this.defaultPrice = value;
    }

    /**
     * Gets the value of the defaultPriceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultPriceType() {
        return defaultPriceType;
    }

    /**
     * Sets the value of the defaultPriceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultPriceType(String value) {
        this.defaultPriceType = value;
    }

    /**
     * Gets the value of the storePrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStorePrice() {
        return storePrice;
    }

    /**
     * Sets the value of the storePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStorePrice(String value) {
        this.storePrice = value;
    }

    /**
     * Gets the value of the storePriceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStorePriceType() {
        return storePriceType;
    }

    /**
     * Sets the value of the storePriceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStorePriceType(String value) {
        this.storePriceType = value;
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
     * Gets the value of the priceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriceType() {
        return priceType;
    }

    /**
     * Sets the value of the priceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriceType(String value) {
        this.priceType = value;
    }

    /**
     * Gets the value of the defaultTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultTitle() {
        return defaultTitle;
    }

    /**
     * Sets the value of the defaultTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultTitle(String value) {
        this.defaultTitle = value;
    }

    /**
     * Gets the value of the storeTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreTitle() {
        return storeTitle;
    }

    /**
     * Sets the value of the storeTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreTitle(String value) {
        this.storeTitle = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

}
