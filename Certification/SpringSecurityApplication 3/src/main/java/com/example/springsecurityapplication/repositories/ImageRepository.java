package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Modifying
    @Query(value = "Update image set file_name = ?1 where id = ?2",nativeQuery = true)
    void updateImage(String file_name,int id);

    @Modifying
    @Query(value = "DELETE  from image where id = ?1",nativeQuery = true)
    void deleteImage(int id);

}
