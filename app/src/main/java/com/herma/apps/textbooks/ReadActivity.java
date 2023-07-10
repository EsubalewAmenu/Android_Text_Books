package com.herma.apps.textbooks;

import static com.herma.apps.textbooks.common.TokenUtils.isTokenExpired;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.common.TermsAndConditionsActivity;
import com.herma.apps.textbooks.settings.SettingsActivity;
import com.herma.apps.textbooks.ui.about.About_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ReadActivity extends AppCompatActivity {

    PDFView pdfView;

    private FrameLayout adContainerView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    File f;
    String rewardId = "", storedPhone = "0", TAG = "ReadActivity.java";

    private RewardedAd mRewardedAd;

//    TextView txtTimerValue;
//    ImageButton btnGiftReward;
    long reward_p_id, reward_minutes;


    FloatingActionButton mAddFab, mAddQuizFab, mAddCommentFab;

    // These are taken to make visible and invisible along with FABs
    TextView addQuizActionText, addCommentActionText;

    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


// Apply the theme
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("themeMode","light").equals("dark")) {
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

        pdfView = (PDFView) findViewById(R.id.pdfView);
//        txtTimerValue = (TextView) findViewById(R.id.timerValue);
//        btnGiftReward = (ImageButton) findViewById(R.id.btnGiftReward);

        if (getIntent().getExtras() != null) {

            String filePath = getFilesDir().getPath() + "/Herma/books/";

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            try {
                this.setTitle(getIntent().getStringExtra("chapterName") + " (" + getIntent().getStringExtra("subject") + ")");
                if (new Commons(ReadActivity.this).dec(filePath, getIntent().getStringExtra("fileName") + ".pdf", getIntent().getStringExtra("p"))) {
                    f = new File(ReadActivity.this.getFilesDir() + "nor.pdf");
                    pdfView.fromFile(f)
                            .onRender(new OnRenderListener() {
                                @Override
                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {

//                                    mAdView = findViewById(R.id.adView);
                                    adContainerView = findViewById(R.id.ad_view_container);

                                    AdRequest adRequest = new AdRequest.Builder().build();

                                    if (new Commons(getApplicationContext()).showGoogleAd(1)) {

//                                        mAdView.loadAd(adRequest);

                                        adContainerView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                new Commons(getApplicationContext()).loadBanner(mAdView, getString(R.string.adReader), adContainerView, getWindowManager().getDefaultDisplay());
                                            }
                                        });


                                        InterstitialAd.load(getApplicationContext(), getString(R.string.adReaderInt), adRequest, new InterstitialAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                                // The mInterstitialAd reference will be null until
                                                // an ad is loaded.

//                                            System.out.println("request seconds remaining: isTheirReward");

                                                SharedPreferences sharedPref = ReadActivity.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                                                storedPhone = sharedPref.getString("storedPhone", "0");

//                                                isThereReward();

                                                mInterstitialAd = interstitialAd;
                                                mInterstitialAd.show(ReadActivity.this);
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                // Handle the error
                                                mInterstitialAd = null;
                                            }
                                        });


                                    } else {
                                        adContainerView.setVisibility(View.GONE);
                                    }

                                }
                            }).load();


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
public void commentRelateds(){

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
                                             SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                             String token = pre.getString("token", "None");

                                             boolean isExpired;

                                             if (token.equals("None")) {
                                                 isExpired = true;
                                             }
                                             else {
                                                 isExpired = isTokenExpired(token);
                                             }


                                             if(!isExpired) {

                                             Intent commentIntent = new Intent(ReadActivity.this, CommentActivity.class);
                                             commentIntent.putExtra("chapterName", getIntent().getStringExtra("chapterName"));
                                             commentIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                                             commentIntent.putExtra("fileName", getIntent().getStringExtra("fileName"));
                                             startActivity(commentIntent);
                                             }else{
                                                 Toast.makeText(ReadActivity.this, getString(R.string.sign_in_first), Toast.LENGTH_SHORT).show();

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
            Intent chapterQuizIntent = new Intent(ReadActivity.this, ChapterQuizHomeActivity.class);
            chapterQuizIntent.putExtra("chapterName", getIntent().getStringExtra("chapterName"));
            chapterQuizIntent.putExtra("subject", getIntent().getStringExtra("subject"));
            chapterQuizIntent.putExtra("fileName", getIntent().getStringExtra("fileName"));
            startActivity(chapterQuizIntent);
        }
    });
}
//    private void requestPhoneNumber() {
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(ReadActivity.this);
//        builder.setTitle(R.string.insert_phone_eg);
//
//// Set up the input
//        final EditText input = new EditText(getApplicationContext());
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_PHONE);
//
//        input.setText(storedPhone);
//
//        builder.setView(input);
//
//// Set up the buttons
//        builder.setPositiveButton(R.string.insert_phone, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                        m_Text = input.getText().toString();
//
////                if ((input.getText().toString()).matches("^(09|07)\\d{8}$")) {
//                if ((input.getText().toString()).matches("^(09)\\d{8}$")) {
////                    btnGiftReward.setVisibility(View.INVISIBLE);
//                    endReward(input.getText().toString());
//
//                    ////////////
//                    SharedPreferences sharedPref = ReadActivity.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString("storedPhone", input.getText().toString());
//                    editor.apply();
//                    //////////////
//                } else requestPhoneNumber();
//
////                System.out.println("inserted phone is " + input.getText().toString());
//
//            }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//
////                btnGiftReward.setVisibility(View.INVISIBLE);
//                Toast.makeText(ReadActivity.this, R.string.cancel_phone, Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//        builder.show();
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                Toast.makeText(ReadActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
                rateApp();
                return true;
            case R.id.action_store:
                Toast.makeText(ReadActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
                openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
                return true;
            case R.id.action_add_quiz:
                Intent addQuizActivityIntent = new Intent(ReadActivity.this, TermsAndConditionsActivity.class);
                addQuizActivityIntent.putExtra("chapterName", getIntent().getStringExtra("chapterName"));
                addQuizActivityIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                addQuizActivityIntent.putExtra("fileName", getIntent().getStringExtra("fileName"));
                startActivity(addQuizActivityIntent);
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

    public void rewardCountdown(double _minute) {  //

//        txtTimerValue.setVisibility(View.VISIBLE);

        new CountDownTimer((long) (_minute * 60 * 1000), 1000) { // 30000 mili = 30 sec

            public void onTick(long millisUntilFinished) {
//                System.out.println("seconds remaining: " + millisUntilFinished / 1000);


                int secs = (int) (millisUntilFinished / 1000);
                int mins = secs / 60;
                secs = secs % 60;
//                int milliseconds = (int) (millisUntilFinished % 1000);
//                System.out.println(mins + ":"
//                                + String.format("%02d", secs)
////                    + ":"+ String.format("%03d", milliseconds)
//                );

//                txtTimerValue.setText(mins + ":" + String.format("%02d", secs));

            }

            public void onFinish() {
//                btnGiftReward.setVisibility(View.VISIBLE);
//                txtTimerValue.setVisibility(View.INVISIBLE);
                System.out.println("reward done!");
            }
        }.start();
    }

    public boolean isThereReward() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String email = pre.getString("email", "1");

        String pattern = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        if (!email.matches(pattern))
            return false;

        String url = "ds_rewards/v1/is_reward_open/1-12-textbooks?email=" + email;

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
                                if (jsonObj.getInt("code") == 200) {
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
                Map<String, String> params = new HashMap<>();
                params.put("username", SplashActivity.USERNAME);
                params.put("password", SplashActivity.PAZZWORD);
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

        return true;
    }


    public void endReward(String _phone) {

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.adReward),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
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

                                    String custom_data="";
                                    try {
                                        JSONObject jsonBody = new JSONObject();
                                        jsonBody.put("phone", _phone);
                                        jsonBody.put("email", pre.getString("email", "1"));
                                        jsonBody.put("reward_p_id", reward_p_id);
                                        custom_data = jsonBody.toString();
                                    }catch (Exception kl){}

                                    ServerSideVerificationOptions options = new ServerSideVerificationOptions
                                            .Builder()
                                            .setCustomData(custom_data)
                                            .build();
                                    rewardedAd.setServerSideVerificationOptions(options);
                                }
                            });
                        } else {
                            Log.d(TAG, "The rewarded ad wasn't ready yet.");
                        }

                    }
                });

