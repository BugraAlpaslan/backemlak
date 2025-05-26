// src/main/java/com/example/dostemlakprojesi/KullaniciNode.java
package com.example.dostemlakprojesi;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kullanicilar") // Türkçe tablo adı
public class KullaniciNode {
    // Mevcut static field - JPA ile uyumlu hale getirdik
    public static int benzersizID = 1000;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    
    public int sifre;
    
    @Column(unique = true, length = 100)
    public String kullaniciAdi;
    
    // Mevcut LinkedList yapınız - JPA dışında tutuyoruz
    @Transient
    public KullaniciNode next;
    
    // JPA için ilişkiler
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Ilan> ilanlar;
    
    @Column(name = "created_at")
    public LocalDateTime createdAt;
    
    // Mevcut constructor'ınızı koruyoruz
    public KullaniciNode(String kullaniciAdi, int sifre) {
        this.id = benzersizID;
        benzersizID++;
        this.next = null;
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.createdAt = LocalDateTime.now();
    }
    
    // JPA için default constructor
    public KullaniciNode() {
        this.createdAt = LocalDateTime.now();
    }
    
    // React frontend için getters
    public Long getUserId() { return (long) id; }
    public String getUsername() { return kullaniciAdi; }
    public Integer getPassword() { return sifre; }
}