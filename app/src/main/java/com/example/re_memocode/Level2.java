package com.example.re_memocode;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Level2 extends AppCompatActivity {

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
    private int seconds = 120; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;

    int user_id;
    final int level_id = 2;

    //First Part
    TextView textTimerLevel2Part1, textResultLevel2Part1, progressTextLevel2Part1,
            drag_item_wait_part1,drag_item_pay_part1, drag_item_place_order_part1, drag_item_eat_food_part1,
            drag_item_look_menu_part1, drag_item_walk_counter_part1;

    ImageView star1Level2Part1 , star2Level2Part1, star3Level2Part1;
    LinearLayout dropAreaLevel2Part1;
    ImageButton btnProceedLevel2Part1, btnGoBackLevel2Part1, btnClearAreaLevel2Part1, btnRunLevel2Part1;

    //Second Part
    TextView textTimerLevel2Part2, textResultLevel2Part2, progressTextLevel2Part2,
            drag_item_start_Level2_Part2, drag_item_look_menu_Level2_Part2,
            drag_item_enter_restaurant_Level2_Part2, drag_item_walk_counter_Level2_Part2,
            drag_item_place_order_Level2_Part2, drag_item_wait_order_Level2_Part2,
            drag_item_pay_Level2_Part2, drag_item_take_food_Level2_Part2,
            drag_item_end_Level2_Part2;

    ImageView star1Level2Part2 , star2Level2Part2, star3Level2Part2;
    LinearLayout dropAreaLevel2Part2;
    ImageButton btnProceedLevel2Part2, btnGoBackLevel2Part2, btnClearAreaLevel2Part2, btnRunLevel2Part2;

    private String[] correctOrder1 = {
            "Look at the menu to decide what you want.",
            "Walk to the counter.",
            "Place your order at the counter.",
            "Pay for the food.",
            "Wait for your order to be served.",
            "Eat your food.",
    };

    private String[] correctOrder2 = {
            "START",
            "Enter the fast-food restaurant.",
            "Look at the menu and decide what you want to order.",
            "Walk to the counter.",
            "Place your order at the counter.",
            "Pay for the food.",
            "Wait until your order is ready.",
            "Take your food and eat.",
            "END",
    };

    private String[] nullOrder = new String[6];

    private String[] playerOrder1 = new String[6];

    private String[] nullOrder2 = new String[9];

    private String[] playerOrder2 = new String[9];// Placeholder for user-selected order

    private int currentDropIndex = 0; // Keeps track of the drop index in userOrder

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level2part1);

        textTimerLevel2Part1 = findViewById(R.id.timerTextLevel2Part1);
        textResultLevel2Part1 = findViewById(R.id.resultTextLevel2Part1);
        progressTextLevel2Part1 = findViewById(R.id.progressTextLevel2Part1);

        star1Level2Part1 = findViewById(R.id.star1Level2Part1);
        star2Level2Part1 = findViewById(R.id.star2Level2Part1);
        star3Level2Part1 = findViewById(R.id.star3Level2Part1);

        dropAreaLevel2Part1 = findViewById(R.id.drop_areaLevel2Part1);

        btnProceedLevel2Part1 = findViewById(R.id.buttonProceedLevel2Part1);
        btnGoBackLevel2Part1 = findViewById(R.id.buttonGoBackLevel2Part1);
        btnClearAreaLevel2Part1 = findViewById(R.id.buttonClearAreaLevel2Part1);
        btnRunLevel2Part1 = findViewById(R.id.buttonRunLevel2Part1);

        drag_item_wait_part1 = findViewById(R.id.drag_item_wait_part1);
        drag_item_pay_part1 = findViewById(R.id.drag_item_pay_part1);
        drag_item_place_order_part1 = findViewById(R.id.drag_item_place_order_part1);
        drag_item_eat_food_part1 = findViewById(R.id.drag_item_eat_food_part1);
        drag_item_look_menu_part1 = findViewById(R.id.drag_item_look_menu_part1);
        drag_item_walk_counter_part1 = findViewById(R.id.drag_item_walk_counter_part1);

        star1Level2Part1.setImageResource(R.drawable.star_filled);
        star2Level2Part1.setImageResource(R.drawable.star_filled);
        star3Level2Part1.setImageResource(R.drawable.star_filled);

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

        setupDragAndDrop();

        btnProceedLevel2Part1.setVisibility(GONE);

        btnClearAreaLevel2Part1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDroppedArea();
            }
        });

        //to check if offlineMode is on
        if(offlineMode){
            playerid = getIntent().getStringExtra("playerid");

            btnGoBackLevel2Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    Intent intent = new Intent(Level2.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            btnRunLevel2Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswerLevel2Part1Offline();
                    clearDroppedArea();
                }
            });

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (seconds > 0) {
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
                        completedTime = 0;
                        Intent intent = new Intent(Level2.this, ResultActivity.class);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("offlineMode", true);
                        intent.putExtra("score", score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }

                }
            };
            handler.post(runnable);

        } else {

            //Session Token
            SessionManager sessionManager = new SessionManager(Level2.this);
            if (sessionManager.hasSessionToken()) {
                // Check if a session exists
                if (sessionManager.hasSessionToken()) {
                    String token = sessionManager.getSessionToken();
                    user_id = Integer.parseInt(sessionManager.getUserId());
                }
            } else {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }

            //Initialize API
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Httplink.httpLink)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            accountAPI = retrofit.create(AccountAPI.class);

            btnRunLevel2Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswerLevel2Part1();
                    clearDroppedArea();
                }
            });

            btnGoBackLevel2Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    Intent intent = new Intent(Level2.this, MainActivity.class);

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
                        progressStars();
                        handler.postDelayed(this, 1000); // Repeat every second

                    } else {
                        stopTimer();
                        score = 0;
                        stars = 0;
                        Intent intent = new Intent(Level2.this, ResultActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("score",score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }
                }
            };
            handler.post(runnable);
        }

    }

    private void setupDragAndDrop (){
        setDragListener(drag_item_wait_part1, "Wait for your order to be served.");
        setDragListener(drag_item_pay_part1, "Pay for the food.");
        setDragListener(drag_item_place_order_part1, "Place your order at the counter.");
        setDragListener(drag_item_eat_food_part1, "Eat your food.");
        setDragListener(drag_item_look_menu_part1, "Look at the menu to decide what you want.");
        setDragListener(drag_item_walk_counter_part1, "Walk to the counter.");

        dropAreaLevel2Part1.setOnDragListener(new DropAreaDragListener());
    }

    private void setDragListener(final TextView textView, final String level2Part1Snippet) {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("algorithm", level2Part1Snippet);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
        });
    }

    // DropAreaDragListener handles dropping in the drop area
    private class DropAreaDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropAreaLevel2Part1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropAreaLevel2Part1.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    if (currentDropIndex < playerOrder1.length) {
                        playerOrder1[currentDropIndex] = droppedData;
                        currentDropIndex++;

                        // Display the dropped item in the drop area
                        TextView droppedView = new TextView(Level2.this);
                        droppedView.setText(droppedData);
                        droppedView.setPadding(10, 10, 10, 10);
                        droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                        droppedView.setTextColor(getResources().getColor(R.color.black));
                        dropAreaLevel2Part1.addView(droppedView);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropAreaLevel2Part1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    //Online Checking
    private void checkAnswerLevel2Part1(){
        if(Arrays.equals(playerOrder1, correctOrder1)) {
            stopTimer();
            //The player is correct
            Toast.makeText(getApplicationContext(), "Correct! That is an example of an algorithm.", Toast.LENGTH_SHORT).show();
            setupPart2();

            setupDragAndDrop2();

            btnClearAreaLevel2Part2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearDroppedArea2();
                }
            });

            btnProceedLevel2Part2.setOnClickListener(new View.OnClickListener() {
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

            btnGoBackLevel2Part2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Level2.this, ResultActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("level_id", level_id);
                    intent.putExtra("score", score);
                    intent.putExtra("stars", stars);
                    intent.putExtra("CompletionTime", completedTime);
                    startActivity(intent);
                }
            });


            btnRunLevel2Part2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswerLevel2Part2();
                    clearDroppedArea2();
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
                        Intent intent = new Intent(Level2.this, ResultActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("score",score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }
                }
            };
            handler.post(runnable);


        }else if(Arrays.equals(playerOrder1, nullOrder)){
            textResultLevel2Part1.setText("Result:\nPlease put the blocks in the box.");
            mistakes++;
        } else {
            textResultLevel2Part1.setText("Result:\nIncorrect. Try again.");
            mistakes++;
        }
    }

    //Offline Checking
    private void checkAnswerLevel2Part1Offline(){
        if(Arrays.equals(playerOrder1, correctOrder1)) {

            stopTimer();
            //The player is correct
            Toast.makeText(getApplicationContext(), "Correct! That is an example of an algorithm.", Toast.LENGTH_SHORT).show();

            setupPart2();

            setupDragAndDrop2();

            btnClearAreaLevel2Part2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearDroppedArea2();
                }
            });

                btnProceedLevel2Part2.setOnClickListener(new View.OnClickListener() {
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

                btnGoBackLevel2Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getApplicationContext(), ResultActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("offlineMode", true);
                        startActivity(intent);
                    }
                });


                btnRunLevel2Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswerLevel2Part2Offline();
                        clearDroppedArea2();
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
                        Intent intent = new Intent(Level2.this, ResultActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("level_id", level_id);
                        getIntent().putExtra("offlineMode", true);
                        intent.putExtra("score",score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }
                }
            };
            handler.post(runnable);


        }else if(Arrays.equals(playerOrder1, nullOrder)){
            textResultLevel2Part1.setText("Result:\nPlease put the blocks in the box.");
            mistakes++;
        } else {
            textResultLevel2Part1.setText("Result:\nIncorrect. Try again.");
            mistakes++;
        }
    }

    //To clear the drop area
    private void clearDroppedArea(){
        // Remove all views from the dropArea
        dropAreaLevel2Part1.removeAllViews();

        // Reset the user order and the current drop index
        playerOrder1 = new String[correctOrder1.length];
        currentDropIndex = 0;

        // Optionally, reset any result message
        textResultLevel2Part1.setText("Result:\n");
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
        textTimerLevel2Part1.setText(String.format("%02d:%02d", mins, secs));
    }

    private void progressStars() {
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel2Part1.setText("Progress: " + intProgress + "%");

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
                star3Level2Part1.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level2Part1.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level2Part1.setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void setupDragAndDrop2 (){

        setDragListener(drag_item_start_Level2_Part2, "START");
        setDragListener(drag_item_look_menu_Level2_Part2, "Look at the menu and decide what you want to order.");
        setDragListener(drag_item_enter_restaurant_Level2_Part2, "Enter the fast-food restaurant.");
        setDragListener(drag_item_walk_counter_Level2_Part2, "Walk to the counter.");
        setDragListener(drag_item_place_order_Level2_Part2, "Place your order at the counter.");
        setDragListener(drag_item_wait_order_Level2_Part2, "Wait until your order is ready.");
        setDragListener(drag_item_pay_Level2_Part2, "Pay for the food.");
        setDragListener(drag_item_take_food_Level2_Part2, "Take your food and eat.");
        setDragListener(drag_item_end_Level2_Part2, "END");

        dropAreaLevel2Part2.setOnDragListener(new DropAreaDragListener2());
    }

    // DropAreaDragListener handles dropping in the drop area
    private class DropAreaDragListener2 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropAreaLevel2Part2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropAreaLevel2Part2.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    if (currentDropIndex < playerOrder2.length) {
                        playerOrder2[currentDropIndex] = droppedData;
                        currentDropIndex++;

                        // Display the dropped item in the drop area
                        TextView droppedView = new TextView(Level2.this);
                        droppedView.setText(droppedData);
                        droppedView.setPadding(10, 10, 10, 10);
                        droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                        droppedView.setTextColor(getResources().getColor(R.color.black));
                        dropAreaLevel2Part2.addView(droppedView);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropAreaLevel2Part2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    //Online Checking
    private void checkAnswerLevel2Part2(){
        if(Arrays.equals(playerOrder1, correctOrder1)) {
            //The player is correct
            textResultLevel2Part1.setText("Result:\nCorrect! That is a correct algorithm. Good Job.");
            if(randomMode){
                stopTimer();
                if(currentLevelRandom == 8){
                    completedTime = 600 - seconds;

                    intent = new Intent(Level2.this, ResultActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("level_id", "Randomness");
                    intent.putExtra("score", score);
                    intent.putExtra("stars", stars);
                    intent.putExtra("CompletionTime", completedTime);
                    startActivity(intent);


                } else {
                    currentLevelRandom++;
                    int getLevel = arrayLevels[currentLevelRandom];

                    intent = new Intent(Level2.this, getLevelClass(getLevel));
                    intent.putExtra("score", score);
                    intent.putExtra("arrayLevels", arrayLevels);
                    intent.putExtra("randomMode", true);
                    intent.putExtra("currentLevelRandom", currentLevelRandom);
                    intent.putExtra("randomDuration", seconds);
                    intent.putExtra("score", score);
                    startActivity(intent);
                }

            } else {
                btnProceedLevel2Part1.setVisibility(View.VISIBLE);
                stopTimer();
                completedTime = 120 - seconds;

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
                        Log.e(TAG, "Failed to Fetch data", t);
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
                        Log.e(TAG, "Failed to Fetch data", t);
                        Toast.makeText(getApplicationContext(), "Failed to Fetch data", Toast.LENGTH_SHORT).show();
                    }
                });

                btnProceedLevel2Part2.setVisibility(View.VISIBLE);
            }

        }else if(Arrays.equals(playerOrder1, nullOrder)){
            textResultLevel2Part1.setText("Result:\nPlease put the blocks in the box.");
            mistakes++;
        } else {
            textResultLevel2Part1.setText("Result:\nIncorrect. Try again.");
            mistakes++;
        }
    }

    //Offline Checking
    private void checkAnswerLevel2Part2Offline(){
        if(Arrays.equals(playerOrder2, correctOrder2)) {
            //The player is correct
            textResultLevel2Part1.setText("Result:\nCorrect! That is a correct algorithm. Good Job.");
            if(randomMode){
                stopTimer();
                if(currentLevelRandom == 8){
                    completedTime = 600 - seconds;

                    intent = new Intent(Level2.this, ResultActivity.class);
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

                    intent = new Intent(Level2.this, getLevelClass(getLevel));
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
                btnProceedLevel2Part1.setVisibility(View.VISIBLE);
                stopTimer();
                completedTime = 120 - seconds;

                try {
                    sharedPreferences = getSharedPreferences("OfflineLevel2Data", MODE_PRIVATE);
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
                    Log.d("SharedPreferences", "Saving Data: playerid" + playerid + " = " + playername);
                    Log.d("SharedPreferences", "Saving Data: player" + playerid + "_score = " + score);
                    Log.d("SharedPreferences", "Saving Data: player" + playerid + "_stars = " + stars);
                    Log.d("SharedPreferences", "Saving Data: player" + playerid + "_completedTime = " + completedTime);
                    Log.d("SharedPreferences", "Saving Data: player" + playerid + "_status = complete");


                    Toast.makeText(getApplicationContext(), "Level Progress Saved Successfully", Toast.LENGTH_LONG).show();

                    btnProceedLevel2Part2.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to save level progress", Toast.LENGTH_SHORT).show();
                }
            }

        }else if(Arrays.equals(playerOrder2, nullOrder2)){
            textResultLevel2Part1.setText("Result:\nPlease put the blocks in the box.");
            mistakes++;
        } else {
            textResultLevel2Part1.setText("Result:\nIncorrect. Try again.");
            mistakes++;
        }
    }

    //To clear the drop area
    private void clearDroppedArea2(){
        // Remove all views from the dropArea
        dropAreaLevel2Part2.removeAllViews();

        // Reset the user order and the current drop index
        playerOrder2 = new String[correctOrder2.length];
        currentDropIndex = 0;

        // Optionally, reset any result message
        textResultLevel2Part2.setText("Result:\n");
    }

    private void updateTimer2() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        textTimerLevel2Part2.setText(String.format("%02d:%02d", mins, secs));
    }

    private void progressStars2() {
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel2Part2.setText("Progress: " + intProgress + "%");

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
                star3Level2Part2.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level2Part2.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level2Part2.setImageResource(R.drawable.star_outline);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
        finish();
    }

    private void setupPart2(){
        setContentView(R.layout.activity_level2part2);

        textResultLevel2Part2 = findViewById(R.id.resultTextLevel2Part2);
        progressTextLevel2Part2 = findViewById(R.id.progressTextLevel2Part2);
        textTimerLevel2Part2 = findViewById(R.id.timerTextLevel2Part2);

        star1Level2Part2 = findViewById(R.id.star1Level2Part2);
        star2Level2Part2 = findViewById(R.id.star2Level2Part2);
        star3Level2Part2 = findViewById(R.id.star3Level2Part2);

        dropAreaLevel2Part2 = findViewById(R.id.drop_area_Level2_Part2);

        btnProceedLevel2Part2 = findViewById(R.id.buttonProceedLevel2Part2);
        btnGoBackLevel2Part2 = findViewById(R.id.buttonGoBackLevel2Part2);
        btnClearAreaLevel2Part2 = findViewById(R.id.buttonClearAreaLevel2Part2);
        btnRunLevel2Part2 = findViewById(R.id.buttonRunLevel2Part2);

        drag_item_start_Level2_Part2 = findViewById(R.id.drag_item_start_Level2_Part2);
        drag_item_look_menu_Level2_Part2 = findViewById(R.id.drag_item_look_menu_Level2_Part2);
        drag_item_enter_restaurant_Level2_Part2 = findViewById(R.id.drag_item_enter_restaurant_Level2_Part2);
        drag_item_walk_counter_Level2_Part2 = findViewById(R.id.drag_item_walk_counter_Level2_Part2);
        drag_item_place_order_Level2_Part2 = findViewById(R.id.drag_item_place_order_Level2_Part2);
        drag_item_wait_order_Level2_Part2 = findViewById(R.id.drag_item_wait_order_Level2_Part2);
        drag_item_pay_Level2_Part2 = findViewById(R.id.drag_item_pay_Level2_Part2);
        drag_item_take_food_Level2_Part2 = findViewById(R.id.drag_item_take_food_Level2_Part2);
        drag_item_end_Level2_Part2 = findViewById(R.id.drag_item_end_Level2_Part2);

        star1Level2Part2.setImageResource(R.drawable.star_filled);
        star2Level2Part2.setImageResource(R.drawable.star_filled);
        star3Level2Part2.setImageResource(R.drawable.star_filled);
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