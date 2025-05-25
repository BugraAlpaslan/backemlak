package com.example.dostemlakprojesi;

public class Ilan {
    public static int benzersizIlanId = 1;
    int ilanID, fiyat, m2, binaYasi, odaSayisi;
    String kimden, ismi, aciklama, konum;
    FotoNode fotoHead;
    Ilan next;

    public Ilan(int fiyat, int m2, int binaYasi, int odaSayisi, String kimden, String ismi, String aciklama, String konum) {
        this.ilanID = benzersizIlanId;
        benzersizIlanId++;
        this.fiyat = fiyat;
        this.m2 = m2;
        this.binaYasi = binaYasi;
        this.odaSayisi = odaSayisi;
        this.kimden = kimden;
        this.ismi = ismi;
        this.aciklama = aciklama;
        this.konum = konum;
        this.fotoHead = null;
        this.next=null;
    }

    public Ilan() {

    }

    public void fotoEkle(String fotoPath) {
        FotoNode yeni = new FotoNode(fotoPath);

        if (fotoHead == null) {
            fotoHead = yeni;
            fotoHead.next = fotoHead;
            fotoHead.prev = fotoHead;
        } else {
            FotoNode son = fotoHead.prev;
            son.next = yeni;
            yeni.prev = son;
            yeni.next = fotoHead;
            fotoHead.prev = yeni;
        }
    }
}