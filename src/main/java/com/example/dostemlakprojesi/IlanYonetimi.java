package com.example.dostemlakprojesi;

import java.util.HashMap;

public class IlanYonetimi {
    private static IlanYonetimi instance;
    private HashMap<Integer, IlanLinkedList> ilanHash = new HashMap<>();

    private IlanYonetimi() {}

    public HashMap<Integer, IlanLinkedList> getIlanHash() {
        return ilanHash;
    }


    public static IlanYonetimi getInstance() {
        if (instance == null) {
            instance = new IlanYonetimi();
        }
        return instance;
    }

    public void ilanEkle(int kullaniciId, Ilan ilan) {
        ilanHash.putIfAbsent(kullaniciId, new IlanLinkedList());
        ilanHash.get(kullaniciId).ilanEkle(ilan);
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
}
