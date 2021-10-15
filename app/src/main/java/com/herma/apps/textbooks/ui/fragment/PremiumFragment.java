package com.herma.apps.textbooks.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
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
import com.herma.apps.textbooks.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class PremiumFragment extends Fragment {


    Button btnUpdate;

    EditText etName, etPhone, etCode;
    TextView tvMac, tvShow_license, tvPremium;

    String license_type, paid_date, out_date, last_update, deviceUuid;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_premium, container, false);

        btnUpdate = (Button) root.findViewById(R.id.btnUpdate);

        etName = (EditText) root.findViewById(R.id.etName);
        etCode = (EditText) root.findViewById(R.id.etCode);
        etPhone = (EditText) root.findViewById(R.id.etPhone);

        tvMac = (TextView) root.findViewById(R.id.tvMac);
        tvShow_license = (TextView) root.findViewById(R.id.tvShow_license);
        tvPremium = (TextView) root.findViewById(R.id.tvPremium);

        tvPremium.setMovementMethod(LinkMovementMethod.getInstance());
        tvPremium.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("http://datascienceplc.com/services?activation=textbooks"));
                startActivity(browserIntent);
            }
        });

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
        license_type = pre.getString("license_type", "");
        paid_date = pre.getString("paid_date", "");
        out_date = pre.getString("out_date", "");
        last_update = pre.getString("last_update", "");

        deviceUuid = pre.getString("deviceUuid", "");

        if(deviceUuid.equals("")) {
            deviceUuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            pre.edit().putString("deviceUuid", deviceUuid ).apply();

        }

        tvMac.setText("ID : " + deviceUuid);


        showUsersLicense();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String _phone = "0923481783", _mac = "MMAC", _license_code="5335", _name="Grains";
                String _phone = etPhone.getText().toString(), _mac = deviceUuid,
                        _license_code = etCode.getText().toString(), _name = etName.getText().toString();

                if(_phone.equals("") || _license_code.equals("") || _mac.equals("") || _name.equals("") ){
                    Toast.makeText(getContext(), getString(R.string.fill_the_form), Toast.LENGTH_SHORT).show();
                }else {
                    btnUpdate.setEnabled(false);
//                    System.out.println("print daata " + _phone + _mac + _license_code + _name);
                    makePremiumAPI(_phone, _mac, _license_code, _name);
                }
            }
        });

        return root;
    }

    public void showUsersLicense(){


                try {

                    String newLine = System.getProperty("line.separator");

                    String show = newLine;

                    if (!license_type.equals("")) {

                        if(license_type.equals("1"))  show += getString(R.string.your_license_type) + "Silver";
                        else if(license_type.equals("2"))  show += getString(R.string.your_license_type) + "Gold";

                        show += newLine + getString(R.string.paid_date) + paid_date;
                        show += newLine + getString(R.string.out_date) + out_date;


                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        Date last_updateDate = sdf.parse(last_update);
                        Date out_dateDate = sdf.parse(out_date);

                        if (last_updateDate.compareTo(out_dateDate) > 0) { // if expired
                            show += newLine + newLine + getString(R.string.license_out_dated);
                        } else {
                            show += newLine + newLine + getString(R.string.license_not_out_dated);
                        }
                        tvShow_license.setText(show);
                    } else { // if not licensed
                        tvShow_license.setText(R.string.not_registed);
                    }

                }catch (Exception lk) {System.out.println("Exeption from premiumFragment " + lk); }
    }
    private void makePremiumAPI(String _phone, String _mac, String _license_code, String _name) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(getContext());


                String url = "DSACTIVATOR/v1/register?service_id=1";


                StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashActivity().BASEAPI+url+
                        "&phone=" + _phone +"&mac=" + _mac + "&license_code=" + _license_code + "&name=" + _name,

                        new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {
//                                    System.out.println(" response is " + response);
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
                                    license_type = jsonObj.getString("license_type");
                                            paid_date = jsonObj.getString("paid_date");
                                             out_date = jsonObj.getString("out_date");
                                            last_update = jsonObj.getString("last_update");
                                    //
                                            System.out.println("license_code "+license_code + " license_type="+license_type+" paid_date="+paid_date+" out_date="+out_date+" last_update="+last_update);

                                            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
                                            pre.edit().putString("license_type", license_type ).apply();
                                            pre.edit().putString("paid_date", paid_date ).apply();
                                            pre.edit().putString("out_date", out_date ).apply();
                                            pre.edit().putString("last_update", last_update ).apply();

                                            showUsersLicense();

                                            Toast.makeText(getContext(), getString(R.string.thanks_for_activated), Toast.LENGTH_LONG).show();

                                        }else
                                        {
                                            Toast.makeText(getContext(), getString( R.string.wrong_code ), Toast.LENGTH_LONG).show();
//                                            System.out.println("activator is not correct");
                                            btnUpdate.setEnabled(true);
                                        }


                                    } catch (final JSONException e) {
                                        Toast.makeText(getContext(), getString(R.string.check_your_internet ), Toast.LENGTH_SHORT).show();
                                        System.out.println("Exception is " + e);
                                        btnUpdate.setEnabled(true);
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

}