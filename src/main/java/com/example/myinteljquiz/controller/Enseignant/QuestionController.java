package com.example.myinteljquiz.controller.Enseignant;

import com.example.myinteljquiz.model.DbConnct;

import javafx.collections.FXCollections;
//import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class QuestionController {
    private int quizId; // Variable to store the id_quiz
    private int questionCounter = 1; // Start counting from 1
    private static final int MAX_QUESTIONS = 10; // Maximum number of questions

    @FXML
    private Label questionNumberLabel; // Label to show the question number
    @FXML
    private TextField questionTextField;
    @FXML
    private TextField option1TextField;
    @FXML
    private TextField option2TextField;
    @FXML
    private TextField option3TextField;
    @FXML
    private TextField option4TextField;
    @FXML
    private ComboBox<String> correctAnswerComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    public void initialize() {
        // Set the initial question number
        updateQuestionNumberLabel();

        // Populate ComboBox with options
        correctAnswerComboBox.setItems(FXCollections.observableArrayList(
                "Option 1",
                "Option 2",
                "Option 3",
                "Option 4"
        ));
    }

    // Method to set the quizId from QuizController
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void SaveOnAction(ActionEvent event) throws IOException {
        if (questionCounter > MAX_QUESTIONS) {
            // If the maximum number of questions has been reached, navigate to Enseignant.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view//Enseignant.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            return; // Exit the method
        }

        String question = questionTextField.getText();
        String option1 = option1TextField.getText();
        String option2 = option2TextField.getText();
        String option3 = option3TextField.getText();
        String option4 = option4TextField.getText();
        String correctAnswer = correctAnswerComboBox.getValue();

        // Insert question and options
        DbConnct dbConnct = new DbConnct();
        String queryQuestion = "INSERT INTO question (id_quiz, txt_question, point) VALUES (?, ?, ?)";

        try (Connection conx = dbConnct.getConnection();
             PreparedStatement stmt = conx.prepareStatement(queryQuestion, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set question parameters
            stmt.setInt(1, quizId); // Use the quizId passed from QuizController
            stmt.setString(2, question);
            stmt.setInt(3, 1); // Assuming point is 1; change as needed
            stmt.executeUpdate();

            // Retrieve the generated id_question
            int questionId = -1;
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                questionId = generatedKeys.getInt(1); // Get the id_question
            }

            // Insert options using the questionId
            insertOption(conx, questionId, option1, "Option 1".equals(correctAnswer));
            insertOption(conx, questionId, option2, "Option 2".equals(correctAnswer));
            insertOption(conx, questionId, option3, "Option 3".equals(correctAnswer));
            insertOption(conx, questionId, option4, "Option 4".equals(correctAnswer));

            // Increment the question counter
            questionCounter++;

            if (questionCounter <= MAX_QUESTIONS) {
                // Update the question number label for the next question
                updateQuestionNumberLabel();
                // Clear fields for the next question
                clearFields();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertOption(Connection conx, int questionId, String optionText, boolean isCorrect) throws SQLException {
        String queryOption = "INSERT INTO options (id_question, txt_options, est_correct) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conx.prepareStatement(queryOption)) {
            stmt.setInt(1, questionId);
            stmt.setString(2, optionText);
            stmt.setBoolean(3, isCorrect);
            stmt.executeUpdate();
        }
    }

    private void clearFields() {
        questionTextField.clear();
        option1TextField.clear();
        option2TextField.clear();
        option3TextField.clear();
        option4TextField.clear();
        correctAnswerComboBox.getSelectionModel().clearSelection();
    }

    private void updateQuestionNumberLabel() {
        questionNumberLabel.setText("Enter question number " + questionCounter);
    }

    public void CancelOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/Enseignant.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}


