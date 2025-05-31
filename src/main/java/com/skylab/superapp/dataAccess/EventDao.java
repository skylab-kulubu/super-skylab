package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface EventDao extends JpaRepository<Event,Integer> {
    Event findById(int id);

    List<Event> findAllByType_NameOrderByDateDesc(String name);

    List<Event> findAllByType_NameAndIsActiveTrue(String name);

    List<Event> findAllByType_Name(String name);


    List<Event> findAllByType_NameAndDateAfter(String name, Date date);
}
