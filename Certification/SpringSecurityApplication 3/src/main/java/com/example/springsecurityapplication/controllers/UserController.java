package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.enumm.Status;
import com.example.springsecurityapplication.models.Cart;
import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.CartRepository;
import com.example.springsecurityapplication.repositories.OrderRepository;
import com.example.springsecurityapplication.repositories.ProductRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.services.ProductService;
import com.example.springsecurityapplication.util.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserController {

    private final OrderRepository orderRepository;

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final PersonService personService;
    private final ProductValidator productValidator;

    @Autowired
    public UserController(OrderRepository orderRepository, CartRepository cartRepository, ProductService productService, ProductRepository productRepository, PersonService personService, ProductValidator productValidator) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.productRepository=productRepository;
        this.personService = personService;
        this.productValidator = productValidator;
    }

    @GetMapping("/index")
    public String index(Model model){
        // Получае объект аутентификации - > c помощью SecurityContextHolder обращаемся к контексту и на нем вызываем метод аутентификации. По сути из потока для текущего пользователя мы получаем объект, который был положен в сессию после аутентификации пользователя
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        // Преобразовываем объект аутентификации в специальный объект класса по работе с пользователями
//        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
//        System.out.println("ID пользователя: " + personDetails.getPerson().getId());
//        System.out.println("Логин пользователя: " + personDetails.getPerson().getLogin());
//        System.out.println("Пароль пользователя: " + personDetails.getPerson().getPassword());

        // Получае объект аутентификации - > c помощью SecurityContextHolder обращаемся к контексту и на нем вызываем метод аутентификации. По сути из потока для текущего пользователя мы получаем объект, который был положен в сессию после аутентификации пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Преобразовываем объект аутентификации в специальный объект класса по работе с пользователями
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        String role = personDetails.getPerson().getRole();
        String login = personDetails.getPerson().getLogin();

        if(role.equals("ROLE_ADMIN")){
            return "redirect:/admin";
        } else if (role.equals("ROLE_SELLER")) {
            return "redirect:/seller";
        }
        model.addAttribute("person",personService.getPersonFindByLogin(new Person(personDetails.getPerson().getLogin(),personDetails.getPerson().getPassword())));
        model.addAttribute("products", productService.getAllProduct());
        return "user/index";
    }

    // Добавить товар в корзину
    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model){
        Product product = productService.getProductId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();
        for (Cart cart: cartList) {
            productList.add(productService.getProductId(cart.getProductId()));
        }

        float price = 0;
        for (Product product: productList) {
            price += product.getPrice();
        }

        model.addAttribute("price", price);
        model.addAttribute("cart_product", productList);
        return "user/cart";
    }

    @GetMapping("/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductId(id));
        return "product/infoProduct";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteProductFromCart(Model model, @PathVariable("id") int id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        cartRepository.deleteCartById(id, id_person);
        return "redirect:/cart";
    }

//    @PostMapping("/search")
//    public String productSearch(@RequestParam("search") String search, @RequestParam("ot") String ot, @RequestParam("do") String Do, @RequestParam(value = "price", required = false, defaultValue = "") String price, @RequestParam(value = "category", required = false, defaultValue = "") String category, Model model){
//        System.out.println("lf");
//        return "redirect:/product";
//    }

   /* @GetMapping("/order/create")
    public String createOrder(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();
        for (Cart cart: cartList) {
            productList.add(productService.getProductId(cart.getProductId()));
        }

        String uuid = UUID.randomUUID().toString();

        for (Product product: productList) {
            Order newOrder = new Order(uuid, 1, product.getPrice(), Status.Оформлен, product, personDetails.getPerson());
            orderRepository.save(newOrder);
            cartRepository.deleteCartById(product.getId(), id_person);
        }
        return "redirect:/index/orders";
    }
*/


    @PostMapping("/index/order/create/{id}")
    public String createOrder(@ModelAttribute("product") Product product, BindingResult bindingResult, @PathVariable("id") int id,@RequestParam("counts") int counts){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();
       Optional<Product> product1=productRepository.findById(product.getId());
        productValidator.validateCounts(counts,bindingResult);
        if(bindingResult.hasErrors()){
            return "/user/cart";

        }


        String uuid = UUID.randomUUID().toString();
        Order newOrder = new Order(uuid, counts, counts*product1.get().getPrice(), Status.Оформлен, product, personDetails.getPerson());
        orderRepository.save(newOrder);
        cartRepository.deleteCartById(product.getId(), id_person);

        return "redirect:/index/orders";
    }


    @GetMapping("/index/orders")
    public String ordersUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        model.addAttribute("orders", orderList);
        return "/user/orders";
    }


    @GetMapping("/index/search")
    public String index(){
        return "user/search";
    }



    @PostMapping("/index/search")
    public String productSearchMethod(@RequestParam("search") String search,
                                @RequestParam("ot") String ot,
                                @RequestParam("do") String Do,
                                @RequestParam(value = "price", required = false, defaultValue = "") String price,
                                @RequestParam(value = "category", required = false, defaultValue = "") String category,
                                Model model){
        // Если диапазон цен от и до не пустой
        if(!ot.isEmpty() & !Do.isEmpty()) {

            // Если сортировка по убыванию или возрастанию выбрана
            if (!price.isEmpty()) {
                // Если в качестве сортировки выбрана сортировкам по возрастанию
                if (price.equals("sorted_by_ascending_price")) {
                    // Если категория товара не пустая
                    if (!category.isEmpty()) {
                        // Если категория равная
                        if (category.equals("1:43")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                            // Если категория равная
                        } else if (category.equals("1:18")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                            // Если категория равная
                        } else if (category.equals("1:8")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        }
                        // Если категория не выбрана
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }

                    // Если в качестве сортировки выбрана сортировкам по убыванию
                } else if (price.equals("sorted_by_descending_price")) {

                    // Если категория не пустая
                    if (!category.isEmpty()) {
                        // Если категория равная
                        if (category.equals("1:43")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                            // Если категория равная
                        } else if (category.equals("1:18")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                            // Если категория равная
                        } else if (category.equals("1:8")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        }
                        // Если категория не выбрана
                    }
                    else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                }
            }else if(!category.isEmpty()){
                if (category.equals("1:43")) {
                    model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                    // Если категория равная
                } else if (category.equals("1:18")) {
                    model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                    // Если категория равная
                } else if (category.equals("1:8")) {
                    model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                }
            }  else {//если выбрана цена
                model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThan(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
            }
            //если категория- масштаб не пустой
        } else if (!category.isEmpty() && price.isEmpty() && ot.isEmpty() && Do.isEmpty() ) {
            if (category.equals("1:43")) {
                model.addAttribute("search_product", productRepository.findByTitleAndCategory(search.toLowerCase(), 1));
            } else if (category.equals("1:18")) {
                model.addAttribute("search_product", productRepository.findByTitleAndCategory(search.toLowerCase(), 3));
            } else if (category.equals("1:8")) {
                model.addAttribute("search_product", productRepository.findByTitleAndCategory(search.toLowerCase(), 2));
            }
            //если цена от не пустая и пустая цена до и пустая категория и сортировка не выбрана
        }else if (!ot.isEmpty() & Do.isEmpty() & category.isEmpty() & price.isEmpty() ){
            model.addAttribute("search_product",productRepository.findByPriceFrom(search.toLowerCase(),Float.parseFloat(ot)));
            //если цена от  пустая и не пустая цена до и пустая категория и сортировка не выбрана
        }else if (ot.isEmpty() & !Do.isEmpty() & category.isEmpty() & price.isEmpty()){
            model.addAttribute("search_product",productRepository.findByPriceBefore(search.toLowerCase(),Float.parseFloat(Do)));
        }
        //если выбрана цена от и категория не пустая
        else if(!category.isEmpty() & !ot.isEmpty() & price.isEmpty() ){
            if (category.equals("1:43")) {
                model.addAttribute("search_product", productRepository.findByPriceFromAndCategory(search.toLowerCase(), Float.parseFloat(ot),1 ));
            }else if (category.equals("1:18"))
            { model.addAttribute("search_product", productRepository.findByPriceFromAndCategory(search.toLowerCase(), Float.parseFloat(ot),3 ));
            }else if (category.equals("1:8"))
            { model.addAttribute("search_product", productRepository.findByPriceFromAndCategory(search.toLowerCase(), Float.parseFloat(ot),2 ));}
        }
        //если выбрана цена до и категория не пустая
        else if(!category.isEmpty() & !Do.isEmpty() & price.isEmpty()){
            if (category.equals("1:43")) {
                model.addAttribute("search_product", productRepository.findByPriceBeforeAndCategory(search.toLowerCase(), Float.parseFloat(Do),1 ));
            }else if (category.equals("1:18"))
            { model.addAttribute("search_product", productRepository.findByPriceBeforeAndCategory(search.toLowerCase(), Float.parseFloat(Do),3 ));
            }else if (category.equals("1:8"))
            { model.addAttribute("search_product", productRepository.findByPriceBeforeAndCategory(search.toLowerCase(), Float.parseFloat(Do),2 ));}
        }
        //если выбрана цена от и сортировка
        else if (!ot.isEmpty() & !price.isEmpty() & category.isEmpty() ){
            //сортировка по возрастанию
            if (price.equals("sorted_by_ascending_price")) {
                model.addAttribute("search_product", productRepository.findByPriceFromByAsc(search.toLowerCase(), Float.parseFloat(ot)));
            }
            // Если в качестве сортировки выбрана сортировкам по убыванию
            else if (price.equals("sorted_by_descending_price")) {
                model.addAttribute("search_product", productRepository.findByPriceFromByDesc(search.toLowerCase(), Float.parseFloat(ot)));
            }
            //если выбрана цена до и сортировка
        } else if (!Do.isEmpty() & !price.isEmpty() & category.isEmpty()) {
            //сортировка по возрастанию
            if (price.equals("sorted_by_ascending_price")) {
                model.addAttribute("search_product", productRepository.findByPriceBeforeByAsc(search.toLowerCase(), Float.parseFloat(Do)));
            }
            // Если в качестве сортировки выбрана сортировкам по убыванию
            else if (price.equals("sorted_by_descending_price")) {
                model.addAttribute("search_product", productRepository.findByPriceBeforeByDesc(search.toLowerCase(), Float.parseFloat(Do)));

            }

        } //если выбрана цена от и сортировка  и масштаб
        else if(!ot.isEmpty() & !price.isEmpty() & !category.isEmpty()){

            if (price.equals("sorted_by_ascending_price")) {
                if (category.equals("1:43")) {
                    model.addAttribute("search_product", productRepository.findByPriceFromByAsc
                            (search.toLowerCase(), Float.parseFloat(ot), 1 ));
                } else if (category.equals("1:18")) {
                    model.addAttribute("search_product", productRepository.findByPriceFromByAsc
                            (search.toLowerCase(), Float.parseFloat(ot), 3 ));
                } else if (category.equals("1:8")) {
                    model.addAttribute("search_product", productRepository.findByPriceFromByAsc
                            (search.toLowerCase(), Float.parseFloat(ot), 2 ));
                }
            } else if (price.equals("sorted_by_descending_price")) {
                if (category.equals("1:43")) {
                    model.addAttribute("search_product", productRepository.findByPriceFromByDesc
                            (search.toLowerCase(), Float.parseFloat(ot), 1 ));
                } else if (category.equals("1:18")) {
                    model.addAttribute("search_product", productRepository.findByPriceFromByDesc
                            (search.toLowerCase(), Float.parseFloat(ot), 3 ));
                } else if (category.equals("1:8")) {
                    model.addAttribute("search_product", productRepository.findByPriceFromByDesc
                            (search.toLowerCase(), Float.parseFloat(ot), 2 ));
                }
            }
        }
        //если выбрана цена до и сортировка и масштаб
        else if(!Do.isEmpty() & !price.isEmpty() & !category.isEmpty()){
            if (price.equals("sorted_by_ascending_price")) {
                if (category.equals("1:43")) {
                    model.addAttribute("search_product", productRepository.findByPriceBeforeByAsc
                            (search.toLowerCase(), Float.parseFloat(Do), 1 ));
                } else if (category.equals("1:18")) {
                    model.addAttribute("search_product", productRepository.findByPriceBeforeByAsc
                            (search.toLowerCase(), Float.parseFloat(Do), 3 ));
                } else if (category.equals("1:8")) {
                    model.addAttribute("search_product", productRepository.findByPriceBeforeByAsc
                            (search.toLowerCase(), Float.parseFloat(Do), 2 ));
                }
            }else if (price.equals("sorted_by_descending_price")) {

                if (category.equals("1:43")) {
                    model.addAttribute("search_product", productRepository.findByPriceBeforeByDesc
                            (search.toLowerCase(), Float.parseFloat(Do), 1 ));
                } else if (category.equals("1:18")) {
                    model.addAttribute("search_product", productRepository.findByPriceBeforeByDesc
                            (search.toLowerCase(), Float.parseFloat(Do), 3 ));
                } else if (category.equals("1:8")) {
                    model.addAttribute("search_product", productRepository.findByPriceBeforeByDesc
                            (search.toLowerCase(), Float.parseFloat(Do), 2 ));
                }

            }
            //если выбрана категория и сортировка
        }else if(!price.isEmpty() & !category.isEmpty()) {
            //если выбрана категория и сортировка по возрастанию
            if (price.equals("sorted_by_ascending_price")) {
                if (category.equals("1:43")) {
                    model.addAttribute("search_product", productRepository.findByCategoryByAsc
                            (search.toLowerCase(), 1));
                } else if (category.equals("1:18")) {
                    model.addAttribute("search_product", productRepository.findByCategoryByAsc
                            (search.toLowerCase(), 3));
                } else if (category.equals("1:8")) {
                    model.addAttribute("search_product", productRepository.findByCategoryByAsc
                            (search.toLowerCase(), 2));
                }
                //если выбрана категория и сортировка по убыванию
            }else if (price.equals("sorted_by_descending_price")) {

                if (category.equals("1:43")) {
                    model.addAttribute("search_product", productRepository.findByCategoryByDesc
                            (search.toLowerCase(),  1 ));
                } else if (category.equals("1:18")) {
                    model.addAttribute("search_product", productRepository.findByCategoryByDesc
                            (search.toLowerCase(),  3 ));
                } else if (category.equals("1:8")) {
                    model.addAttribute("search_product", productRepository.findByCategoryByDesc
                            (search.toLowerCase(), 2 ));
                }
            }
            //если выбрана только сортировка
        }else if (!price.isEmpty() & category.isEmpty()& Do.isEmpty() & ot.isEmpty()){
            if (price.equals("sorted_by_ascending_price")) {
                model.addAttribute("search_product", productRepository.findAllByAsc());
            }
            if (price.equals("sorted_by_descending_price")) {

                model.addAttribute("search_product", productRepository.findAllByDesc());
            }
        } else  {
            //ищет товары по полному и неполному имени
            model.addAttribute("search_product",productRepository.findByTitleContainingIgnoreCase(search));
        }


        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);
       
        //model.addAttribute("products", productService.getAllProduct());

        return "/user/search";
    }
    
    
    
    
}
