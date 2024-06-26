package com.herma.apps.textbooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.MobileAds;
import com.herma.apps.textbooks.common.Commons;
import com.herma.apps.textbooks.common.questions.RadioBoxesFragment;
import com.herma.apps.textbooks.common.questions.ViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
public class QuestionActivity extends AppCompatActivity
{
    final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    //private TextView questionToolbarTitle;
    private TextView questionPositionTV;
    private String totalQuestions = "1";
    private ViewPager questionsViewPager;
//    DB db;
    public String[][] questionsWithAnswer;
    public String[] answerKey, response, responseShouldBe, questions, queId;

    long startTime = 0L;
    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    public int mins, secs;
    TextView timerValue;

//    int per_exam;

    public boolean show_answer;

    public String packege;
    TextView tvAds;

    private InterstitialAd mInterstitialAd;
    private FrameLayout adContainerView;
    private AdView mAdView;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

//        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(QuestionActivity.this);
//        show_answer = pre.getString("show_answer", "anon");
//try{
//        per_exam = Integer.parseInt(pre.getString("no_of_que", "anon"));
//    }catch (Exception klk) { per_exam = 50; }

        toolBarInit();

//        if (getIntent().getExtras() != null)
//        {
//            Bundle bundle = getIntent().getExtras();
//            parsingData(bundle);
//        }
            parsingData();

            tvAds = (TextView) findViewById(R.id.tvAds);

//            setAd();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        mAdView = findViewById(R.id.adView);
        adContainerView = findViewById(R.id.ad_view_container);


        AdRequest adRequest = new AdRequest.Builder().build();

