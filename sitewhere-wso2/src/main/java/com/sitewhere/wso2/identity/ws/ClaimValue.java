
package com.sitewhere.wso2.identity.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClaimValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClaimValue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="claimURI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "ClaimValue", namespace = "http://common.mgt.user.carbon.wso2.org/xsd", propOrder = {
    "claimURI",
    "value"
})
public class ClaimValue {

    @XmlElementRef(name = "claimURI", namespace = "http://common.mgt.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> claimURI;
    @XmlElementRef(name = "value", namespace = "http://common.mgt.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> value;

    /**
     * Gets the value of the claimURI property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getClaimURI() {
        return claimURI;
    }

    /**
     * Sets the value of the claimURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setClaimURI(JAXBElement<String> value) {
        this.claimURI = ((JAXBElement<String> ) value);
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
