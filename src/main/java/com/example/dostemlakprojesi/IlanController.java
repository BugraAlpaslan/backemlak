package com.example.dostemlakprojesi;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
public class IlanController {

    @FXML
    public Label prevPhoto, nextPhoto,fiyatLabel,metrekareLabel,kimdenLabel,binaYasiLabel,odaSayisiLabel,baslikLabel;
    @FXML
    public TextArea aciklamaTextArea;
    @FXML
    public ImageView fotoImageView,favorilereEkle;

    Ilan geciciIlan;


    public void setIlanVerisi(Ilan ilan) {
        geciciIlan=ilan;
        baslikLabel.setText(ilan.ismi);
        fiyatLabel.setText(Integer.toString(ilan.fiyat));
        metrekareLabel.setText(Integer.toString(ilan.m2));
        kimdenLabel.setText(ilan.kimden);
        binaYasiLabel.setText(Integer.toString(ilan.binaYasi));
        odaSayisiLabel.setText(Integer.toString(ilan.odaSayisi));
        if (ilan.fotoHead != null && ilan.fotoHead.image != null) {
            fotoImageView.setImage(ilan.fotoHead.image);
            fotoImageView.setFitWidth(350);
            fotoImageView.setFitHeight(200);
            fotoImageView.setPreserveRatio(true);
        }

    }
    public void oncekifoto(){
        fotoImageView.setImage(geciciIlan.fotoHead.prev.image);
        geciciIlan.fotoHead=geciciIlan.fotoHead.prev;
    }

    public void sonrakifoto(){
        fotoImageView.setImage(geciciIlan.fotoHead.next.image);
        geciciIlan.fotoHead=geciciIlan.fotoHead.next;
    }

    public void buyukfoto(){
        fotoImageView.setFitWidth(500);
        fotoImageView.setFitHeight(277);
        //bu sayılar düzenlenecek ve büyültünce güzel vir görünüm verilecek şu an bok gibi gözüküyo
        fotoImageView.setX(60);
        fotoImageView.setY(50);
    }
    @FXML
    public void kucukfoto(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            fotoImageView.setFitWidth(350);
            fotoImageView.setFitHeight(200);
            //bu sayılar düzenlenecek ve büyültünce güzel vir görünüm verilecek şu an bok gibi gözüküyo
            fotoImageView.setX(18);
            fotoImageView.setY(12);
        }
    }

}
