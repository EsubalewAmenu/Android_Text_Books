package com.herma.apps.textbooks.ui.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViewPagerAdapter extends FragmentStateAdapter {
    JSONArray mFollowers, mFollowings;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, JSONArray followers, JSONArray followings) {
        super(fragmentActivity);
        mFollowers = followers;
        mFollowings = followings;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FollowersFragment(mFollowers);
            case 1:
            default:
                return new FollowersFragment(mFollowings);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
