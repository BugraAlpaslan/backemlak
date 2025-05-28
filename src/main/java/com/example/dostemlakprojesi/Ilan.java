// Ilan.java - Temiz ve Basit Versiyon
package com.example.dostemlakprojesi;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ilanlar")
public class Ilan {
    
    // ========================================
    // TEMEL BİLGİLER
    // ========================================
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long ilanID;
    
    @Column(name = "owner_id", nullable = false)
    public Integer ownerId; // İlanı ekleyen kullanıcının ID'si
    
    @Column(nullable = false)
    public String ismi; // İlan başlığı
    
    @Column(length = 2000)
    public String aciklama; // İlan açıklaması
    
    @Column(nullable = false)
    public Integer fiyat; // Fiyat (₺)
    
    @Column(nullable = false)
    public Integer m2; // Alan (m²)
    
    @Column(nullable = false)
    public Integer odaSayisi; // Oda sayısı
    
    public Integer binaYasi = 0; // Bina yaşı
    
    @Column(length = 500)
    public String konum; // Konum bilgisi
    
    // ========================================
    // DETAY BİLGİLER
    // ========================================
    
    public String city; // Şehir
    public String district; // İlçe  
    public String neighborhood; // Mahalle
    
    public Integer bathrooms = 1; // Banyo sayısı
    public Integer floor; // Kat
    public Integer totalFloors; // Toplam kat
    
    public String type = "rent"; // rent/sale
    public String heatingType = "central"; // Isıtma tipi
    public String furnished = "unfurnished"; // Eşya durumu
    
    public Boolean parkingSpot = false; // Otopark
    public Boolean balcony = false; // Balkon
    
    @Column(length = 500)
    public String kimden = "İlan Sahibi"; // İlan sahibi
    
    // ========================================
    // RESİMLER - BASİT SİSTEM
    // ========================================
    
    @ElementCollection
    @CollectionTable(name = "ilan_images", joinColumns = @JoinColumn(name = "ilan_id"))
    @Column(name = "image_url", length = 1000)
    public List<String> imageUrls = new ArrayList<>();
    
    // ========================================
    // ÖZELLİKLER
    // ========================================
    
    @ElementCollection
    @CollectionTable(name = "ilan_features", joinColumns = @JoinColumn(name = "ilan_id"))
    @Column(name = "feature")
    public List<String> features = new ArrayList<>();
    
    // ========================================
    // SİSTEM BİLGİLERİ
    // ========================================
    
    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
    
    public Integer viewCount = 0; // Görüntülenme sayısı
    
    // ========================================
    // ESKİ SİSTEM UYUMLULUĞU (LinkedList)
    // ========================================
    
    @Transient
    @JsonIgnore
    public FotoNode fotoHead; // Eski foto sistemi ile uyumluluk
    
    @Transient
    @JsonIgnore
    public Ilan next; // Eski linkedlist yapısı
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    @JsonIgnore
    public KullaniciNode owner;
    
    // ========================================
    // CONSTRUCTORS
    // ========================================
    
    public Ilan() {
        this.createdAt = LocalDateTime.now();
        this.imageUrls = new ArrayList<>();
        this.features = new ArrayList<>();
        System.out.println("🏠 Boş ilan oluşturuldu");
    }
    
    public Ilan(Integer fiyat, Integer m2, Integer binaYasi, Integer odaSayisi, 
                String kimden, String ismi, String aciklama, String konum) {
        this();
        this.fiyat = fiyat;
        this.m2 = m2;
        this.binaYasi = binaYasi;
        this.odaSayisi = odaSayisi;
        this.kimden = kimden;
        this.ismi = ismi;
        this.aciklama = aciklama;
        this.konum = konum;
        
        System.out.println("🏠 Yeni ilan oluşturuldu: " + ismi);
    }
    
    // ========================================
    // RESİM YÖNETİMİ - BASİT
    // ========================================
    
