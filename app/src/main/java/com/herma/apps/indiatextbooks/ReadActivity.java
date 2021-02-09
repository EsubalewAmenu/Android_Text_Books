package com.herma.apps.indiatextbooks;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.herma.apps.indiatextbooks.common.Commons;
import com.herma.apps.indiatextbooks.ui.about.About_us;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;

public class ReadActivity extends AppCompatActivity {

    PDFView pdfView;

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

            try {
                this.setTitle(getIntent().getStringExtra("chapterName") + " (" + getIntent().getStringExtra("subject") + ")");
                if(new Commons(ReadActivity.this).dec(filePath, getIntent().getStringExtra("fileName")+".pdf",  getIntent().getStringExtra("p"))) {
                    File f = new File(ReadActivity.this.getFilesDir()+"nor.pdf");
                    pdfView.fromFile(f).load();
                }
            } catch (Exception e) {e.printStackTrace();}

        }

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
            case android.R.id.home: // handle back arrow click here
                finish(); // close this activity and return to preview activity (if there is any)
                return true;
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