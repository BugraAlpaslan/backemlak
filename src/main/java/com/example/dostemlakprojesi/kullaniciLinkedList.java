package com.example.dostemlakprojesi;

import javafx.scene.control.Alert;
import org.w3c.dom.Node;

import java.util.Objects;

public class kullaniciLinkedList
{
    private static kullaniciLinkedList instance;
    KullaniciNode head;

    kullaniciLinkedList()
    {
        head=null;
    }

    public static kullaniciLinkedList getInstance()
    {
        if (instance==null)
        {
            instance=new kullaniciLinkedList();
        }return instance;
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


    public void kullaniciEkle(String kullaniciAdi,int sifre)
    {
        KullaniciNode yeniKullanici=new KullaniciNode(kullaniciAdi,sifre);
        KullaniciNode temp=head;
        if (head==null)
        {
            head=yeniKullanici;
            return;
        }
        while(temp.next!=null)
        {
            temp=temp.next;
        }
        temp.next=yeniKullanici;
    }

    public void listele()
    {
        KullaniciNode temp = head;
        while (temp != null) {
            System.out.println("ID: " + temp.id + " | Kullanıcı Adı: " + temp.kullaniciAdi + " | Şifre: " + temp.sifre);
            temp = temp.next;
        }
    }

    public boolean arama(String kullaniciAdi,int sifre)
    {
        KullaniciNode temp=head;
        while(temp!=null)
        {
            if (temp.kullaniciAdi.equals(kullaniciAdi)||temp.sifre==sifre)
            {
                return true;
            }
            temp=temp.next;
        }
        return false;
    }

    public boolean tamEslesme(String kullaniciAdi,int sifre)
    {
        KullaniciNode temp=head;
        while(temp!=null)
        {
            if (temp.kullaniciAdi.equals(kullaniciAdi)&&temp.sifre==sifre)
            {
                return true;
            }
            temp=temp.next;
        }
        return false;
    }

}
