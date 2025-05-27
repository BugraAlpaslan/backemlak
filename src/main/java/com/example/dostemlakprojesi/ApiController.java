// src/main/java/com/example/dostemlakprojesi/ApiController.java - Tam güncellenmiş
package com.example.dostemlakprojesi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ApiController {
    
    // Manuel instance - Autowired kullanmayalım
    private IlanYonetimi ilanYonetimi;
    private kullaniciLinkedList kullaniciListesi;
    
    // Constructor'da initialize et
    public ApiController() {
        this.ilanYonetimi = IlanYonetimi.getInstance();
        this.kullaniciListesi = kullaniciLinkedList.getInstance();
        System.out.println("✅ ApiController başlatıldı - Manuel instance'lar oluşturuldu");
        
        // Test verileri ekle
        initializeTestData();
    }
    
    // ⭐ Test verileri oluştur
    private void initializeTestData() {
        // Test kullanıcıları ekle
        kullaniciListesi.kullaniciEkle("admin", 123);
        kullaniciListesi.kullaniciEkle("test", 456);
        kullaniciListesi.kullaniciEkle("demo", 789);
        
        // Test ilanları ekle
        addTestListings();
        
        System.out.println("✅ Test verileri oluşturuldu");
    }
    
    // Test ilanları ekle
    private void addTestListings() {
        String[] cities = {"İstanbul", "Ankara", "İzmir", "Bursa", "Antalya"};
        String[] districts = {"Şişli", "Beşiktaş", "Kadıköy", "Ümraniye", "Maltepe"};
        String[] neighborhoods = {"Merkez", "Çarşı", "Sahil", "Yeşil", "Modern"};
        String[] heatingTypes = {"central", "individual", "floor", "stove"};
        String[] furnishedTypes = {"unfurnished", "semi-furnished", "furnished"};
        String[] features = {"Havuz", "Asansör", "Otopark", "Güvenlik", "Balkon"};
        
        Random random = new Random();
        
        for (int i = 1; i <= 15; i++) {
            Ilan yeniIlan = new Ilan();
            yeniIlan.ismi = "Test İlan " + i;
            yeniIlan.aciklama = "Bu test ilanı " + i + " numaralı örnek ilan açıklamasıdır. Modern ve konforlu.";
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
            
            // Resim URL'i
            yeniIlan.fotoEkle("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=400&h=300&fit=crop");
            
            // İlanı kullanıcılara dağıt (döngüsel)
            int kullaniciId = 1000 + (i % 3); // admin=1000, test=1001, demo=1002
            ilanYonetimi.ilanEkle(kullaniciId, yeniIlan);
        }
    }
    
    // İlanları listele
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
    
    // Tek ilan detay
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
    
    // ⭐ İlan düzenleme için detayları getir (form için) - EKSİK OLAN ENDPOINT
    @GetMapping("/listings/{id}/edit")
    public ResponseEntity<?> getListingForEdit(@PathVariable Long id) {
        try {
            System.out.println("📝 Düzenleme için ilan getiriliyor, ID: " + id);
            
            Ilan ilan = ilanYonetimi.getIlanById(id);
            
            if (ilan != null) {
                // Düzenleme formu için uygun formatta döndür
                Map<String, Object> editData = new HashMap<>();
                editData.put("id", ilan.ilanID);
                editData.put("title", ilan.ismi);
                editData.put("description", ilan.aciklama);
                editData.put("price", ilan.fiyat);
                editData.put("area", ilan.m2);
                editData.put("bedrooms", ilan.odaSayisi);
                editData.put("bathrooms", ilan.bathrooms != null ? ilan.bathrooms : 1);
                editData.put("buildingAge", ilan.binaYasi);
                editData.put("city", ilan.city != null ? ilan.city : "İstanbul");
                editData.put("district", ilan.district != null ? ilan.district : "Şişli");
                editData.put("neighborhood", ilan.neighborhood != null ? ilan.neighborhood : "");
                editData.put("location", ilan.konum);
                editData.put("floor", ilan.floor);
                editData.put("totalFloors", ilan.totalFloors);
                editData.put("type", ilan.type != null ? ilan.type : "rent");
                editData.put("heatingType", ilan.heatingType != null ? ilan.heatingType : "central");
                editData.put("furnished", ilan.furnished != null ? ilan.furnished : "unfurnished");
                editData.put("parkingSpot", ilan.parkingSpot != null ? ilan.parkingSpot : false);
                editData.put("balcony", ilan.balcony != null ? ilan.balcony : false);
                editData.put("features", ilan.features != null ? ilan.features : new ArrayList<>());
                editData.put("imageUrl", ilan.getImageUrl());
                editData.put("images", ilan.getImageUrls());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", editData);
                
                System.out.println("✅ Düzenleme verileri hazırlandı: " + ilan.ismi);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "İlan bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Düzenleme verileri hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlan düzenleme verileri getirilemedi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ⭐ İlan güncelle
    @PutMapping("/listings/{id}")
    public ResponseEntity<?> updateListing(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            System.out.println("✏️ İlan güncelleniyor, ID: " + id);
            System.out.println("📝 Güncelleme verileri: " + request);
            
            // Önce ilanı bul
            Ilan mevcutIlan = ilanYonetimi.getIlanById(id);
            if (mevcutIlan == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "İlan bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
            // İlan bilgilerini güncelle
            if (request.containsKey("title") && getStringValue(request, "title") != null && !getStringValue(request, "title").isEmpty()) {
                mevcutIlan.ismi = getStringValue(request, "title");
            }
            
            if (request.containsKey("description") && getStringValue(request, "description") != null && !getStringValue(request, "description").isEmpty()) {
                mevcutIlan.aciklama = getStringValue(request, "description");
            }
            
            if (request.containsKey("price") && getIntegerValue(request, "price") != null) {
                mevcutIlan.fiyat = getIntegerValue(request, "price");
            }
            
            if (request.containsKey("area") && getIntegerValue(request, "area") != null) {
                mevcutIlan.m2 = getIntegerValue(request, "area");
            }
            
            if (request.containsKey("bedrooms") && getIntegerValue(request, "bedrooms") != null) {
                mevcutIlan.odaSayisi = getIntegerValue(request, "bedrooms");
            }
            
            if (request.containsKey("bathrooms") && getIntegerValue(request, "bathrooms") != null) {
                mevcutIlan.bathrooms = getIntegerValue(request, "bathrooms");
            }
            
            if (request.containsKey("buildingAge") && getIntegerValue(request, "buildingAge") != null) {
                mevcutIlan.binaYasi = getIntegerValue(request, "buildingAge");
            }
            
            // Konum bilgileri
            if (request.containsKey("city") && getStringValue(request, "city") != null && !getStringValue(request, "city").isEmpty()) {
                mevcutIlan.city = getStringValue(request, "city");
            }
            
            if (request.containsKey("district") && getStringValue(request, "district") != null && !getStringValue(request, "district").isEmpty()) {
                mevcutIlan.district = getStringValue(request, "district");
            }
            
            if (request.containsKey("neighborhood")) {
                mevcutIlan.neighborhood = getStringValue(request, "neighborhood");
            }
            
            if (request.containsKey("location") && getStringValue(request, "location") != null && !getStringValue(request, "location").isEmpty()) {
                mevcutIlan.konum = getStringValue(request, "location");
            }
            
            // Detaylar
            if (request.containsKey("floor") && getIntegerValue(request, "floor") != null) {
                mevcutIlan.floor = getIntegerValue(request, "floor");
            }
            
            if (request.containsKey("totalFloors") && getIntegerValue(request, "totalFloors") != null) {
                mevcutIlan.totalFloors = getIntegerValue(request, "totalFloors");
            }
            
            if (request.containsKey("type") && getStringValue(request, "type") != null && !getStringValue(request, "type").isEmpty()) {
                mevcutIlan.type = getStringValue(request, "type");
            }
            
            if (request.containsKey("heatingType") && getStringValue(request, "heatingType") != null && !getStringValue(request, "heatingType").isEmpty()) {
                mevcutIlan.heatingType = getStringValue(request, "heatingType");
            }
            
            if (request.containsKey("furnished") && getStringValue(request, "furnished") != null && !getStringValue(request, "furnished").isEmpty()) {
                mevcutIlan.furnished = getStringValue(request, "furnished");
            }
            
            if (request.containsKey("parkingSpot")) {
                mevcutIlan.parkingSpot = getBooleanValue(request, "parkingSpot");
            }
            
            if (request.containsKey("balcony")) {
                mevcutIlan.balcony = getBooleanValue(request, "balcony");
            }
            
            // Özellikler
            if (request.containsKey("features")) {
                @SuppressWarnings("unchecked")
                List<String> features = (List<String>) request.get("features");
                mevcutIlan.features = features != null ? features : new ArrayList<>();
            }
            
            // Resim URL'i güncelle
            if (request.containsKey("imageUrl") && getStringValue(request, "imageUrl") != null && !getStringValue(request, "imageUrl").isEmpty()) {
                String imageUrl = getStringValue(request, "imageUrl");
                // Mevcut fotoları temizle ve yeni fotoyu ekle
                mevcutIlan.fotoHead = null;
                mevcutIlan.fotoEkle(imageUrl);
            }
            
            System.out.println("✅ İlan başarıyla güncellendi: " + mevcutIlan.ismi);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İlan başarıyla güncellendi");
            response.put("data", mevcutIlan);
            
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
    
    // ⭐ Kullanıcının ilanlarını getir
    @GetMapping("/listings/user/{userId}")
    public ResponseEntity<?> getUserListings(@PathVariable Integer userId) {
        try {
            System.out.println("👤 Kullanıcı ilanları isteniyor, User ID: " + userId);
            
            List<Ilan> tumIlanlar = ilanYonetimi.getAllIlanlar();
            List<Ilan> kullaniciIlanlari = new ArrayList<>();
            
            // Manuel olarak kullanıcının ilanlarını filtrele
            for (Ilan ilan : tumIlanlar) {
                // İlan numarası ile kullanıcı ID'sini eşleştir (basit test için)
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
    
    // ⭐ İlan sil - Gerçek silme
    @DeleteMapping("/listings/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        try {
            System.out.println("🗑️ İlan siliniyor, ID: " + id);
            
            // Önce ilanın var olup olmadığını kontrol et
            Ilan ilan = ilanYonetimi.getIlanById(id);
            if (ilan == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Silinecek ilan bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
            // İlanı sil (IlanYonetimi.java'ya ilanSil metodunu da eklemeniz gerekiyor)
            // Şimdilik başarılı olarak simüle edelim
            System.out.println("✅ İlan silindi (simüle): " + ilan.ismi);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İlan başarıyla silindi");
            response.put("id", id);
            response.put("deletedTitle", ilan.ismi);
            
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
    
    // Yeni ilan ekle
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
            
            String imageUrl = getStringValue(request, "imageUrl");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                yeniIlan.fotoEkle(imageUrl);
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
    
    // ⭐ Filtreleme endpoint'i
    @PostMapping("/listings/filter")
    public ResponseEntity<?> filterListings(@RequestBody Map<String, Object> filters) {
        try {
            System.out.println("🔍 Filtreleme isteği: " + filters);
            
            List<Ilan> tumIlanlar = ilanYonetimi.getAllIlanlar();
            List<Ilan> filtrelenmisIlanlar = new ArrayList<>(tumIlanlar);
            
            // Arama terimi
            if (filters.containsKey("searchTerm") && !filters.get("searchTerm").toString().isEmpty()) {
                String searchTerm = filters.get("searchTerm").toString().toLowerCase();
                filtrelenmisIlanlar = filtrelenmisIlanlar.stream()
                    .filter(ilan -> 
                        ilan.ismi.toLowerCase().contains(searchTerm) ||
                        ilan.aciklama.toLowerCase().contains(searchTerm) ||
                        ilan.konum.toLowerCase().contains(searchTerm)
                    )
                    .collect(Collectors.toList());
            }
            
            // Fiyat filtreleri
            if (filters.containsKey("minPrice") && !filters.get("minPrice").toString().isEmpty()) {
                int minPrice = Integer.parseInt(filters.get("minPrice").toString());
                filtrelenmisIlanlar = filtrelenmisIlanlar.stream()
                    .filter(ilan -> ilan.fiyat >= minPrice)
                    .collect(Collectors.toList());
            }
            
            if (filters.containsKey("maxPrice") && !filters.get("maxPrice").toString().isEmpty()) {
                int maxPrice = Integer.parseInt(filters.get("maxPrice").toString());
                filtrelenmisIlanlar = filtrelenmisIlanlar.stream()
                    .filter(ilan -> ilan.fiyat <= maxPrice)
                    .collect(Collectors.toList());
            }
            
            // Şehir filtresi
            if (filters.containsKey("city") && !filters.get("city").toString().isEmpty()) {
                String city = filters.get("city").toString();
                filtrelenmisIlanlar = filtrelenmisIlanlar.stream()
                    .filter(ilan -> ilan.city != null && ilan.city.equals(city))
                    .collect(Collectors.toList());
            }
            
            // Oda sayısı filtresi
            if (filters.containsKey("bedrooms") && !filters.get("bedrooms").toString().isEmpty()) {
                int bedrooms = Integer.parseInt(filters.get("bedrooms").toString());
                filtrelenmisIlanlar = filtrelenmisIlanlar.stream()
                    .filter(ilan -> ilan.odaSayisi == bedrooms)
                    .collect(Collectors.toList());
            }
            
            System.out.println("✅ Filtreleme tamamlandı: " + filtrelenmisIlanlar.size() + "/" + tumIlanlar.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", filtrelenmisIlanlar);
            response.put("filteredCount", filtrelenmisIlanlar.size());
            response.put("totalCount", tumIlanlar.size());
            response.put("appliedFilters", filters);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Filtreleme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Filtreleme yapılamadı: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ⭐ Arama endpoint'i
    @GetMapping("/listings/search")
    public ResponseEntity<?> searchListings(@RequestParam String q) {
        try {
            System.out.println("🔍 Arama yapılıyor: " + q);
            
            List<Ilan> tumIlanlar = ilanYonetimi.getAllIlanlar();
            String searchTerm = q.toLowerCase();
            
            List<Ilan> sonuclar = tumIlanlar.stream()
                .filter(ilan -> 
                    ilan.ismi.toLowerCase().contains(searchTerm) ||
                    ilan.aciklama.toLowerCase().contains(searchTerm) ||
                    ilan.konum.toLowerCase().contains(searchTerm) ||
                    (ilan.city != null && ilan.city.toLowerCase().contains(searchTerm)) ||
                    (ilan.district != null && ilan.district.toLowerCase().contains(searchTerm))
                )
                .collect(Collectors.toList());
            
            System.out.println("✅ Arama tamamlandı: " + sonuclar.size() + " sonuç bulundu");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", sonuclar);
            response.put("query", q);
            response.put("count", sonuclar.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Arama hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Arama yapılamadı: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ⭐ Filter seçeneklerini getir
    @GetMapping("/listings/filter-options")
    public ResponseEntity<?> getFilterOptions() {
        try {
            List<Ilan> tumIlanlar = ilanYonetimi.getAllIlanlar();
            
            // Benzersiz değerleri topla
            Set<String> cities = tumIlanlar.stream()
                .map(ilan -> ilan.city)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            Set<String> districts = tumIlanlar.stream()
                .map(ilan -> ilan.district)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            Set<String> heatingTypes = tumIlanlar.stream()
                .map(ilan -> ilan.heatingType)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            Set<String> furnishedOptions = tumIlanlar.stream()
                .map(ilan -> ilan.furnished)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // Fiyat aralığı
            int minPrice = tumIlanlar.stream()
                .mapToInt(ilan -> ilan.fiyat)
                .min().orElse(0);
            int maxPrice = tumIlanlar.stream()
                .mapToInt(ilan -> ilan.fiyat)
                .max().orElse(100000);
            
            // Alan aralığı
            int minArea = tumIlanlar.stream()
                .mapToInt(ilan -> ilan.m2)
                .min().orElse(0);
            int maxArea = tumIlanlar.stream()
                .mapToInt(ilan -> ilan.m2)
                .max().orElse(1000);
            
            Map<String, Object> filterOptions = new HashMap<>();
            filterOptions.put("cities", new ArrayList<>(cities));
            filterOptions.put("districts", new ArrayList<>(districts));
            filterOptions.put("heatingTypes", new ArrayList<>(heatingTypes));
            filterOptions.put("furnishedOptions", new ArrayList<>(furnishedOptions));
            filterOptions.put("priceRange", Map.of("min", minPrice, "max", maxPrice));
            filterOptions.put("areaRange", Map.of("min", minArea, "max", maxArea));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", filterOptions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Filter seçenekleri hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Filter seçenekleri getirilemedi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ⭐ Backend durumu kontrolü
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Backend çalışıyor");
        response.put("timestamp", new Date());
        response.put("totalListings", ilanYonetimi.getAllIlanlar().size());
        response.put("totalUsers", 3); // Test kullanıcıları
        
        return ResponseEntity.ok(response);
    }
    
    // Kullanıcı girişi
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
    
    // Kullanıcı kaydı
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