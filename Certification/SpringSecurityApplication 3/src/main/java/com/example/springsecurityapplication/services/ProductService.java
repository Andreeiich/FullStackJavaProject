package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Image;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.ImageRepository;
import com.example.springsecurityapplication.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    // Данный метод позволяет вернуть все продукты
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    // Данный метод позволяет вернуть товар по id
    public Product getProductId(int id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }


    // Данный метод позволяет сохранить объект продукта, который пришел с формы
    @Transactional
    public void saveProduct(Product product){
        productRepository.save(product);
    }

    // Данный метод позволяет обновить информацию о продукте
    @Transactional
    public void updateProduct(int id, Product product){
        product.setId(id);
        productRepository.save(product);
    }

    @Transactional
    public void updateProductWithImage(int id, Product product, List<Image> imageList){
        product.setId(id);

       for(int i=0;i<imageList.size();i++) {
           imageRepository.updateImage(imageList.get(i).getFileName(),imageList.get(i).getId());
       }
        productRepository.save(product);

    }

    @Transactional
    public void updateProductWithImageDelete(Product product,List<Image> imageList,int index){
        if (imageList.size()!=1){
        imageRepository.deleteImage(imageList.get(index).getId());
        imageList.remove(index);}
    }

    // Данный метод позволяет удалить товар по id
    @Transactional
    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }

    // Данный метод позволит получить товар по наименованию
    public Product  getProductFindByTitle(Product product){
        Optional<Product> product_db = productRepository.findByTitle(product.getTitle());
        return product_db.orElse(null);
    }

  /*  public Product findByTitleNotFull (String search){
       // Optional<Product> product_db = productRepository.findByTitleNotFull(search);
        return product_db.orElse(null);
    }
*/
  public List<Product> findAllProductsBySeller (String loginSeller){
     List <Product> product_db = productRepository.findAllByIiSeller(loginSeller);
      return product_db;
  }



}
