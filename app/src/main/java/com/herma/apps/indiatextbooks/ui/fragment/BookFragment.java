package com.herma.apps.indiatextbooks.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.indiatextbooks.MainActivity;
import com.herma.apps.indiatextbooks.R;
import com.herma.apps.indiatextbooks.common.MainAdapter;
import com.herma.apps.indiatextbooks.common.DB;
import com.herma.apps.indiatextbooks.common.Item;

import java.util.ArrayList;

public class BookFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;
    DB db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subject, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        try {
            ArrayList arrayList = new MainActivity().getData(getActivity(), requireArguments().getString("choosedSubject"));
            MainAdapter adapter = new MainActivity().setData(getActivity(), arrayList, requireArguments().getString("p"));

            recyclerView.setAdapter(adapter);

            GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);

        } catch (Exception e) {e.printStackTrace();}

        return root;
    }
}