package com.herma.apps.textbooks.comment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.herma.apps.textbooks.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
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

        String imageUrl = "https://www.gravatar.com/avatar/dfssa";

        Glide.with(holder.llReplies.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.herma)
                .into(holder.ivProfilePicture);


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

                        addChild(comment, inputMessage, "Test Author", "15/05/2021 at 12:25", holder.llReplies.getContext(), holder.llReplies);
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
                comment.setLike(comment.getLike()+1);
                holder.btnLike.setText(comment.getLike()+"");
            }
        });
        holder.btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment.setDislike(comment.getDislike()-1);
                holder.btnDislike.setText(comment.getDislike()+"");
            }
        });

    }

    private void addChild(Comment parentComment, String inputMessage, String author, String timestamp, Context context, LinearLayoutCompat llReplies) {

        Random r = new Random();
//                for (int i = 0; i < 5; i++) {

        Comment newComment = new Comment();
        newComment.setLike(r.nextInt(1000));
        newComment.setDislike(r.nextInt(1000));
        newComment.setComment(inputMessage);
        newComment.setAuthor(author);
        newComment.setTimestamp(timestamp);

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
            CommentAdapter replyAdapter = new CommentAdapter(replyList);
            rvReplies.setAdapter(replyAdapter);
//            btn_more.se
        }
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
}
