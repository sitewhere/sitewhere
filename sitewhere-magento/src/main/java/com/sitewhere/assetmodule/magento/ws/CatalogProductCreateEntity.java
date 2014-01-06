
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductCreateEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductCreateEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="categories" type="{urn:Magento}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="websites" type="{urn:Magento}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="short_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url_key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url_path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="visibility" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="category_ids" type="{urn:Magento}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="website_ids" type="{urn:Magento}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="has_options" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gift_message_available" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="special_price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="special_from_date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="special_to_date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax_class_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tier_price" type="{urn:Magento}catalogProductTierPriceEntityArray" minOccurs="0"/>
 *         &lt;element name="meta_title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta_keyword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom_design" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom_layout_update" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="options_container" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additional_attributes" type="{urn:Magento}associativeArray" minOccurs="0"/>
 *         &lt;element name="stock_data" type="{urn:Magento}catalogInventoryStockItemUpdateEntity" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductCreateEntity", propOrder = {
    "categories",
    "websites",
    "name",
    "description",
    "shortDescription",
    "weight",
    "status",
    "urlKey",
    "urlPath",
    "visibility",
    "categoryIds",
    "websiteIds",
    "hasOptions",
    "giftMessageAvailable",
    "price",
    "specialPrice",
    "specialFromDate",
    "specialToDate",
    "taxClassId",
    "tierPrice",
    "metaTitle",
    "metaKeyword",
    "metaDescription",
    "customDesign",
    "customLayoutUpdate",
    "optionsContainer",
    "additionalAttributes",
    "stockData"
})
public class CatalogProductCreateEntity {

    protected ArrayOfString categories;
    protected ArrayOfString websites;
    protected String name;
    protected String description;
    @XmlElement(name = "short_description")
    protected String shortDescription;
    protected String weight;
    protected String status;
    @XmlElement(name = "url_key")
    protected String urlKey;
    @XmlElement(name = "url_path")
    protected String urlPath;
    protected String visibility;
    @XmlElement(name = "category_ids")
    protected ArrayOfString categoryIds;
    @XmlElement(name = "website_ids")
    protected ArrayOfString websiteIds;
    @XmlElement(name = "has_options")
    protected String hasOptions;
    @XmlElement(name = "gift_message_available")
    protected String giftMessageAvailable;
    protected String price;
    @XmlElement(name = "special_price")
    protected String specialPrice;
    @XmlElement(name = "special_from_date")
    protected String specialFromDate;
    @XmlElement(name = "special_to_date")
    protected String specialToDate;
    @XmlElement(name = "tax_class_id")
    protected String taxClassId;
    @XmlElement(name = "tier_price")
    protected CatalogProductTierPriceEntityArray tierPrice;
    @XmlElement(name = "meta_title")
    protected String metaTitle;
    @XmlElement(name = "meta_keyword")
    protected String metaKeyword;
    @XmlElement(name = "meta_description")
    protected String metaDescription;
    @XmlElement(name = "custom_design")
    protected String customDesign;
    @XmlElement(name = "custom_layout_update")
    protected String customLayoutUpdate;
    @XmlElement(name = "options_container")
    protected String optionsContainer;
    @XmlElement(name = "additional_attributes")
    protected AssociativeArray additionalAttributes;
    @XmlElement(name = "stock_data")
    protected CatalogInventoryStockItemUpdateEntity stockData;

