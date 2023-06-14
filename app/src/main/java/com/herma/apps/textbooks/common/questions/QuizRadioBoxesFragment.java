package com.herma.apps.textbooks.common.questions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.herma.apps.textbooks.QuizActivity;
import com.herma.apps.textbooks.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * This fragment provide the RadioButton/Single Options.
 */
public class QuizRadioBoxesFragment extends Fragment
{
    private final ArrayList<RadioButton> radioButtonArrayList = new ArrayList<>();
    private boolean screenVisible = false;
    private String[] radioButtonTypeQuestion;
    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    private Button previousButton;
    private TextView questionRBTypeTextView;
    private RadioGroup radioGroupForChoices;
    private boolean atLeastOneChecked = false;
    private String questionId = "";
    private int currentPagePosition = 0;
    private int clickedRadioButtonPosition = 0;
    private String qState = "0";

    List<String> choices;

    public QuizRadioBoxesFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_radio_boxes, container, false);

        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        previousButton = rootView.findViewById(R.id.previousButton);
        questionRBTypeTextView = rootView.findViewById(R.id.questionRBTypeTextView);
        radioGroupForChoices = rootView.findViewById(R.id.radioGroupForChoices);

        nextOrFinishButton.setOnClickListener(v -> {

//            setSeen(questionId);

            if (currentPagePosition == ((QuizActivity) mContext).getTotalQuestionsSize())
            {
                /* Here, You go back from where you started OR If you want to go next Activity just change the Intent*/
                Intent returnIntent = new Intent();



                returnIntent.putExtra("timer", "" + ((QuizActivity)mContext).mins + ":"
                        + String.format("%02d", ((QuizActivity)mContext).secs));

                returnIntent.putExtra("answerKey", ((QuizActivity) mContext).answerKey);
//                returnIntent.putExtra("queId", ((QuizActivity) mContext).queId);
                returnIntent.putExtra("response", ((QuizActivity) mContext).response);
                returnIntent.putExtra("responseShouldBe", ((QuizActivity) mContext).responseShouldBe);
//                returnIntent.putExtra("questions", ((QuizActivity) mContext).questions);
//                returnIntent.putExtra("packege", ((QuizActivity) mContext).packege);
//                returnIntent.putExtra("questionsWithAnswer", ((QuizActivity) mContext).questionsWithAnswer);

                mContext.setResult(Activity.RESULT_OK, returnIntent);
                mContext.finish();

            } else
            {
                ((QuizActivity) mContext).nextQuestion();
            }
        });
        previousButton.setOnClickListener(view -> mContext.onBackPressed());

        return rootView;
    }
