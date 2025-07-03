package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.ErrorDataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.dataAccess.EventTypeDao;
import com.skylab.superapp.entities.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class EventTypeManager implements EventTypeService {

    private final EventTypeDao eventTypeDao;

    @Autowired
    public EventTypeManager(EventTypeDao eventTypeDao) {
        this.eventTypeDao = eventTypeDao;
    }


    @Override
    public DataResult<EventType> getEventTypeByName(String eventTypeName) {

        var eventTypeResult = eventTypeDao.findByName(eventTypeName);
        if (!eventTypeResult.isPresent()) {
            return new ErrorDataResult<>(EventTypeMessages.eventTypeNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(eventTypeResult.get(), EventTypeMessages.eventTypeFound, HttpStatus.OK);


    }

    @Override
    public DataResult<EventType> getEventTypeById(int eventTypeId) {
        var eventTypeResult = eventTypeDao.findById(eventTypeId);
        if (!eventTypeResult.isPresent()) {
            return new ErrorDataResult<>(EventTypeMessages.eventTypeNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(eventTypeResult.get(), EventTypeMessages.eventTypeFound, HttpStatus.OK);

    }
}