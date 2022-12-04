package com.herma.apps.textbooks;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.herma.apps.textbooks.common.MainAdapter;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.common.DB;
import com.herma.apps.textbooks.common.Item;
import com.herma.apps.textbooks.ui.about.About_us;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ChaptersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;

    public String fName, fEn;

    private FrameLayout adContainerView;
    private AdView mAdView;

    String FILEPATH;

    boolean is_short;

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

        FILEPATH = getFilesDir().getPath()+"/Herma/books/";

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
                openUnitToRead(item);
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
//        open(getApplicationContext(),"read", "books.hrm");
        DB db = new DB(getApplicationContext());
        final Cursor subjectsCursor = db.getSelect("*", "chapters", "subject_id='" + subj + "' ORDER BY chaptername ASC");
        if (subjectsCursor.moveToFirst()) {
            do {
                arrayList.add(new Item("", subjectsCursor.getString(2) , subjectsCursor.getString(3), p, R.drawable.icon, "#000000"));
            } while (subjectsCursor.moveToNext());
        }

    }
    public void openUnitToRead(Item item){
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