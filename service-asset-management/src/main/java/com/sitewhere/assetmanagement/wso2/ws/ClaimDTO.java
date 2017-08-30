
package com.sitewhere.assetmanagement.wso2.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClaimDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClaimDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="claimUri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dialectURI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayOrder" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="displayTag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regEx" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="supportedByDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClaimDTO", namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", propOrder = {
    "claimUri",
    "description",
    "dialectURI",
    "displayOrder",
    "displayTag",
    "regEx",
    "required",
    "supportedByDefault",
    "value"
})
public class ClaimDTO {

    @XmlElementRef(name = "claimUri", namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> claimUri;
    @XmlElementRef(name = "description", namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> description;
    @XmlElementRef(name = "dialectURI", namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> dialectURI;
    protected Integer displayOrder;
    @XmlElementRef(name = "displayTag", namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> displayTag;
    @XmlElementRef(name = "regEx", namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> regEx;
    protected Boolean required;
    protected Boolean supportedByDefault;
    @XmlElementRef(name = "value", namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> value;

    /**
     * Gets the value of the claimUri property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getClaimUri() {
        return claimUri;
    }

    /**
     * Sets the value of the claimUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setClaimUri(JAXBElement<String> value) {
        this.claimUri = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescription(JAXBElement<String> value) {
        this.description = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the dialectURI property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDialectURI() {
        return dialectURI;
    }

    /**
     * Sets the value of the dialectURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDialectURI(JAXBElement<String> value) {
        this.dialectURI = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the displayOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * Sets the value of the displayOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDisplayOrder(Integer value) {
        this.displayOrder = value;
    }

    /**
     * Gets the value of the displayTag property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDisplayTag() {
        return displayTag;
    }

    /**
     * Sets the value of the displayTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDisplayTag(JAXBElement<String> value) {
        this.displayTag = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the regEx property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRegEx() {
        return regEx;
    }

    /**
     * Sets the value of the regEx property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRegEx(JAXBElement<String> value) {
        this.regEx = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the required property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequired() {
        return required;
    }

    /**
     * Sets the value of the required property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequired(Boolean value) {
        this.required = value;
    }

    /**
     * Gets the value of the supportedByDefault property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSupportedByDefault() {
        return supportedByDefault;
    }

    /**
     * Sets the value of the supportedByDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSupportedByDefault(Boolean value) {
        this.supportedByDefault = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setValue(JAXBElement<String> value) {
        this.value = ((JAXBElement<String> ) value);
    }

}
