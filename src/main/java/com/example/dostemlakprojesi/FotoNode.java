// src/main/java/com/example/dostemlakprojesi/FotoNode.java
package com.example.dostemlakprojesi;

import jakarta.persistence.*;

@Entity
@Table(name = "listing_images")
public class FotoNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Web için resim bilgileri (JavaFX Image yerine)
    @Column(name = "image_path")
    public String imagePath;        // Dosya yolu
    
    @Column(name = "image_url")
    public String imageUrl;         // Web URL'i (React için)
    
    @Column(name = "original_name")
    public String originalName;     // Orijinal dosya adı
    
    @Column(name = "file_size")
    public Long fileSize;           // Dosya boyutu
    
    @Column(name = "content_type")
    public String contentType;      // image/jpeg, image/png vs.
    
    // LinkedList yapısı (mevcut sisteminizi koruyoruz)
    @Transient
    public FotoNode next;
    
    @Transient  
    public FotoNode prev;
    
    // JPA ilişkisi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ilan_id")
    public Ilan ilan;
    
    // Constructors
    public FotoNode() {}
    
    // Mevcut constructor'ınızı web uyumlu hale getiriyoruz
    public FotoNode(String path) {
        this.imagePath = path;
        this.imageUrl = convertPathToUrl(path);
        this.next = null;
        this.prev = null;
    }
    
    // Web için yeni constructor
    public FotoNode(String path, String originalName, String contentType, Long fileSize) {
        this.imagePath = path;
        this.imageUrl = convertPathToUrl(path);
        this.originalName = originalName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.next = null;
        this.prev = null;
    }
    
    // Path'i URL'e çevir (React için)
    private String convertPathToUrl(String path) {
        if (path == null) return null;
        
        // Eğer zaten URL ise olduğu gibi döndür
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        
        // Local path'i web URL'ine çevir
        // Örnek: "uploads/resim.jpg" → "http://localhost:8080/uploads/resim.jpg"
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        return "http://localhost:8080/uploads/" + fileName;
    }
    
    // React frontend için getter metodları
    public String getUrl() {
        return imageUrl != null ? imageUrl : convertPathToUrl(imagePath);
    }
    
    public String getPath() {
        return imagePath;
    }
    
    public String getName() {
        return originalName != null ? originalName : "resim.jpg";
    }
    
    // Getters ve Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { 
        this.imagePath = imagePath;
        this.imageUrl = convertPathToUrl(imagePath);
    }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public Ilan getIlan() { return ilan; }
    public void setIlan(Ilan ilan) { this.ilan = ilan; }
}