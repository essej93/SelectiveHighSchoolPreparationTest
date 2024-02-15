package com.example.selectivehighschoolpreparationtest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Question class used to hold information about an individual question
public class Question {
    private String questionText;
    private String answerText;
    private String[] incorrectAnswers;
    private boolean multipleChoice;
    private String[] answersOrder;

    // constructor for questions with one answer
    public Question(String question, String answer){
        questionText = question;
        answerText = answer;
        multipleChoice = false;
    }

    //constructor for multiple choice questions with 1 answer and 3 incorrect answers
    public Question(String question, String answer, String[] incorrect){
        questionText = question;
        answerText = answer;
        incorrectAnswers = incorrect;
        multipleChoice = true;
    }

    public String getAnswerText() {
        return answerText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public boolean isMultipleChoice() {return multipleChoice;}

    public String[] getIncorrectAnswers() {return incorrectAnswers;}

    public String[] getAnswersOrder() {return answersOrder;}

    // method to check the answer
    public boolean checkAnswer(String answer){

        if(answerText.equals(answer)) return true;
        else return false;

    }

    // method generates array of answers and then shuffles them
    // so the order of the answers are different everytime the quiz is created
    public void generateAnswersOrder(){
        answersOrder = new String[4];
        for(int x = 0; x<answersOrder.length-1; x++){
            answersOrder[x] = incorrectAnswers[x];
        }

        answersOrder[3] = answerText;

        List<String> strList = Arrays.asList(answersOrder);
        Collections.shuffle(strList);
        strList.toArray(answersOrder);
    }

}
