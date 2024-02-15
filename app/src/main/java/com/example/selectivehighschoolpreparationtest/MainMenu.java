package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity  { //ListActivity

    // component variables
    ListView quizListView;
    Button bLogout, bChangePword, bViewAttempts;

    // db access variable
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.setTitle("Main Menu"); // sets title

        // assigns component variables
        bLogout = findViewById(R.id.bLogout);
        bChangePword = findViewById(R.id.bChangePword);
        bViewAttempts = findViewById(R.id.bViewAttempts);

        // assigns db
        db = DatabaseHelper.getInstance(MainMenu.this);

        // assigns listener for buttons
        bLogout.setOnClickListener(mainMenuClickListener);
        bChangePword.setOnClickListener(mainMenuClickListener);
        bViewAttempts.setOnClickListener(mainMenuClickListener);

        // creates header text for listview
        TextView headerText = new TextView(this);
        headerText.setTypeface(Typeface.DEFAULT_BOLD);
        headerText.setText("Select a quiz");

        // assigns listview variable and header text
        quizListView = (ListView) findViewById(R.id.quizListView);
        quizListView.addHeaderView(headerText);

        // creates quizlist adapter and sets adapter for listview
        QuizList quizList = new QuizList(this);
        quizListView.setAdapter(quizList);

        quizListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String type;
                switch(pos){
                    case 1:
                        Intent mathIntent = new Intent(MainMenu.this, MathQuiz.class);
                        startActivity(mathIntent);
                        break;
                    case 2:
                        Intent thinkingMultiChoiceIntent = new Intent (MainMenu.this, MultipleChoiceQuiz.class);
                        type = "Thinking";
                        thinkingMultiChoiceIntent.putExtra("quiztype", type);
                        startActivity(thinkingMultiChoiceIntent);
                        break;
                    case 3:
                        Intent readingMultiChoiceIntent = new Intent(MainMenu.this, MultipleChoiceQuiz.class);
                        type = "Reading";
                        readingMultiChoiceIntent.putExtra("quiztype", type);
                        startActivity(readingMultiChoiceIntent);
                        break;
                }
            }
        });
    }

    // on click listener for the buttons
    private View.OnClickListener mainMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bLogout:
                    onBackPressed(); // calls on back pressed method
                    break;
                case R.id.bChangePword:
                    Intent changePasswordIntent = new Intent(MainMenu.this, ChangePassword.class);
                    startActivity(changePasswordIntent);
                    break;
                case R.id.bViewAttempts:
                    Intent attemptsIntent = new Intent(MainMenu.this, PastAttempts.class);
                    startActivity(attemptsIntent);
                    break;
            }
        }
    };

    // method to logout user either on back or log off button pressed
    public void onBackPressed(){

        // gets the users total points
        int totalPoints = db.retrieveUserTotalPoints(MainActivity.currentUser.getUserName());

        // creates string for toast
        String logOffToast = "Log off successful, " + MainActivity.currentUser.getUserName();
        logOffToast += " you have overall " + totalPoints + " points.";

        // toast to show log off string
        Toast.makeText(MainMenu.this, logOffToast, Toast.LENGTH_LONG).show();

        // wipes the currentUser object and creates default user
        MainActivity.currentUser = new UserAccount();

        // goes back to main activity which is login/register screen
        Intent intent = new Intent(MainMenu.this, MainActivity.class);
        startActivity(intent);
    }


}