package com.herma.apps.textbooks.comment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
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
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {//RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;
    private String chapter;

    private static final int ITEM_TYPE_COMMENT = 0;
    private static final int ITEM_TYPE_LOAD_MORE = 1;

    private boolean isLoading = false;
    LoadMoreListener loadMoreListener;
    LoadMoreViewHolder loadMoreViewHolder;
    int repliesPerPage = 20;
    int page = 1;

    CommentAdapter replyAdapter = null, subReplyAdapter = null;
    SharedPreferences pre = null;

    public CommentAdapter(List<Comment> commentList, Context context, String chapter, LoadMoreListener loadMoreListener) {
        this.commentList = commentList;
        this.context = context;
        this.chapter = chapter;
        this.loadMoreListener = loadMoreListener;

        pre = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_COMMENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
            return new CommentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_button, parent, false);
            return new LoadMoreViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentViewHolder) {

            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;

            final Comment comment = commentList.get(position);

            commentViewHolder.tvComment.setText(comment.getComment());
            commentViewHolder.tvAuthor.setText(comment.getAuthor());
            commentViewHolder.tvTimestamp.setText(comment.getTimestamp());
            commentViewHolder.btnLike.setText(comment.getLike()+"");
//            commentViewHolder.btnDislike.setText(comment.getDislike()+"");
            commentViewHolder.btn_more.setText(context.getString(R.string.see) + comment.getChildCommentCount() + context.getString(R.string.replies));

        if(comment.getChildCommentCount() > 0 )
            commentViewHolder.btn_more.setVisibility(View.VISIBLE);

            if(comment.isAddReplyToParent()){
                commentViewHolder.btnReply.setVisibility(View.GONE);
            }
//        String imageUrl = "https://www.gravatar.com/avatar/dfssa";

        Glide.with(commentViewHolder.llReplies.getContext())
                .load(comment.getAuthor_avatar_url())//imageUrl)
                .placeholder(R.drawable.herma)
                .into(commentViewHolder.ivProfilePicture);

            Drawable normalLikeDrawable = null, activeLikeDrawable = null, normalDislikeDrawable = null, activeDislikeDrawable = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                normalLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like, context.getTheme());
                 activeLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like_active, context.getTheme());
                 normalDislikeDrawable = context.getResources().getDrawable(R.drawable.ic_dislike, context.getTheme());
                 activeDislikeDrawable = context.getResources().getDrawable(R.drawable.ic_dislike_active, context.getTheme());
            }else{
                normalLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like);
                 activeLikeDrawable = context.getResources().getDrawable(R.drawable.ic_like_active);
                 normalDislikeDrawable = context.getResources().getDrawable(R.drawable.ic_dislike);
                 activeDislikeDrawable = context.getResources().getDrawable(R.drawable.ic_dislike_active);

            }


//        int isUserLiked = (new Random().nextInt(1));
//        int isUserDisliked = (new Random().nextInt(1));

        if (comment.getIs_user_liked() == 1) {
            commentViewHolder.btnLike.setCompoundDrawablesWithIntrinsicBounds(activeLikeDrawable, null, null, null);
        } else {
            commentViewHolder.btnLike.setCompoundDrawablesWithIntrinsicBounds(normalLikeDrawable, null, null, null);
        }

        if (comment.getIs_user_disliked() == 1) {
            commentViewHolder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(activeDislikeDrawable, null, null, null);
        } else {
            commentViewHolder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(normalDislikeDrawable, null, null, null);
        }

            commentViewHolder.btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    System.out.println("Reported "+ comment.getCommentId());
                    ReportCommentDialog dialog = new ReportCommentDialog(context, comment.getCommentId());
                    dialog.show();

                }
            });
            commentViewHolder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an instance of the dialog box
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(commentViewHolder.llReplies.getContext());

                // Inflate the XML layout file
