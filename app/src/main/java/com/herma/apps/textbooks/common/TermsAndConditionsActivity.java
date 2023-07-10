package com.herma.apps.textbooks.common;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.herma.apps.textbooks.AddQuizActivity;
import com.herma.apps.textbooks.R;

public class TermsAndConditionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set checkbox
        CheckBox acceptCheckBox = findViewById(R.id.acceptCheckBox);

        // Set Accept & Continue button click listener
        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(acceptCheckBox.isChecked()){
                    Intent addQuizActivityIntent = new Intent(TermsAndConditionsActivity.this, AddQuizActivity.class);
                    addQuizActivityIntent.putExtra("chapterName", getIntent().getStringExtra("chapterName"));
                    addQuizActivityIntent.putExtra("subject", getIntent().getStringExtra("subject"));
                    addQuizActivityIntent.putExtra("fileName", getIntent().getStringExtra("fileName"));
                    startActivity(addQuizActivityIntent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.accept_the_terms), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
