package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Photo.CreatePhotoDto;
import com.skylab.superapp.entities.DTOs.Photo.GetPhotoDto;
import com.skylab.superapp.entities.Photo;

import java.util.List;

public interface PhotoService {

    Result addPhoto(CreatePhotoDto createPhotoDto);

    Result deletePhoto(int id);

    DataResult<Photo> getPhotoEntityById(int id);

    DataResult<List<GetPhotoDto>> getAllPhotos();

    DataResult<List<Photo>> getPhotosByIds(List<Integer> ids);


}
