package com.herma.apps.textbooks.common;

/*
 * Created by Esubalew Amenu on 04-Jan-19
 * Mobile +251 92 348 1783
 * Email esubalew.a2009@gmail.com/
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.ReadActivity;
import com.herma.apps.textbooks.SplashActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Commons {

    Context context;
    DB db;
//    public final String WEBSITE = "https://datascienceplc.com/api";
ProgressDialog progressBar;

    /** A numeric constant for request code */
    public static final int REQUEST_CODE_DIALOG = 0;

    String cchapterName = null, csubject = null;
    public Commons(Context context){
        this.context = context;
    }
    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
    public void messageDialog(final Context context, final String serviceType, int title, int message, String fileName, final String fEn, int yesBtn, int noBtn, final int processHeader, String chapterName, String subject, String grade, String chapterID, boolean is_short) {
        cchapterName = chapterName; csubject = subject;
        final Dialog myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.custom_dialog_box);
        myDialog.setTitle(context.getResources().getString(title));
        myDialog.setCancelable(false);

        TextView text = (TextView) myDialog.findViewById(R.id.custom_title);
        text.setMovementMethod(ScrollingMovementMethod.getInstance());

//        System.out.println(" print from common is serviceType" + serviceType + " fEn " + fEn + " chapterName " + chapterName + " subject " + subject + " grade " + grade + " chapterID " + chapterID);
        if(is_short){
            fileName = "Sh " + grade + " " + subject + " " + chapterName;
        }
        if(message==1234)
            text.setText(context.getResources().getString(R.string.no_file) + context.getResources().getString(R.string.no_file_desc_pre)+context.getResources().getString(R.string.no_file_desc_pos));
        else
            text.setText(context.getResources().getString(message));

        Button btnDownloadBrowser = (Button) myDialog.findViewById(R.id.btnDownloadBrowser);

        Button btnDownload = (Button) myDialog.findViewById(R.id.btnDownload);
        btnDownload.setText(context.getResources().getString(yesBtn));
        String finalFileName1 = fileName;
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {

//                System.out.println("download clicked");

                progressBar = new ProgressDialog(context);
                progressBar.setMessage(context.getResources().getString(processHeader));
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setIndeterminate(false);
                progressBar.setCancelable(false);

                progressBar.show();
                if (serviceType.equals("d")) {
                    try {
                        AsyncDownloader asyncDownloader = new AsyncDownloader();
                        String downloadUrl;// = WEBSITE + "/consol/chap?cnt=eth&name=";

                        if (is_short) {

                            downloadUrl = SplashActivity.BASEAPI + "ds_bm/v1/get_et_book/";//+ finalFileName; // Path where you want to download file.
//                            downloadUrl = WEBSITE + "/manager/api/items/get_for_books?cnt=eth&what=short&name=";//+ finalFileName; // Path where you want to download file.
                            asyncDownloader.execute(downloadUrl, chapterID, fEn);

                        } else if (finalFileName1.startsWith("new")) {
                            downloadUrl = SplashActivity.BASEAPI + "ds_bm/v1/book/download/1-12-textbooks/";//+ finalFileName; // Path where you want to download file.
                            asyncDownloader.execute(downloadUrl, finalFileName1, fEn);
                        }else{
//                            downloadUrl = WEBSITE + "/manager/api/items/get_for_books?cnt=eth&what=txt&name=";//+ finalFileName; // Path where you want to download file.
                            downloadUrl = SplashActivity.BASEAPI + "ds_bm/v1/get_et_book/";//+ finalFileName; // Path where you want to download file.
                        asyncDownloader.execute(downloadUrl, finalFileName1, fEn);
                    }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "not Downloaded!/n pls check your connection or try later!", Toast.LENGTH_SHORT).show();
                    }
                }

                myDialog.dismiss();
            }
        });

        Button btnCancel = (Button) myDialog.findViewById(R.id.btnCancel);
        btnCancel.setText(context.getResources().getString(noBtn));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                System.out.println("cancel clicked");
                if (serviceType.equals("first")) {
                    System.exit(0);
                }
                myDialog.dismiss();


            }
        });


        myDialog.show();

    }
    public void deleteSubjectDialog(final Context context, ArrayList<Item> arrayList) {

        String filePath = context.getFilesDir().getPath() + "/Herma/books/";

        new AlertDialog.Builder(context)
                .setTitle("Are you sure")
                .setMessage("Do you want to delete this subject units?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        for(int i = 0; i<arrayList.size(); i++) {
                            System.out.println("delete is " + arrayList.get(i).chapName + " " + arrayList.get(i).fileName);

                            try {
                                File file = file = new File(filePath + arrayList.get(i).fileName);
                                if (file.exists()) file.delete();
                            } catch (Exception ds) {
                            }
                        }

                        Toast.makeText(context,  R.string.you_can_download_again, Toast.LENGTH_LONG).show();


                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    ///////////////////////////////////////////////////////////////
    public class AsyncDownloader extends AsyncTask<String, Integer, String> {
//        private String URL;
        public String fName, fEn;
//        File file;


        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        @Override
        protected String doInBackground(String... params) {

//            URL = params[0]+params[1];

            fName = params[1];
            fEn = params[2];

            try {
                System.out.println("donwload url is ' " + params[0]+params[1]);
                URL url = new URL(params[0]+params[1]);
                connection = (HttpURLConnection) url.openConnection();

                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
                if(!pre.getString("token", "None").equalsIgnoreCase("None"))
                    connection.setRequestProperty("Authorization", "Bearer "+pre.getString("token", "None"));
                connection.setRequestProperty ("Content-Type", "application/pdf");
                connection.setUseCaches(false);
//            connection.setDoInput(true);
//            connection.setDoOutput(true);

                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                String FILEPATH = context.getFilesDir().getPath() + "/Herma/books/";

                output = new FileOutputStream(FILEPATH+fName);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

            } catch (UnknownHostException e) {

                return "Can't download. Please check your connection!";
            }catch (Exception e) {
                e.printStackTrace();
                return "Unknown error! Please try again ";
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            progressBar.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
                try {
//                    if (file.exists()) file.delete();
                } catch (Exception ds) {
                }
            }else {
                    Intent chaptersIntent = new Intent(context, ReadActivity.class);
                    chaptersIntent.putExtra("fileName", fName);
                    chaptersIntent.putExtra("p", fEn);
                    chaptersIntent.putExtra("chapterName", cchapterName);
                    chaptersIntent.putExtra("subject", csubject);
                    context.startActivity(chaptersIntent);


                }
        }
    }


    public boolean showGoogleAd(int licenseLevel){

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
        String license_type = pre.getString("license_type", "");
        String out_date = pre.getString("out_date", "2100-12-31");
        String last_update = pre.getString("last_update", "");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date last_updateDate = null;
        try {
            last_updateDate = sdf.parse(last_update);
            Date out_dateDate = sdf.parse(out_date);


            if( !license_type.equals("") )
            {
                if(Integer.parseInt(license_type) < licenseLevel || last_updateDate.compareTo(out_dateDate) > 0 ){ // if expired
                    System.out.println("isshow ad yes and prevent license_type="+license_type +" out_date="+out_date+" last_update="+last_update);
                    return true;
                }else {
//                    System.out.println("isshow ad no license accepted license_type=" + license_type + " out_date=" + out_date + " last_update=" + last_update);
                    return false;
                }
            }else{ // if not licensed
//                System.out.println("isshow ad yes no license provided license_type="+license_type);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(last_updateDate +" isshow ad yes no license provided license_type="+license_type);
            return true;
        }
    }

    public void loadBanner(AdView adView, String AD_UNIT_ID, FrameLayout adContainerView, Display display) {
        // Create an ad request.
        adView = new AdView(context);
        adView.setAdUnitId(AD_UNIT_ID);
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdSize adSize = getAdSize(adContainerView, display);
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    public AdSize getAdSize(FrameLayout adContainerView, Display display) {
        // Determine the screen width (less decorations) to use for the ad width.
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }


//    comment this method when production
    /**
     * Enables https connections
     */
//    @SuppressLint("TrulyRandom")
//    public static void handleSSLHandshake() {
//        try {
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }};
//
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String arg0, SSLSession arg1) {
//                    return true;
//                }
//            });
//        } catch (Exception ignored) {
//        }
//    }

}
