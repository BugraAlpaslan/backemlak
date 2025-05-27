// src/main/java/com/example/dostemlakprojesi/ApiController.java
package com.example.dostemlakprojesi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ApiController {
    
    // Manuel instance - Autowired kullanmıyoruz
    private IlanYonetimi ilanYonetimi;
    private kullaniciLinkedList kullaniciListesi;
    
    // Constructor'da initialize et ve test verileri yükle
    public ApiController() {
        this.ilanYonetimi = IlanYonetimi.getInstance();
        this.kullaniciListesi = kullaniciLinkedList.getInstance();
        
        // Test verileri yükle
        initializeTestData();
        
        System.out.println("✅ ApiController başlatıldı - Manuel instance'lar oluşturuldu");
        System.out.println("📋 Test ilanları yüklendi");
        System.out.println("👥 Test kullanıcıları oluşturuldu");
        System.out.println("🌐 API Endpoints hazır:");
        System.out.println("   GET  /api/listings");
        System.out.println("   GET  /api/listings/{id}");
        System.out.println("   POST /api/listings");
        System.out.println("   POST /api/auth/login");
        System.out.println("   POST /api/auth/register");
    }
    
    // Test verileri yükle
    // ApiController.java - initializeTestData metodunda ID'leri manuel ata

private void initializeTestData() {
    try {
        System.out.println("🔄 Test verileri yükleniyor...");
        
        // Test kullanıcıları ekle
        kullaniciListesi.kullaniciEkle("admin", 123);
        kullaniciListesi.kullaniciEkle("test", 456);
        kullaniciListesi.kullaniciEkle("demo", 789);
        
        // Test ilanları oluştur ve ID'leri manuel ata
        Ilan ilan1 = new Ilan(15000, 120, 5, 3, "Emlak Ofisi", 
                               "Şişli'de Lüks 3+1 Daire", 
                               "Merkezi konumda, asansörlü binada modern 3+1 daire. Tüm ulaşım imkanlarına yakın.", 
                               "Şişli Merkez, İstanbul");
        ilan1.ilanID = 1; // ⭐ Manuel ID ata
        ilan1.city = "İstanbul";
        ilan1.district = "Şişli";
        ilan1.neighborhood = "Merkez Mahallesi";
        ilan1.bathrooms = 2;
        ilan1.floor = 4;
        ilan1.totalFloors = 8;
        ilan1.heatingType = "central";
        ilan1.furnished = "furnished";
        ilan1.parkingSpot = true;
        ilan1.balcony = true;
        ilan1.features.add("Asansör");
        ilan1.features.add("Güvenlik");
        ilan1.features.add("Otopark");
        ilan1.features.add("Balkon");
        ilan1.fotoEkle("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=400&h=300&fit=crop");
        
        Ilan ilan2 = new Ilan(8000, 85, 10, 2, "Emlak Ofisi", 
                               "Kadıköy'de Deniz Manzaralı 2+1", 
                               "Harika deniz manzarası olan, balkonlu 2+1 daire. Sahile 5 dakika yürüme mesafesi.", 
                               "Moda, Kadıköy, İstanbul");
        ilan2.ilanID = 2; // ⭐ Manuel ID ata
        ilan2.city = "İstanbul";
        ilan2.district = "Kadıköy";
        ilan2.neighborhood = "Moda Mahallesi";
        ilan2.bathrooms = 1;
        ilan2.floor = 6;
        ilan2.totalFloors = 10;
        ilan2.heatingType = "individual";
        ilan2.furnished = "semi-furnished";
        ilan2.balcony = true;
        ilan2.features.add("Deniz Manzarası");
        ilan2.features.add("Balkon");
        ilan2.features.add("Sahile Yakın");
        ilan2.fotoEkle("https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400&h=300&fit=crop");
        
        Ilan ilan3 = new Ilan(25000, 200, 2, 4, "Emlak Ofisi", 
                               "Bebek'te Boğaz Manzaralı Villa", 
                               "Boğaz'ın eşsiz manzarasına sahip, bahçeli lüks villa. Özel otopark ve güvenlik.", 
                               "Bebek, Beşiktaş, İstanbul");
        ilan3.ilanID = 3; // ⭐ Manuel ID ata
        ilan3.city = "İstanbul";
        ilan3.district = "Beşiktaş";
        ilan3.neighborhood = "Bebek Mahallesi";
        ilan3.bathrooms = 3;
        ilan3.floor = 1;
        ilan3.totalFloors = 2;
        ilan3.heatingType = "floor";
        ilan3.furnished = "furnished";
        ilan3.parkingSpot = true;
        ilan3.balcony = true;
        ilan3.features.add("Bahçe");
        ilan3.features.add("Boğaz Manzarası");
        ilan3.features.add("Otopark");
        ilan3.features.add("Güvenlik");
        ilan3.features.add("Lüks");
        ilan3.fotoEkle("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=400&h=300&fit=crop");
        
        Ilan ilan4 = new Ilan(12000, 110, 8, 3, "Emlak Ofisi", 
                               "Beyoğlu'nda Şehir Manzaralı Loft", 
                               "Modern loft tarzı daire, yüksek tavanlar ve geniş pencereler ile şehir manzarası.", 
                               "Galata, Beyoğlu, İstanbul");
        ilan4.ilanID = 4; // ⭐ Manuel ID ata
        ilan4.city = "İstanbul";
        ilan4.district = "Beyoğlu";
        ilan4.neighborhood = "Galata Mahallesi";
        ilan4.bathrooms = 2;
        ilan4.floor = 8;
        ilan4.totalFloors = 12;
        ilan4.heatingType = "central";
        ilan4.furnished = "unfurnished";
        ilan4.features.add("Loft");
        ilan4.features.add("Şehir Manzarası");
        ilan4.features.add("Yüksek Tavan");
        ilan4.fotoEkle("https://images.unsplash.com/photo-1560185127-6ed189bf02f4?w=400&h=300&fit=crop");
        
        Ilan ilan5 = new Ilan(6500, 75, 15, 1, "Emlak Ofisi", 
                               "Üsküdar'da Kompakt 1+1", 
                               "Tek kişi veya çift için ideal, merkezi konumda 1+1 daire. Ulaşım imkanları mükemmel.", 
                               "Üsküdar Merkez, İstanbul");
        ilan5.ilanID = 5; // ⭐ Manuel ID ata
        ilan5.city = "İstanbul";
        ilan5.district = "Üsküdar";
        ilan5.neighborhood = "Merkez Mahallesi";
        ilan5.bathrooms = 1;
        ilan5.floor = 3;
        ilan5.totalFloors = 5;
        ilan5.heatingType = "individual";
        ilan5.furnished = "furnished";
        ilan5.features.add("Merkezi Konum");
        ilan5.features.add("Ulaşım");
        ilan5.features.add("Kompakt");
        ilan5.fotoEkle("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=400&h=300&fit=crop");
        
        // İlanları ekle (kullanıcı ID 1'e)
        ilanYonetimi.ilanEkle(1, ilan1);
        ilanYonetimi.ilanEkle(1, ilan2);
        ilanYonetimi.ilanEkle(1, ilan3);
        ilanYonetimi.ilanEkle(1, ilan4);
        ilanYonetimi.ilanEkle(1, ilan5);
        
        System.out.println("✅ Test verileri başarıyla yüklendi:");
        System.out.println("   - 3 test kullanıcısı (admin/123, test/456, demo/789)");
        System.out.println("   - 5 test ilanı (ID: 1-5)");
        
        // ID'leri kontrol et
        System.out.println("🆔 Yüklenen ilan ID'leri:");
        for (IlanLinkedList list : ilanYonetimi.getIlanHash().values()) {
            IlanNode current = list.head;
            while (current != null) {
                System.out.println("   ID: " + current.ilan.ilanID + " - " + current.ilan.ismi);
                current = current.next;
            }
        }
        
        // Kullanıcıları listele
        System.out.println("👥 Yüklenen kullanıcılar:");
        kullaniciListesi.listele();
        
    } catch (Exception e) {
        System.err.println("❌ Test verisi yükleme hatası: " + e.getMessage());
        e.printStackTrace();
    }
}
    // CORS için OPTIONS metodunu handle et
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .build();
    }
    
    // İlanları listele
    @GetMapping("/listings")
    public ResponseEntity<?> getListings() {
        try {
            System.out.println("📋 İlanlar isteniyor...");
            List<Ilan> ilanlar = ilanYonetimi.getAllIlanlar();
            System.out.println("📋 " + ilanlar.size() + " ilan bulundu");
            
            if (!ilanlar.isEmpty()) {
                System.out.println("📋 İlk ilan örneği: " + ilanlar.get(0).ismi + " - " + ilanlar.get(0).fiyat + "₺");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ilanlar);
            response.put("count", ilanlar.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ İlan listeleme hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "İlanlar getirilemedi: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
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
                System.out.println("✅ İlan bulundu: " + ilan.ismi + " (Görüntülenme: " + ilan.viewCount + ")");
                
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
    
    // Yeni ilan ekle
    @PostMapping("/listings")
    public ResponseEntity<?> createListing(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("➕ Yeni ilan ekleniyor...");
            System.out.println("📝 Request data: " + request.keySet());
            
            // Yeni ilan oluştur
            Ilan yeniIlan = new Ilan();
            yeniIlan.ilanID = getNextAvailableId(); // Manuel ID ata
            System.out.println("🆔 Yeni ilan ID: " + yeniIlan.ilanID);
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
            yeniIlan.kimden = "Web Kullanıcısı"; // Default değer
            
            // Features ekle
            @SuppressWarnings("unchecked")
            List<String> features = (List<String>) request.get("features");
            yeniIlan.features = features != null ? features : new ArrayList<>();
            
            // Resim ekle
            String imageUrl = getStringValue(request, "imageUrl");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                yeniIlan.fotoEkle(imageUrl);
                System.out.println("📷 Resim eklendi: " + imageUrl);
            }
            
            // İlanı sisteme ekle (kullanıcı ID 1'e)
            int kullaniciId = 1; // TODO: Session'dan al
            ilanYonetimi.ilanEkle(kullaniciId, yeniIlan);
            
            System.out.println("✅ İlan başarıyla eklendi:");
            System.out.println("   - Başlık: " + yeniIlan.ismi);
            System.out.println("   - Fiyat: " + yeniIlan.fiyat + "₺");
            System.out.println("   - Alan: " + yeniIlan.m2 + "m²");
            System.out.println("   - Konum: " + yeniIlan.city + "/" + yeniIlan.district);
            
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
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Kullanıcı girişi
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("🔑 Login isteği alındı");
            System.out.println("📝 Request keys: " + request.keySet());
            
            String kullaniciAdi = (String) request.get("username");
            String sifreStr = request.get("password").toString();
            
            System.out.println("👤 Kullanıcı: " + kullaniciAdi);
            System.out.println("🔐 Şifre: " + sifreStr);
            
            if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
                System.out.println("❌ Kullanıcı adı boş");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Kullanıcı adı boş olamaz");
                return ResponseEntity.ok(response);
            }
            
            // Şifreyi integer'a çevir
            Integer sifre;
            try {
                sifre = Integer.parseInt(sifreStr.trim());
                System.out.println("🔢 Şifre integer: " + sifre);
            } catch (NumberFormatException e) {
                sifre = Math.abs(sifreStr.hashCode());
                System.out.println("🔢 Şifre hash: " + sifre);
            }
            
            // Kullanıcı kontrolü
            boolean gecerli = kullaniciListesi.tamEslesme(kullaniciAdi, sifre);
            System.out.println("✔️ Giriş geçerli mi: " + gecerli);
            
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
                System.out.println("👥 Mevcut kullanıcılar:");
                kullaniciListesi.listele();
                
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
            System.out.println("📝 Register isteği alındı");
            System.out.println("📝 Request keys: " + request.keySet());
            
            String kullaniciAdi = (String) request.get("username");
            String sifreStr = request.get("password").toString();
            
            System.out.println("👤 Yeni kullanıcı: " + kullaniciAdi);
            
            if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
                System.out.println("❌ Kullanıcı adı boş");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Kullanıcı adı boş olamaz");
                return ResponseEntity.ok(response);
            }
            
            // Şifreyi integer'a çevir
            Integer sifre;
            try {
                sifre = Integer.parseInt(sifreStr.trim());
                System.out.println("🔢 Şifre integer: " + sifre);
            } catch (NumberFormatException e) {
                sifre = Math.abs(sifreStr.hashCode());
                System.out.println("🔢 Şifre hash: " + sifre);
            }
            
            // Kullanıcı zaten var mı kontrol et
            boolean mevcutMu = kullaniciListesi.arama(kullaniciAdi, sifre);
            System.out.println("🔍 Kullanıcı mevcut mu: " + mevcutMu);
            
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
    
    // Sistem durumu kontrolü
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        try {
            List<Ilan> ilanlar = ilanYonetimi.getAllIlanlar();
            
            Map<String, Object> status = new HashMap<>();
            status.put("success", true);
            status.put("message", "Sistem çalışıyor");
            status.put("timestamp", new Date());
            status.put("totalListings", ilanlar.size());
            status.put("endpoints", Arrays.asList(
                "GET /api/listings",
                "GET /api/listings/{id}",
                "POST /api/listings",
                "POST /api/auth/login",
                "POST /api/auth/register",
                "GET /api/status"
            ));
            
            System.out.println("ℹ️ Sistem durumu sorgulandı - " + ilanlar.size() + " ilan mevcut");
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            System.err.println("❌ Status hatası: " + e.getMessage());
            
            Map<String, Object> status = new HashMap<>();
            status.put("success", false);
            status.put("message", "Sistem hatası: " + e.getMessage());
            status.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.badRequest().body(status);
        }
    }
        // ApiController.java'ya eklenecek filtreleme metodları

