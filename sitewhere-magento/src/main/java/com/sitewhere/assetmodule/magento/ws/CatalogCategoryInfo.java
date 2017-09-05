
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogCategoryInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogCategoryInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="category_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="is_active" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parent_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="all_children" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="children" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="created_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated_at" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url_key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta_title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta_keywords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url_path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="children_count" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="display_mode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_anchor" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="available_sort_by" type="{urn:Magento}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="custom_design" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom_design_apply" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom_design_from" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom_design_to" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="page_layout" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom_layout_update" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="default_sort_by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="landing_page" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogCategoryInfo", propOrder = {
    "categoryId",
    "isActive",
    "position",
    "level",
    "parentId",
    "allChildren",
    "children",
    "createdAt",
    "updatedAt",
    "name",
    "urlKey",
    "description",
    "metaTitle",
    "metaKeywords",
    "metaDescription",
    "path",
    "urlPath",
    "childrenCount",
    "displayMode",
    "isAnchor",
    "availableSortBy",
    "customDesign",
    "customDesignApply",
    "customDesignFrom",
    "customDesignTo",
    "pageLayout",
    "customLayoutUpdate",
    "defaultSortBy",
    "landingPage"
})
public class CatalogCategoryInfo {

    @XmlElement(name = "category_id", required = true)
    protected String categoryId;
    @XmlElement(name = "is_active")
    protected int isActive;
    @XmlElement(required = true)
    protected String position;
    @XmlElement(required = true)
    protected String level;
    @XmlElement(name = "parent_id", required = true)
    protected String parentId;
    @XmlElement(name = "all_children", required = true)
    protected String allChildren;
    @XmlElement(required = true)
    protected String children;
    @XmlElement(name = "created_at")
    protected String createdAt;
    @XmlElement(name = "updated_at")
    protected String updatedAt;
    protected String name;
    @XmlElement(name = "url_key")
    protected String urlKey;
    protected String description;
    @XmlElement(name = "meta_title")
    protected String metaTitle;
    @XmlElement(name = "meta_keywords")
    protected String metaKeywords;
    @XmlElement(name = "meta_description")
    protected String metaDescription;
    protected String path;
    @XmlElement(name = "url_path")
    protected String urlPath;
    @XmlElement(name = "children_count")
    protected Integer childrenCount;
    @XmlElement(name = "display_mode")
    protected String displayMode;
    @XmlElement(name = "is_anchor")
    protected Integer isAnchor;
    @XmlElement(name = "available_sort_by")
    protected ArrayOfString availableSortBy;
    @XmlElement(name = "custom_design")
    protected String customDesign;
    @XmlElement(name = "custom_design_apply")
    protected String customDesignApply;
    @XmlElement(name = "custom_design_from")
    protected String customDesignFrom;
    @XmlElement(name = "custom_design_to")
    protected String customDesignTo;
    @XmlElement(name = "page_layout")
    protected String pageLayout;
    @XmlElement(name = "custom_layout_update")
    protected String customLayoutUpdate;
    @XmlElement(name = "default_sort_by")
    protected String defaultSortBy;
    @XmlElement(name = "landing_page")
    protected Integer landingPage;

    /**
     * Gets the value of the categoryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the value of the categoryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryId(String value) {
        this.categoryId = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     */
    public int getIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     */
    public void setIsActive(int value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPosition(String value) {
        this.position = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel(String value) {
        this.level = value;
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
     * Gets the value of the allChildren property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllChildren() {
        return allChildren;
    }

    /**
     * Sets the value of the allChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllChildren(String value) {
        this.allChildren = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildren(String value) {
        this.children = value;
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
     * Gets the value of the metaKeywords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaKeywords() {
        return metaKeywords;
    }

    /**
     * Sets the value of the metaKeywords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaKeywords(String value) {
        this.metaKeywords = value;
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
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
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
     * Gets the value of the childrenCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getChildrenCount() {
        return childrenCount;
    }

    /**
     * Sets the value of the childrenCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setChildrenCount(Integer value) {
        this.childrenCount = value;
    }

    /**
     * Gets the value of the displayMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayMode() {
        return displayMode;
    }

    /**
     * Sets the value of the displayMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayMode(String value) {
        this.displayMode = value;
    }

    /**
     * Gets the value of the isAnchor property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsAnchor() {
        return isAnchor;
    }

    /**
     * Sets the value of the isAnchor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsAnchor(Integer value) {
        this.isAnchor = value;
    }

    /**
     * Gets the value of the availableSortBy property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getAvailableSortBy() {
        return availableSortBy;
    }

    /**
     * Sets the value of the availableSortBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setAvailableSortBy(ArrayOfString value) {
        this.availableSortBy = value;
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
     * Gets the value of the customDesignApply property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomDesignApply() {
        return customDesignApply;
    }

    /**
     * Sets the value of the customDesignApply property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomDesignApply(String value) {
        this.customDesignApply = value;
    }

    /**
     * Gets the value of the customDesignFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomDesignFrom() {
        return customDesignFrom;
    }

    /**
     * Sets the value of the customDesignFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomDesignFrom(String value) {
        this.customDesignFrom = value;
    }

    /**
     * Gets the value of the customDesignTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomDesignTo() {
        return customDesignTo;
    }

    /**
     * Sets the value of the customDesignTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomDesignTo(String value) {
        this.customDesignTo = value;
    }

    /**
     * Gets the value of the pageLayout property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPageLayout() {
        return pageLayout;
    }

    /**
     * Sets the value of the pageLayout property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPageLayout(String value) {
        this.pageLayout = value;
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
     * Gets the value of the defaultSortBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultSortBy() {
        return defaultSortBy;
    }

    /**
     * Sets the value of the defaultSortBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultSortBy(String value) {
        this.defaultSortBy = value;
    }

    /**
     * Gets the value of the landingPage property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLandingPage() {
        return landingPage;
    }

    /**
     * Sets the value of the landingPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLandingPage(Integer value) {
        this.landingPage = value;
    }

}
