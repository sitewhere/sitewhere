
package com.sitewhere.assetmodule.magento.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductSpecialPriceReturnEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductSpecialPriceReturnEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="special_price" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="special_from_date" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="special_to_date" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductSpecialPriceReturnEntity", propOrder = {
    "specialPrice",
    "specialFromDate",
    "specialToDate"
})
public class CatalogProductSpecialPriceReturnEntity {

    @XmlElement(name = "special_price", required = true)
    protected String specialPrice;
    @XmlElement(name = "special_from_date", required = true)
    protected String specialFromDate;
    @XmlElement(name = "special_to_date", required = true)
    protected String specialToDate;

    /**
     * Gets the value of the specialPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialPrice() {
        return specialPrice;
    }

    /**
     * Sets the value of the specialPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialPrice(String value) {
        this.specialPrice = value;
    }

    /**
     * Gets the value of the specialFromDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialFromDate() {
        return specialFromDate;
    }

    /**
     * Sets the value of the specialFromDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialFromDate(String value) {
        this.specialFromDate = value;
    }

    /**
     * Gets the value of the specialToDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialToDate() {
        return specialToDate;
    }

    /**
     * Sets the value of the specialToDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialToDate(String value) {
        this.specialToDate = value;
    }

}
