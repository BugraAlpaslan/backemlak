// src/main/java/com/example/dostemlakprojesi/FileUploadController.java
package com.example.dostemlakprojesi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:3000")
public class FileUploadController {
    
    private final String uploadDir = "uploads";
    
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "Dosya boş"
                ));
            }
            
            // Dosya türü kontrolü
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "Sadece resim dosyaları kabul edilir"
                ));
            }
            
            // Dosya boyutu kontrolü (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "Dosya boyutu 5MB'dan küçük olmalı"
                ));
            }
            
            // Upload dizinini oluştur
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Benzersiz dosya adı oluştur
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Dosyayı kaydet
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // URL oluştur
            String fileUrl = "http://localhost:8080/uploads/" + newFilename;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dosya başarıyla yüklendi");
            response.put("filename", newFilename);
            response.put("originalName", originalFilename);
            response.put("url", fileUrl);
            response.put("size", file.getSize());
            response.put("contentType", contentType);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, 
                "message", "Dosya yüklenemedi: " + e.getMessage()
            ));
        }
    }
}