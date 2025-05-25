package com.example.dostemlakprojesi;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;


public class kayitOlController
{
    public Label geriLabel;
    @FXML
    public Pane rootPane;
    @FXML
    TextField kayitOlKullaniciAdiTextField;
    @FXML
    TextField kayitOlSifreTextField;
    @FXML
    Button kayitOlButon;

    public kullaniciLinkedList kullaniciListesi=kullaniciLinkedList.getInstance();


    @FXML
    public void geriDon(MouseEvent event)
    {
        Stage currentStage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        pencereDegistir pencere=new pencereDegistir();
        pencere.sahnedegistir(currentStage,"login.fxml");
    }

    public void yeniKullaniciEkle()
    {
        String kullaniciAdi = kayitOlKullaniciAdiTextField.getText().trim();
        String sifreMetin = kayitOlSifreTextField.getText().trim();

        if (kullaniciAdi.isEmpty() || sifreMetin.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Kullanıcı adı ve şifre alanları boş olamaz!");
            alert.showAndWait();
            return;
        }

        int sifre;
        try {
            sifre = Integer.parseInt(sifreMetin);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Şifre sadece sayı olmalıdır!");
            alert.showAndWait();
            return;
        }
        boolean sorgulama=kullaniciListesi.arama(kullaniciAdi,sifre);
        if (sorgulama)
        {
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Bu kullanıcı adı ve ya şifre daha önceden alınmış!");
            alert.showAndWait();
        }else {
            kullaniciListesi.kullaniciEkle(kullaniciAdi, sifre);
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Başarılı");
            alert.setHeaderText(null);
            alert.setContentText("Kaydınız başarıyla gerçekleşmiştir!");
            alert.showAndWait();
            kullaniciListesi.listele();
        }

    }

}
