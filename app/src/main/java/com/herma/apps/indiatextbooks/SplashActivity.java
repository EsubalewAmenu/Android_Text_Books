package com.herma.apps.indiatextbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        SharedPreferences sharedPref = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        int choosedGrade = sharedPref.getInt("choosedGrade", 0);
        String choosedP = sharedPref.getString("choosedP", null);
        String choosedSubject = sharedPref.getString("choosedSubject", null);

        if(choosedGrade != 0){
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("choosedGrade", choosedGrade);
            intent.putExtra("choosedP", choosedP);
            intent.putExtra("choosedSubject", choosedSubject);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(SplashActivity.this, ChooseCountry.class);
            startActivity(intent);
            finish();
        }

    }
}