    /**
     * Gets the value of the categories property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getCategories() {
        return categories;
    }

    /**
     * Sets the value of the categories property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setCategories(ArrayOfString value) {
        this.categories = value;
    }

    /**
     * Gets the value of the websites property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getWebsites() {
        return websites;
    }

    /**
     * Sets the value of the websites property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setWebsites(ArrayOfString value) {
        this.websites = value;
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
     * Gets the value of the shortDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the value of the shortDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortDescription(String value) {
        this.shortDescription = value;
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
     * Gets the value of the urlKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlKey() {
        return urlKey;
    }

    /**
     * Sets the value of the urlKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlKey(String value) {
        this.urlKey = value;
    }

    /**
     * Gets the value of the urlPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * Sets the value of the urlPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlPath(String value) {
        this.urlPath = value;
    }

    /**
     * Gets the value of the visibility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisibility(String value) {
        this.visibility = value;
    }

    /**
     * Gets the value of the categoryIds property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getCategoryIds() {
        return categoryIds;
    }

    /**
     * Sets the value of the categoryIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setCategoryIds(ArrayOfString value) {
        this.categoryIds = value;
    }

    /**
     * Gets the value of the websiteIds property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getWebsiteIds() {
        return websiteIds;
    }

    /**
     * Sets the value of the websiteIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setWebsiteIds(ArrayOfString value) {
        this.websiteIds = value;
    }

    /**
     * Gets the value of the hasOptions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHasOptions() {
        return hasOptions;
    }

    /**
     * Sets the value of the hasOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHasOptions(String value) {
        this.hasOptions = value;
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
     * Gets the value of the specialPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialPrice() {
        return specialPrice;
    }

    /**
     * Sets the value of the specialPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialPrice(String value) {
        this.specialPrice = value;
    }

    /**
     * Gets the value of the specialFromDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialFromDate() {
        return specialFromDate;
    }

    /**
     * Sets the value of the specialFromDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialFromDate(String value) {
        this.specialFromDate = value;
    }

    /**
     * Gets the value of the specialToDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialToDate() {
        return specialToDate;
    }

    /**
     * Sets the value of the specialToDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialToDate(String value) {
        this.specialToDate = value;
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

    /**
     * Gets the value of the tierPrice property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductTierPriceEntityArray }
     *     
     */
    public CatalogProductTierPriceEntityArray getTierPrice() {
        return tierPrice;
    }

    /**
     * Sets the value of the tierPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductTierPriceEntityArray }
     *     
     */
    public void setTierPrice(CatalogProductTierPriceEntityArray value) {
        this.tierPrice = value;
    }

    /**
     * Gets the value of the metaTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaTitle() {
        return metaTitle;
    }

    /**
     * Sets the value of the metaTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaTitle(String value) {
        this.metaTitle = value;
    }

    /**
     * Gets the value of the metaKeyword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaKeyword() {
        return metaKeyword;
    }

    /**
     * Sets the value of the metaKeyword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaKeyword(String value) {
        this.metaKeyword = value;
    }

    /**
     * Gets the value of the metaDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaDescription() {
        return metaDescription;
    }

    /**
     * Sets the value of the metaDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaDescription(String value) {
        this.metaDescription = value;
    }

    /**
     * Gets the value of the customDesign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomDesign() {
        return customDesign;
    }

    /**
     * Sets the value of the customDesign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomDesign(String value) {
        this.customDesign = value;
    }

    /**
     * Gets the value of the customLayoutUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomLayoutUpdate() {
        return customLayoutUpdate;
    }

    /**
     * Sets the value of the customLayoutUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomLayoutUpdate(String value) {
        this.customLayoutUpdate = value;
    }

    /**
     * Gets the value of the optionsContainer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionsContainer() {
        return optionsContainer;
    }

    /**
     * Sets the value of the optionsContainer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionsContainer(String value) {
        this.optionsContainer = value;
    }

    /**
     * Gets the value of the additionalAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link AssociativeArray }
     *     
     */
    public AssociativeArray getAdditionalAttributes() {
        return additionalAttributes;
    }

    /**
     * Sets the value of the additionalAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssociativeArray }
     *     
     */
    public void setAdditionalAttributes(AssociativeArray value) {
        this.additionalAttributes = value;
    }

    /**
     * Gets the value of the stockData property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogInventoryStockItemUpdateEntity }
     *     
     */
    public CatalogInventoryStockItemUpdateEntity getStockData() {
        return stockData;
    }

    /**
     * Sets the value of the stockData property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogInventoryStockItemUpdateEntity }
     *     
     */
    public void setStockData(CatalogInventoryStockItemUpdateEntity value) {
        this.stockData = value;
    }

}