//    public Drawable getDrawable(String source) {
//        LevelListDrawable d = new LevelListDrawable();
//        Drawable empty = getResources().getDrawable(R.drawable.icon);
//        d.addLevel(0, 0, empty);
//        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
//
//        source = source.substring(2, source.length()-2);
////        System.out.println("source is " + source );
////        source.replace("localhost","datascienceplc.com");
////        System.out.println("source is " + source );
//        new LoadImage().execute(source, d);
//
//        return d;
//    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;
        TextView tv;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            tv = (TextView) params[2];
            Log.d("print from", "doInBackground " + source);
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d("Print from post execute", "onPostExecute drawable " + mDrawable);
//            Log.d(TAG, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = tv.getText();
                tv.setText(t);
            }
        }
    }
    /*This method get called only when the fragment get visible, and here states of Radio Button(s) retained*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {
            screenVisible = true;
            for (int i = 0; i < radioButtonArrayList.size(); i++)
            {
                RadioButton radioButton = radioButtonArrayList.get(i);
                String cbPosition = String.valueOf(i);

                String[] data = new String[]{questionId, cbPosition};
                Observable.just(data)
                        .map(this::getTheStateOfRadioBox)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>()
                        {
                            @Override
                            public void onSubscribe(Disposable d)
                            {

                            }

                            @Override
                            public void onNext(String s)
                            {
                                qState = s;
                            }

                            @Override
                            public void onError(Throwable e)
                            {

                            }

                            @Override
                            public void onComplete()
                            {
                                if (qState.equals("1"))
                                {
                                    radioButton.setChecked(true);
                                } else
                                {
                                    radioButton.setChecked(false);
                                }
                            }
                        });
            }
        }
    }

    private String getTheStateOfRadioBox(String[] data)
    {
        return "";//appDatabase.getQuestionChoicesDao().isChecked(data[0], data[1]);
    }

    private void saveActionsOfRadioBox()
    {

        for (int i = 0; i < radioButtonArrayList.size(); i++)
        {
            if (i == clickedRadioButtonPosition)
            {
                RadioButton radioButton = radioButtonArrayList.get(i);
                if (radioButton.isChecked())
                {

                    radioButton.setBackgroundColor(Color.GREEN);

                    atLeastOneChecked = true;

                    String cbPosition = String.valueOf(radioButtonArrayList.indexOf(radioButton));

                    String[] data = new String[]{"1", questionId, cbPosition};
                    insertChoiceInDatabase(data);

//                    System.out.println("Current Question = "+currentPagePosition);

                    ((QuizActivity)mContext).response[(currentPagePosition)-1] = cbPosition;//choices.get(Integer.parseInt(cbPosition));

                    if(true){//((QuizActivity) mContext).show_answer) {
                        if(Integer.parseInt(cbPosition) == Integer.parseInt(radioButtonTypeQuestion[8]))
                            Toast.makeText(mContext, "ትክክል!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mContext, "ስህተት!", Toast.LENGTH_SHORT).show();
                    }

                    ((QuizActivity)mContext).responseShouldBe[(currentPagePosition)-1] = radioButtonTypeQuestion[8];//choices.get(0);



                } else
                {
                    String cbPosition = String.valueOf(radioButtonArrayList.indexOf(radioButton));

                    String[] data = new String[]{"0", questionId, cbPosition};
                    insertChoiceInDatabase(data);

                    radioButton.setBackgroundColor(Color.WHITE);
//                    if(i%2==0)
//                        radioButton.setBackgroundColor(Color.GRAY);
//                    else
//                        radioButton.setBackgroundColor(Color.LTGRAY);

                }
            }
        }

        if (atLeastOneChecked)
        {
            nextOrFinishButton.setEnabled(true);


        } else
        {
            nextOrFinishButton.setEnabled(false);
        }
    }

    private void insertChoiceInDatabase(String[] data)
    {
        Observable.just(data)
                .map(this::insertingInDb)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private String insertingInDb(String[] data)
    {
//        appDatabase.getQuestionChoicesDao().updateQuestionWithChoice(data[0], data[1], data[2]);
        return "";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        if (getArguments() != null)
        {
            radioButtonTypeQuestion = getArguments().getStringArray("question");
            questionId = String.valueOf(radioButtonTypeQuestion != null ? radioButtonTypeQuestion[0] : 0);
            currentPagePosition = getArguments().getInt("page_position") + 1;
        }


        questionRBTypeTextView.setText(Html.fromHtml(radioButtonTypeQuestion[1], new Html.ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {

                LevelListDrawable d = new LevelListDrawable();
                Drawable empty = getResources().getDrawable(R.drawable.icon);
                d.addLevel(0, 0, empty);
                d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

                source = source.substring(2, source.length()-2);

                new LoadImage().execute(source, d, questionRBTypeTextView);
                return d;
            }
        }, null));


//        questionRBTypeTextView.setText("text" +radioButtonTypeQuestion[1]);

//        Spanned spanned = Html.fromHtml(radioButtonTypeQuestion[1], this::getDrawable, null);
//        questionRBTypeTextView.setText(spanned);



//        List<AnswerOptions> choices = radioButtonTypeQuestion.getAnswerOptions();
        choices = new ArrayList<String>();

        for(int i=2; i<8; i++)
            if(!radioButtonTypeQuestion[i].equals("null")){//radioButtonTypeQuestion[i]!=null && !radioButtonTypeQuestion[i].equals(null)) {

                ((QuizActivity) mContext).questions[getArguments().getInt("page_position")] = radioButtonTypeQuestion[1];

//                System.out.println("radioButtonTypeQuestion[i] choices is" + radioButtonTypeQuestion[i]+"/");
                    choices.add(radioButtonTypeQuestion[i]);
            }
///////////////

//        System.out.println("radioButtonTypeQuestion[i] answer is " + radioButtonTypeQuestion[8]);

            ((QuizActivity) mContext).answerKey[getArguments().getInt("page_position")] = radioButtonTypeQuestion[8];
//            choices.add(radioButtonTypeQuestion[i].substring(3));

        ////////////////////////


        radioButtonArrayList.clear();

//            int ic = 0;
        for (String choice : choices) {
            if( !choice.equals("")){
//            ic++;

            RadioButton rb = new RadioButton(mContext);
//            rb.setText(choice);

            System.out.println("rb.setText(choice); yes" + choice);

            rb.setText(Html.fromHtml(choice, new Html.ImageGetter() {

                @Override
                public Drawable getDrawable(String source) {

                    LevelListDrawable d = new LevelListDrawable();
                    Drawable empty = getResources().getDrawable(R.drawable.icon);
                    d.addLevel(0, 0, empty);
                    d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

                    source = source.substring(2, source.length() - 2);
//        System.out.println("source is " + source );
//        source.replace("localhost","datascienceplc.com");
//        System.out.println("source is " + source );
                    new LoadImage().execute(source, d, rb);
                    return d;
                }
            }, null));

//            if(ic%2==0)
//                rb.setBackgroundColor(Color.GRAY);
//            else
//                rb.setBackgroundColor(Color.LTGRAY);

            rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            rb.setTextColor(ContextCompat.getColor(mContext, R.color.grey));

            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(((QuizActivity) mContext));
            int view_gap;
            try {
                view_gap = Integer.parseInt(pre.getString("view_gap", "anon"));
            } catch (Exception klk) {
                view_gap = 70;
            }

            rb.setPadding(10, view_gap, 10, view_gap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 25;
            rb.setLayoutParams(params);

            View view = new View(mContext);
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.divider));
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

            radioGroupForChoices.addView(rb);
            radioGroupForChoices.addView(view);
            radioButtonArrayList.add(rb);

            rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (screenVisible) {
                    clickedRadioButtonPosition = radioButtonArrayList.indexOf(buttonView);
                    saveActionsOfRadioBox();

                }
            });
        } else System.out.println("rb.setText(choice); no");
        }

        if (atLeastOneChecked)
        {
            nextOrFinishButton.setEnabled(true);
        } else
        {
            nextOrFinishButton.setEnabled(false);
        }

        /* If the current question is last in the questionnaire then
        the "Next" button will change into "Finish" button*/
        if (currentPagePosition == ((QuizActivity) mContext).getTotalQuestionsSize())
        {
            nextOrFinishButton.setText(R.string.finish);
        } else
        {
            nextOrFinishButton.setText(R.string.next);
        }
    }

}