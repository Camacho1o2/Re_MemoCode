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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Level3 extends AppCompatActivity {

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

    String stringDop, integerDop, floatDop, booleanDop, bytesDop;

    AccountAPI accountAPI;
    private Handler handler;
    private Runnable runnable;

    private String status = "completed";
    private int seconds = 120; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;

    int user_id;
    final int level_id = 3;

    //Part One
    TextView progressTextLevel3Part1, timerTextLevel3Part1, resultTextLevel3Part1,
        dropArea1Level3Part1, dropArea2Level3Part1, dropArea3Level3Part1,
            dragItemLevel3Part1, dragItem2Level3Part1, dragItem3Level3Part1,
            dragItem4Level3Part1, dragItem5Level3Part1;

    ImageButton buttonGoBackLevel3Part1, buttonClearAreaLevel3Part1, buttonRunLevel3Part1, buttonProceedLevel3Part1;
    ImageView star1Level3Part1, star2Level3Part1, star3Level3Part1;

    //Part Two
    TextView progressTextLevel3Part2, timerTextLevel3Part2, resultTextLevel3Part2,
            dragItemLevel3Part2, dragItem2Level3Part2, dragItem3Level3Part2, dragItem4Level3Part2, dragItem5Level3Part2;
    FrameLayout dropArea1Level3Part2, dropArea2Level3Part2, dropArea3Level3Part2, dropArea4Level3Part2, dropArea5Level3Part2;

    ImageButton buttonGoBackLevel3Part2, buttonClearAreaLevel3Part2, buttonRunLevel3Part2, buttonProceedLevel3Part2;
    ImageView star1Level3Part2, star2Level3Part2, star3Level3Part2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level3_part1);

        progressTextLevel3Part1 = findViewById(R.id.progressTextLevel3Part1);
        timerTextLevel3Part1 = findViewById(R.id.timerTextLevel3Part1);
        resultTextLevel3Part1 = findViewById(R.id.resultTextLevel3Part1);

        dropArea1Level3Part1 = findViewById(R.id.dropArea1Level3Part1);
        dropArea2Level3Part1 = findViewById(R.id.dropArea2Level3Part1);
        dropArea3Level3Part1 = findViewById(R.id.dropArea3Level3Part1);

        buttonGoBackLevel3Part1 = findViewById(R.id.buttonGoBackLevel3Part1);
        buttonClearAreaLevel3Part1 = findViewById(R.id.buttonClearAreaLevel3Part1);
        buttonRunLevel3Part1 = findViewById(R.id.buttonRunLevel3Part1);
        buttonProceedLevel3Part1 = findViewById(R.id.buttonProceedLevel3Part1);

        dragItemLevel3Part1 = findViewById(R.id.dragItemLevel3Part1);
        dragItem2Level3Part1 = findViewById(R.id.dragItem2Level3Part1);
        dragItem3Level3Part1 = findViewById(R.id.dragItem3Level3Part1);
        dragItem4Level3Part1 = findViewById(R.id.dragItem4Level3Part1);
        dragItem5Level3Part1 = findViewById(R.id.dragItem5Level3Part1);


        star1Level3Part1 = findViewById(R.id.star1Level3Part1);
        star2Level3Part1 = findViewById(R.id.star2Level3Part1);
        star3Level3Part1 = findViewById(R.id.star3Level3Part1);

        star1Level3Part1.setImageResource(R.drawable.star_filled);
        star2Level3Part1.setImageResource(R.drawable.star_filled);
        star3Level3Part1.setImageResource(R.drawable.star_filled);

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

        buttonProceedLevel3Part1.setVisibility(GONE);

        buttonClearAreaLevel3Part1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDroppedAreas();
            }
        });

        if(offlineMode){
            playerid = intent.getStringExtra("playerid");

            buttonGoBackLevel3Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    Intent intent = new Intent(Level3.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonRunLevel3Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswerLevel3Part1();
                }
            });

            buttonClearAreaLevel3Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearDroppedAreas();
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
                        Intent intent = new Intent(Level3.this, ResultActivity.class);
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
            handler.post(runnable);

        } else {

            //Session Token
            SessionManager sessionManager = new SessionManager(Level3.this);
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

            buttonGoBackLevel3Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    Intent intent = new Intent(Level3.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            buttonRunLevel3Part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswerLevel3Part1();
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
                        Intent intent = new Intent(Level3.this, ResultActivity.class);
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
        setDragListener(dragItemLevel3Part1, "Literal");
        setDragListener(dragItem2Level3Part1, "Variable");
        setDragListener(dragItem3Level3Part1, "Data Type");
        setDragListener(dragItem4Level3Part1, "Method");
        setDragListener(dragItem5Level3Part1, "Assignment Operator");

        dropArea1Level3Part1.setOnDragListener(new Level3.DropAreaDragListenerP1P1());
        dropArea2Level3Part1.setOnDragListener(new Level3.DropAreaDragListenerP1P2());
        dropArea3Level3Part1.setOnDragListener(new Level3.DropAreaDragListenerP1P3());
    }

    private void setDragListener(final TextView textView, final String level3Part1Snippet) {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("answer", level3Part1Snippet);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
        });
    }

    // DropAreaDragListener handles dropping in the drop area 1
    private class DropAreaDragListenerP1P1 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropArea1Level3Part1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropArea1Level3Part1.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    dropArea1Level3Part1.setText(droppedData);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropArea1Level3Part1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    // DropAreaDragListener handles dropping in the drop area 2
    private class DropAreaDragListenerP1P2 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropArea2Level3Part1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropArea2Level3Part1.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    dropArea2Level3Part1.setText(droppedData);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropArea2Level3Part1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    // DropAreaDragListener handles dropping in the drop area 3
    private class DropAreaDragListenerP1P3 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropArea3Level3Part1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropArea3Level3Part1.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    dropArea3Level3Part1.setText(droppedData);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropArea3Level3Part1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    private void clearDroppedAreas(){
        dropArea1Level3Part1.setText("");
        dropArea2Level3Part1.setText("");
        dropArea3Level3Part1.setText("");
        resultTextLevel3Part1.setText("Results:\n");
    }

    private void checkAnswerLevel3Part1(){
        String xDropArea = String.valueOf(dropArea1Level3Part1.getText());
        String equalsDropArea = String.valueOf(dropArea2Level3Part1.getText());
        String fiveDropArea = String.valueOf(dropArea3Level3Part1.getText());

        if(xDropArea.equals("Variable") && equalsDropArea.equals("Assignment Operator") && fiveDropArea.equals("Literal")){
            Toast.makeText(getApplicationContext(),"Correct! Those are the correct labels. Congrats", Toast.LENGTH_SHORT).show();
            stopTimer();
            setupPart2();
            setupDragAndDropPart2();

            buttonProceedLevel3Part2.setVisibility(GONE);

            buttonClearAreaLevel3Part2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearDropAreas2();
                }
            });

            if(offlineMode){
                buttonGoBackLevel3Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopTimer();
                        Intent intent = new Intent(Level3.this, MainActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("offlineMode", true);
                        startActivity(intent);
                    }
                });


                buttonRunLevel3Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswerLevel3Part2Offline();
                        clearDropAreas2();
                    }
                });

                buttonProceedLevel3Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Level3.this, ResultActivity.class);
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
                            updateTimer2();
                            progressStars2();
                            handler.postDelayed(this, 1000); // Repeat every second

                        } else {
                            stopTimer();
                            score = 0;
                            stars = 0;
                            Intent intent = new Intent(Level3.this, ResultActivity.class);
                            intent.putExtra("player", playerid);
                            intent.putExtra("level_id", level_id);
                            intent.putExtra("offlineMode", true);
                            intent.putExtra("score",score);
                            intent.putExtra("stars", stars);
                            intent.putExtra("CompletionTime", completedTime);
                            startActivity(intent);
                        }
                    }
                };
                handler.post(runnable);

            } else{
                buttonGoBackLevel3Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopTimer();
                        Intent intent = new Intent(Level3.this, MainActivity.class);
                        startActivity(intent);
                    }
                });


                buttonRunLevel3Part2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswerLevel3Part2();
                        clearDropAreas2();
                    }
                });

                buttonProceedLevel3Part2.setOnClickListener(new View.OnClickListener() {
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
                            Intent intent = new Intent(Level3.this, ResultActivity.class);
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

        } else {
            resultTextLevel3Part1.setText("Results:\nThose labels are wrong. Please try again");
            mistakes++;
        }

    }

    private void stopTimer() {
        handler.removeCallbacks(runnable);
        Toast.makeText(getApplicationContext(), "Stopped the Handler", Toast.LENGTH_SHORT).show();
    }

    private void updateTimer() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerTextLevel3Part1.setText(String.format("%02d:%02d", mins, secs));
    }

    private void progressStars(){
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel3Part1.setText("Progress: " + intProgress + "%");

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
                star3Level3Part1.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level3Part1.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level3Part1.setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void setupPart2(){
        setContentView(R.layout.activity_level3_part2);

        progressTextLevel3Part2 = findViewById(R.id.progressText_level3_part2);
        timerTextLevel3Part2 = findViewById(R.id.timerText_level3_part2);
        resultTextLevel3Part2 = findViewById(R.id.resultTextLevel3Part2);

        dropArea1Level3Part2 = findViewById(R.id.drop_area_string_level3_part2);
        dropArea2Level3Part2 = findViewById(R.id.drop_area_integer_level3_part2);
        dropArea3Level3Part2 = findViewById(R.id.drop_area_float_level3_part2);
        dropArea4Level3Part2 = findViewById(R.id.drop_area_boolean_level3_part2);
        dropArea5Level3Part2 = findViewById(R.id.drop_area_bytes_level3_part2);

        buttonGoBackLevel3Part2 = findViewById(R.id.buttonGoBack_level3_part2);
        buttonClearAreaLevel3Part2 = findViewById(R.id.buttonClearArea_level3_part2);
        buttonRunLevel3Part2 = findViewById(R.id.buttonRun_level3_part2);
        buttonProceedLevel3Part2 = findViewById(R.id.buttonProceedLevel2_level3_part2);

        dragItemLevel3Part2 = findViewById(R.id.drag_item_string_level3_part2);
        dragItem2Level3Part2 = findViewById(R.id.drag_item_integer_level3_part2);
        dragItem3Level3Part2 = findViewById(R.id.drag_item_float_level3_part2);
        dragItem4Level3Part2 = findViewById(R.id.drag_item_boolean_level3_part2);
        dragItem5Level3Part2 = findViewById(R.id.drag_item_bytes_level3_part2);

        star1Level3Part2 = findViewById(R.id.star1_level3_part2);
        star2Level3Part2 = findViewById(R.id.star2_level3_part2);
        star3Level3Part2 = findViewById(R.id.star3_level3_part2);

        star1Level3Part2.setImageResource(R.drawable.star_filled);
        star2Level3Part2.setImageResource(R.drawable.star_filled);
        star3Level3Part2.setImageResource(R.drawable.star_filled);

    }

    private void setupDragAndDropPart2(){
        setDragListener(dragItemLevel3Part2, "String");
        setDragListener(dragItem2Level3Part2, "Integer");
        setDragListener(dragItem3Level3Part2, "Float");
        setDragListener(dragItem4Level3Part2, "Boolean");
        setDragListener(dragItem5Level3Part2, "Bytes");

        dropArea1Level3Part2.setOnDragListener(new Level3.DropAreaDragListenerP2P1());
        dropArea2Level3Part2.setOnDragListener(new Level3.DropAreaDragListenerP2P2());
        dropArea3Level3Part2.setOnDragListener(new Level3.DropAreaDragListenerP2P3());
        dropArea4Level3Part2.setOnDragListener(new Level3.DropAreaDragListenerP2P4());
        dropArea5Level3Part2.setOnDragListener(new Level3.DropAreaDragListenerP2P5());

    }

    // DropAreaDragListener handles dropping in the drop area 1
    private class DropAreaDragListenerP2P1 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropArea1Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropArea1Level3Part2.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    // Display the dropped item in the drop area
                    TextView droppedView = new TextView(Level3.this);
                    droppedView.setText(droppedData);
                    droppedView.setPadding(10, 10, 10, 10);
                    droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                    droppedView.setTextColor(getResources().getColor(R.color.black));
                    dropArea1Level3Part2.addView(droppedView);
                    stringDop = droppedData;

                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropArea1Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    // DropAreaDragListener handles dropping in the drop area 2
    private class DropAreaDragListenerP2P2 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropArea2Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropArea2Level3Part2.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    // Display the dropped item in the drop area
                    TextView droppedView = new TextView(Level3.this);
                    droppedView.setText(droppedData);
                    droppedView.setPadding(10, 10, 10, 10);
                    droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                    droppedView.setTextColor(getResources().getColor(R.color.black));
                    dropArea2Level3Part2.addView(droppedView);
                    integerDop = droppedData;
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropArea2Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    // DropAreaDragListener handles dropping in the drop area 3
    private class DropAreaDragListenerP2P3 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropArea3Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropArea3Level3Part2.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    // Display the dropped item in the drop area
                    TextView droppedView = new TextView(Level3.this);
                    droppedView.setText(droppedData);
                    droppedView.setPadding(10, 10, 10, 10);
                    droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                    droppedView.setTextColor(getResources().getColor(R.color.black));
                    dropArea3Level3Part2.addView(droppedView);
                    floatDop = droppedData;
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropArea3Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    private class DropAreaDragListenerP2P4 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropArea4Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropArea4Level3Part2.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    // Display the dropped item in the drop area
                    TextView droppedView = new TextView(Level3.this);
                    droppedView.setText(droppedData);
                    droppedView.setPadding(10, 10, 10, 10);
                    droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                    droppedView.setTextColor(getResources().getColor(R.color.black));
                    dropArea4Level3Part2.addView(droppedView);
                    booleanDop = droppedData;
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropArea4Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    private class DropAreaDragListenerP2P5 implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropArea5Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropArea5Level3Part2.setBackgroundColor(getResources().getColor(R.color.custom_light_gray));
                    return true;

                case DragEvent.ACTION_DROP:
                    // Get the data and store it in the user's order
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String droppedData = item.getText().toString();

                    // Display the dropped item in the drop area
                    TextView droppedView = new TextView(Level3.this);
                    droppedView.setText(droppedData);
                    droppedView.setPadding(10, 10, 10, 10);
                    droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                    droppedView.setTextColor(getResources().getColor(R.color.black));
                    dropArea5Level3Part2.addView(droppedView);
                    bytesDop = droppedData;
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropArea5Level3Part2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;

                default:
                    return false;
            }
        }
    }

    private void checkAnswerLevel3Part2(){
        if(stringDop.equals("String") && integerDop.equals("Integer") && floatDop.equals("Float") && booleanDop.equals("Boolean") && bytesDop.equals("Bytes")){
            resultTextLevel3Part2.setText("Results:\n Correct, those are the right data types.");
            if(randomMode){
                stopTimer();
                if(currentLevelRandom == 8){
                    completedTime = 600 - seconds;

                    intent = new Intent(Level3.this, ResultActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("level_id", "Randomness");
                    intent.putExtra("score", score);
                    intent.putExtra("stars", stars);
                    intent.putExtra("CompletionTime", completedTime);
                    startActivity(intent);

                } else {
                    currentLevelRandom++;
                    int getLevel = arrayLevels[currentLevelRandom];

                    intent = new Intent(Level3.this, getLevelClass(getLevel));
                    intent.putExtra("score", score);
                    intent.putExtra("arrayLevels", arrayLevels);
                    intent.putExtra("randomMode", true);
                    intent.putExtra("currentLevelRandom", currentLevelRandom);
                    intent.putExtra("randomDuration", seconds);
                    intent.putExtra("score", score);
                    startActivity(intent);
                }

            } else {
                stopTimer();
                completedTime = 120 - seconds;
                progressStars2();

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

                buttonProceedLevel3Part2.setVisibility(View.VISIBLE);
            }

        }else if(stringDop.equals("") && integerDop.equals("") && floatDop.equals("") && booleanDop.equals("") && bytesDop.equals("")) {
            resultTextLevel3Part2.setText("Results:\nNo data was entered.");
            mistakes++;
        }
        else {
            resultTextLevel3Part2.setText("Results:\n Wrong, the labeling is wrong. Please try again");
            mistakes++;
        }
    }

    private void checkAnswerLevel3Part2Offline(){
        if(stringDop.equals("String") && integerDop.equals("Integer") &&
                floatDop.equals("Float") && booleanDop.equals("Boolean") && bytesDop.equals("Bytes")){
            resultTextLevel3Part2.setText("Results:\n Correct, those are the right data types.");
            if(randomMode){
                stopTimer();
                if(currentLevelRandom == 8){
                    completedTime = 600 - seconds;

                    intent = new Intent(Level3.this, ResultActivity.class);
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

                    intent = new Intent(Level3.this, getLevelClass(getLevel));
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
                progressStars2();
                stopTimer();
                completedTime = 120 - seconds;

                try {
                    sharedPreferences = getSharedPreferences("OfflineLevel3Data", MODE_PRIVATE);
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

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Level Progress Failed to Save", Toast.LENGTH_LONG).show();
                }

                buttonProceedLevel3Part2.setVisibility(View.VISIBLE);
            }

        }else if(stringDop.equals("") && integerDop.equals("") && floatDop.equals("") && booleanDop.equals("") && bytesDop.equals("")) {
            resultTextLevel3Part2.setText("Results:\nNo data was entered.");
            mistakes++;
        }
        else {
            resultTextLevel3Part2.setText("Results:\n Wrong, the labeling is wrong. Please try again");
            mistakes++;
        }
    }

    private void updateTimer2() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerTextLevel3Part2.setText(String.format("%02d:%02d", mins, secs));
    }

    private void progressStars2(){
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel3Part2.setText("Progress: " + intProgress + "%");

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
                star3Level3Part2.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level3Part2.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level3Part2.setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void clearDropAreas2(){
        dropArea1Level3Part2.removeAllViews();
        dropArea2Level3Part2.removeAllViews();
        dropArea3Level3Part2.removeAllViews();
        dropArea4Level3Part2.removeAllViews();
        dropArea5Level3Part2.removeAllViews();

        stringDop = " ";
        integerDop = " ";
        floatDop = " ";
        booleanDop = " ";
        bytesDop = " ";

        resultTextLevel3Part2.setText("Results:\n");
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