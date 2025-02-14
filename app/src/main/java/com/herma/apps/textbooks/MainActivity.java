package com.herma.apps.textbooks;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateSuccessListener;
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateFailureListener;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.common.MainAdapter;
import com.herma.apps.textbooks.common.DB;
import com.herma.apps.textbooks.common.Item;
import com.herma.apps.textbooks.settings.LanguageHelper;
import com.herma.apps.textbooks.settings.SettingsActivity;
import com.herma.apps.textbooks.ui.about.About_us;
import com.herma.apps.textbooks.ui.fragment.AllNewCurriculumBooks;
import com.herma.apps.textbooks.ui.fragment.FavOldCurriculumBooks;
import com.herma.apps.textbooks.ui.fragment.MyNewCurriculumBooks;
import com.herma.apps.textbooks.ui.fragment.QuestionsFragment;
import com.herma.apps.textbooks.ui.fragment.RewardFragment;
import com.herma.apps.textbooks.ui.profile.ProfileActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    AllNewCurriculumBooks allNewCurriculumBooks;
    MyNewCurriculumBooks myNewCurriculumBooks;
    FavOldCurriculumBooks favOldCurriculumBooks;

    SharedPreferences pre;

    public static String Ads = "";
    public static int Ads_font = 22;
    TextView tvAds;
    boolean myB = false;

    private ConsentInformation consentInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.updateLanguage(this);

// Apply the theme
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("themeMode","light").equals("dark")) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
//                .addTestDeviceHashedId("18998c6a0a39d135a063c156d3ac9339")
//                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//                .build();

        // Set tag for under age of consent. false means users are not under age
        // of consent.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
