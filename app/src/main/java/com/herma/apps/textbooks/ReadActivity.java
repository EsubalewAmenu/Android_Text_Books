package com.herma.apps.textbooks;

import static com.herma.apps.textbooks.common.TokenUtils.isTokenExpired;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
//import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.common.TermsAndConditionsActivity;
import com.herma.apps.textbooks.settings.SettingsActivity;
import com.herma.apps.textbooks.ui.about.About_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ReadActivity extends AppCompatActivity {

    PDFView pdfView;

    private FrameLayout adContainerView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    File f;
    String rewardId = "", storedPhone = "0", TAG = "ReadActivity.java";

    private RewardedAd mRewardedAd;

        TextView txtTimerValue;
//    ImageButton btnGiftReward;
    long reward_p_id, reward_minutes;


    FloatingActionButton mAddFab, mAddQuizFab, mAddCommentFab;

    // These are taken to make visible and invisible along with FABs
    TextView addQuizActionText, addCommentActionText;

    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

    AdRequest adRequest = new AdRequest.Builder().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


// Apply the theme
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("themeMode", "light").equals("dark")) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        // Add back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        // add back arrow to toolbar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }

//      mAdView = findViewById(R.id.adView);
        adContainerView = findViewById(R.id.ad_view_container);


        pdfView = (PDFView) findViewById(R.id.pdfView);


//        txtTimerValue = (TextView) findViewById(R.id.timerValueTV);
//        btnGiftReward = (ImageButton) findViewById(R.id.btnGiftReward);


        if (getIntent().getExtras() != null) {

            String filePath = getFilesDir().getPath() + "/Herma/books/";

            try {
                this.setTitle(getIntent().getStringExtra("chapterName") + " (" + getIntent().getStringExtra("subject") + ")");
                if (dec(filePath, getIntent().getStringExtra("fileName") + ".pdf", getIntent().getStringExtra("p"))) {
                    f = new File(ReadActivity.this.getFilesDir() + "nor.pdf");

                    pdfView.fromFile(f)
                            .load();

//                    openInterstitialAd();
                    // Delay the call to openInterstitialAd() by 30 seconds
                    new Handler(Looper.getMainLooper()).postDelayed(this::openInterstitialAd, 30000); // 30 sec = 30000

                    isThereReward();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

//        btnGiftReward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                requestPhoneNumber();
//
//            }
//        });

        /////////////

        commentRelateds();
    }


    public void openInterstitialAd(){


        MobileAds.initialize(ReadActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {


                adContainerView.post(new Runnable() {
                    @Override
                    public void run() {
                        AdView adView = new Commons(getApplicationContext()).loadBanner(mAdView, getString(R.string.adReader), adContainerView, getWindowManager().getDefaultDisplay());

                        adView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mAddFab.getLayoutParams();
                                params.setMargins(
                                        params.leftMargin,
                                        params.topMargin,
                                        params.rightMargin,
                                        170 // Add some extra margin
                                );
                                mAddFab.setLayoutParams(params);
                            }
                        });

                    }
                });



                InterstitialAd.load(getApplicationContext(), getString(R.string.adReaderInt), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
//
//                                        SharedPreferences sharedPref = ReadActivity.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
//                                        storedPhone = sharedPref.getString("storedPhone", "0");

                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(ReadActivity.this);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

            }
        });
    }

    public void commentRelateds() {

        // Register all the FABs with their IDs This FAB button is the Parent
        mAddFab = findViewById(R.id.add_fab);

        // FAB button
        mAddQuizFab = findViewById(R.id.add_quiz_fab);
        mAddCommentFab = findViewById(R.id.add_comment_fab);

        // Also register the action name text, of all the FABs.
        addQuizActionText = findViewById(R.id.add_quiz_action_text);
        addCommentActionText = findViewById(R.id.add_comment_action_text);

        // Now set all the FABs and all the action name texts as GONE
        mAddQuizFab.setVisibility(View.GONE);
        mAddCommentFab.setVisibility(View.GONE);

        addQuizActionText.setVisibility(View.GONE);
        addCommentActionText.setVisibility(View.GONE);

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible
        isAllFabsVisible = false;

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        mAddFab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                // when isAllFabsVisible becomes true make all
                // the action name texts and FABs VISIBLE
                mAddQuizFab.show();
                mAddCommentFab.show();
                addQuizActionText.setVisibility(View.VISIBLE);
                addCommentActionText.setVisibility(View.VISIBLE);

                // make the boolean variable true as we
                // have set the sub FABs visibility to GONE
                isAllFabsVisible = true;
            } else {
                // when isAllFabsVisible becomes true make
                // all the action name texts and FABs GONE.
                mAddQuizFab.hide();
                mAddCommentFab.hide();
                addQuizActionText.setVisibility(View.GONE);
                addCommentActionText.setVisibility(View.GONE);

                // make the boolean variable false as we
                // have set the sub FABs visibility to GONE
                isAllFabsVisible = false;
            }
        });
        // below is the sample action to handle add person FAB. Here it shows simple Toast msg.
        // The Toast will be shown only when they are visible and only when user clicks on them
        mAddCommentFab.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  //            view -> Toast.makeText(ReadActivity.this, "Person Added", Toast.LENGTH_SHORT).show();
                                                  if (isLoggedIn()) {
                                                      Intent commentIntent = new Intent(ReadActivity.this, CommentActivity.class);
                                                      commentIntent.putExtra("chapterName", getIntent().getStringExtra("chapterName"));
                                                      commentIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                                                      commentIntent.putExtra("fileName", getIntent().getStringExtra("fileName"));
                                                      startActivity(commentIntent);
                                                  }
                                              }
                                          }
        );

        // below is the sample action to handle add alarm FAB. Here it shows simple Toast msg
        // The Toast will be shown only when they are visible and only when user clicks on them
