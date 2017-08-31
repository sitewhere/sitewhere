
package com.sitewhere.assetmanagement.modules.wso2.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="claims" type="{http://common.mgt.user.carbon.wso2.org/xsd}ClaimValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="profileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "userName",
    "claims",
    "profileName"
})
@XmlRootElement(name = "addUserClaimValues")
public class AddUserClaimValues {

    @XmlElementRef(name = "userName", namespace = "http://service.ws.um.carbon.wso2.org", type = JAXBElement.class)
    protected JAXBElement<String> userName;
    @XmlElement(nillable = true)
    protected List<ClaimValue> claims;
    @XmlElementRef(name = "profileName", namespace = "http://service.ws.um.carbon.wso2.org", type = JAXBElement.class)
    protected JAXBElement<String> profileName;

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUserName(JAXBElement<String> value) {
        this.userName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the claims property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the claims property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClaims().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClaimValue }
     * 
     * 
     */
    public List<ClaimValue> getClaims() {
        if (claims == null) {
            claims = new ArrayList<ClaimValue>();
        }
        return this.claims;
    }

    /**
     * Gets the value of the profileName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getProfileName() {
        return profileName;
    }

    /**
     * Sets the value of the profileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setProfileName(JAXBElement<String> value) {
        this.profileName = ((JAXBElement<String> ) value);
    }

}
