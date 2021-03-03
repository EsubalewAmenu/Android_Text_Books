package com.herma.apps.textbooks;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.ui.about.About_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;

public class ReadActivity extends AppCompatActivity {

    PDFView pdfView;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        pdfView = (PDFView) findViewById(R.id.pdfView);

        if (getIntent().getExtras() != null) {

            String filePath = "/storage/emulated/0/Herma/books/";


            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            try {
                this.setTitle(getIntent().getStringExtra("chapterName") + " (" + getIntent().getStringExtra("subject") + ")");
                if(new Commons(ReadActivity.this).dec(filePath, getIntent().getStringExtra("fileName")+".pdf",  getIntent().getStringExtra("p"))) {
                    f = new File(ReadActivity.this.getFilesDir()+"nor.pdf");
                    pdfView.fromFile(f)
                            .onRender(new OnRenderListener() {
                                @Override
                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {

                            mAdView = findViewById(R.id.adView);
                            AdRequest adRequest = new AdRequest.Builder().build();
                            mAdView.loadAd(adRequest);

                            InterstitialAd.load(getApplicationContext(), getString(R.string.adReaderInt), adRequest, new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    // The mInterstitialAd reference will be null until
                                    // an ad is loaded.
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
                    }).load();



//                    mAdView = findViewById(R.id.adView);
//                    AdRequest adRequest = new AdRequest.Builder().build();
//                    mAdView.loadAd(adRequest);
//
//                    InterstitialAd.load(getApplicationContext(), getString(R.string.adReaderInt), adRequest, new InterstitialAdLoadCallback() {
//                        @Override
//                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                            // The mInterstitialAd reference will be null until
//                            // an ad is loaded.
//                            mInterstitialAd = interstitialAd;
//                            if (mInterstitialAd != null) {
//                                mInterstitialAd.show(ReadActivity.this);
//                            }
//                        }
//
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            // Handle the error
//                            mInterstitialAd = null;
//                        }
//                    });


                }
            } catch (Exception e) {e.printStackTrace();}

        }


        /////////////


        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_rate:
                Toast.makeText(ReadActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
                rateApp();
                return true;
            case R.id.action_store:
                Toast.makeText(ReadActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
                openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
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
}