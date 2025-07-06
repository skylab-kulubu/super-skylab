package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {


    Image addImage(MultipartFile file);

    List<Image> getImages();

    Image getImageById(int id);

    void deleteImage(int id);

    Image getImageByUrl(String url);

    List<Image> getImagesByIds(List<Integer> imageIds);



}
