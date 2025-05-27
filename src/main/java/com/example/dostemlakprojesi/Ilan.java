// src/main/java/com/example/dostemlakprojesi/Ilan.java - JSON serialization düzeltmesi
package com.example.dostemlakprojesi;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ilanlar") // Türkçe tablo adı
public class Ilan {
    // Mevcut alanlarınız - sadece JPA annotations eklendi
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int ilanID;
     private static int nextId = 1; // Static counter
    
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
    
    // Yeni alanlar (React frontend ile uyumlu) - HEPSİNİ EKLEDİM
    public String city;           // Şehir
    public String district;       // İlçe  
    public String neighborhood;   // Mahalle ← Bu eksikti, ekledim
    public Integer bathrooms = 1; // Banyo sayısı
    public Integer floor;         // Kat
    public Integer totalFloors;   // Toplam kat
    public String type = "rent";  // Kiralık/Satılık
    public String heatingType = "central"; // Isıtma
    public String furnished = "unfurnished"; // Eşya durumu
    public Boolean parkingSpot = false;
    public Boolean balcony = false;
    
    @ElementCollection
    @CollectionTable(name = "ilan_features", joinColumns = @JoinColumn(name = "ilan_id"))
    @Column(name = "feature")
    public List<String> features;
    
    // İlişkiler - mevcut FotoNode yapınızı koruduk
    // ⭐ JSON serialization'dan hariç tut - circular reference önlemek için
    @Transient
    @JsonIgnore
    public FotoNode fotoHead; // Mevcut foto linkedlist yapınız
    
    @Transient
    @JsonIgnore
    public Ilan next; // Mevcut linkedlist yapınız
    
    // JPA için owner ilişkisi - JSON'da gösterme
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    public KullaniciNode owner;
    
    // Timestamps
    @Column(name = "created_at")
    public LocalDateTime createdAt;
    
    public Integer viewCount = 0;
    
    // Constructors - Mevcut constructor'ınızı koruyoruz
        public Ilan(int fiyat, int m2, int binaYasi, int odaSayisi, String kimden, 
                String ismi, String aciklama, String konum) {
        this.ilanID = nextId++; // ⭐ Manuel ID ata
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
        
        System.out.println("🆔 Yeni ilan ID atandı: " + this.ilanID + " - " + ismi);
    }
    
    public Ilan() {
        this.ilanID = nextId++; // ⭐ Default constructor için de
        this.createdAt = LocalDateTime.now();
        this.features = new ArrayList<>();
        
        System.out.println("🆔 Boş ilan ID atandı: " + this.ilanID);
    }
    // Mevcut fotoEkle metodunuzu koruyoruz
    public void fotoEkle(String fotoPath) {
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
    
    // Yeni metod (detaylı bilgi ile - React upload için)
    public void fotoEkle(String fotoPath, String originalName, String contentType, Long fileSize) {
        FotoNode yeni = new FotoNode(fotoPath, originalName, contentType, fileSize);
        yeni.ilan = this; // İlişki kur

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

    // React için fotoları liste olarak al - JSON safe
    public List<String> getImageUrls() {
        List<String> imageUrls = new ArrayList<>();
        
        if (fotoHead != null) {
            FotoNode current = fotoHead;
            do {
                imageUrls.add(current.getUrl());
                current = current.next;
            } while (current != fotoHead && current != null);
        }
        
        return imageUrls;
    }

    // İlk resmin URL'ini al (React carousel için)
    public String getImageUrl() {
        if (fotoHead != null) {
            return fotoHead.getUrl();
        }
        return "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=400&h=300&fit=crop";
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