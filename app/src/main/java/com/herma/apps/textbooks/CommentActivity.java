package com.herma.apps.textbooks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.herma.apps.textbooks.comment.Comment;
import com.herma.apps.textbooks.comment.CommentAdapter;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.common.Item;
import com.herma.apps.textbooks.ui.about.About_us;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

//    private Button btnAddComment;
    private RecyclerView rvComment;
    private CommentAdapter commentAdapter;
    private List<Comment> comments;
    private Comment comment;

    private TextView tv_no_comment;
    private Button retry_button;

    public String chapter = "";
    SharedPreferences pre = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Add back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getIntent().getStringExtra("chapterName"));

        chapter = getIntent().getStringExtra("fileName");
        pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        initViews();
//        initListeners();

        try {
            initRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comment, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_comment) {
            // Handle the button click event here
            addComment();
            return true;
        }else if(id == android.R.id.home){
        // Handle clicks on the back button (the left arrow in the toolbar)
        onBackPressed();
        return true;
    }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
//        btnAddComment = findViewById(R.id.btn_add_comment);
        rvComment = findViewById(R.id.rv_comment);
        tv_no_comment = findViewById(R.id.tv_no_comment);
        retry_button = findViewById(R.id.retry_button);
    }

//    private void initListeners() {
//        retry_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addComment();
//            }
//        });
//    }

    private void initRecyclerView() throws JSONException {

        getComments(0, 1);

        comments = new ArrayList<>();

        commentAdapter = new CommentAdapter(comments, getApplicationContext(), chapter);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setAdapter(commentAdapter);
    }

