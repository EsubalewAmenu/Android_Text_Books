package com.herma.apps.textbooks;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChapterQuizHomeActivity extends AppCompatActivity {

    public RequestQueue queue;
    Button btnQuizRetry;
    SharedPreferences pre = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_quiz_home);
        // Add back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        btnQuizRetry = findViewById(R.id.btnQuizRetry);

        loadQuizApiCall();

    }

    private void loadQuizApiCall() {

        String quizApiUrl = new SplashActivity().BASEAPI + "ds_questions/v1/ enter quiz full url";

        queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, quizApiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                                System.out.println("main resp is " + response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnQuizRetry.setEnabled(true);

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
        if(id == android.R.id.home){
            // Handle clicks on the back button (the left arrow in the toolbar)
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}