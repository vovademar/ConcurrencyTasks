
package ru.nsu.medvedev.schema;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for person-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="person-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="spouse" type="{ru.nsu.medvedev}person-reference-type" minOccurs="0"/&gt;
 *         &lt;element name="mother" type="{ru.nsu.medvedev}person-reference-type" minOccurs="0"/&gt;
 *         &lt;element name="father" type="{ru.nsu.medvedev}person-reference-type" minOccurs="0"/&gt;
 *         &lt;element name="sister" type="{ru.nsu.medvedev}person-reference-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="brother" type="{ru.nsu.medvedev}person-reference-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="son" type="{ru.nsu.medvedev}person-reference-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="daughter" type="{ru.nsu.medvedev}person-reference-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="firstname" use="required" type="{ru.nsu.medvedev}person-firstname-type" /&gt;
 *       &lt;attribute name="lastname" use="required" type="{ru.nsu.medvedev}person-surname-type" /&gt;
 *       &lt;attribute name="gender" use="required" type="{ru.nsu.medvedev}person-gender-type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person-type", namespace = "ru.nsu.medvedev", propOrder = {
    "spouse",
    "mother",
    "father",
    "sister",
    "brother",
    "son",
    "daughter"
})
public class PersonType {

    @XmlElement(namespace = "ru.nsu.medvedev")
    protected PersonReferenceType spouse;
    @XmlElement(namespace = "ru.nsu.medvedev")
    protected PersonReferenceType mother;
    @XmlElement(namespace = "ru.nsu.medvedev")
    protected PersonReferenceType father;
    @XmlElement(namespace = "ru.nsu.medvedev")
    protected List<PersonReferenceType> sister;
    @XmlElement(namespace = "ru.nsu.medvedev")
    protected List<PersonReferenceType> brother;
    @XmlElement(namespace = "ru.nsu.medvedev")
    protected List<PersonReferenceType> son;
    @XmlElement(namespace = "ru.nsu.medvedev")
    protected List<PersonReferenceType> daughter;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "firstname", required = true)
    protected String firstname;
    @XmlAttribute(name = "lastname", required = true)
    protected String lastname;
    @XmlAttribute(name = "gender", required = true)
    protected PersonGenderType gender;

    /**
     * Gets the value of the spouse property.
     * 
     * @return
     *     possible object is
     *     {@link PersonReferenceType }
     *     
     */
    public PersonReferenceType getSpouse() {
        return spouse;
    }

    /**
     * Sets the value of the spouse property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonReferenceType }
     *     
     */
    public void setSpouse(PersonReferenceType value) {
        this.spouse = value;
    }

    /**
     * Gets the value of the mother property.
     * 
     * @return
     *     possible object is
     *     {@link PersonReferenceType }
     *     
     */
    public PersonReferenceType getMother() {
        return mother;
    }

    /**
     * Sets the value of the mother property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonReferenceType }
     *     
     */
    public void setMother(PersonReferenceType value) {
        this.mother = value;
    }

    /**
     * Gets the value of the father property.
     * 
     * @return
     *     possible object is
     *     {@link PersonReferenceType }
     *     
     */
    public PersonReferenceType getFather() {
        return father;
    }

    /**
     * Sets the value of the father property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonReferenceType }
     *     
     */
    public void setFather(PersonReferenceType value) {
        this.father = value;
    }

    /**
     * Gets the value of the sister property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the sister property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSister().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonReferenceType }
     * 
     * 
     */
    public List<PersonReferenceType> getSister() {
        if (sister == null) {
            sister = new ArrayList<PersonReferenceType>();
        }
        return this.sister;
    }

    /**
     * Gets the value of the brother property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the brother property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBrother().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonReferenceType }
     * 
     * 
     */
    public List<PersonReferenceType> getBrother() {
        if (brother == null) {
            brother = new ArrayList<PersonReferenceType>();
        }
        return this.brother;
    }

    /**
     * Gets the value of the son property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the son property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSon().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonReferenceType }
     * 
     * 
     */
    public List<PersonReferenceType> getSon() {
        if (son == null) {
            son = new ArrayList<PersonReferenceType>();
        }
        return this.son;
    }

    /**
     * Gets the value of the daughter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the daughter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDaughter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonReferenceType }
     * 
     * 
     */
    public List<PersonReferenceType> getDaughter() {
        if (daughter == null) {
            daughter = new ArrayList<PersonReferenceType>();
        }
        return this.daughter;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the firstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the value of the firstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstname(String value) {
        this.firstname = value;
    }

    /**
     * Gets the value of the lastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the value of the lastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastname(String value) {
        this.lastname = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link PersonGenderType }
     *     
     */
    public PersonGenderType getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonGenderType }
     *     
     */
    public void setGender(PersonGenderType value) {
        this.gender = value;
    }

}
