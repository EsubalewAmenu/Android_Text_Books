package com.herma.apps.textbooks.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.textbooks.MainActivity;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.common.Item;
import com.herma.apps.textbooks.common.MainAdapter;

import java.util.ArrayList;

public class FavOldCurriculumBooks extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subject, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        try {
            MainActivity mainActivity = (MainActivity) getActivity();
            ArrayList arrayList = mainActivity.getData(getActivity(), "fav", "old");
            MainAdapter adapter = mainActivity.setData(getActivity(), arrayList, "a");

            recyclerView.setAdapter(adapter);

            GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);

        } catch (Exception e) {e.printStackTrace();}

        return root;
    }
}