package com.example.dostemlakprojesi;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class FavoriLinkedList {
    private static FavoriLinkedList instance;
    private FavoriNode head; // Tüm favorilerin başı
    
    public FavoriLinkedList() {
        head = null;
    }
    
    public static FavoriLinkedList getInstance() {
        if (instance == null) {
            instance = new FavoriLinkedList();
        }
        return instance;
    }
    
    // ⭐ Favoriye ekle
    public boolean favoriEkle(int kullaniciId, int ilanId) {
        // Zaten favori mi kontrol et
        if (favorideVarMi(kullaniciId, ilanId)) {
            System.out.println("❌ Bu ilan zaten favorilerde: " + ilanId);
            return false;
        }
        
        // Yeni favori node oluştur
        FavoriNode yeniFavori = new FavoriNode(kullaniciId, ilanId);
        
        // Başa ekle (en hızlı)
        if (head == null) {
            head = yeniFavori;
        } else {
            yeniFavori.next = head;
            head = yeniFavori;
        }
        
        System.out.println("✅ Favori eklendi: Kullanıcı " + kullaniciId + " -> İlan " + ilanId);
        return true;
    }
    
    // ⭐ Favoriden çıkar
    public boolean favoriCikar(int kullaniciId, int ilanId) {
        if (head == null) {
            System.out.println("❌ Favori listesi boş");
            return false;
        }
        
        // İlk node mu silinecek?
        if (head.kullaniciId == kullaniciId && head.ilanId == ilanId) {
            head = head.next;
            System.out.println("✅ Favori çıkarıldı: Kullanıcı " + kullaniciId + " -> İlan " + ilanId);
            return true;
        }
        
        // Ortadaki veya sondaki node'u bul ve sil
        FavoriNode current = head;
        while (current.next != null) {
            if (current.next.kullaniciId == kullaniciId && current.next.ilanId == ilanId) {
                current.next = current.next.next; // Node'u atla (sil)
                System.out.println("✅ Favori çıkarıldı: Kullanıcı " + kullaniciId + " -> İlan " + ilanId);
                return true;
            }
            current = current.next;
        }
        
        System.out.println("❌ Favori bulunamadı: Kullanıcı " + kullaniciId + " -> İlan " + ilanId);
        return false;
    }
    
    // ⭐ Favori var mı kontrol et
    public boolean favorideVarMi(int kullaniciId, int ilanId) {
        FavoriNode current = head;
        while (current != null) {
            if (current.kullaniciId == kullaniciId && current.ilanId == ilanId) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    // ⭐ Kullanıcının tüm favorilerini getir (ilanId'leri döndür)
    public KullaniciFavoriNode getKullaniciFavorileri(int kullaniciId) {
        KullaniciFavoriNode favoriHead = null;
        KullaniciFavoriNode favoriTail = null;
        
        FavoriNode current = head;
        while (current != null) {
            if (current.kullaniciId == kullaniciId) {
                // Yeni favori ilanı node'u oluştur
                KullaniciFavoriNode yeniFavori = new KullaniciFavoriNode(current.ilanId, current.eklenmeTarihi);
                
                if (favoriHead == null) {
                    favoriHead = yeniFavori;
                    favoriTail = yeniFavori;
                } else {
                    favoriTail.next = yeniFavori;
                    favoriTail = yeniFavori;
                }
            }
            current = current.next;
        }
        
        return favoriHead;
    }
    
    // ⭐ Kullanıcının favori sayısı
    public int getKullaniciFavoriSayisi(int kullaniciId) {
        int sayac = 0;
        FavoriNode current = head;
        while (current != null) {
            if (current.kullaniciId == kullaniciId) {
                sayac++;
            }
            current = current.next;
        }
        return sayac;
    }
    
    // ⭐ İlanı kaç kişi favorilemiş
    public int getIlanFavoriSayisi(int ilanId) {
        int sayac = 0;
        FavoriNode current = head;
        while (current != null) {
            if (current.ilanId == ilanId) {
                sayac++;
            }
            current = current.next;
        }
        return sayac;
    }
    
    // ⭐ Kullanıcının tüm favorilerini sil
    public int kullaniciFavorileriniTemizle(int kullaniciId) {
        int silinenSayisi = 0;
        
        // Baştan başlayarak sil
        while (head != null && head.kullaniciId == kullaniciId) {
            head = head.next;
            silinenSayisi++;
        }
        
        // Ortadan ve sondan sil
        FavoriNode current = head;
        while (current != null && current.next != null) {
            if (current.next.kullaniciId == kullaniciId) {
                current.next = current.next.next;
                silinenSayisi++;
            } else {
                current = current.next;
            }
        }
        
        System.out.println("🗑️ Kullanıcı " + kullaniciId + " için " + silinenSayisi + " favori silindi");
        return silinenSayisi;
    }
    
    // ⭐ Tüm favorileri listele (debug için)
    public void tumFavorileriListele() {
        System.out.println("📋 TÜM FAVORİLER:");
        FavoriNode current = head;
        int sayac = 0;
        while (current != null) {
            System.out.println((++sayac) + ". " + current);
            current = current.next;
        }
        if (sayac == 0) {
            System.out.println("❌ Hiç favori yok");
        }
    }
    
    // ⭐ Favori istatistikleri
    public Map<String, Integer> getFavoriIstatistikleri() {
        Map<String, Integer> istatistikler = new HashMap<>();
        int toplamFavori = 0;
        int aktifKullaniciSayisi = 0;
        
        // Kullanıcı sayısını hesaplamak için geçici set benzeri yapı
        boolean[] kullaniciVar = new boolean[10000]; // ID 10000'e kadar
        
        FavoriNode current = head;
        while (current != null) {
            toplamFavori++;
            if (!kullaniciVar[current.kullaniciId]) {
                kullaniciVar[current.kullaniciId] = true;
                aktifKullaniciSayisi++;
            }
            current = current.next;
        }
        
        istatistikler.put("toplamFavori", toplamFavori);
        istatistikler.put("aktifKullaniciSayisi", aktifKullaniciSayisi);
        
        return istatistikler;
    }
}
