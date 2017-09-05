
package com.sitewhere.wso2.identity.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element name="claimUri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="claimValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "claimUri",
    "claimValue",
    "profile"
})
@XmlRootElement(name = "getUserList")
public class GetUserList {

    @XmlElementRef(name = "claimUri", namespace = "http://service.ws.um.carbon.wso2.org", type = JAXBElement.class)
    protected JAXBElement<String> claimUri;
    @XmlElementRef(name = "claimValue", namespace = "http://service.ws.um.carbon.wso2.org", type = JAXBElement.class)
    protected JAXBElement<String> claimValue;
    @XmlElementRef(name = "profile", namespace = "http://service.ws.um.carbon.wso2.org", type = JAXBElement.class)
    protected JAXBElement<String> profile;

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
     * Gets the value of the claimValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getClaimValue() {
        return claimValue;
    }

    /**
     * Sets the value of the claimValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setClaimValue(JAXBElement<String> value) {
        this.claimValue = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setProfile(JAXBElement<String> value) {
        this.profile = ((JAXBElement<String> ) value);
    }

}
