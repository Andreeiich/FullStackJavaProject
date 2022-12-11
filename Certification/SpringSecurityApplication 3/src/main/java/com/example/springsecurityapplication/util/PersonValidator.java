package com.example.springsecurityapplication.util;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.PersonRepository;
import com.example.springsecurityapplication.services.PersonService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {
    private final PersonService personService;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;

    public PersonValidator(PersonService personService, PasswordEncoder passwordEncoder, PersonRepository personRepository) {
        this.personService = personService;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
    }

    // В данно методе указываем для какой модели предназначен данный валидатор
    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        // Если метод по поиску пользователя по логину не равен 0 тогда такой логин уже занят
        if(personService.getPersonFindByLogin(person) != null){
            errors.rejectValue("login", "", "Логин занят");
        }
    }

   /* public  void findUser(Object target, Errors errors){
        Person person = (Person) target;
        if(personService.getPersonFindByLogin(person)==null){
            errors.rejectValue("login", "", "Данного пользователя нет в системе");
        }

    }*/

    public  void findUser(Object target, Errors errors){
        Person person = (Person) target;

        //проверка на наличие пользователя в БД с таким логином
        if(/*personService.getPersonFindByLogin(person.getCheckLogin())==null*/personRepository.findByLogin(person.getCheckLogin()).isEmpty()){
            errors.rejectValue("checkLogin", "", "Данного пользователя нет в системе");
        }

        //проверка совпадения введенного дуйствующего пароля с паролем сохраненным в БД
       Optional<Person> pers =personRepository.findByLogin(person.getCheckLogin());
        if(pers.isPresent()){
        person.setLogin(pers.get().getLogin());
        person.setRole(pers.get().getRole());
        person.setId(pers.get().getId());
        person.setOrderList(pers.get().getOrderList());
        person.setProducts(pers.get().getProducts());
        Boolean passwordByCodeBoll=passwordEncoder.matches(person.getOldPassword(),personService.getUsersByLogin(person.getCheckLogin()).getPassword());

        if(!passwordByCodeBoll){
            errors.rejectValue("oldPassword", "", "Действующий пароль не совпадает с введенным");
        }
        }

         //проверка нового пароля на соответствие шаблону
        boolean strMatch =person.getPassword().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$");
        if(!strMatch){
            errors.rejectValue("password", "", "Пароль должен содержать не менее 6 символов, хотя бы одну цифру, спец символ, букву в верхнем и нижнем регистре ");

        }

    }
    public void validatePassword(String password, Errors errors) {
        boolean strMatch =password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$");
        if(!strMatch){
            errors.rejectValue("password", "", "Пароль должен содержать не менее 6 символов, хотя бы одну цифру, спец символ, букву в верхнем и нижнем регистре ");

        }

    }

    public  void  checkOrdersOfUser(Object target,Errors errors){
        Person person = (Person) target;
        int id= person.getId();


        if (personService.checkOrders(id)){
            errors.rejectValue("person", "", "Данный пользователь имеет заказы");
        }

    }


    }






