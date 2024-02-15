package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// activity used to display results of an attempted quiz
public class ResultPage extends AppCompatActivity {

    //component variables
    Button bBackToMenu, bRetakeQuiz;
    TextView resultsText;

    // variable to store the intent extra of the quiz attempt
    QuizAttempt submittedAttempt;

    // database to access information
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        this.setTitle("Results");

        db = DatabaseHelper.getInstance(ResultPage.this);

        // assigns components to variables
        bBackToMenu = findViewById(R.id.bReturnToMenu);
        bRetakeQuiz = findViewById(R.id.bRetakeQuiz);
        resultsText = findViewById(R.id.resultsTextView);

        // sets on click listener
        bBackToMenu.setOnClickListener(resultOnClickListener);
        bRetakeQuiz.setOnClickListener(resultOnClickListener);

        //gets quiz attempt from intent extra
        submittedAttempt = (QuizAttempt) getIntent().getSerializableExtra("QUIZ_ATTEMPT");

        //calls show result method
        showResults();


    }

    // Method used to concat results in a string and get total points from database for the specific user
    private void showResults(){

        // creates string to store the entire concat information
        String resultsString = "";

        int totalPoints = db.retrieveUserTotalPoints(MainActivity.currentUser.getUserName());

        resultsString = "Well done " + submittedAttempt.getUser() + " you have finished the " + '"'+ submittedAttempt.getQuizType()+ '"' +" quiz ";
        resultsString += "with " + submittedAttempt.getCorrect() + " correct and " + submittedAttempt.getIncorrect() + " incorrect answers or ";
        resultsString += submittedAttempt.getPoints() + " points for this attempt.\n";
        resultsString += "Overall you have " + totalPoints + " points."; // add total points for user

        resultsText.setText(resultsString);
    }

    private View.OnClickListener resultOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bRetakeQuiz:
                    // starts new quiz attempt based on the submitted attempt quiz type
                    if(submittedAttempt.getQuizType().equals("Mathematical")){
                        Intent mathIntent = new Intent(ResultPage.this, MathQuiz.class);
                        startActivity(mathIntent);
                    }
                    else {
                        Intent readingIntent = new Intent(ResultPage.this, MultipleChoiceQuiz.class);
                        readingIntent.putExtra("quiztype", submittedAttempt.getQuizType());
                        startActivity(readingIntent);
                    }
                    // add other else ifs for other quiz's
                    break;
                case R.id.bReturnToMenu:
                    onBackPressed();
                    break;
            }
        }
    };

    public void onBackPressed(){
        Intent intent = new Intent(ResultPage.this, MainMenu.class);
        startActivity(intent);
    }
}