package com.herma.apps.textbooks.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.ReadActivity;
import com.herma.apps.textbooks.SplashActivity;
import com.herma.apps.textbooks.common.Commons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class PremiumFragment extends Fragment {


    Button btnUpdate;

    EditText etName, etPhone, etCode;
    TextView tvMac;

    @SuppressLint("WifiManagerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_premium, container, false);

        btnUpdate = (Button) root.findViewById(R.id.btnUpdate);

        etName = (EditText) root.findViewById(R.id.etName);
        etCode = (EditText) root.findViewById(R.id.etCode);
        etPhone = (EditText) root.findViewById(R.id.etPhone);

        tvMac = (TextView) root.findViewById(R.id.tvMac);

        final String macAddr, androidId;
        UUID deviceUuid;

//        String macAddress;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            WifiManager wifiMan = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();

            macAddr = wifiInf.getMacAddress();
            androidId = "" + android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());

        }
        else {
            androidId = "" + android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            deviceUuid = new UUID(androidId.hashCode(), new Random(999999).nextLong() );
        }


        tvMac.setText("Your id is :" + deviceUuid.toString().substring(0,6));

        handleSSLHandshake();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String _phone = "0923481783", _mac = "MMAC", _license_code="5335", _name="Grains";
                String _phone = etPhone.getText().toString(), _mac = deviceUuid.toString().substring(0,6),
                        _license_code = etCode.getText().toString(), _name = etName.getText().toString();

                System.out.println("print daata " + _phone+ _mac+ _license_code+ _name);
                makePremiumAPI(_phone, _mac, _license_code, _name);

            }
        });

        return root;
    }

    private void makePremiumAPI(String _phone, String _mac, String _license_code, String _name) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(getContext());

//
//
//                String BASEURL = "https://192.168.8.101:8082/wp/ds/api/DSACTIVATOR/v1/";
//                String BASEURL = "https://192.168.8.101:8082/wp/ds/wp-json/DSACTIVATOR/v1/";

//                String BASEURL = "https://datascienceplc.com/wp-json/DSACTIVATOR/v1/";

                String url = "DSACTIVATOR/v1/register?service_id=1";


                StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashActivity().BASEAPI+url+
                        "&phone=" + _phone +"&mac=" + _mac + "&license_code=" + _license_code + "&name=" + _name,

                        new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {
                                    System.out.println(" response is " + response);
//        response is {"success":true,"error":false,"activator":{"license_code":"5335","license_type":"1","out_date":"2021-09-22"}}

                                    try {
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);
//
//                                        String verif_customer_rewards = "";
//
//
                                        if(jsonObj.getString("success").equals("true") ) {

                                            jsonObj = new JSONObject(jsonObj.getString("activator"));

                                    String license_code = jsonObj.getString("license_code");
                                    String license_type = jsonObj.getString("license_type");
                                    String out_date = jsonObj.getString("out_date");
                                            String last_update = jsonObj.getString("last_update");
                                    //
                                            System.out.println("license_code "+license_code + " license_type="+license_type+" out_date="+out_date+" last_update="+last_update);

                                            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
                                            pre.edit().putString("license_type", license_type ).apply();
                                            pre.edit().putString("out_date", out_date ).apply();
                                            pre.edit().putString("last_update", last_update ).apply();


                                            Toast.makeText(getContext(), getString(R.string.thanks_for_activated), Toast.LENGTH_SHORT).show();

                                        }else
                                        {
                                            Toast.makeText(getContext(), getString( R.string.wrong_code ), Toast.LENGTH_SHORT).show();
//                                            System.out.println("activator is not correct");
                                        }


                                    } catch (final JSONException e) {
                                        Toast.makeText(getContext(), getString(R.string.check_your_internet ), Toast.LENGTH_SHORT).show();
                                        System.out.println("Exception is " + e);
                                    }

                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Toast.makeText(getContext(), getString(R.string.check_your_internet ), Toast.LENGTH_SHORT).show();
                            System.out.println("That didn't work! pls try again" + error);

                        }catch (Exception j){}
                    }

                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", "bloger_api@datascienceplc.com");//public user
                        params.put("password", "public-password");
                        params.put("Authorization", "Basic YmxvZ2VyX2FwaUBkYXRhc2NpZW5jZXBsYy5jb206cHVibGljLXBhc3N3b3Jk");
                        return params;
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
        }, 1500);
    }

//    comment this method when production
    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}