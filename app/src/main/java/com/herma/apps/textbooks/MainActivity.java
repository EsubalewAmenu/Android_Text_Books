package com.herma.apps.textbooks;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.common.MainAdapter;
import com.herma.apps.textbooks.common.DB;
import com.herma.apps.textbooks.common.Item;
import com.herma.apps.textbooks.ui.about.About_us;
import com.herma.apps.textbooks.ui.fragment.BookFragment;
import com.herma.apps.textbooks.ui.fragment.PremiumFragment;
import com.herma.apps.textbooks.ui.fragment.QuestionsFragment;
import com.herma.apps.textbooks.ui.fragment.RewardFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    DB db;
    Intent chaptersIntent;
//    public ViewPager fragmentViewPager;
//    final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
/////////////////////////
//    Menu drawerMenu;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
//    NavigationView.OnNavigationItemSelectedListener item_click_listener;
//    ActionBarDrawerToggle mDrawerToggle;
//    ArrayList<String> menuItemsArray;
    String choosedGrade = "1"; int closeCounter = 1;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Map<String, String> gradeMap;//subjectsMap;
    Bundle Fragmentbundle;
    ////////////////////////
    ArrayList<String[]> grades = new ArrayList<>();

    String choosedGradeT = "", //selectedGrade = "0",
            p = "";

    private FrameLayout adContainerView;
    private AdView mAdView;

    QuestionsFragment questionsFragment;

    SharedPreferences pre;

    public static String Ads = "";
    public static int Ads_font = 22;
    TextView tvAds;
    boolean myB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DB(getApplicationContext());

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

//        drawerMenu = navigationView.getMenu();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


//        try { grades = getGradesFromDB(MainActivity.this);} catch (Exception e) { e.printStackTrace(); }


