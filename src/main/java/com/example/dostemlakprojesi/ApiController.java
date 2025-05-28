// ApiController.java - Temiz ve Basit Versiyon
package com.example.dostemlakprojesi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class ApiController {
    
    @Autowired
    private IlanYonetimi ilanYonetimi;
    
    @Autowired
    private kullaniciLinkedList kullaniciListesi;
    
    @Autowired
    private FavoriLinkedList favoriLinkedList;
    
    private boolean initialized = false;
    
    // ========================================
    // SUNUCU DURUMU
    // ========================================
    
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        initializeIfNeeded();
        
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("message", "DOSTemlak Backend aktif");
        status.put("timestamp", LocalDateTime.now());
        status.put("ilanSayisi", ilanYonetimi.getAllIlanlar().size());
        
        return ResponseEntity.ok(status);
    }
    
    // ========================================
    // KULLANICI AUTHENTICATION
    // ========================================
    
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        initializeIfNeeded();
        
        try {
            String username = getString(request, "username");
            String passwordStr = getString(request, "password");
            
            if (username == null || username.trim().isEmpty()) {
                return error("Kullanıcı adı gerekli");
            }
            
            // Şifreyi integer'a çevir
            Integer password;
            try {
                password = Integer.parseInt(passwordStr.trim());
            } catch (NumberFormatException e) {
                password = Math.abs(passwordStr.hashCode());
            }
            
            // Kullanıcı kontrolü
            boolean valid = kullaniciListesi.tamEslesme(username, password);
            
            if (valid) {
                int userId = kullaniciListesi.getKullaniciId(username, password);
                
                Map<String, Object> user = new HashMap<>();
                user.put("id", userId);
                user.put("username", username);
                
                return success("Giriş başarılı", Map.of("user", user));
            } else {
                return error("Kullanıcı adı veya şifre yanlış");
            }
            
        } catch (Exception e) {
            return error("Giriş hatası: " + e.getMessage());
        }
    }
    
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        initializeIfNeeded();
        
        try {
            String username = getString(request, "username");
            String passwordStr = getString(request, "password");
            
            if (username == null || username.trim().isEmpty()) {
                return error("Kullanıcı adı gerekli");
            }
            
            Integer password;
            try {
                password = Integer.parseInt(passwordStr.trim());
            } catch (NumberFormatException e) {
                password = Math.abs(passwordStr.hashCode());
            }
            
            // Kullanıcı zaten var mı?
            boolean exists = kullaniciListesi.arama(username, password);
            
            if (!exists) {
                kullaniciListesi.kullaniciEkle(username, password);
                int userId = kullaniciListesi.getKullaniciId(username, password);
                
                Map<String, Object> user = new HashMap<>();
                user.put("id", userId);
                user.put("username", username);
                
                return success("Kullanıcı kaydedildi", Map.of("user", user));
            } else {
                return error("Bu kullanıcı adı zaten alınmış");
            }
            
        } catch (Exception e) {
            return error("Kayıt hatası: " + e.getMessage());
        }
    }
    
    // ========================================
    // İLAN İŞLEMLERİ
    // ========================================
    
    @GetMapping("/listings")
    public ResponseEntity<?> getAllListings() {
        initializeIfNeeded();
        
        try {
            List<Ilan> ilanlar = ilanYonetimi.getAllIlanlar();
            return success("İlanlar listelendi", ilanlar, ilanlar.size());
            
        } catch (Exception e) {
            return error("İlanlar getirilemedi: " + e.getMessage());
        }
    }
    
    @GetMapping("/listings/{id}")
    public ResponseEntity<?> getListingById(@PathVariable Long id) {
        initializeIfNeeded();
        
        try {
            Ilan ilan = ilanYonetimi.getIlanById(id);
            
            if (ilan != null) {
                ilan.viewCount++; // Görüntülenme sayısını artır
                return success("İlan bulundu", ilan);
            } else {
                return notFound("İlan bulunamadı");
            }
            
        } catch (Exception e) {
            return error("İlan getirilemedi: " + e.getMessage());
        }
    }
    
    @PostMapping("/listings")
    public ResponseEntity<?> createListing(@RequestBody Map<String, Object> request) {
        initializeIfNeeded();
        
        try {
            // Gerekli alanları kontrol et
            if (getString(request, "title") == null || getString(request, "title").trim().isEmpty()) {
                return error("İlan başlığı gerekli");
            }
            if (getInteger(request, "price") == null || getInteger(request, "price") <= 0) {
                return error("Geçerli fiyat gerekli");
            }
            
            // Kullanıcı ID'sini al - Frontend'den gelmeli
            Integer userId = getInteger(request, "userId");
            if (userId == null) {
                return error("Kullanıcı bilgisi gerekli");
            }
            
            // Yeni ilan oluştur
            Ilan ilan = new Ilan(
                getInteger(request, "price"),
                getInteger(request, "area", 100),
                getInteger(request, "buildingAge", 0),
                getInteger(request, "bedrooms", 2),
                getString(request, "ownerName", "İlan Sahibi"),
                getString(request, "title"),
                getString(request, "description", ""),
                getString(request, "location", "İstanbul")
            );
            
            // Kullanıcı ID'sini ata - ÖNEMLİ!
            ilan.ownerId = userId;
            
            // Ek bilgiler
            ilan.city = getString(request, "city", "İstanbul");
            ilan.district = getString(request, "district", "Merkez");
            ilan.neighborhood = getString(request, "neighborhood");
            ilan.bathrooms = getInteger(request, "bathrooms", 1);
            ilan.floor = getInteger(request, "floor");
            ilan.totalFloors = getInteger(request, "totalFloors");
            ilan.type = getString(request, "type", "rent");
            ilan.heatingType = getString(request, "heatingType", "central");
            ilan.furnished = getString(request, "furnished", "unfurnished");
            ilan.parkingSpot = getBoolean(request, "parkingSpot", false);
            ilan.balcony = getBoolean(request, "balcony", false);
            ilan.features = getStringList(request, "features");
            
            // Fotoğrafları ekle
            addImagesToListing(ilan, request);
            
            // İlanı kaydet
            ilanYonetimi.ilanEkle(userId, ilan);
            
            System.out.println("✅ Yeni ilan eklendi: " + ilan.ismi + " (ID: " + ilan.ilanID + ", User: " + userId + ")");
            
            return success("İlan eklendi", ilan);
            
        } catch (Exception e) {
            System.err.println("❌ İlan ekleme hatası: " + e.getMessage());
            return error("İlan eklenemedi: " + e.getMessage());
        }
    }
    
    @GetMapping("/listings/user/{userId}")
    public ResponseEntity<?> getUserListings(@PathVariable Integer userId) {
        initializeIfNeeded();
        
        try {
            List<Ilan> tumIlanlar = ilanYonetimi.getAllIlanlar();
            List<Ilan> kullaniciIlanlari = new ArrayList<>();
            
            System.out.println("👤 Kullanıcı " + userId + " ilanları aranıyor...");
            
            for (Ilan ilan : tumIlanlar) {
                // Kullanıcı ID eşleşmesi kontrolü
                if (ilan.ownerId != null && ilan.ownerId.equals(userId)) {
                    kullaniciIlanlari.add(ilan);
                    System.out.println("   ✅ İlan " + ilan.ilanID + ": " + ilan.ismi);
                }
            }
            
            System.out.println("📋 Kullanıcı " + userId + " için " + kullaniciIlanlari.size() + " ilan bulundu");
            
            return success("Kullanıcı ilanları", kullaniciIlanlari, kullaniciIlanlari.size());
            
        } catch (Exception e) {
            System.err.println("❌ Kullanıcı ilanları hatası: " + e.getMessage());
            return error("Kullanıcı ilanları getirilemedi: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/listings/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        initializeIfNeeded();
        
        try {
            boolean deleted = ilanYonetimi.ilanSil(id);
            
            if (deleted) {
                return success("İlan silindi", Map.of("id", id));
            } else {
                return notFound("Silinecek ilan bulunamadı");
            }
            
        } catch (Exception e) {
            return error("İlan silinemedi: " + e.getMessage());
        }
    }
    
    @GetMapping("/listings/{id}/edit")
    public ResponseEntity<?> getListingForEdit(@PathVariable Long id) {
        initializeIfNeeded();
        
        try {
            Ilan ilan = ilanYonetimi.getIlanById(id);
            
            if (ilan != null) {
                // Düzenleme için uygun format
                Map<String, Object> editData = new HashMap<>();
                editData.put("id", ilan.ilanID);
                editData.put("title", ilan.ismi);
                editData.put("description", ilan.aciklama);
                editData.put("price", ilan.fiyat);
                editData.put("area", ilan.m2);
                editData.put("bedrooms", ilan.odaSayisi);
                editData.put("bathrooms", ilan.bathrooms);
                editData.put("buildingAge", ilan.binaYasi);
                editData.put("city", ilan.city);
                editData.put("district", ilan.district);
                editData.put("neighborhood", ilan.neighborhood);
                editData.put("location", ilan.konum);
                editData.put("floor", ilan.floor);
                editData.put("totalFloors", ilan.totalFloors);
                editData.put("type", ilan.type);
                editData.put("heatingType", ilan.heatingType);
                editData.put("furnished", ilan.furnished);
                editData.put("parkingSpot", ilan.parkingSpot);
                editData.put("balcony", ilan.balcony);
                editData.put("features", ilan.features);
                editData.put("images", ilan.getImageUrls());
                editData.put("imageUrl", ilan.getImageUrl());
                
                return success("Düzenleme verileri", editData);
            } else {
                return notFound("Düzenlenecek ilan bulunamadı");
            }
            
        } catch (Exception e) {
            return error("Düzenleme verileri getirilemedi: " + e.getMessage());
        }
    }
    
    @PutMapping("/listings/{id}")
    public ResponseEntity<?> updateListing(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        initializeIfNeeded();
        
        try {
            Ilan ilan = ilanYonetimi.getIlanById(id);
            
            if (ilan == null) {
                return notFound("Güncellenecek ilan bulunamadı");
            }
            
            // İlan verilerini güncelle
            ilan.ismi = getString(request, "title", ilan.ismi);
            ilan.aciklama = getString(request, "description", ilan.aciklama);
            ilan.fiyat = getInteger(request, "price", ilan.fiyat);
            ilan.m2 = getInteger(request, "area", ilan.m2);
            ilan.odaSayisi = getInteger(request, "bedrooms", ilan.odaSayisi);
            ilan.bathrooms = getInteger(request, "bathrooms", ilan.bathrooms);
            ilan.binaYasi = getInteger(request, "buildingAge", ilan.binaYasi);
            ilan.konum = getString(request, "location", ilan.konum);
            ilan.city = getString(request, "city", ilan.city);
            ilan.district = getString(request, "district", ilan.district);
            ilan.neighborhood = getString(request, "neighborhood", ilan.neighborhood);
            ilan.floor = getInteger(request, "floor", ilan.floor);
            ilan.totalFloors = getInteger(request, "totalFloors", ilan.totalFloors);
            ilan.type = getString(request, "type", ilan.type);
            ilan.heatingType = getString(request, "heatingType", ilan.heatingType);
            ilan.furnished = getString(request, "furnished", ilan.furnished);
            ilan.parkingSpot = getBoolean(request, "parkingSpot", ilan.parkingSpot);
            ilan.balcony = getBoolean(request, "balcony", ilan.balcony);
            ilan.features = getStringList(request, "features", ilan.features);
            
            return success("İlan güncellendi", ilan);
            
        } catch (Exception e) {
            return error("İlan güncellenemedi: " + e.getMessage());
        }
    }
    
    // ========================================
    // FAVORİ İŞLEMLERİ
    // ========================================
    
    @PostMapping("/favorites/add")
    public ResponseEntity<?> addToFavorites(@RequestBody Map<String, Object> request) {
        initializeIfNeeded();
        
        try {
            Integer userId = getInteger(request, "userId");
            Integer listingId = getInteger(request, "listingId");
            
            if (userId == null || listingId == null) {
                return error("UserId ve listingId gerekli");
            }
            
            boolean added = favoriLinkedList.favoriEkle(userId, listingId);
            
            if (added) {
                return success("Favorilere eklendi", null);
            } else {
                return error("Bu ilan zaten favorilerinizde");
            }
            
        } catch (Exception e) {
            return error("Favori eklenemedi: " + e.getMessage());
        }
    }
    
    @PostMapping("/favorites/remove")
    public ResponseEntity<?> removeFromFavorites(@RequestBody Map<String, Object> request) {
        initializeIfNeeded();
        
        try {
            Integer userId = getInteger(request, "userId");
            Integer listingId = getInteger(request, "listingId");
            
            if (userId == null || listingId == null) {
                return error("UserId ve listingId gerekli");
            }
            
            boolean removed = favoriLinkedList.favoriCikar(userId, listingId);
            
            if (removed) {
                return success("Favorilerden çıkarıldı", null);
            } else {
                return error("Bu ilan favorilerinizde değil");
            }
            
        } catch (Exception e) {
            return error("Favori çıkarılamadı: " + e.getMessage());
        }
    }
    
    @GetMapping("/favorites/check")
    public ResponseEntity<?> checkFavorite(@RequestParam Integer userId, @RequestParam Integer listingId) {
        initializeIfNeeded();
        
        try {
            boolean isFavorite = favoriLinkedList.favorideVarMi(userId, listingId);
            return success("Favori durumu", Map.of("isFavorite", isFavorite));
            
        } catch (Exception e) {
            return error("Favori durumu kontrol edilemedi: " + e.getMessage());
        }
    }
    
    @GetMapping("/favorites/user/{userId}")
    public ResponseEntity<?> getUserFavorites(@PathVariable Integer userId) {
        initializeIfNeeded();
        
        try {
            KullaniciFavoriNode favoriHead = favoriLinkedList.getKullaniciFavorileri(userId);
            List<Ilan> favoriIlanlar = new ArrayList<>();
            
            KullaniciFavoriNode current = favoriHead;
            while (current != null) {
                Ilan ilan = ilanYonetimi.getIlanById((long) current.ilanId);
                if (ilan != null) {
                    favoriIlanlar.add(ilan);
                }
                current = current.next;
            }
            
            return success("Kullanıcı favorileri", favoriIlanlar, favoriIlanlar.size());
            
        } catch (Exception e) {
            return error("Favori ilanlar getirilemedi: " + e.getMessage());
        }
    }
    
    // ========================================
    // DEBUG
    // ========================================
    
    @GetMapping("/debug/listings")
    public ResponseEntity<?> debugListings() {
        initializeIfNeeded();
        
        try {
            List<Ilan> tumIlanlar = ilanYonetimi.getAllIlanlar();
            
            Map<String, Object> debug = new HashMap<>();
            debug.put("totalListings", tumIlanlar.size());
            
            List<Map<String, Object>> ilanlar = new ArrayList<>();
            Map<Integer, Integer> kullaniciIlanSayilari = new HashMap<>();
            
            for (Ilan ilan : tumIlanlar) {
                Map<String, Object> ilanInfo = new HashMap<>();
                ilanInfo.put("ilanID", ilan.ilanID);
                ilanInfo.put("ismi", ilan.ismi);
                ilanInfo.put("ownerId", ilan.ownerId);
                ilanInfo.put("createdAt", ilan.createdAt);
                ilanlar.add(ilanInfo);
                
                // Kullanıcı ilan sayılarını say
                if (ilan.ownerId != null) {
                    kullaniciIlanSayilari.put(ilan.ownerId, 
                        kullaniciIlanSayilari.getOrDefault(ilan.ownerId, 0) + 1);
                }
            }
            
            debug.put("listings", ilanlar);
            debug.put("listingsByUser", kullaniciIlanSayilari);
            
            return ResponseEntity.ok(debug);
            
        } catch (Exception e) {
            return error("Debug hatası: " + e.getMessage());
        }
    }
    
    // ========================================
    // HELPER METODLARı
    // ========================================
    
    private void initializeIfNeeded() {
        if (!initialized) {
            System.out.println("🔄 Test verileri yükleniyor...");
            initTestData();
            initialized = true;
            System.out.println("✅ API Controller hazır!");
        }
    }
    
    private void initTestData() {
        // Test kullanıcıları
        kullaniciListesi.kullaniciEkle("admin", 123);
        kullaniciListesi.kullaniciEkle("test", 456);
        kullaniciListesi.kullaniciEkle("demo", 789);
        
        // Test ilanları - her birine doğru kullanıcı ID'si ata
        String[] testTitles = {
            "Modern Şişli Dairesi", "Beşiktaş'ta Lüks Ev", "Kadıköy Bahçeli Villa", 
            "Ümraniye Yeni Konut", "Maltepe Deniz Manzaralı"
        };
        
        for (int i = 0; i < testTitles.length; i++) {
            Ilan testIlan = new Ilan(
                2000 + (i * 500), // fiyat
                100 + (i * 20),   // m2
                i + 1,            // yaş
                2 + (i % 3),      // oda
                "Test Sahip " + (i+1),
                testTitles[i],
                "Bu modern daire şehrin merkezinde yer alır ve tüm ihtiyaçlarınızı karşılar.",
                "İstanbul/Merkez"
            );
            
            // Kullanıcı ID'sini ata (1000, 1001, 1002 döngüsü)
            testIlan.ownerId = 1000 + (i % 3);
            
            // Test resmi ekle
            testIlan.fotoEkle("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800&h=600&fit=crop");
            
            // İlanı kaydet
            ilanYonetimi.ilanEkle(testIlan.ownerId, testIlan);
        }
        
        System.out.println("✅ " + testTitles.length + " test ilanı oluşturuldu");
    }
    
    private void addImagesToListing(Ilan ilan, Map<String, Object> request) {
        // Ana resim
        String imageUrl = getString(request, "imageUrl");
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            ilan.fotoEkle(imageUrl);
        }
        
        // Resim array'i
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> images = (List<Map<String, Object>>) request.get("images");
        if (images != null) {
            for (Map<String, Object> img : images) {
                String url = getString(img, "url");
                if (url != null && !url.trim().isEmpty()) {
                    ilan.fotoEkle(url);
                }
            }
        }
        
        // Hiç resim yoksa default ekle
        if (ilan.getImageCount() == 0) {
            ilan.fotoEkle("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800&h=600&fit=crop");
        }
    }
    
    // Response helper'ları
    private ResponseEntity<?> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<?> success(String message, Object data, int count) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("count", count);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<?> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }
    
    private ResponseEntity<?> notFound(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(404).body(response);
    }
    
    // Data extraction helper'ları
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString().trim() : null;
    }
    
    private String getString(Map<String, Object> map, String key, String defaultValue) {
        String value = getString(map, key);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }
    
    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private Integer getInteger(Map<String, Object> map, String key, Integer defaultValue) {
        Integer value = getInteger(map, key);
        return value != null ? value : defaultValue;
    }
    
    private Boolean getBoolean(Map<String, Object> map, String key, Boolean defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return "true".equalsIgnoreCase((String) value);
        return defaultValue;
    }
    
    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> map, String key, List<String> defaultValue) {
        List<String> value = getStringList(map, key);
        return !value.isEmpty() ? value : defaultValue;
    }
}