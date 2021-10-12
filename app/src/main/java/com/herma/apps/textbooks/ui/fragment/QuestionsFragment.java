package com.herma.apps.textbooks.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
//import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.herma.apps.textbooks.AnswersActivity;
import com.herma.apps.textbooks.ChaptersActivity;
import com.herma.apps.textbooks.MainActivity;
import com.herma.apps.textbooks.QuestionActivity;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.SplashActivity;
import com.herma.apps.textbooks.common.Item;
//import com.herma.apps.textbooks.adapter.Common;
//import com.herma.apps.textbooks.questions.AnswersActivity;
//import com.herma.apps.textbooks.questions.DB;
//import com.herma.apps.textbooks.questions.QuestionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * This fragment provide the RadioButton/Single Options.
 */
public class QuestionsFragment extends Fragment {
    private static final int QUESTIONNAIRE_REQUEST = 2018;
    Button resultButton, openNotesBtn, btnQuetionRetry;
    public String[] answerKey, response, responseShouldBe, current_questions, queId;
    public String timer, packege;
    public String[][] questionsWithAnswer;
    TextView txtScore, doneQuestions, percentAnsQue, textView2;
    ImageView imgBadge;
    ProgressBar unseenProgressBar;
    private FragmentActivity mContext;

    EditText etOutOf;
    CheckBox show_answer;
    Spinner spGrade, spSubject, spChapter;
    HashMap<String, String> gradeMap, chapMap;//, schapMap;

    LinearLayout linearLayoutGrade, linearLayoutSubject, linearLayoutUnit, linearLayoutOutOf,
            linearLayoutsGrade, linearLayoutsSubject;//, linearLayoutsUnit;
    Spinner sspGrade, sspSubject;//, sspChapter;

    String ssChaps = "", en = "";


    private boolean screenVisible = false;

//    int countAll = 1, unseen = 10;

    WebView youtubeWebView;
    String url = new SplashActivity().BASEAPI + "ds_questions/v1/questions/";
//    String url = "https://datascienceplc.com/apps/manager/api/items/get_for_books?what=q&no_of_q=";

    public RequestQueue queue;

    Button questionnaireButton;

    public QuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_question_home, container, false);

//        new SplashActivity().handleSSLHandshake();

        isHasTobeUpdated();

        questionnaireButton = rootView.findViewById(R.id.questionnaireButton);
        btnQuetionRetry = rootView.findViewById(R.id.btnQuetionRetry);

        openNotesBtn = rootView.findViewById(R.id.shortnoteButton);

        resultButton = rootView.findViewById(R.id.resultButton);

        textView2 = (TextView) rootView.findViewById(R.id.textView2);


        txtScore = (TextView) rootView.findViewById(R.id.txtScore);
        doneQuestions = (TextView) rootView.findViewById(R.id.doneQuestions);
//        percentAnsQue = (TextView) rootView.findViewById(R.id.percentAnsQue);
        imgBadge = (ImageView) rootView.findViewById(R.id.imgBadge);

        youtubeWebView = (WebView) rootView.findViewById(R.id.youtube_web_view);

        unseenProgressBar = (ProgressBar) rootView.findViewById(R.id.unseenProgressBar); // initiate the progress bar
        unseenProgressBar.setMax(100); // 100 maximum value for the progress bar


        etOutOf = (EditText) rootView.findViewById(R.id.etOutOf);
        show_answer = (CheckBox) rootView.findViewById(R.id.show_answer);

        spGrade = (Spinner) rootView.findViewById(R.id.spGrade);
        spSubject = (Spinner) rootView.findViewById(R.id.spSubject);
        spChapter = (Spinner) rootView.findViewById(R.id.spChapter);

        linearLayoutGrade = (LinearLayout) rootView.findViewById(R.id.linearLayoutGrade);
        linearLayoutSubject = (LinearLayout) rootView.findViewById(R.id.linearLayoutSubject);
        linearLayoutUnit = (LinearLayout) rootView.findViewById(R.id.linearLayoutUnit);
        linearLayoutOutOf = (LinearLayout) rootView.findViewById(R.id.linearLayoutOutOf);

        ////

        sspGrade = (Spinner) rootView.findViewById(R.id.sspGrade);
        sspSubject = (Spinner) rootView.findViewById(R.id.sspSubject);
