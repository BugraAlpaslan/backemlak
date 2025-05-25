package com.example.dostemlakprojesi;


public class KullaniciNode
{
    public static int benzersizID=1000;
    int id,sifre;
    String kullaniciAdi;
    KullaniciNode next;
    KullaniciNode(String kullaniciAdi,int sifre)
    {
        this.id=benzersizID;
        benzersizID++;
        this.next=null;
        this.kullaniciAdi=kullaniciAdi;
        this.sifre=sifre;
    }
}
