package com.herma.apps.textbooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.herma.apps.textbooks.common.Commons;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SplashActivity extends AppCompatActivity {

    public String BASEAPI = "https://datascienceplc.com/wp-json/";
//    public String BASEAPI = "https://192.168.8.101:8082/wp/ds/wp-json/";

//    String url = BASEAPI + "available/subjects";
//    String url = "https://datascienceplc.com/apps/manager/api/items/get_for_books?what=init";
    public RequestQueue queue;


    private static int SPLASH_SCREEN_TIME_OUT = 1100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        setContentView(R.layout.activity_splash);
        ;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//
                getLastUpdated();
//


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
        }, SPLASH_SCREEN_TIME_OUT);

    }

        private void getLastUpdated() {

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    String url = "DSSERVICE/v1/last_update";


                    StringRequest stringRequest = new StringRequest(Request.Method.GET, BASEAPI+url ,

                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response != null) {
//                                        System.out.println(" response is " + response);
//        response is {"success":true,"error":false,"activator":{"license_code":"5335","license_type":"1","out_date":"2021-09-22"}}

                                        try {
                                            // Getting JSON Array node
                                            JSONObject jsonObj = new JSONObject(response);
//
//                                        String verif_customer_rewards = "";
//
//
                                            if(jsonObj.getString("success").equals("true") ) {

                                                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                pre.edit().putString("last_update", jsonObj.getString("last_update") ).apply();

                                            }

                                        } catch (final JSONException e) {
                                        }

                                    }
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


}
