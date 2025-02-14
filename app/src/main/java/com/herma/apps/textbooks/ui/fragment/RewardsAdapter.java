package com.herma.apps.textbooks.ui.fragment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.textbooks.R;

import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.RewardViewHolder> {

    private List<RewardItem> rewardsList;

    public RewardsAdapter(List<RewardItem> rewardsList) {
        this.rewardsList = rewardsList;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        RewardItem reward = rewardsList.get(position);

        // Format the reward amount
        String amount = reward.getAmount().isEmpty() ? "0" : reward.getAmount();

        // Set data to the views
        holder.dateTextView.setText(reward.getDate());
        holder.amountTextView.setText("+" + amount + " ETB");

        // Set the reward status and color
        String status = reward.getStatus();
        holder.statusTextView.setText(status.substring(0, 1).toUpperCase() + status.substring(1)); // Capitalize status
        if (status.equals("pending")) {
            holder.statusTextView.setTextColor(Color.parseColor("#F44336")); // Red
        } else if (status.equals("publish")) {
            holder.statusTextView.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.statusTextView.setTextColor(Color.parseColor("#9E9E9E")); // Gray
        }
    }


    @Override
    public int getItemCount() {
        return rewardsList.size();
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, amountTextView, statusTextView;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.rewardDate);
            amountTextView = itemView.findViewById(R.id.rewardAmount);
            statusTextView = itemView.findViewById(R.id.rewardStatus);
        }
    }
}
