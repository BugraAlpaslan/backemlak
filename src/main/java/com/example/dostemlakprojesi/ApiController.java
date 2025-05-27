// ApiController.java - PostConstruct alternatifi
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
    
    // Constructor
    public ApiController() {
        System.out.println("🚀 ApiController oluşturuluyor...");
    }
    
    // Her endpoint çağrıldığında test verilerini yükle (lazy initialization)
    private void ensureInitialized() {
        if (!initialized && ilanYonetimi != null && kullaniciListesi != null) {
            System.out.println("🔄 Test verileri yükleniyor (lazy init)...");
            initializeTestData();
            initialized = true;
            System.out.println("✅ ApiController başlatıldı - Tüm sistemler hazır");
        }
    }
    
    // ⭐ Sunucu durumu kontrolü endpoint'i
    @GetMapping("/status")
    public ResponseEntity<?> getServerStatus() {
        ensureInitialized(); // Lazy init
        
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("message", "DOSTemlak Backend Server aktif");
        status.put("timestamp", LocalDateTime.now());
        status.put("version", "1.0.0");
        
        // Sistem durumlarını kontrol et
        try {
            int ilanSayisi = ilanYonetimi != null ? ilanYonetimi.getAllIlanlar().size() : 0;
            status.put("ilanSayisi", ilanSayisi);
            status.put("services", Map.of(
                "ilanYonetimi", ilanYonetimi != null ? "aktif" : "pasif",
                "kullaniciListesi", kullaniciListesi != null ? "aktif" : "pasif",
                "favoriLinkedList", favoriLinkedList != null ? "aktif" : "pasif"
            ));
        } catch (Exception e) {
            status.put("error", e.getMessage());
        }
        
        System.out.println("🔍 Status endpoint çağrıldı");
        return ResponseEntity.ok(status);
    }
    
    // Test verilerini yükle
    private void initializeTestData() {
        try {
            // Test kullanıcıları
            if (kullaniciListesi != null) {
                kullaniciListesi.kullaniciEkle("admin", 123);
                kullaniciListesi.kullaniciEkle("test", 456);
                kullaniciListesi.kullaniciEkle("demo", 789);
                System.out.println("✅ Test kullanıcıları oluşturuldu");
            }
            
            // Test ilanları
            if (ilanYonetimi != null) {
                addTestListingsWithImages();
                System.out.println("✅ Test ilanları oluşturuldu");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Test verisi yükleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test ilanlarını oluştur
    private void addTestListingsWithImages() {
        String[] cities = {"İstanbul", "Ankara", "İzmir", "Bursa", "Antalya"};
        String[] districts = {"Şişli", "Beşiktaş", "Kadıköy", "Ümraniye", "Maltepe"};
        String[] neighborhoods = {"Merkez", "Çarşı", "Sahil", "Yeşil", "Modern"};
        String[] heatingTypes = {"central", "individual", "floor", "stove"};
        String[] furnishedTypes = {"unfurnished", "semi-furnished", "furnished"};
        String[] features = {"Havuz", "Asansör", "Otopark", "Güvenlik", "Balkon", "Teras"};
        
        // Mock resim pool'u
        String[] mockImages = {
            "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=800&h=600&fit=crop"
        };
        
        Random random = new Random();
        
        for (int i = 1; i <= 20; i++) {
            try {
                // Yeni ilan constructor kullan
                Ilan yeniIlan = new Ilan(
                    2000 + (i * 500) + random.nextInt(1000), // fiyat
                    80 + (i * 10) + random.nextInt(50),      // m2
                    random.nextInt(20),                       // binaYasi
                    2 + (i % 4),                              // odaSayisi
                    "İlan Sahibi " + i,                       // kimden
                    "Lüks Daire " + i + " - " + cities[i % cities.length], // ismi
                    "Bu modern ve konforlu daire, şehrin merkezinde yer alır. Tüm olanakları ile hayalinizdeki evi sunar. Modern tasarım, kaliteli malzemeler ve mükemmel konum.", // aciklama
                    cities[i % cities.length] + "/" + districts[i % districts.length] // konum
                );
                
                // Ek özellikler
                yeniIlan.bathrooms = 1 + (i % 3);
                yeniIlan.city = cities[i % cities.length];
                yeniIlan.district = districts[i % districts.length];
                yeniIlan.neighborhood = neighborhoods[i % neighborhoods.length];
                yeniIlan.floor = 1 + random.nextInt(10);
                yeniIlan.totalFloors = yeniIlan.floor + random.nextInt(5);
                yeniIlan.heatingType = heatingTypes[i % heatingTypes.length];
                yeniIlan.furnished = furnishedTypes[i % furnishedTypes.length];
                yeniIlan.parkingSpot = random.nextBoolean();
                yeniIlan.balcony = random.nextBoolean();
                yeniIlan.viewCount = random.nextInt(100) + 10;
                
                // Özellikler ekle
                List<String> ilanFeatures = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    String feature = features[random.nextInt(features.length)];
                    if (!ilanFeatures.contains(feature)) {
                        ilanFeatures.add(feature);
                    }
                }
                yeniIlan.features = ilanFeatures;
                
                // Çoklu resim ekle (2-4 resim arası)
                int resimSayisi = 2 + random.nextInt(3);
                for (int j = 0; j < resimSayisi; j++) {
                    String randomImage = mockImages[random.nextInt(mockImages.length)];
                    yeniIlan.fotoEkle(randomImage);
                }
                
                // İlanı kullanıcılara dağıt
                int kullaniciId = 1000 + (i % 3);
                ilanYonetimi.ilanEkle(kullaniciId, yeniIlan);
                
            } catch (Exception e) {
                System.err.println("❌ Test ilan " + i + " oluşturma hatası: " + e.getMessage());
            }
        }
        
        System.out.println("✅ 20 test ilanı oluşturuldu");
    }
    
    // ⭐ Ana endpoint'ler
    @GetMapping("/listings")
    public ResponseEntity<?> getListings() {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("📋 İlanlar isteniyor...");
            
            if (ilanYonetimi == null) {
                throw new RuntimeException("IlanYonetimi servisi bulunamadı");
            }
            
            List<Ilan> ilanlar = ilanYonetimi.getAllIlanlar();
            System.out.println("📋 " + ilanlar.size() + " ilan bulundu");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ilanlar);
            response.put("count", ilanlar.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ İlan listeleme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlanlar getirilemedi: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/listings/{id}")
    public ResponseEntity<?> getListingById(@PathVariable Long id) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("🔍 İlan aranıyor ID: " + id);
            
            if (ilanYonetimi == null) {
                throw new RuntimeException("IlanYonetimi servisi bulunamadı");
            }
            
            Ilan ilan = ilanYonetimi.getIlanById(id);
            
            if (ilan != null) {
                ilan.viewCount++;
                System.out.println("✅ İlan bulundu: " + ilan.ismi);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", ilan);
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ İlan bulunamadı ID: " + id);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "İlan bulunamadı");
                return ResponseEntity.status(404).body(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ İlan detay hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan getirilemedi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ⭐ Kullanıcı Authentication
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("🔑 Login isteği: " + request);
            
            if (kullaniciListesi == null) {
                throw new RuntimeException("KullaniciListesi servisi bulunamadı");
            }
            
            String kullaniciAdi = (String) request.get("username");
            String sifreStr = request.get("password").toString();
            
            if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Kullanıcı adı boş olamaz");
                return ResponseEntity.badRequest().body(response);
            }
            
            Integer sifre;
            try {
                sifre = Integer.parseInt(sifreStr.trim());
            } catch (NumberFormatException e) {
                sifre = Math.abs(sifreStr.hashCode());
                System.out.println("🔢 Şifre hash: " + sifre);
            }
            
            boolean gecerli = kullaniciListesi.tamEslesme(kullaniciAdi, sifre);
            
            if (gecerli) {
                int kullaniciId = kullaniciListesi.getKullaniciId(kullaniciAdi, sifre);
                System.out.println("✅ Giriş başarılı: " + kullaniciAdi + " (ID: " + kullaniciId + ")");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Giriş başarılı");
                response.put("user", Map.of(
                    "id", kullaniciId,
                    "username", kullaniciAdi
                ));
                
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ Giriş başarısız: " + kullaniciAdi);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Kullanıcı adı veya şifre yanlış");
                return ResponseEntity.status(401).body(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Login hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Giriş yapılamadı: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("📝 Register isteği: " + request);
            
            if (kullaniciListesi == null) {
                throw new RuntimeException("KullaniciListesi servisi bulunamadı");
            }
            
            String kullaniciAdi = (String) request.get("username");
            String sifreStr = request.get("password").toString();
            
            if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Kullanıcı adı boş olamaz");
                return ResponseEntity.badRequest().body(response);
            }
            
            Integer sifre;
            try {
                sifre = Integer.parseInt(sifreStr.trim());
            } catch (NumberFormatException e) {
                sifre = Math.abs(sifreStr.hashCode());
                System.out.println("🔢 Şifre hash: " + sifre);
            }
            
            boolean mevcutMu = kullaniciListesi.arama(kullaniciAdi, sifre);
            
            if (!mevcutMu) {
                kullaniciListesi.kullaniciEkle(kullaniciAdi, sifre);
                int kullaniciId = kullaniciListesi.getKullaniciId(kullaniciAdi, sifre);
                System.out.println("✅ Kullanıcı kaydedildi: " + kullaniciAdi + " (ID: " + kullaniciId + ")");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Kullanıcı başarıyla kaydedildi");
                response.put("user", Map.of(
                    "username", kullaniciAdi,
                    "id", kullaniciId
                ));
                
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ Kullanıcı zaten mevcut: " + kullaniciAdi);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Bu kullanıcı adı zaten alınmış");
                return ResponseEntity.status(409).body(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Register hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kayıt yapılamadı: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ⭐ İlan ekleme - Fotoğraf ekleme düzeltildi
    @PostMapping("/listings")
    public ResponseEntity<?> createListing(@RequestBody Map<String, Object> request) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("➕ Yeni ilan ekleniyor: " + request);
            
            if (ilanYonetimi == null) {
                throw new RuntimeException("IlanYonetimi servisi bulunamadı");
            }
            
            // Constructor ile ilan oluştur
            Ilan yeniIlan = new Ilan(
                getIntegerValue(request, "price"),
                getIntegerValue(request, "area"),
                getIntegerValue(request, "buildingAge", 0),
                getIntegerValue(request, "bedrooms"),
                getStringValue(request, "ownerName", "İlan Sahibi"),
                getStringValue(request, "title"),
                getStringValue(request, "description"),
                getStringValue(request, "location")
            );
            
            // Ek özellikler
            yeniIlan.bathrooms = getIntegerValue(request, "bathrooms", 1);
            yeniIlan.city = getStringValue(request, "city");
            yeniIlan.district = getStringValue(request, "district");
            yeniIlan.neighborhood = getStringValue(request, "neighborhood");
            yeniIlan.floor = getIntegerValue(request, "floor");
            yeniIlan.totalFloors = getIntegerValue(request, "totalFloors");
            yeniIlan.type = getStringValue(request, "type", "rent");
            yeniIlan.heatingType = getStringValue(request, "heatingType", "central");
            yeniIlan.furnished = getStringValue(request, "furnished", "unfurnished");
            yeniIlan.parkingSpot = getBooleanValue(request, "parkingSpot");
            yeniIlan.balcony = getBooleanValue(request, "balcony");
            
            @SuppressWarnings("unchecked")
            List<String> features = (List<String>) request.get("features");
            yeniIlan.features = features != null ? features : new ArrayList<>();
            
            // ⭐ FOTOĞRAF EKLEME - Düzeltildi
            System.out.println("📸 Fotoğraf ekleme işlemi başlıyor...");
            
            // 1. Ana resim URL'i (imageUrl)
            String imageUrl = getStringValue(request, "imageUrl");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                System.out.println("📸 Ana resim ekleniyor: " + imageUrl);
                yeniIlan.fotoEkle(imageUrl);
            }
            
            // 2. Images array'i (çoklu resim)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> images = (List<Map<String, Object>>) request.get("images");
            if (images != null && !images.isEmpty()) {
                System.out.println("📸 " + images.size() + " adet resim array'i işleniyor...");
                
                for (int i = 0; i < images.size(); i++) {
                    Map<String, Object> img = images.get(i);
                    String url = null;
                    
                    // URL'i farklı key'lerden almaya çalış
                    if (img.get("url") != null) {
                        url = img.get("url").toString();
                    } else if (img.get("imageUrl") != null) {
                        url = img.get("imageUrl").toString();
                    } else if (img.get("src") != null) {
                        url = img.get("src").toString();
                    }
                    
                    if (url != null && !url.isEmpty()) {
                        System.out.println("📸 Array resmi " + (i+1) + " ekleniyor: " + url);
                        yeniIlan.fotoEkle(url);
                    } else {
                        System.out.println("⚠️ Array resmi " + (i+1) + " boş, atlanıyor");
                    }
                }
            }
            
            // 3. Eğer hiç resim yoksa default resim ekle
            if (yeniIlan.getImageCount() == 0) {
                String defaultImage = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800&h=600&fit=crop";
                System.out.println("📸 Hiç resim yok, default resim ekleniyor: " + defaultImage);
                yeniIlan.fotoEkle(defaultImage);
            }
            
            System.out.println("✅ Toplam " + yeniIlan.getImageCount() + " resim eklendi");
            System.out.println("📸 Ana resim: " + yeniIlan.getImageUrl());
            
            // İlanı kaydet
            int kullaniciId = 1001; // Default kullanıcı
            ilanYonetimi.ilanEkle(kullaniciId, yeniIlan);
            
            System.out.println("✅ İlan başarıyla eklendi: " + yeniIlan.ismi + " (ID: " + yeniIlan.ilanID + ")");
            
            // Response'ta resim bilgilerini de döndür
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İlan başarıyla eklendi");
            response.put("data", yeniIlan);
            
            // Debug için ek bilgiler
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("imageCount", yeniIlan.getImageCount());
            debugInfo.put("mainImage", yeniIlan.getImageUrl());
            debugInfo.put("allImages", yeniIlan.getImageUrls());
            response.put("imageDebug", debugInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ İlan ekleme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan eklenemedi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ⭐ Favori sistem endpoint'leri
    @PostMapping("/favorites/add")
    public ResponseEntity<?> addToFavorites(@RequestBody Map<String, Object> request) {
        ensureInitialized(); // Lazy init
        
        try {
            if (favoriLinkedList == null) {
                throw new RuntimeException("FavoriLinkedList servisi bulunamadı");
            }
            
            int userId = getIntegerValue(request, "userId");
            int listingId = getIntegerValue(request, "listingId");
            
            System.out.println("💖 Favoriye ekleniyor: User " + userId + " -> Listing " + listingId);
            
            boolean success = favoriLinkedList.favoriEkle(userId, listingId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Favorilere eklendi" : "Bu ilan zaten favorilerinizde");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Favori ekleme hatası: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Favori eklenemedi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/favorites/remove")
    public ResponseEntity<?> removeFromFavorites(@RequestBody Map<String, Object> request) {
        ensureInitialized(); // Lazy init
        
        try {
            if (favoriLinkedList == null) {
                throw new RuntimeException("FavoriLinkedList servisi bulunamadı");
            }
            
            int userId = getIntegerValue(request, "userId");
            int listingId = getIntegerValue(request, "listingId");
            
            System.out.println("💔 Favoriden çıkarılıyor: User " + userId + " -> Listing " + listingId);
            
            boolean success = favoriLinkedList.favoriCikar(userId, listingId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Favorilerden çıkarıldı" : "Bu ilan favorilerinizde değil");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Favori çıkarma hatası: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Favori çıkarılamadı: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/favorites/check")
    public ResponseEntity<?> checkFavoriteStatus(@RequestParam int userId, @RequestParam int listingId) {
        ensureInitialized(); // Lazy init
        
        try {
            if (favoriLinkedList == null) {
                throw new RuntimeException("FavoriLinkedList servisi bulunamadı");
            }
            
            boolean isFavorite = favoriLinkedList.favorideVarMi(userId, listingId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorite", isFavorite);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Favori kontrol hatası: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Favori durumu kontrol edilemedi");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/favorites/user/{userId}")
    public ResponseEntity<?> getUserFavorites(@PathVariable int userId) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("💖 Kullanıcı favorileri isteniyor: " + userId);
            
            if (favoriLinkedList == null || ilanYonetimi == null) {
                throw new RuntimeException("Gerekli servisler bulunamadı");
            }
            
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
            
            System.out.println("✅ " + favoriIlanlar.size() + " favori ilan bulundu");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", favoriIlanlar);
            response.put("count", favoriIlanlar.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Kullanıcı favorileri hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Favori ilanlar getirilemedi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ⭐ Kullanıcının ilanları
    @GetMapping("/listings/user/{userId}")
    public ResponseEntity<?> getUserListings(@PathVariable Integer userId) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("👤 Kullanıcı ilanları isteniyor, User ID: " + userId);
            
            if (ilanYonetimi == null) {
                throw new RuntimeException("IlanYonetimi servisi bulunamadı");
            }
            
            List<Ilan> tumIlanlar = ilanYonetimi.getAllIlanlar();
            List<Ilan> kullaniciIlanlari = new ArrayList<>();
            
            for (Ilan ilan : tumIlanlar) {
                int ilanUserId = 1000 + ((ilan.ilanID - 1) % 3);
                if (ilanUserId == userId) {
                    kullaniciIlanlari.add(ilan);
                }
            }
            
            System.out.println("✅ Kullanıcı " + userId + " için " + kullaniciIlanlari.size() + " ilan bulundu");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", kullaniciIlanlari);
            response.put("userId", userId);
            response.put("count", kullaniciIlanlari.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Kullanıcı ilanları hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kullanıcı ilanları getirilemedi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ⭐ İlan sil
    @DeleteMapping("/listings/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("🗑️ İlan siliniyor, ID: " + id);
            
            if (ilanYonetimi == null) {
                throw new RuntimeException("IlanYonetimi servisi bulunamadı");
            }
            
            boolean success = ilanYonetimi.ilanSil(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "İlan başarıyla silindi" : "İlan bulunamadı");
            response.put("id", id);
            
            System.out.println(success ? "✅ İlan silindi: " + id : "❌ İlan bulunamadı: " + id);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ İlan silme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan silinemedi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ⭐ İlan düzenleme için veri getir
    @GetMapping("/listings/{id}/edit")
    public ResponseEntity<?> getListingForEdit(@PathVariable Long id) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("✏️ İlan düzenleme verileri isteniyor, ID: " + id);
            
            if (ilanYonetimi == null) {
                throw new RuntimeException("IlanYonetimi servisi bulunamadı");
            }
            
            Ilan ilan = ilanYonetimi.getIlanById(id);
            
            if (ilan != null) {
                System.out.println("✅ Düzenleme için ilan bulundu: " + ilan.ismi);
                
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
                
                // Resim URL'lerini ekle
                List<String> imageUrls = ilan.getImageUrls();
                editData.put("images", imageUrls);
                editData.put("imageUrl", ilan.getImageUrl());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", editData);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Düzenlenecek ilan bulunamadı");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ İlan düzenleme verisi hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan düzenleme verisi getirilemedi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ⭐ İlan güncelle
    @PutMapping("/listings/{id}")
    public ResponseEntity<?> updateListing(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        ensureInitialized(); // Lazy init
        
        try {
            System.out.println("✏️ İlan güncelleniyor, ID: " + id);
            System.out.println("📝 Güncelleme verileri: " + request);
            
            if (ilanYonetimi == null) {
                throw new RuntimeException("IlanYonetimi servisi bulunamadı");
            }
            
            Ilan ilan = ilanYonetimi.getIlanById(id);
            
            if (ilan == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Güncellenecek ilan bulunamadı");
                return ResponseEntity.status(404).body(response);
            }
            
            // İlan verilerini güncelle
            ilan.ismi = getStringValue(request, "title");
            ilan.aciklama = getStringValue(request, "description");
            ilan.fiyat = getIntegerValue(request, "price");
            ilan.m2 = getIntegerValue(request, "area");
            ilan.odaSayisi = getIntegerValue(request, "bedrooms");
            ilan.bathrooms = getIntegerValue(request, "bathrooms");
            ilan.binaYasi = getIntegerValue(request, "buildingAge");
            ilan.konum = getStringValue(request, "location");
            ilan.city = getStringValue(request, "city");
            ilan.district = getStringValue(request, "district");
            ilan.neighborhood = getStringValue(request, "neighborhood");
            ilan.floor = getIntegerValue(request, "floor");
            ilan.totalFloors = getIntegerValue(request, "totalFloors");
            ilan.type = getStringValue(request, "type");
            ilan.heatingType = getStringValue(request, "heatingType");
            ilan.furnished = getStringValue(request, "furnished");
            ilan.parkingSpot = getBooleanValue(request, "parkingSpot");
            ilan.balcony = getBooleanValue(request, "balcony");
            
            @SuppressWarnings("unchecked")
            List<String> features = (List<String>) request.get("features");
            ilan.features = features != null ? features : new ArrayList<>();
            
            System.out.println("✅ İlan başarıyla güncellendi: " + ilan.ismi);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İlan başarıyla güncellendi");
            response.put("data", ilan);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ İlan güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan güncellenemedi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ⭐ Helper metodlar
    private String getStringValue(Map<String, Object> map, String key) {
        return getStringValue(map, key, null);
    }
    
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        return value.toString().trim();
    }
    
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        return getIntegerValue(map, key, null);
    }
    
    private Integer getIntegerValue(Map<String, Object> map, String key, Integer defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return "true".equalsIgnoreCase((String) value);
        return false;
    }
}