//        if (getIntent().getExtras() != null) {
////            spin.setSelection(getIntent().getIntExtra("choosedGrade", 0)-1);
//            p = getIntent().getStringExtra("choosedP");
//            choosedGrade = getIntent().getIntExtra("choosedGrade", 1);
//            choosedGradeT = getIntent().getStringExtra("choosedGradeT");
//        }

        pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        choosedGrade = pre.getString("choosedGrade", "1");
        choosedGradeT = pre.getString("choosedGradeT", "Grade 12");

        changeFragment(choosedGrade+"", choosedGradeT);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);

        AdRequest adRequest = new AdRequest.Builder().build();

        if(new Commons(getApplicationContext()).showGoogleAd( 2)) {
//            System.out.println("poiug yesss" );

            // Since we're loading the banner based on the adContainerView size, we need to wait until this
            // view is laid out before we can get the width.
            adContainerView.post(new Runnable() {
                @Override
                public void run() {
                    new Commons(getApplicationContext()).loadBanner(mAdView, getString(R.string.adHome), adContainerView, getWindowManager().getDefaultDisplay());
                }
            });


        }else{
//            System.out.println("poiug no " );
            adContainerView.setVisibility(View.GONE);
        }


        tvAds = (TextView) findViewById(R.id.tvAds);
        /// Ad here...
        doApiCall();

    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (closeCounter == 1) {
                closeCounter++;
                Toast.makeText(MainActivity.this, "Press back once more to exit.",
                        Toast.LENGTH_SHORT).show();
            } else {
                finish();
                super.onBackPressed();
            }
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
            case R.id.action_rate:
                Toast.makeText(MainActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
                rateApp();
                return true;
            case R.id.action_store:
                Toast.makeText(MainActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
                openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
                return true;
            case R.id.action_about:
                startActivity(new Intent(getApplicationContext(), About_us.class));
                return true;
            case R.id.action_exit:

                System.exit(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int QUESTIONNAIRE_REQUEST = 2018;


//        if (requestCode == QUESTIONNAIRE_REQUEST)
//        {
        if (resultCode == RESULT_OK) {


            questionsFragment.queId = data.getStringArrayExtra("queId");
            questionsFragment.timer = data.getStringExtra("timer");
            questionsFragment.current_questions = data.getStringArrayExtra("questions");
            questionsFragment.answerKey = data.getStringArrayExtra("answerKey");
            questionsFragment.response = data.getStringArrayExtra("response");

            questionsFragment.responseShouldBe = data.getStringArrayExtra("responseShouldBe");

            questionsFragment.packege = data.getStringExtra("packege");

//            questionsFragment.questionsWithAnswer = data.getStringArrayExtra("questionsWithAnswer");

            questionsFragment.examResult();

//            fragmentViewPager.setCurrentItem(0);
        }

    }

    public ArrayList getData(Context context, String choosedGrade) {

//        open(context,"read", "books.hrm");

        db = new DB(context);
        // get if textbook or teacher guide
        final Cursor gradeCursor = db.getSelect("*", "grade", "id="+choosedGrade);
        gradeCursor.moveToFirst();

        ArrayList arrayList = new ArrayList<>();

        final Cursor subjectsCursor = db.getSelect("*", "books", "grade='" + gradeCursor.getString(2) + "' and gtype='" + gradeCursor.getString(3) + "' ORDER BY name ASC");
        if (subjectsCursor.moveToFirst()) {
            do {
                arrayList.add(new Item("", subjectsCursor.getString(2), subjectsCursor.getString(0), subjectsCursor.getString(6), 0, "#09A9FF"));
            } while (subjectsCursor.moveToNext());
        }
        return arrayList;
    }
//    public ArrayList<String[]> getGradesFromDB(Context context) throws Exception {
//
//        ArrayList<String[]> dbGrades = new ArrayList<>();
//
//        open(context,"read", "books.hrm");
//        final Cursor gradeCursor = db.getSelect("*", "grade", "1");
//        if (gradeCursor.moveToFirst()) {
//            do {
//                dbGrades.add(new String[]{gradeCursor.getString(1), gradeCursor.getString(2), gradeCursor.getString(3)});//grade name, num, p
//            } while (gradeCursor.moveToNext());
//        }
//
//
//        return dbGrades;
//    }
//    public Map<String, String> getSubjectsFromDB(Context context, String grade) throws Exception {
//
//        Map<String, String> dbSubjects = new HashMap<>();
//
////        open(context,"read", "books.hrm");
//        final Cursor gradeCursor = db.getSelect("*", "grade", "1");
//        if (gradeCursor.moveToFirst()) {
//            do {
//                dbSubjects.put(gradeCursor.getString(1), gradeCursor.getInt(0)+"");
////                dbGrades.add(new String[]{gradeCursor.getString(1), gradeCursor.getString(2), gradeCursor.getString(3)});//grade name, num, p
//            } while (gradeCursor.moveToNext());
//        }
//
//
//        return dbSubjects;
//    }
    public MainAdapter setData(final Context context, ArrayList arrayList, String a){

        MainAdapter adapter = new MainAdapter(context, arrayList, new MainAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {

                System.out.println("en p is " + item.en);

                chaptersIntent = new Intent(context, ChaptersActivity.class);
                chaptersIntent.putExtra("subj", item.fileName);
                chaptersIntent.putExtra("name", item.chapName);
                chaptersIntent.putExtra("p", item.en);
                chaptersIntent.putExtra("title", a);
                context.startActivity(chaptersIntent);

            }
        });
        return adapter;
    }
//    public void open(Context context, String write, String db_name) {
//
//        db = new DB(context, db_name);
//        try {
//            if (write.equals("write"))
//                db.writeDataBase();
//            else
//                db.createDataBase();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            db.openDataBase();
//        } catch (SQLException sqle) {
//            throw sqle;
//        }
//    }
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
//    public void setMenuItems(ArrayList<String> menuItemsArray){
//
//        drawerMenu.clear();
//        for(int temp=0;temp<menuItemsArray.size();temp++){
//            // groupId, itemId, order, title
//            drawerMenu.add(1, 0, 0, menuItemsArray.get(temp)).setIcon(R.drawable.ic_menu_send);
//        }
//        drawerMenu.setGroupCheckable(1, true, true);
//
//        Menu communicateMenu = drawerMenu.addSubMenu("Communicate");
//        communicateMenu.add(2, 50, 50, "Tell a friend (Link)").setIcon(R.drawable.ic_menu_share);
////        communicateMenu.add(2, 51, 51, "Share app (apk)").setIcon(R.drawable.ic_menu_share);
//
//        Menu aboutMenu = drawerMenu.addSubMenu("About");
//        aboutMenu.add(3, 56, 56, "Rate us on PlayStore").setIcon(android.R.drawable.star_on);
//        aboutMenu.add(3, 57, 57, "More apps from us").setIcon(R.drawable.b_next);
//        aboutMenu.add(3, 58, 58, "About").setIcon(R.drawable.about);
//        aboutMenu.add(3, 59, 59, "Exit").setIcon(android.R.drawable.ic_delete);
//
//        if(getIntent().getStringExtra("choosedGrade") != null) changeFragment(gradeMap.get(getIntent().getStringExtra("choosedGrade")), getIntent().getStringExtra("choosedGrade"));
//        else changeFragment((String)gradeMap.values().toArray()[0], (String)gradeMap.keySet().toArray()[0]);
//
//        item_click_listener = new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
////                System.out.println("Clicked item is " + item.toString() + " & id is " + item.getItemId());
//
//                  if(item.getItemId() == 0){
////System.out.println("gradeMap.get(item.toString()) is " + gradeMap.get(item.toString()));
//                      changeFragment(gradeMap.get(item.toString()), item.toString());
//
//                      SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
//                      SharedPreferences.Editor editor = sharedPref.edit();
//                      editor.putString("choosedGrade", item.toString());
//                      editor.apply();
//
//                  }else if(item.getItemId() == 50){// Tell a friend (Link)
//                Intent intent4 = new Intent("android.intent.action.SEND");
//                intent4.setType("text/plain");
//                intent4.putExtra("android.intent.extra.TEXT", getString(R.string.share_link_pre) + " " + getString(R.string.app_name) + " " + getString(R.string.share_link_center) + " " + "https://play.google.com/store/apps/details?id="+getPackageName() + " "+ getString(R.string.share_link_pos));
//                startActivity(Intent.createChooser(intent4, "SHARE VIA"));
////                  }else if(item.getItemId() == 51){// Share app (apk)
////                      shareApp(MainActivity.this,"titleTezt", "messageTezt", "subjectTezt");
//                  }else if(item.getItemId() == 56){//Rate us
//            Toast.makeText(MainActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
//            rateApp();
//                  }else if(item.getItemId() == 57){//More app
//            Toast.makeText(MainActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
//            openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
//                  }else if(item.getItemId() == 58){//About
//            startActivity(new Intent(getApplicationContext(), About_us.class));
//                  }else if(item.getItemId() == 59){//Exit
//                      finish();
//                      System.exit(0);
//                  }
//
//                drawerLayout.closeDrawers();
//                return true;
//            }
//        };
//
//        navigationView.setNavigationItemSelectedListener(item_click_listener);
//        mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.app_name,
//                R.string.app_name);
//        drawerLayout.setDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();
//
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_g12) {
            changeFragment("1", "Grade 12");
        } else if (id == R.id.nav_g11) {
            changeFragment("2", "Grade 11");
        } else if (id == R.id.nav_g10) {
            changeFragment("3", "Grade 10");
        } else if (id == R.id.nav_g9) {
            changeFragment("4", "Grade 9");
        } else if (id == R.id.nav_g8) {
            changeFragment("5", "Grade 8");
        } else if (id == R.id.nav_g7) {
            changeFragment("6", "Grade 7");
        } else if (id == R.id.nav_g6) {
            changeFragment("7", "Grade 6");
        } else if (id == R.id.nav_g5) {
            changeFragment("8", "Grade 5");
        } else if (id == R.id.nav_g4) {
            changeFragment("11", "Grade 4");
        } else if (id == R.id.nav_g3) {
            changeFragment("12", "Grade 3");
        } else if (id == R.id.nav_g2) {
            changeFragment("13", "Grade 2");
        } else if (id == R.id.nav_g1) {
            changeFragment("14", "Grade 1");
        } else if (id == R.id.nav_g12t) {
            changeFragment("9", "Grade 12 T. Guide");
        } else if (id == R.id.nav_g11t) {
            changeFragment("10", "Grade 11 T. Guide");
        } else if (id == R.id.nav_g10t) {
            changeFragment("15", "Grade 10 T. Guide");
        } else if (id == R.id.nav_g9t) {
            changeFragment("16", "Grade 9 T. Guide");
        } else if (id == R.id.nav_questions) {

//            Fragmentbundle = new Bundle();
            //0
             questionsFragment = new QuestionsFragment();
//            Fragmentbundle.putString("choosedGrade", grade);
//            Fragmentbundle.putString("title", "Worksheet");
//            questionsFragment.setArguments(Fragmentbundle);
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, questionsFragment).commit();
            setTitle("Worksheet");


