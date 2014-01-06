
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductDownloadableLinkSampleEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductDownloadableLinkSampleEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sample_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="product_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sample_file" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sample_url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sample_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sort_order" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "catalogProductDownloadableLinkSampleEntity", propOrder = {
    "sampleId",
    "productId",
    "sampleFile",
    "sampleUrl",
    "sampleType",
    "sortOrder",
    "defaultTitle",
    "storeTitle",
    "title"
})
public class CatalogProductDownloadableLinkSampleEntity {

    @XmlElement(name = "sample_id", required = true)
    protected String sampleId;
    @XmlElement(name = "product_id", required = true)
    protected String productId;
    @XmlElement(name = "sample_file")
    protected String sampleFile;
    @XmlElement(name = "sample_url")
    protected String sampleUrl;
    @XmlElement(name = "sample_type", required = true)
    protected String sampleType;
    @XmlElement(name = "sort_order", required = true)
    protected String sortOrder;
    @XmlElement(name = "default_title", required = true)
    protected String defaultTitle;
    @XmlElement(name = "store_title", required = true)
    protected String storeTitle;
    @XmlElement(required = true)
    protected String title;

    /**
     * Gets the value of the sampleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleId() {
        return sampleId;
    }

    /**
     * Sets the value of the sampleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleId(String value) {
        this.sampleId = value;
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
     * Gets the value of the sampleFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleFile() {
        return sampleFile;
    }

    /**
     * Sets the value of the sampleFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleFile(String value) {
        this.sampleFile = value;
    }

    /**
     * Gets the value of the sampleUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleUrl() {
        return sampleUrl;
    }

    /**
     * Sets the value of the sampleUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleUrl(String value) {
        this.sampleUrl = value;
    }

    /**
     * Gets the value of the sampleType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleType() {
        return sampleType;
    }

    /**
     * Sets the value of the sampleType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleType(String value) {
        this.sampleType = value;
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
