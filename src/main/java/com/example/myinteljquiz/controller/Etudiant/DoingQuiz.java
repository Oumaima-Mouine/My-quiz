package com.example.myinteljquiz.controller.Etudiant;

import com.example.myinteljquiz.model.Option;
import com.example.myinteljquiz.model.Question;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.sql.*;
import java.util.*;
import com.example.myinteljquiz.model.DbConnct;

public class DoingQuiz {

    public DoingQuiz() {
    }
    @FXML
    private Label questionLabel;  // For displaying the current question
    @FXML
    private VBox optionsVBox;    // VBox to hold the options dynamically
    @FXML
    private Label timeLabel;     // Label to display the time
    @FXML
    private ProgressBar progressBar;  // To display progress
    @FXML
    private Button previousButton; // Navigation button
    @FXML
    private Button nextButton;     // Navigation button
    @FXML
    private Button submitButton;   // Submit button

    private DbConnct dbConnect;  // Database connection handler
    private List<Question> questions; // List of questions for the quiz
    private int currentQuestionIndex = 0; // Track the current question
    private Timer quizTimer;  // Timer for quiz duration
    private int timeLeft;     // Time left in seconds

    public DoingQuiz(DbConnct dbConnect) {
        this.dbConnect = dbConnect;
    }

    // Initialize method to load quiz data
    public void initialize(int quizId) {
        try {
            questions = getQuestionsAndOptions(quizId);
            displayQuestion(currentQuestionIndex);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Initialize Timer (e.g., 15 minutes = 900 seconds)
        timeLeft = 900;
        startQuizTimer();
    }

    // Retrieve the questions and options from the database
    private List<Question> getQuestionsAndOptions(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT q.id_question, q.txt_question, q.point, o.id_options, o.option_text, o.est_correct "
                + "FROM questions q "
                + "JOIN options o ON q.id_question = o.id_question "
                + "WHERE q.id_quiz = ?";

        try (PreparedStatement stmt = dbConnect.getConnection().prepareStatement(query)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            Map<Integer, Question> questionMap = new HashMap<>();

            while (rs.next()) {
                int questionId = rs.getInt("id_question");
                String questionText = rs.getString("txt_question");
                int points = rs.getInt("point");
                int optionId = rs.getInt("id_options");
                String optionText = rs.getString("option_text");
                boolean isCorrect = rs.getBoolean("est_correct");

                Question question = questionMap.get(questionId);
                if (question == null) {
                    question = new Question(questionId, questionText, points);
                    questionMap.put(questionId, question);
                }

                question.addOption(new Option(optionId, optionText, isCorrect));
            }

            questions.addAll(questionMap.values());
            System.out.println("Loaded " + questions.size() + " questions.");  // Debug output
        }
        return questions;
    }

    // Display the current question and its options
    private void displayQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            Question question = questions.get(index);
            System.out.println("Displaying Question: " + question.getText());  // Debug output
            questionLabel.setText(question.getText());

            optionsVBox.getChildren().clear(); // Clear previous options

            ToggleGroup optionsGroup = new ToggleGroup();  // Grouping radio buttons for options

            // Add options dynamically
            for (Option option : question.getOptions()) {
                RadioButton optionButton = new RadioButton(option.getText());
                optionButton.setToggleGroup(optionsGroup);
                optionButton.setUserData(option);  // Store the option for later
                optionsVBox.getChildren().add(optionButton);
            }
        }
    }

    // Start the quiz timer
    private void startQuizTimer() {
        quizTimer = new Timer();
        quizTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                updateTimeDisplay();

                if (timeLeft <= 0) {
                    quizTimer.cancel();
                    submitQuiz();  // Automatically submit when time is up
                }
            }
        }, 1000, 1000);  // Update every second
    }

    // Update the time display on the UI
    private void updateTimeDisplay() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));

        // Update the progress bar based on time left
        progressBar.setProgress((double) timeLeft / 900);
    }

    // Go to the next question
    @FXML
    private void nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        }
    }

    // Go to the previous question
    @FXML
    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        }
    }

    // Submit the quiz and calculate the score
    @FXML
    private void submitQuiz() {
        int totalPoints = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            for (Node node : optionsVBox.getChildren()) {
                if (node instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) node;
                    Option selectedOption = (Option) radioButton.getUserData();

                    if (radioButton.isSelected() && selectedOption.isCorrect()) {
                        totalPoints += question.getPoints(); // Assuming points are stored in Question
                    }
                }
            }
        }

        // Display total points or save to database
        System.out.println("Total Points: " + totalPoints);
    }
}
