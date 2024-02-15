package com.example.selectivehighschoolpreparationtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // creates static instance of database which is accessible
    // from all activities
    private static DatabaseHelper dbInstance;

    public static final String DATABASE_NAME = "PreparationTest.db";
    public static final int DATABASE_VERSION = 1;

    // User account table variables
    public static final String USER_TABLE_NAME = "USERACCOUNTS";
    public static final String USER_COL1 = "USERNAME";
    public static final String USER_COL2 = "PASSWORD";
    public static final String USER_COL3 = "EMAILADDRESS";

    // Column names for quiz tables
    public static final String QUIZ_COL1 = "ID";
    public static final String QUIZ_COL2 = "QUESTION";
    public static final String QUIZ_COL3 = "ANSWER";

    //Math quiz question table
    public static final String MATH_QUIZ_TABLE_NAME = "MATHQUESTIONS";

    //reading quiz question table
    public static final String READING_QUIZ_TABLE_NAME = "READINGQUESTIONS";

    //thinking skills quiz question table
    public static final String THINKING_QUIZ_TABLE_NAME = "THINKINGQUESTIONS";


    //incorrect answer table holds reference to the question ID it is related to
    // and the quiz type
    public static final String INCORRECT_ANSWER_TABLE_NAME = "INCORRECTANSWERS";
    public static final String INCORRECT_ANSWER_COL1 = "ID";
    public static final String INCORRECT_ANSWER_COL2 = "ANSWER";
    public static final String INCORRECT_ANSWER_COL3 = "RELATEDQUESTIONID";
    public static final String INCORRECT_ANSWER_COL4 = "QUIZTYPE";

    //Attempts table
    public static final String QUIZ_ATTEMPT_TABLE_NAME = "ATTEMPTS";
    public static final String ATTEMPT_COL1 = "ID";
    public static final String ATTEMPT_COL2 = "QUIZTYPE";
    public static final String ATTEMPT_COL3 = "USERNAME";
    public static final String ATTEMPT_COL4 = "DATE";
    public static final String ATTEMPT_COL5 = "POINTS";

    // method used to obtain db throughout activities
    public static synchronized DatabaseHelper getInstance(Context context){

        if(dbInstance == null){
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creates USERACCOUNTS table
        db.execSQL("CREATE TABLE " + USER_TABLE_NAME +
                    "(USERNAME TEXT PRIMARY KEY NOT NULL," +
                    "PASSWORD TEXT, EMAILADDRESS TEXT)");

        // creates MATHQUESTIONS table
        db.execSQL("CREATE TABLE " + MATH_QUIZ_TABLE_NAME +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, QUESTION TEXT," +
                "ANSWER TEXT)");

        // creates table for reading multiple choice questions
        db.execSQL("CREATE TABLE " + READING_QUIZ_TABLE_NAME +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT, QUESTION TEXT," +
                    "ANSWER TEXT)");

        // creates table for thinking multiple choice questions
        db.execSQL("CREATE TABLE " + THINKING_QUIZ_TABLE_NAME +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, QUESTION TEXT," +
                "ANSWER TEXT)");

        // creates table to store incorrect answers for multiple choice questions
        db.execSQL("CREATE TABLE " + INCORRECT_ANSWER_TABLE_NAME +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, ANSWER TEXT," +
                "RELATEDQUESTIONID INTEGER, QUIZTYPE TEXT)");

        // creates ATTEMPTS table
        db.execSQL("CREATE TABLE " + QUIZ_ATTEMPT_TABLE_NAME +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, QUIZTYPE TEXT," +
                "USERNAME TEXT, DATE TEXT, POINTS INTEGER)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MATH_QUIZ_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QUIZ_ATTEMPT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + READING_QUIZ_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INCORRECT_ANSWER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + THINKING_QUIZ_TABLE_NAME);

        onCreate(db);
    }

    // method used to insert a user after registration and details have been validated
    public boolean insertUser (String userName, String password, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_COL1, userName);
        contentValues.put(USER_COL2, password);
        contentValues.put(USER_COL3, email);

        long result = db.insert(USER_TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    // method used to insert attempt results to databse
    public boolean insertAttempt(QuizAttempt newAttempt){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ATTEMPT_COL2, newAttempt.getQuizType());
        contentValues.put(ATTEMPT_COL3, newAttempt.getUser());
        contentValues.put(ATTEMPT_COL4, newAttempt.getDate());
        contentValues.put(ATTEMPT_COL5, newAttempt.getPoints());

        long result = db.insert(QUIZ_ATTEMPT_TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }

    }

    public Cursor retrieveUserAttempts(String username, String sortType){

        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = new String[]{ATTEMPT_COL2, ATTEMPT_COL4, ATTEMPT_COL5};
        String selection = ATTEMPT_COL3 + " = ?";
        String[] selectionArgs = new String[]{username};
        String sortOrder = ATTEMPT_COL1 + " DESC";

        // checks what the sort type is and sets the sort order accordingly
        if(sortType.equals("date")){
            sortOrder = ATTEMPT_COL4 + " DESC";
        }
        else if(sortType.equals("points")){
            sortOrder = ATTEMPT_COL5 + " DESC";
        }
        else if(sortType.equals("quiz")){
            sortOrder = ATTEMPT_COL2 + " ASC";
        }

        Cursor cursor = db.query(
                        QUIZ_ATTEMPT_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
        );

        return cursor;
    }

    // retrieves the total points of a user.
    public int retrieveUserTotalPoints(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT SUM(POINTS) FROM ATTEMPTS WHERE USERNAME = '"+username+"'";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToNext();

        int points = cursor.getInt(0);

        return points;
    }

    // retrieves login info based on username provided
    public Cursor retrieveLoginInformation(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM USERACCOUNTS WHERE USERNAME = '"+username+"'";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    // retrieves a specific question based on the tablename/question number
    public Cursor retrieveQuestion(String tablename, int questionNum){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+tablename+" WHERE ID = '"+questionNum+"'";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    // method used to get incorrect questions that related to question ID and quiz type
    public Cursor retrieveIncorrectAnswers(int qID, String type){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+INCORRECT_ANSWER_TABLE_NAME+" WHERE RELATEDQUESTIONID = '"+qID+"' AND QUIZTYPE = '"+type+"'";

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //method used to update password of a user
    public boolean updatePassword(String username, String newPassword){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_COL2, newPassword);

        db.update(USER_TABLE_NAME, contentValues, "USERNAME = ?", new String[] {username});

        return true;
    }




    // -------------First time load data and methods below this comment------------------

    // method used to load all questions for the first time
    // method is called in main activity after checking if this is
    // first time start up.
    public void preLoadData(String tablename, Question[] questions, boolean multipleChoice){
        SQLiteDatabase db = this.getWritableDatabase();

        // checks if its multiple choice or not to determine if it needs to load
        // incorrect questions to another table
        if(!multipleChoice){
            for(int x = 0; x < questions.length; x++){

                ContentValues contentValues = new ContentValues();

                contentValues.put(QUIZ_COL2, questions[x].getQuestionText());
                contentValues.put(QUIZ_COL3, questions[x].getAnswerText());


                db.insert(tablename, null, contentValues);
            }

        } else{

            long questionID;

            // for loop runs through each question to load into database
            for(int x = 0; x < questions.length; x++){

                ContentValues contentValues = new ContentValues();

                contentValues.put(QUIZ_COL2, questions[x].getQuestionText());
                contentValues.put(QUIZ_COL3, questions[x].getAnswerText());


                questionID = db.insert(tablename, null, contentValues);

                String[] incorrectAnswers = questions[x].getIncorrectAnswers();

                // for loop loads each incorrect answer from the array to the data base
                for(int i = 0; i < incorrectAnswers.length; i++){

                    ContentValues incorrectAnswer = new ContentValues();

                    incorrectAnswer.put(INCORRECT_ANSWER_COL2, incorrectAnswers[i]);
                    incorrectAnswer.put(INCORRECT_ANSWER_COL3, questionID);

                    // determines quiztype based on which quiz table it is loading questions for
                    if(tablename.equals(READING_QUIZ_TABLE_NAME)){
                        incorrectAnswer.put(INCORRECT_ANSWER_COL4, "Reading");
                    }
                    else if(tablename.equals(THINKING_QUIZ_TABLE_NAME)){
                        incorrectAnswer.put(INCORRECT_ANSWER_COL4, "Thinking");
                    }

                    db.insert(INCORRECT_ANSWER_TABLE_NAME, null, incorrectAnswer);

                }
            }

        }




    }



    // static final Question array which holds all mathQuestions which are loaded on
    // first time run. Every time after, questions will be accessed directly from the database

    public static final Question[] mathQuestions = {
            new Question("5 * 4 = ?", "20"),
            new Question("81 divided by 9?", "9"),
            new Question("4 * 10 = ?", "40"),
            new Question("1004 divided by 2?", "502"),
            new Question("96 - 30 = ?", "66"),
            new Question("21 divided by 7?", "3"),
            new Question("48 divided by 4?", "12"),
            new Question("7 + 26 = ?", "33"),
            new Question("167 - 22 = ?", "145"),
            new Question("18 + 5 = ?", "23")
    };

    // static final arrays which hold the reading questions
    // these questions also hold an array of incorrect answers which are loaded into a seperate table to the questions
    // and are identified by the quiz type and ID number of the question they are related to
    public static final Question[] readingQuestions = {
            new Question("_______ going to the movies.", "We're", new String[]{"Were", "Where", "Wear"}),
            new Question("Have you ______ about the new game?", "heard", new String[]{"herd", "heired", "hered"}),
            new Question("Synonym for 'happy'", "Cheerful", new String[]{"Pensiveness", "Lugubrious", "Excited"}),
            new Question("Synonym for 'melancholy'", "Sad", new String[]{"Untroubled", "Passionate", "Wild"}),
            new Question('"' + "The dog run down the road" + '"' + " Where is the error in this sentence?", "run", new String[]{"dog", "down", "road"}),
            new Question("Is 'Cromulent' a word?", "No", new String[]{"Yes", "Maybe", "I don't know"}),
            new Question("What is the meaning of the word 'composite'?", "made up of several parts or elements",
                                new String[]{"a whole formed by combining several separate elements", "connect with something else in one's mind", "no longer needed or useful"}),
            new Question("Which word does not have a vowel?", "Fly", new String[]{"Cake", "Frog", "Bird"}),
            new Question("Which word is a verb?", "Mount", new String[]{"Car", "Friend", "Journey"}),
            new Question("Which word is a noun?", "Car", new String[]{"Long", "Run", "Hot"})
    };

    public static final Question[] thinkingSkillsQuestions = {
            new Question("If you're travelling at 100km/hr and an object is travelling at 100km/hr in the same direction, how fast are you travelling relative to the object?", "0km/hr",
                    new String[]{"100km/hr", "50km/hr", "20km/hr"}),
            new Question("If you roll 2 die, what is the probability of getting a 7?", "1/6", new String[]{"1/9", "1/12", "5/36"}),
            new Question("If you're travelling at 120km/hr how far will you travel in 2 and a half hours?", "300km", new String[]{"250km", "240km", "310km"}),
            new Question("What is the airspeed velocity of an african swallow?", "32km/hr", new String[]{"25km/hr", "39km/hr", "15km/hr"}),
            new Question("How long does it take for the earth to rotate once?", "24 hours", new String[]{"12 hours", "32 hours", "48 hours"}),
            new Question("How many days in a leap year?", "366", new String[]{"365", "364", "363"}),
            new Question("You're in a race and you pass the person in second place, what place are you in now?", "2nd place", new String[]{"1st place", "3rd place", "4th place"}),
            new Question("What four-letter word can be written forward, backward, or upside down, and can still be read from left to right?", "NOON", new String[]{"SPADES", "RACECAR", "PEEP"}),
            new Question("You are in a dark room with a box of matches. On a table are a candle, an oil lamp, and a log of firewood. What do you light first?", "The match",
                            new String[]{"The candle", "The fire wood", "The oil lamp"}),
            new Question("An electric train is headed east. Where does the smoke go?", "Electric trains don't produce smoke", new String[]{"West", "East", "No where"})
    };


}
