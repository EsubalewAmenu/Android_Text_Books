package com.herma.apps.textbooks;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.herma.apps.textbooks.common.FileUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ChaptersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;
    DB db;

    public String fName, fEn;
    int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 90;

    private FrameLayout adContainerView;
    private AdView mAdView;

    String FILEPATH = "/storage/emulated/0/Herma/books/";

    boolean is_short;

    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;

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

            if(getIntent().getStringExtra("unitsArrayList") != null){

                try {
                    this.setTitle(getIntent().getStringExtra("subject") + "(" + getIntent().getStringExtra("grade")+")");
                    setFromShort(getIntent().getStringExtra("unitsArrayList"));
                    is_short = true;
                } catch (JSONException e) { System.out.println("JSONException on chapters parsing ");e.printStackTrace(); }

            }
            else{
                this.setTitle(getIntent().getStringExtra("name") + "(" + getIntent().getStringExtra("title")+")");
                setData(getIntent().getStringExtra("subj"),  getIntent().getStringExtra("p"));
                is_short = false;
                }
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

//        mAdView = findViewById(R.id.adView);
        adContainerView = findViewById(R.id.ad_view_container);

        AdRequest adRequest = new AdRequest.Builder().build();

        if(new Commons(getApplicationContext()).showGoogleAd( 2)) {
//            mAdView.loadAd(adRequest);
            // Since we're loading the banner based on the adContainerView size, we need to wait until this
            // view is laid out before we can get the width.
            adContainerView.post(new Runnable() {
                @Override
                public void run() {
                    new Commons(getApplicationContext()).loadBanner(mAdView, getString(R.string.adChapter), adContainerView, getWindowManager().getDefaultDisplay());
                }
            });

        }else{
            adContainerView.setVisibility(View.GONE);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chapters, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: // handle back arrow click here
                finish(); // close this activity and return to preview activity (if there is any)
                return true;
            case R.id.action_insert_subject:


                Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFileIntent.setType("*/*");
                // Only return URIs that can be opened with ContentResolver
                chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

                chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
                startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);

                return true;
            case R.id.action_delete_subject:

                new Commons(ChaptersActivity.this).deleteSubjectDialog(ChaptersActivity.this, arrayList);

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
public void setFromShort(String shortArrayList) throws JSONException {

                                        JSONObject jsonObj = new JSONObject(shortArrayList);

                                    JSONArray datas = jsonObj.getJSONArray("chap");
                                    JSONObject c;

    arrayList = new ArrayList<>();

                                    for (int i = 0; i < datas.length(); i++) {

                                        c = datas.getJSONObject(i);
                                        arrayList.add(new Item(c.getString("id"), c.getString("chaptername"), c.getString("file_url"), "en", R.drawable.icon, "#000000"));
                                    }

}
    public void setData(String subj, String p){
        open(getApplicationContext(),"read", "books.hrm");
        final Cursor subjectsCursor = db.getSelect("*", "chapters", "subject_id='" + subj + "' ORDER BY chaptername ASC");
        if (subjectsCursor.moveToFirst()) {
            do {
                arrayList.add(new Item("", subjectsCursor.getString(2) , subjectsCursor.getString(3), p, R.drawable.icon, "#000000"));
            } while (subjectsCursor.moveToNext());
        }

    }
    public  boolean isStoragePermissionGranted(Item item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            FILEPATH = getFilesDir().getPath()+"/Herma/books/";
//
//            if (checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
//                System.out.println("Permission is granted for version R");
                forGranted(item);
                return true;
//            } else {
//                Toast.makeText(ChaptersActivity.this, "Storage Permission is revoked for version R", Toast.LENGTH_SHORT).show();
////                ActivityCompat.requestPermissions(this, new String[]{Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
////                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
//
//                return false;
//            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

        File chapterFile = new File(FILEPATH + fName);
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

                    if(is_short){
            new Commons(ChaptersActivity.this).messageDialog(ChaptersActivity.this, "d", R.string.no_file, 1234, fName, fEn, R.string.download, R.string.cancel, R.string.downloading, item.chapName, getIntent().getStringExtra("subject") , getIntent().getStringExtra("grade"), item.chapterID, is_short);
        }else
            new Commons(ChaptersActivity.this).messageDialog(ChaptersActivity.this, "d", R.string.no_file, 1234, fName, fEn, R.string.download, R.string.cancel, R.string.downloading, item.chapName, getIntent().getStringExtra("name"), "", "", is_short);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK ) {
                    if(data != null)  {
                        Uri fileUri = data.getData();
//                        Log.i(LOG_TAG, "Uri: " + fileUri);

                        String filePath = null;
                        try {
//                            System.out.println("filePath fileUri is " + fileUri);
                            System.out.println("filePath is " + filePath);

                            filePath = FileUtils.getPath(getApplicationContext(), fileUri);

//                             copy to FILEPATH + fName
                            File fileSource = new File(filePath);
                            File fileDest = new File(FILEPATH + fName);

                            copyChapter(fileSource, fileDest);

                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void copyChapter(File src, File dst) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("እርግጠኛ ኖት?");
        builder.setMessage("\"" + src.getName() + "\" ማስገባት ይፈልጋሉ?");

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog


                try {
                    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                    if(isKitKat){
                        try (InputStream in = new FileInputStream(src)) {
                            try (OutputStream out = new FileOutputStream(dst)) {
                                // Transfer bytes from in to out
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                            }
                        }
                    }else {
                        InputStream in = new FileInputStream(src);
                        try {
                            OutputStream out = new FileOutputStream(dst);
                            try {
                                // Transfer bytes from in to out
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                            } finally {
                                out.close();
                            }
                        } finally {
                            in.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

//    public void copyChapter(File src, File dst) throws Exception {
//        System.out.println("print from 0");
//
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case DialogInterface.BUTTON_POSITIVE:
//                        //Yes button clicked
//                        System.out.println("print from 1");
//                        try {
//                        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//                        if(isKitKat){
//                                try (InputStream in = new FileInputStream(src)) {
//                                    try (OutputStream out = new FileOutputStream(dst)) {
//                                        // Transfer bytes from in to out
//                                        byte[] buf = new byte[1024];
//                                        int len;
//                                        while ((len = in.read(buf)) > 0) {
//                                            out.write(buf, 0, len);
//                                        }
//                                    }
//                                }
//                        }else {
//                            InputStream in = new FileInputStream(src);
//                            try {
//                                OutputStream out = new FileOutputStream(dst);
//                                try {
//                                    // Transfer bytes from in to out
//                                    byte[] buf = new byte[1024];
//                                    int len;
//                                    while ((len = in.read(buf)) > 0) {
//                                        out.write(buf, 0, len);
//                                    }
//                                } finally {
//                                    out.close();
//                                }
//                            } finally {
//                                in.close();
//                            }
//                        }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        //No button clicked
//                        break;
//                }
//            }
//        };
//
//
//    }

}