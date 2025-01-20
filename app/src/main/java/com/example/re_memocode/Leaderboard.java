package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Leaderboard extends AppCompatActivity {
    private Intent intent;
    String playerid;
    boolean offlineMode;

    Map<String, ?> allEntriesOffline;
    List<LevelProgressOffline> leaderboardListOffline;
    LeaderBoardOfflineAdapter adapterOffline;

    AccountAPI accountAPI;
    private int level_id;
    private int user_id ;
    private ListView listViewLeaderboard;
    private LeaderBoardAdapter adapter;
    private TextView buttonGoBackLB;
    private TextView textViewLeaderboardLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listViewLeaderboard = findViewById(R.id.leaderboardList);
        buttonGoBackLB = findViewById(R.id.buttonGoBackLeaderBoard);
        textViewLeaderboardLevel = findViewById(R.id.textViewLeaderboardLevel);

        intent = getIntent();
        level_id = intent.getIntExtra("level_id", level_id);
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        textViewLeaderboardLevel.setText("Level " + level_id);

        if (offlineMode){
            playerid = intent.getStringExtra("playerid");

            fetchLeaderboardDataOffline();

            buttonGoBackLB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Leaderboard.this, LeaderboardDashboard.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

        } else {
            //Session Token
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            if (sessionManager.hasSessionToken()) {
                // Check if a session exists
                if (sessionManager.hasSessionToken()) {
                    String token = sessionManager.getSessionToken();
                    user_id = Integer.parseInt(sessionManager.getUserId());
//                username = sessionManager.getUsername();
                    // Use the token and userId as needed
                }
            } else {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }


            buttonGoBackLB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Leaderboard.this, LeaderboardDashboard.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Httplink.httpLink)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            accountAPI = retrofit.create(AccountAPI.class);

            // Fetch the leaderboard data
            fetchLeaderboardData();
        }

    }

    private void fetchLeaderboardData() {
        // To fetch the leaderboard progress for a specific level
        Call<JsonObject>  call = accountAPI.getLevelProgress(level_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    int success = jsonObject.get("success").getAsInt();

                    if (success == 1) {
                        // Parsing the leaderboard data from the response
                        JsonArray data = jsonObject.getAsJsonArray("data");

                        // Convert the JsonArray into a list of LevelProgress objects
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<LevelProgress>>() {}.getType();
                        List<LevelProgress> leaderboardList = gson.fromJson(data, listType);

                        // Pass the leaderboardList to the adapter for the ListView or RecyclerView
                        adapter = new LeaderBoardAdapter(getApplicationContext(), leaderboardList);
                        listViewLeaderboard.setAdapter(adapter);

                        Toast.makeText(getApplicationContext(), "Leaderboard Data Loaded", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMsg = "Server Error: No leaderboard data available.";
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error: Empty response.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Failed to Fetch leaderboard data", t);
                Toast.makeText(getApplicationContext(), "Failed to Fetch leaderboard data", Toast.LENGTH_SHORT).show();
            }
        });



    }
    private void fetchLeaderboardDataOffline(){
        SharedPreferences sharedPreferences = getSharedPreferences("OfflineLevel" + String.valueOf(level_id) + "Data", MODE_PRIVATE);

        allEntriesOffline = sharedPreferences.getAll();
        leaderboardListOffline = new ArrayList<>();

        try {
            for (Map.Entry<String, ?> entry : allEntriesOffline.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();

                // Check if the key starts with "player" (this ensures we're getting player data)
                if (key.startsWith("player")) {
                    try {
                        String playerId = key.replace("playerid", ""); // Get the player ID from the key
                        String playerName = sharedPreferences.getString("playerid" + playerId, "Unknown");
                        double score = Double.parseDouble(sharedPreferences.getString("player" + playerId + "_score", "0"));
                        double stars = Double.parseDouble(sharedPreferences.getString("player" + playerId + "_stars", "0"));
                        double completedTime = Double.parseDouble(sharedPreferences.getString("player" + playerId + "_completedTime", "0"));

                        // Create LevelProgress object
                        LevelProgressOffline progress = new LevelProgressOffline(playerId, playerName, score, stars, completedTime);

                        // Add to the leaderboard list
                        leaderboardListOffline.add(progress);
                        Toast.makeText(getApplicationContext(), "Successfully Fetched leaderboard data", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to Fetch leaderboard data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // Sort leaderboard by completedTime (ascending) and score (descending) in case of ties
            Collections.sort(leaderboardListOffline, new Comparator<LevelProgressOffline>() {
                @Override
                public int compare(LevelProgressOffline p1, LevelProgressOffline p2) {
                    // First, compare by completedTime (ascending)
                    int timeComparison = Double.compare(p1.getCompletedTime(), p2.getCompletedTime());
                    if (timeComparison != 0) {
                        return timeComparison;
                    }
                    // If completedTime is the same, compare by score (descending)
                    return Double.compare(p2.getScore(), p1.getScore());
                }
            });

            // Now the leaderboardList is sorted, pass it to the adapter to display in the ListView
            adapterOffline = new LeaderBoardOfflineAdapter(getApplicationContext(), leaderboardListOffline);
            listViewLeaderboard.setAdapter(adapterOffline);

            Toast.makeText(getApplicationContext(), "Offline Leaderboard Data Loaded", Toast.LENGTH_LONG).show();
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed to Fetch leaderboard data", Toast.LENGTH_SHORT).show();
        }

    }
}