
package com.sitewhere.wso2.identity.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RealmConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RealmConfiguration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addAdmin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adminPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adminRoleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adminUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="authorizationManagerClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="authzProperties" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="everyOneRoleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="multipleCredentialProps" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="passwordsExternallyManaged" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="persistedTimestamp" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="primary" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="realmClassName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="realmProperties" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="secondaryRealmConfig" type="{http://api.user.carbon.wso2.org/xsd}RealmConfiguration" minOccurs="0"/>
 *         &lt;element name="tenantId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="userStoreClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userStoreProperties" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealmConfiguration", namespace = "http://api.user.carbon.wso2.org/xsd", propOrder = {
    "addAdmin",
    "adminPassword",
    "adminRoleName",
    "adminUserName",
    "authorizationManagerClass",
    "authzProperties",
    "description",
    "everyOneRoleName",
    "multipleCredentialProps",
    "passwordsExternallyManaged",
    "persistedTimestamp",
    "primary",
    "realmClassName",
    "realmProperties",
    "secondaryRealmConfig",
    "tenantId",
    "userStoreClass",
    "userStoreProperties"
})
public class RealmConfiguration {

    @XmlElementRef(name = "addAdmin", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> addAdmin;
    @XmlElementRef(name = "adminPassword", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> adminPassword;
    @XmlElementRef(name = "adminRoleName", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> adminRoleName;
    @XmlElementRef(name = "adminUserName", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> adminUserName;
    @XmlElementRef(name = "authorizationManagerClass", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> authorizationManagerClass;
    @XmlElement(nillable = true)
    protected List<String> authzProperties;
    @XmlElementRef(name = "description", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> description;
    @XmlElementRef(name = "everyOneRoleName", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> everyOneRoleName;
    @XmlElement(nillable = true)
    protected List<String> multipleCredentialProps;
    protected Boolean passwordsExternallyManaged;
    @XmlElementRef(name = "persistedTimestamp", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> persistedTimestamp;
    protected Boolean primary;
    @XmlElementRef(name = "realmClassName", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> realmClassName;
    @XmlElement(nillable = true)
    protected List<String> realmProperties;
    @XmlElementRef(name = "secondaryRealmConfig", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<RealmConfiguration> secondaryRealmConfig;
    protected Integer tenantId;
    @XmlElementRef(name = "userStoreClass", namespace = "http://api.user.carbon.wso2.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> userStoreClass;
    @XmlElement(nillable = true)
    protected List<String> userStoreProperties;

    /**
     * Gets the value of the addAdmin property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAddAdmin() {
        return addAdmin;
    }

    /**
     * Sets the value of the addAdmin property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAddAdmin(JAXBElement<String> value) {
        this.addAdmin = ((JAXBElement<String> ) value);
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
     * Gets the value of the adminRoleName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdminRoleName() {
        return adminRoleName;
    }

    /**
     * Sets the value of the adminRoleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdminRoleName(JAXBElement<String> value) {
        this.adminRoleName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the adminUserName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdminUserName() {
        return adminUserName;
    }

    /**
     * Sets the value of the adminUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdminUserName(JAXBElement<String> value) {
        this.adminUserName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the authorizationManagerClass property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAuthorizationManagerClass() {
        return authorizationManagerClass;
    }

    /**
     * Sets the value of the authorizationManagerClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAuthorizationManagerClass(JAXBElement<String> value) {
        this.authorizationManagerClass = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the authzProperties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authzProperties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthzProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAuthzProperties() {
        if (authzProperties == null) {
            authzProperties = new ArrayList<String>();
        }
        return this.authzProperties;
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
     * Gets the value of the everyOneRoleName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEveryOneRoleName() {
        return everyOneRoleName;
    }

    /**
     * Sets the value of the everyOneRoleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEveryOneRoleName(JAXBElement<String> value) {
        this.everyOneRoleName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the multipleCredentialProps property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the multipleCredentialProps property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMultipleCredentialProps().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMultipleCredentialProps() {
        if (multipleCredentialProps == null) {
            multipleCredentialProps = new ArrayList<String>();
        }
        return this.multipleCredentialProps;
    }

    /**
     * Gets the value of the passwordsExternallyManaged property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPasswordsExternallyManaged() {
        return passwordsExternallyManaged;
    }

    /**
     * Sets the value of the passwordsExternallyManaged property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPasswordsExternallyManaged(Boolean value) {
        this.passwordsExternallyManaged = value;
    }

    /**
     * Gets the value of the persistedTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getPersistedTimestamp() {
        return persistedTimestamp;
    }

    /**
     * Sets the value of the persistedTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setPersistedTimestamp(JAXBElement<XMLGregorianCalendar> value) {
        this.persistedTimestamp = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the primary property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrimary() {
        return primary;
    }

    /**
     * Sets the value of the primary property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrimary(Boolean value) {
        this.primary = value;
    }

    /**
     * Gets the value of the realmClassName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRealmClassName() {
        return realmClassName;
    }

    /**
     * Sets the value of the realmClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRealmClassName(JAXBElement<String> value) {
        this.realmClassName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the realmProperties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the realmProperties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRealmProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRealmProperties() {
        if (realmProperties == null) {
            realmProperties = new ArrayList<String>();
        }
        return this.realmProperties;
    }

    /**
     * Gets the value of the secondaryRealmConfig property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RealmConfiguration }{@code >}
     *     
     */
    public JAXBElement<RealmConfiguration> getSecondaryRealmConfig() {
        return secondaryRealmConfig;
    }

    /**
     * Sets the value of the secondaryRealmConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RealmConfiguration }{@code >}
     *     
     */
    public void setSecondaryRealmConfig(JAXBElement<RealmConfiguration> value) {
        this.secondaryRealmConfig = ((JAXBElement<RealmConfiguration> ) value);
    }

    /**
     * Gets the value of the tenantId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTenantId() {
        return tenantId;
    }

    /**
     * Sets the value of the tenantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTenantId(Integer value) {
        this.tenantId = value;
    }

    /**
     * Gets the value of the userStoreClass property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUserStoreClass() {
        return userStoreClass;
    }

    /**
     * Sets the value of the userStoreClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUserStoreClass(JAXBElement<String> value) {
        this.userStoreClass = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the userStoreProperties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userStoreProperties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserStoreProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUserStoreProperties() {
        if (userStoreProperties == null) {
            userStoreProperties = new ArrayList<String>();
        }
        return this.userStoreProperties;
    }

}
