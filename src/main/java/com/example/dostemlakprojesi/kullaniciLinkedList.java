// src/main/java/com/example/dostemlakprojesi/kullaniciLinkedList.java
package com.example.dostemlakprojesi;

import org.springframework.stereotype.Service; // ← BU SATIRI EKLEYİN
import org.springframework.beans.factory.annotation.Autowired; // ← BU SATIRI EKLEYİN

@Service // ← BU ANNOTATION'I EKLEYİN - Spring Bean yapar
public class kullaniciLinkedList {
    private static kullaniciLinkedList instance;
    KullaniciNode head;

    // JPA Repository (opsiyonel - gelecekte kullanmak için)
    @Autowired(required = false)
    private KullaniciRepository kullaniciRepository;

    public kullaniciLinkedList() {
        head = null;
    }

    public static kullaniciLinkedList getInstance() {
        if (instance == null) {
            instance = new kullaniciLinkedList();
        }
        return instance;
    }

    public int getKullaniciId(String kullaniciAdi, int sifre) {
        KullaniciNode temp = head;
        while (temp != null) {
            if (temp.kullaniciAdi.equals(kullaniciAdi) && temp.sifre == sifre) {
                return temp.id;
            }
            temp = temp.next;
        }
        return -1;
    }

    public void kullaniciEkle(String kullaniciAdi, int sifre) {
        KullaniciNode yeniKullanici = new KullaniciNode(kullaniciAdi, sifre);
        KullaniciNode temp = head;
        if (head == null) {
            head = yeniKullanici;
            return;
        }
        while(temp.next != null) {
            temp = temp.next;
        }
        temp.next = yeniKullanici;
        
        // JPA'ya da kaydet (eğer repository varsa)
        if (kullaniciRepository != null) {
            try {
                kullaniciRepository.save(yeniKullanici);
            } catch (Exception e) {
                System.out.println("JPA kayıt hatası: " + e.getMessage());
            }
        }
    }

    public void listele() {
        KullaniciNode temp = head;
        while (temp != null) {
            System.out.println("ID: " + temp.id + " | Kullanıcı Adı: " + temp.kullaniciAdi + " | Şifre: " + temp.sifre);
            temp = temp.next;
        }
    }

    public boolean arama(String kullaniciAdi, int sifre) {
        KullaniciNode temp = head;
        while(temp != null) {
            if (temp.kullaniciAdi.equals(kullaniciAdi) || temp.sifre == sifre) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    public boolean tamEslesme(String kullaniciAdi, int sifre) {
        KullaniciNode temp = head;
        while(temp != null) {
            if (temp.kullaniciAdi.equals(kullaniciAdi) && temp.sifre == sifre) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }
}