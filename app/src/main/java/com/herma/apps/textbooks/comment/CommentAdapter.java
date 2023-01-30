package com.herma.apps.textbooks.comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.textbooks.R;

import java.util.ArrayList;
import java.util.List;

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



        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView rvReplies = new RecyclerView(holder.llReplies.getContext());
                rvReplies.setLayoutManager(new LinearLayoutManager(holder.llReplies.getContext()));
                holder.llReplies.addView(rvReplies);

                List<Comment> replyList = new ArrayList<>();
//                for (int i = 0; i < 5; i++) {
                    replyList.add(new Comment("Simple Text", "Test Author", "replay.t"));
//                }
                CommentAdapter replyAdapter = new CommentAdapter(replyList);
                rvReplies.setAdapter(replyAdapter);

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
        private Button btnReply;
        LinearLayoutCompat llReplies;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_date);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvTimestamp = itemView.findViewById(R.id.tv_date);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnReply = itemView.findViewById(R.id.btn_reply);
            llReplies = itemView.findViewById(R.id.ll_replies);



        }

    }
}