//            pre.edit().putString("choosedGrade", "17" ).apply();
//            pre.edit().putString("choosedGradeT", "Worksheet").apply();

        }else if (id == R.id.nav_share) {
                Intent intent4 = new Intent("android.intent.action.SEND");
                intent4.setType("text/plain");
                intent4.putExtra("android.intent.extra.TEXT", getString(R.string.share_link_pre) + " " + getString(R.string.app_name) + " " + getString(R.string.share_link_center) + " " + "https://play.google.com/store/apps/details?id="+getPackageName() + " "+ getString(R.string.share_link_pos));
                startActivity(Intent.createChooser(intent4, "SHARE VIA"));

        } else if (id == R.id.nav_ad_free) {

//            Fragmentbundle = new Bundle();
            PremiumFragment premiumFragment = new PremiumFragment();
//            Fragmentbundle.putString("choosedGrade", grade);
//            Fragmentbundle.putString("title", title);
//            premiumFragment.setArguments(Fragmentbundle);
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView,premiumFragment).commit();
            setTitle(R.string.menu_ad_free);

//        } else if (id == R.id.nav_ad_reward) {
//
//            RewardFragment rewardFragment = new RewardFragment();
//            mFragmentManager = getSupportFragmentManager();
//            mFragmentTransaction = mFragmentManager.beginTransaction();
//            mFragmentTransaction.replace(R.id.containerView,rewardFragment).commit();
//            setTitle(R.string.menu_ad_reward);

        } else if (id == R.id.nav_rate) {
            Toast.makeText(MainActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
            rateApp();
            return true;
        } else if (id == R.id.nav_store) {
            Toast.makeText(MainActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
            openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
            return true;
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), About_us.class));
            return true;
        } else if (id == R.id.nav_exit) {
            System.exit(0);
            return true;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void changeFragment(String grade, String title){
        Fragmentbundle = new Bundle();
        //0
        BookFragment bookFragment = new BookFragment();
        Fragmentbundle.putString("choosedGrade", grade);
        Fragmentbundle.putString("title", title);
        bookFragment.setArguments(Fragmentbundle);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,bookFragment).commit();
        setTitle(title);

        pre.edit().putString("choosedGrade", grade ).apply();
        pre.edit().putString("choosedGradeT", title).apply();

    }

