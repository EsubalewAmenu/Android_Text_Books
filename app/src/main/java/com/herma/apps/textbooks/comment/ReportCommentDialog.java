package com.herma.apps.textbooks.comment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ReportCommentDialog {

    private Context context;
    private int commentId;

    public ReportCommentDialog(Context context, int commentId) {
        this.context = context;
        this.commentId = commentId;
    }

    public void show() {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            final EditText reasonEditText = new EditText(context);

            new AlertDialog.Builder(context)
                    .setTitle(R.string.report_comment)
                    .setMessage(R.string.report_reason)
                    .setView(reasonEditText)
                    .setPositiveButton(R.string.report, (dialog, which) -> {
                        String reason = reasonEditText.getText().toString();
//                        ReportCommentTask task = new ReportCommentTask();
//                        task.execute(commentId, reason);

                        try {
                            postCommentReport(commentId, reason);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }


    public void postCommentReport(int CommentId, String reason) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = SplashActivity.BASEAPI+"wp/v2/report-comment/" + commentId;

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);

        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("google_user_id", pre.getString("google_user_id", "1"));
//        jsonBody.put("username", pre.getString("email", "1"));
//        jsonBody.put("login_with", pre.getString("registed_with", "1"));
        jsonBody.put("reason", reason);
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        System.out.println("post comment response is ");
//                        System.out.println(response);

                            Toast.makeText(context, context.getString(R.string.thanks_for_reporting), Toast.LENGTH_LONG).show();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                        Toast.makeText(context, context.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                }catch (Exception j){}
            }

        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", pre.getString("email", "1"));
                params.put("password", pre.getString("google_user_id", "1"));
                params.put("login_with", pre.getString("registed_with", "1"));
                return params;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag(this);
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
