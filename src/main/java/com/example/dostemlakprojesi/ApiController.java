// src/main/java/com/example/dostemlakprojesi/ApiController.java
package com.example.dostemlakprojesi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

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
                return ResponseEntity.ok(response); // 200 OK döndür
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
                return ResponseEntity.ok(response); // 200 OK döndür
            }
            
        } catch (Exception e) {
            System.err.println("❌ Login hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Giriş yapılamadı: " + e.getMessage());
            return ResponseEntity.ok(response); // 200 OK döndür
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
                return ResponseEntity.ok(response); // 200 OK döndür
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
                return ResponseEntity.ok(response); // 200 OK döndür
            }
            
        } catch (Exception e) {
            System.err.println("❌ Register hatası: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kayıt yapılamadı: " + e.getMessage());
            return ResponseEntity.ok(response); // 200 OK döndür
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