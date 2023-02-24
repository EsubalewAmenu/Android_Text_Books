package com.herma.apps.textbooks.ui.about;

/*
 * Created by Esubalew Amenu on 04-Jan-19
 * Mobile +251 92 348 1783
 * Email esubalew.a2009@gmail.com/
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.ReadActivity;

public class About_us extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        // Add back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((Button) findViewById(R.id.telegram)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                About_us.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/ethio_textbooks")));
            }
        });
        ((Button) findViewById(R.id.moreap)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
            }
        });
        ((Button) findViewById(R.id.shareap)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction("android.intent.action.SEND");
                sendIntent.putExtra("android.intent.extra.SUBJECT", About_us.this.getText(R.string.app_name));
                sendIntent.putExtra("android.intent.extra.TEXT", "Downloads \nhttps://play.google.com/store/apps/details?id=" + About_us.this.getPackageName());
                sendIntent.setType("text/plain");
                About_us.this.startActivity(Intent.createChooser(sendIntent, About_us.this.getText(R.string.app_name)));
            }
        });
        ((Button) findViewById(R.id.description)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                About_us.this.startActivity(new Intent(About_us.this.getApplicationContext(), Describe.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            // Handle clicks on the back button (the left arrow in the toolbar)
            onBackPressed();
            return true;
        }else if(id == R.id.action_rate) {
            Toast.makeText(About_us.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
            rateApp();
            return true;
        }else if(id == R.id.action_store) {
            Toast.makeText(About_us.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
            openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openUrl(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
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

}
