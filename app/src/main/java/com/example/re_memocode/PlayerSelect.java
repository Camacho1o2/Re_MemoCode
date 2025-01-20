package com.example.re_memocode;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSelect extends AppCompatActivity {

    private String selectedPlayerId;
    private String selectedPlayerName;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private List<Map<String, String>> playerList;
    RecyclerView playerRecyclerView;
    PlayerAdapter adapter;

    SharedPreferences temporaryPreference;

    TextView selectPlayerButton, addPlayerButton, goBackButton, deletePlayerButton;
    EditText inputPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_select);

        playerList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("OfflineData", MODE_PRIVATE);

        selectPlayerButton = findViewById(R.id.selectPlayerButton);
        addPlayerButton = findViewById(R.id.addPlayerButton);
        goBackButton = findViewById(R.id.goBackButton);
        inputPlayerName = findViewById(R.id.inputPlayerName);
        deletePlayerButton = findViewById(R.id.deletePlayerButton);
        playerRecyclerView = findViewById(R.id.playerRecyclerView);

        readSharedPreferences();

        // Set up RecyclerView
        playerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up RecyclerView
        playerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the Adapter with the player list and the selection listener
        adapter = new PlayerAdapter(playerList, new PlayerAdapter.OnPlayerSelectListener() {
            @Override
            public void onPlayerSelected(String playerId, String playerName) {
                // You can now use playerId and playerName for any further logic
                selectedPlayerId = playerId;
                selectedPlayerName = playerName;

                // For example, display a Toast with the selected player's name
                Toast.makeText(getApplicationContext(), "Selected Player: " + playerName, Toast.LENGTH_SHORT).show();
            }
        });

        // Set the adapter to the RecyclerView
        playerRecyclerView.setAdapter(adapter);

        selectPlayerButton.setOnClickListener(v -> {
            if (selectedPlayerId != null) {
                Intent intent = new Intent(PlayerSelect.this, MainActivity.class);
                intent.putExtra("offlineMode", true);
                intent.putExtra("playerid", selectedPlayerId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No player selected!", Toast.LENGTH_SHORT).show();
            }
        });

        addPlayerButton.setOnClickListener(v -> {
            String newPlayerName = inputPlayerName.getText().toString().trim();

            if (newPlayerName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Player name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a unique player ID
            int newPlayerId = sharedPreferences.getAll().size();

            try {
                //e.g. player1 : playerName
                editor = sharedPreferences.edit();
                editor.putString("playerid" + newPlayerId, newPlayerName);
                editor.apply();

                refreshRecyclerView();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to add player", Toast.LENGTH_SHORT).show();
            }
        });

        deletePlayerButton.setOnClickListener(v -> {
            if (selectedPlayerId != null) {
                deletePlayerOrNot(selectedPlayerId);
            } else {
                Toast.makeText(this, "No player selected for deletion!", Toast.LENGTH_SHORT).show();
            }
        });

        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        });
    }

    //Methods

    private void readSharedPreferences() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        playerList.clear();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();

            // Check if the key matches the player ID format
            if (key.startsWith("playerid")) {
                String playerId = key.replace("playerid", ""); // Extract the ID (e.g., "1" from "playerid1")
                String playerName = entry.getValue().toString(); // The player's name is the value

                // Avoid duplicates in the list
                boolean alreadyExists = false;
                for (Map<String, String> player : playerList) {
                    if (player.get("playerid").equals(playerId)) {
                        alreadyExists = true;
                        break;
                    }
                }

                if (!alreadyExists) {
                    Map<String, String> playerData = new HashMap<>();
                    playerData.put("playerid", playerId);
                    playerData.put("playername", playerName);
                    playerList.add(playerData);
                }
            }
        }
    }



    private void removeDataByPlayerId(String playerIdToRemove) {
        // Loop through all level SharedPreferences files
        for (int i = 1; i <= 8; i++) {
            // Get the SharedPreferences file for the current level
            String sharedPreferenceName = "OfflineLevel" + i + "Data";
            SharedPreferences levelPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
            SharedPreferences.Editor levelEditor = levelPreferences.edit();

            // Get all entries in the current SharedPreferences file
            Map<String, ?> allEntries = levelPreferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String key = entry.getKey();

                // Check if the key matches the player's data
                if (key.startsWith("player" + playerIdToRemove)) {
                    levelEditor.remove(key); // Remove the key
                }
            }

            // Apply changes to the current SharedPreferences file
            levelEditor.apply();
        }

        // Optionally, clear other player-specific data stored elsewhere (e.g., "OfflineData")
        SharedPreferences offlineDataPreferences = getSharedPreferences("OfflineData", MODE_PRIVATE);
        SharedPreferences.Editor offlineDataEditor = offlineDataPreferences.edit();
        Map<String, ?> offlineDataEntries = offlineDataPreferences.getAll();
        for (Map.Entry<String, ?> entry : offlineDataEntries.entrySet()) {
            String key = entry.getKey();

            // Check if the key matches the player ID format in "OfflineData"
            if (key.startsWith("playerid" + playerIdToRemove)) {
                offlineDataEditor.remove(key); // Remove the key
            }
        }

        // Apply changes to the "OfflineData" SharedPreferences
        offlineDataEditor.apply();
    }



    private void deletePlayerOrNot(String playerId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you really want to delete this player?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            removeDataByPlayerId(playerId);
            refreshRecyclerView();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshRecyclerView() {
        playerList.clear();
        readSharedPreferences();
        adapter.notifyDataSetChanged();
    }
}
