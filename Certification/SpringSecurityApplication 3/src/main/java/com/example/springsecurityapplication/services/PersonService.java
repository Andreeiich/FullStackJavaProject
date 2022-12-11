package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {
    private final PersonRepository personRepository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Person getPersonFindByLogin(Person person){
        Optional<Person> person_db = personRepository.findByLogin(person.getLogin());
        return person_db.orElse(null);

    }

    public Person getPersonFindById(int id){
        Optional<Person> person_db = personRepository.findById(id);
        return person_db.orElse(null);
    }

    @Transactional
    public void register(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        personRepository.save(person);
    }

    @Transactional
    public List<Person> getAllUsers(){
        return personRepository.findAll();
    }

    @Transactional
    public  List<Person> getUsersExceptMe(int id){
      List<Person> person_db=personRepository.getAllExceptMe(id);
        return person_db;

    }
    @Transactional
    public  Person getUsersByLogin(String login){
        Optional<Person> person_db = personRepository.findByLogin(login);
        return person_db.orElse(null);

    }

    @Transactional
    public  void changePassword(int id,String password){
      personRepository.updatePersonById(id,passwordEncoder.encode(password));
    }

    @Transactional
    public  void deleteById(int id){
        personRepository.deleteById(id);
    }

    @Transactional
    public  void updatePersonById(int id,String login,String role){
        personRepository.updatePersonById(login,role,id);
    }


    public boolean checkOrders(int id){
        boolean order=false;

        if(personRepository.checkOrdersByIdPerson(id).isPresent()){
            order=true;
        }
        return  order;
    }



    }
