package com.example.dostemlakprojesi;

public class OturumYonetimi {
    private static int aktifKullaniciID = -1;

    public static void girisYap(int id) {
        aktifKullaniciID = id;
    }

    public static int getAktifKullaniciID() {
        return aktifKullaniciID;
    }

    public static void oturumuKapat() {
        aktifKullaniciID = -1;
    }
}