    /**
     * İlana resim ekle
     */
    public void fotoEkle(String imageUrl) {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            if (!imageUrls.contains(imageUrl)) {
                imageUrls.add(imageUrl);
                System.out.println("📸 Resim eklendi: " + imageUrl);
            }
        }
    }
    
    /**
     * Ana resim URL'ini al (ilk resim)
     */
    public String getImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800&h=600&fit=crop";
    }
    
    /**
     * Tüm resim URL'lerini al
     */
    public List<String> getImageUrls() {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return List.of("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800&h=600&fit=crop");
        }
        return new ArrayList<>(imageUrls);
    }
    
    /**
     * Resim sayısını al
     */
    public int getImageCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }
    
    /**
     * Tüm resimleri temizle
     */
    public void clearAllImages() {
        if (imageUrls != null) {
            imageUrls.clear();
        }
        System.out.println("🗑️ Tüm resimler temizlendi");
    }
    
    // ========================================
    // ESKİ SİSTEM UYUMLULUĞU
    // ========================================
    
    /**
     * Eski foto linkedlist sistemi ile uyumluluk
     * Sadece eski kodlarla çalışmak için
     */
    @Deprecated
    public void fotoEkleEski(String fotoPath) {
        // Yeni sisteme ekle
        fotoEkle(fotoPath);
        
        // Eski linkedlist sistemine de ekle (uyumluluk için)
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
    }
    
    // ========================================
    // REACT FRONTEND İÇİN GETTERS
    // ========================================
    
    public Long getId() { return ilanID; }
    public String getTitle() { return ismi; }
    public String getDescription() { return aciklama; }
    public Integer getPrice() { return fiyat; }
    public Integer getArea() { return m2; }
    public Integer getBedrooms() { return odaSayisi; }
    public String getLocation() { return konum; }
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
    public List<String> getFeatures() { return features != null ? features : new ArrayList<>(); }
    public Integer getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Integer getOwnerId() { return ownerId; }
    
    // ========================================
    // SETTERS
    // ========================================
    
    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
        System.out.println("👤 İlan sahip ID'si set edildi: " + ownerId);
    }
    
    public void setTitle(String title) { this.ismi = title; }
    public void setDescription(String description) { this.aciklama = description; }
    public void setPrice(Integer price) { this.fiyat = price; }
    public void setArea(Integer area) { this.m2 = area; }
    public void setBedrooms(Integer bedrooms) { this.odaSayisi = bedrooms; }
    public void setLocation(String location) { this.konum = location; }
    public void setBuildingAge(Integer buildingAge) { this.binaYasi = buildingAge; }
    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }
    public void setType(String type) { this.type = type; }
    public void setHeatingType(String heatingType) { this.heatingType = heatingType; }
    public void setFurnished(String furnished) { this.furnished = furnished; }
    public void setParkingSpot(Boolean parkingSpot) { this.parkingSpot = parkingSpot; }
    public void setBalcony(Boolean balcony) { this.balcony = balcony; }
    public void setFeatures(List<String> features) { this.features = features; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    // ========================================
    // UTILITY METODLARı
    // ========================================
    
    @Override
    public String toString() {
        return "Ilan{" +
                "ilanID=" + ilanID +
                ", ismi='" + ismi + '\'' +
                ", fiyat=" + fiyat +
                ", m2=" + m2 +
                ", ownerId=" + ownerId +
                ", imageCount=" + getImageCount() +
                '}';
    }
    
    /**
     * İlan geçerli mi kontrol et
     */
    public boolean isValid() {
        return ismi != null && !ismi.trim().isEmpty() &&
               fiyat != null && fiyat > 0 &&
               m2 != null && m2 > 0 &&
               odaSayisi != null && odaSayisi > 0 &&
               ownerId != null;
    }
    
    /**
     * İlan özet bilgisi
     */
    public String getSummary() {
        return String.format("%s - %,d₺ - %dm² - %d+1 oda", 
                            ismi, fiyat, m2, odaSayisi);
    }
}