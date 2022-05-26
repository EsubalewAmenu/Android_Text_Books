package com.herma.apps.textbooks.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.herma.apps.textbooks.R;

import com.google.android.gms.ads.AdRequest;
import com.herma.apps.textbooks.SplashActivity;
import com.herma.apps.textbooks.common.Commons;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RewardFragment extends Fragment {


//    Button btnStartReward;

    EditText etPhone ;
    TextView tvReward, tvCurrentReward ;
String phoneString, nameString;

//    private RewardedAd mRewardedAd;
    public RequestQueue queue;
    private FrameLayout rewardAdContainerView;
    private AdView rewardAdView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reward, container, false);

//        btnStartReward = (Button) root.findViewById(R.id.btnStartReward);

        etPhone = (EditText) root.findViewById(R.id.etPhone);

        tvReward = (TextView) root.findViewById(R.id.tvReward);
        tvCurrentReward = (TextView) root.findViewById(R.id.tvCurrentReward);

//         new Commons( getContext() ).handleSSLHandshake();

        tvReward.setMovementMethod(LinkMovementMethod.getInstance());
        tvReward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phoneString = etPhone.getText().toString().trim();

                if(phoneString.equals("")  ){
                    Toast.makeText(getContext(), getString(R.string.fill_the_form), Toast.LENGTH_SHORT).show();
                }else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse("http://datascienceplc.com/services?reward=textbooks&" + phoneString));
                    startActivity(browserIntent);
                }
            }
        });

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
        phoneString = pre.getString("phone", "");
        nameString = pre.getString("name", "") ;
        etPhone.setText(phoneString);



        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


//        btnStartReward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//              phoneString = etPhone.getText().toString().trim();
//
//                if(phoneString.equals("")  ){
//                    Toast.makeText(getContext(), getString(R.string.fill_the_form), Toast.LENGTH_SHORT).show();
//                }else {
//                    showUsersReward();
////                    showUsersWaitingTime();
//                }
//            }
//        });

        rewardAdContainerView = root.findViewById(R.id.reward_ad_view_container);

        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
//        rewardAdContainerView.post(new Runnable() {
//            @Override
//            public void run() {
//                new Commons(getContext()).loadBanner(rewardAdView, getString(R.string.adHome), rewardAdContainerView, getActivity().getWindowManager().getDefaultDisplay());
//            }
//        });

        // Create an ad request.
        rewardAdView = new AdView(getContext());
        rewardAdView.setAdUnitId(getString(R.string.adHome));
        rewardAdContainerView.removeAllViews();
        rewardAdContainerView.addView(rewardAdView);

        AdSize adSize = new Commons(getContext()).getAdSize(rewardAdContainerView, getActivity().getWindowManager().getDefaultDisplay());
        rewardAdView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        rewardAdView.loadAd(adRequest);

        rewardAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                System.out.println("ad clicked by the user");

                phoneString = etPhone.getText().toString().trim();

                if(phoneString.equals("")  ){
                    Toast.makeText(getContext(), getString(R.string.fill_the_form), Toast.LENGTH_SHORT).show();
                }else {
//                    showUsersReward();
//                    showUsersWaitingTime();
                }
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        doApiCall();

        return root;
    }

//    public void showUsersReward(){
//
////        Toast.makeText(getContext(), "Show reward here", Toast.LENGTH_SHORT).show();
//
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        RewardedAd.load(getContext(), getString(R.string.adReward),
//                adRequest, new RewardedAdLoadCallback() {
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error.
//                        Log.d("showUsersReward", loadAdError.getMessage());
//                        mRewardedAd = null;
//                    }
//
//                    @Override
//                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                        mRewardedAd = rewardedAd;
//                        Log.d("showUsersReward", "Ad was loaded.");
//                        if (mRewardedAd != null) {
//                            Activity activityContext = getActivity();
//                            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
//                                @Override
//                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                                    // Handle the reward.
//                                    Log.d("showUsersReward", "The user earned the reward.");
//                                    int rewardAmount = rewardItem.getAmount();
//                                    String rewardType = rewardItem.getType();
//                                }
//                            });
//                        } else {
//                            Log.d("showUsersReward", "The rewarded ad wasn't ready yet.");
//                        }
//                    }
//                });
//    }

    private void doApiCall() {
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {

        String url_subjects = new SplashActivity().BASEAPI + "ds_rewards/v1/update";

        queue = Volley.newRequestQueue(getContext());


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_subjects,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String resp = response;
//                        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
//                        pre.edit().putString("que_service", resp).apply();
//
//                        pre.edit().putString("updated_at", (new Date()).toString()).apply();

                        System.out.println(" response is " + response );


                Toast.makeText(getContext(), "response on logcat", Toast.LENGTH_SHORT).show();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "reward resp is error " + error, Toast.LENGTH_SHORT).show();

                System.out.println("reward resp url is  " + url_subjects);
                System.out.println("reward resp is error " + error);

            }

        }) {
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
//            }
//        }, 1500);
    }

}