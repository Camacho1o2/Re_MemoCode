package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Level4 extends AppCompatActivity {

    private int level_id = 4;
    Intent intent;
    String playerid;
    String playername;
    Boolean offlineMode;
    SharedPreferences sharedPreferences;

    //for the randomized mode
    int[] arrayLevels;
    int currentLevelRandom;
    Boolean randomMode;
    int randomDuration;

    AccountAPI accountAPI;
    private Handler handler;
    private Runnable runnable;
    private int seconds = 100; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;
    String status = "completed";
    int user_id;

    String playerAnswer;
    String playerAnswer2;
    String correctAnswer = "# Declaring variables and assigning values\nname = Alice\nage = 25\nheight = 5.7\nisstudent = true\n\n# Printing the values of the variables using the print() function\n\nprint(&quot;Name: &quot;, name)\nprint(&quot;Age: &quot;, age)\nprint(&quot;Height: &quot;, height)\nprint(&quot;Is Student: &quot;, isstudent)";
    String correctAnswer2 = "x = 10\\ny = 5\\n\\nsum_result = x + y\\nproduct = x - y\\nquotient = x / y\\nremainder = x % y\\n\\nprint(&quot;Sum:&quot;, sum_result)\\nprint(&quot;Product:&quot;, product)\\nprint(&quot;Quotient:&quot;, quotient)\\nprint(&quot;Remainder:&quot;, remainder)";

    //Part 1
    private TextView timerTextViewLevel4Part1, textResultsLevel4Part1, progressTextLevel4Part1;
    private EditText codeSnippetEditTextLevel4Part1;
    private ImageView star1Level4Part1, star2Level4Part1, star3Level4Part1;

    private ImageButton buttonGoBackLevel4Part1, buttonClearAreaLevel4Part1, buttonRunLevel4Part1, buttonProceedLevel4Part1;

    //Part 2
    private TextView timerTextViewLevel4Part2, textResultsLevel4Part2, progressTextLevel4Part2;
    private EditText codeSnippetEditTextLevel4Part2;
    private ImageView star1Level4Part2, star2Level4Part2, star3Level4Part2;

    private ImageButton buttonGoBackLevel4Part2, buttonClearAreaLevel4Part2, buttonRunLevel4Part2, buttonProceedLevel4Part2;

    // 60 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level4_part1);

        timerTextViewLevel4Part1 = findViewById(R.id.timerTextLevel4Part1);
        textResultsLevel4Part1 = findViewById(R.id.textResultLevel4Part1);
        progressTextLevel4Part1 = findViewById(R.id.progressTextLevel4Part1);

        codeSnippetEditTextLevel4Part1 = findViewById(R.id.codeSnippetLevel4Part1);

        buttonGoBackLevel4Part1 = findViewById(R.id.buttonGoBackLevel4Part1);
        buttonClearAreaLevel4Part1 = findViewById(R.id.buttonClearAreaLevel4Part1);
        buttonRunLevel4Part1 = findViewById(R.id.buttonRunLevel4Part1);
        buttonProceedLevel4Part1 = findViewById(R.id.buttonProceedLevel4Part1);

        star1Level4Part1 = findViewById(R.id.star1Level4Part1);
        star2Level4Part1 = findViewById(R.id.star2Level4Part1);
        star3Level4Part1 = findViewById(R.id.star3Level4Part1);

        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        randomMode = intent.getBooleanExtra("randomMode", false);
        if (randomMode){
            arrayLevels = intent.getIntArrayExtra("arrayLevels");
            currentLevelRandom = intent.getIntExtra("currentLevelRandom", 0);
            randomDuration = intent.getIntExtra("randomDuration", 0);
            score = intent.getDoubleExtra("score", 0);

            seconds = randomDuration;
        }

        buttonProceedLevel4Part1.setVisibility(View.GONE);

        star1Level4Part1.setImageResource(R.drawable.star_filled);
        star2Level4Part1.setImageResource(R.drawable.star_filled);
        star3Level4Part1.setImageResource(R.drawable.star_filled);

        buttonClearAreaLevel4Part1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCode();
            }
        });

        if (offlineMode) {
            playerid = intent.getStringExtra("playerid");

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if(seconds > 0){
                        seconds--;
                        int mins = seconds / 60;
                        int secs = seconds % 60;
                        updateTimer();
                        progressStars();
                        handler.postDelayed(this, 1000); // Repeat every second

                    } else {
                        stopTimer();
                        score = 0;
                        stars = 0;
                        Intent intent = new Intent(Level4.this, ResultActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("offlineMode", true);
                        intent.putExtra("score",score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }

                }
            };
            handler.post(runnable); // Start the timer

            buttonGoBackLevel4Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    Intent intent = new Intent(Level4.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                    stopTimer();
                }
            });

            buttonRunLevel4Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer1();
                    resetCode();
                }
            });

        } else {

            buttonGoBackLevel4Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    Intent intent = new Intent(Level4.this, MainActivity.class);
                    startActivity(intent);
                    stopTimer();
                }
            });

            buttonRunLevel4Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer1();
                    resetCode();
                }
            });

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

            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Httplink.httpLink)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

            accountAPI = retrofit.create(AccountAPI.class);

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if(seconds > 0){
                        seconds--;
                        int mins = seconds / 60;
                        int secs = seconds % 60;
                        updateTimer();
                        progressStars();
                        handler.postDelayed(this, 1000); // Repeat every second

                    } else {
                        stopTimer();
                        score = 0;
                        stars = 0;
                        Intent intent = new Intent(Level4.this, ResultActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("score",score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }

                }
            };
            handler.post(runnable); // Start the timer
        }


    }

    // Optional method to check the user's answer (you can customize this logic)
    private void checkAnswer1() {
        playerAnswer = codeSnippetEditTextLevel4Part1.getText().toString().trim();

        // Normalize newlines and remove escape characters for better comparison
        String formattedCorrectAnswer = correctAnswer.replace("\\n", "\n")
                .replace("&quot;", "\"");

        if (playerAnswer.equals(formattedCorrectAnswer)) {
            textResultsLevel4Part1.setText("Name: Alice\nAge: 25\nheight: 5.7\nisstudent: 1");
            Toast.makeText(this, "Correct!Congrats!", Toast.LENGTH_LONG).show();
            buttonProceedLevel4Part1.setVisibility(View.VISIBLE);
            stopTimer();

            //Part2
            setupPart2();

            buttonClearAreaLevel4Part2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetCode();
                    textResultsLevel4Part2.setText(" ");
                }
            });

            if(offlineMode){
                buttonRunLevel4Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswer2Offline();
                        resetCode2();
                    }
                });
                buttonGoBackLevel4Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopTimer();
                        Intent intent = new Intent(Level4.this, MainActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("offlineMode", true);
                        startActivity(intent);
                    }
                });

                buttonProceedLevel4Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getApplicationContext(), ResultActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("offlineMode", true);
                        intent.putExtra("score", score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }
                });

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(seconds > 0){
                            seconds--;
                            int mins = seconds / 60;
                            int secs = seconds % 60;
                            updateTimer2();
                            progressStars2();
                            handler.postDelayed(this, 1000); // Repeat every second

                        } else {
                            stopTimer();
                            score = 0;
                            stars = 0;
                            Intent intent = new Intent(Level4.this, ResultActivity.class);
                            intent.putExtra("playerid", playerid);
                            intent.putExtra("level_id", level_id);
                            intent.putExtra("offlineMode", true);
                            intent.putExtra("score",score);
                            intent.putExtra("stars", stars);
                            intent.putExtra("CompletionTime", completedTime);
                            startActivity(intent);
                        }

                    }
                };
                handler.post(runnable); // Start the timer
            } else {

                buttonProceedLevel4Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getApplicationContext(), ResultActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("score", score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }
                });

                buttonRunLevel4Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswer2();
                        resetCode2();
                    }
                });

                buttonGoBackLevel4Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopTimer();
                        Intent intent = new Intent(Level4.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(seconds > 0){
                            seconds--;
                            int mins = seconds / 60;
                            int secs = seconds % 60;
                            updateTimer2();
                            progressStars2();
                            handler.postDelayed(this, 1000); // Repeat every second

                        } else {
                            stopTimer();
                            score = 0;
                            stars = 0;
                            Intent intent = new Intent(Level4.this, ResultActivity.class);
                            intent.putExtra("user_id", user_id);
                            intent.putExtra("level_id", level_id);
                            intent.putExtra("score",score);
                            intent.putExtra("stars", stars);
                            intent.putExtra("CompletionTime", completedTime);
                            startActivity(intent);
                        }

                    }
                };
                handler.post(runnable); // Start the timer

            }

        } else {
            textResultsLevel4Part1.setText("Syntax Error:Invalid Syntax");
            mistakes++;
        }
    }

    //for the timer
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop the timer when activity is destroyed
    }

    private void stopTimer() {
        handler.removeCallbacks(runnable);
        Toast.makeText(getApplicationContext(), "Stopped the Handler", Toast.LENGTH_SHORT).show();
    }

    private void updateTimer() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerTextViewLevel4Part1.setText(String.format("%02d:%02d", mins, secs));
    }

    private void updateTimer2() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerTextViewLevel4Part2.setText(String.format("%02d:%02d", mins, secs));
    }

    private void progressStars() {
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel4Part1.setText("Progress: " + intProgress + "%");

            switch (seconds) {
                case 115:
                    Random random = new Random();
                    score = score - random.nextInt(5) + 1;
                    break;

                case 110:
                    score = score - 20;
                case 102:
                    score = score - 30;
                    break;
                case 82:
                    score = score - 80;
                    break;
                case 62:
                    score = score - 130;
                    break;
                case 42:
                    score = score - 180;
                    break;
                case 22:
                    score = score - 200;
                default:
                    break;
            }

            toMinusMistakes = mistakes * 100;
            score = score - toMinusMistakes;

            mistakes = 0;

            if (score >= 701 && score <= 1000) {
                stars = 3;

            } else if (score >= 301 && score <= 700) {
                stars = 2;
                star3Level4Part1.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level4Part1.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star2Level4Part1.setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void progressStars2() {
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel4Part2.setText("Progress: " + intProgress + "%");

            switch (seconds) {
                case 115:
                    Random random = new Random();
                    score = score - random.nextInt(5) + 1;
                    break;

                case 110:
                    score = score - 20;
                case 102:
                    score = score - 30;
                    break;
                case 82:
                    score = score - 80;
                    break;
                case 62:
                    score = score - 130;
                    break;
                case 42:
                    score = score - 180;
                    break;
                case 22:
                    score = score - 200;
                default:
                    break;
            }

            toMinusMistakes = mistakes * 100;
            score = score - toMinusMistakes;

            mistakes = 0;

            if (score >= 701 && score <= 1000) {
                stars = 3;

            } else if (score >= 301 && score <= 700) {
                stars = 2;
                star3Level4Part2.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level4Part2.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star2Level4Part2.setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void resetCode(){
        codeSnippetEditTextLevel4Part1.setText("___ Declaring variables and assigning values\\nname = _______\\nage = _______\\nheight = _______\\nisstudent = _______\\n\\n___ Printing the values of the variables using the print() function\\n\\nprint(&quot;Name: &quot;, name)\\nprint(&quot;Age: &quot;, age)\\nprint(&quot;Height: &quot;, height)\\nprint(&quot;Is Student: &quot;, isstudent)");
    }

    private void setupPart2(){
        setContentView(R.layout.activity_level4_part2);

        timerTextViewLevel4Part2 = findViewById(R.id.timerTextLevel4Part2);
        textResultsLevel4Part2 = findViewById(R.id.textResultLevel4Part2);
        progressTextLevel4Part2 = findViewById(R.id.progressTextLevel4Part2);

        codeSnippetEditTextLevel4Part2 = findViewById(R.id.codeSnippetLevel4Part2);

        buttonGoBackLevel4Part2 = findViewById(R.id.buttonGoBackLevel4Part2);
        buttonClearAreaLevel4Part2 = findViewById(R.id.buttonClearAreaLevel4Part2);
        buttonRunLevel4Part2 = findViewById(R.id.buttonRunLevel4Part2);
        buttonProceedLevel4Part2 = findViewById(R.id.buttonProceedLevel4Part2);

        star1Level4Part2 = findViewById(R.id.star1Level4Part2);
        star2Level4Part2 = findViewById(R.id.star2Level4Part2);
        star3Level4Part2 = findViewById(R.id.star3Level4Part2);

        star1Level4Part2.setImageResource(R.drawable.star_filled);
        star2Level4Part2.setImageResource(R.drawable.star_filled);
        star3Level4Part2.setImageResource(R.drawable.star_filled);

        buttonProceedLevel4Part2.setVisibility(View.GONE);

    }

    private void resetCode2(){
        codeSnippetEditTextLevel4Part2.setText("x = 10\ny = 5\n\nsum_result = x _______ y\nproduct = x _______ y\nquotient = x _______ y\nremainder = x _______ y\n\nprint(&quot;Sum:&quot;, sum_result)\nprint(&quot;Product:&quot;, product)\nprint(&quot;Quotient:&quot;, quotient)\nprint(&quot;Remainder:&quot;, remainder)");
    }

    private void checkAnswer2(){
        playerAnswer = codeSnippetEditTextLevel4Part2.getText().toString().trim();

        // Normalize newlines and remove escape characters for better comparison
        String formattedCorrectAnswer = correctAnswer2.replace("\\n", "\n")
                .replace("&quot;", "\"");

        if (playerAnswer2.equals(formattedCorrectAnswer)){
            textResultsLevel4Part2.setText("Sum: 15\nProduct: 5\nQuotient: 2\nRemainder: 0");
            Toast.makeText(this, "Correct!Congrats!", Toast.LENGTH_LONG).show();
            if(randomMode){
                stopTimer();
                if(currentLevelRandom == 8){
                    completedTime = 600 - seconds;

                    intent = new Intent(Level4.this, ResultActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("level_id", "Randomness");
                    intent.putExtra("score", score);
                    intent.putExtra("stars", stars);
                    intent.putExtra("CompletionTime", completedTime);
                    startActivity(intent);

                } else {
                    currentLevelRandom++;
                    int getLevel = arrayLevels[currentLevelRandom];

                    intent = new Intent(Level4.this, getLevelClass(getLevel));
                    intent.putExtra("score", score);
                    intent.putExtra("arrayLevels", arrayLevels);
                    intent.putExtra("randomMode", true);
                    intent.putExtra("currentLevelRandom", currentLevelRandom);
                    intent.putExtra("randomDuration", seconds);
                    intent.putExtra("score", score);
                    startActivity(intent);
                }

            } else {
                completedTime = 100 - seconds;

                //to save user progress
                Call<JsonObject> call = accountAPI.saveProgress(user_id, level_id, status);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject jsonObject = response.body();
                            int success = jsonObject.get("success").getAsInt();
                            if (success == 1) {
                                Toast.makeText(getApplicationContext(), "User progress saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = "Server Error: User Progress Not saved";
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Server Error: Empty response.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e(TAG, "Failed to Fetch data Number 1", t);
                        Toast.makeText(getApplicationContext(), "Failed to Fetch data, No Response", Toast.LENGTH_SHORT).show();
                    }
                });

                //to save level progress of user
                call = accountAPI.saveLevelProgress(user_id, level_id, score, stars, completedTime);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject jsonObject = response.body();
                            int success = jsonObject.get("success").getAsInt();
                            if (success == 1) {
                                Toast.makeText(getApplicationContext(), "Level Progress Saved Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                String errorMsg = "Server Error: Level Progress Not saved";
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Server Error: Empty response.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e(TAG, "Failed to Fetch data Number 2", t);
                        Toast.makeText(getApplicationContext(), "Failed to Fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            textResultsLevel4Part2.setText("Syntax Error:Invalid Syntax");
            mistakes++;
        }
    }

    private void checkAnswer2Offline(){
        playerAnswer2 = codeSnippetEditTextLevel4Part2.getText().toString().trim();

        // Normalize newlines and remove escape characters for better comparison
        String formattedCorrectAnswer = correctAnswer2.replace("\\n", "\n")
                .replace("&quot;", "\"");

        if (playerAnswer2.equals(formattedCorrectAnswer)){
            textResultsLevel4Part2.setText("Results:\nSum: 15\nProduct: 5\nQuotient: 2\nRemainder: 0");
            if(randomMode){
                stopTimer();
                if(currentLevelRandom == 8){
                    completedTime = 600 - seconds;

                    intent = new Intent(Level4.this, ResultActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    intent.putExtra("level_id", "Randomness");
                    intent.putExtra("score", score);
                    intent.putExtra("stars", stars);
                    intent.putExtra("CompletionTime", completedTime);
                    startActivity(intent);

                    try {
                        sharedPreferences = getSharedPreferences("OfflineLevelRandomData", MODE_PRIVATE);
                        SharedPreferences sharedPreferencesPlayer = getSharedPreferences("OfflineData", MODE_PRIVATE);

                        playername = sharedPreferencesPlayer.getString("playerid" + playerid, "Player");
                        // Step 2: Create an editor to write data
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        // Step 3: Add key-value pairs
                        editor.putString("playerid" + playerid, playername);
                        editor.putString("player" + playerid + "_score", Double.toString(score));
                        editor.putString("player" + playerid + "_stars", Double.toString(stars));
                        editor.putString("player" + playerid + "_completedTime", Double.toString(completedTime));
                        editor.apply();

                        Log.d("SharedPreferences", "Saving Data: playerid" + playerid + " = " + playername);
                        Log.d("SharedPreferences", "Saving Data: player" + playerid + "_score = " + score);
                        Log.d("SharedPreferences", "Saving Data: player" + playerid + "_stars = " + stars);
                        Log.d("SharedPreferences", "Saving Data: player" + playerid + "_completedTime = " + completedTime);


                        Toast.makeText(getApplicationContext(), "Random Mode Completion Saved Successfully", Toast.LENGTH_LONG).show();


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to save level progress", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    currentLevelRandom++;
                    int getLevel = arrayLevels[currentLevelRandom];

                    intent = new Intent(Level4.this, getLevelClass(getLevel));
                    intent.putExtra("score", score);
                    intent.putExtra("arrayLevels", arrayLevels);
                    intent.putExtra("randomMode", true);
                    intent.putExtra("currentLevelRandom", currentLevelRandom);
                    intent.putExtra("randomDuration", seconds);
                    intent.putExtra("offlineMode", true);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("score", score);
                    startActivity(intent);
                }

            } else {
                stopTimer();
                progressStars2();
                completedTime = 100 - seconds;

                try {
                    sharedPreferences = getSharedPreferences("OfflineLevel4Data", MODE_PRIVATE);
                    SharedPreferences sharedPreferencesPlayer = getSharedPreferences("OfflineData", MODE_PRIVATE);

                    playername = sharedPreferencesPlayer.getString("playerid" + playerid, "Player");
                    // Step 2: Create an editor to write data
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Step 3: Add key-value pairs
                    editor.putString("playerid" + playerid, playername);
                    editor.putString("player" + playerid + "_score", Double.toString(score));
                    editor.putString("player" + playerid + "_stars", Double.toString(stars));
                    editor.putString("player" + playerid + "_completedTime", Double.toString(completedTime));
                    editor.putString("player" + playerid + "_status", "complete");
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "Level Progress Saved Successfully", Toast.LENGTH_LONG).show();

                    buttonProceedLevel4Part2.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to save level progress", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            textResultsLevel4Part2.setText("Syntax Error:Invalid Syntax");
            mistakes++;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
        finish();
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
                return null; // Return null if level doesn't exist
        }
    }

}
