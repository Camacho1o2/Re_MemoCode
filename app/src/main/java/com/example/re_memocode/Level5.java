package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Level5 extends AppCompatActivity {

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

    private String status = "completed";
    private int seconds = 150; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;

    int user_id;
    final int level_id = 5;
    private String[] correctOrder1 = {
            "number = int(input('Enter a number: '))",
            "if number % 2 == 0:",
            "print(f'{number} is Even')",
            "else:",
            "print(f'{number} is Odd')"
    };
    private String[] correctOrder2 = {
            "names = [\"Alice\", \"Bob\", \"Charlie\"]",
            "for name in names:",
            "    print(name)"

    };
    private String[] correctOrder3 = {
            "count = 0",
            "while count < 5:",
            "    print(\"Count is:\", count)",
            "    count += 1"
    };

    private String[] nullOrder1 = new String[5];

    private String[] playerOrder1 = new String[5];

    private String[] nullOrder2 = new String[3];

    private String[] playerOrder2 = new String[3];

    private String[] nullOrder3 = new String[6];

    private String[] playerOrder3 = new String[6];

    private int currentDropIndex1 = 0;
    private int currentDropIndex2 = 0;
    private int currentDropIndex3 = 0;

    //Part 1
    TextView progressTextLevel5Part1, timerTextLevel5Part1, resultTextLevel5Part1,
    draggableItem1Level5Part1, draggableItem2Level5Part1, draggableItem3Level5Part1, draggableItem5Level5Part1;
    EditText draggableItem4Level5Part1;
    LinearLayout dropAreaLevel5Part1;
    ImageButton buttonGoBackLevel5Part1, buttonClearAreaLevel5Part1, buttonRunLevel5Part1, buttonProceedLevel5Part1;
    ImageView star1Level5Part1, star2Level5Part1, star3Level5Part1;

    //Part 2
    TextView progressTextLevel5Part2, timerTextLevel5Part2, resultTextLevel5Part2,
            draggableItem1Level5Part2, draggableItem2Level5Part2, draggableItem4Level5Part2;

    EditText draggableItem3Level5Part2;
    LinearLayout dropAreaLevel5Part2;
    ImageButton buttonGoBackLevel5Part2, buttonClearAreaLevel5Part2, buttonRunLevel5Part2, buttonProceedLevel5Part2;
    ImageView star1Level5Part2, star2Level5Part2, star3Level5Part2;

    //Part 3
    TextView progressTextLevel5Part3, timerTextLevel5Part3, resultTextLevel5Part3,
            draggableItem1Level5Part3, draggableItem2Level5Part3, draggableItem3Level5Part3, draggableItem4Level5Part3,
    draggableItem5Level5Part3, draggableItem6Level5Part3;
    LinearLayout dropAreaLevel5Part3;
    ImageButton buttonGoBackLevel5Part3, buttonClearAreaLevel5Part3, buttonRunLevel5Part3, buttonProceedLevel5Part3;
    ImageView star1Level5Part3, star2Level5Part3, star3Level5Part3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level5_part1);

        setupPart1();

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

        setupDragAndDrop1();

        buttonClearAreaLevel5Part1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDroppedArea1();
            }
        });

        buttonRunLevel5Part1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer1();
            }
        });

        if(offlineMode){
            playerid = intent.getStringExtra("playerid");

            buttonGoBackLevel5Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    Intent intent = new Intent(Level5.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
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
                        updateTimer();
                        progressStars1();
                        handler.postDelayed(this, 1000); // Repeat every second

                    } else {
                        stopTimer();
                        score = 0;
                        stars = 0;
                        Intent intent = new Intent(Level5.this, ResultActivity.class);
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

            buttonGoBackLevel5Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    Intent intent = new Intent(Level5.this, MainActivity.class);
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
                        updateTimer();
                        progressStars1();
                        handler.postDelayed(this, 1000); // Repeat every second

                    } else {
                        stopTimer();
                        score = 0;
                        stars = 0;
                        Intent intent = new Intent(Level5.this, ResultActivity.class);
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
        timerTextLevel5Part1.setText(String.format("%02d:%02d", mins, secs));
    }

    private void updateTimer2() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerTextLevel5Part2.setText(String.format("%02d:%02d", mins, secs));
    }

    private void updateTimer3() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerTextLevel5Part3.setText(String.format("%02d:%02d", mins, secs));
    }

    private void progressStars1(){
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel5Part1.setText("Progress: " + intProgress + "%");

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
                star3Level5Part1.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level5Part1.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level5Part1.setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void progressStars2(){
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel5Part2.setText("Progress: " + intProgress + "%");

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
                star3Level5Part2.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level5Part2.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level5Part2.setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void progressStars3(){
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel5Part3.setText("Progress: " + intProgress + "%");

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
                star3Level5Part3.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level5Part3.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level5Part3.setImageResource(R.drawable.star_outline);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
        finish();
    }

    private void setupDragAndDrop1(){
        setDragListener(draggableItem1Level5Part1, "number = int(input('Enter a number: '))");
        setDragListener(draggableItem2Level5Part1, "print(f'{number} is Odd')");
        setDragListener(draggableItem3Level5Part1, "print(f'{number} is Even')");
        setDragListener(draggableItem5Level5Part1, "if number % 2 == 0:");

        dropAreaLevel5Part1.setOnDragListener(new DropAreaDragListener1());
    }

    private void setupDragAndDrop2(){
        setDragListener(draggableItem1Level5Part2, "for name in names:");
        setDragListener(draggableItem2Level5Part2, "names = [\"Alice\", \"Bob\", \"Charlie\"]");
        setDragListener(draggableItem4Level5Part2, "name = \"Alice\"");

        dropAreaLevel5Part2.setOnDragListener(new DropAreaDragListener2());
    }

    private void setupDragAndDrop3(){
        setDragListener(draggableItem1Level5Part3, "   print(\"Count is:\", count)");
        setDragListener(draggableItem2Level5Part3, "    count += 1");
        setDragListener(draggableItem3Level5Part3, "count = 0");
        setDragListener(draggableItem4Level5Part3, "    print(\"Count is:\", count)");
        setDragListener(draggableItem5Level5Part3, "   count += 1");
        setDragListener(draggableItem6Level5Part3, "while count < 5:");

        dropAreaLevel5Part3.setOnDragListener(new DropAreaDragListener3());
    }

    private void setupPart1(){
         progressTextLevel5Part1 = findViewById(R.id.progressTextLevel5Part1);
         timerTextLevel5Part1 = findViewById(R.id.timerTextLevel5Part1);
         resultTextLevel5Part1 = findViewById(R.id.resultTextLevel5Part1);

         draggableItem1Level5Part1 = findViewById(R.id.draggableItem1Level5Part1);
         draggableItem2Level5Part1 = findViewById(R.id.draggableItem2Level5Part1);
         draggableItem3Level5Part1 = findViewById(R.id.draggableItem3Level5Part1);
         draggableItem5Level5Part1 = findViewById(R.id.draggableItem5Level5Part1);
         draggableItem4Level5Part1 = findViewById(R.id.draggableItem4Level5Part1);

         dropAreaLevel5Part1 = findViewById(R.id.drop_area_Level5Part1);

         buttonGoBackLevel5Part1 = findViewById(R.id.buttonGoBackLevel5Part1);
         buttonClearAreaLevel5Part1 = findViewById(R.id.buttonClearAreaLevel5Part1);
         buttonRunLevel5Part1 = findViewById(R.id.buttonRunLevel5Part1);
         buttonProceedLevel5Part1 = findViewById(R.id.buttonProceedLevel5Part1);

         star1Level5Part1 = findViewById(R.id.star1Level5Part1);
         star2Level5Part1 = findViewById(R.id.star2Level5Part1);
         star3Level5Part1 = findViewById(R.id.star3Level5Part1);

         star1Level5Part1.setImageResource(R.drawable.star_filled);
         star2Level5Part1.setImageResource(R.drawable.star_filled);
         star3Level5Part1.setImageResource(R.drawable.star_filled);

         buttonProceedLevel5Part1.setVisibility(View.GONE);
    }

    private void setupPart2(){
        setContentView(R.layout.activity_level5_part2);

        progressTextLevel5Part2 = findViewById(R.id.progressTextLevel5Part2);
        timerTextLevel5Part2 = findViewById(R.id.timerTextLevel5Part2);
        resultTextLevel5Part2 = findViewById(R.id.resultTextLevel5Part2);

        draggableItem1Level5Part2 = findViewById(R.id.draggableItem1Level5Part2);
        draggableItem2Level5Part2 = findViewById(R.id.draggableItem2Level5Part2);
        draggableItem3Level5Part2 = findViewById(R.id.draggableItem3Level5Part2);
        draggableItem4Level5Part2 = findViewById(R.id.draggableItem4Level5Part2);

        dropAreaLevel5Part2 = findViewById(R.id.drop_area_Level5Part2);

        buttonGoBackLevel5Part2 = findViewById(R.id.buttonGoBackLevel5Part2);
        buttonClearAreaLevel5Part2 = findViewById(R.id.buttonClearAreaLevel5Part2);
        buttonRunLevel5Part2 = findViewById(R.id.buttonRunLevel5Part2);
        buttonProceedLevel5Part2 = findViewById(R.id.buttonProceedLevel5Part2);

        star1Level5Part2 = findViewById(R.id.star1Level5Part2);
        star2Level5Part2 = findViewById(R.id.star2Level5Part2);
        star3Level5Part2  = findViewById(R.id.star3Level5Part2);

        star1Level5Part1.setImageResource(R.drawable.star_filled);
        star2Level5Part1.setImageResource(R.drawable.star_filled);
        star3Level5Part1.setImageResource(R.drawable.star_filled);

        buttonProceedLevel5Part2.setVisibility(View.GONE);
    }

    private void setupPart3(){
        setContentView(R.layout.activity_level5_part3);

        progressTextLevel5Part3 = findViewById(R.id.progressTextLevel5Part3);
        timerTextLevel5Part3 = findViewById(R.id.timerTextLevel5Part3);
        resultTextLevel5Part3 = findViewById(R.id.resultsTextLevel5Part3);

        draggableItem1Level5Part3 = findViewById(R.id.draggableItem1Level5Part3);
        draggableItem2Level5Part3 = findViewById(R.id.draggableItem2Level5Part3);
        draggableItem3Level5Part3 = findViewById(R.id.draggableItem3Level5Part3);
        draggableItem4Level5Part3 = findViewById(R.id.draggableItem4Level5Part3);
        draggableItem5Level5Part3 = findViewById(R.id.draggableItem5Level5Part3);
        draggableItem6Level5Part3 = findViewById(R.id.draggableItem6Level5Part3);

        dropAreaLevel5Part3 = findViewById(R.id.drop_area_Level5Part3);

        buttonGoBackLevel5Part3 = findViewById(R.id.buttonGoBackLevel5Part3);
        buttonClearAreaLevel5Part3 = findViewById(R.id.buttonClearAreaLevel5Part3);
        buttonRunLevel5Part3 = findViewById(R.id.buttonRunLevel5Part3);
        buttonProceedLevel5Part3 = findViewById(R.id.buttonProceedLevel5Part3);

        star1Level5Part3 = findViewById(R.id.star1Level5Part3);
        star2Level5Part3 = findViewById(R.id.star2Level5Part3);
        star3Level5Part3 = findViewById(R.id.star3Level5Part3);

        star1Level5Part3.setImageResource(R.drawable.star_filled);
        star2Level5Part3.setImageResource(R.drawable.star_filled);
        star3Level5Part3.setImageResource(R.drawable.star_filled);

        buttonProceedLevel5Part3.setVisibility(View.GONE);
    }

    //To clear the drop area
    private void clearDroppedArea1(){
        // Remove all views from the dropArea
        dropAreaLevel5Part1.removeAllViews();

        // Reset the user order and the current drop index
        playerOrder1 = new String[correctOrder1.length];
        currentDropIndex1 = 0;

        // Optionally, reset any result message
        resultTextLevel5Part1.setText("Result:\n");
    }

    //To clear the drop area
    private void clearDroppedArea2(){
        // Remove all views from the dropArea
        dropAreaLevel5Part2.removeAllViews();

        // Reset the user order and the current drop index
        playerOrder2 = new String[correctOrder2.length];
        currentDropIndex2 = 0;

        // Optionally, reset any result message
        resultTextLevel5Part2.setText("Result:\n");
    }

    //To clear the drop area
    private void clearDroppedArea3(){
        // Remove all views from the dropArea
        dropAreaLevel5Part3.removeAllViews();

        // Reset the user order and the current drop index
        playerOrder3 = new String[correctOrder3.length];
        currentDropIndex3 = 0;

        // Optionally, reset any result message
        resultTextLevel5Part3.setText("Result:\n");
    }

    private void checkAnswer1(){
        if(Arrays.equals(playerOrder1, correctOrder1)){
            stopTimer();

            Toast.makeText(getApplicationContext(), "Correct! That is an if-else statement in Python.", Toast.LENGTH_SHORT).show();

            setupPart2();

            setupDragAndDrop2();

            buttonClearAreaLevel5Part2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearDroppedArea2();
                }
            });

            buttonRunLevel5Part2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer2();
                    clearDroppedArea2();
                }
            });


            if(offlineMode){
                buttonGoBackLevel5Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Level5.this, MainActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("offlineMode", true);
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
                            Intent intent = new Intent(Level5.this, ResultActivity.class);
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
                buttonGoBackLevel5Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Level5.this, MainActivity.class);
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
                            Intent intent = new Intent(Level5.this, ResultActivity.class);
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
        }else if(Arrays.equals(playerOrder1, nullOrder1)){
            resultTextLevel5Part1.setText("Result:\nPlease put the blocks in the box.");
            mistakes++;
        } else {
            resultTextLevel5Part1.setText("Result:\nSyntax Error:Invalid Syntax");
            mistakes++;
        }
    }

    private void checkAnswer2(){
        if(Arrays.equals(playerOrder2, correctOrder2)){
            stopTimer();

            Toast.makeText(getApplicationContext(), "Correct! That is an example of a while conditional statement in Python.", Toast.LENGTH_SHORT).show();

            setupPart3();

            setupDragAndDrop3();

            buttonClearAreaLevel5Part3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearDroppedArea3();
                }
            });

            buttonRunLevel5Part3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer3();
                }
            });


            if(offlineMode){
                buttonGoBackLevel5Part3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Level5.this, MainActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("offlineMode", true);
                        startActivity(intent);
                    }
                });

                buttonProceedLevel5Part3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Level5.this, ResultActivity.class);

                        intent.putExtra("playerid", playerid);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("offlineMode", true);
                        intent.putExtra("score",score);
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
                            updateTimer3();
                            progressStars3();
                            handler.postDelayed(this, 1000); // Repeat every second

                        } else {
                            stopTimer();
                            score = 0;
                            stars = 0;
                            Intent intent = new Intent(Level5.this, ResultActivity.class);
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
                buttonGoBackLevel5Part3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Level5.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                buttonProceedLevel5Part3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Level5.this, ResultActivity.class);

                        intent.putExtra("user_id", user_id);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("score",score);
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
                            updateTimer3();
                            progressStars3();
                            handler.postDelayed(this, 1000); // Repeat every second

                        } else {
                            stopTimer();
                            score = 0;
                            stars = 0;
                            Intent intent = new Intent(Level5.this, ResultActivity.class);
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
        } else if(Arrays.equals(playerOrder2, nullOrder2)){
            resultTextLevel5Part2.setText("Result:\nPlease put the blocks in the box.");
            mistakes++;
        } else {
            resultTextLevel5Part2.setText("Result:\nSyntax Error:Invalid Syntax");
            mistakes++;
        }
    }

    private void checkAnswer3(){
        if(Arrays.equals(playerOrder3, correctOrder3)){
            stopTimer();
            resultTextLevel5Part3.setText("Result:\nCount is: 0\nCount is: 1\nCount is: 2\nCount is: 3\nCount is: 4\nCount is: 5");
                progressStars3();
                completedTime = 150 - seconds;

                if (offlineMode) {
                    if(randomMode){
                        if(currentLevelRandom == 8){
                            completedTime = 600 - seconds;

                            intent = new Intent(Level5.this, ResultActivity.class);
                            intent.putExtra("user_id", user_id);
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

                            intent = new Intent(Level5.this, getLevelClass(getLevel));
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
                        sharedPreferences = getSharedPreferences("OfflineLevel5Data", MODE_PRIVATE);
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

                        buttonProceedLevel5Part3.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (randomMode) {
                        if(currentLevelRandom == 8){
                            completedTime = 600 - seconds;

                            intent = new Intent(Level5.this, ResultActivity.class);
                            intent.putExtra("user_id", user_id);
                            intent.putExtra("level_id", "Randomness");
                            intent.putExtra("score", score);
                            intent.putExtra("stars", stars);
                            intent.putExtra("CompletionTime", completedTime);
                            startActivity(intent);

                        } else {
                            currentLevelRandom++;
                            int getLevel = arrayLevels[currentLevelRandom];

                            intent = new Intent(Level5.this, getLevelClass(getLevel));
                            intent.putExtra("score", score);
                            intent.putExtra("arrayLevels", arrayLevels);
                            intent.putExtra("randomMode", true);
                            intent.putExtra("currentLevelRandom", currentLevelRandom);
                            intent.putExtra("randomDuration", seconds);
                            intent.putExtra("score", score);
                            startActivity(intent);
                        }

                    } else {
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
            }

        } else if(Arrays.equals(playerOrder3, nullOrder3)){
            resultTextLevel5Part3.setText("Result:\nPlease put the blocks in the box.");
            mistakes++;
        } else {
            resultTextLevel5Part3.setText("Result:\nSyntax Error:Invalid Syntax");
            mistakes++;
        }
    }

    // DropAreaDragListener handles dropping in the drop area
    private class DropAreaDragListener1 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropAreaLevel5Part1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropAreaLevel5Part1.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    setEditTextDragListener(draggableItem4Level5Part1, draggableItem4Level5Part1.getText().toString());
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    if (currentDropIndex1 < playerOrder1.length) {
                        playerOrder1[currentDropIndex1] = droppedData;
                        currentDropIndex1++;

                        // Display the dropped item in the drop area
                        TextView droppedView = new TextView(Level5.this);
                        droppedView.setText(droppedData);
                        droppedView.setPadding(10, 10, 10, 10);
                        droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                        droppedView.setTextColor(getResources().getColor(R.color.black));
                        dropAreaLevel5Part1.addView(droppedView);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropAreaLevel5Part1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    // DropAreaDragListener handles dropping in the drop area
    private class DropAreaDragListener2 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropAreaLevel5Part2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropAreaLevel5Part2.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    setEditTextDragListener(draggableItem3Level5Part2, draggableItem3Level5Part2.getText().toString());
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    if (currentDropIndex2 < playerOrder2.length) {
                        playerOrder2[currentDropIndex2] = droppedData;
                        currentDropIndex2++;

                        // Display the dropped item in the drop area
                        TextView droppedView = new TextView(Level5.this);
                        droppedView.setText(droppedData);
                        droppedView.setPadding(10, 10, 10, 10);
                        droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                        droppedView.setTextColor(getResources().getColor(R.color.black));
                        dropAreaLevel5Part2.addView(droppedView);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropAreaLevel5Part2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    // DropAreaDragListener handles dropping in the drop area
    private class DropAreaDragListener3 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropAreaLevel5Part3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropAreaLevel5Part3.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    if (currentDropIndex3 < playerOrder3.length) {
                        playerOrder3[currentDropIndex3] = droppedData;
                        currentDropIndex3++;

                        // Display the dropped item in the drop area
                        TextView droppedView = new TextView(Level5.this);
                        droppedView.setText(droppedData);
                        droppedView.setPadding(10, 10, 10, 10);
                        droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                        droppedView.setTextColor(getResources().getColor(R.color.black));
                        dropAreaLevel5Part3.addView(droppedView);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropAreaLevel5Part3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    private void setDragListener(final TextView textView, final String level5Part1Snippet) {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("block of code", level5Part1Snippet);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
        });
    }

    private void setEditTextDragListener(final EditText textView, final String level5Part1Snippet) {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("block of code", level5Part1Snippet);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
        });
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