
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductDownloadableLinkAddEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductDownloadableLinkAddEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_unlimited" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="number_of_downloads" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_shareable" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sample" type="{urn:Magento}catalogProductDownloadableLinkAddSampleEntity" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="file" type="{urn:Magento}catalogProductDownloadableLinkFileEntity" minOccurs="0"/>
 *         &lt;element name="link_url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sample_url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sort_order" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductDownloadableLinkAddEntity", propOrder = {
    "title",
    "price",
    "isUnlimited",
    "numberOfDownloads",
    "isShareable",
    "sample",
    "type",
    "file",
    "linkUrl",
    "sampleUrl",
    "sortOrder"
})
public class CatalogProductDownloadableLinkAddEntity {

    @XmlElement(required = true)
    protected String title;
    protected String price;
    @XmlElement(name = "is_unlimited")
    protected Integer isUnlimited;
    @XmlElement(name = "number_of_downloads")
    protected Integer numberOfDownloads;
    @XmlElement(name = "is_shareable")
    protected Integer isShareable;
    protected CatalogProductDownloadableLinkAddSampleEntity sample;
    protected String type;
    protected CatalogProductDownloadableLinkFileEntity file;
    @XmlElement(name = "link_url")
    protected String linkUrl;
    @XmlElement(name = "sample_url")
    protected String sampleUrl;
    @XmlElement(name = "sort_order")
    protected Integer sortOrder;

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
     * Gets the value of the isUnlimited property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsUnlimited() {
        return isUnlimited;
    }

    /**
     * Sets the value of the isUnlimited property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsUnlimited(Integer value) {
        this.isUnlimited = value;
    }

    /**
     * Gets the value of the numberOfDownloads property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfDownloads() {
        return numberOfDownloads;
    }

    /**
     * Sets the value of the numberOfDownloads property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfDownloads(Integer value) {
        this.numberOfDownloads = value;
    }

    /**
     * Gets the value of the isShareable property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsShareable() {
        return isShareable;
    }

    /**
     * Sets the value of the isShareable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsShareable(Integer value) {
        this.isShareable = value;
    }

    /**
     * Gets the value of the sample property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductDownloadableLinkAddSampleEntity }
     *     
     */
    public CatalogProductDownloadableLinkAddSampleEntity getSample() {
        return sample;
    }

    /**
     * Sets the value of the sample property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductDownloadableLinkAddSampleEntity }
     *     
     */
    public void setSample(CatalogProductDownloadableLinkAddSampleEntity value) {
        this.sample = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductDownloadableLinkFileEntity }
     *     
     */
    public CatalogProductDownloadableLinkFileEntity getFile() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductDownloadableLinkFileEntity }
     *     
     */
    public void setFile(CatalogProductDownloadableLinkFileEntity value) {
        this.file = value;
    }

    /**
     * Gets the value of the linkUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkUrl() {
        return linkUrl;
    }

    /**
     * Sets the value of the linkUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkUrl(String value) {
        this.linkUrl = value;
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
     * Gets the value of the sortOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the value of the sortOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSortOrder(Integer value) {
        this.sortOrder = value;
    }

}
