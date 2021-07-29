package com.herma.apps.textbooks.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.ReadActivity;
import com.herma.apps.textbooks.common.Commons;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PremiumFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_premium, container, false);


        return root;
    }


    public void makePremium(){

        String _phone = "", _mac = "", _license_code="";


        OkHttpClient rewardClient = new OkHttpClient();

        Request request = new Request.Builder()
                .header("email", "bloger_api@datascienceplc.com")//public user
                .header("password", "public-password")
                .header("Authorization", "Basic YmxvZ2VyX2FwaUBkYXRhc2NpZW5jZXBsYy5jb206cHVibGljLXBhc3N3b3Jk")
                .url(new Commons(getContext()).WEBSITE + "/manager/api/items/licensee?phone=" + _phone +
                        "&mac=" + _mac + "&license_code=" + _license_code + "&app_id=1")
                .build();
        rewardClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject reader = new JSONObject(myResponse);
                                if((reader.getString("success")).equals("true") ) {

                                    // parse this

//                                    {
//                                        "success": true,
//                                            "error": false,
//                                            "license": {
//                                        "payer_name": "payer_name",
//                                                "license_type": "2",
//                                                "out_date": "2022-05-23",
//                                                "paid_at": "2021-05-23"
//                                    }
//                                    }

                                    //
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });

    }
}