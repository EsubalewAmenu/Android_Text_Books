package com.herma.apps.textbooks;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.herma.apps.textbooks.common.MainAdapter;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.common.DB;
import com.herma.apps.textbooks.common.Item;
import com.herma.apps.textbooks.ui.about.About_us;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ChaptersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;
    DB db;

    public String fName, fEn;
    int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 90;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();


        if (getIntent().getExtras() != null) {
            this.setTitle(getIntent().getStringExtra("name") + "(" + getIntent().getStringExtra("title")+")");
            setData(getIntent().getStringExtra("subj"),  getIntent().getStringExtra("p"));
        }

        MainAdapter adapter = new MainAdapter(ChaptersActivity.this, arrayList, new MainAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {
                isStoragePermissionGranted(item);
            }
        });
        recyclerView.setAdapter(adapter);


        GridLayoutManager manager = new GridLayoutManager(ChaptersActivity.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);


        /////////////
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
                Toast.makeText(ChaptersActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
                rateApp();
                return true;
            case R.id.action_store:
                Toast.makeText(ChaptersActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
                openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
                return true;
            case R.id.action_about:
                startActivity(new Intent(getApplicationContext(), About_us.class));
                return true;
            case R.id.action_exit:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setData(String subj, String p){
        open(getApplicationContext(),"read", "books.hrm");
        final Cursor subjectsCursor = db.getSelect("*", "chapters", "subject_id='" + subj + "'");
        if (subjectsCursor.moveToFirst()) {
            do {
                arrayList.add(new Item(subjectsCursor.getString(2) , subjectsCursor.getString(3), p, R.drawable.icon, "#000000"));
            } while (subjectsCursor.moveToNext());
        }

    }
    public  boolean isStoragePermissionGranted(Item item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission is granted");
                forGranted(item);
                return true;
            } else {
                Toast.makeText(ChaptersActivity.this, "Storage Permission is revoked", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            System.out.println("Permission is granted");
            forGranted(item);
            return true;
        }
    }
    public void forGranted(Item item){
        fName = item.fileName;
        fEn = item.en;
String FILEPATH = "/storage/emulated/0/Herma/books/";

        File chapterFile = new File("/storage/emulated/0/Herma/books/" + fName);
        if (chapterFile.exists()) {

            Intent chaptersIntent = new Intent(ChaptersActivity.this, ReadActivity.class);
            chaptersIntent.putExtra("fileName", fName);
            chaptersIntent.putExtra("p", fEn);
            chaptersIntent.putExtra("chapterName", item.chapName);
            chaptersIntent.putExtra("subject", getIntent().getStringExtra("name"));
            startActivity(chaptersIntent);
        } else {
//////////////////////////////
try {
    File booksDirectory = new File(FILEPATH);
    if (!booksDirectory.exists()) System.out.println(booksDirectory.mkdirs());
}catch (Exception kl){System.out.println("Exception on ChaptersActivity mkdirs " + kl);}
//////////////////////////////

            new Commons(ChaptersActivity.this).messageDialog(ChaptersActivity.this, "d", R.string.no_file, 1234, fName, fEn, R.string.download, R.string.cancel, R.string.downloading, item.chapName, getIntent().getStringExtra("name"));
        }
    }
    public void open(Context context, String write, String db_name) {

        db = new DB(context, db_name);
        try {
            if (write.equals("write"))
                db.writeDataBase();
            else
                db.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            db.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
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