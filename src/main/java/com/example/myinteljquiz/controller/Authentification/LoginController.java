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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button loginBtn;
    @FXML
    private Label labelMessage;
    @FXML
    private TextField usernameFeild;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signupBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void loginButtonOnAction(ActionEvent e) {
        if (!usernameFeild.getText().isBlank() && !passwordField.getText().isBlank()) {
            try {
                validationLogin();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            labelMessage.setText("Please enter your username and password");
        }
    }

    public void validationLogin() throws IOException {
        DbConnct connctNow = new DbConnct();
        Connection connectionDb = connctNow.getConnection();
        String verifyLogin ="SELECT role FROM utilisateur WHERE nom_user = ? AND mdp_user = ?";

        try {
            PreparedStatement preparedStatement = connectionDb.prepareStatement(verifyLogin);
            preparedStatement.setString(1, usernameFeild.getText());
            preparedStatement.setString(2, passwordField.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String rolee = resultSet.getString("role");

                // Load the new scene
                if ("etudiant".equals(rolee)) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/Etudiant.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                    Stage stage = (Stage) loginBtn.getScene().getWindow();
                    stage.setTitle("Login Form!");
                    stage.setScene(scene);
                    stage.show();
//                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Etudiant.fxml"));
//                    Parent root = fxmlLoader.load();
//                    Stage stage = (Stage) loginBtn.getScene().getWindow();
//                    stage.setScene(new Scene(root, 600, 400));
//                    stage.setTitle("Etudiant View");
//                    stage.show();
                }else if ("enseignant".equals(rolee)) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/Enseignant.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = (Stage) loginBtn.getScene().getWindow();
                    stage.setScene(new Scene(root, 600, 400));
                    stage.setTitle("Sign Up");
                }else {
                    System.out.printf("not found");
                }
            } else {
                labelMessage.setText("Invalid login. Please try again!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public  void SignUpBtnOnAction(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/myinteljquiz/view/SIgn-up.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) signupBtn.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Sign Up");
        stage.show();
    }
}
