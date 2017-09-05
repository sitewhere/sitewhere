
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
 *         &lt;element name="UserStoreException" type="{http://core.user.carbon.wso2.org/xsd}AX2597UserStoreException" minOccurs="0"/>
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
    "userStoreException"
})
@XmlRootElement(name = "RemoteUserStoreManagerServiceUserStoreException")
public class RemoteUserStoreManagerServiceUserStoreException {

    @XmlElementRef(name = "UserStoreException", namespace = "http://service.ws.um.carbon.wso2.org", type = JAXBElement.class)
    protected JAXBElement<AX2597UserStoreException> userStoreException;

    /**
     * Gets the value of the userStoreException property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AX2597UserStoreException }{@code >}
     *     
     */
    public JAXBElement<AX2597UserStoreException> getUserStoreException() {
        return userStoreException;
    }

    /**
     * Sets the value of the userStoreException property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AX2597UserStoreException }{@code >}
     *     
     */
    public void setUserStoreException(JAXBElement<AX2597UserStoreException> value) {
        this.userStoreException = ((JAXBElement<AX2597UserStoreException> ) value);
    }

}
