package com.example.myinteljquiz.controller.Enseignant;

import com.example.myinteljquiz.HelloApplication;
import com.example.myinteljquiz.model.DbConnct;
import com.example.myinteljquiz.model.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnseignantController {
    @FXML private Button log_out_Btn;
    @FXML private Button create_Btn;
    @FXML private Button update_Btn;
    @FXML private Button delete_Btn;
    @FXML private Button cancelButton;
    @FXML private Button updateButton;
    @FXML private TableView<Quiz> table_Enseignant;
    @FXML private TableColumn<Quiz, Integer> id_column;
    @FXML private TableColumn<Quiz, String> name_column;
    @FXML private TableColumn<Quiz, String> descript_Btn;
    @FXML private TableColumn<Quiz, String> time_column;
    @FXML
    private TextField quizNameField;
    @FXML
    private TextArea quizDescriptionArea;
    @FXML
    private TextField quizTimeField;
    private int idQuiz;

    // ObservableList to hold quiz data
    private ObservableList<Quiz> quizList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up the TableView columns
        id_column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        name_column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        descript_Btn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        time_column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTime()));

        // Load quiz data from the database
        loadQuizData();
        table_Enseignant.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                idQuiz = newValue.getId(); // Make sure `getId()` returns the quiz ID
            }
        });
    }

    // Method to load quiz data from the database
    private void loadQuizData() {
        String querySelect = "SELECT * FROM `quiz`";

        try (Connection connection = DbConnct.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id_quiz");
                String name = resultSet.getString("titre_quiz");
                String description = resultSet.getString("description");
                String time = resultSet.getString("duree");

                // Add the quiz to the list
                quizList.add(new Quiz(id, name, description, time));
            }

            // Set the list to the TableView
            table_Enseignant.setItems(quizList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void LogOutOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) log_out_Btn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void CreateOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/creationQuiz.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) create_Btn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setQuizId(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public void DeleteOnAction(ActionEvent event) {
        if (idQuiz == 0) { // Check if idQuiz is valid
            System.out.println("No quiz selected for deletion.");
            // Optionally, display a message to the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No Quiz Selected");
            alert.setContentText("Please select a quiz to delete.");
            alert.showAndWait();
            return;
        }

        String query = "DELETE FROM `quiz` WHERE id_quiz = ?";
        System.out.println("Attempting to delete quiz with id: " + idQuiz); // Debugging line

        try (Connection connection = DbConnct.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idQuiz);

            int i = preparedStatement.executeUpdate();
            if (i > 0) {
                System.out.println("Quiz deleted successfully.");
                // Remove the deleted quiz from the TableView
                quizList.removeIf(quiz -> quiz.getId() == idQuiz);
                table_Enseignant.refresh();
            } else {
                System.out.println("Quiz could not be deleted.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Deletion Failed");
                alert.setContentText("Could not delete the selected quiz.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.out.println("Error occurred during quiz deletion: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Deletion Failed");
            alert.setContentText("An error occurred while trying to delete the quiz.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    //    public void UpdateOnAction(ActionEvent event) {
//        // Check if a quiz is selected
//        Quiz selectedQuiz = table_Enseignant.getSelectionModel().getSelectedItem();
//        if (selectedQuiz == null) {
//            showAlert(Alert.AlertType.WARNING, "Selection Error", "No quiz selected for updating.");
//            return;
//        }
//
//        // Pre-fill the fields with the current quiz details
//        quizNameField.setText(selectedQuiz.getName());
//        quizDescriptionArea.setText(selectedQuiz.getDescription());
//        quizTimeField.setText(selectedQuiz.getTime());
//
//        // Action on the updateButton
//        updateButton.setOnAction(e -> {
//            String newQuizName = quizNameField.getText();
//            String newQuizDescription = quizDescriptionArea.getText();
//            String newQuizTime = quizTimeField.getText();
//
//            if (newQuizName.trim().isEmpty() || newQuizDescription.trim().isEmpty() || newQuizTime.trim().isEmpty()) {
//                showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled out.");
//                return;
//            }
//
//            // Update the quiz in the database
//            String query = "UPDATE quiz SET titre_quiz = ?, description = ?, duree = ? WHERE id_quiz = ?";
//
//            try (Connection connection = DbConnct.getConnection();
//                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//                preparedStatement.setString(1, newQuizName);
//                preparedStatement.setString(2, newQuizDescription);
//                preparedStatement.setString(3, newQuizTime);
//                preparedStatement.setInt(4, selectedQuiz.getId());
//
//                int i = preparedStatement.executeUpdate();
//                if (i > 0) {
//                    showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz updated successfully");
//
//                    // Update the selected quiz in the list and refresh the TableView
//                    selectedQuiz.setName(newQuizName);
//                    selectedQuiz.setDescription(newQuizDescription);
//                    selectedQuiz.setTime(newQuizTime);
//                    table_Enseignant.refresh();
//                } else {
//                    showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update the quiz.");
//                }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the quiz.");
//            }
//        });
//
//        // Action for the cancelButton to clear the fields
//        cancelButton.setOnAction(e -> {
//            quizNameField.clear();
//            quizDescriptionArea.clear();
//            quizTimeField.clear();
//        });
//    }
@FXML
public void UpdateOnAction(ActionEvent event) throws IOException {
    Quiz selectedQuiz = table_Enseignant.getSelectionModel().getSelectedItem();
    if (selectedQuiz == null) {
        showAlert(Alert.AlertType.WARNING, "Selection Error", "No quiz selected for updating.");
        return;
    }

    // Pass the selected quiz ID to the new view
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/updateQuiz.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) update_Btn.getScene().getWindow();
    stage.setScene(new Scene(root));

    UpdateQuizController updateQuizController = fxmlLoader.getController();
    updateQuizController.initialize(selectedQuiz); // Pass selected quiz to the controller

    stage.show();
}





    @FXML
    public void onUpdateButtonAction(ActionEvent event) {
        // Get the updated values from the text fields
        String newQuizName = quizNameField.getText();
        String newQuizDescription = quizDescriptionArea.getText();
        String newQuizTime = quizTimeField.getText();

        // Validate the input fields (ensure no field is empty)
        if (newQuizName.trim().isEmpty() || newQuizDescription.trim().isEmpty() || newQuizTime.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled out.");
            return;
        }

        // Prepare the SQL query to update the quiz in the database
        String query = "UPDATE quiz SET titre_quiz = ?, description = ?, duree = ? WHERE id_quiz = ?";

        try (Connection connection = DbConnct.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the parameters for the query
            preparedStatement.setString(1, newQuizName);
            preparedStatement.setString(2, newQuizDescription);
            preparedStatement.setString(3, newQuizTime);
            preparedStatement.setInt(4, idQuiz);  // The ID of the selected quiz

            // Execute the update query
            int i = preparedStatement.executeUpdate();
            if (i > 0) {
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz updated successfully");

                // Update the quiz object in the table view
                for (Quiz quiz : quizList) {
                    if (quiz.getId() == idQuiz) {
                        quiz.setName(newQuizName);
                        quiz.setDescription(newQuizDescription);
                        quiz.setTime(newQuizTime);
                        break;
                    }
                }

                // Refresh the table to display updated quiz data
                table_Enseignant.refresh();
            } else {
                // Show error if the update fails
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update the quiz.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the quiz.");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
