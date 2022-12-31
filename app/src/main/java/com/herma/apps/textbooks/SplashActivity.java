package com.herma.apps.textbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    public static String BASEAPI = "https://datascienceplc.com/api/";
//    public String BASEAPI = "https://192.168.8.101:8082/wp/ds/api/";
    public RequestQueue queue;
    public static String USERNAME = "public-api-user", PAZZWORD = "public-api-password";

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 9001;
    private String clientId = "924950298904-idfco62fpgu65naq9mb8oa6ij8evji5t.apps.googleusercontent.com";
    private String clientSecret = "YOUR_CLIENT_SECRET_HERE";

    private static int SPLASH_SCREEN_TIME_OUT = 1100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        setContentView(R.layout.activity_splash);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLastUpdated();

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });




//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                getLastUpdated();
//
//                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
////                startActivity(intent);
////                finish();
//
//            }
//        }, SPLASH_SCREEN_TIME_OUT);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Tag", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    private void updateUI(GoogleSignInAccount account){

        String userId = account.getId();
        String userName = account.getDisplayName();
        String userEmail = account.getEmail();

        System.out.println("userId " + userId + " userName " + userName + " userEmail " + userEmail);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

        private void getLastUpdated() {

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    String url = "DSSERVICE/v1/last_update";


                    StringRequest stringRequest = new StringRequest(Request.Method.GET, BASEAPI+url ,

                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response != null) {
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

//                                                System.out.println(" adf response is " + jsonObj.getString("last_update"));
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
                            params.put("username", USERNAME);
                            params.put("password", PAZZWORD);
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
