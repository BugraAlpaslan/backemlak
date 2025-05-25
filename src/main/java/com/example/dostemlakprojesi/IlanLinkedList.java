package com.example.dostemlakprojesi;

public class IlanLinkedList {
    IlanNode head;

    public void ilanEkle(Ilan ilan) {
        IlanNode yeni = new IlanNode(ilan);
        if (head == null) {
            head = yeni;
        } else {
            IlanNode temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = yeni;
        }
    }



    public void yazdir() {
        IlanNode temp = head;
        while (temp != null) {
            System.out.println(temp.ilan);
            temp = temp.next;
        }
    }
}
