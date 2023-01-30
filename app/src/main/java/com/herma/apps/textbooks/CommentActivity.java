package com.herma.apps.textbooks;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.textbooks.comment.Comment;
import com.herma.apps.textbooks.comment.CommentAdapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity {

    private Button btnComment, btnAddComment;
    private RecyclerView rvComment;
    private CommentAdapter commentAdapter;
    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initViews();
        initListeners();
        initRecyclerView();

    }

    private void initViews() {
        btnComment = findViewById(R.id.btn_comment);
        btnAddComment = findViewById(R.id.btn_add_comment);
        rvComment = findViewById(R.id.rv_comment);
    }

    private void initListeners() {
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentSection();
            }
        });

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
    }

    private void initRecyclerView() {
        comments = new ArrayList<>();


        comments.add(new Comment("sample text 1", "test author 1", "2022-01-01 10:00:00"));
        comments.add(new Comment("sample text 2", "test author 2", "2022-01-02 11:00:00"));


        commentAdapter = new CommentAdapter(comments);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setAdapter(commentAdapter);
    }

    private void showCommentSection() {
//        btnComment.setVisibility(View.GONE);
        rvComment.setVisibility(View.VISIBLE);
        btnAddComment.setVisibility(View.VISIBLE);
    }

    private void addComment() {
        String commentText = "Simple text";
        String author = "Test author";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Comment comment = new Comment(commentText, author, timestamp);
        comments.add(comment);
        commentAdapter.notifyDataSetChanged();
    }
}