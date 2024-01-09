package dk.lyngby.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Person")
@NamedQuery(name = "Person.deleteAllRows", query = "DELETE from Person")
@Getter
@Setter
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPersonPK", nullable = false)
    private Integer id;

    @Column(name = "firstName", length = 45)
    private String firstName;

    @Column(name = "lastName", length = 45)
    private String lastName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "createDate")
    private LocalDateTime createDate;

    @Column(name = "updateDate")
    private LocalDateTime updateDate;

    @PrePersist
    public void onPrePersist() {
        this.setCreateDate(LocalDateTime.now());
        this.setUpdateDate(LocalDateTime.now());
    }

    @PreUpdate
    public void onPreUpdate() {
        this.setUpdateDate(LocalDateTime.now());
    }

    public Person(String firstName, String lastName, Integer age, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(age, person.age) && Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, age, email);
    }
}