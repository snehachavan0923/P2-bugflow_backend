package com.bugflow.service;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bugflow.exception.CloudinaryException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class FileStorageService {

    @Autowired
    private Cloudinary cloudinary;

    public String upload(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                        "folder", "bugflow/issues"
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
        	throw new CloudinaryException("Image upload failed" + e.getMessage());
        }
    }
}