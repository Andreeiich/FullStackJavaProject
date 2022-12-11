package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.enumm.Status;
import com.example.springsecurityapplication.models.Image;
import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.CategoryRepository;
import com.example.springsecurityapplication.repositories.OrderRepository;
import com.example.springsecurityapplication.repositories.PersonRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.OrderService;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.services.ProductService;
import com.example.springsecurityapplication.util.PersonValidator;
import com.example.springsecurityapplication.util.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
public class AdminController {

    @Value("${upload.path}")
    private String uploadPath;
    private final PersonValidator personValidator;
    private final PersonService personService;
    private final ProductValidator productValidator;
    private final ProductService productService;

    private final CategoryRepository categoryRepository;
    private final PersonRepository personRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Autowired
    public AdminController(PersonValidator personValidator, PersonService personService, ProductValidator productValidator, ProductService productService, CategoryRepository categoryRepository, PersonRepository personRepository, OrderRepository orderRepository, OrderService orderService) {
        this.personValidator = personValidator;
        this.personService = personService;
        this.productValidator = productValidator;
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.personRepository = personRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') and hasRole('')")
//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('')")

    // Метод по отображению главной страницы администратора с выводом товаров
    @GetMapping()
    public String admin(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        String role = personDetails.getPerson().getRole();

        if(role.equals("ROLE_USER")){
            return "redirect:/index";
        }else if (role.equals("ROLE_SELLER")) {
            return "redirect:/seller";
        }
        try {
            model.addAttribute("products", productService.getAllProduct());
            return "admin/admin";
        }catch (NullPointerException e){
            return "admin/admin";
        }

    }

    @GetMapping("/users")
    public String UsersWatch(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id =personDetails.getPerson().getId();
        model.addAttribute("person",personService.getUsersExceptMe(id));

        //model.addAttribute("person",personService.getAllUsers());

        return "admin/users";
    }

    @GetMapping("/person/infos/{id}")
    public String infoUser(@PathVariable("id") int id, Model model){
        model.addAttribute("person", personService.getPersonFindById(id));
        return "admin/infoUser";
    }

    @GetMapping("/addUser")
    public String addUsers(Model model){
        model.addAttribute("person", new Person());

        return "admin/addUser";
    }

    @PostMapping("/addUser")
    public String addUsers(@ModelAttribute("person") @Valid Person person,BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors()){
            return "registration";
        }
        personService.register(person);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int id){
        ////
       /* Person person = personService.getPersonFindById(id);
       personValidator.checkOrdersOfUser(person,bindingResult);
       if(bindingResult.hasErrors()){
         return "/users/delete/{id}";
       }
        ////*/
        personService.deleteById(id);
        return "redirect:/admin/users";
    }
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") int id,Model model){
        model.addAttribute("person",personService.getPersonFindById(id));

