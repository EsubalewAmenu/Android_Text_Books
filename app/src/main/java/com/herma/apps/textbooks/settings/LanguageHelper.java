package com.herma.apps.textbooks.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.ReadActivity;
import com.herma.apps.textbooks.ui.about.About_us;

import java.util.Locale;

public class LanguageHelper {

    public static final String LANGUAGE_CODE_KEY = "language_code";

    public static void setLanguage(Context context, String languageCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LANGUAGE_CODE_KEY, languageCode);
        editor.apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        String languageCode = prefs.getString(LANGUAGE_CODE_KEY, "None");
        return prefs.getString(LANGUAGE_CODE_KEY, Locale.getDefault().getLanguage());
    }

    public static void updateLanguage(Activity activity) {
        String languageCode = getLanguage(activity);
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        Resources res = activity.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());

        // Recreate any views that need to be updated with the new language
        activity.setContentView(R.layout.activity_settings);
    }

}
