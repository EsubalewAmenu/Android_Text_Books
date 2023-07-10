package com.herma.apps.textbooks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.herma.apps.textbooks.common.ContentSubmittedActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AddQuizActivity extends AppCompatActivity {

    private EditText questionEditText, option1EditText, option2EditText, option3EditText, option4EditText,
            answerDescriptionEditText;
    private Spinner correctAnswerSpinner;
    private LinearLayout submitQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

                JSONArray correctAnswerArray = new JSONArray();

                if(correctAnswer.equalsIgnoreCase("Option A")) correctAnswerArray.put(0);
                else if(correctAnswer.equalsIgnoreCase("Option B")) correctAnswerArray.put( 1);
                else if(correctAnswer.equalsIgnoreCase("Option C")) correctAnswerArray.put( 2);
                else if(correctAnswer.equalsIgnoreCase("Option D")) correctAnswerArray.put( 3);


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

                    quizData.put("chapter", getIntent().getStringExtra("fileName"));
                    quizData.put("question", questionArray);
                    quizData.put("options", optionsArray);
                    quizData.put("correct_answers", correctAnswerArray);

                    JSONArray explanationArray = new JSONArray();
                    JSONObject explanationObject = new JSONObject();
                    explanationObject.put("p", answerDescription);
                    explanationArray.put(explanationObject);

                    quizData.put("description", explanationArray);




                    new AlertDialog.Builder(AddQuizActivity.this)
                            .setTitle("Are you sure")
                            .setMessage("Do you want to submit this quiz?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with submission operation

                                    try {
                                        submitQuiz(quizData.toString());
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(R.string.cancel, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // TODO: Send quizData to the web server for submission
                // You can use networking libraries like Retrofit or Volley to handle the API request

                // For testing purposes, log the prepared data
            }
        });

    }

    public void submitQuiz(String requestBody) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = SplashActivity.BASEAPI+"ds_quiz/v1/contribute";

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        System.out.println("post comment response is ");
//                        System.out.println(response);

                        Toast.makeText(getApplicationContext(), getString(R.string.thanks_for_the_quiz), Toast.LENGTH_LONG).show();


                        Intent addQuizActivityIntent = new Intent(AddQuizActivity.this, ContentSubmittedActivity.class);
                        startActivity(addQuizActivityIntent);
                        finish();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                }catch (Exception j){}
            }

        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer "+pre.getString("token", "None"));
                return params;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag(this);
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}