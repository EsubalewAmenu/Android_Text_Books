package com.herma.apps.textbooks.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
//import androidx.preference.PreferenceManager;

import com.herma.apps.textbooks.AnswersActivity;
import com.herma.apps.textbooks.QuestionActivity;
import com.herma.apps.textbooks.R;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This fragment provide the RadioButton/Single Options.
 */
public class QuestionsFragment extends Fragment {
    private static final int QUESTIONNAIRE_REQUEST = 2018;
    Button resultButton;
    public String[] answerKey, response, responseShouldBe, current_questions, queId;
    public String timer, packege;
    public String[][] questionsWithAnswer;
    TextView txtScore, doneQuestions, percentAnsQue;
    ImageView imgBadge;
    ProgressBar unseenProgressBar;
    private FragmentActivity mContext;

    EditText etOutOf;
    Spinner spGrade, spSubject, spChapter;
    HashMap<String, String> chapMap;

    private boolean screenVisible = false;

//    int countAll = 1, unseen = 10;

    WebView youtubeWebView;

    public QuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_question_home, container, false);

        Button questionnaireButton = rootView.findViewById(R.id.questionnaireButton);
        resultButton = rootView.findViewById(R.id.resultButton);
        txtScore = (TextView) rootView.findViewById(R.id.txtScore);
        doneQuestions = (TextView) rootView.findViewById(R.id.doneQuestions);
        percentAnsQue = (TextView) rootView.findViewById(R.id.percentAnsQue);
        imgBadge = (ImageView) rootView.findViewById(R.id.imgBadge);

        youtubeWebView = (WebView) rootView.findViewById(R.id.youtube_web_view);

        unseenProgressBar = (ProgressBar) rootView.findViewById(R.id.unseenProgressBar); // initiate the progress bar
        unseenProgressBar.setMax(100); // 100 maximum value for the progress bar


        etOutOf = (EditText) rootView.findViewById(R.id.etOutOf);

        spGrade = (Spinner) rootView.findViewById(R.id.spGrade);
        spSubject = (Spinner) rootView.findViewById(R.id.spSubject);
        spChapter = (Spinner) rootView.findViewById(R.id.spChapter);


        services();

//        open("read", "full.hrm");
//
//
//        try{ Cursor c = db.doExcute("SELECT COUNT(*) FROM que;");
//        if(c.moveToFirst()) countAll = c.getInt(0); }catch (Exception lk){countAll = 0;}
//
//        try{Cursor cc = db.doExcute("SELECT COUNT(*) FROM que where seen=0;");
//        if(cc.moveToFirst()) unseen = cc.getInt(0);}catch (Exception lk){unseen = countAll;}
//db.close();
//    int seenPer = 100-((unseen*100)/countAll);
//
//        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        int tot_score = pre.getInt("tot_score", 0);
//        int tot_asked = pre.getInt("tot_asked", 0);
//
//        int totPerc = 0;
//        try{ totPerc = (100*tot_score)/tot_asked; }catch (Exception lk){}

//        unseenProgressBar.setProgress(seenPer);
//        doneQuestions.setText("(ከጠቅላላው) እስካሁን የሰሩት ፡ " + seenPer + "%");
//        percentAnsQue.setText("እስካሁን ከተጠየቁት የመለሱት ፡ " + totPerc + "%");

//        getActivity().setTitle(R.string.exam_questions);

        questionnaireButton.setOnClickListener(v -> {
            resultButton.setVisibility(View.GONE);
            txtScore.setVisibility(View.GONE);

//            System.out.println("spChapter.getSelectedItem()" + spChapter.getSelectedItem());
//            System.out.println("spChapter.getSelectedItem()" + chapMap.get(spChapter.getSelectedItem()));

//            https://datascienceplc.com/apps/manager/api/items/get_for_books?what=q&no_of_q=1&chapter=1

            String ques = "{\"success\":true,\"error\":false,\"ques\":[{\"id\":\"4567\",\"question\":\"sample question\",\"a\":\"choose a\",\"b\":\"choose b\",\"c\":\"choose c\",\"d\":\"choose d\",\"e\":null,\"f\":null,\"ans\":\"A\",\"desc\":\"desc\"},{\"id\":\"4568\",\"question\":\"12que\",\"a\":\"a\",\"b\":\" ds\",\"c\":\"sdf\",\"d\":\"asdf\",\"e\":\"asdf\",\"f\":\"sdf\",\"ans\":\"C\",\"desc\":null}]}";

            Intent questions = new Intent(getActivity(), QuestionActivity.class);
            questions.putExtra("chap_name", spChapter.getSelectedItem()+"");
            questions.putExtra("chap_no", chapMap.get(spChapter.getSelectedItem()));
            questions.putExtra("que", ques);
            startActivityForResult(questions, QUESTIONNAIRE_REQUEST);

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

    public void services() {
        //        https://datascienceplc.com/apps/manager/api/items/get_for_books?what=init

        String que_service = "{\"success\":true,\"error\":false,\"que_service\":[{\"id\":\"1\",\"grade\":\"12\",\"subject\":\"Biology\",\"chapter_name\":\"Unit\",\"chap\":[{\"id\":\"4567\",\"chapter\":\"1\"}]},{\"id\":\"2\",\"grade\":\"12\",\"subject\":\"Physics\",\"chapter_name\":\"Unit\",\"chap\":[]}]}";
//        String[] stringsGrades;// = { "Grade 12", "Grade 11", "Grade 10"};
//        String[] stringsSubjecs;// = { "Bio", "Phy", "Geo"};

        try {
            // Getting JSON Array node
            JSONObject jsonObj = new JSONObject(que_service);

            JSONArray datas = jsonObj.getJSONArray("que_service");
//            stringsGrades = new String[datas.length()];

            HashMap<String, String> gradeMap = new LinkedHashMap<>();

            for (int i = 0; i < datas.length(); i++) {

                JSONObject c = datas.getJSONObject(i);


//                    stringsGrades[i] = " Grade "+c.getString("grade");

//                gradeMap.put(i+"", c.getString("subject"));
                gradeMap.put(c.getString("grade"), " Grade " + c.getString("grade"));

            }

//            if(datas.length() == 0 ){
//                verif_customer_rewards = new String[1];
//                verif_customer_rewards[0] = "No customer to pay";
//
//            } else btnorder_reward.setVisibility(View.VISIBLE);

//            ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
//                    R.layout.activity_listview, verif_customer_rewards);
//
//            listView.setAdapter(adapter);


            ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, gradeMap.values().toArray());
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spGrade.setAdapter(aa);
            spGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        JSONObject jsonObj = new JSONObject(que_service);

                        JSONArray datas = jsonObj.getJSONArray("que_service");
//            stringsGrades = new String[datas.length()];

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

                                    JSONObject jsonObj = new JSONObject("{\"chap\":" + subjectMap.get(spSubject.getSelectedItem()) + "}");


                                    JSONArray datas = jsonObj.getJSONArray("chap");
//            stringsGrades = new String[datas.length()];

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

        } catch (final JSONException e) {
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
    public void youtubeEmbededPlay(String url, String play_open) {


        if (play_open.equalsIgnoreCase("p")) {
            youtubeWebView.setVisibility(View.VISIBLE);
            youtubeWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
            WebSettings webSettings = youtubeWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            youtubeWebView.loadUrl(url);
        } else {
            youtubeWebView.setVisibility(View.VISIBLE);
            youtubeWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
            WebSettings webSettings = youtubeWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            youtubeWebView.loadUrl(url);

//            Uri uriUrl = Uri.parse(url);
//            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
//            startActivity(launchBrowser);

        }
    }
}