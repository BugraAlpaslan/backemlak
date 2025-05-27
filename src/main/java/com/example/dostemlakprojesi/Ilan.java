// Ilan.java - Fotoğraf ekleme sorunu düzeltildi
package com.example.dostemlakprojesi;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ilanlar")
public class Ilan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int ilanID;
    private static int nextId = 1;
    
    public int fiyat;
    public int m2;
    public int binaYasi;
    public int odaSayisi;
    
    @Column(length = 500)
    public String kimden;
    
    @Column(length = 500)
    public String ismi;
    
    @Column(length = 2000)
    public String aciklama;
    
    @Column(length = 500)
    public String konum;
    
    // Yeni alanlar (React frontend ile uyumlu)
    public String city;
    public String district;
    public String neighborhood;
    public Integer bathrooms = 1;
    public Integer floor;
    public Integer totalFloors;
    public String type = "rent";
    public String heatingType = "central";
    public String furnished = "unfurnished";
    public Boolean parkingSpot = false;
    public Boolean balcony = false;
    
    @ElementCollection
    @CollectionTable(name = "ilan_features", joinColumns = @JoinColumn(name = "ilan_id"))
    @Column(name = "feature")
    public List<String> features;
    
    // ⭐ Resim sistemi - hem LinkedList hem de List olarak
    @Transient
    @JsonIgnore
    public FotoNode fotoHead; // Mevcut foto linkedlist yapısı
    
    // ⭐ React için basit resim listesi
    @ElementCollection
    @CollectionTable(name = "ilan_images", joinColumns = @JoinColumn(name = "ilan_id"))
    @Column(name = "image_url", length = 1000)
    public List<String> imageUrls; // Resim URL'lerini direkt liste olarak tut
    
    @Transient
    @JsonIgnore
    public Ilan next; // Mevcut linkedlist yapısı
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    public KullaniciNode owner;
    
    @Column(name = "created_at")
    public LocalDateTime createdAt;
    
    public Integer viewCount = 0;
    
    // Constructors
    public Ilan(int fiyat, int m2, int binaYasi, int odaSayisi, String kimden, 
                String ismi, String aciklama, String konum) {
        this.ilanID = nextId++;
        this.fiyat = fiyat;
        this.m2 = m2;
        this.binaYasi = binaYasi;
        this.odaSayisi = odaSayisi;
        this.kimden = kimden;
        this.ismi = ismi;
        this.aciklama = aciklama;
        this.konum = konum;
        this.createdAt = LocalDateTime.now();
        this.fotoHead = null;
        this.next = null;
        this.features = new ArrayList<>();
        this.imageUrls = new ArrayList<>(); // ⭐ Resim listesini initialize et
        
        System.out.println("🆔 Yeni ilan ID atandı: " + this.ilanID + " - " + ismi);
    }
    
    public Ilan() {
        this.ilanID = nextId++;
        this.createdAt = LocalDateTime.now();
        this.features = new ArrayList<>();
        this.imageUrls = new ArrayList<>(); // ⭐ Resim listesini initialize et
        
        System.out.println("🆔 Boş ilan ID atandı: " + this.ilanID);
    }
    
    // ⭐ Geliştirilmiş fotoğraf ekleme metodları
    public void fotoEkle(String fotoPath) {
        System.out.println("📸 Resim ekleniyor: " + fotoPath);
        
        // 1. LinkedList'e ekle (mevcut sistem)
        FotoNode yeni = new FotoNode(fotoPath);
        if (fotoHead == null) {
            fotoHead = yeni;
            fotoHead.next = fotoHead;
            fotoHead.prev = fotoHead;
        } else {
            FotoNode son = fotoHead.prev;
            son.next = yeni;
            yeni.prev = son;
            yeni.next = fotoHead;
            fotoHead.prev = yeni;
        }
        
        // 2. List'e de ekle (React için)
        if (imageUrls != null && !imageUrls.contains(fotoPath)) {
            imageUrls.add(fotoPath);
            System.out.println("✅ Resim liste sistemine eklendi: " + fotoPath);
        }
        
        System.out.println("✅ Toplam resim sayısı: " + (imageUrls != null ? imageUrls.size() : 0));
    }
    
    // Detaylı bilgi ile foto ekleme
    public void fotoEkle(String fotoPath, String originalName, String contentType, Long fileSize) {
        System.out.println("📸 Detaylı resim ekleniyor: " + fotoPath);
        
        // 1. LinkedList'e ekle
        FotoNode yeni = new FotoNode(fotoPath, originalName, contentType, fileSize);
        yeni.ilan = this;
        
        if (fotoHead == null) {
            fotoHead = yeni;
            fotoHead.next = fotoHead;
            fotoHead.prev = fotoHead;
        } else {
            FotoNode son = fotoHead.prev;
            son.next = yeni;
            yeni.prev = son;
            yeni.next = fotoHead;
            fotoHead.prev = yeni;
        }
        
        // 2. List'e de ekle
        if (imageUrls != null && !imageUrls.contains(fotoPath)) {
            imageUrls.add(fotoPath);
            System.out.println("✅ Detaylı resim liste sistemine eklendi: " + fotoPath);
        }
    }
    
    // ⭐ React için fotoları liste olarak al - Düzeltildi
    public List<String> getImageUrls() {
        System.out.println("🔍 Resim URL'leri alınıyor...");
        
        // Önce liste sistemini kontrol et
        if (imageUrls != null && !imageUrls.isEmpty()) {
            System.out.println("✅ Liste sisteminden " + imageUrls.size() + " resim bulundu");
            return new ArrayList<>(imageUrls);
        }
        
        // Liste boşsa LinkedList'ten al
        List<String> urls = new ArrayList<>();
        if (fotoHead != null) {
            FotoNode current = fotoHead;
            do {
                String url = current.getUrl();
                urls.add(url);
                current = current.next;
            } while (current != fotoHead && current != null);
            
            System.out.println("✅ LinkedList'ten " + urls.size() + " resim alındı");
            
            // Liste sistemini güncelle
            if (imageUrls == null) {
                imageUrls = new ArrayList<>();
            }
            imageUrls.addAll(urls);
        }
        
        // Hiç resim yoksa default resim ekle
        if (urls.isEmpty()) {
            String defaultImage = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=400&h=300&fit=crop";
            urls.add(defaultImage);
            if (imageUrls == null) {
                imageUrls = new ArrayList<>();
            }
            imageUrls.add(defaultImage);
            System.out.println("✅ Default resim eklendi");
        }
        
        System.out.println("📸 Toplam dönen resim sayısı: " + urls.size());
        return urls;
    }
    
    // ⭐ İlk resmin URL'ini al (React carousel için) - Düzeltildi
    public String getImageUrl() {
        System.out.println("🔍 Ana resim URL'i alınıyor...");
        
        // Önce liste sistemini kontrol et
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String url = imageUrls.get(0);
            System.out.println("✅ Liste sisteminden ana resim: " + url);
            return url;
        }
        
        // LinkedList'ten al
        if (fotoHead != null) {
            String url = fotoHead.getUrl();
            System.out.println("✅ LinkedList'ten ana resim: " + url);
            
            // Liste sistemini güncelle
            if (imageUrls == null) {
                imageUrls = new ArrayList<>();
            }
            if (!imageUrls.contains(url)) {
                imageUrls.add(url);
            }
            
            return url;
        }
        
        // Default resim
        String defaultImage = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=400&h=300&fit=crop";
        System.out.println("✅ Default ana resim döndürülüyor");
        
        // Liste sistemine de ekle
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }
        if (!imageUrls.contains(defaultImage)) {
            imageUrls.add(defaultImage);
        }
        
        return defaultImage;
    }
    
    // ⭐ Resim sayısını al
    public int getImageCount() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.size();
        }
        
        int count = 0;
        if (fotoHead != null) {
            FotoNode current = fotoHead;
            do {
                count++;
                current = current.next;
            } while (current != fotoHead && current != null);
        }
        
        return count > 0 ? count : 1; // En az 1 (default resim)
    }
    
    // ⭐ Tüm resimleri temizle
    public void clearAllImages() {
        System.out.println("🗑️ Tüm resimler temizleniyor...");
        
        // LinkedList'i temizle
        fotoHead = null;
        
        // Liste'yi temizle
        if (imageUrls != null) {
            imageUrls.clear();
        }
        
        System.out.println("✅ Tüm resimler temizlendi");
    }
    
    // React frontend için getters (JSON serialization için)
    public String getTitle() { return ismi; }
    public String getDescription() { return aciklama; }
    public Integer getPrice() { return fiyat; }
    public Integer getArea() { return m2; }
    public Integer getBedrooms() { return odaSayisi; }
    public String getLocation() { return konum; }
    public Long getId() { return (long) ilanID; }
    public Integer getBuildingAge() { return binaYasi; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getNeighborhood() { return neighborhood; }
    public Integer getBathrooms() { return bathrooms; }
    public Integer getFloor() { return floor; }
    public Integer getTotalFloors() { return totalFloors; }
    public String getType() { return type; }
    public String getHeatingType() { return heatingType; }
    public String getFurnished() { return furnished; }
    public Boolean getParkingSpot() { return parkingSpot; }
    public Boolean getBalcony() { return balcony; }
    public List<String> getFeatures() { return features; }
    public Integer getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}