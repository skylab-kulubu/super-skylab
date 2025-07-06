package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageDao extends JpaRepository<Image, Integer> {

    @Transactional
    Optional<Image> findByUrl(String url);


    @Query("SELECT i FROM Image i WHERE i.id IN :ids")
    List<Image> findAllByIds(@Param("ids") List<Integer> ids);
}
