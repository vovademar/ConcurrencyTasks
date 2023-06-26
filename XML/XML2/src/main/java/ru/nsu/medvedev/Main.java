package ru.nsu.medvedev;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.xml.sax.SAXException;
import ru.nsu.medvedev.people.Person;
import ru.nsu.medvedev.schema.People;
import ru.nsu.medvedev.schema.PersonGenderType;
import ru.nsu.medvedev.schema.PersonReferenceType;
import ru.nsu.medvedev.schema.PersonType;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;



public class Main {
    private static final Map<String, Person> personById = new HashMap<>();
    private static final Map<String, Person> correctPersonById = new HashMap<>();
    private static final Map<String, Set<String>> idsByName = new HashMap<>();
    private static final Map<String, Set<Person>> personsWithoutID = new HashMap<>();
    private static XMLEventReader reader;

    public static void main(String[] args) {
        String fileName = "people.xml";
        List<Person> persons = parseXMLFile(fileName);
        persons = combinePeople(persons);
        System.out.println("Checking consistency");
        List<Person> personsWithProblems = persons.stream().filter(p ->
                !p.checkConsistency(personById)).toList();
        for (Person problemPerson : personsWithProblems) {
            Set<Person> personsNoId = new HashSet<>();
            for (final Set<Person> personSet : personsWithoutID.values()) {
                for (Person personNoId : personSet)
                    if (personNoId.getFullName().equals(problemPerson.getFullName())) {
                        personsNoId.add(personNoId);
                    }
            }
            normalize(problemPerson, personsNoId);
        }

        for (final Person person : personById.values()) {
            if (person.getSpouse() == null || person.getChildrenNumber() == null) {
                person.setChildrenNumber(0);
            }
        }

        for (final Person person : persons) {
            final String id = person.getId();
            correctPersonById.put(id, person);
        }

        writeToXML(correctPersonById);
    }

    private static List<Person> combinePeople(List<Person> persons) {
        System.out.println("===Combining people===");
        for (final Person person : persons) {
            final String id = person.getId();
            if (id == null) {
                Set<Person> personsWithNoId = personsWithoutID.getOrDefault(person.getFullName(), new HashSet<>());
                personsWithNoId.add(person);
                personsWithoutID.put(person.getFullName(), personsWithNoId);
                continue;
            }

            if (personById.containsKey(id)) {
                Person collision = personById.get(id);
                person.mergePerson(collision);
            }
            personById.put(id, person);
        }

        for (final Person person : personById.values()) {
            final String name = person.getFullName();
            if (person.getId() == null || name == null) {
                throw new RuntimeException("Person must have ID and name");
            }

            Set<String> ids = idsByName.getOrDefault(name, new HashSet<>());
            ids.add(person.getId());
            idsByName.put(name, ids);
        }

        for (final Person person : persons) {
            final String name = person.getFullName();
            if (name == null) continue;

            final Set<String> ids = idsByName.get(name);
            final String id = ids.size() == 1 ?
                    ids.iterator().next() : null;

            if (id != null) {
                person.setId(id);
                personById.get(id).mergePerson(person);
            }
        }

        for (final Person person : personById.values()) {
            if (person.getSpouse() != null) {
                final Set<Person> spouseToAdd = new HashSet<>();
                connectRelatives(spouseToAdd, person.getSpouse());

                if (!spouseToAdd.isEmpty()) {
                    final Person spouse = spouseToAdd.iterator().next();
                    person.setSpouse(spouse);
                }
            }

            Set<Person> childrenToAdd = new HashSet<>();
            for (final Person child : person.getChildren()) {
                connectRelatives(childrenToAdd, child);
            }
            person.getChildren().clear();
            childrenToAdd.forEach(person::addChild);

            Set<Person> siblingsToAdd = new HashSet<>();
            for (final Person sibling : person.getSiblings()) {
                connectRelatives(siblingsToAdd, sibling);
            }
            person.getSiblings().clear();
            siblingsToAdd.forEach(person::addSibling);

            Set<Person> parentsToAdd = new HashSet<>();
            for (final Person parent : person.getParents()) {
                connectRelatives(parentsToAdd, parent);
            }
            person.getParents().clear();
            parentsToAdd.forEach(person::addParent);
        }

        return personById.values().stream()
                .sorted(Comparator.comparing(Person::getId)).toList();
    }

