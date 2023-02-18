package com.herma.apps.textbooks.comment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.textfield.TextInputEditText;
import com.herma.apps.textbooks.CommentActivity;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;
    private String chapter;

    public CommentAdapter(List<Comment> commentList, Context context, String chapter) {
        this.commentList = commentList;
        this.context = context;
        this.chapter = chapter;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        final Comment comment = commentList.get(position);

        holder.tvComment.setText(comment.getComment());
        holder.tvAuthor.setText(comment.getAuthor());
        holder.tvTimestamp.setText(comment.getTimestamp());
        holder.btnLike.setText(comment.getLike()+"");
        holder.btnDislike.setText(comment.getDislike()+"");
        holder.btn_more.setText("See " + comment.getChildCommentCount() + " replies");

        if(comment.getChildCommentCount() > 0 )
            holder.btn_more.setVisibility(View.VISIBLE);

//        String imageUrl = "https://www.gravatar.com/avatar/dfssa";

        Glide.with(holder.llReplies.getContext())
                .load(comment.getAuthor_avatar_url())//imageUrl)
                .placeholder(R.drawable.herma)
                .into(holder.ivProfilePicture);

        Drawable normalLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like);
        Drawable activeLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like_active);

        Drawable normalDislikeDrawable = context.getResources().getDrawable(R.drawable.ic_dislike);
        Drawable activeDislikeDrawable = context.getResources().getDrawable(R.drawable.ic_dislike_active);

        int isUserLiked = (new Random().nextInt(1));
        int isUserDisliked = (new Random().nextInt(1));

        if (isUserLiked == 1) {
            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(activeLikeDrawable, null, null, null);
        } else {
            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(normalLikeDrawable, null, null, null);
        }

        if (isUserDisliked == 1) {
            holder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(activeDislikeDrawable, null, null, null);
        } else {
            holder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(normalDislikeDrawable, null, null, null);
        }

        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an instance of the dialog box
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(holder.llReplies.getContext());

                // Inflate the XML layout file
//                View dialogView = getLayoutInflater().inflate(R.layout.dialog_comment, null);
                View dialogView = LayoutInflater.from(holder.llReplies.getContext()).inflate(R.layout.dialog_comment, null);
                dialogBuilder.setView(dialogView);

                // Add the OK and Cancel buttons
                dialogBuilder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get the input value and do something with it
                        TextInputEditText input = dialogView.findViewById(R.id.ti_message);
                        String inputMessage = input.getText().toString();

//                        addChild(comment, inputMessage, holder.llReplies.getContext(), holder.llReplies);
                        try {
                            postChildComment(inputMessage, comment, holder.llReplies);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", null);

                // Show the dialog box
                dialogBuilder.show();
            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.btnLike.getCompoundDrawables()[0] == normalLikeDrawable) {
                    holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(activeLikeDrawable, null, null, null);
                    comment.setLike(comment.getLike()+1);
                } else {
                    holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(normalLikeDrawable, null, null, null);
                    comment.setLike(comment.getLike()-1);
                }

                holder.btnLike.setText(comment.getLike()+"");
            }
        });
        holder.btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                comment.setDislike(comment.getDislike()-1);
