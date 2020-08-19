package com.herma.apps.textbooks;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;
import com.google.crypto.tink.apps.rewardedads.RewardedAdsVerifier;
import com.herma.apps.textbooks.util.Commons;

import java.io.File;
import java.security.GeneralSecurityException;

public class ReadActivity extends AppCompatActivity {

    PDFView pdfView;
    private AdView mAdView;
    RewardedAdLoadCallback adLoadCallback;

    private RewardedAd rewardedAd;

    int failedToLoadCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        toolBarInit();

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        pdfView = (PDFView) findViewById(R.id.pdfView);

        if (getIntent().getExtras() != null) {

            String filePath = "/storage/emulated/0/Herma/books/";

            try {
                if(new Commons(ReadActivity.this).dec(filePath, getIntent().getStringExtra("fileName")+".pdf",  getIntent().getStringExtra("p"))) {
//                    pdfView.fromAsset(filePath+"nor.pdf").load();
                    File f = new File(ReadActivity.this.getFilesDir()+"nor.pdf");
                    pdfView.fromFile(f).load();
                }
            } catch (Exception e) {e.printStackTrace();}

        }



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        rewardedAd = new RewardedAd(this,
                "ca-app-pub-8011674951494696/2726130955");
        ServerSideVerificationOptions options = new ServerSideVerificationOptions.Builder()
                .setCustomData("{\"ctr\":\"Ethiopia\",\"v\":\"1.0\",\"a\":\"1\"}")//setCustomData("cust")
                .setUserId("test@datascienceplc.com")
                .build();
        rewardedAd.setServerSideVerificationOptions(options);


        adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                if (rewardedAd.isLoaded()) {
                    Activity activityContext = ReadActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            /////////////
                            try {
                                RewardedAdsVerifier verifier = new RewardedAdsVerifier.Builder()
                                        .fetchVerifyingPublicKeysWith(
                                                RewardedAdsVerifier.KEYS_DOWNLOADER_INSTANCE_PROD)
                                        .build();
                                verifier.verify("https://datascienceplc.com/apps/reward/g/veri");
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                            ///////////////
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            // Ad failed to display.
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);
                } else {
//                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
                if(failedToLoadCounter < 3){
                    rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                    failedToLoadCounter++;
                }
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

    }

//pdfView.fromAsset(String)
//            .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//    .enableSwipe(true) // allows to block changing pages using swipe
//    .swipeHorizontal(false)
//    .enableDoubletap(true)
//    .defaultPage(0)
//    // allows to draw something on the current page, usually visible in the middle of the screen
//    .onDraw(onDrawListener)
//    // allows to draw something on all pages, separately for every page. Called only for visible pages
//    .onDrawAll(onDrawListener)
//    .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
//    .onPageChange(onPageChangeListener)
//    .onPageScroll(onPageScrollListener)
//    .onError(onErrorListener)
//    .onPageError(onPageErrorListener)
//    .onRender(onRenderListener) // called after document is rendered for the first time
//    // called on single tap, return true if handled, false to toggle scroll handle visibility
//    .onTap(onTapListener)
//    .onLongPress(onLongPressListener)
//    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//    .password(null)
//    .scrollHandle(null)
//    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//    // spacing between pages in dp. To define spacing color, set view background
//    .spacing(0)
//    .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//    .linkHandler(DefaultLinkHandler)
//    .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
//    .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
//    .pageSnap(false) // snap pages to screen boundaries
//    .pageFling(false) // make a fling change only a single page like ViewPager
//    .nightMode(false) // toggle night mode
//    .load();


    private void toolBarInit()
    {
        Toolbar toolBar = findViewById(R.id.questionToolbar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolBar.setNavigationOnClickListener(v -> onBackPressed());

    }
}
