module com.example.dostemlakprojesi {
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires org.jfxtras.styles.jmetro;
    requires java.desktop;

    opens com.example.dostemlakprojesi to javafx.fxml;
    exports com.example.dostemlakprojesi;
}