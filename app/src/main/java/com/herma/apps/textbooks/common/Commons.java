package com.herma.apps.textbooks.common;

/*
 * Created by Esubalew Amenu on 04-Jan-19
 * Mobile +251 92 348 1783
 * Email esubalew.a2009@gmail.com/
 */

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.herma.apps.textbooks.ChaptersActivity;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.ReadActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Commons {

    Context context;
    DB db;
    public final String WEBSITE = "https://datascienceplc.com/apps";
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
    public boolean isOnline(Context context) {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
//
//    /**
//     * When offline, show a snackbar message
//     */
//    public void showSnackbarOffline(View view) {
//        Snackbar snackbar = Snackbar.make(
//                view, R.string.snackbar_offline, Snackbar.LENGTH_LONG);
//        // Set background color of the snackbar
//        View sbView = snackbar.getView();
//        sbView.setBackgroundColor(Color.WHITE);
//        // Set background color of the snackbar
//        TextView textView = sbView.findViewById(R.id.snackbar_text);
//        textView.setTextColor(Color.BLACK);
//        snackbar.show();
//    }
    public void messageDialog(final Context context, final String serviceType, int title, int message, final String fileName, final String fEn, int yesBtn, int noBtn, final int processHeader, String chapterName, String subject) {
        cchapterName = chapterName; csubject = subject;
        final Dialog myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.custom_dialog_box);
        myDialog.setTitle(context.getResources().getString(title));
        myDialog.setCancelable(false);

        TextView text = (TextView) myDialog.findViewById(R.id.custom_title);
        text.setMovementMethod(ScrollingMovementMethod.getInstance());
        if(message==1234)
            text.setText(context.getResources().getString(R.string.no_file) + context.getResources().getString(R.string.no_file_desc_pre)+fileName+context.getResources().getString(R.string.no_file_desc_pos));
        else
            text.setText(context.getResources().getString(message));

        Button btnDownloadBrowser = (Button) myDialog.findViewById(R.id.btnDownloadBrowser);
        btnDownloadBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

////                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE + "/consol/chap?cnt=eth&name="+fileName));
////                context.startActivity(browserIntent);
//                System.out.println("just clicked...");
                Uri uri = Uri.parse(WEBSITE + "/consol/chap?cnt=eth&name="+fileName); // Path where you want to download file.
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
                request.setTitle(fileName); // Title for notification.
                request.setVisibleInDownloadsUi(true);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);//uri.getLastPathSegment());  // Storage directory path
                ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

                myDialog.dismiss();
            }
        });

        Button btnDownload = (Button) myDialog.findViewById(R.id.btnDownload);
        btnDownload.setText(context.getResources().getString(yesBtn));
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
                            String downloadUrl = WEBSITE + "/consol/chap?cnt=eth&name=";
                            asyncDownloader.execute(downloadUrl, fileName, fEn);

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

    ///////////////////////////////////////////////////////////////
    public class AsyncDownloader extends AsyncTask<String, Long, Pair<String, byte[]>> {
        private String URL;
        public String fName, fEn;
        File file;


        @Override
        protected Pair<String, byte[]> doInBackground(String... params) {

            URL = params[0]+params[1];

            fName = params[1];
            fEn = params[2];

            OkHttpClient httpClient = new OkHttpClient();

            System.out.println("URL is " + URL);
            Request request = new Request.Builder().url(URL)
                    .addHeader("X-CSRFToken", "csrftoken")
                    .addHeader("Content-Type", "application/pdf").build();

            Call call = httpClient.newCall(request);
            try {
                Response response = call.execute();
                if (response.code() == 200) {
                    InputStream inputStream = null;
                    try {
                        inputStream = response.body().byteStream();

                        //added
                        BufferedInputStream input = new BufferedInputStream(inputStream);
                        // create a File object for the parent directory
                        String FILEPATH = "/storage/emulated/0/Herma/books/";
                        File booksDirectory = new File(FILEPATH);
// have the object build the directory structure, if needed.

                        if(!booksDirectory.exists()) System.out.println(booksDirectory.mkdirs());

                        file = new File(FILEPATH + params[1]);
//                        File file = new File(FILEPATH + params[1]);

                        OutputStream output = new FileOutputStream(file);

                        byte[] buff = new byte[1024];

                        long downloaded = 0;
                        long target = response.body().contentLength();
                        publishProgress(0L, target);


                        while (true) {
                            int count = input.read(buff);
                            if (count == -1) {
                                break;
                            }
                            downloaded += count;
                            publishProgress(downloaded, target);

                            //write buff
                            output.write(buff, 0, count);

                            if (isCancelled()) {
                                return null;//false;
                            }
                        }

                        output.flush();
                        output.close();
                        input.close();

                        Pair<String, byte[]> pair = new Pair<>(params[1], buff);

                        return pair;//downloaded == target;

                    } catch (IOException ignore) {
                        System.out.println("on try exception is ignore " + ignore);
//                        System.out.println("Not downloaded " ); // b/c of permission
                        return null;//false;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();

                        }
                    }
                } else {
                    return null;//false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;//false;
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            progressBar.setMax(values[1].intValue());
            progressBar.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(Pair<String, byte[]> pair) {


            try {
                if (pair.second != null && pair.second.length > 0) {

//                    byte[] bytes = pair.second;

                    Intent chaptersIntent = new Intent(context, ReadActivity.class);
                    chaptersIntent.putExtra("fileName", fName);
                    chaptersIntent.putExtra("p", fEn);
                    chaptersIntent.putExtra("chapterName", cchapterName);
                    chaptersIntent.putExtra("subject", csubject);
                    context.startActivity(chaptersIntent);


                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        try{if (file.exists())file.delete();}catch (Exception ds){}
            }
            progressBar.dismiss();
////        textViewStatus.setText(result ? "Downloaded" : "Failed");
        }
    }
//    public void open(Context context, String write, String db_name) {
//
//        db = new DB(context, db_name);
//        try {
//            if (write.equals("write"))
//                db.writeDataBase();
//            else
//                db.createDataBase();
//        } catch (IOException ioe) {
//            throw new Error("Unable to create database");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            db.openDataBase();
//        } catch (SQLException sqle) {
//            throw sqle;
//        }
//    }

    public boolean dec(String filePath, String fileName, String p) {
        try {
            byte[] salt = {69, 121, 101, 45, 62, 118, 101, 114, 69, 121, 101, 45, 62, 118, 101, 114};

            SecretKeyFactory factory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA1");
            String fullPassword = p + fileName;
            KeySpec keySpec = new PBEKeySpec(fullPassword.toCharArray(), salt, 65536,
                    256);
            SecretKey tmp = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            // file decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            IvParameterSpec ivspec = new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            cipher.init(Cipher.DECRYPT_MODE, secret, ivspec);
            FileInputStream fis = new FileInputStream(filePath + fileName.substring(0, fileName.length() - 4));
            FileOutputStream fos = new FileOutputStream(context.getFilesDir() + "nor.pdf");
            byte[] in = new byte[64];
            int read;
            while ((read = fis.read(in)) != -1) {
                byte[] output = cipher.update(in, 0, read);
                if (output != null)
                    fos.write(output);
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                fos.write(output);
            fis.close();
            fos.flush();
            fos.close();
        }catch (Exception lkj) {
            try{ File file = file = new File(filePath + fileName.substring(0, fileName.length() - 4));
                if (file.exists())file.delete();}catch (Exception ds){}
        }
        return true;
    }



}
