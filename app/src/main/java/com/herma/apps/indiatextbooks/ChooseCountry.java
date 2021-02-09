package com.herma.apps.indiatextbooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.herma.apps.indiatextbooks.common.DB;
import com.herma.apps.indiatextbooks.common.Item;
import com.herma.apps.indiatextbooks.common.MainAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseCountry extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;

    ArrayList<String[]> grades = new ArrayList<>();

    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_country);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();

        try { grades = getGradesFromDB(ChooseCountry.this);} catch (Exception e) { e.printStackTrace(); }

        for (int i = 0; i< grades.size(); i++) {
            arrayList.add(new Item(grades.get(i)[0] , grades.get(i)[1], grades.get(i)[2], R.drawable.icon, "#000000"));
        }
//        arrayList.add(new Item("India" , "1", "", R.drawable.icon, "#000000"));

        MainAdapter adapter = new MainAdapter(ChooseCountry.this, arrayList, new MainAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {


                    SharedPreferences sharedPref = ChooseCountry.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("choosedGrade", Integer.parseInt(item.fileName));
                editor.putString("choosedP", item.en);
                editor.apply();

                ///////////
                Intent intent = new Intent(ChooseCountry.this, MainActivity.class);
                intent.putExtra("choosedGrade", Integer.parseInt(item.fileName));
                intent.putExtra("choosedP", item.en);
                intent.putExtra("choosedSubject", (String) null);
                startActivity(intent);
                finish();
                ///////////////////////////////////////////////
            }
        });
        recyclerView.setAdapter(adapter);


        GridLayoutManager manager = new GridLayoutManager(ChooseCountry.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
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
    public ArrayList<String[]> getGradesFromDB(Context context) throws Exception {

        ArrayList<String[]> dbGrades = new ArrayList<>();

        open(context,"read", "books.hrm");
        final Cursor gradeCursor = db.getSelect("*", "grade", "1");
        if (gradeCursor.moveToFirst()) {
            do {
                dbGrades.add(new String[]{gradeCursor.getString(1), gradeCursor.getString(0), gradeCursor.getString(3)});//grade name, num, p
            } while (gradeCursor.moveToNext());
        }
        return dbGrades;
    }
}