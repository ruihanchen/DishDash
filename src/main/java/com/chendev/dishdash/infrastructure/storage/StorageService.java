package com.chendev.dishdash.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    //Uploads a file and returns the public URL or path to access it./
    String upload(MultipartFile file, String directory);

    // Deletes a previously uploaded file.
    void delete(String fileUrl);
}