//                View dialogView = getLayoutInflater().inflate(R.layout.dialog_comment, null);
                View dialogView = LayoutInflater.from(commentViewHolder.llReplies.getContext()).inflate(R.layout.dialog_comment, null);
                dialogBuilder.setView(dialogView);

                // Add the OK and Cancel buttons
                dialogBuilder.setPositiveButton(context.getString(R.string.post), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get the input value and do something with it
                        TextInputEditText input = dialogView.findViewById(R.id.ti_message);
                        String inputMessage = input.getText().toString().trim();

                        if(!inputMessage.isEmpty()){
                        try {
                            postChildComment(inputMessage, comment, commentViewHolder.llReplies);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                            Toast.makeText(context, context.getString(R.string.write_your_comment),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogBuilder.setNegativeButton(context.getString(R.string.cancel), null);

                // Show the dialog box
                dialogBuilder.show();
            }
        });

            Drawable finalActiveLikeDrawable = activeLikeDrawable;
            Drawable finalNormalLikeDrawable = normalLikeDrawable;
            Drawable finalActiveDislikeDrawable = activeDislikeDrawable;
            Drawable finalNormalDislikeDrawable = normalDislikeDrawable;
            commentViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (commentViewHolder.btnLike.getCompoundDrawables()[0] == finalNormalLikeDrawable) {
                    commentViewHolder.btnLike.setCompoundDrawablesWithIntrinsicBounds(finalActiveLikeDrawable, null, null, null);
                    comment.setLike(comment.getLike()+1);


                    if(commentViewHolder.btnDislike.getCompoundDrawables()[0] == finalActiveDislikeDrawable){
                        commentViewHolder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(finalNormalDislikeDrawable, null, null, null);
//                        comment.setDislike(comment.getDislike()-1);
//                        commentViewHolder.btnDislike.setText(comment.getDislike()+"");
                    }

                    try {
                        postInteraction(comment.getCommentId(), "L", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    commentViewHolder.btnLike.setCompoundDrawablesWithIntrinsicBounds(finalNormalLikeDrawable, null, null, null);
                    comment.setLike(comment.getLike()-1);

                    try {
                        postInteraction(comment.getCommentId(), "L", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                commentViewHolder.btnLike.setText(comment.getLike()+"");
            }
        });
            Drawable finalNormalDislikeDrawable1 = normalDislikeDrawable;
            Drawable finalActiveDislikeDrawable1 = activeDislikeDrawable;
            Drawable finalActiveLikeDrawable1 = activeLikeDrawable;
            Drawable finalNormalLikeDrawable1 = normalLikeDrawable;

            if(comment.getComment_author_email().equals(pre.getString("email", "1"))) {
                commentViewHolder.btn_delete_comment.setVisibility(View.VISIBLE);
                commentViewHolder.btn_edit_comment.setVisibility(View.VISIBLE);
            }


            commentViewHolder.btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                comment.setDislike(comment.getDislike()-1);
//                holder.btnDislike.setText(comment.getDislike()+"");

                if (commentViewHolder.btnDislike.getCompoundDrawables()[0] == finalNormalDislikeDrawable1) {
                    commentViewHolder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(finalActiveDislikeDrawable1, null, null, null);
                    comment.setDislike(comment.getDislike()+1);

                    if(commentViewHolder.btnLike.getCompoundDrawables()[0] == finalActiveLikeDrawable1){
                        commentViewHolder.btnLike.setCompoundDrawablesWithIntrinsicBounds(finalNormalLikeDrawable1, null, null, null);
                        comment.setLike(comment.getLike()-1);
                        commentViewHolder.btnLike.setText(comment.getLike()+"");

                    }

                    try {
                        postInteraction(comment.getCommentId(), "D", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    commentViewHolder.btnDislike.setCompoundDrawablesWithIntrinsicBounds(finalNormalDislikeDrawable1, null, null, null);
                    comment.setDislike(comment.getDislike()-1);

                    try {
                        postInteraction(comment.getCommentId(), "D", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

//                commentViewHolder.btnDislike.setText(comment.getDislike()+"");

            }
        });
            commentViewHolder.btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!comment.isChildSeen()) {

                        page = 1;

                        try {
                            getComments(chapter, comment, commentViewHolder.llReplies);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        comment.setChildSeen(true);
                    }else{
                        commentViewHolder.llReplies.removeAllViews();
                        comment.setChildSeen(false);
                    }
                }
            });

            commentViewHolder.btn_edit_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("btn_edit_comment");
                }
            });
            commentViewHolder.btn_delete_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.confirem_delete);
                    builder.setMessage(R.string.confirem_delete_message);

// Add the buttons
                    builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked Yes button, perform the delete action here
                            try {
                                postInteraction(comment.getCommentId(), "delete", 0);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked Cancel button, do nothing
                            dialog.cancel();
                        }
                    });

// Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
        } else if (holder instanceof LoadMoreViewHolder) {
            loadMoreViewHolder = (LoadMoreViewHolder) holder;

            if (commentList.isEmpty()) {
                // Disable the "Load More" button if there are no comments
                loadMoreViewHolder.progressBar.setVisibility(View.GONE);
                loadMoreViewHolder.btnLoadMore.setVisibility(View.GONE);
            } else if(loadMoreViewHolder.removeChildCommentLoadMore ){
                if(loadMoreViewHolder.noMoreComment) {
                    setNoMoreReplies();
                }else{
                    loadMoreViewHolder.progressBar.setVisibility(View.GONE);
                loadMoreViewHolder.btnLoadMore.setVisibility(View.GONE);
                }

            }else {
                if (isLoading) {

                    loadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
                    loadMoreViewHolder.btnLoadMore.setVisibility(View.GONE);
                } else {
                    loadMoreViewHolder.progressBar.setVisibility(View.GONE);
                    loadMoreViewHolder.btnLoadMore.setVisibility(View.VISIBLE);
                }
            loadMoreViewHolder.btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loadMoreListener != null) {
                        isLoading = true;
                        loadMoreListener.onLoadMore();
                    }
                }
            });
        }            }

    }

    private void setNoMoreReplies() {

        loadMoreViewHolder.btnLoadMore.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        loadMoreViewHolder.btnLoadMore.setTextColor(ContextCompat.getColor(context, R.color.cardview_dark_background));
        loadMoreViewHolder.btnLoadMore.setText(context.getString(R.string.end_of_replies));
        loadMoreViewHolder.btnLoadMore.setEnabled(false);
        loadMoreViewHolder.btnLoadMore.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return commentList.size() + 1; // +1 for Load More item
    }

    @Override
    public int getItemViewType(int position) {
        if (position < commentList.size()) {
            return ITEM_TYPE_COMMENT;
        } else {
            return ITEM_TYPE_LOAD_MORE;
        }
    }

    public void setNoMoreComment() {

        loadMoreViewHolder.btnLoadMore.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        loadMoreViewHolder.btnLoadMore.setTextColor(ContextCompat.getColor(context, R.color.cardview_dark_background));
        loadMoreViewHolder.btnLoadMore.setText(context.getString(R.string.end_of_comments));
        loadMoreViewHolder.btnLoadMore.setEnabled(false);

        notifyDataSetChanged();
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    public void removeLoadMore() {
        this.isLoading = false;
        loadMoreViewHolder.removeChildCommentLoadMore = true;
        notifyDataSetChanged();
    }
    public interface LoadMoreListener {
        void onLoadMore();
    }


    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAuthor;
        private TextView tvComment;
        private TextView tvTimestamp;
        private Button btnLike;
        private Button btnDislike;
        private Button btnReply;
        private Button btnReport;
        LinearLayoutCompat llReplies;
        ImageView ivProfilePicture;
        private Button btn_more;
        private Button btn_edit_comment;
        private Button btn_delete_comment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvTimestamp = itemView.findViewById(R.id.tv_date);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnDislike = itemView.findViewById(R.id.btn_dislike);
            btnReply = itemView.findViewById(R.id.btn_reply);
            btnReport = itemView.findViewById(R.id.btn_report);
            llReplies = itemView.findViewById(R.id.ll_replies);
            ivProfilePicture = itemView.findViewById(R.id.iv_profile_picture);
            btn_more = itemView.findViewById(R.id.btn_more);
            btn_edit_comment = itemView.findViewById(R.id.btn_comment_edit);
            btn_delete_comment = itemView.findViewById(R.id.btn_comment_delete);

        }

    }

    static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private Button btnLoadMore;

        private boolean removeChildCommentLoadMore;
        private boolean noMoreComment;


        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnLoadMore = itemView.findViewById(R.id.btn_load_more);
            removeChildCommentLoadMore = false;
            noMoreComment = false;
        }
    }

    public void postChildComment(String userComment, Comment parentComment, LinearLayoutCompat llReplies) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = SplashActivity.BASEAPI+"wp/v2/chapter/comment/"+chapter+"/"+parentComment.getCommentId();

        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("google_user_id", pre.getString("google_user_id", "1"));
//        jsonBody.put("username", pre.getString("email", "1"));
//        jsonBody.put("login_with", pre.getString("registed_with", "1"));
        jsonBody.put("comment_content", chapter+" "+userComment);
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url ,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                        JSONObject c = new JSONObject(response).getJSONObject("comment");

                        Comment newComment = new Comment();
                        newComment.setCommentId(c.getInt("comment_ID"));
                        newComment.setLike(c.getInt("likes"));
                        newComment.setDislike(c.getInt("dislikes"));
                        newComment.setIs_user_liked(c.getInt("is_user_liked"));
                        newComment.setIs_user_disliked(c.getInt("is_user_liked"));
                        newComment.setComment(c.getString("comment_content").substring(chapter.length()));
                        newComment.setAuthor(c.getString("display_name"));
                        newComment.setComment_author_email(c.getString("comment_author_email"));
                        newComment.setTimestamp(c.getString("comment_date_gmt"));
                        newComment.setAuthor_avatar_url(c.getString("author_avatar_urls"));
