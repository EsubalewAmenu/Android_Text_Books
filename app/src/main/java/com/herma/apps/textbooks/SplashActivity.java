package com.herma.apps.textbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT=1100;

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