//
////        System.out.println("endReward() reward done!");
//        OkHttpClient rewardClient = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .header("username", SplashActivity.USERNAME)
//                .header("password", SplashActivity.PAZZWORD)
//                .url(new Commons(this).WEBSITE + "/reward/api/items/end_reward?phone=" + _phone + "&id=" + rewardId )
//                .build();
//        rewardClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                btnGiftReward.setVisibility(View.VISIBLE);
//                e.printStackTrace();
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    final String myResponse = response.body().string();
//                    ReadActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            System.out.println("endReward() reward done!");
////                            {"success":true,"error":false,"message":"REWARDED","amount":"0.50000000","currency_code":"ETB"}
////
////                            System.out.println("seconds remaining: " + myResponse);
//
//
//                            try {
//                                JSONObject reader = new JSONObject(myResponse);
//                                if((reader.getString("success")).equals("true") && (reader.getString("message")).equals("REWARDED")){
//
//
//                                    System.out.println("display_message = " + reader.getString("display_message"));
//
//                                    AlertDialog.Builder builder;
//                                    builder = new AlertDialog.Builder(ReadActivity.this);
////                                    $result->amount.$result->currency_code." ሽልማትዎን መዝግበናል። እኛጋር ያልዎት ከ 2 ብር ከበለጠ ወደስልኮ እንልካለን። ስላነበቡ_እናመሰግናለን!"
//                                    builder.setMessage(reader.getString("display_message")) .setTitle("REWARDED!")
//                                            .setCancelable(true)
//                                            .setPositiveButton(R.string.እሺ, new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int id) {
////                                                    finish();
//                                                }
//                                            });
//
//                                    //Creating dialog box
//                                    AlertDialog alert = builder.create();
//                                    //Setting the title manually
////                                    alert.setTitle("AlertDialogExample");
//                                    alert.show();
//
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    });
//                }
//            }
//        });
//
    }
}