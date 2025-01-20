package com.example.re_memocode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LeaderboardDashboard extends AppCompatActivity {

    private Intent intent;
    String playerid;
    boolean offlineMode;

    private int user_id;
    Button buttonGoToLevel1, buttonGoToLevel2, buttonGoToLevel3, buttonGoToLevel4, buttonGoToLevel5,
            buttonGoToLevel6, buttonGoToLevel7, buttonGoToLevel8;
    TextView btnGoBackLevelDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard_dashboard);

        buttonGoToLevel1 = findViewById(R.id.buttonGoToLevel1);
        buttonGoToLevel2 = findViewById(R.id.buttonGoToLevel2);
        buttonGoToLevel3 = findViewById(R.id.buttonGoToLevel3);
        buttonGoToLevel4 = findViewById(R.id.buttonGoToLevel4);
        buttonGoToLevel5 = findViewById(R.id.buttonGoToLevel5);
        btnGoBackLevelDB = findViewById(R.id.btnGoBackLvlDB);
        buttonGoToLevel6 = findViewById(R.id.buttonGoToLevel6);
        buttonGoToLevel7 = findViewById(R.id.buttonGoToLevel7);
        buttonGoToLevel8 = findViewById(R.id.buttonGoToLevel8);

        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        if (offlineMode){
            playerid = intent.getStringExtra("playerid");

            setButtonToGoToLevelOffline(buttonGoToLevel1, 1);
            setButtonToGoToLevelOffline(buttonGoToLevel2, 2);
            setButtonToGoToLevelOffline(buttonGoToLevel3, 3);
            setButtonToGoToLevelOffline(buttonGoToLevel4, 4);
            setButtonToGoToLevelOffline(buttonGoToLevel5, 5);
            setButtonToGoToLevelOffline(buttonGoToLevel6, 6);
            setButtonToGoToLevelOffline(buttonGoToLevel7, 7);
            setButtonToGoToLevelOffline(buttonGoToLevel8, 8);

            btnGoBackLevelDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(LeaderboardDashboard.this, MainActivity.class);
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

            setButtonToGoToLevel(buttonGoToLevel1, 1);
            setButtonToGoToLevel(buttonGoToLevel2, 2);
            setButtonToGoToLevel(buttonGoToLevel3, 3);
            setButtonToGoToLevel(buttonGoToLevel4, 4);
            setButtonToGoToLevel(buttonGoToLevel5, 5);
            setButtonToGoToLevel(buttonGoToLevel6, 6);
            setButtonToGoToLevel(buttonGoToLevel7, 7);
            setButtonToGoToLevel(buttonGoToLevel8, 8);

            btnGoBackLevelDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(LeaderboardDashboard.this, MainActivity.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });
        }

    }

    // Method to set button to go to an activity and pass an integer value
    private void setButtonToGoToLevel(Button button, int level_id) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the target activity
                Intent intent = new Intent(LeaderboardDashboard.this, Leaderboard.class);
                // Put the integer value as an extra
                intent.putExtra("level_id", level_id);
//                intent.putExtra("user_id", user_id);
                // Start the target activity
                startActivity(intent);
            }
        });
    }

    private void setButtonToGoToLevelOffline(Button button, int level_id) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the target activity
                Intent intent = new Intent(LeaderboardDashboard.this, Leaderboard.class);
                intent.putExtra("level_id", level_id);
                intent.putExtra("playerid", playerid);
                intent.putExtra("offlineMode", true);
                // Start the target activity
                startActivity(intent);
            }
        });
    }

}