
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductDownloadableLinkListEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductDownloadableLinkListEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="links" type="{urn:Magento}catalogProductDownloadableLinkEntityArray"/>
 *         &lt;element name="samples" type="{urn:Magento}catalogProductDownloadableLinkSampleEntityArray"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductDownloadableLinkListEntity", propOrder = {
    "links",
    "samples"
})
public class CatalogProductDownloadableLinkListEntity {

    @XmlElement(required = true)
    protected CatalogProductDownloadableLinkEntityArray links;
    @XmlElement(required = true)
    protected CatalogProductDownloadableLinkSampleEntityArray samples;

    /**
     * Gets the value of the links property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductDownloadableLinkEntityArray }
     *     
     */
    public CatalogProductDownloadableLinkEntityArray getLinks() {
        return links;
    }

    /**
     * Sets the value of the links property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductDownloadableLinkEntityArray }
     *     
     */
    public void setLinks(CatalogProductDownloadableLinkEntityArray value) {
        this.links = value;
    }

    /**
     * Gets the value of the samples property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogProductDownloadableLinkSampleEntityArray }
     *     
     */
    public CatalogProductDownloadableLinkSampleEntityArray getSamples() {
        return samples;
    }

    /**
     * Sets the value of the samples property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogProductDownloadableLinkSampleEntityArray }
     *     
     */
    public void setSamples(CatalogProductDownloadableLinkSampleEntityArray value) {
        this.samples = value;
    }

}
