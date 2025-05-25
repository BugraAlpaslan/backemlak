package com.example.dostemlakprojesi;

import javafx.scene.image.Image;

public class FotoNode
{
    Image image;
    FotoNode next;
    FotoNode prev;

    public FotoNode(String path)
    {
        this.image=new Image("file:" + path);
        this.next=null;
        this.prev=null;
    }
}
