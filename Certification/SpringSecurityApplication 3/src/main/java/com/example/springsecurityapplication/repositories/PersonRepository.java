package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    // Получаем запись из БД по логину
    Optional<Person> findByLogin(String login);
    Optional<Person> findById(String login);

    @Modifying
    @Query(value = "UPDATE person SET password = ?2 WHERE id=?1", nativeQuery = true)
    void updatePersonById(int id,String password);

    @Modifying
    @Query(value = "UPDATE person SET login=?1, role=?2 WHERE id=?3",nativeQuery = true)
    void  updatePersonById(String login,String role,int id);

    @Query (value = " select * from orders where person_id = ?1",nativeQuery = true)
    Optional<Person> checkOrdersByIdPerson(int id);

    @Query(value = "select *from person where id!=?1",nativeQuery = true)
    List<Person> getAllExceptMe(int id);

    @Query(value = "select * from person where (((lower(login) LIKE %?1%) or (lower(login) LIKE '?1%')" +
            "or (lower(login) LIKE '%?1')))", nativeQuery = true)
    List <Person> getPersonByPartOfName(String str);
}
