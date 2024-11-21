package com.example.myinteljquiz.controller.Etudiant;

import javafx.application.Platform;
import com.example.myinteljquiz.model.Option;
import com.example.myinteljquiz.model.Question;
import com.example.myinteljquiz.model.DbConnct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DoingQuiz {

    @FXML
    private Label questionLabel;
    @FXML
    private VBox optionsVBox;
    @FXML
    private Label timeLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button submitButton;

    private DbConnct dbConnect = new DbConnct(); // Gestionnaire de connexion à la base de données
    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private Timer quizTimer;
    private int timeLeft;

    private String formattedTime;
//    table for the correct answer
    private int correctAnswer = 0;

    //    table for the incorrect answer
    private int incorrectAnswer = 0;

    public void initializeQuiz(int quizId) {
        try {
            // Récupérer la durée du quiz
            int quizDuration = getQuizDuration(quizId);
            if (quizDuration <= 0) {
                showAlert("Durée du quiz invalide.");
                return;
            }
            timeLeft = quizDuration; // Initialiser la durée

            // Charger les questions
            questions = getQuestionsAndOptions(quizId);
            if (!questions.isEmpty()) {
                displayQuestion(currentQuestionIndex);
                startQuizTimer(); // Démarrer le timer
            } else {
                showAlert("Aucune question trouvée pour ce quiz.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'initialisation du quiz.");
        }
    }

    // Récupérer la durée du quiz depuis la base de données
    public int getQuizDuration(int quizId) throws SQLException {
        String query = "SELECT duree FROM quiz WHERE id_quiz = ?";
        try (Connection connection = dbConnect.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Time time = resultSet.getTime("duree"); // Replace "duration" with your column name
                if (time != null) {
                    long milliseconds = time.getTime();
                    return (int) (milliseconds / (1000 * 60)); // Convert to minutes
                }
            }
        }
        return 0; // Default value if no duration is found
    }



    private List<Question> getQuestionsAndOptions(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT q.id_question, q.txt_question, q.point, o.id_options, o.txt_options, o.est_correct " +
                "FROM question q " +
                "JOIN options o ON q.id_question = o.id_question " +
                "WHERE q.id_quiz = ?";

        try (Connection connection = dbConnect.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            Map<Integer, Question> questionMap = new HashMap<>();
            while (rs.next()) {
                int questionId = rs.getInt("id_question");
                String questionText = rs.getString("txt_question");
                int points = rs.getInt("point");
                int optionId = rs.getInt("id_options");
                String optionText = rs.getString("txt_options");
                boolean isCorrect = rs.getBoolean("est_correct");

                Question question = questionMap.get(questionId);
                if (question == null) {
                    question = new Question(questionId, questionText, points);
                    questionMap.put(questionId, question);
                }

                question.addOption(new Option(optionId, optionText, isCorrect));
            }
            questions.addAll(questionMap.values());
        }
        return questions;
    }

    private void displayQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            Question question = questions.get(index);
            questionLabel.setText(question.getText());
            optionsVBox.getChildren().clear();

            ToggleGroup optionsGroup = new ToggleGroup();
            for (Option option : question.getOptions()) {
                RadioButton optionButton = new RadioButton(option.getText());
                optionButton.setToggleGroup(optionsGroup);
                optionButton.setUserData(option);
                optionsVBox.getChildren().add(optionButton);
            }
        }
    }

    private void startQuizTimer() {
        quizTimer = new Timer();
        quizTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                updateTimeDisplay();

                if (timeLeft <= 0) {
                    quizTimer.cancel();
                    try {
                        submitQuiz();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 1000, 1000);
    }

    private void updateTimeDisplay() {
//        int minutes = timeLeft / 60;
//        int seconds = timeLeft % 60;
//        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
//        progressBar.setProgress((double) timeLeft / (timeLeft + 1)); // Met à jour la barre de progression
        Platform.runLater(() -> {
            // Assuming formattedTime contains the time you want to show
            timeLabel.setText("Time: " + formattedTime); // Update your label or any other UI component
        });
    }
    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
        updateTimeDisplay(); // Update the UI with the new time
    }

    @FXML
    private void nextQuestion() {
        // Get the selected option from the radio button group
        RadioButton selectedGroup = optionsVBox.getChildren().stream()
                .filter(node -> node instanceof RadioButton)
                .map(node -> (RadioButton) node)
                .filter(RadioButton::isSelected)
                .findFirst()
                .orElse(null);

        if (selectedGroup != null) {
            // Get the selected option
            Option selectedOption = (Option) selectedGroup.getUserData();

            // Check if the selected option is correct or incorrect
            if (selectedOption != null) {
                if (selectedOption.isCorrect()) {
                    correctAnswer++;  // Increment correct answers
                } else {
                    incorrectAnswer++;  // Increment incorrect answers
                }
            }
        }

        // Proceed to the next question
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        }
    }


    @FXML
    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        }
    }

    @FXML
    private void submitQuiz() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myinteljquiz/view/graduation.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}
