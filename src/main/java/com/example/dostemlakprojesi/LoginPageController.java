package com.example.dostemlakprojesi;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
;

public class LoginPageController
{
    @FXML
    Pane rootPane;
    @FXML
    TextField girisTextField;
    @FXML
    PasswordField girisPasswordField;
    @FXML
    Button girisButton;
    @FXML
    Button girisKayitOl;

    public kullaniciLinkedList kullaniciListesi=kullaniciLinkedList.getInstance();


    @FXML
    public void kayitOlgecis()
    {
        pencereDegistir pencere=new pencereDegistir();
        Stage stage=(Stage) girisKayitOl.getScene().getWindow();
        pencere.sahnedegistir(stage,"/com/example/dostemlakprojesi/kayitOl.fxml");
    }

    @FXML
    public void girisGecis()
    {
        String kullniciAdi=girisTextField.getText().trim();
        int sifre=Integer.parseInt(girisPasswordField.getText().trim());
        boolean kontrol=kullaniciListesi.tamEslesme(kullniciAdi,sifre);

        if (kontrol)
        {
            int kullaniciGlobalID= kullaniciListesi.getKullaniciId(kullniciAdi,sifre);
            if (kullaniciGlobalID!=-1){OturumYonetimi.girisYap(kullaniciGlobalID);}
            pencereDegistir pencere=new pencereDegistir();
            Stage stage=(Stage) girisButton.getScene().getWindow();
            pencere.sahnedegistir(stage,"/com/example/dostemlakprojesi/anaSayfa.fxml");
        }else{
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Kullanıcı adı ve ya şifre yanlış");
            alert.showAndWait();
            return;
        }
    }


}
