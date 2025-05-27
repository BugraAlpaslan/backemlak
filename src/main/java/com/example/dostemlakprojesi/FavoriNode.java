// FavoriNode.java - Sadece veri yapısı, Spring annotation yok
package com.example.dostemlakprojesi;

import java.time.LocalDateTime;

public class FavoriNode {
    public int kullaniciId;        // Hangi kullanıcının favorisi
    public int ilanId;             // Hangi ilan favorilendi
    public LocalDateTime eklenmeTarihi; // Ne zaman eklendi
    public FavoriNode next;        // Sonraki favori
    
    // Constructor
    public FavoriNode(int kullaniciId, int ilanId) {
        this.kullaniciId = kullaniciId;
        this.ilanId = ilanId;
        this.eklenmeTarihi = LocalDateTime.now();
        this.next = null;
    }
    
    // Default constructor
    public FavoriNode() {
        this.eklenmeTarihi = LocalDateTime.now();
        this.next = null;
    }
    
    // toString metodu (debug için)
    @Override
    public String toString() {
        return "Kullanıcı " + kullaniciId + " -> İlan " + ilanId + " (" + eklenmeTarihi + ")";
    }
    
    // Getter metodları
    public int getKullaniciId() {
        return kullaniciId;
    }
    
    public int getIlanId() {
        return ilanId;
    }
    
    public LocalDateTime getEklenmeTarihi() {
        return eklenmeTarihi;
    }
    
    public FavoriNode getNext() {
        return next;
    }
    
    // Setter metodları
    public void setKullaniciId(int kullaniciId) {
        this.kullaniciId = kullaniciId;
    }
    
    public void setIlanId(int ilanId) {
        this.ilanId = ilanId;
    }
    
    public void setEklenmeTarihi(LocalDateTime eklenmeTarihi) {
        this.eklenmeTarihi = eklenmeTarihi;
    }
    
    public void setNext(FavoriNode next) {
        this.next = next;
    }
}