        return "admin/editUser";
    }

    @PostMapping("/users/edit/{id}")
    public String editUser(@ModelAttribute("person") Person person,BindingResult bindingResult,@PathVariable("id") int id,
                           @RequestParam(value = "ROLE", required = false, defaultValue = "") String ROLE,Model model){
        personValidator.validate(person,bindingResult);
        if(bindingResult.hasErrors()){
            return "admin/editUser";
        }
        person.setRole(ROLE);
        personService.updatePersonById(person.getId(),person.getLogin(),person.getRole());
        return "redirect:/admin";
    }

    @GetMapping("/users/editRole/{id}")
    public String editUserRole(@PathVariable("id") int id,Model model){
        model.addAttribute("person",personService.getPersonFindById(id));

        return "admin/editRoleUser";
    }
    @PostMapping("/users/editRole/{id}")
    public String editUser(@PathVariable("id") int id,
                           @RequestParam(value = "ROLE", required = false, defaultValue = "") String ROLE){
        Person per=personService.getPersonFindById(id);
        personService.updatePersonById(per.getId(),per.getLogin(),ROLE);
        return "redirect:/admin/users";
    }


    // Метод по отображению формы добавление
    @GetMapping("/product/add")
    public String addProduct(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
//        System.out.println(categoryRepository.findAll().size());
        return "product/addProduct";
    }

    // Метод по добавлению объекта с формы в таблицу product
    @PostMapping("/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult,
                             @RequestParam("file_one") MultipartFile file_one, @RequestParam("file_two") MultipartFile file_two,
                             @RequestParam("file_three") MultipartFile file_three, @RequestParam("file_four") MultipartFile file_four,
                             @RequestParam("file_five") MultipartFile file_five,Model model) throws IOException {

        productValidator.validate(product, bindingResult);
        if(bindingResult.hasErrors()){
            model.addAttribute("category",categoryRepository.findAll());
            return "product/addProduct";
        }


        // Проверка на пустоту файла
        if(file_one != null){
            // Дирректория по сохранению файла
            File uploadDir = new File(uploadPath);
            // Если данной дирректории по пути не сущетсвует
            if(!uploadDir.exists()){
                // Создаем данную дирректорию
                uploadDir.mkdir();
            }

            if(file_one.getOriginalFilename().length()==0){
                model.addAttribute("category",categoryRepository.findAll());
                productValidator.validateImage(product,bindingResult);
                return "product/addProduct";
            }

            // Создаем уникальное имя файла
            // UUID представляет неищменный универсальный уникальный идентификатор
            String uuidFile = UUID.randomUUID().toString();
            // file_one.getOriginalFilename() - наименование файла с формы
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            // Загружаем файл по указаннопу пути
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageProduct(image);
        }

        if(file_two.getOriginalFilename().length()!=0){
            // Проверка на пустоту файла
            if(file_two != null){
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if(!uploadDir.exists()){
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }
                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_two.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);
                product.addImageProduct(image);
            }
        }

        if(file_three.getOriginalFilename().length()!=0){
            // Проверка на пустоту файла
            if(file_three != null){
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if(!uploadDir.exists()){
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }
                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_three.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_three.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);
                product.addImageProduct(image);
            }}

        if(file_four.getOriginalFilename().length()!=0){
            // Проверка на пустоту файла

            if(file_four != null){
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if(!uploadDir.exists()){
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }
                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_four.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_four.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);
                product.addImageProduct(image);
            }}

        if(file_five.getOriginalFilename().length()!=0) {
            // Проверка на пустоту файла
            if (file_five != null) {
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if (!uploadDir.exists()) {
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }
                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_five.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_five.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);
                product.addImageProduct(image);
            }
        }
        productService.saveProduct(product);
        return "redirect:/admin";
    }

    // Метод по удалению товара по id
    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id){
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    // Метод по получению товара по id и отображение шаблона редактирования
    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("editProduct", productService.getProductId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";
    }

    @PostMapping("/product/edit/{id}")
    public String editProduct(@ModelAttribute("editProduct") Product product,BindingResult bindingResult, @PathVariable("id") int id,
                              @RequestParam("file_one") MultipartFile file_one, @RequestParam("file_two") MultipartFile file_two,
                              @RequestParam("file_three") MultipartFile file_three, @RequestParam("file_four") MultipartFile file_four,
                              @RequestParam("file_five") MultipartFile file_five,
                              @RequestParam(value = "second", required = false, defaultValue = "") String second, @RequestParam(value = "third", required = false, defaultValue = "") String third,
                              @RequestParam(value = "fourth", required = false, defaultValue = "") String fourth, @RequestParam(value = "fifth", required = false, defaultValue = "") String fifth,
                              Model model) throws IOException {
        productValidator.validateEdit(product, bindingResult);
        if(bindingResult.hasErrors()){
            model.addAttribute("category",categoryRepository.findAll());
            return "product/editProduct";
        }

        Product pr = productService.getProductId(id);
        pr.setTitle(product.getTitle());
        pr.setDescription(product.getDescription());
        pr.setPrice(product.getPrice());
        pr.setWarehouse(product.getWarehouse());
        pr.setCategory(product.getCategory());
        pr.setSeller(product.getSeller());
        //pr.setImageList(product.getImageList());

        if(file_one.getOriginalFilename().length()!=0){
            // Проверка на пустоту файла
            if(file_one != null){
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if(!uploadDir.exists()){
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }

                if(file_one.getOriginalFilename().length()==0){
                    model.addAttribute("category",categoryRepository.findAll());
                    productValidator.validateImage(product,bindingResult);
                    return "product/addProduct";
                }

                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_one.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(pr);
                image.setFileName(resultFileName);
                pr.ChangeImageProduct(image,0);


                //product.addImageProduct(image);
            }
        }

        if(file_two.getOriginalFilename().length()!=0){
            // Проверка на пустоту файла
            if(file_two != null){
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if(!uploadDir.exists()){
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }
                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_two.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);


                if(pr.getImageList().size()<=1){
                    pr.addImageProduct(image);
                }else {
                    pr.ChangeImageProduct(image,1);
                }

            }
        }

        if(file_three.getOriginalFilename().length()!=0){
            // Проверка на пустоту файла
            if(file_three != null){
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if(!uploadDir.exists()){
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }
                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_three.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_three.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);
                //product.addImageProduct(image);


                if(pr.getImageList().size()<=2){
                    pr.addImageProduct(image);
                }else {
                    pr.ChangeImageProduct(image,2);
                }

            }}

        if(file_four.getOriginalFilename().length()!=0){
            // Проверка на пустоту файла

            if(file_four != null){
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if(!uploadDir.exists()){
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }
                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_four.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_four.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);

                //product.addImageProduct(image);
                if(pr.getImageList().size()<=3){
                    pr.addImageProduct(image);
                }else {
                    pr.ChangeImageProduct(image,3);
                }

            }}

        if(file_five.getOriginalFilename().length()!=0) {
            // Проверка на пустоту файла
            if (file_five != null) {
                // Дирректория по сохранению файла
                File uploadDir = new File(uploadPath);
                // Если данной дирректории по пути не сущетсвует
                if (!uploadDir.exists()) {
                    // Создаем данную дирректорию
                    uploadDir.mkdir();
                }
                // Создаем уникальное имя файла
                // UUID представляет неищменный универсальный уникальный идентификатор
                String uuidFile = UUID.randomUUID().toString();
                // file_one.getOriginalFilename() - наименование файла с формы
                String resultFileName = uuidFile + "." + file_five.getOriginalFilename();
                // Загружаем файл по указаннопу пути
                file_five.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);
                //product.addImageProduct(image);

                if(pr.getImageList().size()<=4){
                    pr.addImageProduct(image);
                }else {
                    pr.ChangeImageProduct(image,4);
                }
            }
        }

        //удаление фото по выбранному номеру
        int index_for_delete=0;
        if(fifth.length()!=0) {
            int photo = Integer.parseInt(fifth);
            //photo -= index_for_delete;
            index_for_delete++;
            if (pr.getImageList().size() >= 5) {
                productService.updateProductWithImageDelete(pr, pr.getImageList(), photo);
            }
        }
        if(fourth.length()!=0){
            int photo=Integer.parseInt(fourth);
            //photo-=index_for_delete;
            index_for_delete++;
            if(pr.getImageList().size()>=4) {
                productService.updateProductWithImageDelete(pr, pr.getImageList(), photo);
            }
        }
        if(third.length()!=0){
            int photo=Integer.parseInt(third);
            //photo-=index_for_delete;
            index_for_delete++;
            if(pr.getImageList().size()>=3) {
                productService.updateProductWithImageDelete(pr, pr.getImageList(), photo);
            }
        }
        if(second.length()!=0){
            int photo=Integer.parseInt(second);
            //photo-=index_for_delete;
            index_for_delete++;
            if(pr.getImageList().size()>=2){
                productService.updateProductWithImageDelete(pr,pr.getImageList(),photo);
            }
        }







        // productService.updateProduct(id, pr);
        productService.updateProductWithImage(id,pr,pr.getImageList());
        return "redirect:/admin";
    }

    @GetMapping("/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductId(id));
        return "product/infoProduct";
    }



    @GetMapping("/changePassword")
    public String changePassword(Model model){
        model.addAttribute("person", new Person());
        return "admin/changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("person")  Person person, BindingResult bindingResult){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        int id=personDetails.getPerson().getId();


        personValidator.validatePassword(person.getPassword(), bindingResult);
        if(bindingResult.hasErrors()){
            return "admin/changePassword";
        }

        personService.changePassword(id,person.getPassword());

        return "redirect:/admin";
    }


    @GetMapping("/searchUser")
    public String searchUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id =personDetails.getPerson().getId();
        model.addAttribute("person",new Person());
        model.addAttribute("person",personService.getUsersExceptMe(id));
        return "admin/searchUser";
    }

    @PostMapping("/user/search")
    public  String searchUser(@RequestParam("search") String search,Model model){

        // model.addAttribute("value_search",search);
        model.addAttribute("search_user",personRepository.getPersonByPartOfName(search.toLowerCase()));

        return "admin/searchUser";
    }


    @GetMapping("/orders")
    public String allOrder(Model model){
        model.addAttribute("order",orderRepository.findAll());

        return "/admin/orders";
    }


    @GetMapping("/searchOrder")
    public  String searchOrder(){

        return "admin/searchOrder";
    }


    @PostMapping("/order/search")
    public String searchOrder(@RequestParam("search") String search, Model model){

        model.addAttribute("order",orderService.findByLastFourCharacters(search));
        return "admin/searchOrder";
    }


    @GetMapping("/order/edit/{id}")
    public String edithOrder(@PathVariable("id") int id,Model model){
        model.addAttribute("order",orderService.getOrderFindById(id));
        return "/admin/editOrder";
    }

    @PostMapping("/order/edit/{id}")
    public String edithOrder(@PathVariable("id") int id,@RequestParam(value = "status", required = false, defaultValue = "") String status){

        orderService.updateOrderStatus(Integer.parseInt(status),id);

        return "redirect:/admin/orders";
    }




}
