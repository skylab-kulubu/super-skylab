package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {


    DataResult<Image> addImage(MultipartFile file);

    DataResult<List<Image>> getImages();

    DataResult<Image> getImageById(int id);

    Result deleteImage(int id);

    DataResult<Image> getImageByUrl(String url);

    DataResult<List<Image>> getImagesByIds(List<Integer> imageIds);



}
