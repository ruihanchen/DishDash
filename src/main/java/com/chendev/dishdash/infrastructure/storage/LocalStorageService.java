package com.chendev.dishdash.infrastructure.storage;

import com.chendev.dishdash.common.exception.BusinessException;
import com.chendev.dishdash.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Slf4j
@Service
@Profile("local")
public class LocalStorageService implements StorageService {

    @Value("${app.storage.local-base-path:${java.io.tmpdir}/dishdash-uploads}")
    private String basePath;

    @PostConstruct
    public void init() throws IOException {
        // Ensure the base directory exists at startup so we fail fast
        // if there's a permissions problem, rather than at first upload.
        Files.createDirectories(Paths.get(basePath));
        log.info("LocalStorageService initialized. Base path: {}", basePath);
    }

    @Override
    public String upload(MultipartFile file, String directory) {
        try {
            Path dir = Paths.get(basePath, directory);
            Files.createDirectories(dir);

            // use UUID for the filename to avoid collisions and prevent
            // path traversal attacks via malicious original filenames
            String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
            Path destination = dir.resolve(filename);
            file.transferTo(destination);

            // return a path the client can use. In production this would
            // be a full HTTPS URL from the CDN/S3 bucket.
            String filePath = "/uploads/" + directory + "/" + filename;
            log.debug("File saved locally: {}", filePath);
            return filePath;

        } catch (IOException e) {
            log.error("Failed to save file locally", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String fileUrl) {
        try {
            // strip the /uploads/ prefix to get the relative path
            String relativePath = fileUrl.replaceFirst("^/uploads/", "");
            Path target = Paths.get(basePath, relativePath);
            Files.deleteIfExists(target);
            log.debug("File deleted locally: {}", fileUrl);
        } catch (IOException e) {
            // log but don't throw that a failed delete of an orphaned file
            // should not roll back the business operation that triggered it
            log.warn("Could not delete local file: {}", fileUrl, e);
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
