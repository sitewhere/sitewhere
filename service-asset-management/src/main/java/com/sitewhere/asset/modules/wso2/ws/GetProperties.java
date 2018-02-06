
package com.sitewhere.asset.modules.wso2.ws;

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
 *         &lt;element name="tenant" type="{http://tenant.core.user.carbon.wso2.org/xsd}AX2604Tenant" minOccurs="0"/>
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
    "tenant"
})
@XmlRootElement(name = "getProperties")
public class GetProperties {

    @XmlElementRef(name = "tenant", namespace = "http://service.ws.um.carbon.wso2.org", type = JAXBElement.class)
    protected JAXBElement<AX2604Tenant> tenant;

    /**
     * Gets the value of the tenant property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AX2604Tenant }{@code >}
     *     
     */
    public JAXBElement<AX2604Tenant> getTenant() {
        return tenant;
    }

    /**
     * Sets the value of the tenant property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AX2604Tenant }{@code >}
     *     
     */
    public void setTenant(JAXBElement<AX2604Tenant> value) {
        this.tenant = ((JAXBElement<AX2604Tenant> ) value);
    }

}
