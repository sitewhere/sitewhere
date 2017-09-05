
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductDownloadableLinkEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductDownloadableLinkEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="link_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="number_of_downloads" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_unlimited" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is_shareable" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="link_url" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="link_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sample_file" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sample_url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sample_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sort_order" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="file_save" type="{urn:Magento}catalogProductDownloadableLinkFileInfoEntityArray" minOccurs="0"/>
 *         &lt;element name="sample_file_save" type="{urn:Magento}catalogProductDownloadableLinkFileInfoEntityArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductDownloadableLinkEntity", propOrder = {
    "linkId",
    "title",
    "price",
    "numberOfDownloads",
    "isUnlimited",
    "isShareable",
    "linkUrl",
    "linkType",
    "sampleFile",
    "sampleUrl",
    "sampleType",
    "sortOrder",
    "fileSave",
    "sampleFileSave"
})
public class CatalogProductDownloadableLinkEntity {

    @XmlElement(name = "link_id", required = true)
    protected String linkId;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true)
    protected String price;
    @XmlElement(name = "number_of_downloads")
    protected Integer numberOfDownloads;
    @XmlElement(name = "is_unlimited")
    protected Integer isUnlimited;
    @XmlElement(name = "is_shareable")
    protected int isShareable;
    @XmlElement(name = "link_url", required = true)
    protected String linkUrl;
    @XmlElement(name = "link_type", required = true)
    protected String linkType;
    @XmlElement(name = "sample_file")
    protected String sampleFile;
    @XmlElement(name = "sample_url")
    protected String sampleUrl;
    @XmlElement(name = "sample_type", required = true)
    protected String sampleType;
    @XmlElement(name = "sort_order")
    protected int sortOrder;
    @XmlElement(name = "file_save")
    protected CatalogProductDownloadableLinkFileInfoEntityArray fileSave;
    @XmlElement(name = "sample_file_save")
    protected CatalogProductDownloadableLinkFileInfoEntityArray sampleFileSave;

    /**
     * Gets the value of the linkId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkId() {
        return linkId;
    }

    /**
     * Sets the value of the linkId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkId(String value) {
        this.linkId = value;
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
     * Gets the value of the isShareable property.
     * 
     */
    public int getIsShareable() {
        return isShareable;
    }

    /**
     * Sets the value of the isShareable property.
     * 
     */
    public void setIsShareable(int value) {
        this.isShareable = value;
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
     * Gets the value of the linkType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkType() {
        return linkType;
    }

    /**
     * Sets the value of the linkType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkType(String value) {
        this.linkType = value;
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
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the value of the sortOrder property.
     * 
     */
    public void setSortOrder(int value) {
        this.sortOrder = value;
    }

    /**
     * Gets the value of the fileSave property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductDownloadableLinkFileInfoEntityArray }
     *     
     */
    public CatalogProductDownloadableLinkFileInfoEntityArray getFileSave() {
        return fileSave;
    }

    /**
     * Sets the value of the fileSave property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductDownloadableLinkFileInfoEntityArray }
     *     
     */
    public void setFileSave(CatalogProductDownloadableLinkFileInfoEntityArray value) {
        this.fileSave = value;
    }

    /**
     * Gets the value of the sampleFileSave property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductDownloadableLinkFileInfoEntityArray }
     *     
     */
    public CatalogProductDownloadableLinkFileInfoEntityArray getSampleFileSave() {
        return sampleFileSave;
    }

    /**
     * Sets the value of the sampleFileSave property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductDownloadableLinkFileInfoEntityArray }
     *     
     */
    public void setSampleFileSave(CatalogProductDownloadableLinkFileInfoEntityArray value) {
        this.sampleFileSave = value;
    }

}
