package com.example.myinteljquiz.controller.Authentification;

import com.example.myinteljquiz.HelloApplication;
import com.example.myinteljquiz.model.DbConnct;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable  {
    @FXML
    private TextField emailFeild;
    @FXML
    private TextField usernameFeild;
    @FXML
    private PasswordField passwordFeild;
    @FXML
    private RadioButton etudiantRadio;
    @FXML
    private RadioButton enseigantRadio;
    @FXML
    private Button signupButton;
    @FXML
    private Button loginButton;

    public void SignUpOnAction(ActionEvent event) throws IOException {
        // Get the input values from the fields
        String email = emailFeild.getText();
        String username = usernameFeild.getText();
        String password = passwordFeild.getText();

        // Check if the radio button for student or teacher is selected
        String role = "";
        if (etudiantRadio.isSelected()) {
            role = "etudiant"; // Student role
        } else if (enseigantRadio.isSelected()) {
            role = "enseignant"; // Teacher role
        } else {
            // Show an error message if no role is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Role not selected");
            alert.setContentText("Please select whether you are a student or teacher.");
            alert.showAndWait();
            return;
        }

        // Validate email, username, and password (you can add more validation here)
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Input Missing");
            alert.setContentText("Please fill in all the fields.");
            alert.showAndWait();
            return;
        }

        // Assuming you have a method to insert data into your database
        DbConnct dbConnct = new DbConnct();
        String insertQuery = "INSERT INTO utilisateur (email, nom_user, mdp_user, role) VALUES (?, ?, ?, ?)";

        try (Connection connection = dbConnct.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, password);  // You might want to hash the password before storing it
            stmt.setString(4, role);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // If the insertion is successful, show a success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("User Registered");
                alert.setContentText("You have successfully registered.");
                alert.showAndWait();

                // Redirect to the login screen
//                LoginOnAction(event);
                if ("etudiant".equals(role)) {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/myinteljquiz/view/Etudiant.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = (Stage) signupButton.getScene().getWindow();
                    stage.setScene(new Scene(root, 600, 400));
                    stage.setTitle("Etudiant View");
                    stage.show();
                }else if ("enseignant".equals(role)) {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/myinteljquiz/view/Enseignant.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = (Stage) signupButton.getScene().getWindow();
                    stage.setScene(new Scene(root, 600, 400));
                    stage.setTitle("Enseignant View");
                    stage.show();
                }else {
                    System.out.println("not found");
                }
            } else {
                // If the insertion fails
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Registration Failed");
                alert.setContentText("There was an issue with your registration. Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error Connecting to Database");
            alert.setContentText("There was an issue connecting to the database.");
            alert.showAndWait();
        }
    }

    public void LoginOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setTitle("Login Form!");
        stage.setScene(scene);
        stage.show();
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
