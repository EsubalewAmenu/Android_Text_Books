package com.herma.apps.textbooks.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.herma.apps.textbooks.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FollowersRecyclerViewAdapter extends RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder> {

    private List<JSONObject> data;
    private Context context;
    private int itemLayout;

    public FollowersRecyclerViewAdapter(Context context, int itemLayout, List<JSONObject> data) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.textView.setText(data.get(position).getString("first_name") + " " + data.get(position).getString("last_name"));
            Glide.with(context)
                    .load(data.get(position).getString("avatar_url"))
                    .placeholder(R.drawable.herma)
                    .into(holder.avatar_image);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView avatar_image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name_text);
            avatar_image = itemView.findViewById(R.id.avatar_image);
        }
    }
}
