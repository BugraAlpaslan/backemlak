// IlanYonetimi.java - Spring Boot ile uyumlu hale getirilmiş
package com.example.dostemlakprojesi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class IlanYonetimi {
    
    // Mevcut HashMap yapınızı koruyoruz (eski sistem ile uyumlu)
    private HashMap<Integer, IlanLinkedList> ilanHash = new HashMap<>();
    
    // JPA Repository - yeni sistem
    @Autowired(required = false)
    private IlanRepository ilanRepository;
    
    // Constructor
    public IlanYonetimi() {
        System.out.println("🏗️ IlanYonetimi servisi oluşturuluyor...");
    }

    public HashMap<Integer, IlanLinkedList> getIlanHash() {
        return ilanHash;
    }

    // Mevcut metodlarınızı koruyoruz
    public void ilanEkle(int kullaniciId, Ilan ilan) {
        // Eski sistem (LinkedList)
        ilanHash.putIfAbsent(kullaniciId, new IlanLinkedList());
        ilanHash.get(kullaniciId).ilanEkle(ilan);
        
        System.out.println("✅ İlan LinkedList'e eklendi: " + ilan.ismi + " (ID: " + ilan.ilanID + ")");
        
        // Yeni sistem (JPA) - aynı anda her ikisine de kaydeder
        if (ilanRepository != null) {
            try {
                ilanRepository.save(ilan);
                System.out.println("✅ İlan JPA'ya da kaydedildi");
            } catch (Exception e) {
                System.out.println("⚠️ JPA kayıt hatası (normal): " + e.getMessage());
            }
        }
    }

    public void kullaniciIlanlariniYazdir(int kullaniciId) {
        if (ilanHash.containsKey(kullaniciId)) {
            System.out.println("📋 Kullanıcı " + kullaniciId + " ilanları:");
            ilanHash.get(kullaniciId).yazdir();
        } else {
            System.out.println("❌ Bu kullanıcıya ait ilan yok: " + kullaniciId);
        }
    }

    public void tumIlanlariYazdir() {
        System.out.println("📋 TÜM İLANLAR:");
        for (int kullaniciId : ilanHash.keySet()) {
            System.out.println("👤 Kullanıcı ID: " + kullaniciId);
            ilanHash.get(kullaniciId).yazdir();
        }
    }
    
    // YENİ: React frontend için metodlar
    public List<Ilan> getAllIlanlar() {
        System.out.println("📋 Tüm ilanlar getiriliyor...");
        
        // Önce JPA'dan dene
        if (ilanRepository != null) {
            try {
                List<Ilan> jpaIlanlar = ilanRepository.findAll();
                if (!jpaIlanlar.isEmpty()) {
                    System.out.println("✅ JPA'dan " + jpaIlanlar.size() + " ilan bulundu");
                    return jpaIlanlar;
                }
            } catch (Exception e) {
                System.out.println("⚠️ JPA okuma hatası (HashMap'e geçiliyor): " + e.getMessage());
            }
        }
        
        // Fallback: HashMap'ten al
        List<Ilan> allIlanlar = new ArrayList<>();
        System.out.println("📦 HashMap'ten ilanlar toplanıyor...");
        
        for (Integer kullaniciId : ilanHash.keySet()) {
            IlanLinkedList list = ilanHash.get(kullaniciId);
            if (list != null && list.head != null) {
                IlanNode current = list.head;
                while (current != null) {
                    allIlanlar.add(current.ilan);
                    current = current.next;
                }
            }
        }
        
        System.out.println("✅ HashMap'ten " + allIlanlar.size() + " ilan bulundu");
        return allIlanlar;
    }
    
    public Ilan getIlanById(Long id) {
        System.out.println("🔍 İlan aranıyor ID: " + id);
        
        // JPA Repository ile dene
        if (ilanRepository != null) {
            try {
                Ilan ilan = ilanRepository.findById(id).orElse(null);
                if (ilan != null) {
                    System.out.println("✅ JPA'dan bulundu: " + ilan.ismi);
                    return ilan;
                }
            } catch (Exception e) {
                System.out.println("⚠️ JPA hatası (HashMap'e geçiliyor): " + e.getMessage());
            }
        }
        
        // Fallback: HashMap'ten ara
        System.out.println("🔍 HashMap'te aranıyor...");
        for (Integer kullaniciId : ilanHash.keySet()) {
            IlanLinkedList list = ilanHash.get(kullaniciId);
            if (list != null && list.head != null) {
                IlanNode current = list.head;
                while (current != null) {
                    // ID matching - hem int hem Long için
                    if (current.ilan.ilanID == id.intValue()) {
                        System.out.println("✅ HashMap'te bulundu: " + current.ilan.ismi);
                        return current.ilan;
                    }
                    current = current.next;
                }
            }
        }
        
        System.out.println("❌ İlan hiçbir yerde bulunamadı ID: " + id);
        
        // Debug: Mevcut ilanları listele
        System.out.println("📋 Mevcut ilanlar:");
        int sayac = 0;
        for (Integer kullaniciId : ilanHash.keySet()) {
            IlanLinkedList list = ilanHash.get(kullaniciId);
            if (list != null && list.head != null) {
                IlanNode current = list.head;
                while (current != null) {
                    sayac++;
                    System.out.println("   " + sayac + ". ID: " + current.ilan.ilanID + " - " + current.ilan.ismi);
                    current = current.next;
                }
            }
        }
        
        if (sayac == 0) {
            System.out.println("   ❌ Hiç ilan yok!");
        }
        
        return null;
    }
    
    public Page<Ilan> getIlanlarWithPaging(Pageable pageable) {
        if (ilanRepository != null) {
            try {
                return ilanRepository.findAll(pageable);
            } catch (Exception e) {
                System.out.println("⚠️ JPA sayfalama hatası: " + e.getMessage());
            }
        }
        return null;
    }
    
    public List<Ilan> searchIlanlar(String keyword) {
        System.out.println("🔍 İlan arama: " + keyword);
        
        if (ilanRepository != null) {
            try {
                List<Ilan> jpaResults = ilanRepository.findByIsmiContainingOrAciklamaContaining(keyword, keyword);
                if (!jpaResults.isEmpty()) {
                    System.out.println("✅ JPA'dan " + jpaResults.size() + " arama sonucu");
                    return jpaResults;
                }
            } catch (Exception e) {
                System.out.println("⚠️ JPA arama hatası: " + e.getMessage());
            }
        }
        
        // Fallback: HashMap'te ara
        List<Ilan> results = new ArrayList<>();
        for (Integer kullaniciId : ilanHash.keySet()) {
            IlanLinkedList list = ilanHash.get(kullaniciId);
            if (list != null && list.head != null) {
                IlanNode current = list.head;
                while (current != null) {
                    if (current.ilan.ismi.toLowerCase().contains(keyword.toLowerCase()) ||
                        current.ilan.aciklama.toLowerCase().contains(keyword.toLowerCase())) {
                        results.add(current.ilan);
                    }
                    current = current.next;
                }
            }
        }
        
        System.out.println("✅ HashMap'ten " + results.size() + " arama sonucu");
        return results;
    }
    
    // İlan sil - HashMap'ten gerçek silme
    public boolean ilanSil(Long id) {
        System.out.println("🗑️ İlan siliniyor, ID: " + id);
        
        // JPA ile dene
        if (ilanRepository != null) {
            try {
                if (ilanRepository.existsById(id)) {
                    ilanRepository.deleteById(id);
                    System.out.println("✅ JPA'dan silindi: " + id);
                }
            } catch (Exception e) {
                System.out.println("⚠️ JPA silme hatası: " + e.getMessage());
            }
        }
        
        // HashMap'ten de sil
        boolean bulundu = false;
        for (Integer kullaniciId : ilanHash.keySet()) {
            IlanLinkedList ilanListesi = ilanHash.get(kullaniciId);
            
            if (ilanListesi == null || ilanListesi.head == null) {
                continue;
            }
            
            // İlk node mu silinecek?
            if (ilanListesi.head.ilan.ilanID == id.intValue()) {
                ilanListesi.head = ilanListesi.head.next;
                bulundu = true;
                System.out.println("✅ HashMap başından silindi: " + id);
                break;
            }
            
            // Ortadaki veya sondaki node'u bul ve sil
            IlanNode current = ilanListesi.head;
            while (current != null && current.next != null) {
                if (current.next.ilan.ilanID == id.intValue()) {
                    current.next = current.next.next; // Node'u atla (sil)
                    bulundu = true;
                    System.out.println("✅ HashMap ortasından silindi: " + id);
                    break;
                }
                current = current.next;
            }
            
            if (bulundu) break;
        }
        
        if (!bulundu) {
            System.out.println("❌ Silinecek ilan bulunamadı: " + id);
        }
        
        return bulundu;
    }
    
    // İstatistikler
    public java.util.Map<String, Integer> getIstatistikler() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        
        int toplamIlan = 0;
        int kullaniciSayisi = ilanHash.size();
        
        for (IlanLinkedList list : ilanHash.values()) {
            if (list != null && list.head != null) {
                IlanNode current = list.head;
                while (current != null) {
                    toplamIlan++;
                    current = current.next;
                }
            }
        }
        
        stats.put("toplamIlan", toplamIlan);
        stats.put("kullaniciSayisi", kullaniciSayisi);
        
        System.out.println("📊 İstatistikler: " + toplamIlan + " ilan, " + kullaniciSayisi + " kullanıcı");
        return stats;
    }
}