//                .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w("consentInformation", String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                            }
                    );
                },

                (OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w("consentInformation", String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

//        consentInformation.reset();

        db = new DB(getApplicationContext());

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setUserData(navigationView);

        pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        openBooksFragment(pre.getString("nav_type", "new"), pre.getString("nav_type_title", "All new curriculum books"));

//        choosedGrade = pre.getString("choosedGrade", "1");
//        choosedGradeT = pre.getString("choosedGradeT", "Grade 12");

//        changeFragment(choosedGrade+"", choosedGradeT);
//        System.out.println("is user consent");
        if (consentInformation.canRequestAds()) {
//            System.out.println(" yes user consent");
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

                adContainerView = findViewById(R.id.ad_view_container);

//                AdRequest adRequest = new AdRequest.Builder().build();

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
                    adContainerView.setVisibility(View.GONE);
                }

                /// Ad here...


            }
        });

        }


        tvAds = (TextView) findViewById(R.id.tvAds);
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

        if( id == R.id.action_rate){
                Toast.makeText(MainActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
                rateApp();
                return true;
        }else if ( id == R.id.action_store){
                Toast.makeText(MainActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
                openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
                return true;

    }else if ( id ==  R.id.action_about){
                startActivity(new Intent(getApplicationContext(), About_us.class));
                return true;

}else if ( id ==  R.id.action_settings){
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

        }else if ( id ==  R.id.action_exit){
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

    public ArrayList getData(Context context, String choosedGrade, String old_new) {
        db = new DB(context);
        ArrayList arrayList = new ArrayList<>();

        Cursor subjectsCursor = null;
        if(choosedGrade.equals("fav") || choosedGrade.equals("newf")) {
            subjectsCursor = db.getSelect("*", "books", "uc='"+choosedGrade+"' ORDER BY name ASC");
        }else if(old_new.equals("new")) {
            subjectsCursor = db.getSelect("*", "books", "(uc='new' or uc='newf') and grade='" + choosedGrade + "' ORDER BY name DESC");

        }else {

            // get if textbook or teacher guide
            final Cursor gradeCursor = db.getSelect("*", "grade", "id="+choosedGrade);
            gradeCursor.moveToFirst();

            if(old_new.equals("blockchain"))
                subjectsCursor = db.getSelect("*", "books", "(uc='new' or uc='newf') and grade='15' ORDER BY name ASC");
            else
                subjectsCursor = db.getSelect("*", "books", "uc!='new' and uc!='newf' and grade='" + gradeCursor.getString(2) + "' and gtype='" + gradeCursor.getString(3) + "' ORDER BY name ASC");
        }

        if (subjectsCursor.moveToFirst()) {
            do {
                arrayList.add(new Item("", subjectsCursor.getString(2), subjectsCursor.getString(0), subjectsCursor.getString(6), 0, "#09A9FF"));
            } while (subjectsCursor.moveToNext());
        }
        return arrayList;
    }
    public MainAdapter setData(final Context context, ArrayList arrayList, String a){

        MainAdapter adapter = new MainAdapter(context, arrayList, new MainAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {


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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_books) {
            openMyBooksFragment();
        } else if (id == R.id.nav_all_books) {
            openBooksFragment("new", "All new curriculum books");
        } else if (id == R.id.nav_old_books) {
            openBooksFragment("old", "Old curriculum books");
        } else if (id == R.id.nav_fav_old_books) {
            openMyFavOldBooksFragment();
        } else if (id == R.id.nav_blockchain) {
            openBooksFragment("blockchain", "Blockchain");
        } else if (id == R.id.nav_questions) {
            questionsFragment = new QuestionsFragment();
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, questionsFragment).commit();
            setTitle("Worksheet");

        }else if (id == R.id.nav_share) {
            Intent intent4 = new Intent("android.intent.action.SEND");
            intent4.setType("text/plain");
            intent4.putExtra("android.intent.extra.TEXT", getString(R.string.share_link_pre) + " " + getString(R.string.app_name) + " " + getString(R.string.share_link_center) + " " + "https://play.google.com/store/apps/details?id=" + getPackageName() + " " + getString(R.string.share_link_pos));
            startActivity(Intent.createChooser(intent4, "SHARE VIA"));
        }else if(id == R.id.action_logout){

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            // Perform sign out
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SharedPreferences prefs = null;
                            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                            prefs.edit().putString("token", "None").apply();

                            Intent splashActivityIntent = new Intent(getApplicationContext(), SplashActivity.class);
                            startActivity(splashActivityIntent);
                        }
                    });

        }else if(id == R.id.action_settings){
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
//        } else if (id == R.id.nav_ad_free) {
//
//            PremiumFragment premiumFragment = new PremiumFragment();
//            mFragmentManager = getSupportFragmentManager();
//            mFragmentTransaction = mFragmentManager.beginTransaction();
//            mFragmentTransaction.replace(R.id.containerView,premiumFragment).commit();
//            setTitle(R.string.menu_ad_free);

        } else if (id == R.id.nav_ad_reward) {

            RewardFragment rewardFragment = new RewardFragment();
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView,rewardFragment).commit();
            setTitle(R.string.menu_ad_reward);

        } else if (id == R.id.nav_college_books) {
            openUrl("https://play.google.com/store/apps/details?id=com.herma.apps.collegebooks&hl=en_US&gl=US");
            return true;
        } else if (id == R.id.nav_expertsway) {
            openUrl("https://play.google.com/store/apps/details?id=com.herma.apps.expertsway");
            return true;
//        } else if (id == R.id.nav_add_books) {
//            openUrl("https://docs.google.com/forms/d/e/1FAIpQLSfCJtHIIZYY0CJe0V0W4GLkgr1407qMG3RwOs2KTqiIxt53ig/viewform?usp=sf_link");
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
    public void openMyBooksFragment(){
        myNewCurriculumBooks = new MyNewCurriculumBooks();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, myNewCurriculumBooks).commit();
        setTitle("My new curriculum books");


        pre.edit().putString("choosedGrade", "my_b" ).apply();
        pre.edit().putString("choosedGradeT", "My new curriculum books").apply();
    }
    public void openMyFavOldBooksFragment(){
        favOldCurriculumBooks = new FavOldCurriculumBooks();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, favOldCurriculumBooks).commit();
        setTitle("Favorite old curriculum books");


        pre.edit().putString("choosedGrade", "my_b" ).apply();
        pre.edit().putString("choosedGradeT", "My new curriculum books").apply();
    }
    public void openBooksFragment(String type, String title){
        allNewCurriculumBooks = new AllNewCurriculumBooks();
        pre.edit().putString("nav_type", type).apply();

        Bundle args = new Bundle();
        args.putString("type", type);
        allNewCurriculumBooks.setArguments(args);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, allNewCurriculumBooks).commit();

        setTitle(title);
        pre.edit().putString("nav_type_title", title).apply();

    }
    private void doApiCall() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                // Request a string response from the provided URL.

                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        SplashActivity.BASEAPI+"DSSERVICE/v1/updates?number_of_ad=1&app=1-12-textbooks&last_updated="+pre.getString("last_update", "2020-11-22 12:39:52"),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {
                                    try {
//                                        System.out.println("ad request is ");
//                                        System.out.println(response);
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);

//                                        System.out.println("response code is '" + jsonObj.getString("code")+"'");
                                        if(jsonObj.getString("code").equals("new_updates") ){

                                            parseAllBooks(jsonObj.getString("books"));


                                                pre.edit().putString("last_update", jsonObj.getString("last_update") ).apply();

                                            Ads = jsonObj.getString("ad");

                                            if(!Ads.equals("null")){
                                                setAd(); myB = true;
                                            }else tvAds.setVisibility(View.GONE);


                                        }else{
                                            tvAds.setVisibility(View.GONE);
                                        }

                                    } catch (final Exception e) {
                                        tvAds.setVisibility(View.GONE);
                                    }

                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error is " + error);
                        tvAds.setVisibility(View.GONE);
                    }

                });

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

    private void parseAllBooks(String response) {
        try {
            // Getting JSON Array node
            JSONArray datas = new JSONArray(response);

//            ArrayList<Object> items = new ArrayList<Object>();
            for (int i = 0; i < datas.length(); i++) {
                JSONObject c = datas.getJSONObject(i);
                isDBSouldBeUpdated(setFromWeb(c.getString("chapters"), c.getString("en").trim()),
                        c.getString("category").trim(),
                        c.getString("name").trim(),
                        c.getString("en").trim()
                        );

            }
//            System.out.println("updated items count is " + items.size());


        } catch (Exception e) {System.out.println("Exception on parseAllBooks " + e);}

    }

    public ArrayList<Item> setFromWeb(String chaptersJsonArray, String en) throws JSONException {
        ArrayList<Item> arrayList = new ArrayList<>();

        JSONArray datas = new JSONArray(chaptersJsonArray);

        for(int i = 0; i < datas.length(); i++){
            JSONObject c = datas.getJSONObject(i);
            arrayList.add(new Item("0", c.getString("name") , c.getString("file_name"), en, R.drawable.icon, "#000000"));
        }
        return arrayList;
    }

    private void isDBSouldBeUpdated(ArrayList<Item> arrayList, String grade, String subject, String fEn) {
        if(arrayList.size()>0){
            if(arrayList.get(0).chapterID.equals("0")) {
                String chapterNamesList ="", chapterNamesListAnd ="";
                for (int k=0;k<arrayList.size();k++) {
                    chapterNamesList += " or filename='"+arrayList.get(k).fileName+"'";
                    chapterNamesListAnd += " and filename!='"+arrayList.get(k).fileName+"'";
                }
                // grade table
                grade = grade.replace("Grade ", "");

                // get grade by using subject in gradeName and subject_slug in gradeInNum
                // if not exist create grade by using subject for gradeName and subject_slug for gradeInNum
                Cursor chap = db.getSelect("*", "chapters", "filename='x'"+chapterNamesList);
                if (chap.moveToFirst()) {
                    updateChapters(arrayList, chap.getString(1),grade, subject, chapterNamesListAnd, fEn);
                }else{
                    Cursor bookCursor = db.getSelect("*", "books", "name='" + subject + "' and grade='"+grade+"' and uc='new'");
                    if (bookCursor.getCount() == 0) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("grade", grade);
                        contentValues.put("name", subject);
                        contentValues.put("uc", "new");
                        contentValues.put("gtype", System.currentTimeMillis());
                        contentValues.put("p", fEn);
                        db.insert("books",contentValues);
                    }

                    bookCursor = db.getSelect("*", "books", "name='" + subject + "' and grade='"+grade+"' and uc='new'");
                    if (bookCursor.moveToFirst()) {
                        updateChapters(arrayList, bookCursor.getString(0),grade, subject, chapterNamesListAnd, fEn);
                    }
                }
            }
        }

    }

    private void updateChapters(ArrayList<Item> arrayList, String subject_id, String subject_slug, String course, String chapterNamesListAnd, String fEn) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("grade", subject_slug );
        contentValues.put("name", course );
        contentValues.put("uc", "new" );
        contentValues.put("p", fEn );
        db.update("books", contentValues, "id", subject_id);

        final Cursor allChaptersFromDb = db.getSelect("*", "chapters", "subject_id='" + subject_id + "'");
        if (allChaptersFromDb.moveToFirst()) {
            do {
                for (int i = 0; i < arrayList.size(); i++) {
                    if(allChaptersFromDb.getString(3).equals(arrayList.get(i).fileName) ) {
                        contentValues = new ContentValues();
                        contentValues.put("chaptername", arrayList.get(i).chapName );
                        db.update("chapters", contentValues, "id", allChaptersFromDb.getString(0));
                    }
                }

            } while (allChaptersFromDb.moveToNext());
        }

        for (int i = 0; i < arrayList.size(); i++) {
            final Cursor singleChapter = db.getSelect("*", "chapters", "filename='" + arrayList.get(i).fileName + "'");
            if (!singleChapter.moveToFirst()) {

                contentValues = new ContentValues();
                contentValues.put("subject_id", subject_id);
                contentValues.put("chaptername", arrayList.get(i).chapName);
                contentValues.put("filename", arrayList.get(i).fileName);
                db.insert("chapters",contentValues);
            }
        }

        db.deleteData("chapters", "subject_id="+subject_id+" and (filename!='00000'"+chapterNamesListAnd+")");

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
    public void setUserData(NavigationView navigationView){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString("token", "None");

        if (!token.equals("None")) {
            View navHeaderView = navigationView.getHeaderView(0);

            ImageView userImage = navHeaderView.findViewById(R.id.userImage);
            TextView userName = navHeaderView.findViewById(R.id.userName);
            TextView userEmail = navHeaderView.findViewById(R.id.userEmail);

            Glide.with(getApplicationContext())
                    .load(prefs.getString("image", "None"))
                    .into(userImage);

            userName.setText(prefs.getString("first_name", "None"));
            userEmail.setText(prefs.getString("user_email", "None"));

            navigationView.setNavigationItemSelectedListener(this);

            navHeaderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    profileIntent.putExtra("username", prefs.getString("username", "None"));
                    startActivity(profileIntent);
                }
            });


        }else{

// Hide the logout item
            Menu menu = navigationView.getMenu();
            MenuItem logoutItem = menu.findItem(R.id.action_logout);
            logoutItem.setVisible(false);
        }
    }
}