package com.example.myinteljquiz.controller.Enseignant;

import com.example.myinteljquiz.model.DbConnct;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.awt.*;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizController {
    @FXML
    private TextField quizNameField;
    @FXML
    private TextArea quizDescriptionArea;
    @FXML
    private TextField quizTimeField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button nextButton;
    public  void NextOnAction(ActionEvent event)throws IOException{
//        String quizName = quizNameField.getText();
//        String quizDescription = quizDescriptionArea.getText();
//        String quizTime = quizTimeField.getText();
//
//        DbConnct dbConnct = new DbConnct();
//        String query = "INSERT INTO quiz( `titre_quiz`, `description`, `duree`) VALUES (?,?,?) ";
//        try (Connection conx = dbConnct.getConnection();
//             PreparedStatement stmt  = conx.prepareStatement(query)) {
//            stmt.setString(1,quizName);
//            stmt.setString(2,quizDescription);
//            stmt.setString(3,quizTime);
//            stmt.executeUpdate();
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/creationQuestions.fxml"));
//            Parent root = fxmlLoader.load();
//            Stage stage = (Stage) nextButton.getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Creation Questions");
//            stage.show();
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
        String quizName = quizNameField.getText();
        String quizDescription = quizDescriptionArea.getText();
        String quizTime = quizTimeField.getText();

        DbConnct dbConnct = new DbConnct();
        String query = "INSERT INTO quiz( `titre_quiz`, `description`, `duree`) VALUES (?, ?, ?)";
        int quizId = -1; // Variable to store the id_quiz

        try (Connection conx = dbConnct.getConnection();
             PreparedStatement stmt = conx.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, quizName);
            stmt.setString(2, quizDescription);
            stmt.setString(3, quizTime);
            stmt.executeUpdate();

            // Retrieve the generated keys
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                quizId = generatedKeys.getInt(1); // Get the id_quiz
            }

            // Pass the quizId to the next controller
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/creationQuestions.fxml"));
            Parent root = fxmlLoader.load();
            QuestionController questionController = fxmlLoader.getController();
            questionController.setQuizId(quizId); // Pass quizId to the QuestionController

            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Creation Questions");
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void CancelOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/Enseignant.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }



}
