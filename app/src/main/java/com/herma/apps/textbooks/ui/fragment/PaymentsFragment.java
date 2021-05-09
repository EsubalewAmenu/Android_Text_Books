package com.herma.apps.textbooks.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.ReadActivity;

public class PaymentsFragment extends Fragment {


    Button btnMyPayments;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payments, container, false);


        EditText etPhone = (EditText) root.findViewById(R.id.etPhone);
        btnMyPayments = (Button) root.findViewById(R.id.btnMyPayments);

        btnMyPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((etPhone.getText().toString()).matches("^09\\d{8}")) {

                    // display report

                } else {
                    Toast.makeText(getActivity(), R.string.wrong_number, Toast.LENGTH_LONG).show();
                }

            }
        });


        return root;
    }
}