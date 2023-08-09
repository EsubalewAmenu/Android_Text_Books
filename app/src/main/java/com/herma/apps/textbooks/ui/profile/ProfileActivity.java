package com.herma.apps.textbooks.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ListView contributedQuizList;
    private Button retryButton, followButton;

    private RequestQueue queue;
    private TextView profile_name, textview_contributions;
    private ImageView profile_image;

    private String username;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Add back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        username = getIntent().getStringExtra("username");

        setTitle("@"+username);

        profile_name = findViewById(R.id.profile_name);
        textview_contributions = findViewById(R.id.textview_contributions);
        profile_image = findViewById(R.id.profile_image);

        contributedQuizList = findViewById(R.id.contributedQuizList);



        retryButton = findViewById(R.id.retry_button);
        followButton = findViewById(R.id.follow_button);

        // Set the retry button's click listener
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the retry button initially when attempting to fetch again
                retryButton.setVisibility(View.GONE);
                // Try to fetch the data again
                fetchDataFromBackend();
            }
        });

        fetchDataFromBackend();
    }
    private void fetchDataFromBackend(){


        String apiUrl = new SplashActivity().BASEAPI + "wp/v2/users/profile/"+username;

        queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                                System.out.println("main resp is " + response);

                        try {

                            contributedQuizList.setVisibility(View.VISIBLE);
                            profile_name.setVisibility(View.VISIBLE);
                            profile_image.setVisibility(View.VISIBLE);
                            textview_contributions.setVisibility(View.VISIBLE);


                            JSONObject c = new JSONObject(response);

                            profile_name.setText(c.getString("first_name") + " " + c.getString("last_name"));
                            Glide.with(getApplicationContext())
                                    .load(c.getString("avatar_url"))
                                    .placeholder(R.drawable.herma)
                                    .into(profile_image);

                            if(!prefs.getString("username", "None").equals(username)) {
                                followButton.setVisibility(View.VISIBLE);
                                followButton.setHint(c.getBoolean("is_following")+"");

                                if(followButton.getHint().equals("true"))
                                    followButton.setText("Unfollow");
                                else
                                    followButton.setText("Follow");

                                followButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                            followUnfollowRequest(username, followButton.getHint().equals("true") ? "unfollow" : "follow", followButton);
                                    }
                                });
                            }

                            populateListView();
                            followersTab(c.getJSONArray("followers"),c.getJSONArray("followings"));

                        } catch (final JSONException e) {
                            System.out.println(e);
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
//                System.out.println("main resp is error " + error);
                showError("Failed to fetch data from server. Please try again.");

            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("Authorization", "Bearer "+pre.getString("token", "None"));

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag(this);
        queue.add(stringRequest);
    }

    private void followUnfollowRequest(String username, String type, Button followButton){

        String apiUrl = new SplashActivity().BASEAPI + "wp/v2/users/friendship/"+type+"/"+username;

        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        System.out.println("follow unfollow " + response);
                        if(type.equals("follow")){
                            followButton.setText("Unfollow");
                            followButton.setHint("true");
                        }else{
                            followButton.setText("Follow");
                            followButton.setHint("false");
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
//                    System.out.println("Error response body: " + responseBody);
                    Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_SHORT).show();
                    showError(responseBody);
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
//                    System.out.println("main resp is error " + error);
                    showError(getString(R.string.check_your_internet));
                }


            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("Authorization", "Bearer "+pre.getString("token", "None"));

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag(this);
        queue.add(stringRequest);
    }

    private void followersTab(JSONArray followers, JSONArray followings){
        ViewPager2 viewPager = findViewById(R.id.view_pager_followers);
        TabLayout tabs = findViewById(R.id.followersTab);

        viewPager.setAdapter(new ViewPagerAdapter(this, followers, followings));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabs, viewPager, true, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Followers");
                    break;
                case 1:
                    tab.setText("Followings");
                    break;
            }
        }
        );
        tabLayoutMediator.attach();

    }



    private void showError(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Hide your ListView or other content views
                contributedQuizList.setVisibility(View.GONE);
                profile_name.setVisibility(View.GONE);
                profile_image.setVisibility(View.GONE);
                textview_contributions.setVisibility(View.GONE);
                followButton.setVisibility(View.GONE);

                // Show the retry button and optionally set an error message as its text
                retryButton.setVisibility(View.VISIBLE);
                retryButton.setText(message + "\nRetry?");
            }
        });
    }


    private void populateListView() {
        // Sample static data
        List<String> contributedQuizListArray = new ArrayList<>();
        contributedQuizListArray.add("Coming soon!");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_follow, R.id.name_text, contributedQuizListArray);
        contributedQuizList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
        // Handle clicks on the back button (the left arrow in the toolbar)
        onBackPressed();
        return true;
    }

        return super.onOptionsItemSelected(item);
    }

}