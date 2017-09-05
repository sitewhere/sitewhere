
package com.sitewhere.assetmodule.magento.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogProductLinkAttributeEntityArray complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogProductLinkAttributeEntityArray">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="complexObjectArray" type="{urn:Magento}catalogProductLinkAttributeEntity" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogProductLinkAttributeEntityArray", propOrder = {
    "complexObjectArray"
})
public class CatalogProductLinkAttributeEntityArray {

    protected List<CatalogProductLinkAttributeEntity> complexObjectArray;

    /**
     * Gets the value of the complexObjectArray property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the complexObjectArray property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComplexObjectArray().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CatalogProductLinkAttributeEntity }
     * 
     * 
     */
    public List<CatalogProductLinkAttributeEntity> getComplexObjectArray() {
        if (complexObjectArray == null) {
            complexObjectArray = new ArrayList<CatalogProductLinkAttributeEntity>();
        }
        return this.complexObjectArray;
    }

}
