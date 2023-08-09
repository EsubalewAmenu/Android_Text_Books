package com.herma.apps.textbooks.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.textbooks.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * This fragment provide the RadioButton/Single Options.
 */
public class FollowersFragment extends Fragment {


    RecyclerView recyclerView;
    FollowersRecyclerViewAdapter adapter;
    JSONArray mFollows;

    public FollowersFragment(JSONArray follows) {
        mFollows = follows;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_subject, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // using a linear layout manager


        populateListView();
        return rootView;
    }

    private void populateListView() {
        List<JSONObject> followers = new ArrayList<>();
        try {

            for (int i = 0; i < mFollows.length(); i++) {
                followers.add(mFollows.getJSONObject(i));
            }
            if(mFollows.length() == 0){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("first_name", "No data");
                jsonObject.put("last_name", "found");
                jsonObject.put("avatar_url", "found");
                followers.add(jsonObject);

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        adapter = new FollowersRecyclerViewAdapter(getContext(), R.layout.list_item_follow, followers);
        recyclerView.setAdapter(adapter);
    }

}