package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // component variables
    Button backButton, registerButton;
    EditText usernameText, pwordText, confirmPwordText, emailText;

    // database access
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.setTitle("Registration");

        db = DatabaseHelper.getInstance(RegisterActivity.this);

        backButton = findViewById(R.id.bBackRegister);
        registerButton = findViewById(R.id.bRegisterConfirm);
        backButton.setOnClickListener(registerListener);
        registerButton.setOnClickListener(registerListener);

        usernameText = (EditText) findViewById(R.id.eRegisterUserName);
        pwordText = (EditText) findViewById(R.id.eRegisterPword);
        confirmPwordText = (EditText) findViewById(R.id.eRegisterPwordConfirm);
        emailText = (EditText) findViewById(R.id.eEmailRegister);

        usernameText.requestFocus();
    }

    private View.OnClickListener registerListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bBackRegister:
                    Intent newIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(newIntent);
                    break;
                case R.id.bRegisterConfirm:
                    if(validateRegistration()){
                        Toast.makeText(RegisterActivity.this, "Registration successful, please log in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        //intent.putExtra("newUser", newUser);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    // method used to validate information provided in registration
    private boolean validateRegistration(){

        String username = usernameText.getText().toString();
        String pword = pwordText.getText().toString();
        String confirmPword = confirmPwordText.getText().toString();
        String email = emailText.getText().toString();

        // checks if data fields are empty
        if(username.isEmpty() || pword.isEmpty() || confirmPword.isEmpty() || email.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please enter all your information", Toast.LENGTH_SHORT).show();
            return false;
        }
        // checks if both passwords don't match
        else if(!pword.equals(confirmPword)){
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        // checks for invalid email
        else if(!isValidEmail(email)){
            Toast.makeText(RegisterActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        // checks if insert to database table USERACCOUNTS is unsuccessful
        else if(!db.insertUser(username, pword, email)){
            Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // returns true if does not meet any if statement condition
    }


    // method to validate correct email format
    private boolean isValidEmail(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}