package com.herma.apps.textbooks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.herma.apps.textbooks.comment.Comment;
import com.herma.apps.textbooks.comment.CommentAdapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CommentActivity extends AppCompatActivity {

    private Button btnComment, btnAddComment;
    private RecyclerView rvComment;
    private CommentAdapter commentAdapter;
    private List<Comment> comments;
    private Comment comment;
    private Random r = new Random();

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

        comment = new Comment();
        comment.setLike(r.nextInt(1000));
        comment.setDislike(r.nextInt(1000));
        comment.setComment("Sample text 1");
        comment.setAuthor("test Author 1 ");
        comment.setTimestamp("2022-01-01 10:00:00");

        comments.add(comment);

        comment = new Comment();
        comment.setLike(r.nextInt(1000));
        comment.setDislike(r.nextInt(1000));
        comment.setComment("Sample text 2");
        comment.setAuthor("test Author 2 ");
        comment.setTimestamp("2022-01-02 10:00:00");

        comments.add(comment);


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
        {
            String author = "Test author";

            // Create an instance of the dialog box
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CommentActivity.this);

            // Inflate the XML layout file
//                View dialogView = getLayoutInflater().inflate(R.layout.dialog_comment, null);
            View dialogView = LayoutInflater.from(CommentActivity.this).inflate(R.layout.dialog_comment, null);
            dialogBuilder.setView(dialogView);

            // Add the OK and Cancel buttons
            dialogBuilder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Get the input value and do something with it
                    TextInputEditText input = dialogView.findViewById(R.id.ti_message);
                    String inputMessage = input.getText().toString();


                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                    comment = new Comment();
                    comment.setLike(r.nextInt(1000));
                    comment.setDislike(r.nextInt(1000));
                    comment.setComment(inputMessage);
                    comment.setAuthor(author);
                    comment.setTimestamp(timestamp);
                    comments.add(comment);
                    commentAdapter.notifyDataSetChanged();

                }
            });
            dialogBuilder.setNegativeButton("Cancel", null);

            // Show the dialog box
            dialogBuilder.show();
        }
    }
}