// Filtreleme endpoint'i - POST /api/listings/filter
@PostMapping("/listings/filter")
public ResponseEntity<?> filterListings(@RequestBody Map<String, Object> filters) {
    try {
        System.out.println("🔍 Filtreleme isteği alındı: " + filters);
        
        List<Ilan> allListings = ilanYonetimi.getAllIlanlar();
        List<Ilan> filteredListings = new ArrayList<>(allListings);
        
        // Fiyat filtresi
        if (filters.containsKey("minPrice") && filters.get("minPrice") != null) {
            Integer minPrice = getIntegerValue(filters, "minPrice");
            if (minPrice != null) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.fiyat >= minPrice)
                    .collect(Collectors.toList());
                System.out.println("💰 Min fiyat filtresi uygulandı: " + minPrice + "₺");
            }
        }
        
        if (filters.containsKey("maxPrice") && filters.get("maxPrice") != null) {
            Integer maxPrice = getIntegerValue(filters, "maxPrice");
            if (maxPrice != null) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.fiyat <= maxPrice)
                    .collect(Collectors.toList());
                System.out.println("💰 Max fiyat filtresi uygulandı: " + maxPrice + "₺");
            }
        }
        
        // Şehir filtresi
        if (filters.containsKey("city") && filters.get("city") != null) {
            String city = getStringValue(filters, "city").toLowerCase();
            if (!city.isEmpty()) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.city != null && ilan.city.toLowerCase().contains(city))
                    .collect(Collectors.toList());
                System.out.println("🏙️ Şehir filtresi uygulandı: " + city);
            }
        }
        
        // İlçe filtresi
        if (filters.containsKey("district") && filters.get("district") != null) {
            String district = getStringValue(filters, "district").toLowerCase();
            if (!district.isEmpty()) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.district != null && ilan.district.toLowerCase().contains(district))
                    .collect(Collectors.toList());
                System.out.println("🏘️ İlçe filtresi uygulandı: " + district);
            }
        }
        
        // Oda sayısı filtresi
        if (filters.containsKey("bedrooms") && filters.get("bedrooms") != null) {
            Integer bedrooms = getIntegerValue(filters, "bedrooms");
            if (bedrooms != null) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.odaSayisi == bedrooms)
                    .collect(Collectors.toList());
                System.out.println("🏠 Oda sayısı filtresi uygulandı: " + bedrooms);
            }
        }
        
        // Banyo sayısı filtresi
        if (filters.containsKey("bathrooms") && filters.get("bathrooms") != null) {
            Integer bathrooms = getIntegerValue(filters, "bathrooms");
            if (bathrooms != null) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.bathrooms != null && ilan.bathrooms == bathrooms)
                    .collect(Collectors.toList());
                System.out.println("🛁 Banyo sayısı filtresi uygulandı: " + bathrooms);
            }
        }
        
        // Alan filtresi (m²)
        if (filters.containsKey("minArea") && filters.get("minArea") != null) {
            Integer minArea = getIntegerValue(filters, "minArea");
            if (minArea != null) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.m2 >= minArea)
                    .collect(Collectors.toList());
                System.out.println("📐 Min alan filtresi uygulandı: " + minArea + " m²");
            }
        }
        
        if (filters.containsKey("maxArea") && filters.get("maxArea") != null) {
            Integer maxArea = getIntegerValue(filters, "maxArea");
            if (maxArea != null) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.m2 <= maxArea)
                    .collect(Collectors.toList());
                System.out.println("📐 Max alan filtresi uygulandı: " + maxArea + " m²");
            }
        }
        
        // İlan tipi filtresi (kiralık/satılık)
        if (filters.containsKey("type") && filters.get("type") != null) {
            String type = getStringValue(filters, "type");
            if (!type.isEmpty() && !type.equals("all")) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.type != null && ilan.type.equals(type))
                    .collect(Collectors.toList());
                System.out.println("🏷️ İlan tipi filtresi uygulandı: " + type);
            }
        }
        
        // Isıtma tipi filtresi
        if (filters.containsKey("heatingType") && filters.get("heatingType") != null) {
            String heatingType = getStringValue(filters, "heatingType");
            if (!heatingType.isEmpty() && !heatingType.equals("all")) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.heatingType != null && ilan.heatingType.equals(heatingType))
                    .collect(Collectors.toList());
                System.out.println("🔥 Isıtma tipi filtresi uygulandı: " + heatingType);
            }
        }
        
        // Eşya durumu filtresi
        if (filters.containsKey("furnished") && filters.get("furnished") != null) {
            String furnished = getStringValue(filters, "furnished");
            if (!furnished.isEmpty() && !furnished.equals("all")) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> ilan.furnished != null && ilan.furnished.equals(furnished))
                    .collect(Collectors.toList());
                System.out.println("🛋️ Eşya durumu filtresi uygulandı: " + furnished);
            }
        }
        
        // Özellik filtresi
        if (filters.containsKey("features") && filters.get("features") != null) {
            @SuppressWarnings("unchecked")
            List<String> requiredFeatures = (List<String>) filters.get("features");
            if (!requiredFeatures.isEmpty()) {
                filteredListings = filteredListings.stream()
                    .filter(ilan -> {
                        if (ilan.features == null) return false;
                        return ilan.features.containsAll(requiredFeatures);
                    })
                    .collect(Collectors.toList());
                System.out.println("⭐ Özellik filtresi uygulandı: " + requiredFeatures);
            }
        }
        
        // Sonuçları log'la
        System.out.println("✅ Filtreleme tamamlandı:");
        System.out.println("   - Toplam ilan: " + allListings.size());
        System.out.println("   - Filtrelenmiş ilan: " + filteredListings.size());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", filteredListings);
        response.put("totalCount", allListings.size());
        response.put("filteredCount", filteredListings.size());
        response.put("appliedFilters", filters);
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        System.err.println("❌ Filtreleme hatası: " + e.getMessage());
        e.printStackTrace();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Filtreleme başarısız: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}

