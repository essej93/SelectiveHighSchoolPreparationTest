package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassword extends AppCompatActivity {

    //component variables
    EditText eCurrentPword, eNewPword, eConfirmNewPword;
    Button cancelButton, confirmButton;

    //database variables
    DatabaseHelper db;

    //general variables
    String currentPword, newPword, confirmPword;
    Cursor loginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        this.setTitle("Update Password");

        db = DatabaseHelper.getInstance(ChangePassword.this);

        //assigns component variables
        eCurrentPword = findViewById(R.id.eCurrentPword);
        eNewPword = findViewById(R.id.eNewPword);
        eConfirmNewPword = findViewById(R.id.eConfirmNewPword);

        cancelButton = findViewById(R.id.bCancel);
        confirmButton = findViewById(R.id.bConfirmPword);

        //sets listeners
        cancelButton.setOnClickListener(passwordClickListener);
        confirmButton.setOnClickListener(passwordClickListener);


    }

    private View.OnClickListener passwordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bConfirmPword:
                    if(validatePasswordInformation()){
                        Toast.makeText(ChangePassword.this, "Password change successful", Toast.LENGTH_SHORT).show();
                        updatePassword();
                        Intent mainMenuIntent = new Intent(ChangePassword.this, MainMenu.class);
                        startActivity(mainMenuIntent); // after password change sends user to main menu
                    }
                    break;
                case R.id.bCancel:
                    onBackPressed();
                    break;
            }
        }
    };

    private boolean validatePasswordInformation(){

        currentPword = eCurrentPword.getText().toString();
        newPword = eNewPword.getText().toString();
        confirmPword = eConfirmNewPword.getText().toString();

        // checks for empty fields
        if(currentPword.isEmpty() || newPword.isEmpty() || confirmPword.isEmpty()){
            Toast.makeText(ChangePassword.this, "Please enter all password fields.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // gets user info based on the username provided
        loginInfo = db.retrieveLoginInformation(MainActivity.currentUser.getUserName());

        // checks to see if new password and confirm new password matches
        if(!newPword.equals(confirmPword)){
            Toast.makeText(ChangePassword.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        loginInfo.moveToNext(); // moves the cursor to the first line with user info

        // checks if current password matches the one in the database
        if(!loginInfo.getString(1).equals(currentPword)){
            Toast.makeText(ChangePassword.this, "Current Password Incorrect, please re-enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // returns true if it passes all validation
    }

    private void updatePassword(){
        db.updatePassword(MainActivity.currentUser.getUserName(), newPword);
        updateCurrentUser();
    }

    private void updateCurrentUser(){
        MainActivity.currentUser.setPassword(newPword);
    }
}