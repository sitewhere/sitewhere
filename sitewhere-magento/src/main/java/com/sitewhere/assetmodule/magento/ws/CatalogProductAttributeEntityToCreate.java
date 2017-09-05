
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductAttributeEntityToCreate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductAttributeEntityToCreate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attribute_code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="frontend_input" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="scope" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="default_value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_unique" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_required" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="apply_to" type="{urn:Magento}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="is_configurable" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_searchable" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_visible_in_advanced_search" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_comparable" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_used_for_promo_rules" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_visible_on_front" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="used_in_product_listing" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="additional_fields" type="{urn:Magento}associativeArray" minOccurs="0"/>
 *         &lt;element name="frontend_label" type="{urn:Magento}catalogProductAttributeFrontendLabelArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductAttributeEntityToCreate", propOrder = {
    "attributeCode",
    "frontendInput",
    "scope",
    "defaultValue",
    "isUnique",
    "isRequired",
    "applyTo",
    "isConfigurable",
    "isSearchable",
    "isVisibleInAdvancedSearch",
    "isComparable",
    "isUsedForPromoRules",
    "isVisibleOnFront",
    "usedInProductListing",
    "additionalFields",
    "frontendLabel"
})
public class CatalogProductAttributeEntityToCreate {

    @XmlElement(name = "attribute_code", required = true)
    protected String attributeCode;
    @XmlElement(name = "frontend_input", required = true)
    protected String frontendInput;
    protected String scope;
    @XmlElement(name = "default_value")
    protected String defaultValue;
    @XmlElement(name = "is_unique")
    protected Integer isUnique;
    @XmlElement(name = "is_required")
    protected Integer isRequired;
    @XmlElement(name = "apply_to")
    protected ArrayOfString applyTo;
    @XmlElement(name = "is_configurable")
    protected Integer isConfigurable;
    @XmlElement(name = "is_searchable")
    protected Integer isSearchable;
    @XmlElement(name = "is_visible_in_advanced_search")
    protected Integer isVisibleInAdvancedSearch;
    @XmlElement(name = "is_comparable")
    protected Integer isComparable;
    @XmlElement(name = "is_used_for_promo_rules")
    protected Integer isUsedForPromoRules;
    @XmlElement(name = "is_visible_on_front")
    protected Integer isVisibleOnFront;
    @XmlElement(name = "used_in_product_listing")
    protected Integer usedInProductListing;
    @XmlElement(name = "additional_fields")
    protected AssociativeArray additionalFields;
    @XmlElement(name = "frontend_label")
    protected CatalogProductAttributeFrontendLabelArray frontendLabel;

    /**
     * Gets the value of the attributeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeCode() {
        return attributeCode;
    }

    /**
     * Sets the value of the attributeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeCode(String value) {
        this.attributeCode = value;
    }

    /**
     * Gets the value of the frontendInput property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrontendInput() {
        return frontendInput;
    }

    /**
     * Sets the value of the frontendInput property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrontendInput(String value) {
        this.frontendInput = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScope(String value) {
        this.scope = value;
    }

    /**
     * Gets the value of the defaultValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the isUnique property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsUnique() {
        return isUnique;
    }

    /**
     * Sets the value of the isUnique property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsUnique(Integer value) {
        this.isUnique = value;
    }

    /**
     * Gets the value of the isRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsRequired() {
        return isRequired;
    }

    /**
     * Sets the value of the isRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsRequired(Integer value) {
        this.isRequired = value;
    }

    /**
     * Gets the value of the applyTo property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getApplyTo() {
        return applyTo;
    }

    /**
     * Sets the value of the applyTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setApplyTo(ArrayOfString value) {
        this.applyTo = value;
    }

    /**
     * Gets the value of the isConfigurable property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsConfigurable() {
        return isConfigurable;
    }

    /**
     * Sets the value of the isConfigurable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsConfigurable(Integer value) {
        this.isConfigurable = value;
    }

    /**
     * Gets the value of the isSearchable property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsSearchable() {
        return isSearchable;
    }

    /**
     * Sets the value of the isSearchable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsSearchable(Integer value) {
        this.isSearchable = value;
    }

    /**
     * Gets the value of the isVisibleInAdvancedSearch property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsVisibleInAdvancedSearch() {
        return isVisibleInAdvancedSearch;
    }

    /**
     * Sets the value of the isVisibleInAdvancedSearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsVisibleInAdvancedSearch(Integer value) {
        this.isVisibleInAdvancedSearch = value;
    }

    /**
     * Gets the value of the isComparable property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsComparable() {
        return isComparable;
    }

    /**
     * Sets the value of the isComparable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsComparable(Integer value) {
        this.isComparable = value;
    }

    /**
     * Gets the value of the isUsedForPromoRules property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsUsedForPromoRules() {
        return isUsedForPromoRules;
    }

    /**
     * Sets the value of the isUsedForPromoRules property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsUsedForPromoRules(Integer value) {
        this.isUsedForPromoRules = value;
    }

    /**
     * Gets the value of the isVisibleOnFront property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsVisibleOnFront() {
        return isVisibleOnFront;
    }

    /**
     * Sets the value of the isVisibleOnFront property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsVisibleOnFront(Integer value) {
        this.isVisibleOnFront = value;
    }

    /**
     * Gets the value of the usedInProductListing property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUsedInProductListing() {
        return usedInProductListing;
    }

    /**
     * Sets the value of the usedInProductListing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUsedInProductListing(Integer value) {
        this.usedInProductListing = value;
    }

    /**
     * Gets the value of the additionalFields property.
     * 
     * @return
     *     possible object is
     *     {@link AssociativeArray }
     *     
     */
    public AssociativeArray getAdditionalFields() {
        return additionalFields;
    }

    /**
     * Sets the value of the additionalFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssociativeArray }
     *     
     */
    public void setAdditionalFields(AssociativeArray value) {
        this.additionalFields = value;
    }

    /**
     * Gets the value of the frontendLabel property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductAttributeFrontendLabelArray }
     *     
     */
    public CatalogProductAttributeFrontendLabelArray getFrontendLabel() {
        return frontendLabel;
    }

    /**
     * Sets the value of the frontendLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductAttributeFrontendLabelArray }
     *     
     */
    public void setFrontendLabel(CatalogProductAttributeFrontendLabelArray value) {
        this.frontendLabel = value;
    }

}
