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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.herma.apps.textbooks.settings.LanguageHelper;
import com.herma.apps.textbooks.settings.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    public static String BASEAPI = "https://datascienceplc.com/api/";
//    public String BASEAPI = "https://192.168.8.101:8082/wp/ds/api/";
    public RequestQueue queue;
    public static String USERNAME = "public-api-user", PAZZWORD = "public-api-password";

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 9001;

    private static int SPLASH_SCREEN_TIME_OUT = 1050;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.updateLanguage(this);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        setContentView(R.layout.activity_splash);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = prefs.getString("language_code", "None");

        if(languageCode.equals("None"))
            startActivity(new Intent(SplashActivity.this, SettingsActivity.class));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);




        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // The user is already signed in

            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnSkip).setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                openMainActivity();

            }
        }, SPLASH_SCREEN_TIME_OUT);
        }else{

            findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });

            findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMainActivity();
                }
            });
        }



    }

    private void openMainActivity() {
        getLastUpdated();

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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

        try {

        String userId = account.getId();
//        String userName = account.getDisplayName();
        String userEmail = account.getEmail();
        String givenName = account.getGivenName();
        String familyName = account.getFamilyName();

//        System.out.println("givenName " + givenName + "FamilyName " + familyName + "userId " + userId + " userName " + userName + " userEmail " + userEmail);

            registerOrSignUp(givenName, familyName, userId, userEmail);
        }catch (Exception e ){}
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void registerOrSignUp(String givenName, String familyName, String userId, String userEmail) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "wp/v2/users/register";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("google_user_id", userId);
        jsonBody.put("given_name", givenName);
        jsonBody.put("family_name", familyName);
        jsonBody.put("email", userEmail);
        jsonBody.put("registed_with", "google");
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASEAPI+url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        System.out.println(response);

                        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        pre.edit().putString("userId", userId).apply();
                        pre.edit().putString("given_name", givenName).apply();
                        pre.edit().putString("family_name", familyName).apply();
                        pre.edit().putString("email", userEmail).apply();
                        pre.edit().putString("registed_with", "'google'").apply();

                        openMainActivity();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Check if the error has a network response
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    // Get the error status code
                    int statusCode = response.statusCode;

                    // Get the error response body as a string
                    String responseBody = new String(response.data, StandardCharsets.UTF_8);

                    // Print the error details
                    System.out.println("Error status code: " + statusCode);
                    System.out.println("Error response body: " + responseBody);
                    if(statusCode == 409 ){

                        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        pre.edit().putString("userId", userId).apply();
                        pre.edit().putString("given_name", givenName).apply();
                        pre.edit().putString("family_name", familyName).apply();
                        pre.edit().putString("email", userEmail).apply();
                        pre.edit().putString("registed_with", "'google'").apply();

                        openMainActivity();
                    }
                } else {
                    // The error does not have a network response
                    System.out.println("Error message: " + error.getMessage());
                }
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
