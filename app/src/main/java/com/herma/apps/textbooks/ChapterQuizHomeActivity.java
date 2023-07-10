package com.herma.apps.textbooks;

import static com.herma.apps.textbooks.common.TokenUtils.isTokenExpired;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.herma.apps.textbooks.common.TermsAndConditionsActivity;
import com.herma.apps.textbooks.settings.SettingsActivity;
import com.herma.apps.textbooks.ui.about.About_us;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChapterQuizHomeActivity extends AppCompatActivity {

    public RequestQueue queue;
    Button btnQuizRetry;//, resultButton;
    SharedPreferences pre = null;
    public String chapter = "";
    private static final int QUIZ_REQUEST = 2018;
    TextView txtScore, titleTextView, contentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_quiz_home);
        // Add back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.practical));

        pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        chapter = getIntent().getStringExtra("fileName");

        btnQuizRetry = findViewById(R.id.btnQuizRetry);
        txtScore = (TextView) findViewById(R.id.txtScore);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        contentTextView = (TextView) findViewById(R.id.contentTextView);
//        resultButton = findViewById(R.id.resultButton);

        loadQuizApiCall();

//        String questionsFromServer = "[{\"id\":288,\"question\":\"test quiz 2\",\"answer_options\":[\"quiz 2 test option 1\",\"quiz 2 test option 2\",\"quiz 2 test option3\",\"\"],\"correct_answer\":[0],\"answer_description\":\"quiz 2 test description\",\"randomize_options\":\"1\"},{\"id\":287,\"question\":\"test quiz 1\",\"answer_options\":[\"test option 1\",\"test option 2\",\"test option 3\",\"test option 4\"],\"correct_answer\":[1],\"answer_description\":\"test description\",\"randomize_options\":\"1\"}]";
////        String questionsFromServer = "[]";

        btnQuizRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnQuizRetry.getText().toString().equals(getString(R.string.back))) {
                    finish();
                }else if(isOnline()) {
                btnQuizRetry.setVisibility(View.INVISIBLE);
                loadQuizApiCall();
                }else
                    Toast.makeText(getApplicationContext(), getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loadQuizApiCall() {

        String quizApiUrl = new SplashActivity().BASEAPI + "ds_quiz/v1/questions/"+chapter;

        queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, quizApiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                                System.out.println("main resp is " + response);

                        try {
                            JSONArray datas = new JSONArray(response);
                            if(datas.length() > 0){
                                Intent questions = new Intent(ChapterQuizHomeActivity.this, QuizActivity.class);
                                questions.putExtra("showAnswer", false);//show_answer.isChecked());
                                questions.putExtra("outof", 10);//etOutOf.getText().toString());
                                questions.putExtra("que", response);
                                startActivityForResult(questions, QUIZ_REQUEST);
                                btnQuizRetry.setVisibility(View.VISIBLE);

                            }else{
                                titleTextView.setText(getString(R.string.quiz_not_found));
                                contentTextView.setText(getString(R.string.no_quiz_added));
                                btnQuizRetry.setText(getString(R.string.back));
                                btnQuizRetry.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnQuizRetry.setEnabled(true);

                Toast.makeText(getApplicationContext(), getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
                System.out.println("main resp is error " + error);

            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer "+pre.getString("token", "None"));

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag(this);
        queue.add(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_rate:
                Toast.makeText(ChapterQuizHomeActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
                rateApp();
                return true;
            case R.id.action_store:
                Toast.makeText(ChapterQuizHomeActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
                openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
                return true;
            case R.id.action_add_quiz:
                if(isLoggedIn()) {
                    Intent addQuizActivityIntent = new Intent(ChapterQuizHomeActivity.this, TermsAndConditionsActivity.class);
                    addQuizActivityIntent.putExtra("chapterName", getIntent().getStringExtra("chapterName"));
                    addQuizActivityIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                    addQuizActivityIntent.putExtra("fileName", getIntent().getStringExtra("fileName"));
                    startActivity(addQuizActivityIntent);
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(getApplicationContext(), About_us.class));
                return true;
            case R.id.action_exit:
                super.onBackPressed();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isLoggedIn(){

        SharedPreferences pre = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = pre.getString("token", "None");

        boolean isExpired;

        if (token.equals("None")) {
            isExpired = true;
        }
        else {
            isExpired = isTokenExpired(token);
        }


        if(!isExpired) {
            return true;

        }else{
            Toast.makeText(ChapterQuizHomeActivity.this, getString(R.string.sign_in_first), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    private void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void openUrl(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            String timer = data.getStringExtra("timer");
            String[] current_questions = data.getStringArrayExtra("questions");
            String[] answerKey = data.getStringArrayExtra("answerKey");
            String[] response = data.getStringArrayExtra("response");

            String[] responseShouldBe = data.getStringArrayExtra("responseShouldBe");

            int score = 0;
            if (answerKey.length > 0) {
                for (int i = 0; i < answerKey.length; i++) {
                    if (responseShouldBe[i].equals(response[i]))
                        score++;
                }
            }

            int perc = (100 * score) / answerKey.length;
            if (perc >= 74) {
//            rank = "አልፈዋል!";
                txtScore.setBackgroundColor(Color.GREEN);

            } else {
//            rank = "አላለፉም!";
                txtScore.setBackgroundColor(Color.RED);
                txtScore.setTextColor(Color.WHITE);
            }

//            resultButton.setVisibility(View.VISIBLE);
            btnQuizRetry.setVisibility(View.VISIBLE);
            txtScore.setVisibility(View.VISIBLE);

            txtScore.setText("ውጤት : " + score + "/" + answerKey.length + " (" + perc + "%) \nየፈጀብዎት ጊዜ :- " + timer);



//            resultButton.setOnClickListener(v -> {
//                Intent questions = new Intent(getApplicationContext(), AnswersActivity.class);
//
////                questions.putExtra("queId", queId);
//                questions.putExtra("answerKey", answerKey);
//                questions.putExtra("response", response);
//                questions.putExtra("responseShouldBe", responseShouldBe);
//                questions.putExtra("questions", current_questions);
//
//                startActivity(questions);
//            });
        }

    }
}