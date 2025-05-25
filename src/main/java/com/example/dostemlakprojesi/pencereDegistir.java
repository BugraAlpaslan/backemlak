package com.example.dostemlakprojesi;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class pencereDegistir
{

    FXMLLoader fxmlLoader=new FXMLLoader();

    public void sahnedegistir(Stage stage, String fxmlpath)
    {
        try {

            fxmlLoader.setLocation(pencereDegistir.class.getResource(fxmlpath));
            Parent root=fxmlLoader.load();

            Scene scene=new Scene(root);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Object getController(){
        return fxmlLoader.getController();
    }


}
