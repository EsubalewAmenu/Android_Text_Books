package com.herma.apps.textbooks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddQuizActivity extends AppCompatActivity {

    private EditText questionEditText, option1EditText, option2EditText, option3EditText, option4EditText,
            answerDescriptionEditText;
    private Spinner correctAnswerSpinner;
    private Button submitQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);


        // Initialize views
        questionEditText = findViewById(R.id.questionEditText);
        option1EditText = findViewById(R.id.option1EditText);
        option2EditText = findViewById(R.id.option2EditText);
        option3EditText = findViewById(R.id.option3EditText);
        option4EditText = findViewById(R.id.option4EditText);
        correctAnswerSpinner = findViewById(R.id.correctAnswerSpinner);
        answerDescriptionEditText = findViewById(R.id.answerDescriptionEditText);
        submitQuizButton = findViewById(R.id.submitQuizButton);


        // Set up spinner adapter with options from string array resource
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.correct_answer_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        correctAnswerSpinner.setAdapter(adapter);


        // Set click listener for submit button
        submitQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user inputs
                String question = questionEditText.getText().toString();
                String option1 = option1EditText.getText().toString();
                String option2 = option2EditText.getText().toString();
                String option3 = option3EditText.getText().toString();
                String option4 = option4EditText.getText().toString();
                String correctAnswer = correctAnswerSpinner.getSelectedItem().toString();
                String answerDescription = answerDescriptionEditText.getText().toString();


                // Perform validation
                if (question.isEmpty()) {
                    questionEditText.setError("Question is required");
                    return;
                }

                if (option1.isEmpty()) {
                    option1EditText.setError("Option 1 is required");
                    return;
                }

                if (option2.isEmpty()) {
                    option2EditText.setError("Option 2 is required");
                    return;
                }


                if ((option3.isEmpty() && correctAnswer.equalsIgnoreCase("Option C")) ||
                        (option4.isEmpty() && correctAnswer.equalsIgnoreCase("Option D")) ||
                        (correctAnswer.isEmpty() || correctAnswer.equals("Select the answer"))) {

                    TextView selectedTextView = (TextView) correctAnswerSpinner.getSelectedView();
                    selectedTextView.setError("Please select a correct answer");
                    selectedTextView.requestFocus();
                    return;
                }

                System.out.println("fileName is " + getIntent().getStringExtra("fileName"));

//                int[] correctAnswerArray = new int[1];
//
//                if(correctAnswer.equalsIgnoreCase("Option A")) correctAnswerArray[0] = 0;
//                else if(correctAnswer.equalsIgnoreCase("Option B")) correctAnswerArray[0] = 1;
//                else if(correctAnswer.equalsIgnoreCase("Option C")) correctAnswerArray[0] = 2;
//                else if(correctAnswer.equalsIgnoreCase("Option D")) correctAnswerArray[0] = 3;

//                int[] correctAnswerArray = new int[1];
                JSONArray correctAnswerArray = new JSONArray();

                if(correctAnswer.equalsIgnoreCase("Option A")) correctAnswerArray.put(0);
                else if(correctAnswer.equalsIgnoreCase("Option B")) correctAnswerArray.put( 1);
                else if(correctAnswer.equalsIgnoreCase("Option C")) correctAnswerArray.put( 2);
                else if(correctAnswer.equalsIgnoreCase("Option D")) correctAnswerArray.put( 3);

//                questionArray.put(0);



                // Prepare data for submission
                JSONObject quizData = new JSONObject();
                try {
                    JSONArray questionArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("p", question);
                    questionArray.put(jsonObject);

                    JSONArray optionsArray = new JSONArray();
                    jsonObject = new JSONObject();
                    jsonObject.put("p", option1);

                    JSONArray optionAArray = new JSONArray();
                    optionAArray.put(jsonObject);
                    optionsArray.put(optionAArray);

                    jsonObject = new JSONObject();
                    jsonObject.put("p", option2);

                    JSONArray optionBArray = new JSONArray();
                    optionBArray.put(jsonObject);
                    optionsArray.put(optionBArray);

                    jsonObject = new JSONObject();
                    jsonObject.put("p", option3);
                    JSONArray optionCArray = new JSONArray();
                    optionCArray.put(jsonObject);
                    optionsArray.put(optionCArray);

                    jsonObject = new JSONObject();
                    jsonObject.put("p", option4);
                    JSONArray optionDArray = new JSONArray();
                    optionDArray.put(jsonObject);
                    optionsArray.put(optionDArray);

                    quizData.put("question", questionArray);
                    quizData.put("options", optionsArray);
                    quizData.put("correctAnswer", correctAnswerArray);

                    JSONArray explanationArray = new JSONArray();
                    JSONObject explanationObject = new JSONObject();
                    explanationObject.put("p", answerDescription);
                    explanationArray.put(explanationObject);

                    quizData.put("answerDescription", explanationArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // TODO: Send quizData to the web server for submission
                // You can use networking libraries like Retrofit or Volley to handle the API request

                // For testing purposes, log the prepared data
                System.out.println("QuizData for submition" + quizData.toString());
            }
        });

    }

}