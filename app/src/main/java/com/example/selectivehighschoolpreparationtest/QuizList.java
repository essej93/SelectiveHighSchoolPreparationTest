package com.example.selectivehighschoolpreparationtest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


// this class is in charge of implementing images and strings to the quiz list in the main menu
public class QuizList extends ArrayAdapter<String> {

    // arrays which hold the info that needs to be loaded into the list
    public static String[] quizNames = {"Mathematical reasoning", "Thinking skills", "Reading"};
    public static String[] quizComments = {"Test your mathematical skills", "Test your critical thinking", "Test your reading skills"};
    public static Integer[] imageid = {R.drawable.mathico, R.drawable.thinkingico, R.drawable.readingskillsico};
    public Activity context;


    public QuizList(Activity context) {
        super(context, R.layout.quiz_list_layout, quizNames);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        LayoutInflater inflater = context.getLayoutInflater();

        if(convertView == null)
            row = inflater.inflate(R.layout.quiz_list_layout, null, true);
        TextView textViewQuiz = (TextView) row.findViewById(R.id.textViewQuiz);
        TextView textViewQuizComment = (TextView) row.findViewById(R.id.textViewQuizComment);
        ImageView imageViewQuiz = (ImageView) row.findViewById(R.id.imageViewQuiz);

        textViewQuiz.setText(quizNames[position]);
        textViewQuizComment.setText(quizComments[position]);
        imageViewQuiz.setImageResource(imageid[position]);

        return  row;
    }
}

