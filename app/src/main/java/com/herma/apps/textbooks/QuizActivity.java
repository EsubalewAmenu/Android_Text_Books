package com.herma.apps.textbooks;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.herma.apps.textbooks.common.questions.QuizRadioBoxesFragment;
import com.herma.apps.textbooks.common.questions.ViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity
{
    final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private TextView questionPositionTV;
    private String totalQuestions = "1";
    private ViewPager questionsViewPager;
    public String[][] questionsWithAnswer;
    public String[] answerKey, response, responseShouldBe, questions, queId;
    public String packege;

    long startTime = 0L;
    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    public int mins, secs;
    TextView timerValue;

    public boolean show_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);


        toolBarInit();

            parsingData();

    }
    private void toolBarInit()
    {
        Toolbar questionToolbar = findViewById(R.id.questionToolbar);
        questionToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        questionToolbar.setNavigationOnClickListener(v -> onBackPressed());

        questionPositionTV = questionToolbar.findViewById(R.id.questionPositionTV);

        //questionToolbarTitle.setText("Questions");
    }

    /*This method decides how many Question-Screen(s) will be created and
    what kind of (Multiple/Single choices) each Screen will be.*/
    private void parsingData() {

        if (getIntent().getExtras() != null) {

            String que = getIntent().getStringExtra("que");


            try {
                JSONArray datas = new JSONArray(que);

                questionsWithAnswer = new String[datas.length()][11];
System.out.println("c.getString(\"answer_options\") is " );

                JSONObject c;
                for (int i = 0; i < datas.length(); i++) {

                    c = datas.getJSONObject(i);
                    questionsWithAnswer[i][0] = c.getString("id");
                    questionsWithAnswer[i][1] = c.getString("question");

                    questionsWithAnswer[i][2] = c.getJSONArray("answer_options").get(0).toString(); //c.getString("ans_a");
                    questionsWithAnswer[i][3] = c.getJSONArray("answer_options").get(1).toString(); //c.getString("ans_b");
                    questionsWithAnswer[i][4] = c.getJSONArray("answer_options").get(2).toString(); //c.getString("ans_c");
                    questionsWithAnswer[i][5] = c.getJSONArray("answer_options").get(3).toString(); //c.getString("ans_d");
                    questionsWithAnswer[i][6] = ""; //c.getString("ans_e");
                    questionsWithAnswer[i][7] = ""; //c.getString("ans_f");
                    questionsWithAnswer[i][8] = c.getJSONArray("correct_answer").get(0).toString(); //c.getString("correct_answer");
                    questionsWithAnswer[i][9] = c.getString("answer_description");
                    questionsWithAnswer[i][10] = c.getString("randomize_options");
                }


        queId = new String[questionsWithAnswer.length];
        answerKey = new String[questionsWithAnswer.length];
        response = new String[questionsWithAnswer.length];
        responseShouldBe  = new String[questionsWithAnswer.length];
        questions = new String[questionsWithAnswer.length];

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

//            if (questionsWithAnswer[i][6].equals("R"))
//            {
                QuizRadioBoxesFragment radioBoxesFragment = new QuizRadioBoxesFragment();
                Bundle radioButtonBundle = new Bundle();
                radioButtonBundle.putStringArray("question", questionsWithAnswer[i]);
                radioButtonBundle.putInt("page_position", i);
                radioBoxesFragment.setArguments(radioButtonBundle);
                fragmentArrayList.add(radioBoxesFragment);
//            }
        }

        questionsViewPager = findViewById(R.id.pager);
        questionsViewPager.setOffscreenPageLimit(1);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        questionsViewPager.setAdapter(mPagerAdapter);


            timerValue = (TextView) findViewById(R.id.timerValue);


            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);

    }
    }

//    public void nextQuestion()
//    {
//        int item = questionsViewPager.getCurrentItem() + 1;
//        questionsViewPager.setCurrentItem(item);
//
//        String currentQuestionPosition = String.valueOf(item + 1);
//
//        String questionPosition = currentQuestionPosition + "/" + totalQuestions;
//        setTextWithSpan(questionPosition);
//    }

//    public int getTotalQuestionsSize()
//    {
//        return questionsWithAnswer.length;
//    }


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
//            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs)
//                    + ":"+ String.format("%03d", milliseconds)
            );
            customHandler.postDelayed(this, 0);
        }

    };

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
}