//                holder.btnDislike.setText(comment.getDislike()+"");


                if (holder.btnDislike.getCompoundDrawables()[0] == normalDislikeDrawable) {
                    holder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(activeDislikeDrawable, null, null, null);
                    comment.setDislike(comment.getDislike()-1);
                } else {
                    holder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(normalDislikeDrawable, null, null, null);
                    comment.setDislike(comment.getDislike()+1);
                }

                holder.btnDislike.setText(comment.getDislike()+"");

            }
        });
        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!comment.isChildSeen()) {


                    try {
                        getComments(chapter, comment, holder.llReplies);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    comment.setChildSeen(true);
                }else{
                    holder.llReplies.removeAllViews();
                    comment.setChildSeen(false);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAuthor;
        private TextView tvComment;
        private TextView tvTimestamp;
        private Button btnLike;
        private Button btnDislike;
        private Button btnReply;
        LinearLayoutCompat llReplies;
        ImageView ivProfilePicture;
        private Button btn_more;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvTimestamp = itemView.findViewById(R.id.tv_date);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnDislike = itemView.findViewById(R.id.btn_dislike);
            btnReply = itemView.findViewById(R.id.btn_reply);
            llReplies = itemView.findViewById(R.id.ll_replies);
            ivProfilePicture = itemView.findViewById(R.id.iv_profile_picture);
            btn_more = itemView.findViewById(R.id.btn_more);

        }

    }


    public void postChildComment(String userComment, Comment parentComment, LinearLayoutCompat llReplies) throws JSONException {

        String chapter = "new_12wst1";

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = SplashActivity.BASEAPI+"wp/v2/chapter/comment/"+chapter+"/"+parentComment.getCommentId();

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("google_user_id", pre.getString("userId", "1"));
        jsonBody.put("email", pre.getString("email", "1"));
        jsonBody.put("registed_with", "google");
        jsonBody.put("comment_content", chapter+" "+userComment);
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        System.out.println("post comment response is ");
//                        System.out.println(response);

                        Random r = new Random();
//                for (int i = 0; i < 5; i++) {
                        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                        Comment newComment = new Comment();
                        newComment.setLike(r.nextInt(1000));
                        newComment.setDislike(r.nextInt(1000));
                        newComment.setComment(userComment);
                        newComment.setAuthor(pre.getString("email", "1"));
                        newComment.setTimestamp(timestamp);
                        newComment.setChildCommentCount(r.nextInt(3));

                        newComment.setAddReplyToParent(true);

                        if(parentComment.isAddReplyToParent()) {
                            commentList.add(newComment);
                        }else{
                            RecyclerView rvReplies = new RecyclerView(context);
                            rvReplies.setLayoutManager(new LinearLayoutManager(context));
                            llReplies.addView(rvReplies);

                            List<Comment> replyList = new ArrayList<>();
                            replyList.add(newComment);
//                }
                            CommentAdapter replyAdapter = new CommentAdapter(replyList, context, chapter);
                            rvReplies.setAdapter(replyAdapter);
//            btn_more.se
                        }
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
                params.put("username", pre.getString("email", "1"));//SplashActivity.USERNAME);
                params.put("password", pre.getString("userId", "1"));//SplashActivity.PAZZWORD);
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

    private void getComments(String chapter, Comment parentComment, LinearLayoutCompat llReplies) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(context);
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);

        String url = SplashActivity.BASEAPI+"wp/v2/chapter/comments/"+chapter+"/"+parentComment.getCommentId();


        JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", pre.getString("email", "1"));
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        System.out.println("get child comment request response is ");
//                        System.out.println(response);

                        if (response != null) {


                            try {
                                JSONArray datas = new JSONArray(response);

                                for(int i = 0; i < datas.length(); i++){
                                    JSONObject c = datas.getJSONObject(i);

//                                    addChild(parentComment, c.getString("comment_content"), context, llReplies);

                                    Random r = new Random();

                                    Comment newComment = new Comment();
                                    newComment.setCommentId(c.getInt("comment_ID"));
                                    newComment.setLike(r.nextInt(1000));
                                    newComment.setDislike(r.nextInt(1000));
                                    newComment.setComment(c.getString("comment_content").substring(chapter.length()));
                                    newComment.setAuthor(c.getString("display_name"));
                                    newComment.setTimestamp(c.getString("comment_date_gmt"));
                                    newComment.setAuthor_avatar_url(c.getString("author_avatar_urls"));
//                    comment.setAuthor_avatar_url(c.getJSONObject("author_avatar_urls").getString("24")); // options are 24, 48 & 96
                                    newComment.setChildCommentCount(c.getInt("child_comments_count"));

                                    newComment.setAddReplyToParent(true);

                                    if(parentComment.isAddReplyToParent()) {
                                        commentList.add(newComment);
                                    }else{
                                        RecyclerView rvReplies = new RecyclerView(context);
                                        rvReplies.setLayoutManager(new LinearLayoutManager(context));
                                        llReplies.addView(rvReplies);

                                        List<Comment> replyList = new ArrayList<>();
                                        replyList.add(newComment);
//                }
                                        CommentAdapter replyAdapter = new CommentAdapter(replyList, context, chapter);
                                        rvReplies.setAdapter(replyAdapter);
//            btn_more.se
                                    }

//                                    comment = new Comment();
//                                    comment.setCommentId(c.getInt("comment_ID"));
//                                    comment.setLike(r.nextInt(1000));
//                                    comment.setDislike(r.nextInt(1000));
//                                    comment.setComment(c.getString("comment_content"));
//                                    comment.setAuthor(c.getString("comment_author"));
//                                    comment.setTimestamp(c.getString("comment_date_gmt"));
//                                    comment.setChildCommentCount(r.nextInt(3));
//                                    comment.setAuthor_avatar_url(c.getString("author_avatar_urls"));
////                    comment.setAuthor_avatar_url(c.getJSONObject("author_avatar_urls").getString("24")); // options are 24, 48 & 96
//                                    comment.setChildCommentCount(c.getInt("child_comments_count"));
//                                    comments.add(comment);
//                                    commentAdapter.notifyDataSetChanged();

                                }

                            } catch (final JSONException e) {
                                System.out.println(e);
                            }
                        }
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
                params.put("username", SplashActivity.USERNAME);
                params.put("password", SplashActivity.PAZZWORD);
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
