package com.example.re_memocode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LeaderBoardAdapter extends ArrayAdapter<LevelProgress> {

    private Context context;
    private List<LevelProgress> leaderboardList;

    public LeaderBoardAdapter(Context context, List<LevelProgress> leaderboardList) {
        super(context, R.layout.leaderboard_item, leaderboardList);
        this.context = context;
        this.leaderboardList = leaderboardList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.leaderboard_item, parent, false);
        }

        // Get the current LevelProgress object
        LevelProgress progress = leaderboardList.get(position);

        // Set rank
        TextView tvRank = convertView.findViewById(R.id.tvRank);
        tvRank.setText(String.valueOf(position + 1)); // Rank based on position in list

        // Set username
        TextView tvUsername = convertView.findViewById(R.id.tvPlayername);
        tvUsername.setText(progress.username);

        // Set score
        TextView tvScore = convertView.findViewById(R.id.tvScore);
        tvScore.setText(String.valueOf(progress.score));

        // Set stars
        TextView tvStars = convertView.findViewById(R.id.tvStars);
        tvStars.setText(String.valueOf(progress.stars));

        // Set completion time
        TextView tvCompletionTime = convertView.findViewById(R.id.tvCompletionTime);
        tvCompletionTime.setText(String.format("%.2f", progress.completion_time));

        return convertView;
    }
}