//    public void shareApp(Context context, String chooserTitle,
//                                String message, String messageSubject) {
//        ApplicationInfo app = getApplicationContext().getApplicationInfo();
//        String filePath = app.sourceDir;
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//
//        // MIME of .apk is "application/vnd.android.package-archive".
//        // but Bluetooth does not accept this. Let's use "*/*" instead.
//        intent.setType("*/*");
//
//
//        // Append file and send Intent
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
//        startActivity(Intent.createChooser(intent, "Share app via"));
//
//    }

    private void doApiCall() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                String url ="https://datascienceplc.com/apps/manager/api/items/blog/ad?";

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
// Request a string response from the provided URL.

                final int random = new Random().nextInt((99999 - 1) + 1) + 1;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"v=1.0&app_id=745&company_id=1&rand="+random,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {
                                    try {
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);
                                        Ads = jsonObj.getString("ads");

                                        if(jsonObj.has("font")) Ads_font = jsonObj.getInt("font");
//                                        System.out.println("ads is " + Ads);

                                        if(jsonObj.has("open_ad")) {
                                            if(jsonObj.getString("open_ad").equalsIgnoreCase("my"))
                                            { setAd(); myB = true; }else tvAds.setVisibility(View.GONE);
                                        }else tvAds.setVisibility(View.GONE);

                                    } catch (final JSONException e) { tvAds.setVisibility(View.GONE); }

                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error is " + error);
                        tvAds.setVisibility(View.GONE);
                    }

                })
                {
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
            }
        }, 1500);
    }

    public void setAd(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvAds.setText(Html.fromHtml(Ads, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvAds.setText(Html.fromHtml(Ads));
        }

        tvAds.setTextSize(Ads_font);
        tvAds.setMovementMethod(LinkMovementMethod.getInstance());
        tvAds.setSelected(true);
        tvAds.setVisibility(View.VISIBLE);

    }
}