//    private void showCommentSection() {
//        rvComment.setVisibility(View.VISIBLE);
//        btnAddComment.setVisibility(View.VISIBLE);
//    }

    private void addComment() {
        {
//            String author = "Test author";

            // Create an instance of the dialog box
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CommentActivity.this);

            // Inflate the XML layout file
//                View dialogView = getLayoutInflater().inflate(R.layout.dialog_comment, null);
            View dialogView = LayoutInflater.from(CommentActivity.this).inflate(R.layout.dialog_comment, null);
            dialogBuilder.setView(dialogView);

            // Add the OK and Cancel buttons
            dialogBuilder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Get the input value and do something with it
                    TextInputEditText input = dialogView.findViewById(R.id.ti_message);
                    String inputMessage = input.getText().toString();


                    try {
                        postComment(input.getText().toString(), 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            dialogBuilder.setNegativeButton("Cancel", null);

            // Show the dialog box
            dialogBuilder.show();
        }
    }

    public void postComment(String userComment, int parent) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = SplashActivity.BASEAPI+"wp/v2/chapter/comment/"+chapter+"/"+parent;

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("google_user_id", pre.getString("userId", "1"));
        jsonBody.put("email", pre.getString("email", "1"));
        jsonBody.put("registed_with", "google");
        jsonBody.put("comment_content", chapter+" "+userComment);
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("post comment response is ");
                        System.out.println(response);

                        try {
                                JSONObject c = new JSONObject(response).getJSONObject("comment");

                                comment = new Comment();
                                comment.setCommentId(c.getInt("comment_ID"));
                                comment.setLike(c.getInt("likes"));
                                comment.setDislike(c.getInt("dislikes"));
                                comment.setIs_user_liked(c.getInt("is_user_liked"));
                                comment.setIs_user_disliked(c.getInt("is_user_liked"));
                                comment.setComment(c.getString("comment_content").substring(chapter.length()));
                                comment.setAuthor(c.getString("display_name"));
                                comment.setTimestamp(c.getString("comment_date_gmt"));
                                comment.setAuthor_avatar_url(c.getString("author_avatar_urls"));
//                    comment.setAuthor_avatar_url(c.getJSONObject("author_avatar_urls").getString("24")); // options are 24, 48 & 96
                                comment.setChildCommentCount(c.getInt("child_comments_count"));
                                comments.add(comment);
                                commentAdapter.notifyDataSetChanged();


                        } catch (final JSONException e) {
                            System.out.println(e);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Check if the error has a network response
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    // Get the error status code
                    int statusCode = response.statusCode;

                    // Get the error response body as a string
                    String responseBody = new String(response.data, StandardCharsets.UTF_8);

                    // Print the error details
                    System.out.println("Error status code: " + statusCode);
                    System.out.println("Error response body: " + responseBody);
                } else {
                    // The error does not have a network response
                    System.out.println("Error message: " + error.getMessage());
                }
            }

        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", pre.getString("email", "1"));//SplashActivity.USERNAME);
                params.put("password", pre.getString("userId", "1"));//SplashActivity.PAZZWORD);
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

    private void getComments(int parent, int page) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = SplashActivity.BASEAPI+"wp/v2/chapter/comments/"+chapter+"/"+parent+"?page="+page;


        JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", pre.getString("email", "1"));
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("request response is ");
                        System.out.println(response);

                        if (response != null) {
                            setComments(response, page);
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                  System.out.println("That didn't work! " + error);
                try{
//                            Toast.makeText(getContext(), "That didn't work! " + error, Toast.LENGTH_LONG).show();

                    if (!isOnline()) {
                        showNetworkDialog();
                        tv_no_comment.setText("No network available. Please check your connection settings or ");
                        tv_no_comment.setVisibility(View.VISIBLE);
                        retry_button.setVisibility(View.VISIBLE);

                        retry_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    getComments(parent, page);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                    else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                            || error.getCause() instanceof ConnectTimeoutException
                            || error.getCause() instanceof SocketException
                            || (error.getCause().getMessage() != null
                            && error.getCause().getMessage().contains("Connection timed out"))) {
                        Toast.makeText(getApplicationContext(), "Connection timeout error. \npls Swipe to reload",
                                Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "An unknown error occurred.\npls swap to refresh",
                                Toast.LENGTH_LONG).show();
                        System.out.println("Error on sys:"+error);
                    }
                }catch (Exception j){}
            }

        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", pre.getString("email", "1"));//SplashActivity.USERNAME);
                params.put("password", pre.getString("userId", "1"));//SplashActivity.PAZZWORD);
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

    private void setComments(String response, int page) {

            try {
                JSONArray datas = new JSONArray(response);

                if(datas.length() == 0 && page == 1){
                    Toast.makeText(CommentActivity.this, "There's no post, Be the first!", Toast.LENGTH_LONG).show();
                    tv_no_comment.setText("No comments yet.\nBe the first one to share your thoughts.");
                    tv_no_comment.setVisibility(View.VISIBLE);
                    retry_button.setVisibility(View.GONE);
                }else{
                    tv_no_comment.setVisibility(View.GONE);
                    retry_button.setVisibility(View.GONE);
                }
                for(int i = 0; i < datas.length(); i++){
                    JSONObject c = datas.getJSONObject(i);

                    comment = new Comment();
                    comment.setCommentId(c.getInt("comment_ID"));
                    comment.setLike(c.getInt("likes"));
                    comment.setDislike(c.getInt("dislikes"));
                    comment.setIs_user_liked(c.getInt("is_user_liked"));
                    comment.setIs_user_disliked(c.getInt("is_user_disliked"));
                    comment.setComment(c.getString("comment_content").substring(chapter.length()));
                    comment.setAuthor(c.getString("display_name"));
                    comment.setTimestamp(c.getString("comment_date_gmt"));
                    comment.setAuthor_avatar_url(c.getString("author_avatar_urls"));
//                    comment.setAuthor_avatar_url(c.getJSONObject("author_avatar_urls").getString("24")); // options are 24, 48 & 96
                    comment.setChildCommentCount(c.getInt("child_comments_count"));
                    comments.add(comment);
                    commentAdapter.notifyDataSetChanged();

                }

            } catch (final JSONException e) {
                System.out.println(e);
            }

    }


    public boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void showNetworkDialog() {
        // Create an AlertDialog.Builder
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        // Set an Icon and title, and message
        builder.setIcon(R.drawable.ic_warning);
        builder.setTitle(getString(R.string.no_network_title));
        builder.setMessage(getString(R.string.no_network_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 1234);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);

        // Create and show the AlertDialog
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}