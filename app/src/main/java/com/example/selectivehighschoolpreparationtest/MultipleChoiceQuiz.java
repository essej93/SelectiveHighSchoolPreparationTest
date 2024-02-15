package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Class which is used for both the thinking skills/reading quiz's
// the class loads the questions based on the extra QUIZ_TYPE_EXTRA sent from the main menu
public class MultipleChoiceQuiz extends AppCompatActivity {

    //quiz type EXTRA
    String QUIZ_TYPE_EXTRA = "";

    // component variables
    Button bBack, bNext, bSubmit;
    ImageButton bInfo;
    TextView questionTextView;
    RadioButton rb1Answer, rb2Answer, rb3Answer, rb4Answer, selectedAnswer;
    RadioGroup answerGroup;
    LinearLayout multipleChoiceLayout;

    // database
    DatabaseHelper db;

    //class variables
    ArrayList<Question> questions;
    int currentQuestion;
    String[] answers; // holds the users input answers
    int[] checkedAnswers; // holds the id of the radio button that was checked for a specific question
    boolean nextQuestion;

    //result variables to create attempt object to load into database
    QuizAttempt newAttempt;
    int correct, incorrect;
    String date;

    //Animation variables
    TranslateAnimation trans1, trans2;
    AlphaAnimation alpha1, alpha2;
    AnimationSet set1, set2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_quiz);

        //gets extra from intent which determines quiz type
        QUIZ_TYPE_EXTRA = getIntent().getExtras().getString("quiztype");

        this.setTitle(QUIZ_TYPE_EXTRA + " Quiz"); // sets title



        // assigns component variables
        bBack = findViewById(R.id.bBackMultipleChoice);
        bNext = findViewById(R.id.bNextMultipleChoice);
        bSubmit = findViewById(R.id.bSubmitMultipleChoice);
        bInfo = findViewById(R.id.bMultipleChoiceInfo);
        multipleChoiceLayout = findViewById(R.id.multipleChoiceLayout);

        questionTextView = findViewById(R.id.textMultipleQuestion);

        rb1Answer = findViewById(R.id.rb1MultipleChoice);
        rb2Answer = findViewById(R.id.rb2MultipleChoice);
        rb3Answer = findViewById(R.id.rb3MultipleChoice);
        rb4Answer = findViewById(R.id.rb4MultipleChoice);

        answerGroup = findViewById(R.id.answerRadioGroup);

        // sets listeners
        answerGroup.setOnCheckedChangeListener(radioGroupListener);
        bBack.setOnClickListener(multipleChoiceButtonListener);
        bNext.setOnClickListener(multipleChoiceButtonListener);
        bSubmit.setOnClickListener(multipleChoiceButtonListener);
        bInfo.setOnClickListener(multipleChoiceButtonListener);


        // assigns db variable
        db = DatabaseHelper.getInstance(MultipleChoiceQuiz.this);

        // hides submit button
        bSubmit.setVisibility(View.GONE);

        //instantiates variables
        answers = new String[5];
        checkedAnswers = new int[5];
        currentQuestion = 0;
        questions = new ArrayList<Question>();


        loadQuestions(); // loads questions from db
        randomiseAnswerOrder(); // randomises the answer order for each question

        // sets the first question/answers
        updateQuestion();

        //sets the start time of the current attempt
        DateFormat simpleDateF = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        date = simpleDateF.format(new Date());


        //sets animation duration for alpha animation
        alpha1 = new AlphaAnimation(1, 0);
        alpha2 = new AlphaAnimation(0, 1);

        alpha1.setDuration(250);
        alpha2.setDuration(250);

    }

    // listener for the buttons
    private View.OnClickListener multipleChoiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bBackMultipleChoice:
                    if(currentQuestion != 0){ // checks if we're not on the first question
                        nextQuestion = false;
                        animateQuestion();
                    }
                    break;
                case R.id.bNextMultipleChoice:
                    if(currentQuestion != (questions.size()-1)){ // checks if we're not on the last question
                        nextQuestion = true;
                        animateQuestion();
                    }
                    break;
                case R.id.bSubmitMultipleChoice:
                    submitQuiz();
                    break;
                case R.id.bMultipleChoiceInfo:
                    showInfo();
                    break;
            }

        }
    };

    // listener for the radio buttons
    private RadioGroup.OnCheckedChangeListener radioGroupListener = new RadioGroup.OnCheckedChangeListener(){

        // on checked gets the selectedAnswer radio button
        // then collects the ID and puts it in the checked answer array
        // it then collects the selected answer text and stores it in the answers array
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {

                selectedAnswer = (RadioButton) findViewById(id);
                checkedAnswers[currentQuestion] = selectedAnswer.getId();

                answers[currentQuestion] = selectedAnswer.getText().toString();

        }
    };

    // method used to update on screen info to next question
    private void nextQuestion(){

        currentQuestion++;

        if(currentQuestion == (questions.size()-1)) bSubmit.setVisibility(View.VISIBLE); // hides submit button if not last question

        updateQuestion();
    }



    // method used to update on screen info to previous question
    private void previousQuestion(){

        currentQuestion--;

        if(currentQuestion < (questions.size()-1)) bSubmit.setVisibility(View.GONE); // hides submit button if not last question

        updateQuestion();
    }

    // method used when submit button is pressed
    // method makes calls to other methods to ensure all questions have been answered.
    // it then collects the data and inserts it into the database and then goes to the
    // results page
    private void submitQuiz(){

        if(checkAnswered()){
            calculateAttempt();
            if(db.insertAttempt(newAttempt)){
                Toast.makeText(MultipleChoiceQuiz.this, "Attempt Submitted Successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent(MultipleChoiceQuiz.this, ResultPage.class);
                resultIntent.putExtra("QUIZ_ATTEMPT", newAttempt);
                startActivity(resultIntent);
            }
            else{
                Toast.makeText(MultipleChoiceQuiz.this, "Oh no! Something went wrong.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // method used to ensure all questions have been answered
    private boolean checkAnswered(){
        boolean notAnswered = true;

        for(int x = 0; x < answers.length; x++){
            // checks if answer is null
            if(answers[x] == null || answers[x].equals("")){
                Toast.makeText(MultipleChoiceQuiz.this, "You have not answered question: " + (x+1), Toast.LENGTH_SHORT).show();
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

        newAttempt = new QuizAttempt(MainActivity.currentUser.getUserName(), QUIZ_TYPE_EXTRA, correct, incorrect, date);
        newAttempt.calculatePoints();
    }

    // method used to update the question/answers on screen
    private void updateQuestion(){

        // gets the current question and sets it in the text view
        questionTextView.setText(questions.get(currentQuestion).getQuestionText());

        // runs through the answers in the question and sets them to each radio button
        for(int i = 0; i < answerGroup.getChildCount(); i++){
            ((RadioButton) answerGroup.getChildAt(i)).setText(questions.get(currentQuestion).getAnswersOrder()[i]);
        }

        // checks to see if the user has answered the current question
        // if so then it will automatically check the one the user had checked previously
        // otherwise it will uncheck the radio buttons if the question has not been answered
        if(checkedAnswers[currentQuestion] != 0){
            answerGroup.check(checkedAnswers[currentQuestion]);
        } else {
            // as the clearCheck() function causes the oncheckedchanged listener to be called twice
            // we first clear the listener, uncheck the radio button then set the listener again
            answerGroup.setOnCheckedChangeListener(null);
            answerGroup.clearCheck();
            answerGroup.setOnCheckedChangeListener(radioGroupListener);
        }

    }

    // method used to load questions from the database.
    private void loadQuestions(){
        // gets array of randomised ID's
        Integer questionIDs[] = randomiseQuestions();

        String tablename = "";

        if(QUIZ_TYPE_EXTRA.equals("Reading")) tablename = DatabaseHelper.READING_QUIZ_TABLE_NAME;
        else if (QUIZ_TYPE_EXTRA.equals("Thinking")) tablename = DatabaseHelper.THINKING_QUIZ_TABLE_NAME; // placeholder for thinking skill

        // for loop runs 5 times to get 5 questions
        for(int x = 0; x < 5; x++){

            // collects the question info from the db using the table name and questionIDs[x]
            Cursor questionCur = db.retrieveQuestion(tablename, questionIDs[x]);

            questionCur.moveToNext(); // ensures we're on the first line of the cursor

            // creates string to hold the incorrect answers related to the question
            String[] incorrectAnswers = new String[3];

            // cursor collects the incorrect answers related to the current question
            Cursor incorrectAnswersCur = db.retrieveIncorrectAnswers(questionIDs[x], QUIZ_TYPE_EXTRA);


            int incorrectAnswerCounter = 0;
            // while loop runs through the incorrect answer cursor and assigns them to the string array
            while(incorrectAnswersCur.moveToNext()){
                incorrectAnswers[incorrectAnswerCounter] = incorrectAnswersCur.getString(1);
                incorrectAnswerCounter++;
            }

            // adds collected question information to the questions ArrayList
            questions.add(new Question(questionCur.getString(1), questionCur.getString(2), incorrectAnswers));
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

    // after questions have been loaded this method will generate a random order of the answers
    // and the order will exist in the questions object.
    // this order will stay the same until a new activity is created to ensure consistency when
    // navigating through questions
    private void randomiseAnswerOrder(){

        for(Question question : questions){
            question.generateAnswersOrder();
        }

    }

    private void animateQuestion(){

        if(nextQuestion){
            trans1 = new TranslateAnimation(0, -1300, 0, 0);
            trans2 = new TranslateAnimation(1300, 0, 0, 0);
        }
        else{
            trans1 = new TranslateAnimation(0, 1300, 0, 0);
            trans2 = new TranslateAnimation(-1300, 0, 0, 0);
        }

        trans1.setDuration(500);
        trans2.setDuration(500);

        //creates animation sets to run the animations at the same time
        set1 = new AnimationSet(true);
        set1.addAnimation(trans1);
        set1.addAnimation(alpha1);

        set2 = new AnimationSet(true);
        set2.addAnimation(trans2);
        set2.addAnimation(alpha2);

        set1.setAnimationListener(animationListener);

        multipleChoiceLayout.startAnimation(set1);
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // runs the nextQuestion() or previousQuestion() methods based on what it's doing
            if(nextQuestion){
                nextQuestion();
            } else previousQuestion();

            multipleChoiceLayout.startAnimation(set2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    // sends user back to main menu on phone back button pressed
    public void onBackPressed(){
        Intent intent = new Intent(MultipleChoiceQuiz.this, MainMenu.class);
        startActivity(intent);
    }

    //method to show information
    public void showInfo(){
        String title = QUIZ_TYPE_EXTRA + " Quiz Information";
        String msg = "- All questions must be answered before submitting.\n";
        msg += "- If you hit the back button on the phone the attempt will not be submitted.";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();
    }

}