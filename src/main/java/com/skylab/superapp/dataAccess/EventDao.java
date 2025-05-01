package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDao extends JpaRepository<Event,Integer> {
    Event findById(int id);

    List<Event> findAllByTenantOrderByDateDesc(String tenant);

    List<Event> findAllByTenantAndIsActiveTrue(String tenant);

    List<Event> findAllByTenantAndType(String tenant, String type);


}
