package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByTitle(String title);
    // Поиск по части наименования товара в не зависимости от регистра
    List<Product> findByTitleContainingIgnoreCase(String name);

    @Query(value = "select * from product where seller = ?1", nativeQuery = true)
    List<Product> findAllByIiSeller(String sellerLogin);


    @Query(value = "select from product where title LIKE '?1%'", nativeQuery = true)
    List<Product> findByTitleNotFull(String title);

    // Поиск по части наименования товара и фильтрация по диапазону цен +
    @Query(value = "select * from product where (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3))", nativeQuery = true)
    List<Product> findByTitleAndPriceGreaterThanEqualAndPriceLessThan(String title, float ot, float Do);

    // Поиск по части наименования товара и фильтрация по диапазону цен, сортировка по возрастанию
    @Query(value = "select * from product where (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3))order by price ", nativeQuery = true)
    List<Product> findByTitleOrderByPrice(String title, float ot, float Do);

    // Поиск по части наименования товара и фильтрация по диапазону цен, сортировка по убыванию
    @Query(value = "select * from product where (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') or" +
            " (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3)) order by price desc", nativeQuery = true)
    List<Product> findByTitleOrderByPriceDesc(String title, float ot, float Do);


    // Поиск по части наименования товара и фильтрация по диапазону цен, сортировка по возрастанию, фильтрация по категории
    @Query(value = "select * from product where category_id=?4 and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by price", nativeQuery = true)
    List<Product> findByTitleAndCategoryOrderByPrice(String title, float ot, float Do, int category);


    // Поиск по части наименования товара и фильтрация по диапазону цен, сортировка по убыванию, фильтрация по категории
    @Query(value = "select * from product where category_id=?4 and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by price desc", nativeQuery = true)
    List<Product> findByTitleAndCategoryOrderByPriceDesc(String title, float ot, float Do, int category);

    @Query(value = "select * from product where category_id=?2 and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
                      "or (lower(title) LIKE '%?1'))",nativeQuery = true)
    List<Product> findByTitleAndCategory(String title,  int category);

    @Query(value = "select * from product where price >=?2  and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
                                  "or (lower(title) LIKE '%?1'))", nativeQuery = true)
    List<Product> findByPriceFrom(String title,  float price);
    @Query(value = "select * from product where price <=?2  and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1'))", nativeQuery = true)
    List<Product> findByPriceBefore(String title,  float price);


    @Query(value = "select * from product where category_id=?3 and price >=?2  and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1'))", nativeQuery = true)
    List<Product> findByPriceFromAndCategory(String title,  float price,int category);


    @Query(value = "select * from product where category_id=?3 and price <=?2  and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1'))", nativeQuery = true)
    List<Product> findByPriceBeforeAndCategory(String title, float price,int category);


    //выбрана цена от и сортировка по возрастанию
    @Query(value = "select * from product where (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price >= ?2 ))order by price ", nativeQuery = true)
    List<Product> findByPriceFromByAsc(String title, float price);

    //выбрана цена от и сортировка по убыванию
    @Query(value = "select * from product where (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price >= ?2 ))order by price desc ", nativeQuery = true)
    List<Product> findByPriceFromByDesc(String title, float price);


    //выбрана цена до и сортировка по возрастанию
    @Query(value = "select * from product where (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price <= ?2 ))order by price ", nativeQuery = true)
    List<Product> findByPriceBeforeByAsc(String title, float price);

    //выбрана цена до и сортировка по убыванию
    @Query(value = "select * from product where (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price <= ?2 ))order by price desc ", nativeQuery = true)
    List<Product> findByPriceBeforeByDesc(String title, float price);


    //выбрана цена от и сортировка по возрастанию и категория (масштаб)
    @Query(value = "select * from product where category_id=?3 and (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price >= ?2 ))order by price ", nativeQuery = true)
    List<Product> findByPriceFromByAsc(String title, float price,int category);

    //выбрана цена от и сортировка по убыванию и категория (масштаб)
    @Query(value = "select * from product where category_id=?3 and (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price >= ?2 ))order by price desc ", nativeQuery = true)
    List<Product> findByPriceFromByDesc(String title, float price,int category);

    //выбрана цена до и сортировка по возрастанию и категория (масштаб)
    @Query(value = "select * from product where category_id=?3 and (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price <= ?2 ))order by price ", nativeQuery = true)
    List<Product> findByPriceBeforeByAsc(String title, float price,int category);

    //выбрана цена до и сортировка по убыванию  и категория (масштаб)
    @Query(value = "select * from product where  category_id=?3 and (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')) and (price <= ?2 ))order by price desc ", nativeQuery = true)
    List<Product> findByPriceBeforeByDesc(String title, float price,int category);

    //выбрана сортировка по возрастанию и категория (масштаб)
    @Query(value = "select * from product where category_id=?2 and (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')))order by price ", nativeQuery = true)
    List<Product> findByCategoryByAsc(String title, int category);

    //выбрана сортировка по убыванию и категория (масштаб)
    @Query(value = "select * from product where category_id=?2 and (((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') " +
            "or (lower(title) LIKE '%?1')))order by price desc ", nativeQuery = true)
    List<Product> findByCategoryByDesc(String title, int category);

    @Query(value = "select * from product order by price",nativeQuery = true)
    List<Product> findAllByAsc();

    @Query(value = "select * from product order by price desc",nativeQuery = true)
    List<Product> findAllByDesc();
}
