package com.example.springsecurityapplication.util;

import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.services.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProductValidator implements Validator {

    private final ProductService productService;

    public ProductValidator(ProductService productService) {
        this.productService = productService;
    }

    // В данно методе указываем для какой модели предназначен данные валидатор
    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;
        if(productService.getProductFindByTitle(product) != null){
            errors.rejectValue("title", "","Данное наименование товара уже используеться");
        }

    }
    public void validateImage(Object target, Errors errors) {
        Product product = (Product) target;
        try { if ( productService.getProductFindByTitle(product).getImageList().size()==0) {

        }
        }catch (Exception e){
            errors.rejectValue("imageList", "", "Первое фото обязательно к добавлению");
        }
    }

    public void validateEdit(Object target, Errors errors) {
            Product product = (Product) target;

            if(product.getTitle().length()==0   ){
            errors.rejectValue("title", "","Наименование товара не может быть пустым");
            }

            if( product.getDescription().length()==0)  {
            errors.rejectValue("description", "","Описание товара не может быть пустым");
            }
            if( product.getPrice()==0 || product.getPrice()<0){
            errors.rejectValue("price", "","Цена товара не может быть равной нулю или меньше нуля");
            }

          if(product.getWarehouse().length()==0 ){
            errors.rejectValue("warehouse", "","Описание склада не может быть пустым");
          }

          if( product.getSeller().length()==0 ){
              errors.rejectValue("seller", "","Описание продавца не может быть пустым");
          }
    }


    public void validateCounts(int counts, Errors errors) {

    if(counts>3 ){
        errors.rejectValue("counts", "","Количество единиц товара  в заказе должно быть от 1 до 3");
    }
    }

    public void orderStringCheckFourSymbol(String str,Errors errors){

        if (str.length()!=4){
            errors.rejectValue("search", "","Количество символове должно быть 4");
        }

    }



}
