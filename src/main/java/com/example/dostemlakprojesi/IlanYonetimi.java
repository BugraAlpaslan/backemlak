// src/main/java/com/example/dostemlakprojesi/IlanYonetimi.java
package com.example.dostemlakprojesi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service // ← ZATEN VAR
public class IlanYonetimi {
    private static IlanYonetimi instance;
    
    // Mevcut HashMap yapınızı koruyoruz (eski sistem ile uyumlu)
    private HashMap<Integer, IlanLinkedList> ilanHash = new HashMap<>();
    
    // JPA Repository - yeni sistem
    @Autowired(required = false) // ← required = false ekledim
    private IlanRepository ilanRepository;
    
    public IlanYonetimi() {} // ← Constructor public yapın

    public HashMap<Integer, IlanLinkedList> getIlanHash() {
        return ilanHash;
    }

    public static IlanYonetimi getInstance() {
        if (instance == null) {
            instance = new IlanYonetimi();
        }
        return instance;
    }

    // Mevcut metodlarınızı koruyoruz
    public void ilanEkle(int kullaniciId, Ilan ilan) {
        // Eski sistem (LinkedList)
        ilanHash.putIfAbsent(kullaniciId, new IlanLinkedList());
        ilanHash.get(kullaniciId).ilanEkle(ilan);
        
        // Yeni sistem (JPA) - aynı anda her ikisine de kaydeder
        if (ilanRepository != null) {
            try {
                ilanRepository.save(ilan);
            } catch (Exception e) {
                System.out.println("JPA kayıt hatası: " + e.getMessage());
            }
        }
    }

    public void kullaniciIlanlariniYazdir(int kullaniciId) {
        if (ilanHash.containsKey(kullaniciId)) {
            ilanHash.get(kullaniciId).yazdir();
        } else {
            System.out.println("Bu kullanıcıya ait ilan yok.");
        }
    }

    public void tumIlanlariYazdir() {
        for (int kullaniciId : ilanHash.keySet()) {
            System.out.println("📍 Kullanıcı ID: " + kullaniciId);
            ilanHash.get(kullaniciId).yazdir();
        }
    }
    
    // YENİ: React frontend için metodlar
    public List<Ilan> getAllIlanlar() {
        if (ilanRepository != null) {
            try {
                return ilanRepository.findAll();
            } catch (Exception e) {
                System.out.println("JPA okuma hatası: " + e.getMessage());
            }
        }
        
        // Fallback: HashMap'ten al
        List<Ilan> allIlanlar = new ArrayList<>();
        for (IlanLinkedList list : ilanHash.values()) {
            IlanNode current = list.head;
            while (current != null) {
                allIlanlar.add(current.ilan);
                current = current.next;
            }
        }
        return allIlanlar;
    }
    
    // IlanYonetimi.java - getIlanById metodunu düzelt

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
            System.out.println("⚠️ JPA'da bulunamadı, HashMap'e bakılıyor...");
        } catch (Exception e) {
            System.out.println("❌ JPA hatası: " + e.getMessage());
        }
    }
    
    // Fallback: HashMap'ten ara
    System.out.println("🔍 HashMap'te aranıyor...");
    for (IlanLinkedList list : ilanHash.values()) {
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
    
    System.out.println("❌ İlan hiçbir yerde bulunamadı ID: " + id);
    
    // Debug: Mevcut ilanları listele
    System.out.println("📋 Mevcut ilanlar:");
    for (IlanLinkedList list : ilanHash.values()) {
        IlanNode current = list.head;
        while (current != null) {
            System.out.println("   ID: " + current.ilan.ilanID + " - " + current.ilan.ismi);
            current = current.next;
        }
    }
    
    return null;
}
    
    public Page<Ilan> getIlanlarWithPaging(Pageable pageable) {
        if (ilanRepository != null) {
            try {
                return ilanRepository.findAll(pageable);
            } catch (Exception e) {
                System.out.println("JPA sayfalama hatası: " + e.getMessage());
            }
        }
        return null;
    }
    
    public List<Ilan> searchIlanlar(String keyword) {
        if (ilanRepository != null) {
            try {
                return ilanRepository.findByIsmiContainingOrAciklamaContaining(keyword, keyword);
            } catch (Exception e) {
                System.out.println("JPA arama hatası: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }
    // IlanYonetimi.java'ya eklenecek metod

// ⭐ İlan sil - HashMap'ten gerçek silme
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
            System.out.println("❌ JPA silme hatası: " + e.getMessage());
        }
    }
    
    // HashMap'ten de sil
    boolean bulundu = false;
    for (int kullaniciId : ilanHash.keySet()) {
        IlanLinkedList ilanListesi = ilanHash.get(kullaniciId);
        
        // İlk node mu silinecek?
        if (ilanListesi.head != null && ilanListesi.head.ilan.ilanID == id.intValue()) {
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
}