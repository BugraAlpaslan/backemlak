package com.example.dostemlakprojesi;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AnaSayfaController implements Initializable
{
    @FXML
    public TextField aramaButonu;
    @FXML
    public VBox filtrelemeVbox;
    @FXML
    public TextField minFiyat;
    @FXML
    public TextField maxFiyat;
    @FXML
    public TextField minYas;
    @FXML
    public TextField maxYas;
    @FXML
    public TextField minm2;
    @FXML
    public TextField maxm2;
    @FXML
    public Button filtreleButon;
    @FXML
    public ComboBox<String> m2secme;
    @FXML
    public Button ilanSayfasiButon;
    @FXML
    private ListView<Ilan> ilanListView;



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        m2secme.getItems().addAll("1+0", "1+1", "2+1", "2+2", "3+1", "3+2", "3+3");

        int aktifID = OturumYonetimi.getAktifKullaniciID();

        HashMap<Integer, IlanLinkedList> ilanlarMap = IlanYonetimi.getInstance().getIlanHash();

        if (ilanlarMap.containsKey(aktifID)) {
            IlanLinkedList ilanlar = ilanlarMap.get(aktifID);
            IlanNode current = ilanlar.head;

            while (current != null) {
                ilanListView.getItems().add(current.ilan);
                current = current.next;
            }

            ilanListView.setCellFactory(lv -> new ListCell<>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(Ilan item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("📌 Başlık: ").append(item.ismi).append("\n");
                        sb.append("💰 Fiyat: ").append(item.fiyat).append(" ₺\n");
                        sb.append("📍 Konum: ").append(item.konum).append("\n");
                        sb.append("📐 m²: ").append(item.m2).append("\n");
                        sb.append("🚪 Oda Sayısı: ").append(item.odaSayisi).append("\n");
                        sb.append("🏢 Bina Yaşı: ").append(item.binaYasi).append("\n");
                        sb.append("🗒️ Açıklama: ").append(item.aciklama).append("\n");

                        setText(sb.toString());

                        if (item.fotoHead != null && item.fotoHead.image != null) {
                            imageView.setImage(item.fotoHead.image);
                            imageView.setFitWidth(100);
                            imageView.setFitHeight(75);
                            imageView.setPreserveRatio(true);
                            setGraphic(imageView);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });


        }
        ilanListView.setOnMouseClicked(mouseEvent -> {
            Ilan secilenIlan = ilanListView.getSelectionModel().getSelectedItem();
            if (secilenIlan != null) {
                ilanDetaySayfasiGecis(secilenIlan);
            }
        });

    }


    @FXML
    public void filtreleTiklandi() {
        int aktifID = OturumYonetimi.getAktifKullaniciID();
        HashMap<Integer, IlanLinkedList> ilanlarMap = IlanYonetimi.getInstance().getIlanHash();
        ilanListView.getItems().clear();

        if (!ilanlarMap.containsKey(aktifID)) return;

        IlanLinkedList ilanlar = ilanlarMap.get(aktifID);
        IlanNode current = ilanlar.head;

        int minFiyatDeger = parseOrDefault(minFiyat.getText(), Integer.MIN_VALUE);
        int maxFiyatDeger = parseOrDefault(maxFiyat.getText(), Integer.MAX_VALUE);
        int minYasDeger   = parseOrDefault(minYas.getText(), Integer.MIN_VALUE);
        int maxYasDeger   = parseOrDefault(maxYas.getText(), Integer.MAX_VALUE);
        int minM2Deger    = parseOrDefault(minm2.getText(), Integer.MIN_VALUE);
        int maxM2Deger    = parseOrDefault(maxm2.getText(), Integer.MAX_VALUE);
        String odaSecim = m2secme.getValue();

        int secilenToplamOda = -1;
        if (odaSecim != null && odaSecim.contains("+")) {
            try {
                String[] parcalar = odaSecim.split("\\+");
                int oda = Integer.parseInt(parcalar[0].trim());
                int salon = Integer.parseInt(parcalar[1].trim());
                secilenToplamOda = oda + salon;
            } catch (Exception e) {
                secilenToplamOda = -1;
            }
        }

        while (current != null) {
            Ilan ilan = current.ilan;

            boolean fiyatUygun = ilan.fiyat >= minFiyatDeger && ilan.fiyat <= maxFiyatDeger;
            boolean yasUygun = ilan.binaYasi >= minYasDeger && ilan.binaYasi <= maxYasDeger;
            boolean m2Uygun = ilan.m2 >= minM2Deger && ilan.m2 <= maxM2Deger;
            boolean odaUygun = (secilenToplamOda == -1 || ilan.odaSayisi == secilenToplamOda);

            if (fiyatUygun && yasUygun && m2Uygun && odaUygun) {
                ilanListView.getItems().add(ilan);
            }

            current = current.next;
        }

        if (ilanListView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sonuç Yok");
            alert.setHeaderText(null);
            alert.setContentText("Filtreye uyan ilan bulunamadı.");
            alert.showAndWait();
        }
    }


    private int parseOrDefault(String text, int defaultValue) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }



    public void filtrelemeAc(MouseEvent event)
    {
        boolean gorunum=filtrelemeVbox.isVisible();
        filtrelemeVbox.setVisible(!gorunum);
        filtrelemeVbox.setManaged(!gorunum);
    }

    public void ilanSayfasiGecis()
    {
        pencereDegistir pencere=new pencereDegistir();
        Stage stage=(Stage) ilanSayfasiButon.getScene().getWindow();
        pencere.sahnedegistir(stage,"/com/example/dostemlakprojesi/ilanEkleSayfasi.fxml");
    }

    public void ilanDetaySayfasiGecis(Ilan secilenIlan)
    {
        pencereDegistir pencere=new pencereDegistir();
        Stage stage=(Stage) ilanSayfasiButon.getScene().getWindow();
        pencere.sahnedegistir(stage,"/com/example/dostemlakprojesi/ilan.fxml");
        IlanController controller=(IlanController) pencere.getController();
        controller.setIlanVerisi(secilenIlan);

    }

}
