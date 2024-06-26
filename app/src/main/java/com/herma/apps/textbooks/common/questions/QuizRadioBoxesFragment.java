package com.herma.apps.textbooks.common.questions;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.herma.apps.textbooks.ui.profile.ProfileActivity;
import com.herma.apps.textbooks.QuizActivity;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TextView questionRBTypeTextView, answerExplanationTextView;
    private RadioGroup radioGroupForChoices;
    private boolean atLeastOneChecked = false;
    private String questionId = "";
    private int currentPagePosition = 0;
    private int clickedRadioButtonPosition = 0;
    private String qState = "0";

    List<String> choices;

    private Button btnLike, btnDislike;//, btnComment;
    private TextView tv_prepared_by;
    Drawable normalLikeDrawable = null, activeLikeDrawable = null, normalDislikeDrawable = null, activeDislikeDrawable = null;

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
        answerExplanationTextView = rootView.findViewById(R.id.answerExplanationTextView);
        radioGroupForChoices = rootView.findViewById(R.id.radioGroupForChoices);

        btnLike = rootView.findViewById(R.id.btn_like);
        btnDislike = rootView.findViewById(R.id.btn_dislike);
//        btnComment = rootView.findViewById(R.id.btn_reply);
        tv_prepared_by = rootView.findViewById(R.id.tv_prepared_by);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            normalLikeDrawable = getResources().getDrawable(R.drawable.ic_like, getContext().getTheme());
            activeLikeDrawable = getResources().getDrawable(R.drawable.ic_like_active, getContext().getTheme());
            normalDislikeDrawable = getResources().getDrawable(R.drawable.ic_dislike, getContext().getTheme());
            activeDislikeDrawable = getResources().getDrawable(R.drawable.ic_dislike_active, getContext().getTheme());
        } else {
            normalLikeDrawable = getResources().getDrawable(R.drawable.ic_like);
            activeLikeDrawable = getResources().getDrawable(R.drawable.ic_like_active);
            normalDislikeDrawable = getResources().getDrawable(R.drawable.ic_dislike);
            activeDislikeDrawable = getResources().getDrawable(R.drawable.ic_dislike_active);

        }

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
                returnIntent.putExtra("questions", ((QuizActivity) mContext).questions);
                returnIntent.putExtra("questionsWithAnswer", ((QuizActivity) mContext).questionsWithAnswer);
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

        Drawable finalActiveLikeDrawable = activeLikeDrawable;
        Drawable finalNormalLikeDrawable = normalLikeDrawable;
        Drawable finalActiveDislikeDrawable = activeDislikeDrawable;
        Drawable finalNormalDislikeDrawable = normalDislikeDrawable;

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnLike.getCompoundDrawables()[0] == finalNormalLikeDrawable) {
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(finalActiveLikeDrawable, null, null, null);
                    btnLike.setText((Integer.parseInt(btnLike.getText().toString()) + 1)+"");


                    if (btnDislike.getCompoundDrawables()[0] == finalActiveDislikeDrawable) {
                        btnDislike.setCompoundDrawablesWithIntrinsicBounds(finalNormalDislikeDrawable, null, null, null);
//                        btnDislike.setText((Integer.parseInt(btnDislike.getText().toString()) - 1)+"");
                    }

                    try {
                        postInteraction(Integer.parseInt(questionId), "L", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(finalNormalLikeDrawable, null, null, null);
                    btnLike.setText((Integer.parseInt(btnLike.getText().toString()) - 1)+"");
                    try {
                        postInteraction(Integer.parseInt(questionId), "L", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        Drawable finalNormalDislikeDrawable1 = normalDislikeDrawable;
        Drawable finalActiveDislikeDrawable1 = activeDislikeDrawable;
        Drawable finalActiveLikeDrawable1 = activeLikeDrawable;
        Drawable finalNormalLikeDrawable1 = normalLikeDrawable;

        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnDislike.getCompoundDrawables()[0] == finalNormalDislikeDrawable1) {
                    btnDislike.setCompoundDrawablesWithIntrinsicBounds(finalActiveDislikeDrawable1, null, null, null);
//                    btnDislike.setText((Integer.parseInt(btnDislike.getText().toString()) + 1)+"");

                    if (btnLike.getCompoundDrawables()[0] == finalActiveLikeDrawable1) {
                        btnLike.setCompoundDrawablesWithIntrinsicBounds(finalNormalLikeDrawable1, null, null, null);
                        btnLike.setText((Integer.parseInt(btnLike.getText().toString()) - 1)+"");
                    }

                    try {
                        postInteraction(Integer.parseInt(questionId), "D", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    btnDislike.setCompoundDrawablesWithIntrinsicBounds(finalNormalDislikeDrawable1, null, null, null);
//                    btnDislike.setText((Integer.parseInt(btnDislike.getText().toString()) - 1)+"");

                    try {
                        postInteraction(Integer.parseInt(questionId), "D", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


//        btnComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent commentIntent = new Intent(getActivity(), CommentActivity.class);
//                commentIntent.putExtra("chapterName", "testChap");//getIntent().getStringExtra("chapterName"));
//                commentIntent.putExtra("subject", "testSubj");//getIntent().getStringExtra("subject"));
//                commentIntent.putExtra("fileName", "testFile");//getIntent().getStringExtra("fileName"));
//                startActivity(commentIntent);
//            }
//        });

        return rootView;
    }

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


                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(tv.getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_zoomable_image);
                        ZoomImageView imageView = dialog.findViewById(R.id.zoomable_image);
                        Glide.with(tv.getContext()).load(bitmap).into(imageView);
                        dialog.show();
                    }
                });
            }
        }
    }
    /*This method get called only when the fragment get visible, and here states of Radio Button(s) retained*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
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
                    atLeastOneChecked = true;

                    String cbPosition = String.valueOf(radioButtonArrayList.indexOf(radioButton));

                    String[] data = new String[]{"1", questionId, cbPosition};
                    insertChoiceInDatabase(data);

//                    System.out.println("Current Question = "+currentPagePosition);

                    ((QuizActivity)mContext).response[(currentPagePosition)-1] = cbPosition;//choices.get(Integer.parseInt(cbPosition));

                    if(true) {//((QuizActivity) mContext).show_answer) {
                        if (Integer.parseInt(cbPosition) == Integer.parseInt(radioButtonTypeQuestion[8])){
                            Toast.makeText(mContext, "ትክክል!", Toast.LENGTH_SHORT).show();
                        radioButton.setBackgroundColor(Color.GREEN);
                    }else {
                            radioButton.setBackgroundColor(Color.RED);
                            Toast.makeText(mContext, "ስህተት!", Toast.LENGTH_SHORT).show();
                        }

                        if(radioButtonTypeQuestion[9] != null && !radioButtonTypeQuestion[9].isEmpty()) {

                            answerExplanationTextView.setText(Html.fromHtml("Explanation: \n"+radioButtonTypeQuestion[9], new Html.ImageGetter() {

                                @Override
                                public Drawable getDrawable(String source) {

                                    LevelListDrawable d = new LevelListDrawable();
                                    Drawable empty = getResources().getDrawable(R.drawable.icon);
                                    d.addLevel(0, 0, empty);
                                    d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

//                source = source.substring(2, source.length()-2);

                                    new LoadImage().execute(source, d, answerExplanationTextView);
                                    return d;
                                }
                            }, null));

                            answerExplanationTextView.setVisibility(View.VISIBLE);
                        }

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

//                source = source.substring(2, source.length()-2);

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

//            System.out.println("rb.setText(choice); yes" + choice);

            rb.setText(Html.fromHtml(choice, new Html.ImageGetter() {

                @Override
                public Drawable getDrawable(String source) {

                    LevelListDrawable d = new LevelListDrawable();
                    Drawable empty = getResources().getDrawable(R.drawable.icon);
                    d.addLevel(0, 0, empty);
                    d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

//                    source = source.substring(2, source.length() - 2);
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

        btnLike.setText(radioButtonTypeQuestion[13]+"");
//            btnDislike.setText(comment.getDislike()+"");


        if (radioButtonTypeQuestion[15].equals("1")) {
            btnLike.setCompoundDrawablesWithIntrinsicBounds(activeLikeDrawable, null, null, null);
        } else {
            btnLike.setCompoundDrawablesWithIntrinsicBounds(normalLikeDrawable, null, null, null);
        }

        if (radioButtonTypeQuestion[16].equals("1")) {
            btnDislike.setCompoundDrawablesWithIntrinsicBounds(activeDislikeDrawable, null, null, null);
        } else {
            btnDislike.setCompoundDrawablesWithIntrinsicBounds(normalDislikeDrawable, null, null, null);
        }

        tv_prepared_by.setText("Prepared by " + radioButtonTypeQuestion[12]);

        tv_prepared_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                profileIntent.putExtra("username", radioButtonTypeQuestion[11]);
                startActivity(profileIntent);
            }
        });

    }

    public void postInteraction(int comment_id, String like_or_dislike_or_delete, int is_remove) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = SplashActivity.BASEAPI+"wp/v2/post/like_dislike/"+comment_id;

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("like_or_dislike", like_or_dislike_or_delete);
        jsonBody.put("is_remove", is_remove);
        final String requestBody = jsonBody.toString();

        int REQUEST_METHOD = Request.Method.POST;
        if(like_or_dislike_or_delete.equals("delete")) {
            REQUEST_METHOD = Request.Method.DELETE;
            url = SplashActivity.BASEAPI + "wp/v2/comment/delete/" + comment_id;
        }
        StringRequest stringRequest = new StringRequest(REQUEST_METHOD, url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("post comment interaction success");
//                        System.out.println(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Check if the error has a network response
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    // Get the error status code
                    int statusCode = response.statusCode;

                    // Get the error response body as a string
                    String responseBody = new String(response.data, StandardCharsets.UTF_8);

                    // Print the error details
                    System.out.println("Error status code: " + statusCode);
                    System.out.println("Error response body: " + responseBody);
                } else {
                    // The error does not have a network response
                    System.out.println("Error message: " + error.getMessage());
                }
            }

        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer "+pre.getString("token", "None"));
                return params;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
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
}