//        sspChapter = (Spinner) rootView.findViewById(R.id.sspChapter);

        linearLayoutsGrade = (LinearLayout) rootView.findViewById(R.id.linearLayoutsGrade);
        linearLayoutsSubject = (LinearLayout) rootView.findViewById(R.id.linearLayoutsSubject);
//        linearLayoutsUnit = (LinearLayout) rootView.findViewById(R.id.linearLayoutsUnit);


        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String que_service_sting = pre.getString("que_service", "");

        System.out.println("print head is " + que_service_sting);

        if (que_service_sting.equals("")) {
//            Toast.makeText(getActivity(), "Please connect to internet & restart the app!", Toast.LENGTH_SHORT).show();
            textView2.setText( getString(R.string.app_restart ) );
            btnQuetionRetry.setVisibility(View.VISIBLE);
        }else{
            textView2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse("https://t.me/ethio_textbooks"));
                    startActivity(browserIntent);
                }
            });
        }

        show_answer.setChecked(pre.getBoolean("show_answer", true));
//        pre.edit().putInt("tot_score", (tot_score + score)).apply();
//        pre.edit().putInt("tot_asked", (tot_asked + answerKey.length)).apply();

        questionServices(que_service_sting);
//        shortnoteServices(que_service_sting);


        btnQuetionRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOnline()) {
                    btnQuetionRetry.setEnabled(false);
                    doApiCall();
                }else
                Toast.makeText(getContext(), getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();

            }
        });

        questionnaireButton.setOnClickListener(v -> {
            resultButton.setVisibility(View.GONE);
            txtScore.setVisibility(View.GONE);


            questionnaireButton.setText(getString(R.string.loading) );
            questionnaireButton.setEnabled(false);


            if (!isOnline()) {
                System.out.println("Please check your internet!");
                Toast.makeText(getActivity(), "Please check your internet!", Toast.LENGTH_SHORT).show();


                questionnaireButton.setText(getString(R.string.start_questionnaire));
                questionnaireButton.setEnabled(true);

            } else
                doApiCall(etOutOf.getText().toString() + "/" + chapMap.get(spChapter.getSelectedItem()) + "/" + gradeMap.get(spGrade.getSelectedItem()));
//            doApiCall( etOutOf.getText().toString() + "&chapter=" + chapMap.get(spChapter.getSelectedItem())  + "&grade=" + gradeMap.get(spGrade.getSelectedItem()) );

//            https://localhost:8082/wp/ds/wp-json/ds_questions/v1/questions/20/1/1

            pre.edit().putBoolean("show_answer", show_answer.isChecked()).apply();


        });

        openNotesBtn.setOnClickListener(v -> {
//System.out.println( en + " spSubject.getSelectedItemId() "+ spSubject.getSelectedItem() + spGrade.getSelectedItem());
            Intent chaptersIntent = new Intent(getContext(), ChaptersActivity.class);
            chaptersIntent.putExtra("unitsArrayList", ssChaps);
            chaptersIntent.putExtra("subject", (String) spSubject.getSelectedItem());
            chaptersIntent.putExtra("grade", (String) spGrade.getSelectedItem());
            chaptersIntent.putExtra("p", en);
//            chaptersIntent.putExtra("title", "title");
            getActivity().startActivity(chaptersIntent);

//            Intent questions = new Intent(getActivity(), QuestionActivity.class);
//            questions.putExtra("chap_name", spChapter.getSelectedItem()+"");
//            questions.putExtra("chap_no", chapMap.get(spChapter.getSelectedItem()));
//            questions.putExtra("que", ques);
//            startActivityForResult(questions, QUESTIONNAIRE_REQUEST);

        });

        resultButton.setOnClickListener(v -> {
            Intent questions = new Intent(getActivity(), AnswersActivity.class);

            questions.putExtra("queId", queId);
            questions.putExtra("answerKey", answerKey);
            questions.putExtra("response", response);
            questions.putExtra("responseShouldBe", responseShouldBe);
            questions.putExtra("questions", current_questions);

            startActivity(questions);
        });

