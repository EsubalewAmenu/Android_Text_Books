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

                // TODO: Handle quiz submission logic here
                // You can save the quiz data to a database
            }
        });

    }

}