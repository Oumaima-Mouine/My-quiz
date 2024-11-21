package com.example.myinteljquiz.controller.Etudiant;

import com.example.myinteljquiz.HelloApplication;
import com.example.myinteljquiz.model.DbConnct;
import com.example.myinteljquiz.model.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class EtudiantController {
    @FXML private Button logOutBtn;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    @FXML private Button startQuizButton;
    @FXML private ListView<Quiz> quizListView;
    private ObservableList<Quiz> quizList = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        quizListView.setCellFactory(param -> new ListCell<Quiz>() {
            @Override
            protected void updateItem(Quiz quiz, boolean empty) {
                super.updateItem(quiz, empty);
                if (empty || quiz == null) {
                    setGraphic(null);
                } else {
                    // Card layout
                    HBox card = new HBox(10); // Spacing between image and text
                    card.getStyleClass().add("card");

                    // Static ImageView
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(60);  // Set fixed width for the image
                    imageView.setFitHeight(60); // Set fixed height for the image
                    imageView.setPreserveRatio(true); // Preserve aspect ratio

                    // Load the static image from resources
                    imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/myinteljquiz/images/staticImage.png"))); // Adjust the path as necessary

                    // VBox for text details
                    VBox vBox = new VBox(5);
                    Label title = new Label(quiz.getName());
                    title.getStyleClass().add("card-title");

                    Label description = new Label(quiz.getDescription());
                    description.getStyleClass().add("card-description");

                    Label duration = new Label(quiz.getTime());
                    duration.getStyleClass().add("card-duration");

                    vBox.getChildren().addAll(title, description, duration);

                    // Add ImageView and VBox to the card
                    card.getChildren().addAll(imageView, vBox);

                    // Set the card as the graphic of the cell
                    setGraphic(card);
                }
            }
        });

//        // Bind the list to the ListView
//        quizListView.setItems(quizList);
////
////        // Use a custom cell factory to display each quiz as a card
//        quizListView.setCellFactory(param -> new ListCell<Quiz>() {
//            @Override
//            protected void updateItem(Quiz quiz, boolean empty) {
//                super.updateItem(quiz, empty);
//                if (empty || quiz == null) {
//                    setGraphic(null);
//                } else {
//                    // Create the custom card layout
//                    HBox card = new HBox(10);
//                    card.getStyleClass().add("card"); // Apply the 'card' style from the CSS file
//
//                    VBox vBox = new VBox(5);
//                    Label title = new Label(quiz.getName());
//                    title.getStyleClass().add("card-title"); // Apply 'card-title' style
//                    Label description = new Label(quiz.getDescription());
//                    description.getStyleClass().add("card-description"); // Apply 'card-description' style
//                    Label duration = new Label(quiz.getTime());
//                    duration.getStyleClass().add("card-duration"); // Apply 'card-duration' style
//
//                    vBox.getChildren().addAll(title, description, duration);
//                    card.getChildren().add(vBox);
//                    setGraphic(card);
//                }
//            }
//        });
        loadQuizData(); // Ensure quiz data is loaded
    }


    @FXML
    public void LogOutOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) logOutBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    private void loadQuizData() {
        String querySelect = "SELECT * FROM quiz";

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

            // Set the ObservableList to the ListView
            quizListView.setItems(quizList);  // Bind quizList to ListView

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void startQuiz(ActionEvent event) throws IOException {
        Quiz selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/DoQuiz.fxml"));
            Parent root = fxmlLoader.load();

            // Pass quiz ID to DoingQuiz controller
            DoingQuiz controller = fxmlLoader.getController();
            controller.initializeQuiz(selectedQuiz.getId()); // Pass the selected quiz ID

            // Load the new scene
            Stage stage = (Stage) startQuizButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            // Show alert if no quiz is selected
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a quiz to start.", ButtonType.OK);
            alert.showAndWait();
        }
    }


    @FXML
    public void searchQuiz(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Quiz> filteredList = FXCollections.observableArrayList();

        for (Quiz quiz : quizList) {
            if (quiz.getName().toLowerCase().contains(searchText)) {
                filteredList.add(quiz);
            }
        }
        quizListView.setItems(filteredList);  // Update the ListView with filtered results
    }
}
