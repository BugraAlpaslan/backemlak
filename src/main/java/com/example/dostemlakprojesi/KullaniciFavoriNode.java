// KullaniciFavoriNode.java - Sadece veri yapısı, Spring annotation yok
package com.example.dostemlakprojesi;

import java.time.LocalDateTime;

public class KullaniciFavoriNode {
    public int ilanId;
    public LocalDateTime eklenmeTarihi;
    public KullaniciFavoriNode next;
    
    // Constructor
    public KullaniciFavoriNode(int ilanId, LocalDateTime eklenmeTarihi) {
        this.ilanId = ilanId;
        this.eklenmeTarihi = eklenmeTarihi;
        this.next = null;
    }
    
    // Default constructor
    public KullaniciFavoriNode() {
        this.next = null;
    }
    
    // toString metodu (debug için)
    @Override
    public String toString() {
        return "İlan " + ilanId + " (" + eklenmeTarihi + ")";
    }
    
    // Getter metodları (ihtiyaç durumunda)
    public int getIlanId() {
        return ilanId;
    }
    
    public LocalDateTime getEklenmeTarihi() {
        return eklenmeTarihi;
    }
    
    public KullaniciFavoriNode getNext() {
        return next;
    }
    
    // Setter metodları (ihtiyaç durumunda)
    public void setIlanId(int ilanId) {
        this.ilanId = ilanId;
    }
    
    public void setEklenmeTarihi(LocalDateTime eklenmeTarihi) {
        this.eklenmeTarihi = eklenmeTarihi;
    }
    
    public void setNext(KullaniciFavoriNode next) {
        this.next = next;
    }
}