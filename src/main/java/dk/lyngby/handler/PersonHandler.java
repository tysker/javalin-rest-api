package dk.lyngby.handler;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.daos.PersonDAO;
import dk.lyngby.dtos.PersonDTO;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.Message;
import dk.lyngby.model.Person;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class PersonHandler implements IEntityHandler<PersonDTO, Integer>{

    private final PersonDAO PERSON_DAO;

    public PersonHandler() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        PERSON_DAO = PersonDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) throws ApiException {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        Person person = PERSON_DAO.read(id);
        // dto
        PersonDTO personDTO = new PersonDTO(person);
        // response
        ctx.res().setStatus(200);
        ctx.json(personDTO, PersonDTO.class);
    }

    @Override
    public void readAll(Context ctx) throws ApiException {
        // entity
        List<Person> persons = PERSON_DAO.readAll();
        // dto
        List<PersonDTO> personDTOS = PersonDTO.toPersonDTOList(persons);
        // response
        ctx.res().setStatus(200);
        ctx.json(personDTOS, PersonDTO.class);
    }

    @Override
    public void create(Context ctx) throws ApiException {
        // request
        PersonDTO jsonRequest = validateEntity(ctx);
        // entity
        Person person = PERSON_DAO.create(jsonRequest.toPerson());
        // dto
        PersonDTO personDTO = new PersonDTO(person, person.getId());
        // response
        ctx.res().setStatus(201);
        ctx.json(personDTO, PersonDTO.class);
    }

    @Override
    public void update(Context ctx) throws ApiException {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        Person update = PERSON_DAO.update(id, validateEntity(ctx).toPerson());
        // dto
        PersonDTO personDTO = new PersonDTO(update);
        // response
        ctx.res().setStatus(200);
        ctx.json(personDTO, PersonDTO.class);
    }

    @Override
    public void delete(Context ctx) throws ApiException {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        PERSON_DAO.delete(id);
        // response
        ctx.res().setStatus(200);
        ctx.json(new Message(200, "Person with id " + id + " deleted"), Message.class);
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return PERSON_DAO.validatePrimaryKey(id);
    }

    @Override
    public PersonDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(PersonDTO.class)
                .check(p -> p.getAge() > 0 && p.getAge() < 120, "Age must be between 0 and 120")
                .check(p -> p.getFirstName().length() > 0, "First name must be longer than 0 characters")
                .check(p -> p.getLastName().length() > 0, "Last name must be longer than 0 characters")
                .check(p -> p.getEmail().matches("^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$"), "Email must be valid")
                .get();
    }
}