//    mAddQuizFab.setOnClickListener(
//            view -> Toast.makeText(ReadActivity.this, R.string.coming_soon, Toast.LENGTH_SHORT
//            ).show());

        mAddQuizFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                    Intent chapterQuizIntent = new Intent(ReadActivity.this, ChapterQuizHomeActivity.class);
                    chapterQuizIntent.putExtra("chapterName", getIntent().getStringExtra("chapterName"));
                    chapterQuizIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                    chapterQuizIntent.putExtra("fileName", getIntent().getStringExtra("fileName"));
                    startActivity(chapterQuizIntent);
                }
            }
        });
    }

    public boolean isLoggedIn() {

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = pre.getString("token", "None");

        boolean isExpired;

        if (token.equals("None")) {
            isExpired = true;
        } else {
            isExpired = isTokenExpired(token);
        }


        if (!isExpired) {
            return true;

        } else {
            Toast.makeText(ReadActivity.this, getString(R.string.sign_in_first), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.read, menu);
//        return true;


        getMenuInflater().inflate(R.menu.read, menu);

        MenuItem timerItem = menu.findItem(R.id.timerValueMenu);
        View actionView = getLayoutInflater().inflate(R.layout.menu_item_timer, null);
        timerItem.setActionView(actionView);

        // Access the TextView to update the timer value dynamically
//        TextView
                txtTimerValue = actionView.findViewById(R.id.timerValueTV);
//        txtTimerValue.setText("00:00"); // Set your initial value here

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_rate) {
            Toast.makeText(ReadActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
            rateApp();
            return true;
        } else if (id == R.id.action_store) {
            Toast.makeText(ReadActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
            openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
            return true;
        } else if (id == R.id.action_add_quiz) {
            if (isLoggedIn()) {
                Intent addQuizActivityIntent = new Intent(ReadActivity.this, TermsAndConditionsActivity.class);
                addQuizActivityIntent.putExtra("chapterName", getIntent().getStringExtra("chapterName"));
                addQuizActivityIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                addQuizActivityIntent.putExtra("fileName", getIntent().getStringExtra("fileName"));
                startActivity(addQuizActivityIntent);
            }
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), About_us.class));
            return true;
        } else if (id == R.id.action_exit) {
            super.onBackPressed();
//                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public boolean dec(String filePath, String fileName, String p) {
        try {
            byte[] salt = {69, 121, 101, 45, 62, 118, 101, 114, 69, 121, 101, 45, 62, 118, 101, 114};

            SecretKeyFactory factory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA1");
            String fullPassword = p + fileName;
            KeySpec keySpec = new PBEKeySpec(fullPassword.toCharArray(), salt, 65536,
                    256);
            SecretKey tmp = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            // file decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            IvParameterSpec ivspec = new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            cipher.init(Cipher.DECRYPT_MODE, secret, ivspec);
            FileInputStream fis = new FileInputStream(filePath + fileName.substring(0, fileName.length() - 4));
            FileOutputStream fos = new FileOutputStream(getFilesDir() + "nor.pdf");
            byte[] in = new byte[64];
            int read;
            while ((read = fis.read(in)) != -1) {
                byte[] output = cipher.update(in, 0, read);
                if (output != null)
                    fos.write(output);
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                fos.write(output);
            fis.close();
            fos.flush();
            fos.close();
        }catch (Exception lkj) {
            try{ File file = new File(filePath + fileName.substring(0, fileName.length() - 4));
                if (file.exists())file.delete();}catch (Exception ds){}
        }
        return true;
    }
    public void rewardCountdown(double _minute) {  //

        txtTimerValue.setVisibility(View.VISIBLE);

//        new CountDownTimer((long) (_minute * 60 * 1000), 1000) { // 30000 mili = 30 sec
        new CountDownTimer((long) (1000), 1000) { // 30000 mili = 30 sec

            public void onTick(long millisUntilFinished) {
//                System.out.println("seconds remaining: " + millisUntilFinished / 1000);


                int secs = (int) (millisUntilFinished / 1000);
                int mins = secs / 60;
                secs = secs % 60;
//                int milliseconds = (int) (millisUntilFinished % 1000);
//                System.out.println(mins + ":"
//                                + String.format("%02d", secs)
//                    + ":"+ String.format("%03d", milliseconds)
//                );

                txtTimerValue.setText(mins + ":" + String.format("%02d", secs));

            }

            public void onFinish() {
                System.out.println("reward time done!");

                txtTimerValue.setText("\uD83C\uDF81");
                txtTimerValue.setTextSize(20);


                // Set click listener for the TextView
                txtTimerValue.setOnClickListener(v -> {
                    if (!isLoggedIn()) {
                        Toast.makeText(ReadActivity.this, getString(R.string.sign_in_first), Toast.LENGTH_LONG).show();
                    }else if (ActivityCompat.checkSelfPermission(ReadActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ReadActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(ReadActivity.this, getString(R.string.location_permission_not_granted), Toast.LENGTH_LONG).show();

                        int LOCATION_PERMISSION_REQUEST_CODE = 1;

                        // Permission is not granted, request permissions
                        ActivityCompat.requestPermissions(
                                ReadActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE
                        );
                        return; // Exit the method until permissions are granted

                    } else if (!isLocationEnabled()) {
                        // Notify the user to enable location
                        promptEnableLocation();
                    } else {

                        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ReadActivity.this);

                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(location -> {
                                    if (location != null) {
                                        Geocoder geocoder = new Geocoder(ReadActivity.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                            if (!addresses.isEmpty()) {
                                                String country = addresses.get(0).getCountryName();
//                                                Toast.makeText(ReadActivity.this, "Country from GPS: " + country, Toast.LENGTH_LONG).show();
//                                                System.out.println("Country from GPS: " + country);

                                                //show rewarded ad
                                                showRewardedAd(country);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Location Error", e.getMessage()));
                    }
                });

            }
        }.start();
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private void promptEnableLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enable_location))
                .setMessage(R.string.enable_location_service)
                .setPositiveButton(getString(R.string.enable), (dialog, which) -> {
                    // Redirect to location settings
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    public boolean isThereReward() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        if (!isLoggedIn()) {
            return false;
        }

        String url = "ds_rewards/v1/is_reward_open/1-12-textbooks";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, SplashActivity.BASEAPI + url,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        if (response != null) {
                            try {
//                                        System.out.println(response);
                                // Getting JSON Array node
                                JSONObject jsonObj = new JSONObject(response);

                                System.out.println("response code is " + jsonObj.getString("code"));
                                if (jsonObj.getString("code").equals("available")) {
                                    jsonObj = new JSONObject(jsonObj.getString("response"));
                                    reward_p_id = jsonObj.getLong("id");
                                    reward_minutes = jsonObj.getLong("minutes");

                                    System.out.println("value of id = " + reward_p_id + " and valude of mun is " + reward_minutes);
                                    if (reward_p_id > 0 && reward_minutes > 0) {
                                        // show counter
                                        rewardCountdown(reward_minutes);
                                    }
                                }

                            } catch (final JSONException e) {

                            }

                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Check if the error has a network response

                txtTimerValue.setVisibility(View.GONE);

                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    // Get the error status code
                    int statusCode = response.statusCode;

                    // Get the error response body as a string
                    String responseBody = new String(response.data, StandardCharsets.UTF_8);

                    // Print the error details
                    System.out.println("Error status code: " + statusCode);
                    System.out.println("Error response body: " + responseBody);
                } else {
                    // The error does not have a network response
                    System.out.println("Error message: " + error.getMessage());
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + pre.getString("token", "None"));
                return headers;
            }

        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                stringRequest.setTag(this);
                queue.add(stringRequest);

            return true;
        }


    public void showRewardedAd(String country) {

        txtTimerValue.setVisibility(View.GONE);

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.adReward),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        Log.d(TAG, "Ad was loaded.");
                        mRewardedAd = rewardedAd;
                        if (mRewardedAd != null) {
                            mRewardedAd.show(ReadActivity.this, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    // Handle the reward.
                                    Log.d(TAG, "The user earned the reward.");

                                    SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                    String custom_data = "";
                                    try {
                                        JSONObject jsonBody = new JSONObject();
                                        jsonBody.put("username", pre.getString("username", "1"));
                                        jsonBody.put("reward_p_id", reward_p_id);
                                        jsonBody.put("country", country);
                                        custom_data = jsonBody.toString();
                                    } catch (Exception kl) {
                                    }

                                    System.out.println(custom_data);
                                    ServerSideVerificationOptions options = new ServerSideVerificationOptions
                                            .Builder()
                                            .setCustomData(custom_data)
                                            .build();
                                    rewardedAd.setServerSideVerificationOptions(options);

                                    AlertDialog.Builder builder;
                                    builder = new AlertDialog.Builder(ReadActivity.this);
                                    builder.setMessage(getString(R.string.reward_added)) .setTitle(getString(R.string.rewarded))
                                            .setCancelable(true)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
//                                                    finish();
                                                }
                                            });

                                    //Creating dialog box
                                    AlertDialog alert = builder.create();
                                    //Setting the title manually
//                                    alert.setTitle("AlertDialogExample");
                                    alert.show();

                                }
                            });
                        } else {
                            Log.d(TAG, "The rewarded ad wasn't ready yet.");
                        }

                    }
                });

    }
}