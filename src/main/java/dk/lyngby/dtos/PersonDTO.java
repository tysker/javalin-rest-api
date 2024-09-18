package dk.lyngby.dtos;

import dk.lyngby.model.Person;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PersonDTO {

    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private int id;

    public PersonDTO(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.age = person.getAge();
        this.email = person.getEmail();
    }

    public PersonDTO(Person person, int id) {
        this.id = id;
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.age = person.getAge();
        this.email = person.getEmail();
    }

    public static List<PersonDTO> toPersonDTOList(List<Person> persons) {
        return List.of(persons.stream().map(PersonDTO::new).toArray(PersonDTO[]::new));
    }

    public Person toPerson() {
        return new Person(firstName, lastName, age, email);
    }

}
