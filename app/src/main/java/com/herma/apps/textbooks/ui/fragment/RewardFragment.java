package com.herma.apps.textbooks.ui.fragment;

import static com.herma.apps.textbooks.common.TokenUtils.isTokenExpired;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.ReadActivity;
import com.herma.apps.textbooks.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardFragment extends Fragment {

    private TextView totalRewardsTextView, pendingRewardsTextView, paidRewardsTextView;
    private Button orderPaymentButton, loadMoreButton;
    private RecyclerView rewardsRecyclerView;
    private RewardsAdapter rewardsAdapter;
    private List<RewardItem> rewardsList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;

    int pay_me = 0;
    double pendingRewardAmount = 0d, min_payout = 2d;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward, container, false);

        // Initialize views
        totalRewardsTextView = view.findViewById(R.id.totalRewards);
        pendingRewardsTextView = view.findViewById(R.id.pendingRewards);
        paidRewardsTextView = view.findViewById(R.id.paidRewards);
        orderPaymentButton = view.findViewById(R.id.orderPaymentButton);
        rewardsRecyclerView = view.findViewById(R.id.rewardsRecyclerView);
        loadMoreButton = view.findViewById(R.id.loadMoreButton);

        // Setup RecyclerView
        rewardsAdapter = new RewardsAdapter(rewardsList);
        rewardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rewardsRecyclerView.setAdapter(rewardsAdapter);

        // Load initial data
        loadRewardsData(currentPage);

        // Order Payment Button Click
        orderPaymentButton.setOnClickListener(v -> {
             if (pay_me == 1) {
                // Show message if the user has already ordered
                Toast.makeText(getContext(), getString(R.string.already_ordere), Toast.LENGTH_SHORT).show();
            } else if (pendingRewardAmount < min_payout) {
                // Show message for minimum payment
                Toast.makeText(getContext(), getString(R.string.minimum_payment_order) + min_payout + getString(R.string.etb), Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with payment order logic
                 showPhoneNumberDialog(getContext());
            }
        });


        // Load More Button Click
        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading) {
                currentPage++;
                loadRewardsData(currentPage);
            }
        });

        return view;
    }

    // Function to load rewards data
    private void loadRewardsData(int page) {
        isLoading = true;
        loadMoreButton.setEnabled(false);

        // Simulating data fetch (replace this with an API call)
                fetchRewards(page);
    }

    // Function to handle payment order
    private void orderPayment() {

        if (isLoggedIn()) {

            RequestQueue queue = Volley.newRequestQueue(getContext());

            SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String phone_number = prefs.getString("phone_number", "");
            String url = "ds_rewards/v1/pay-me/mobile_card?phone=" + phone_number;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SplashActivity.BASEAPI + url,

                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                        // Update the reward summary
                                    String code = jsonObject.getString("code");

                                    if(code.equals("pay-me")) {
                                        pay_me=1;
                                        Toast.makeText(getContext(), getString(R.string.payment_ordered_successfully), Toast.LENGTH_SHORT).show();
                                    }else{
                                        String response_message = jsonObject.getString("response");
                                        Toast.makeText(getContext(), response_message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Failed to parse rewards data", Toast.LENGTH_SHORT).show();
                                }

                            }


                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Check if the error has a network response
                    System.out.println("my rewards endpoint error ");

                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        String responseBody = new String(response.data, StandardCharsets.UTF_8);
                        Toast.makeText(getContext(), responseBody, Toast.LENGTH_LONG).show();
                    } else {
                        // The error does not have a network response
                        System.out.println("Error message: " + error.getMessage());

                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());

                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + pre.getString("token", "None"));
                    return headers;
                }

            };


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            stringRequest.setTag(this);
            queue.add(stringRequest);

        }
    }

    // Mock function to fetch rewards (replace with real API)
    private void fetchRewards(int page) {
//        List<RewardItem> mockRewards = new ArrayList<>();
        if (isLoggedIn()) {

        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = "ds_rewards/v1/my_rewards/" + page;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, SplashActivity.BASEAPI + url,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("my rewards endpoint response ");
                        System.out.println(response);

                        if (response != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                try {
                                    // Update the reward summary
                                    pendingRewardAmount = jsonObject.getDouble("pending_reward_amount_ETB");
                                    double paidRewardAmount = jsonObject.getDouble("paid_reward_amount_ETB");
                                    min_payout = jsonObject.getDouble("min_payout");

                                    totalRewardsTextView.setText("Total Rewards: " + String.format("%.2f", (pendingRewardAmount + paidRewardAmount) ) + " ETB");
                                    pendingRewardsTextView.setText("Pending: " + String.format("%.2f", pendingRewardAmount) + " ETB");
                                    paidRewardsTextView.setText("Paid: " + String.format("%.2f", paidRewardAmount) + " ETB");

                                    pay_me = jsonObject.getInt("pay_me");
                                    if(pay_me == 1) {
                                        orderPaymentButton.setText(getString(R.string.payment_ordered));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Parse the rewards list
                                JSONArray rewardsArray = jsonObject.getJSONArray("rewards");
                                List<RewardItem> rewards = new ArrayList<>();
                                for (int i = 0; i < rewardsArray.length(); i++) {
                                    JSONObject rewardObject = rewardsArray.getJSONObject(i);
                                    int id = rewardObject.getInt("id");
//                                    String date = rewardObject.getString("date");
                                    String formattedDate = rewardObject.getString("date");;
                                    try {
                                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        Date date = inputFormat.parse(formattedDate);
                                        formattedDate = outputFormat.format(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                    String status = rewardObject.getString("status");
//                                    String userRewardAmount = rewardObject.optString("user_reward_amount", "0");

                                    // Format the userRewardAmount to two decimal places
                                    String userRewardAmountRaw = rewardObject.optString("user_reward_amount", "0");
                                    double userRewardAmountValue = Double.parseDouble(userRewardAmountRaw.isEmpty() ? "0" : userRewardAmountRaw);
                                    String userRewardAmount = String.format("%.2f", userRewardAmountValue);

                                    rewards.add(new RewardItem(id, formattedDate, userRewardAmount, status));
                                }

                                if(!rewards.isEmpty()) {
                                    rewardsList.addAll(rewards);
                                    rewardsAdapter.notifyDataSetChanged();


                                    isLoading = false;
                                    loadMoreButton.setEnabled(true);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Failed to parse rewards data", Toast.LENGTH_SHORT).show();
                            }

                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Check if the error has a network response
                System.out.println("my rewards endpoint error ");

                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    // Get the error status code
//                    int statusCode = response.statusCode;

                    // Get the error response body as a string
                    String responseBody = new String(response.data, StandardCharsets.UTF_8);

                    // Print the error details
//                    System.out.println("Error status code: " + statusCode);
//                    System.out.println("Error response body: " + responseBody);
                    Toast.makeText(getContext(), responseBody, Toast.LENGTH_LONG).show();

                } else {
                    // The error does not have a network response
                    System.out.println("Error message: " + error.getMessage());
//                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());

                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + pre.getString("token", "None"));
                return headers;
            }

        };


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            stringRequest.setTag(this);
            queue.add(stringRequest);

        }
    }

    public boolean isLoggedIn() {

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = pre.getString("token", "None");

        boolean isExpired;

        if (token.equals("None")) {
            isExpired = true;
        } else {
            isExpired = isTokenExpired(token);
        }


        if (!isExpired) {
            return true;

        } else {
            Toast.makeText(getContext(), getString(R.string.sign_in_first), Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void showPhoneNumberDialog(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedPhoneNumber = prefs.getString("phone_number", "");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.reward_receiver_ethiotel_phone_number);

        // EditText for user input
        EditText input = new EditText(context);
        input.setHint("09XXXXXXXX");
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)}); // Limit to 13 characters max
        if (!TextUtils.isEmpty(savedPhoneNumber)) {
            input.setText(savedPhoneNumber);
        }

        // Layout
        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(50, 20, 50, 20);
        layout.addView(input);
        builder.setView(layout);

        // Buttons
        builder.setPositiveButton(R.string.request_payment, (dialog, which) -> {
            String phoneNumber = input.getText().toString().trim();
            if (isValidEthiopianPhoneNumber(phoneNumber)) {
                prefs.edit().putString("phone_number", phoneNumber).apply();
//                Toast.makeText(context, "Phone number saved!", Toast.LENGTH_SHORT).show();
                orderPayment();
            } else {
                Toast.makeText(context, getString(R.string.invalid_ethiopian_phone_number), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton( getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private static boolean isValidEthiopianPhoneNumber(String phone) {
        return phone.matches("\\+2519\\d{8}") || phone.matches("09\\d{8}");
        // Matches +2519XXXXXXXX or 09XXXXXXXX
    }
}
