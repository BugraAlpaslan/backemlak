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
    
    public Ilan getIlanById(Long id) {
        if (ilanRepository != null) {
            try {
                return ilanRepository.findById(id).orElse(null);
            } catch (Exception e) {
                System.out.println("JPA okuma hatası: " + e.getMessage());
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
}