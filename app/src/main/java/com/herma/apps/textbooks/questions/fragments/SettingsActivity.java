package com.herma.apps.textbooks.questions.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.SplashActivity;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference myPref = (Preference) findPreference("set_lang");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            myPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    System.out.println("set_lang is clicked");
                    String changedValue = prefs.getString(preference.getKey(), "defValue");

                    System.out.println(changedValue);
//                    prefs.getString(preference.getKey(), "am");

                    Locale locale = new Locale(changedValue);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getActivity().getBaseContext().getResources().updateConfiguration(config,
                            getActivity().getBaseContext().getResources().getDisplayMetrics());

                    /////////////////////////////////////////////////////////////////////////
//                    Intent mStartActivity = new Intent(getActivity(), SettingsActivity.class);
//                    int mPendingIntentId = 123456;
//                    PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                    AlarmManager mgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
//                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                    System.exit(0);

                    ///////////////////////////////////////////////////////////////////////////
//                    System.exit(0);

//                    getActivity().setContentView(R.layout.settings_activity);

                    return true;
                }

            });
        }
    }
}