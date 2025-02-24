package com.herma.apps.textbooks;

import static com.herma.apps.textbooks.common.TokenUtils.isTokenExpired;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.herma.apps.textbooks.settings.LanguageHelper;
import com.herma.apps.textbooks.settings.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    public static String BASEAPI = "https://datascienceplc.com/api/";
//    public static String BASEAPI = "http://192.168.0.185:8040/api/";
    public RequestQueue queue;
    public static String USERNAME = "public-api-user", PAZZWORD = "public-api-password";

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 9001;

    SharedPreferences prefs = null;

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

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = prefs.getString("language_code", "None");

        if(languageCode.equals("None"))
            startActivity(new Intent(SplashActivity.this, SettingsActivity.class));

//        getLastUpdated();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



            String token = prefs.getString("token", "None");

        boolean isExpired;

            if (token.equals("None")) {
                isExpired = true;
            }
            else {
                isExpired = isTokenExpired(token);
            }

//        if(!(prefs.getString("token", "None")).equals("None")){
                if(!isExpired){
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

/////////////////////

// localhost
//        prefs.edit().putString("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwNDAiLCJpYXQiOjE3MzY3NTQxMTcsIm5iZiI6MTczNjc1NDExNywiZXhwIjoxODMxMzYyMTE3LCJkYXRhIjp7InVzZXIiOnsiaWQiOiIxIn19fQ.Eh78qJD1JrlMeSMwAnF19FyJM4u5mtCHL0uK5iQgCqI").apply();
//        prefs.edit().putString("user_email", "dsfas@fdfds.dfs").apply();
//        prefs.edit().putString("user_nicename", "Admin").apply();
//        prefs.edit().putString("user_display_name", "Admin").apply();
//        prefs.edit().putString("username", "cfew").apply();
//        prefs.edit().putString("first_name", "Admin").apply();
//        prefs.edit().putString("last_name", "Admin").apply();
//        prefs.edit().putString("image", "https://media-cldnry.s-nbcnews.com/image/upload/t_fit-1000w,f_avif,q_auto:eco,dpr_2/rockcms/2022-11/avatar-mc-221123-02-78d2b5.jpg").apply();

// server
//        prefs.edit().putString("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2RhdGFzY2llbmNlcGxjLmNvbSIsImlhdCI6MTczNzAyNTMwNSwibmJmIjoxNzM3MDI1MzA1LCJleHAiOjE4MDAwOTczMDUsImRhdGEiOnsidXNlciI6eyJpZCI6IjQ3In19fQ.QjdnEG9cav1Nfy4qTqDg1COQnuzMeyQ_sqsqGJUDhOY").apply();
//        prefs.edit().putString("user_email", "sabaesubalew@gmail.com").apply();
//        prefs.edit().putString("user_nicename", "Saba").apply();
//        prefs.edit().putString("user_display_name", "Saba").apply();
//        prefs.edit().putString("username", "hana").apply();
//        prefs.edit().putString("first_name", "Saba").apply();
//        prefs.edit().putString("last_name", "Esubalew").apply();
//        prefs.edit().putString("image", "https://secure.gravatar.com/avatar/e2f5ae079ba807ea9353bea4cf49b8f4?s=96&d=mm&r=g").apply();

//            openMainActivity();
    }


    private void openMainActivity() {
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
            String photoUrl = "";
            if (account.getPhotoUrl() != null) {
                photoUrl = account.getPhotoUrl().toString();
                // Use the profile URL as needed
            }
//        System.out.println("givenName " + givenName + "FamilyName " + familyName + "userId " + userId + " userName " + userName + " userEmail " + userEmail);

            registerOrSignUp(givenName, familyName, userId, userEmail, photoUrl);
        }catch (Exception e ){}
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void registerOrSignUp(String givenName, String familyName, String userId, String userEmail, String photoUrl) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "wp/v2/users/register";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("google_user_id", userId);
        jsonBody.put("given_name", givenName);
        jsonBody.put("family_name", familyName);
        jsonBody.put("photo_url", photoUrl);
        jsonBody.put("email", userEmail);
        jsonBody.put("registed_with", "google");
        final String requestBody = jsonBody.toString();

        System.out.println("login request to server");
        System.out.println(requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASEAPI+url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("server response is");
                        System.out.println(response);

                        try {
                            JSONObject jsonObj = new JSONObject(response);

                            prefs.edit().putString("token", jsonObj.getString("token")).apply();
                            prefs.edit().putString("user_email", jsonObj.getString("user_email")).apply();
                            prefs.edit().putString("user_nicename", jsonObj.getString("user_nicename")).apply();
                            prefs.edit().putString("user_display_name", jsonObj.getString("user_display_name")).apply();
                            prefs.edit().putString("username", jsonObj.getString("username")).apply();
                            prefs.edit().putString("first_name", jsonObj.getString("first_name")).apply();
                            prefs.edit().putString("last_name", jsonObj.getString("last_name")).apply();
                            prefs.edit().putString("image", jsonObj.getString("image")).apply();

                        openMainActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = error.getMessage();
                System.out.println("Error message: " + errorMessage);
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
                        try {

                            JSONObject jsonObj = new JSONObject(responseBody);

                            prefs.edit().putString("token", jsonObj.getString("token")).apply();
                            prefs.edit().putString("user_email", jsonObj.getString("user_email")).apply();
                            prefs.edit().putString("user_nicename", jsonObj.getString("user_nicename")).apply();
                            prefs.edit().putString("user_display_name", jsonObj.getString("user_display_name")).apply();
                            prefs.edit().putString("username", jsonObj.getString("username")).apply();
                            prefs.edit().putString("first_name", jsonObj.getString("first_name")).apply();
                            prefs.edit().putString("last_name", jsonObj.getString("last_name")).apply();
                            prefs.edit().putString("image", jsonObj.getString("image")).apply();

                        openMainActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else{
                        // Perform sign out
                        mGoogleSignInClient.signOut();
                    }
                } else {
                    // The error does not have a network response
                    System.out.println("Error message: " + error.getMessage());
                }
            }

        })
        {
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
}
