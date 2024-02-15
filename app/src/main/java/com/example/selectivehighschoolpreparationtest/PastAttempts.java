package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class PastAttempts extends AppCompatActivity {

    // component variables
    Spinner sortBySpinner;
    ListView attemptListView;
    TextView headerText;
    Button bBackButton;

    //db variables
    DatabaseHelper db;
    Cursor pastAttempts;

    //general variables
    String[] pastAttemptsArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_attempts);

        this.setTitle("Past Attempts");

        //gets db instance
        db = DatabaseHelper.getInstance(PastAttempts.this);

        // assigning components
        sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);
        attemptListView = findViewById(R.id.pastAttemptList);
        bBackButton = findViewById(R.id.bBackAttempts);

        // creates spinner adapter which provides drop down list for spinner
        ArrayAdapter<CharSequence> dropDownAdapter = ArrayAdapter.createFromResource(this,
                R.array.sortby, android.R.layout.simple_spinner_item);
        dropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(dropDownAdapter); // sets adapter to spinner


        // sets button and spinner listeners
        sortBySpinner.setOnItemSelectedListener(spinnerListener);


        //calls updateHeader which updates the header of the list with the user and total points
        updateHeader();

        // sets back button listener
        bBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainMenuIntent = new Intent(PastAttempts.this, MainMenu.class);
                startActivity(mainMenuIntent);
            }
        });

    }

    // listener for the drop down/spinner menu
    public AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            switch(pos){
                // if any other sort methods are added they can be added here
                // and the database method will need to be updated to accept the new sort method argument
                // case for selected by date
                case 0:
                    updateList("date");
                    break;

                // case for sorting by points
                case 1:
                    updateList("points");
                    break;
                // case for sorting by type
                case 2:
                    updateList("quiz");
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    // updates the header of the list with the username/overall points
    public void updateHeader(){
        int totalPoints = db.retrieveUserTotalPoints(MainActivity.currentUser.getUserName());
        String headerString = "";
        headerString += "Hi " + MainActivity.currentUser.getUserName() + ", you have earned ";
        headerString += totalPoints + " points in the following attempts";

        headerText = new TextView(this);
        headerText.setTypeface(Typeface.DEFAULT_BOLD);
        headerText.setText(headerString);

        attemptListView.addHeaderView(headerText);
    }

    // updates the list of the past attempts based on the sort type
    public void updateList(String sortType){

        // collects the cursor for the users attempts from the database
        pastAttempts = db.retrieveUserAttempts(MainActivity.currentUser.getUserName(), sortType);

        // checks to ensure the user has attempts
        if(pastAttempts.getCount() != 0){
            pastAttemptsArray = new String[pastAttempts.getCount()];
            int count = 0;
            while(pastAttempts.moveToNext()){
                String currentAttempt = "";
                currentAttempt += '"' + pastAttempts.getString(0) + '"' + " area - ";
                currentAttempt += "attempt started on " + pastAttempts.getString(1);
                currentAttempt += " - points earned " + pastAttempts.getInt(2);

                pastAttemptsArray[count] = currentAttempt;

                count++;
            }

            // toast to confirm sort method
            Toast.makeText(PastAttempts.this, "Sorted by " + sortType, Toast.LENGTH_SHORT).show();
        }
        // if user has no attempts then sets array string to no attempts found
        else{
            pastAttemptsArray = new String[1];
            pastAttemptsArray[0] = "No attempts found";
        }

        // creates adapter
        ArrayAdapter<String> attemptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pastAttemptsArray);

        attemptListView.setAdapter(attemptAdapter); // sets adapter



    }

}