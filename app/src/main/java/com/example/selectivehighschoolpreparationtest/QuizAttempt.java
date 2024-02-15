package com.example.selectivehighschoolpreparationtest;

import java.io.Serializable;

// Quiz attempt class holds all the information of an attempt which can be used to print to screen
// then load into the database
public class QuizAttempt implements Serializable {

    private String user, quizType;
    private int correct, incorrect, points;
    private String date;

    QuizAttempt(String uName, String type, int qCorrect, int qIncorrect, String datetime){
        user = uName;
        quizType = type;
        correct = qCorrect;
        incorrect = qIncorrect;
        date = datetime;
    }

    public void calculatePoints(){
        int calculatedPoints = 0;

        calculatedPoints = correct * 5;
        calculatedPoints -= incorrect * 2;

        points = calculatedPoints;
    }

    public int getPoints() {
        return points;
    }

    public int getCorrect() {
        return correct;
    }

    public int getIncorrect() {
        return incorrect;
    }

    public String getUser() {
        return user;
    }

    public String getQuizType() {
        return quizType;
    }

    public String getDate(){
        return date;
    }



}
