package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    String playerid;
    boolean offlineMode;

    String playername;
    String username;

    int user_id;
    int level_id;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        intent = getIntent();
//        user_id = intent.getIntExtra("user_id", 0);

        TextView welcomePlayer = findViewById(R.id.textWelcome);
        ImageButton newGameButton = findViewById(R.id.newGameButton);
        ImageButton continueButton = findViewById(R.id.continueButton);
        ImageButton leaderboardsButton = findViewById(R.id.leaderboardsButton);
        ImageButton tutorialsButton = findViewById(R.id.btnTutorials);
        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        ImageButton exitButton = findViewById(R.id.btnExit);


        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        //To check if the it's in offlineMode
        if (offlineMode){
            playerid = intent.getStringExtra("playerid");

            sharedPreferences = getSharedPreferences("OfflineData", MODE_PRIVATE);

            playername = sharedPreferences.getString("playerid" + playerid, "Player");

            welcomePlayer.setText("Welome to Re:MemoCode " + playername + "!!");

            settingsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getApplicationContext(), Settings.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            newGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getApplicationContext(), LevelDashboard.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int continueLevelId = getCurrentLevelOffline();
                    Class<?> continuetLevelClass = getLevelClass(continueLevelId);
                    intent = new Intent(MainActivity.this, continuetLevelClass);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                    finish();
                }
            });

            leaderboardsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(MainActivity.this, LeaderboardDashboard.class);
                    intent.putExtra("offlineMode", true);
                    intent.putExtra("playerid", playerid);
                    startActivity(intent);
                }
            });

            tutorialsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(MainActivity.this, TutorialsDashboard.class);
                    intent.putExtra("offlineMode", true);
                    intent.putExtra("playerid", playerid);
                    startActivity(intent);
                }
            });

        } else {
            //Session Token
            SessionManager sessionManager = new SessionManager(MainActivity.this);
            if (sessionManager.hasSessionToken()) {
                // Check if a session exists
                if (sessionManager.hasSessionToken()) {
                    String token = sessionManager.getSessionToken();
                    user_id = Integer.parseInt(sessionManager.getUserId());
                    // Use the token and userId as needed
                }
            } else {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }


            settingsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getApplicationContext(), Settings.class);
//                intent.putExtra("user_id", user_id);
//                intent.putExtra("username", username);
                    startActivity(intent);
                }
            });

            newGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getApplicationContext(), LevelDashboard.class);
//                intent.putExtra("user_id", user_id);
//                intent.putExtra("username", username);
                    startActivity(intent);
                }
            });

            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implement Continue functionality
                    int continueLevelId = level_id + 1;
                    //returns the next level.class based on the current level_id
                    Class<?> continuetLevelClass = getLevelClass(continueLevelId);
                    intent = new Intent(MainActivity.this, continuetLevelClass);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                    finish();

                }
            });

            leaderboardsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(MainActivity.this, LeaderboardDashboard.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            tutorialsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(MainActivity.this, TutorialsDashboard.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Httplink.httpLink)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AccountAPI accountAPI = retrofit.create(AccountAPI.class);

            //get the user's current progress
            Call<JsonObject> call = accountAPI.getProgress(user_id);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject jsonObject = response.body();
                        int success = jsonObject.get("success").getAsInt();
                        if (success == 1){
                            level_id = jsonObject.get("level_id").getAsInt();
                            username = jsonObject.get("username").getAsString();
                            String status = jsonObject.get("status").getAsString();
                            welcomePlayer.setText("Welome " + username + ": " + " Current Level: " + level_id + " Status: " + status);

                            sessionManager.updateUsername(username);
                        } else {
                            String errorrMsg = "Server Error: no progress found";
                            Toast.makeText(getApplicationContext(), errorrMsg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Server Error: Empty response.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Failed to Get Data. Try again", t);
                    Toast.makeText(getApplicationContext(), "Failed to Get Data. Try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0); // Optional: Terminates the process

            }
        });

    }

    private int getCurrentLevelOffline(){
        int currentLevel;
        String status;
        SharedPreferences temporaryPreference;

        for(int i = 1; i <= 8; i++){
            temporaryPreference = getSharedPreferences("OfflineLevel"+ i + "Data", MODE_PRIVATE);

            status = temporaryPreference.getString("player" + playerid + "_status", "incomplete");

            if(status.equals("incomplete")){
                currentLevel = i;
                break;
            }
        }

        return 1;
    }

    private Class<?> getLevelClass(int levelId) {
        switch (levelId) {
            case 1:
                return Level1.class;
            case 2:
                return Level2.class;
            case 3:
                return Level3.class;
            case 4:
                return Level4.class;
            case 5:
                return Level5.class;
            case 6:
                return Level5.class;
            case 7:
                return Level5.class;
            case 8:
                return Level5.class;
            case 9:
                return MainActivity.class;
            default:
                return null; // Return null if level doesn't exist
        }
    }

}