    private static List<Person> parseXMLFile(String fileName) {
        List<Person> peopleList = new ArrayList<>();
        Person person = null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            reader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));
            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "person" -> {
                            person = new Person();
                            Attribute idAttr = startElement.getAttributeByName(new QName("id"));
                            if (idAttr != null) {
                                person.setId(idAttr.getValue().trim());
                            }
                            Attribute fullnameAttr = startElement.getAttributeByName(new QName("name"));
                            if (fullnameAttr != null) {
                                person.setFullName(fullnameAttr.getValue().trim());
                            }
                        }
                        case "id" -> {
                            assert person != null;
                            person.setId(getValue(startElement));
                        }
                        case "first", "firstname" -> {
                            assert person != null;
                            person.setFirstName(getValue(startElement));
                        }
                        case "surname", "family", "family-name" -> {
                            assert person != null;
                            person.setLastName(getValue(startElement));
                        }
                        case "children-number" -> {
                            assert person != null;
                            person.setChildrenNumber(Integer.parseInt(Objects.requireNonNull(getValue(startElement))));
                        }
                        case "siblings-number" -> {
                            assert person != null;
                            person.setSiblingsNumber(Integer.parseInt(Objects.requireNonNull(getValue(startElement))));
                        }
                        case "gender" -> {
                            assert person != null;
                            person.setGender(getGender(startElement));
                        }
                        case "siblings" -> {
                            String[] siblingsValue = getSiblingsValue(startElement);
                            if (siblingsValue != null) {
                                for (String id : siblingsValue) {
                                    assert person != null;
                                    person.addSibling(id);
                                }
                            }
                        }
                        case "brother" -> {
                            String name = getValue(startElement);
                            assert person != null;
                            person.addBrotbers(name, "male");
                        }
                        case "sister" -> {
                            String name = getValue(startElement);
                            assert person != null;
                            person.addSisters(name, "female");
                        }
                        case "son" -> {
                            String id = getValue(startElement);
                            assert person != null;
                            person.addChild(id, "male");
                        }
                        case "daughter" -> {
                            String id = getValue(startElement);
                            assert person != null;
                            person.addChild(id, "female");
                        }
                        case "child" -> {
                            String name = getValue(startElement);
                            assert person != null;
                            person.addChild(name);
                        }
                        case "parent" -> {
                            String id = getValue(startElement);
                            if (id != null) {
                                assert person != null;
                                person.addParent(id);
                            }
                        }
                        case "mother" -> {
                            String fullname = getValue(startElement);
                            assert person != null;
                            person.addParent(fullname, "female");
                        }
                        case "father" -> {
                            String fullname = getValue(startElement);
                            assert person != null;
                            person.addParent(fullname, "male");
                        }

                        case "children", "fullname" -> {
                        }

                        case "spouce" -> {
                            String fullname = getValue(startElement);
                            if (fullname != null) {
                                assert person != null;
                                person.setSpouse(fullname);
                            }
                        }

                        case "wife" -> {
                            String id = getValue(startElement);
                            assert person != null;
                            person.setWife(id);
                        }
                        case "husband" -> {
                            String id = getValue(startElement);
                            assert person != null;
                            person.setHusband(id);
                        }
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("person")) {
                        peopleList.add(person);

                        assert person != null;
                        peopleList.addAll(person.getChildren());
                        peopleList.addAll(person.getParents());
                        peopleList.addAll(person.getSiblings());
                        if (person.getSpouse() != null)
                            peopleList.add(person.getSpouse());
                    }
                }
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return peopleList;
    }

    private static String[] getSiblingsValue(final StartElement event) {
        Iterator<Attribute> iterator = event.getAttributes();
        if (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            return attribute.getValue().trim().split(" ");
        }
        return null;
    }

    private static String getValue(final StartElement event) throws XMLStreamException {
        String value;
        Iterator<Attribute> iterator = event.getAttributes();
        if (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            value = attribute.getValue().trim();
        } else {
            XMLEvent charsEvent = reader.nextEvent();
            if (charsEvent.isEndElement()) {
                return null;
            }
            reader.nextEvent();
            value = charsEvent.asCharacters().getData().trim();
        }

        if (value.equals("NONE") || value.equals("UNKNOWN")) {
            return null;
        } else {
            return value;
        }
    }

    private static String parseGender(String gender) throws Exception {
        return switch (gender) {
            case "male", "M" -> "male";
            case "female", "F" -> "female";
            default -> throw new Exception();
        };
    }

    private static String getGender(final StartElement event) throws Exception {
        Iterator<Attribute> iterator = event.getAttributes();
        if (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            return parseGender(attribute.getValue().trim());
        } else {
            XMLEvent charsEvent = reader.nextEvent();
            reader.nextEvent();
            String gender = charsEvent.asCharacters().getData().trim();
            return parseGender(gender);
        }
    }

    private static void connectRelatives(final Set<Person> peopleToAdd, final Person person) {
        if (person.getId() != null) {
            final String id = person.getId();
            peopleToAdd.add(personById.get(id));
        } else if (person.getFullName() != null) {
            final String name = person.getFullName();
            final Set<String> ids = idsByName.get(name);

            if (ids.size() == 1) {
                final String id = ids.iterator().next();
                peopleToAdd.add(personById.get(id));
            }
        }
    }

    private static void normalize(Person person, Set<Person> others) {
        if (person.getGender() == null) {
            for (Person other : others) {
                if (other.getSpouse() != null && Objects.equals(person.getId(), other.getSpouse().getId())) {
                    if (Objects.equals(other.getSpouse().getGender(), "male"))
                        person.setGender("male");
                    else if (Objects.equals(other.getSpouse().getGender(), "female"))
                        person.setGender("female");
                }
            }
        }

        if (person.getGender() == null) {
            for (Person other : others) {
                for (Person otherSibling : other.getSiblings()) {
                    for (Person personSibling : person.getSiblings()) {
                        if (personSibling.getId().equals(otherSibling.getId())) {
                            if (other.getGender() != null) {
                                person.setGender(other.getGender());
                            }
                        }
                    }
                }
            }
        }

        if (person.getGender() == null) {
            int femaleCount = 0;
            int maleCount = 0;
            for (Person other : others) {
                if (other.getGender() != null && other.getGender().equals("male"))
                    maleCount++;
                if (other.getGender() != null && other.getGender().equals("female"))
                    femaleCount++;
            }

            if (femaleCount == 0 && maleCount > 0)
                person.setGender("male");
            if (maleCount == 0 && femaleCount > 0)
                person.setGender("female");
        }

        if (person.getSpouse() == null) {
            for (Person other : others) {
                if (other.getSpouse() != null && Objects.equals(person.getId(), other.getSpouse().getId())) {
                    person.setSpouse(other);
                    if (person.getSpouse().getId() == null) {
                        for (Person innerOther : others) {
                            if (person.getSpouse() != null &&
                                    person.getSpouse().getFullName().equals(innerOther.getFullName()) &&
                                    innerOther.getSpouse() != null &&
                                    innerOther.getSpouse().getId() != null &&
                                    !Objects.equals(innerOther.getSpouse().getId(), person.getId())) {
                                person.setSpouse(personById.get(innerOther.getSpouse().getId()));
                            }
                        }
                    }
                    break;
                }
            }
        }

        if (person.getSpouse() != null && person.getSpouse().getId() == null) {
            Set<String> spouses = idsByName.get(person.getSpouse().getFullName());
            for (String spouse : spouses) {
                for (Person possibleChild : personById.get(spouse).getChildren()) {
                    if (person.getChildren().contains(possibleChild)) {
                        person.resetSpouce();
                        person.setSpouse(personById.get(spouse));
                        break;
                    }
                }
                if (person.getSpouse().getId() != null) {
                    break;
                }
            }
        }

        if (person.getSpouse() != null && person.getSpouse().getGender() == null) {
            if ((Objects.equals(person.getGender(), "male"))) {
                person.getSpouse().setGender("female");
            } else if ((Objects.equals(person.getGender(), "female"))) {
                person.getSpouse().setGender("male");
            }
        }

        if (person.getChildrenNumber() == null || person.getChildrenNumber() > person.getChildren().size()) {
            if (person.getSpouse() != null) {
                for (Person spouseChild : person.getSpouse().getChildren()) {
                    person.getChildren().add(spouseChild);
                    spouseChild.addParent(person);
                }
            }
        }

        if (person.getSpouse() != null && person.getSpouse().getId() == null) {
            if (person.getSpouse().getSiblingsNumber() != null && person.getSpouse().getSiblingsNumber() == 1) {
                Set<String> spouseSiblings = idsByName.get(person.getSpouse().getFullName());
                if (spouseSiblings.size() == 2) {
                    for (String sibling : spouseSiblings) {
                        if (Objects.equals(person.getId(), sibling))
                            continue;
                        if (personById.get(sibling).getId() != null) {
                            person.setSpouse(personById.get(sibling));
                            person.getSpouse().setSpouse(personById.get(person.getId()));
                        }
                    }
                }
            }
        }
    }

    public static void writeToXML(Map<String, Person> personsByID) {
        System.out.println("===Saving to XML===");
        People people = new People();
        for (Person personFromId : personsByID.values()) {
            PersonType person = new PersonType();
            person.setId(personFromId.getId());
            person.setFirstname(personFromId.getFirstName());
            person.setLastname(personFromId.getLastName());
            if (Objects.equals(personFromId.getGender(), "male"))
                person.setGender(PersonGenderType.MALE);
            else if (Objects.equals(personFromId.getGender(), "female"))
                person.setGender(PersonGenderType.FEMALE);
            else {
                throw new IllegalStateException("No gender");
            }
            people.getPerson().add(person);
        }

        for (PersonType person : people.getPerson()) {
            Person personFromId = personsByID.get(person.getId());
            if (personFromId.getSpouse() != null) {
                PersonReferenceType spouse = new PersonReferenceType();
                if (personFromId.getSpouse().getId() != null) {
                    List<PersonType> p = people.getPerson().parallelStream().filter(x -> x.getId().equals(personFromId.getSpouse().getId())).toList();
                    if (p.size() == 1) {
                        spouse.setPersonId(p.get(0));
                    } else
                        throw new IllegalStateException("Spouse not found");
                } else
                    throw new IllegalStateException("Empty spouse");
                person.setSpouse(spouse);
            }
            if (personFromId.getParents().size() != 0) {
                for (Person parentId : personFromId.getParents()) {
                    List<PersonType> p = people.getPerson().parallelStream().filter(x -> x.getId().equals(parentId.getId())).toList();
                    if (p.size() == 1) {
                        var relRef = new PersonReferenceType();
                        PersonType checkPerson = p.get(0);
                        relRef.setPersonId(checkPerson);
                        if (checkPerson.getGender().equals(PersonGenderType.FEMALE))
                            person.setMother(relRef);
                        else person.setFather(relRef);
                    } else {
                        System.out.println(parentId);
                        throw new IllegalStateException("Parent not found");
                    }
                }
            }

            if (personFromId.getSiblings().size() != 0) {
                for (Person relId : personFromId.getSiblings()) {
                    List<PersonType> p = people.getPerson().parallelStream().filter(x -> x.getId().equals(relId.getId())).toList();
                    if (p.size() == 1) {
                        var relRef = new PersonReferenceType();
                        PersonType pers = p.get(0);
                        relRef.setPersonId(pers);
                        if (pers.getGender().equals(PersonGenderType.FEMALE))
                            person.getSister().add(relRef);
                        else person.getBrother().add(relRef);
                    } else {
                        System.out.println(relId);
                        throw new IllegalStateException("Sibling not found");
                    }
                }
            }


            if (personFromId.getChildren().size() != 0) {
                for (Person relId : personFromId.getChildren()) {
                    List<PersonType> p = people.getPerson().parallelStream().filter(x -> x.getId().equals(relId.getId())).toList();
                    if (p.size() == 1) {
                        var relRef = new PersonReferenceType();
                        PersonType pers = p.get(0);
                        relRef.setPersonId(pers);
                        if (pers.getGender().equals(PersonGenderType.FEMALE))
                            person.getDaughter().add(relRef);
                        else person.getSon().add(relRef);
                    } else {
                        System.out.println(relId);
                        throw new IllegalStateException("Child not found");
                    }
                }

            }
        }

        try {
            ClassLoader classLoader = People.class.getClassLoader();
            jakarta.xml.bind.JAXBContext jc = JAXBContext.newInstance("ru.nsu.medvedev.schema", classLoader);
            jakarta.xml.bind.Marshaller writer = jc.createMarshaller();
            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File schemaFile = new File("schema.xsd");
            writer.setSchema(schemaFactory.newSchema(schemaFile));
            writer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            writer.marshal(people, new File("output.xml"));
        } catch (JAXBException | SAXException e) {
            e.printStackTrace();
        }
    }
}