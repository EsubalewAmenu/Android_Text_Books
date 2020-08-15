package com.herma.apps.textbooks;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.herma.apps.textbooks.adapter.HomeAdapter;
import com.herma.apps.textbooks.model.Item;
import com.herma.apps.textbooks.questions.DB;
import com.herma.apps.textbooks.util.Commons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChaptersActivity extends AppCompatActivity {

    private AdView mAdView;

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;
    DB db;

    public String fName, fEn;
    int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();


        if (getIntent().getExtras() != null) {
            this.setTitle(getIntent().getStringExtra("name"));
        setData(getIntent().getStringExtra("subj"),  getIntent().getStringExtra("p"));
        }
        HomeAdapter adapter = new HomeAdapter(ChaptersActivity.this, arrayList, new HomeAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {
                /////////////////////////////////////////////////////////////
//                if (ContextCompat.checkSelfPermission(ChaptersActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(ChaptersActivity.this,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                        // Show an explanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//                    } else {
//                        // No explanation needed; request the permission
//                        ActivityCompat.requestPermissions(ChaptersActivity.this,
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
//                    }
//                }
                isStoragePermissionGranted(item);
                ////////////////////////////////////////////////////////////
//                else{
//                    fName = item.fileName;
//                fEn = item.en;
//
//                File chapterFile = new File("/storage/emulated/0/Herma/books/" + fName);
//                if (chapterFile.exists()) {
//
//                    Intent chaptersIntent = new Intent(ChaptersActivity.this, ReadActivity.class);
//                    chaptersIntent.putExtra("fileName", fName);
//                    chaptersIntent.putExtra("p", fEn);
//                    startActivity(chaptersIntent);
//                } else {
//                    new Commons(ChaptersActivity.this).messageDialog(ChaptersActivity.this, "d", R.string.no_file, 1234, fName, fEn, R.string.download, R.string.cancel, R.string.downloading);
//
//                }
//            }
        }
        });
        recyclerView.setAdapter(adapter);


        GridLayoutManager manager = new GridLayoutManager(ChaptersActivity.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
    public void open(Context context, String write, String db_name) {

        db = new DB(context, db_name);
        try {
            if (write.equals("write"))
                db.writeDataBase();
            else
                db.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            db.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }
    public  boolean isStoragePermissionGranted(Item item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

        File chapterFile = new File("/storage/emulated/0/Herma/books/" + fName);
        if (chapterFile.exists()) {

            Intent chaptersIntent = new Intent(ChaptersActivity.this, ReadActivity.class);
            chaptersIntent.putExtra("fileName", fName);
            chaptersIntent.putExtra("p", fEn);
            startActivity(chaptersIntent);
        } else {
            new Commons(ChaptersActivity.this).messageDialog(ChaptersActivity.this, "d", R.string.no_file, 1234, fName, fEn, R.string.download, R.string.cancel, R.string.downloading);

        }

    }
}
