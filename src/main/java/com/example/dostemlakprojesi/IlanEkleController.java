package com.example.dostemlakprojesi;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class IlanEkleController
{
    public Label geriDon;
    @FXML TextField ilanIsmi;
    @FXML ImageView logoImageView;
    @FXML TextField baslikTextField;
    @FXML TextField fiyatTextField;
    @FXML TextField odaSayisiTextField;
    @FXML TextField metrekareTextField;
    @FXML TextField binaYasiTextField;
    @FXML TextField konumTextField;
    @FXML TextArea aciklamaTextArea;
    @FXML Button kaydetButton;
    @FXML Label resimEkleLabel;

    private String secilenResimYollari = "";



    IlanYonetimi ilanHashleme=IlanYonetimi.getInstance();
    kullaniciLinkedList kullaniciListesi=kullaniciLinkedList.getInstance();
    int aktifID = OturumYonetimi.getAktifKullaniciID();
    private String secilenResimYolu = null;


    @FXML
    public void resimEkle(javafx.scene.input.MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Fotoğraf Seç");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.jpg", "*.jpeg", "*.png")
        );

        File secilenDosya = fileChooser.showOpenDialog(null);

        if (secilenDosya != null) {
            try {
                File hedefKlasor = new File("src/main/resources/resimler");
                if (!hedefKlasor.exists()) hedefKlasor.mkdirs();

                String dosyaAdi = System.currentTimeMillis() + "_" + secilenDosya.getName();
                File hedefDosya = new File(hedefKlasor, dosyaAdi);

                Files.copy(secilenDosya.toPath(), hedefDosya.toPath(), StandardCopyOption.REPLACE_EXISTING);

                secilenResimYolu = hedefDosya.getAbsolutePath();
                secilenResimYollari += secilenResimYolu+";";



                resimEkleLabel.setText("📷 Fotoğraf eklendi: " + dosyaAdi);

            } catch (Exception e) {
                resimEkleLabel.setText("❌ Fotoğraf kopyalanamadı.");
            }
        }
    }



    public void ilanEkle()
    {
        try{
            String baslik=baslikTextField.getText();
            int fiyat=Integer.parseInt(fiyatTextField.getText());
            int odasayisi=Integer.parseInt(odaSayisiTextField.getText());
            int metrakare=Integer.parseInt(metrekareTextField.getText());
            int binayasi=Integer.parseInt(binaYasiTextField.getText());
            String konum=konumTextField.getText();
            String aciklama=aciklamaTextArea.getText();
            String ismi=ilanIsmi.getText();

            Ilan ilan=new Ilan(fiyat,metrakare,binayasi,odasayisi,ismi,baslik,aciklama,konum);
            String[] yollar = secilenResimYollari.split(";");
            for (String yol : yollar) {
                if (!yol.isEmpty()) ilan.fotoEkle(yol);
            }


            ilanHashleme.ilanEkle(aktifID, ilan);
        }catch (Exception e) {
            resimEkleLabel.setText("❌ Hata: " + e.getMessage());
        }
    }

    public void anaSayfayaDon(MouseEvent event)
    {
        Stage currentStage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        pencereDegistir pencere=new pencereDegistir();
        pencere.sahnedegistir(currentStage,"anaSayfa.fxml");
    }

}
