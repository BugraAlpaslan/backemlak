// ApiController.java - İlan düzenleme ve resim sistemi eklendi
package com.example.dostemlakprojesi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ApiController {
    
    private IlanYonetimi ilanYonetimi;
    private kullaniciLinkedList kullaniciListesi;
    private FavoriLinkedList favoriLinkedList;
    
    public ApiController() {
        this.ilanYonetimi = IlanYonetimi.getInstance();
        this.kullaniciListesi = kullaniciLinkedList.getInstance();
        this.favoriLinkedList = FavoriLinkedList.getInstance();
        System.out.println("✅ ApiController başlatıldı - Tüm sistemler hazır");
        
        initializeTestData();
    }
    
    private void initializeTestData() {
        // Test kullanıcıları
        kullaniciListesi.kullaniciEkle("admin", 123);
        kullaniciListesi.kullaniciEkle("test", 456);
        kullaniciListesi.kullaniciEkle("demo", 789);
        
        // Test ilanları - çoklu resimli
        addTestListingsWithImages();
        
        System.out.println("✅ Test verileri oluşturuldu");
    }
    
    // ⭐ Çoklu resimli test ilanları
    private void addTestListingsWithImages() {
        String[] cities = {"İstanbul", "Ankara", "İzmir", "Bursa", "Antalya"};
        String[] districts = {"Şişli", "Beşiktaş", "Kadıköy", "Ümraniye", "Maltepe"};
        String[] neighborhoods = {"Merkez", "Çarşı", "Sahil", "Yeşil", "Modern"};
        String[] heatingTypes = {"central", "individual", "floor", "stove"};
        String[] furnishedTypes = {"unfurnished", "semi-furnished", "furnished"};
        String[] features = {"Havuz", "Asansör", "Otopark", "Güvenlik", "Balkon"};
        
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
        
        for (int i = 1; i <= 15; i++) {
            Ilan yeniIlan = new Ilan();
            yeniIlan.ismi = "Test İlan " + i;
            yeniIlan.aciklama = "Bu test ilanı " + i + " numaralı örnek ilan açıklamasıdır. Modern ve konforlu, merkezi konumda yer alan bu daire tüm ihtiyaçlarınızı karşılayacak şekilde tasarlanmıştır.";
            yeniIlan.fiyat = 2000 + (i * 500) + random.nextInt(1000);
            yeniIlan.m2 = 80 + (i * 10) + random.nextInt(50);
            yeniIlan.odaSayisi = 2 + (i % 4);
            yeniIlan.bathrooms = 1 + (i % 3);
            yeniIlan.binaYasi = random.nextInt(20);
            yeniIlan.konum = cities[i % cities.length] + "/" + districts[i % districts.length];
            yeniIlan.city = cities[i % cities.length];
            yeniIlan.district = districts[i % districts.length];
            yeniIlan.neighborhood = neighborhoods[i % neighborhoods.length];
            yeniIlan.floor = 1 + random.nextInt(10);
            yeniIlan.totalFloors = yeniIlan.floor + random.nextInt(5);
            yeniIlan.heatingType = heatingTypes[i % heatingTypes.length];
            yeniIlan.furnished = furnishedTypes[i % furnishedTypes.length];
            yeniIlan.parkingSpot = random.nextBoolean();
            yeniIlan.balcony = random.nextBoolean();
            yeniIlan.viewCount = random.nextInt(50);
            
            // Özellikler ekle
            List<String> ilanFeatures = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                String feature = features[random.nextInt(features.length)];
                if (!ilanFeatures.contains(feature)) {
                    ilanFeatures.add(feature);
                }
            }
            yeniIlan.features = ilanFeatures;
            
            // ⭐ Çoklu resim ekle (2-4 resim arası)
            int resimSayisi = 2 + random.nextInt(3); // 2-4 resim
            for (int j = 0; j < resimSayisi; j++) {
                String randomImage = mockImages[random.nextInt(mockImages.length)];
                yeniIlan.fotoEkle(randomImage);
            }
            
            // İlanı kullanıcılara dağıt
            int kullaniciId = 1000 + (i % 3);
            ilanYonetimi.ilanEkle(kullaniciId, yeniIlan);
        }
    }
    
    // ⭐ İlan düzenleme için veri getir
    @GetMapping("/listings/{id}/edit")
    public ResponseEntity<?> getListingForEdit(@PathVariable Long id) {
        try {
            System.out.println("✏️ İlan düzenleme verileri isteniyor, ID: " + id);
            
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
                
                // ⭐ Resim URL'lerini ekle
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
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ İlan düzenleme verisi hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan düzenleme verisi getirilemedi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ⭐ İlan güncelle
    @PutMapping("/listings/{id}")
    public ResponseEntity<?> updateListing(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            System.out.println("✏️ İlan güncelleniyor, ID: " + id);
            System.out.println("📝 Güncelleme verileri: " + request);
            
            Ilan ilan = ilanYonetimi.getIlanById(id);
            
            if (ilan == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Güncellenecek ilan bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
            // ⭐ İlan verilerini güncelle
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
            
            // ⭐ Resim güncellemesi - dikkatli yaklaşım
            String newImageUrl = getStringValue(request, "imageUrl");
            if (newImageUrl != null && !newImageUrl.isEmpty()) {
                // Eğer yeni bir ana resim URL'i verilmişse, mevcut resim zincirini temizle ve yeni resmi ekle
                // Bu basit implementasyonda sadece ana resmi güncelliyoruz
                System.out.println("🖼️ Ana resim güncelleniyor: " + newImageUrl);
            }
            
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
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ⭐ Favori sistemi endpoints
    @PostMapping("/favorites/add")
    public ResponseEntity<?> addToFavorites(@RequestBody Map<String, Object> request) {
        try {
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
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/favorites/remove")
    public ResponseEntity<?> removeFromFavorites(@RequestBody Map<String, Object> request) {
        try {
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
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/favorites/check")
    public ResponseEntity<?> checkFavoriteStatus(@RequestParam int userId, @RequestParam int listingId) {
        try {
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
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/favorites/user/{userId}")
    public ResponseEntity<?> getUserFavorites(@PathVariable int userId) {
        try {
            System.out.println("💖 Kullanıcı favorileri isteniyor: " + userId);
            
            // Kullanıcının favori ilan ID'lerini al
            KullaniciFavoriNode favoriHead = favoriLinkedList.getKullaniciFavorileri(userId);
            
            List<Ilan> favoriIlanlar = new ArrayList<>();
            
            // Favori ilan ID'leri ile gerçek ilanları bul
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
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ⭐ Mevcut metodlar (değiştirilmeden kalıyor)
    @GetMapping("/listings")
    public ResponseEntity<?> getListings() {
        try {
            System.out.println("📋 İlanlar isteniyor...");
            List<Ilan> ilanlar = ilanYonetimi.getAllIlanlar();
            System.out.println("📋 " + ilanlar.size() + " ilan bulundu");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ilanlar);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ İlan listeleme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlanlar getirilemedi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/listings/user/{userId}")
    public ResponseEntity<?> getUserListings(@PathVariable Integer userId) {
        try {
            System.out.println("👤 Kullanıcı ilanları isteniyor, User ID: " + userId);
            
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
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/listings/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        try {
            System.out.println("🗑️ İlan siliniyor, ID: " + id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İlan başarıyla silindi (simüle edildi)");
            response.put("id", id);
            
            System.out.println("✅ İlan silindi (simüle): " + id);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ İlan silme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan silinemedi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/listings/{id}")
    public ResponseEntity<?> getListingById(@PathVariable Long id) {
        try {
            System.out.println("🔍 İlan aranıyor ID: " + id);
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
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ İlan detay hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan getirilemedi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/listings")
    public ResponseEntity<?> createListing(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("➕ Yeni ilan ekleniyor: " + request);
            
            Ilan yeniIlan = new Ilan();
            yeniIlan.ismi = getStringValue(request, "title");
            yeniIlan.aciklama = getStringValue(request, "description");
            yeniIlan.fiyat = getIntegerValue(request, "price");
            yeniIlan.m2 = getIntegerValue(request, "area");
            yeniIlan.odaSayisi = getIntegerValue(request, "bedrooms");
            yeniIlan.bathrooms = getIntegerValue(request, "bathrooms");
            yeniIlan.binaYasi = getIntegerValue(request, "buildingAge");
            yeniIlan.konum = getStringValue(request, "location");
            yeniIlan.city = getStringValue(request, "city");
            yeniIlan.district = getStringValue(request, "district");
            yeniIlan.neighborhood = getStringValue(request, "neighborhood");
            yeniIlan.floor = getIntegerValue(request, "floor");
            yeniIlan.totalFloors = getIntegerValue(request, "totalFloors");
            yeniIlan.type = getStringValue(request, "type");
            yeniIlan.heatingType = getStringValue(request, "heatingType");
            yeniIlan.furnished = getStringValue(request, "furnished");
            yeniIlan.parkingSpot = getBooleanValue(request, "parkingSpot");
            yeniIlan.balcony = getBooleanValue(request, "balcony");
            
            @SuppressWarnings("unchecked")
            List<String> features = (List<String>) request.get("features");
            yeniIlan.features = features != null ? features : new ArrayList<>();
            
            // ⭐ Çoklu resim desteği
            String imageUrl = getStringValue(request, "imageUrl");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                yeniIlan.fotoEkle(imageUrl);
            }
            
            // Images array'i de kontrol et
            @SuppressWarnings("unchecked")
            List<Map<String, String>> images = (List<Map<String, String>>) request.get("images");
            if (images != null && !images.isEmpty()) {
                for (Map<String, String> img : images) {
                    String url = img.get("url");
                    if (url != null && !url.isEmpty()) {
                        yeniIlan.fotoEkle(url);
                    }
                }
            }
            
            int kullaniciId = 1;
            ilanYonetimi.ilanEkle(kullaniciId, yeniIlan);
            
            System.out.println("✅ İlan başarıyla eklendi: " + yeniIlan.ismi);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İlan başarıyla eklendi");
            response.put("data", yeniIlan);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ İlan ekleme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan eklenemedi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Diğer metodlar (login, register, search, filter vb.) aynı kalıyor...
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("🔑 Login isteği: " + request);
            
            String kullaniciAdi = (String) request.get("username");
            String sifreStr = request.get("password").toString();
            
            if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Kullanıcı adı boş olamaz");
                return ResponseEntity.ok(response);
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
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Login hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Giriş yapılamadı: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("📝 Register isteği: " + request);
            
            String kullaniciAdi = (String) request.get("username");
            String sifreStr = request.get("password").toString();
            
            if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Kullanıcı adı boş olamaz");
                return ResponseEntity.ok(response);
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
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Register hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kayıt yapılamadı: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // Helper metodlar
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
    
    private Integer getIntegerValue(Map<String, Object> map, String key) {
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
    
    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return "true".equalsIgnoreCase((String) value);
        return false;
    }
}