//                    newComment.setAuthor_avatar_url(c.getJSONObject("author_avatar_urls").getString("24")); // options are 24, 48 & 96
                        newComment.setChildCommentCount(c.getInt("child_comments_count"));

                        newComment.setAddReplyToParent(true);

                        if(parentComment.isAddReplyToParent()) {
                            commentList.add(0,newComment);
                        }else{
                            RecyclerView rvReplies = new RecyclerView(context);
                            rvReplies.setLayoutManager(new LinearLayoutManager(context));
                            llReplies.addView(rvReplies,0);

                            List<Comment> replyList = new ArrayList<>();
                            replyList.add(newComment);
                            replyAdapter = new CommentAdapter(replyList, context, chapter, new LoadMoreListener() {
                                @Override
                                public void onLoadMore() {
//                                    System.out.println("on load more on child adapter 2");
                                    if (!isLoading) {
                                        isLoading = true;
                                        replyAdapter.setLoading(true);
                                        page++;
                                        try {
                                            getComments(chapter,parentComment, llReplies);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        replyAdapter.setLoading(false);
                                    }
                                }
                            });
                            rvReplies.setAdapter(replyAdapter);
                        }

                        } catch (final JSONException e) {
                            System.out.println(e);
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
                params.put("password", pre.getString("google_user_id", "1"));//SplashActivity.PAZZWORD);
                params.put("login_with", pre.getString("registed_with", "1"));
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

        String url = SplashActivity.BASEAPI+"wp/v2/chapter/comments/"+chapter+"/"+parentComment.getCommentId()+"?page="+page+"&per_page="+repliesPerPage;


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

                                if(datas.length() == 0){ // < repliesPerPage) {
                                    subReplyAdapter.loadMoreViewHolder.noMoreComment = true;
                                    subReplyAdapter.notifyDataSetChanged();
                                }

                                List<Comment> replyList = new ArrayList<>();

                                RecyclerView rvReplies = new RecyclerView(context);
                                rvReplies.setLayoutManager(new LinearLayoutManager(context));
                                llReplies.addView(rvReplies);

                                for(int i = 0; i < datas.length(); i++){
                                    JSONObject c = datas.getJSONObject(i);

                                    Comment newComment = new Comment();
                                    newComment.setCommentId(c.getInt("comment_ID"));
                                    newComment.setLike(c.getInt("likes"));
                                    newComment.setDislike(c.getInt("dislikes"));
                                    newComment.setIs_user_liked(c.getInt("is_user_liked"));
                                    newComment.setIs_user_disliked(c.getInt("is_user_disliked"));
                                    newComment.setComment(c.getString("comment_content").substring(chapter.length()));
                                    newComment.setAuthor(c.getString("display_name"));
                                    newComment.setComment_author_email(c.getString("comment_author_email"));
                                    newComment.setTimestamp(c.getString("comment_date_gmt"));
                                    newComment.setAuthor_avatar_url(c.getString("author_avatar_urls"));
//                    comment.setAuthor_avatar_url(c.getJSONObject("author_avatar_urls").getString("24")); // options are 24, 48 & 96
                                    newComment.setChildCommentCount(c.getInt("child_comments_count"));

                                    newComment.setAddReplyToParent(true);

                                    if(parentComment.isAddReplyToParent()) {
                                        commentList.add(newComment);
                                    }else{

                                        replyList.add(newComment);
                                    }

                                }

                                subReplyAdapter = new CommentAdapter(replyList, context, chapter, new LoadMoreListener() {
                                    @Override
                                    public void onLoadMore() {
                                        if (!isLoading) {
                                            isLoading = true;
                                            subReplyAdapter.setLoading(true);
                                            page++;
                                            try {
                                                getComments(chapter,parentComment, llReplies);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            isLoading = false;
                                            subReplyAdapter.removeLoadMore();


                                        }
                                    }
                                });
                                rvReplies.setAdapter(subReplyAdapter);

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
                params.put("username", pre.getString("email", "1"));//SplashActivity.USERNAME);
                params.put("password", pre.getString("google_user_id", "1"));//SplashActivity.PAZZWORD);
                params.put("login_with", pre.getString("registed_with", "1"));
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

    public void postInteraction(int comment_id, String like_or_dislike_or_delete, int is_remove) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = SplashActivity.BASEAPI+"wp/v2/comment/like_dislike/"+comment_id;

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);

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
                        System.out.println("post comment interaction response is ");
                        System.out.println(response);
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
                params.put("password", pre.getString("google_user_id", "1"));//SplashActivity.PAZZWORD);
                params.put("login_with", pre.getString("registed_with", "1"));
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
