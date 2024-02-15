package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // useraccount which other activities can use
    // to determine the current user that is logged in
    static UserAccount currentUser;

    // component variables
    Button loginButton, registerButton;

    // database variables
    DatabaseHelper appDB;
    Cursor dbChecker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sets header title
        this.setTitle("High School Preparation Test");

        // gets db access
        appDB = DatabaseHelper.getInstance(MainActivity.this);

        // assigned component variables
        loginButton = findViewById(R.id.bLogin);
        registerButton = findViewById(R.id.bRegister);

        // sets on click listener.
        loginButton.setOnClickListener(mainListener);
        registerButton.setOnClickListener(mainListener);

        // calls method to determine if this is the first time
        // launching the app.
        checkFirstTimeLoad();
    }


    private View.OnClickListener mainListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bLogin:
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    break;
                case R.id.bRegister:
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                    break;
            }
        }
    };

    // method used to check if anything exists in the db
    // which determines if the app has been launched before
    // if no questions are found in the db then the questions
    // will be loaded into the db from the DatabaseHelper class
    // then accessed through the db everytime after that
    public void checkFirstTimeLoad(){

        boolean initialLoad = false;

        // collects cursor from MATH question table
        dbChecker = appDB.retrieveQuestion(DatabaseHelper.MATH_QUIZ_TABLE_NAME, 1);

        // checks if there are any entries
        if(dbChecker.getCount() == 0){
            appDB.preLoadData(DatabaseHelper.MATH_QUIZ_TABLE_NAME, DatabaseHelper.mathQuestions, false);
            initialLoad = true;
        }

        // collects cursor from reading question table
        dbChecker = appDB.retrieveQuestion(DatabaseHelper.READING_QUIZ_TABLE_NAME, 1);

        // checks if there are any entries
        if(dbChecker.getCount() == 0){
            appDB.preLoadData(DatabaseHelper.READING_QUIZ_TABLE_NAME, DatabaseHelper.readingQuestions, true);
            initialLoad = true;
        }

        // collects cursor from reading question table
        dbChecker = appDB.retrieveQuestion(DatabaseHelper.THINKING_QUIZ_TABLE_NAME, 1);

        // checks if there are any entries
        if(dbChecker.getCount() == 0){
            appDB.preLoadData(DatabaseHelper.THINKING_QUIZ_TABLE_NAME, DatabaseHelper.thinkingSkillsQuestions, true);
            initialLoad = true;
        }

        if(initialLoad) Toast.makeText(MainActivity.this, "Initial database generated", Toast.LENGTH_SHORT).show();



    }

    public void onBackPressed(){
        // method exists to do nothing other than to
        // stop the user from accessing any other activity through
        // the back button
    }
}