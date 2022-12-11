package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPerson(Person person);

    @Query(value = "select * from orders",nativeQuery = true)
    List<Order> findAll();

    @Query(value = "select * from orders where id= ?1",nativeQuery = true)
    Optional<Order> findById(int id);

    @Modifying
    @Query(value = "update orders set status=?1 where id=?2",nativeQuery = true)
    void updateStatusOrder(int status,int id);

    @Query(value = "select * from orders where substring(number_of_order,(length(number_of_order)-3))= ?1",nativeQuery = true)
    Optional<Order> findByLastFourCharacters(String str);
}
