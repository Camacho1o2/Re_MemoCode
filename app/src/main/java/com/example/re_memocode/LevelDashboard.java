package com.example.re_memocode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.ArrayList;

import java.util.Map;

public class LevelDashboard extends AppCompatActivity {
    Intent intent;
    String playerid;
    String playername;
    Boolean offlineMode;

    int[] arrayLevels;
    int currentLevelRandom;
    Boolean randomMode;
    int getLevel = 1;

    int currentLevel;

    String status;
    SharedPreferences temporaryPreference;

    Button buttonGoBackLevelDB, buttonLevel1DB, buttonLevel2DB, buttonLevel3DB, buttonLevel4DB, buttonLevel5DB, buttonLevel6DB,
            buttonLevel7DB, buttonLevel8DB, buttonRandomize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level_dashboard);

        buttonGoBackLevelDB = findViewById(R.id.buttonGoBackLevelDB);
        buttonLevel1DB = findViewById(R.id.buttonLevel1DB);
        buttonLevel2DB = findViewById(R.id.buttonLevel2DB);
        buttonLevel3DB = findViewById(R.id.buttonLevel3DB);
        buttonLevel4DB = findViewById(R.id.buttonLevel4DB);
        buttonLevel5DB = findViewById(R.id.buttonLevel5DB);
        buttonLevel6DB = findViewById(R.id.buttonLevel6DB2);
        buttonLevel7DB = findViewById(R.id.buttonLevel7DB);
        buttonLevel8DB = findViewById(R.id.buttonLevel8DB2);
        buttonRandomize = findViewById(R.id.buttonRandomize);

        setCurrentLevelOffline();

        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        buttonRandomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    letsRandomize();
                } catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Failed to randomize", Toast.LENGTH_SHORT).show();
                    Log.d("SharedPreferences", String.valueOf(getLevel));


                }

            }
        });

        if (offlineMode){
            playerid = intent.getStringExtra("playerid");

            setButtonToGoToLevelGameOffline(buttonLevel1DB, 1);
            setButtonToGoToLevelGameOffline(buttonLevel2DB, 2);
            setButtonToGoToLevelGameOffline(buttonLevel3DB, 3);
            setButtonToGoToLevelGameOffline(buttonLevel4DB, 4);
            setButtonToGoToLevelGameOffline(buttonLevel5DB, 5);
            setButtonToGoToLevelGameOffline(buttonLevel6DB, 6);
            setButtonToGoToLevelGameOffline(buttonLevel7DB, 7);
            setButtonToGoToLevelGameOffline(buttonLevel8DB, 8);

            buttonGoBackLevelDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(LevelDashboard.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", offlineMode);
                    startActivity(intent);
                }
            });


        } else {
            setButtonToGoToLevelGame(buttonLevel1DB, 1);
            setButtonToGoToLevelGame(buttonLevel2DB, 2);
            setButtonToGoToLevelGame(buttonLevel3DB, 3);
            setButtonToGoToLevelGame(buttonLevel4DB, 4);
            setButtonToGoToLevelGame(buttonLevel5DB, 5);
            setButtonToGoToLevelGame(buttonLevel6DB, 6);
            setButtonToGoToLevelGame(buttonLevel7DB, 7);
            setButtonToGoToLevelGame(buttonLevel8DB, 7);

            buttonGoBackLevelDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(LevelDashboard.this, MainActivity.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });
        }

    }

    // Method to set button to go to an activity and pass an integer value
    private void setButtonToGoToLevelGame(Button button, int level_id) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the target activity
                Intent intent = new Intent(LevelDashboard.this, getLevelClass(level_id));
//                intent.putExtra("user_id", user_id);
                // Start the target activity
                startActivity(intent);
            }
        });
    }

    // Method to set button to go to an activity and pass an integer value
    private void setButtonToGoToLevelGameOffline(Button button, int level_id) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the target activity
                Intent intent = new Intent(LevelDashboard.this, getLevelClass(level_id));
                intent.putExtra("playerid", playerid);
                intent.putExtra("offlineMode", true);
                // Start the target activity
                startActivity(intent);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setCurrentLevelOffline() {
        for (int i = 1; i <= 8; i++) {
            // Access SharedPreferences for the current level
            String sharedPreferenceName = "OfflineLevel" + i + "Data";
            temporaryPreference = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);

            // Retrieve the player's status for the current level
            status = temporaryPreference.getString("player" + playerid + "_status", "incomplete");

            if (status.equals("incomplete")) {
                // Set the current level to the first incomplete level
                currentLevel = i;
                break; // Exit loop once the first incomplete level is found
            }
        }

        // Update button backgrounds for all levels up to the current level
        for (int i = 1; i <= currentLevel; i++) {
            switch (i) {
                case 1:
//                    buttonLevel1DB.setBackgroundColor(androidx.cardview.R.color.cardview_dark_background);
                    buttonLevel1DB.setBackgroundColor(R.color.custom_light_gray);
                    break;
                case 2:
                    buttonLevel2DB.setBackgroundColor(R.color.custom_light_gray);
                    break;
                case 3:
                    buttonLevel3DB.setBackgroundColor(R.color.custom_light_gray);
                    break;
                case 4:
                    buttonLevel4DB.setBackgroundColor(R.color.custom_light_gray);
                    break;
                case 5:
                    buttonLevel5DB.setBackgroundColor(R.color.custom_light_gray);
                    break;
                case 6:
                    buttonLevel6DB.setBackgroundColor(R.color.custom_light_gray);
                    break;
                case 7:
                    buttonLevel7DB.setBackgroundColor(R.color.custom_light_gray);
                    break;
                case 8:
                    buttonLevel8DB.setBackgroundColor(R.color.custom_light_gray);
                    break;
            }
        }

}

    private void letsRandomize(){
        arrayLevels = generateUniqueRandomArray();
        currentLevelRandom = 1;
        getLevel = arrayLevels[currentLevelRandom];

        intent = new Intent(LevelDashboard.this, getLevelClass(getLevel));
        intent.putExtra("arrayLevels", arrayLevels);
        intent.putExtra("randomMode", true);
        intent.putExtra("currentLevelRandom", currentLevelRandom);
        intent.putExtra("randomDuration", 600);
        intent.putExtra("score", 0.0);
        if(offlineMode){
            intent.putExtra("playerid", playerid);
            intent.putExtra("offlineMode", true);
        }
        startActivity(intent);

    }

    public  int[] generateUniqueRandomArray() {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            numbers.add(i); // Add numbers from 1 to 8
        }

        Collections.shuffle(numbers); // Shuffle the list

        // Convert the shuffled list to an array
        int[] array = new int[8];
        for (int i = 0; i < numbers.size(); i++) {
            array[i] = numbers.get(i);
        }
        return array;
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
                return Level6.class;
            case 7:
                return Level7.class;
            case 8:
                return Level8.class;
            default:
                return MainActivity.class; // Return null if level doesn't exist
        }
    }
}