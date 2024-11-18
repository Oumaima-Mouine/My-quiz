module com.example.myinteljquiz {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    exports com.example.myinteljquiz;
    opens com.example.myinteljquiz to javafx.fxml;


    opens com.example.myinteljquiz.view to javafx.fxml;

    exports com.example.myinteljquiz.controller.Authentification;
    opens com.example.myinteljquiz.controller.Authentification to javafx.fxml;

    exports com.example.myinteljquiz.controller.Etudiant;
    opens com.example.myinteljquiz.controller.Etudiant to javafx.fxml;

    exports com.example.myinteljquiz.controller.Enseignant;
    opens com.example.myinteljquiz.controller.Enseignant to javafx.fxml;

    exports com.example.myinteljquiz.model;
    opens com.example.myinteljquiz.model to javafx.fxml;
}
