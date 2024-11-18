package com.example.myinteljquiz.controller.Enseignant;

import com.example.myinteljquiz.model.DbConnct;
import com.example.myinteljquiz.model.Quiz;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateQuizController {

    @FXML private TextField quizNameField;
    @FXML private TextArea quizDescriptionArea;
    @FXML private TextField quizTimeField;
    @FXML private Button cancelButton;

    private Quiz quizToUpdate;  // Store the selected quiz

    // This method will be automatically called by JavaFX after the FXML file is loaded
    @FXML
    public void initialize(Quiz selectedQuiz) {
        // Optional: Any initialization logic that you want to perform when the view loads
//        loadQuizData();
        loadQuizData(selectedQuiz.getId());
    }
    // Setter method to pass data (quiz) to this controller
    public void setQuiz(Quiz quiz) {
        this.quizToUpdate = quiz;
        quizNameField.setText(quiz.getName());
        quizDescriptionArea.setText(quiz.getDescription());
        quizTimeField.setText(quiz.getTime());
    }

    // Method to load quiz data by its ID
    public void loadQuizData(int quizId) {
        quizToUpdate = getQuizById(quizId);

        if (quizToUpdate != null) {
            quizNameField.setText(quizToUpdate.getName());
            quizDescriptionArea.setText(quizToUpdate.getDescription());
            quizTimeField.setText(quizToUpdate.getTime());
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Quiz not found.");
        }
    }

    // Method to get quiz details by ID
    public Quiz getQuizById(int id_quiz) {
        Quiz quiz = null;
        String query = "SELECT * FROM quiz WHERE id_quiz = ?";

        try (Connection conn = DbConnct.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id_quiz);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                quiz = new Quiz();
                quiz.setId(rs.getInt("id_quiz"));
                quiz.setName(rs.getString("titre_quiz"));
                quiz.setDescription(rs.getString("description"));
                quiz.setTime(rs.getString("duree"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quiz;
    }



    @FXML
    public void onUpdateButtonAction(ActionEvent event) {
        String newQuizName = quizNameField.getText();
        String newQuizDescription = quizDescriptionArea.getText();
        String newQuizTime = quizTimeField.getText();

        if (newQuizName.trim().isEmpty() || newQuizDescription.trim().isEmpty() || newQuizTime.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled out.");
            return;
        }

        // Update the quiz data in the database
        String query = "UPDATE quiz SET titre_quiz = ?, description = ?, duree = ? WHERE id_quiz = ?";

        try (Connection connection = DbConnct.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newQuizName);
            preparedStatement.setString(2, newQuizDescription);
            preparedStatement.setString(3, newQuizTime);
            preparedStatement.setInt(4, quizToUpdate.getId());  // Use the quiz ID for updating

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz updated successfully");
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/Enseignant.fxml"));
                Parent root = fxmlLoader.load();
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

                // Update the quiz object with new data
                quizToUpdate.setName(newQuizName);
                quizToUpdate.setDescription(newQuizDescription);
                quizToUpdate.setTime(newQuizTime);
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update the quiz.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the quiz.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Show an alert with custom message
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void cancelOnAction(ActionEvent event) throws IOException {
        // Go back to the previous page (e.g., the list of quizzes)
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/Enseignant.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
