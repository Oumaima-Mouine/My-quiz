package com.example.myinteljquiz.controller.Etudiant;

import com.example.myinteljquiz.model.Quiz;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class QuizCardController {

    @FXML
    private Label quizTitle;
    @FXML
    private Label quizDescription;
    @FXML
    private Label quizTime;

    // Method to set the quiz data
    public void setQuizData(Quiz quiz) {
        quizTitle.setText(quiz.getName());
        quizDescription.setText(quiz.getDescription());
        quizTime.setText("Duration: " + quiz.getTime() + " mins");
    }
}
