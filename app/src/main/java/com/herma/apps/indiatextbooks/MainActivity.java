package com.herma.apps.indiatextbooks;

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
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.herma.apps.indiatextbooks.common.MainAdapter;
import com.herma.apps.indiatextbooks.common.DB;
import com.herma.apps.indiatextbooks.common.Item;
import com.herma.apps.indiatextbooks.ui.about.About_us;
import com.herma.apps.indiatextbooks.ui.fragment.BookFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DB db;
    Intent chaptersIntent;
//    public ViewPager fragmentViewPager;
//    final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
/////////////////////////
    Menu drawerMenu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    NavigationView.OnNavigationItemSelectedListener item_click_listener;
    ActionBarDrawerToggle mDrawerToggle;
    ArrayList<String> menuItemsArray;
    int choosedGrade = 0, closeCounter = 1;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Map<String, String> subjectsMap;
    Bundle Fragmentbundle;
    ////////////////////////
    ArrayList<String[]> grades = new ArrayList<>();

    String selectedGrade = "0", p = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        drawerMenu = navigationView.getMenu();

        View headerView = navigationView.getHeaderView(0);
        Spinner spin = (Spinner) headerView.findViewById(R.id.spinnerCountry);

        try { grades = getGradesFromDB(MainActivity.this);} catch (Exception e) { e.printStackTrace(); }

        String[] gradesSpinner = new String[grades.size()];
        for (int i = 0; i< grades.size(); i++)
            gradesSpinner[i] = grades.get(i)[0];


        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,gradesSpinner);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        if (getIntent().getExtras() != null) {
            spin.setSelection(getIntent().getIntExtra("choosedGrade", 0)-1);
            p = getIntent().getStringExtra("choosedP");
        }

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("choosedGrade", (i+1));
                editor.putString("choosedSubject", null);
                editor.apply();
                choosedGrade = (i+1);

                menuItemsArray = new ArrayList<String>();
                try {
                    for (int j = 0; j < grades.size(); j++){
                        if(spin.getSelectedItem().toString().equalsIgnoreCase(grades.get(i)[0]))
                            selectedGrade = grades.get(i)[1]; p = grades.get(i)[2];
                    }

                    subjectsMap = getSubjectsFromDB(MainActivity.this, selectedGrade);

                    for (Map.Entry<String, String> entry : subjectsMap.entrySet()) {
                        menuItemsArray.add(entry.getKey());
                    }

                } catch (Exception e) {e.printStackTrace();}
                setMenuItems(menuItemsArray);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
    public ArrayList getData(Context context, String choosedSubject) {
        ArrayList arrayList = new ArrayList<>();

        open(context,"read", "books.hrm");
        final Cursor subjectsCursor = db.getSelect("*", "subject", "book_id='" + choosedSubject + "'");
        if (subjectsCursor.moveToFirst()) {
            do {
                arrayList.add(new Item(subjectsCursor.getString(2), subjectsCursor.getString(0), p, 0, "#09A9FF"));
            } while (subjectsCursor.moveToNext());
        }
        return arrayList;
    }
    public ArrayList<String[]> getGradesFromDB(Context context) throws Exception {

        ArrayList<String[]> dbGrades = new ArrayList<>();

        open(context,"read", "books.hrm");
        final Cursor gradeCursor = db.getSelect("*", "grade", "1");
        if (gradeCursor.moveToFirst()) {
            do {
                dbGrades.add(new String[]{gradeCursor.getString(1), gradeCursor.getString(2), gradeCursor.getString(3)});//grade name, num, p
            } while (gradeCursor.moveToNext());
        }
        return dbGrades;
    }
    public Map<String, String> getSubjectsFromDB(Context context, String grade) throws Exception {

        Map<String, String> dbSubjects = new HashMap<>();

        open(context,"read", "books.hrm");
        final Cursor gradeCursor = db.getSelect("*", "books", "grade='" + grade + "'");
        if (gradeCursor.moveToFirst()) {
            do {
                dbSubjects.put(gradeCursor.getString(2), gradeCursor.getInt(0)+"");
            } while (gradeCursor.moveToNext());
        }
        return dbSubjects;
    }
    public MainAdapter setData(final Context context, ArrayList arrayList, String a){

        MainAdapter adapter = new MainAdapter(context, arrayList, new MainAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {

                chaptersIntent = new Intent(context, ChaptersActivity.class);
                chaptersIntent.putExtra("subj", item.fileName);
                chaptersIntent.putExtra("name", item.chapName);
                chaptersIntent.putExtra("p", a);
                context.startActivity(chaptersIntent);

            }
        });
        return adapter;
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
    public void setMenuItems(ArrayList<String> menuItemsArray){

        drawerMenu.clear();
        for(int temp=0;temp<menuItemsArray.size();temp++){
            // groupId, itemId, order, title
            drawerMenu.add(1, 0, 0, menuItemsArray.get(temp)).setIcon(R.drawable.ic_menu_send);
        }
        drawerMenu.setGroupCheckable(1, true, true);

        Menu communicateMenu = drawerMenu.addSubMenu("Communicate");
        communicateMenu.add(2, 50, 50, "Tell a friend (Link)").setIcon(R.drawable.ic_menu_share);
//        communicateMenu.add(2, 51, 51, "Share app (apk)").setIcon(R.drawable.ic_menu_share);

        Menu aboutMenu = drawerMenu.addSubMenu("About");
        aboutMenu.add(3, 56, 56, "Rate us on PlayStore").setIcon(android.R.drawable.star_on);
        aboutMenu.add(3, 57, 57, "More apps from us").setIcon(R.drawable.b_next);
        aboutMenu.add(3, 58, 58, "About").setIcon(R.drawable.about);
        aboutMenu.add(3, 59, 59, "Exit").setIcon(android.R.drawable.ic_delete);

        if(getIntent().getStringExtra("choosedSubject") != null) changeFragment(subjectsMap.get(getIntent().getStringExtra("choosedSubject")), getIntent().getStringExtra("choosedSubject"));
        else changeFragment((String)subjectsMap.values().toArray()[0], (String)subjectsMap.keySet().toArray()[0]);

        item_click_listener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                System.out.println("Clicked item is " + item.toString() + " & id is " + item.getItemId());

                  if(item.getItemId() == 0){

                      changeFragment(subjectsMap.get(item.toString()), item.toString());

                      SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                      SharedPreferences.Editor editor = sharedPref.edit();
                      editor.putString("choosedSubject", item.toString());
                      editor.apply();

                  }else if(item.getItemId() == 50){// Tell a friend (Link)
                Intent intent4 = new Intent("android.intent.action.SEND");
                intent4.setType("text/plain");
                intent4.putExtra("android.intent.extra.TEXT", getString(R.string.share_link_pre) + " " + getString(R.string.app_name) + " " + getString(R.string.share_link_center) + " " + "https://play.google.com/store/apps/details?id="+getPackageName() + " "+ getString(R.string.share_link_pos));
                startActivity(Intent.createChooser(intent4, "SHARE VIA"));
//                  }else if(item.getItemId() == 51){// Share app (apk)
//                      shareApp(MainActivity.this,"titleTezt", "messageTezt", "subjectTezt");
                  }else if(item.getItemId() == 56){//Rate us
            Toast.makeText(MainActivity.this, "Rate this app :)", Toast.LENGTH_SHORT).show();
            rateApp();
                  }else if(item.getItemId() == 57){//More app
            Toast.makeText(MainActivity.this, "More apps by us :)", Toast.LENGTH_SHORT).show();
            openUrl("https://play.google.com/store/apps/developer?id=Herma%20plc");
                  }else if(item.getItemId() == 58){//About
            startActivity(new Intent(getApplicationContext(), About_us.class));
                  }else if(item.getItemId() == 59){//Exit
                      finish();
                      System.exit(0);
                  }

                drawerLayout.closeDrawers();
                return true;
            }
        };

        navigationView.setNavigationItemSelectedListener(item_click_listener);
        mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.app_name,
                R.string.app_name);
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }
    public void changeFragment(String subject, String title){
        Fragmentbundle = new Bundle();
        //0
        BookFragment bookFragment = new BookFragment();
        Fragmentbundle.putString("choosedSubject", subject);
        Fragmentbundle.putString("p", p);
        bookFragment.setArguments(Fragmentbundle);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,bookFragment).commit();
        setTitle(title);
    }

    public void shareApp(Context context, String chooserTitle,
                                String message, String messageSubject) {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");


        // Append file and send Intent
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        startActivity(Intent.createChooser(intent, "Share app via"));

    }
}