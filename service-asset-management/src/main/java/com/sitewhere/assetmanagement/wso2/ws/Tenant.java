
package com.sitewhere.assetmanagement.wso2.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Tenant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Tenant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="adminFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adminFullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adminLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adminName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adminPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createdDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="domain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="realmConfig" type="{http://api.user.carbon.wso2.org/xsd}RealmConfiguration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tenant", namespace = "http://api.user.carbon.wso2.org/xsd", propOrder = {
    "active",
    "adminFirstName",
    "adminFullName",
    "adminLastName",
    "adminName",
    "adminPassword",
    "createdDate",
    "domain",
    "email",
    "id",
    "realmConfig"
})
@XmlSeeAlso({
    AX2604Tenant.class
})
public class Tenant {

    protected Boolean active;
    @XmlElementRef(name = "adminFirstName", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> adminFirstName;
    @XmlElementRef(name = "adminFullName", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> adminFullName;
    @XmlElementRef(name = "adminLastName", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> adminLastName;
    @XmlElementRef(name = "adminName", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> adminName;
    @XmlElementRef(name = "adminPassword", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> adminPassword;
    @XmlElementRef(name = "createdDate", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> createdDate;
    @XmlElementRef(name = "domain", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> domain;
    @XmlElementRef(name = "email", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> email;
    protected Integer id;
    @XmlElementRef(name = "realmConfig", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<RealmConfiguration> realmConfig;

    /**
     * Gets the value of the active property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setActive(Boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the adminFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdminFirstName() {
        return adminFirstName;
    }

    /**
     * Sets the value of the adminFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdminFirstName(JAXBElement<String> value) {
        this.adminFirstName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the adminFullName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdminFullName() {
        return adminFullName;
    }

    /**
     * Sets the value of the adminFullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdminFullName(JAXBElement<String> value) {
        this.adminFullName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the adminLastName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdminLastName() {
        return adminLastName;
    }

    /**
     * Sets the value of the adminLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdminLastName(JAXBElement<String> value) {
        this.adminLastName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the adminName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdminName() {
        return adminName;
    }

    /**
     * Sets the value of the adminName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdminName(JAXBElement<String> value) {
        this.adminName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the adminPassword property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdminPassword() {
        return adminPassword;
    }

    /**
     * Sets the value of the adminPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdminPassword(JAXBElement<String> value) {
        this.adminPassword = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the createdDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the value of the createdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setCreatedDate(JAXBElement<XMLGregorianCalendar> value) {
        this.createdDate = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDomain(JAXBElement<String> value) {
        this.domain = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmail(JAXBElement<String> value) {
        this.email = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the realmConfig property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RealmConfiguration }{@code >}
     *     
     */
    public JAXBElement<RealmConfiguration> getRealmConfig() {
        return realmConfig;
    }

    /**
     * Sets the value of the realmConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RealmConfiguration }{@code >}
     *     
     */
    public void setRealmConfig(JAXBElement<RealmConfiguration> value) {
        this.realmConfig = ((JAXBElement<RealmConfiguration> ) value);
    }

}
