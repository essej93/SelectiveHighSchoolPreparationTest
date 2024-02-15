### Selective High School Preparation Test - Android Studio
# Description
This is an android app i developed in my second year which simulates an app that students would possibly use to sit multiple choice and open ended questions. The app uses SQLite to manage the quiz questions and uses, this is all done on the local device.

# Design notes:
The design is simple and easy to use. All error handling is done within the program and users should not be able to enter anything that could cause the program to crash.
The quiz activities are split into 2, one for non multiple choice (aka MathQuiz) which houses all the functions for the math quiz and one for multiple choice, which handles both the Reading and Thinking quiz’s where the questions are loaded based on the type of quiz which is passed through the intent.
The app is designed to save the users answers/selections while the quiz attempt is in progress so when the user navigates back/fourth through the quiz, the previous answers they have input will be populated into the answer field depending on the question and whether they’ve answered it.
Toasts have been used throughout the app to ensure the user is updated on successful/unsuccessful execution of specific activities and actions.
Questions on all quiz’s have animation which translates them either left or right depending on whether the user is selecting the next question or previous question, they questions will also fade out then back in while this happens.

# Storing/Loading data
Storing and Loading is done through the local SQLite database on the phone. The MainActivity, will check if the questions exist in the database (which determines if this is the first time launching the app) and if it determines that the questions do not exist, it will load the questions into the database from the array of questions included in the DatabaseHelper class. A toast will be displayed on initial launch to notify that these questions have been loaded. Every time the app is launched afterwards, the questions will already be stored in the database and accessed directly through the database.
After a user registers on the app, their user data will be stored in the database in which they will be able to login, even when they close the app and come back to it , they will be able to log back in with credentials they registered with earlier.
After the user finishes a quiz, the quiz will be loaded into the database and will be identified through the users username as well as the quiz type and time taken.
These attempts are then viewed/accessed through the PastAttempts activity where they can be sorted.
When a user quits the app and logs back in, all attempts will still be accessible as they are saved to the database.