// Arama endpoint'i - GET /api/listings/search?q=keyword
@GetMapping("/listings/search")
public ResponseEntity<?> searchListings(@RequestParam(required = false) String q) {
    try {
        System.out.println("🔍 Arama isteği: " + q);
        
        List<Ilan> allListings = ilanYonetimi.getAllIlanlar();
        
        if (q == null || q.trim().isEmpty()) {
            // Arama terimi yoksa tüm ilanları döndür
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", allListings);
            response.put("count", allListings.size());
            response.put("searchTerm", "");
            return ResponseEntity.ok(response);
        }
        
        String searchTerm = q.trim().toLowerCase();
        
        List<Ilan> searchResults = allListings.stream()
            .filter(ilan -> 
                (ilan.ismi != null && ilan.ismi.toLowerCase().contains(searchTerm)) ||
                (ilan.aciklama != null && ilan.aciklama.toLowerCase().contains(searchTerm)) ||
                (ilan.city != null && ilan.city.toLowerCase().contains(searchTerm)) ||
                (ilan.district != null && ilan.district.toLowerCase().contains(searchTerm)) ||
                (ilan.neighborhood != null && ilan.neighborhood.toLowerCase().contains(searchTerm))
            )
            .collect(Collectors.toList());
        
        System.out.println("✅ Arama tamamlandı:");
        System.out.println("   - Arama terimi: " + searchTerm);
        System.out.println("   - Bulunan ilan: " + searchResults.size());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", searchResults);
        response.put("count", searchResults.size());
        response.put("searchTerm", searchTerm);
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        System.err.println("❌ Arama hatası: " + e.getMessage());
        e.printStackTrace();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Arama başarısız: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}

// Filter değerlerini almak için endpoint - GET /api/listings/filter-options
@GetMapping("/listings/filter-options")
public ResponseEntity<?> getFilterOptions() {
    try {
        System.out.println("⚙️ Filter seçenekleri isteniyor...");
        
        List<Ilan> allListings = ilanYonetimi.getAllIlanlar();
        
        // Mevcut değerleri topla
        Set<String> cities = allListings.stream()
            .map(ilan -> ilan.city)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
            
        Set<String> districts = allListings.stream()
            .map(ilan -> ilan.district)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
            
        Set<String> heatingTypes = allListings.stream()
            .map(ilan -> ilan.heatingType)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
            
        Set<String> furnishedOptions = allListings.stream()
            .map(ilan -> ilan.furnished)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
            
        // Fiyat aralığı
        OptionalInt minPrice = allListings.stream().mapToInt(ilan -> ilan.fiyat).min();
        OptionalInt maxPrice = allListings.stream().mapToInt(ilan -> ilan.fiyat).max();
        
        // Alan aralığı
        OptionalInt minArea = allListings.stream().mapToInt(ilan -> ilan.m2).min();
        OptionalInt maxArea = allListings.stream().mapToInt(ilan -> ilan.m2).max();
        
        Map<String, Object> filterOptions = new HashMap<>();
        filterOptions.put("cities", cities);
        filterOptions.put("districts", districts);
        filterOptions.put("heatingTypes", heatingTypes);
        filterOptions.put("furnishedOptions", furnishedOptions);
        filterOptions.put("priceRange", Map.of(
            "min", minPrice.orElse(0),
            "max", maxPrice.orElse(100000)
        ));
        filterOptions.put("areaRange", Map.of(
            "min", minArea.orElse(0),
            "max", maxArea.orElse(1000)
        ));
        filterOptions.put("bedroomOptions", Arrays.asList(1, 2, 3, 4, 5, 6));
        filterOptions.put("bathroomOptions", Arrays.asList(1, 2, 3, 4, 5));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", filterOptions);
        
        System.out.println("✅ Filter seçenekleri hazırlandı");
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        System.err.println("❌ Filter seçenekleri hatası: " + e.getMessage());
        e.printStackTrace();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Filter seçenekleri alınamadı: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
    // Helper metodlar
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString().trim() : null;
    }
    
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof String) {
            try {
                String str = ((String) value).trim();
                if (str.isEmpty()) return null;
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Integer parse hatası '" + key + "': " + value);
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
    private int getNextAvailableId() {
    int maxId = 0;
    for (IlanLinkedList list : ilanYonetimi.getIlanHash().values()) {
        IlanNode current = list.head;
        while (current != null) {
            if (current.ilan.ilanID > maxId) {
                maxId = current.ilan.ilanID;
            }
            current = current.next;
        }
    }
    return maxId + 1;
}
}