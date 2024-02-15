package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MathQuiz extends AppCompatActivity {

    //Animation variables
    TranslateAnimation trans1, trans2;
    AlphaAnimation alpha1, alpha2;
    AnimationSet set1, set2;

    // probably delete
    static final String QUIZ_TYPE = "Mathematical";

    //Component variables
    LinearLayout mathLayout;
    Button bBack, bNext, bSubmit;
    EditText answerText;
    TextView questionText;
    ImageButton bInfo;

    // database
    DatabaseHelper db;

    // class variables
    ArrayList<Question> questions;
    int currentQuestion;
    String[] answers;
    boolean nextQuestion;

    //result variables to create attempt object to load into database
    QuizAttempt newAttempt;
    int correct, incorrect;
    String date;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_quiz);

        this.setTitle(QUIZ_TYPE + " Quiz");

        // Assigns components to java variables
        mathLayout = findViewById(R.id.linearLayoutMath);

        // Assigns components to java variables
        bBack = findViewById(R.id.bBackButtonMath);
        bNext = findViewById(R.id.bNextButtonMath);
        bSubmit = findViewById(R.id.bSubmitButtonMath);
        bInfo = findViewById(R.id.bInfoMathQuiz);

        // sets button listeners
        bBack.setOnClickListener(mathQuizButtonListener);
        bNext.setOnClickListener(mathQuizButtonListener);
        bSubmit.setOnClickListener(mathQuizButtonListener);
        bInfo.setOnClickListener(mathQuizButtonListener);

        bSubmit.setVisibility(View.GONE); // hides submit button

        // Assigns components to java variables
        answerText = findViewById(R.id.eAnswerMath);
        questionText = findViewById(R.id.questionMathText);

        db = DatabaseHelper.getInstance(MathQuiz.this); // retrieves the database

        questions = new ArrayList<Question>(); // instantiates questions arraylist
        loadQuestions(); // calls loadQuestions function

        currentQuestion = 0; // sets current question to 0
        answers = new String[5];

        // sets the questionText field to the first question in the list
        questionText.setText(questions.get(currentQuestion).getQuestionText());

        //sets the start time of the current attempt
        DateFormat simpleDateF = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        date = simpleDateF.format(new Date());

        //sets animation duration for alpha
        alpha1 = new AlphaAnimation(1, 0);
        alpha2 = new AlphaAnimation(0, 1);

        alpha1.setDuration(250);
        alpha2.setDuration(250);

    }

    // onClicklListener for the buttons in the math quiz
    private View.OnClickListener mathQuizButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bBackButtonMath:

                    // checks if it's not the first question to avoid running any animation or methods
                    if(currentQuestion != 0){
                        nextQuestion = false;
                        animateQuestion(false);
                    }

                    break;
                case R.id.bNextButtonMath:
                    if(currentQuestion != (questions.size()-1)){
                        nextQuestion = true;
                        animateQuestion(true);
                    }

                    break;
                case R.id.bSubmitButtonMath:
                    submitQuiz();
                    break;
                case R.id.bInfoMathQuiz:
                    showInfo();
                    break;
            }

        }
    };

    // method used for updating information to next question
    private void nextQuestion(){

        answers[currentQuestion] = answerText.getText().toString();
        currentQuestion++;


        if(currentQuestion == (questions.size()-1)) bSubmit.setVisibility(View.VISIBLE); // hides submit button if not last question

        questionText.setText(questions.get(currentQuestion).getQuestionText());
        answerText.setText(answers[currentQuestion]);

    }

    // method used for updating information to previous question
    private void previousQuestion(){

        answers[currentQuestion] = answerText.getText().toString();
        currentQuestion--;

        if(currentQuestion < (questions.size()-1)) bSubmit.setVisibility(View.GONE); // hides submit button if not last question

        questionText.setText(questions.get(currentQuestion).getQuestionText()); // sets question text
        answerText.setText(answers[currentQuestion]);
                        /*
                        if(answers[currentQuestion].equals("")){
                            answerText.setText("");
                        } else answerText.setText(answers[currentQuestion]);*/

    }

    // method used when submit button is pressed
    // method makes calls to other methods to ensure all questions have been answered.
    // it then collects the data and inserts it into the database and then goes to the
    // results page
    private void submitQuiz(){

        answers[currentQuestion] = answerText.getText().toString();

        if(checkAnswered()){
            calculateAttempt();
            if(db.insertAttempt(newAttempt)){
                Toast.makeText(MathQuiz.this, "Attempt Submitted Successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent(MathQuiz.this, ResultPage.class);
                resultIntent.putExtra("QUIZ_ATTEMPT", newAttempt);
                startActivity(resultIntent);
            }
            else{
                Toast.makeText(MathQuiz.this, "Oh no! Something went wrong.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // method used to ensure all questions have been answered
    private boolean checkAnswered(){
        boolean notAnswered = true;

        for(int x = 0; x < answers.length; x++){
            if(answers[x].equals("")){
                Toast.makeText(MathQuiz.this, "You have not answered question: " + (x+1), Toast.LENGTH_SHORT).show();
                notAnswered = false;
                break;
            }
        }

        return notAnswered;
    }

    // calculates the answers for the attempt and loads the data into a QuizAttempt object.
    private void calculateAttempt(){

        for(int x = 0; x < answers.length; x++){
            if(answers[x].equals(questions.get(x).getAnswerText())) correct++;
            else incorrect++;
        }

        newAttempt = new QuizAttempt(MainActivity.currentUser.getUserName(), QUIZ_TYPE, correct, incorrect, date);
        newAttempt.calculatePoints();

    }


    // method used to load 5 random questions from the database.
    private void loadQuestions(){
        // gets array of randomised ID's
        Integer questionIDs[] = randomiseQuestions();

        // for loop runs 5 times to get 5 questions
        for(int x = 0; x < 5; x++){

            // collects the question info from the db using the table name and questionIDs[x]
            Cursor questionCur = db.retrieveQuestion(DatabaseHelper.MATH_QUIZ_TABLE_NAME, questionIDs[x]);

            questionCur.moveToNext(); // ensures we're on the first line of the cursor

            // adds collected question to the questions ArrayList
            questions.add(new Question(questionCur.getString(1), questionCur.getString(2)));
        }
    }


    // method used to generate and provide a randomised integer array of 1 - n numbers
    // randomises the ID's for 10 questions in an array
    // then passes it to the method that called it
    // the method that calls it will then use the first 5 integers to load the questions
    // each time this happens the list will be randomised.
    // This method ensures that we don't get duplicate questions in the same quiz
    private Integer[] randomiseQuestions(){
        Integer questionsID[]  = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        List<Integer> idList = Arrays.asList(questionsID);

        Collections.shuffle(idList);
        idList.toArray(questionsID);

        return questionsID;
    }

    // method used to animate the questions
    // creates/runs correct animation based on whether user is going to the next
    // or previous question
    private void animateQuestion(boolean next){

        if(next){
            trans1 = new TranslateAnimation(0, -1300, 0, 0 );
            trans2 = new TranslateAnimation(1300, 0, 0, 0);
        }
        else{
            trans1 = new TranslateAnimation(0, 1300, 0, 0 );
            trans2 = new TranslateAnimation(-1300, 0, 0, 0);
        }

        trans1.setDuration(500);
        trans2.setDuration(500);

        // creates animation sets to run the animations at the same time
        set1 = new AnimationSet(true);
        set1.addAnimation(trans1);
        set1.addAnimation(alpha1);

        set2 = new AnimationSet(true);
        set2.addAnimation(trans2);
        set2.addAnimation(alpha2);

        set1.setAnimationListener(animationListener);

        mathLayout.startAnimation(set1);

    }

    private Animation.AnimationListener animationListener = new  Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // runs the nextQuestion() or previousQuestion() methods based on what it's doing
            if(nextQuestion) {
                nextQuestion();
            } else previousQuestion();

            mathLayout.startAnimation(set2);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    // sends user back to main menu on phone back button pressed
    public void onBackPressed(){
        Intent intent = new Intent(MathQuiz.this, MainMenu.class);
        startActivity(intent);
    }

    //method to show information
    public void showInfo(){
        String title = QUIZ_TYPE + " Quiz Information";
        String msg = "- All questions must be answered before submitting.\n";
        msg += "- If you hit the back button on the phone the attempt will not be submitted.\n";
        msg += "- Please make sure your entered answer is final, any errors will be marked as incorrect";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();
    }


}