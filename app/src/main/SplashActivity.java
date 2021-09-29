package com.herma.apps.textbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT=100;//1100;


    OkHttpClient client = new OkHttpClient();

    //    DB db;
    //After completion of 2000 ms, the next activity will get started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.activity_splash);
        //this will bind your MainActivity.class file with activity_main.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {



                SharedPreferences sharedPref = SplashActivity.this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                String choosedP = sharedPref.getString("choosedP", null);
                String choosedGrade = sharedPref.getString("choosedGrade", null);
                String choosedGradeT = sharedPref.getString("choosedGradeT", "Grade 12");

//                if(choosedGrade == null || choosedGrade.equals(null) || choosedGrade.equals("")){
//                    Intent intent = new Intent(SplashActivity.this, ChooseCountry.class);
//                    startActivity(intent);
//                    finish();
//                }else{


                doGetRequestInit("https://datascienceplc.com/apps/manager/api/items/get_for_books?what=init");



                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("choosedP", choosedP);
                intent.putExtra("choosedGrade", choosedGrade);
                intent.putExtra("choosedGradeT", choosedGradeT);
                    startActivity(intent);
                    finish();
//                }

//                Intent i=new Intent( SplashActivity.this, MainActivity.class);
//                //Intent is used to switch from one activity to another.
//                startActivity(i);
//                //invoke the SecondActivity.
//
//                finish();
//                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }



    // code request code here
    void doGetRequestInit(String url) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("email", "bloger_api@datascienceplc.com")//public user
                    .addHeader("password", "public-password")
                    .addHeader("Authorization", "Basic YmxvZ2VyX2FwaUBkYXRhc2NpZW5jZXBsYy5jb206cHVibGljLXBhc3N3b3Jk")
                    .build();

            Response response = null;

            response = client.newCall(request).execute();

            if (response.code() == 200) {

                String resp = response.body().string();

//                System.out.println("res response.body().string() " + resp);

                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                String que_service_sting = pre.getString("que_service", null);
//        int tot_asked = pre.getInt("tot_asked", 0);
                pre.edit().putString("que_service", resp ).apply();
//        pre.edit().putInt("tot_asked", (tot_asked + answerKey.length)).apply();


//                questionServices(resp);
//                shortnoteServices(resp);

            }

        } catch (IOException e) {

            System.out.println("Exception on doGetRequest " + e);
            e.printStackTrace();
        }

    }



}
//package com.herma.apps.textbooks;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SplashActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//
//
//        SharedPreferences sharedPref = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
//        int choosedGrade = sharedPref.getInt("choosedGrade", 0);
//        String choosedP = sharedPref.getString("choosedP", null);
//        String choosedSubject = sharedPref.getString("choosedSubject", null);
//
//        if(choosedGrade != 0){
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            intent.putExtra("choosedGrade", choosedGrade);
//            intent.putExtra("choosedP", choosedP);
//            intent.putExtra("choosedSubject", choosedSubject);
//            startActivity(intent);
//            finish();
//        }else{
//            Intent intent = new Intent(SplashActivity.this, ChooseCountry.class);
//            startActivity(intent);
//            finish();
//        }
//
//    }
//}
