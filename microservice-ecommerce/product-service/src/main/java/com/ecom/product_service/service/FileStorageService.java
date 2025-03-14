package com.ecom.product_service.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Service
public class FileStorageService {
    private static final Logger log = LogManager.getLogger(FileStorageService.class);
    @Value("${app.file.upload-dir}")
    private String baseUploadDir;

    @Value("${app.file.profile-pictures-dir}")
    private String profilePicturesDir;

    @Value("${app.file.products-dir}")
    private String productsDir;


    public String saveProfilePicture(MultipartFile file, String userId, String fileExtension) throws IOException {
        String fileName = userId + fileExtension;
        Path uploadPath = Paths.get(baseUploadDir, profilePicturesDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path oldFilePath = uploadPath.resolve(fileName);
        if (Files.exists(oldFilePath)) {
            Files.delete(oldFilePath);
        }

        Path newFilePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), newFilePath);

        return profilePicturesDir + fileName;
    }

    public String saveProductImage(MultipartFile file, Long productId, String fileName) throws IOException {
        Path uploadPath = Paths.get(baseUploadDir, productsDir, String.valueOf(productId));

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path newFilePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), newFilePath);
        return productsDir + "/" + productId + "/" + fileName;
    }

    public void deleteFile(String filePath) throws IOException {
        String relativeFilePath = filePath.replaceFirst("^/uploads/", "");
        Path fileToDeletePath = Paths.get(baseUploadDir, relativeFilePath);

        if (Files.exists(fileToDeletePath)) {
            Files.delete(fileToDeletePath);
        } else {
            throw new IOException("File not found: " + filePath);
        }
    }

    public void deleteFolder(String dir, Long id) throws IOException {
        Path folderPath  = Paths.get(baseUploadDir, dir, String.valueOf(id));

        if (Files.notExists(folderPath)) {
            throw new IOException("Folder not found: " + folderPath.toAbsolutePath());
        }

        Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });

        log.info("Deleted folder: {}", folderPath.toAbsolutePath());
    }



}
