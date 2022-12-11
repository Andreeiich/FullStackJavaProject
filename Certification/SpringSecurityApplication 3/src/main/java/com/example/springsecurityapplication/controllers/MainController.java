package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.repositories.ProductRepository;
import com.example.springsecurityapplication.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class MainController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public MainController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    // Данный метод предназначен для отображении товаров без прохождения аутентификации и авторизации
    @GetMapping("")
    public String getAllProduct(Model model){
        model.addAttribute("products", productService.getAllProduct());
        return "product/product";
    }

    @GetMapping("/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductId(id));
        return "product/infoProduct";
    }

    @PostMapping("/search")
    public String productSearch(@RequestParam("search") String search,
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
        model.addAttribute("products", productService.getAllProduct());
        return "/product/product";
    }
}
