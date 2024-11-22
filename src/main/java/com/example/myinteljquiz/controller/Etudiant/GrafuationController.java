package com.example.myinteljquiz.controller.Etudiant;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import com.example.myinteljquiz.controller.Etudiant.DoingQuiz;

import java.io.IOException;

import static com.example.myinteljquiz.controller.Etudiant.DoingQuiz.correctAnswer;
import static com.example.myinteljquiz.controller.Etudiant.DoingQuiz.pointAnswer;

public class GrafuationController {
    @FXML private Label correctAnswersText;
    @FXML private Label pointsText;
    @FXML private Button restartButton;
    @FXML private Button cancelButton;

    @FXML
    public void initialize() {
        correctAnswersText.setText(correctAnswer());
        pointsText.setText(pointAnswer());
    }
    @FXML
    public void restartButtonOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/DoQuiz.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) restartButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    public void cancelButtonOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/Etudiant.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