        if(new Commons(getApplicationContext()).showGoogleAd( 1)) {

//            mAdView.loadAd(adRequest);
            adContainerView.post(new Runnable() {
                @Override
                public void run() {
                    new Commons(getApplicationContext()).loadBanner(mAdView, getString(R.string.adQuestions), adContainerView, getWindowManager().getDefaultDisplay());
                }
            });

            InterstitialAd.load(this,getString(R.string.adQuestionsInt), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                            mInterstitialAd = interstitialAd;
                            mInterstitialAd.show(QuestionActivity.this);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                        }
                    });

        }else{
            adContainerView.setVisibility(View.GONE);
        }
    }
    private void toolBarInit()
    {
        Toolbar questionToolbar = findViewById(R.id.questionToolbar);
        questionToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        questionToolbar.setNavigationOnClickListener(v -> onBackPressed());

        //questionToolbarTitle = questionToolbar.findViewById(R.id.questionToolbarTitle);
        questionPositionTV = questionToolbar.findViewById(R.id.questionPositionTV);

        //questionToolbarTitle.setText("Questions");
    }

    /*This method decides how many Question-Screen(s) will be created and
    what kind of (Multiple/Single choices) each Screen will be.*/
    private void parsingData() {

        if (getIntent().getExtras() != null) {

            int chap_id = getIntent().getIntExtra("chap_id", 0);
            String que = getIntent().getStringExtra("que");
            String chap_name = getIntent().getStringExtra("chap_name");
            show_answer = getIntent().getBooleanExtra("showAnswer", true);


            try {

                JSONObject jsonObj = new JSONObject(que);
                JSONArray datas = jsonObj.getJSONArray("ques");

                questionsWithAnswer = new String[datas.length()][11];

                JSONObject c;
                for (int i = 0; i < datas.length(); i++) {

                    c = datas.getJSONObject(i);
                    questionsWithAnswer[i][0] = c.getString("id");
                    questionsWithAnswer[i][1] = c.getString("question");
                    questionsWithAnswer[i][2] = c.getString("ans_a");
                    questionsWithAnswer[i][3] = c.getString("ans_b");
                    questionsWithAnswer[i][4] = c.getString("ans_c");
                    questionsWithAnswer[i][5] = c.getString("ans_d");
                    questionsWithAnswer[i][6] = c.getString("ans_e");
                    questionsWithAnswer[i][7] = c.getString("ans_f");
                    questionsWithAnswer[i][8] = c.getString("correct_ans");
                    questionsWithAnswer[i][9] = c.getString("details");
                }


//            if(type.equalsIgnoreCase("fixed")){
//
//            questionsWithAnswer = db.getSelectArray("*", "que", "id > " +start+ " and id <=  "+(start+per_exam));
//
//        } else if(type.equalsIgnoreCase("rand")){// ORDER BY RANDOM()  ORDER BY random
//                Cursor c = db.doExcute("SELECT MIN(seen) FROM que");
//                int max = 0;
//                if(c.moveToFirst()) max = c.getInt(0);
//                do{
//        questionsWithAnswer = db.getSelectArray("*", "que",  "seen >= (SELECT MIN(seen) FROM que) and seen <=" + max + " order by random() limit " + per_exam);//and id <=  "+(start+per_exam));
//                    max++;
//                }while(questionsWithAnswer.length!=per_exam);
//
//            }
        queId = new String[questionsWithAnswer.length];
        answerKey = new String[questionsWithAnswer.length];
        response = new String[questionsWithAnswer.length];
        responseShouldBe  = new String[questionsWithAnswer.length];
        questions = new String[questionsWithAnswer.length];
//        db.close();

        totalQuestions = (questionsWithAnswer.length)+"";

        String questionPosition = "1/" + totalQuestions;
        setTextWithSpan(questionPosition);



            } catch (Exception kl) {
                System.out.println("some exception on chap" + kl);
            }

        for (int i = 0; i < (questionsWithAnswer.length); i++)
        {
            queId[i] = questionsWithAnswer[i][0];
//            if (questionsWithAnswer[i][6].equals("CheckBox"))
//            {
//                CheckBoxesFragment checkBoxesFragment = new CheckBoxesFragment();
//                Bundle checkBoxBundle = new Bundle();
//                checkBoxBundle.putInt("page_position", i);
//                checkBoxBundle.putStringArray("question", questionsWithAnswer[i]);
//                checkBoxesFragment.setArguments(checkBoxBundle);
//                fragmentArrayList.add(checkBoxesFragment);
//            }

            if (true)//questionsWithAnswer[i][6].equals("R"))
            {
                RadioBoxesFragment radioBoxesFragment = new RadioBoxesFragment();
                Bundle radioButtonBundle = new Bundle();
                radioButtonBundle.putStringArray("question", questionsWithAnswer[i]);
                radioButtonBundle.putInt("page_position", i);
                radioBoxesFragment.setArguments(radioButtonBundle);
                fragmentArrayList.add(radioBoxesFragment);
            }
        }

        questionsViewPager = findViewById(R.id.pager);
        questionsViewPager.setOffscreenPageLimit(1);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        questionsViewPager.setAdapter(mPagerAdapter);


            timerValue = (TextView) findViewById(R.id.timerValue);


        //timer
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);

    }
    }

    public void nextQuestion()
    {
        int item = questionsViewPager.getCurrentItem() + 1;
        questionsViewPager.setCurrentItem(item);

        String currentQuestionPosition = String.valueOf(item + 1);

        String questionPosition = currentQuestionPosition + "/" + totalQuestions;
        setTextWithSpan(questionPosition);
    }

    public int getTotalQuestionsSize()
    {
        return questionsWithAnswer.length;
    }


    private void setTextWithSpan(String questionPosition)
    {
        int slashPosition = questionPosition.indexOf("/");

        Spannable spanText = new SpannableString(questionPosition);
        spanText.setSpan(new RelativeSizeSpan(0.7f), slashPosition, questionPosition.length(), 0);
        questionPositionTV.setText(spanText);
    }


    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs)
//                    + ":"+ String.format("%03d", milliseconds)
            );
            customHandler.postDelayed(this, 0);
        }

    };
}