//        youtubeEmbeded();

        return rootView;


    }


    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
    public boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void doApiCall(String param) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                queue = Volley.newRequestQueue(getContext());


                StringRequest stringRequest = new StringRequest(Request.Method.POST, url + param,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

//                                System.out.println(response);

                                Intent questions = new Intent(getActivity(), QuestionActivity.class);
                                questions.putExtra("chap_name", spChapter.getSelectedItem() + "");
                                questions.putExtra("chap_no", chapMap.get(spChapter.getSelectedItem()));
                                questions.putExtra("showAnswer", show_answer.isChecked());
                                questions.putExtra("outof", etOutOf.getText().toString());
                                questions.putExtra("que", response);
                                startActivityForResult(questions, QUESTIONNAIRE_REQUEST);

                                questionnaireButton.setText(getString(R.string.start_questionnaire));
                                questionnaireButton.setEnabled(true);

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        questionnaireButton.setText(getString(R.string.start_questionnaire) );
                        questionnaireButton.setEnabled(true);

                        System.out.println("some error! " + error);
                        Toast.makeText(getActivity(), "Please check your internet!", Toast.LENGTH_SHORT).show();
                    }

                }) {
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
    // code request code here
//    String doGetRequestQuestions(String url) {
//        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .addHeader("email", "bloger_api@datascienceplc.com")//public user
//                    .addHeader("password", "public-password")
//                    .addHeader("Authorization", "Basic YmxvZ2VyX2FwaUBkYXRhc2NpZW5jZXBsYy5jb206cHVibGljLXBhc3N3b3Jk")
//                    .build();
//
//            Response response = null;
//
//            response = client.newCall(request).execute();
//
//            if (response.code() == 200) {
//
//                String resp = response.body().string();
//
////                System.out.println("Questions response.body().string() " + resp);
//
//                return resp;
//            } else
//                return "failed";
//
//        } catch (IOException e) {
//
//            System.out.println("Exception on doGetRequest " + e);
//            e.printStackTrace();
//            return "failed";
//        }
//
//    }
//
//    // code request code here
//    void doGetRequestInit(String url) {
//        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .addHeader("email", "bloger_api@datascienceplc.com")//public user
//                    .addHeader("password", "public-password")
//                    .addHeader("Authorization", "Basic YmxvZ2VyX2FwaUBkYXRhc2NpZW5jZXBsYy5jb206cHVibGljLXBhc3N3b3Jk")
//                    .build();
//
//            Response response = null;
//
//            response = client.newCall(request).execute();
//
//            if (response.code() == 200) {
//
//                String resp = response.body().string();
//
////                System.out.println("res response.body().string() " + resp);
//
//                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
////                String que_service_sting = pre.getString("que_service", null);
////        int tot_asked = pre.getInt("tot_asked", 0);
//        pre.edit().putString("que_service", resp ).apply();
////        pre.edit().putInt("tot_asked", (tot_asked + answerKey.length)).apply();
//
//
////                questionServices(resp);
////                shortnoteServices(resp);
//
//            }
//
//        } catch (IOException e) {
//
//            System.out.println("Exception on doGetRequest " + e);
//            e.printStackTrace();
//        }
//
//    }

    public void shortnoteServices(String shortnote_services) {

        try {
            // Getting JSON Array node
            JSONObject jsonObj = new JSONObject(shortnote_services);

            JSONArray datas = jsonObj.getJSONArray("short_services");
//            stringsGrades = new String[datas.length()];


            HashMap<String, String> gradeMap = new LinkedHashMap<>();
            linearLayoutsGrade.setVisibility(View.VISIBLE);


            for (int i = 0; i < datas.length(); i++) {

                JSONObject c = datas.getJSONObject(i);


//                    stringsGrades[i] = " Grade "+c.getString("grade");

//                gradeMap.put(i+"", c.getString("subject"));
                gradeMap.put(" Grade " + c.getString("grade"), c.getString("grade"));
//                System.out.println(c.getString("grade")+ " Grade " + c.getString("grade"));

            }

            ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, gradeMap.keySet().toArray());
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sspGrade.setAdapter(aa);
            sspGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        JSONObject jsonObj = new JSONObject(shortnote_services);

                        JSONArray datas = jsonObj.getJSONArray("short_services");
//            stringsGrades = new String[datas.length()];
                        linearLayoutsSubject.setVisibility(View.VISIBLE);
                        HashMap<String, String> subjectMap = new LinkedHashMap<>();
                        JSONObject c;
                        for (int i = 0; i < datas.length(); i++) {

                            c = datas.getJSONObject(i);

                            if ((" Grade " + c.getString("grade")).equals(sspGrade.getSelectedItem().toString())) {
                                subjectMap.put(c.getString("subject"), c.getString("chap"));
                                en = c.getString("en");
//                                System.out.println("en is :" + en);
                            }

                        }


                        ArrayAdapter chapArray = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, subjectMap.keySet().toArray());
                        chapArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sspSubject.setAdapter(chapArray);
                        sspSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                try {

                                    ssChaps = "{\"chap\":" + subjectMap.get(sspSubject.getSelectedItem()) + "}";

//                                    JSONObject jsonObj = new JSONObject("{\"chap\":" + subjectMap.get(sspSubject.getSelectedItem()) + "}");

//                                    JSONArray datas = jsonObj.getJSONArray("chap");
////            stringsGrades = new String[datas.length()];
////                                    linearLayoutsUnit.setVisibility(View.VISIBLE);
//                                    schapMap = new LinkedHashMap<>();
//                                    JSONObject c;
//
//                                    unitsArrayList = new ArrayList<>();
//
//                                    for (int i = 0; i < datas.length(); i++) {
//
//                                        c = datas.getJSONObject(i);
//                                        schapMap.put(("Unit " + c.getString("chaptername")), c.getString("chaptername"));
//                                        System.out.println(("Unit " + c.getString("chaptername"))+ c.getString("chaptername"));
//
//                                        unitsArrayList.add(new Item(c.getString("chaptername"), c.getString("file_url"), "en", R.drawable.icon, "#000000"));
//                                    }

//                                    ArrayAdapter chapArray = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, schapMap.keySet().toArray());
//                                    chapArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                    sspChapter.setAdapter(chapArray);


                                } catch (Exception kl) {
                                    System.out.println("some exception on chap" + kl);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } catch (Exception lk) {
                        System.out.println("some exception on grade");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (final Exception e) {
            System.out.println("some exception on main short note" + e);
        }
    }

    public void questionServices(String que_service) {

        try {
            // Getting JSON Array node
            JSONObject jsonObj = new JSONObject(que_service);
//System.out.println("response array is " + que_service );
            JSONArray datas = jsonObj.getJSONArray("que_service");
//            stringsGrades = new String[datas.length()];

            gradeMap = new LinkedHashMap<>();
            linearLayoutGrade.setVisibility(View.VISIBLE);
            questionnaireButton.setVisibility(View.VISIBLE);
            show_answer.setVisibility(View.VISIBLE);

            for (int i = 0; i < datas.length(); i++) {

                JSONObject c = datas.getJSONObject(i);


//                    stringsGrades[i] = " Grade "+c.getString("grade");

//                gradeMap.put(i+"", c.getString("subject"));
                gradeMap.put(" Grade " + c.getString("grade"), c.getString("id"));

            }
            ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, gradeMap.keySet().toArray());
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spGrade.setAdapter(aa);
            spGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        JSONObject jsonObj = new JSONObject(que_service);

                        JSONArray datas = jsonObj.getJSONArray("que_service");
//            stringsGrades = new String[datas.length()];
                        linearLayoutSubject.setVisibility(View.VISIBLE);
                        HashMap<String, String> subjectMap = new LinkedHashMap<>();
                        JSONObject c;
                        for (int i = 0; i < datas.length(); i++) {

                            c = datas.getJSONObject(i);

                            if ((" Grade " + c.getString("grade")).equals(spGrade.getSelectedItem().toString()))
                                subjectMap.put(c.getString("subject"), c.getString("chap"));

                        }

                        ArrayAdapter chapArray = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, subjectMap.keySet().toArray());
                        chapArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubject.setAdapter(chapArray);
                        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                try {
                                    System.out.println("{\"chap\":" + subjectMap.get(spSubject.getSelectedItem()) + "}");
                                    JSONObject jsonObj = new JSONObject("{\"chap\":" + subjectMap.get(spSubject.getSelectedItem()) + "}");


                                    JSONArray datas = jsonObj.getJSONArray("chap");
//            stringsGrades = new String[datas.length()];
                                    linearLayoutUnit.setVisibility(View.VISIBLE);
                                    linearLayoutOutOf.setVisibility(View.VISIBLE);
                                    chapMap = new LinkedHashMap<>();
                                    JSONObject c;
                                    for (int i = 0; i < datas.length(); i++) {

                                        c = datas.getJSONObject(i);
                                        chapMap.put(("Unit " + c.getString("chapter")), c.getString("chapter"));
                                    }

                                    ArrayAdapter chapArray = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, chapMap.keySet().toArray());
                                    chapArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spChapter.setAdapter(chapArray);


                                } catch (Exception kl) {
                                    System.out.println("some exception on chap" + kl);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } catch (Exception lk) {
                        System.out.println("some exception on grade");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (Exception e) {
        }
    }

    public void examResult() {

        int score = 0;

        if (answerKey.length > 0) {
            for (int i = 0; i < answerKey.length; i++) {
//                System.out.println("answerKey:"+answerKey[i]+"***" + response[i]+" should be " + responseShouldBe[i]);
//                System.out.println(current_questions[i]);
//                if (answerKey[i].equals("***" + response[i]))
                if (responseShouldBe[i].equals(response[i]))
                    score++;
            }
        }

        int perc = (100 * score) / answerKey.length;

//        String rank;
        if (perc >= 74) {
//            rank = "አልፈዋል!";
            txtScore.setBackgroundColor(Color.GREEN);

        } else {
//            rank = "አላለፉም!";
            txtScore.setBackgroundColor(Color.RED);
            txtScore.setTextColor(Color.WHITE);
        }

//        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        int tot_score = pre.getInt("tot_score", 0);
//        int tot_asked = pre.getInt("tot_asked", 0);
//        pre.edit().putInt("tot_score", (tot_score + score)).apply();
//        pre.edit().putInt("tot_asked", (tot_asked + answerKey.length)).apply();

//        int totPerc = (100 * (score + tot_score)) / (answerKey.length + tot_asked);


//        percentAnsQue.setText("እስካሁን ከተጠየቁት የመለሱት ፡ " + totPerc + "%");

        resultButton.setVisibility(View.VISIBLE);
        txtScore.setVisibility(View.VISIBLE);

        txtScore.setText("ውጤት : " + score + "/" + answerKey.length + " (" + perc + "%) \nየፈጀብዎት ጊዜ :- " + timer);
//        txtScore.setText("ውጤት : " + score + "/" + answerKey.length + " (" + perc + "%) " + rank + "\nየፈጀብዎት ጊዜ :- " + timer);
//        if(perc <74)
//            imgBadge.setImageResource(R.drawable.badge_null);
//        else if(perc < 77)
//            imgBadge.setImageResource(R.drawable.badge_null);
//        else if(perc < 85)
//            imgBadge.setImageResource(R.drawable.badge_star);
//        else if(perc < 90)
//            imgBadge.setImageResource(R.drawable.badge_first);
//        else if(perc < 95)
//            imgBadge.setImageResource(R.drawable.badge_gold);
//        else if(perc <= 100)
//            imgBadge.setImageResource(R.drawable.badge_platinum);

//        open("read", "full.hrm");
//
//        Cursor c = db.doExcute("SELECT COUNT(*) FROM que;");
//        if(c.moveToFirst()) countAll = c.getInt(0);
//
//        Cursor cc = db.doExcute("SELECT COUNT(*) FROM que where seen=0;");
//        if(cc.moveToFirst()) unseen = cc.getInt(0);
//        db.close();
//        int seenPer = 100-((unseen*100)/countAll);
//        unseenProgressBar.setProgress(seenPer); // 50 default progress value for the progress bar
//        doneQuestions.setText("(ከጠቅላላው) እስካሁን የሰሩት ፡ " + seenPer + "%");


//        Toast.makeText(getActivity(), "Completed!!", Toast.LENGTH_LONG).show();

    }

    /*This method get called only when the fragment get visible, and here states of Radio Button(s) retained*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            screenVisible = true;

        }
    }

    private String getTheStateOfRadioBox(String[] data) {
        return "";//appDatabase.getQuestionChoicesDao().isChecked(data[0], data[1]);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mContext = getActivity();
        if (getArguments() != null) {
//        }
        }
    }

//    public void open(String write, String db_name) {
//
//        db = new DB(getActivity(), db_name);
//        try {
//            if (write.equals("write"))
//                db.writeDataBase();
//            else
//                db.createDataBase();
//        } catch (IOException ioe) {
//            throw new Error("Unable to create database");
//        }
//        try {
//            db.openDataBase();
//        } catch (SQLException sqle) {
//            throw sqle;
//        }
//    }

    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
//    private boolean isOnline() {
//        // Get a reference to the ConnectivityManager to check the state of network connectivity
//        ConnectivityManager connectivityManager = (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        // Get details on the currently active default data network
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.isConnected();
//        return true;
//    }

//    private void showNetworkDialog(final boolean isOnline, int title, int message, String packege) {
//
//        // Create an AlertDialog.Builder
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog_Alert);
//        // Set an Icon and title, and message
//
//        if (!isOnline) {
//            builder.setIcon(R.drawable.ic_warning);
//        }
//        builder.setTitle(getString(title));
//        builder.setMessage(getString(message));
//        builder.setPositiveButton(getString(R.string.go_to_exam_question), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent questions = new Intent(getActivity(), QuestionActivity.class);
//                questions.putExtra("type", "rand");
//                questions.putExtra("packege", packege);
//                startActivityForResult(questions, QUESTIONNAIRE_REQUEST);
//            }
//        });
//        builder.setNegativeButton(getString(R.string.cancel), null);
//
//        // Create and show the AlertDialog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//
//    }


//    private void showAwardDialog(int title, String rank, int message) {
//
//        // Create an AlertDialog.Builder
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog_Alert);
//        // Set an Icon and title, and message
//
//        builder.setTitle(getString(title));
//        builder.setMessage(rank+"\n"+getString(message));
//
//        // Create and show the AlertDialog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//
//    }
//    public void youtubeEmbededPlay(String url, String play_open) {
//
//
//        if (play_open.equalsIgnoreCase("p")) {
//            youtubeWebView.setVisibility(View.VISIBLE);
//            youtubeWebView.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    return false;
//                }
//            });
//            WebSettings webSettings = youtubeWebView.getSettings();
//            webSettings.setJavaScriptEnabled(true);
//            webSettings.setLoadWithOverviewMode(true);
//            webSettings.setUseWideViewPort(true);
//            youtubeWebView.loadUrl(url);
//        } else {
//            youtubeWebView.setVisibility(View.VISIBLE);
//            youtubeWebView.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    return false;
//                }
//            });
//            WebSettings webSettings = youtubeWebView.getSettings();
//            webSettings.setJavaScriptEnabled(true);
//            webSettings.setLoadWithOverviewMode(true);
//            webSettings.setUseWideViewPort(true);
//            youtubeWebView.loadUrl(url);
//
////            Uri uriUrl = Uri.parse(url);
////            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
////            startActivity(launchBrowser);
//
//        }
//    }
    private void doApiCall() {
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {

        String url_subjects = new SplashActivity().BASEAPI + "ds_questions/v1/available/subjects";

        queue = Volley.newRequestQueue(getContext());


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_subjects,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String resp = response;
                        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
                        pre.edit().putString("que_service", resp).apply();

                        pre.edit().putString("updated_at", (new Date()).toString()).apply();

                        btnQuetionRetry.setVisibility(View.GONE);

                        questionServices(resp);
                        textView2.setText( getString(R.string.questions_intro ) );

                        System.out.println(" (new Date()).toString() updated_at" + (new Date()).toString());

//                                System.out.println("main resp is " + resp);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                Toast.makeText(getContext(), getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
                btnQuetionRetry.setEnabled(true);

//                System.out.println("main resp is error " + error);

            }

        }) {
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
//            }
//        }, 1500);
    }

    private void isHasTobeUpdated() {

        try

    {
        Date now = new Date();

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
        Date updated_at = new Date(pre.getString("updated_at", "Oct 05 06:33:29 GMT+03:00 2020"));

        long diff = now.getTime() - updated_at.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        System.out.println(" (new Date()).toString() updated_at" + diffDays);


        if (diffDays > 7 || diffDays < 7) // not less than or grater than
            doApiCall();

    }catch(
    Exception df)

    {
        doApiCall();
    }
}

}