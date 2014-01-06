
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributeSetId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="forceProductsRemove" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sessionId",
    "attributeSetId",
    "forceProductsRemove"
})
@XmlRootElement(name = "catalogProductAttributeSetRemoveRequestParam")
public class CatalogProductAttributeSetRemoveRequestParam {

    @XmlElement(required = true)
    protected String sessionId;
    @XmlElement(required = true)
    protected String attributeSetId;
    protected String forceProductsRemove;

    /**
     * Gets the value of the sessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the attributeSetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeSetId() {
        return attributeSetId;
    }

    /**
     * Sets the value of the attributeSetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeSetId(String value) {
        this.attributeSetId = value;
    }

    /**
     * Gets the value of the forceProductsRemove property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForceProductsRemove() {
        return forceProductsRemove;
    }

    /**
     * Sets the value of the forceProductsRemove property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForceProductsRemove(String value) {
        this.forceProductsRemove = value;
    }

}
