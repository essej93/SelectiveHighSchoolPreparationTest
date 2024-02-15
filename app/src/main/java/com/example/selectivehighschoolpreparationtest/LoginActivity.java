package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // component variables
    Button loginButton, backButton;
    EditText username, password;

    // database variable
    DatabaseHelper db;

    // general variables
    String inputUser, inputPassword;
    Cursor loginInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setTitle("Login");

        db = DatabaseHelper.getInstance(LoginActivity.this);

        username = findViewById(R.id.eUserLogin);
        password = findViewById(R.id.ePasswordLogin);

        loginButton = findViewById(R.id.bLoginActivity);
        backButton = findViewById(R.id.bBackLogin);

        loginButton.setOnClickListener(loginListener);
        backButton.setOnClickListener(loginListener);

    }

    private View.OnClickListener loginListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bBackLogin:
                    Intent newIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(newIntent);
                    break;
                case R.id.bLoginActivity:
                    // validates login info provided to see if user exists and if password is correct
                    if(validateLoginInformation()){
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        loginUser(); // sets the current user after login is successful
                        Intent mainMenuIntent = new Intent(LoginActivity.this, MainMenu.class);
                        startActivity(mainMenuIntent); // after login sends user to main menu
                        break;
                    }
                break;
            }
        }
    };

    // method used to validate information provided
    // ensures fields are filled correctly
    // checks to see if the user exists
    // if the user exists, it checks to see if the password is correct
    public boolean validateLoginInformation(){

        inputUser = username.getText().toString();
        inputPassword = password.getText().toString();

        // checks for empty fields
        if(inputUser.isEmpty() || inputPassword.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
            return false;
        }

        // gets user info based on the username provided
        loginInfo = db.retrieveLoginInformation(inputUser);

        // checks to see if any user was found
        if(loginInfo.getCount() == 0){
            Toast.makeText(LoginActivity.this, "This user does not exist", Toast.LENGTH_SHORT).show();
            return false;
        }

        loginInfo.moveToNext(); // moves the cursor to the first line with user info

        // checks if password matches the one in the database
        if(!loginInfo.getString(1).equals(inputPassword)){
            Toast.makeText(LoginActivity.this, "Password Incorrect, please re-enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // returns true if it passes all validation

    }

    // method used to log in the user
    public void loginUser(){
         MainActivity.currentUser = new UserAccount(loginInfo.getString(0), loginInfo.getString(1), loginInfo.getString(2));
    }


}