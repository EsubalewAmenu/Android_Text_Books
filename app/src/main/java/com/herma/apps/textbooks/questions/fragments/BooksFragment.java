package com.herma.apps.textbooks.questions.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.textbooks.ChaptersActivity;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.adapter.HomeAdapter;
import com.herma.apps.textbooks.model.Item;
import com.herma.apps.textbooks.questions.DB;
import com.herma.apps.textbooks.util.Commons;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This fragment provide the RadioButton/Single Options.
 */
public class BooksFragment extends Fragment
{
    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;

    Spinner spinner;
    DB db;

    public BooksFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_main, container, false);

        spinner = (Spinner) root.findViewById(R.id.gradeSpinner);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);


        String fragmentServiceType = getArguments().getString("serviceType");


        spinnerSetting(fragmentServiceType);


        return root;
    }
    private void spinnerSetting(String fragmentServiceType){


        open("read", "books.hrm");

        Cursor getClasses;

        if(fragmentServiceType.equals("t"))
            getClasses = db.getSelect("distinct grade", "books", "gtype='t'");
        else
            getClasses = db.getSelect("distinct grade", "books", "gtype='g'");

        String[] data = new String[0];
        if(getClasses.moveToFirst()) {
            data = new String[getClasses.getCount()];
            int getClassesCounter = 0;
            do {
                data[getClassesCounter] = getClasses.getString(0);

                getClassesCounter++;

            } while (getClasses.moveToNext());

        }
        db.close();

//        if(fragmentServiceType.equals("t"))
//            data = new String[]{"12", "11", "10", "9"};
//        else
//            data = new String[]{"12", "11"};

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_selected, data);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());

        spinner.setSelection(pre.getInt(fragmentServiceType, 0));

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                pre.edit().putInt(fragmentServiceType, position).apply();

                String item = adapterView.getItemAtPosition(position).toString();

                try {
                    getData(fragmentServiceType, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                setData();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    public void setData(){

        HomeAdapter adapter = new HomeAdapter(getActivity(), arrayList, new HomeAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {
                Intent chaptersIntent = new Intent(getActivity(), ChaptersActivity.class);
                chaptersIntent.putExtra("subj", item.fileName);
                chaptersIntent.putExtra("name", item.chapName);
                chaptersIntent.putExtra("p", item.en);
                startActivity(chaptersIntent);

            }
        });
        recyclerView.setAdapter(adapter);


        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

    }
    public void getData(String fragmentServiceType, String grade) throws Exception {
        arrayList = new ArrayList<>();

        open("read", "books.hrm");
//
//        final Cursor getVersion = db.getSelect("*", "se", "key='v'");
//
//        if(getVersion.moveToFirst()){
//            double v = getVersion.getDouble(2);
//
//            if(v < 0.053d){
//                if (!new Commons(getActivity()).isOnline(getActivity())) {
////                    System.out.println("No internet from SnackBar");
//                    new Commons(getActivity()).showNetworkDialog(getActivity());
//
////                    new Commons(getActivity()).showSnackbarOffline(getActivity().getWindow().getDecorView().getRootView());
//                }else {
//
//                    new Commons(getActivity()).messageDialog(getActivity(), "first", R.string.internet_for_1, R.string.internet_for_1_desc, v + "", null, R.string.register, R.string.close, R.string.registering);
//                }
//            }
//            else {
//                final Cursor subjectsCursor = db.getSelect("*", "books", "grade='" + grade + "' and gtype='"+fragmentServiceType+"'");
//                if (subjectsCursor.moveToFirst()) {
//                    do {
//
//                        int drawableResourceId = 0;
//                        try {
//                            drawableResourceId = this.getResources().getIdentifier(subjectsCursor.getString(3), "drawable", getActivity().getPackageName());
//                        } catch (Exception kl) {
//                        }
//
//                        arrayList.add(new Item(subjectsCursor.getString(2), subjectsCursor.getString(0), subjectsCursor.getString(6), drawableResourceId, "#09A9FF"));
//                    } while (subjectsCursor.moveToNext());
//                }
//            }
//        }
        final Cursor subjectsCursor = db.getSelect("*", "books", "grade='" + grade + "' and gtype='"+fragmentServiceType+"'");
        if (subjectsCursor.moveToFirst()) {
            do {

                int drawableResourceId = 0;
                try {
                    drawableResourceId = this.getResources().getIdentifier(subjectsCursor.getString(3), "drawable", getActivity().getPackageName());
                } catch (Exception kl) {
                }

                arrayList.add(new Item(subjectsCursor.getString(2), subjectsCursor.getString(0), subjectsCursor.getString(6), drawableResourceId, "#09A9FF"));
            } while (subjectsCursor.moveToNext());
        }

    }
    public void open(String write, String db_name) {

        db = new DB(getActivity(), db_name);
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

    public String getDataFromServer(HttpUrl.Builder urlBuilder){
        OkHttpClient client = new OkHttpClient();

        String url = urlBuilder.build().toString();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        /////////////////////////////////////
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.code() == 200) {
//            System.out.println(" response is ok " + response.code() );
                String result = response.body().string();

                response.close();

                return result;
            }
            else {
                response.close();
//                System.out.println(" response is not ok " + response.code());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        return true;
    }
}