package com.example.re_memocode;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<Map<String, String>> playerList;
    private OnPlayerSelectListener onPlayerSelectListener;

    // Define an interface to listen for player selection
    public interface OnPlayerSelectListener {
        void onPlayerSelected(String playerId, String playerName);
    }

    // Constructor to pass the player list and listener
    public PlayerAdapter(List<Map<String, String>> playerList, OnPlayerSelectListener onPlayerSelectListener) {
        this.playerList = playerList;
        this.onPlayerSelectListener = onPlayerSelectListener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the player item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_item, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Map<String, String> playerData = playerList.get(position);
        String playerId = playerData.get("playerid");
        String playerName = playerData.get("playername");

        holder.playerNameView.setText(playerName);

        // Handle click events
        holder.itemView.setOnClickListener(v -> {
            if (onPlayerSelectListener != null) {
                onPlayerSelectListener.onPlayerSelected(playerId, playerName);
            }
        });
    }


    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameView; // TextView for the player name

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameView = itemView.findViewById(R.id.playerName); // Replace with your TextView ID
        }

        // Bind the player data and set the click listener
        void bind(Map<String, String> player, OnPlayerSelectListener listener) {
            String playerId = player.get("playerid");
            String playerName = player.get("playername");

            // Set the player name in the TextView
            playerNameView.setText(playerName);

            // Set click listener to notify when a player is selected
            itemView.setOnClickListener(v -> listener.onPlayerSelected(playerId, playerName));
        }
    }
}
