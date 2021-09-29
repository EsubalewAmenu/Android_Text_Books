package com.herma.apps.textbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    String url = "https://datascienceplc.com/apps/manager/api/items/get_for_books?what=init";
    public RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);;


                doApiCall();

        SharedPreferences sharedPref = SplashActivity.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String choosedP = sharedPref.getString("choosedP", null);
        String choosedGrade = sharedPref.getString("choosedGrade", null);
        String choosedGradeT = sharedPref.getString("choosedGradeT", "Grade 12");


        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("choosedP", choosedP);
        intent.putExtra("choosedGrade", choosedGrade);
        intent.putExtra("choosedGradeT", choosedGradeT);
        startActivity(intent);
        finish();

    }

    private void doApiCall() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                queue = Volley.newRequestQueue(getApplicationContext());


                StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


//
                                String resp = response;
                                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                pre.edit().putString("que_service", resp ).apply();
//



                                /// start activity
//                                Intent i=new Intent( SplashActivity.this, MainActivity.class);
//                                i.putExtra("response", response);
//                                i.putExtra("rand", random);
//                                i.putExtra("fromAlarm", "no");
//                                startActivity(i);
//                                finish();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", "bloger_api@datascienceplc.com");//public user
                        params.put("password", "public-password");
                        params.put("Authorization", "Basic YmxvZ2VyX2FwaUBkYXRhc2NpZW5jZXBsYy5jb206cHVibGljLXBhc3N3b3Jk");
                        return params;
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
        }, 1500);
    }

}
