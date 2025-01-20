package com.example.re_memocode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    Intent intent;
    String playerid;
    String playername;
    Boolean offlineMode;
    SharedPreferences sharedPreferences;

    int user_id, level_id, nextlevel_id, stars;
    double completionTime, score;

    ImageView star1, star2, star3;
    TextView successMessage, resultText;
    TextView nextLevelButton, restartButton, mainMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        intent = getIntent();
        user_id = intent.getIntExtra("user_id", 0);
        playerid = intent.getStringExtra("playerid");
        level_id = intent.getIntExtra("level_id", 0);
        score = intent.getDoubleExtra("score", 0 );
        stars = intent.getIntExtra("stars", 0);
        completionTime = intent.getDoubleExtra("CompletionTime", 0);

         star1 = findViewById(R.id.star1Level2Part1);
         star2 = findViewById(R.id.star2Level2Part1);
         star3 = findViewById(R.id.star3Level2Part1);
         successMessage = findViewById(R.id.successMessage);
         resultText = findViewById(R.id.resultTextLevel5);
         nextLevelButton = findViewById(R.id.buttonNextLevel);
         restartButton = findViewById(R.id.restartButton);
         mainMenuButton = findViewById(R.id.mainMenuButton);

        offlineMode = intent.getBooleanExtra("offlineMode", false);

        if(offlineMode){

            nextLevelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nextLevelId = level_id + 1;
                    //returns the next level.class based on the current level_id
                    Class<?> nextLevelClass = getLevelClass(nextLevelId);
                    intent = new Intent(ResultActivity.this, nextLevelClass);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode",offlineMode);
                    startActivity(intent);
                }
            });

            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int restartLevelIdOffline = level_id;
                    //returns the next level.class based on the current level_id
                    Class<?> nextLevelClass = getLevelClass(restartLevelIdOffline);
                    intent = new Intent(ResultActivity.this, nextLevelClass);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode",offlineMode);
                    startActivity(intent);
                }
            });

            mainMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go back to Main Menu
                    intent = new Intent(ResultActivity.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", offlineMode);
                    startActivity(intent);
                }
            });

        } else {
            nextLevelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nextLevelId = level_id + 1;
                    //returns the next level.class based on the current level_id
                    Class<?> nextLevelClass = getLevelClass(nextLevelId);
                    intent = new Intent(ResultActivity.this, nextLevelClass);
//                 intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Restart the game
                    int restartLevelId = level_id;
                    //returns the next level.class based on the current level_id
                    Class<?> restartLevelClass = getLevelClass(restartLevelId);
                    intent = new Intent(ResultActivity.this, restartLevelClass);
                    startActivity(intent);
                    finish();
                }
            });

            mainMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go back to Main Menu
                    intent = new Intent(ResultActivity.this, MainActivity.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                    finish();
                }
            });
        }

        if(stars == 3){
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.VISIBLE);
            successMessage.setText("Congratulations!! You completed the level " + Integer.toString(level_id) + " exceptionally!! A score of " + score + " and in over " +
                    completionTime + " seconds!! Here, have 3 stars");
        } else if (stars == 2){
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.INVISIBLE);
            successMessage.setText("Congrats!! You completed the level " + Integer.toString(level_id) + " moderately with a score of " + score + " and with " +
                    completionTime + " seconds of completion!! Have 2 stars");
        } else if (stars == 1){
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
            successMessage.setText("You may have made a mistake here or there but at least you managed to complete the level " + Integer.toString(level_id) + " with a score of " + score + " and in over " +
                    completionTime + " seconds. Have a star.");
        } else if (stars == 0){
            star1.setVisibility(View.INVISIBLE);
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
            successMessage.setText("You tried or did you? Anyway, you go try it again.");
            resultText.setText("LEVEL FAIL");
        }
    }

    // Method to map the level id to the corresponding class
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
            case 9:
                return MainActivity.class;
            default:
                return null; // Return null if level doesn't exist
        }
    }
}
