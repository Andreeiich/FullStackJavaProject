package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Optional;

// http:localhost:8080/auth/login
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final PersonValidator personValidator;

    private final PersonService personService;

    @Autowired
    public AuthController(PersonValidator personValidator, PersonService personService) {
        this.personValidator = personValidator;
        this.personService = personService;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model){
        model.addAttribute("person", new Person());
        return "registration";
    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors()){
            return "registration";
        }

        personService.register(person);
        // return "redirect:/index";
        return "redirect:/auth/login";
    }

    @GetMapping("/changePasswordALL")
    public String changePassword(Model model){
        model.addAttribute("person", new Person());
        return "changePasswordALL";
    }

    @PostMapping("/changePasswordALL")
    public String changePassword(@ModelAttribute("person")  Person person, BindingResult bindingResult){

        //Ошибка вылидации на пустой логин с формы

        //////изначальный вариант без проверки старого пароля
        personValidator.findUser(person,bindingResult);

       if(bindingResult.hasErrors()){
           return "changePasswordALL";
       }
        Person person_db = personService.getUsersByLogin(person.getCheckLogin());
        int id = person_db.getId();
        personService.changePassword(id,person.getPassword());
       // return "redirect:/index";
        return "redirect:/auth/login";
        ////////////

    }


   /* @GetMapping("/setNewPassword")
    public String setNewPassword(Model model){
        model.addAttribute("person", new Person());
        return "setNewPassword";
    }

    @PostMapping("/setNewPassword")
    public String setNewPassword(@ModelAttribute("person") @Valid Person person,*//*@RequestParam("newPassword") String newPassword,*//* BindingResult bindingResult){

        personValidator.findUser(person,bindingResult);
        Person person_db = personService.getPersonFindByLogin(person);

        if(bindingResult.hasErrors()){
            return "setNewPassword";
        }


        int id = person_db.getId();
        //String password=person.getPassword();

        personService.changePassword(id,person.getPassword());
        return "redirect:/index";


    }*/




}
