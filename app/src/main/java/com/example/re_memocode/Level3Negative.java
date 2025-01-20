package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Level3Negative extends AppCompatActivity {

    Intent intent;

    AccountAPI accountAPI;
    private Handler handler;
    private Runnable runnable;
    private int seconds = 60; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;
    String status = "incomplete";
    int user_id;
    final int level_id = 3;

    private TextView levelTitle, timerTextLevel3, question;
    private ImageView timerIcon;
    private LinearLayout timerLayout, dropArea;
    private GridLayout draggableItemsLayout;
    private TextView draggableItem1, draggableItem2, draggableItem3, draggableItem4, draggableItem5, textResultsLevel3;
    private Button checkButton, clearButtonLevel3, proceedButtonLevel3, buttonGoBackLevel3;
    private String[] userOrder = new String[5];
    private String [] correctOrdder = {
            "number = int(input(&quot;Enter a number: &quot;))",
            "if number % 2 == 0:",
            "print(f&quot;{number} is Even&quot;)",
            "else:",
            "print(f&quot;{number} is Odd&quot;)"
    };
    // Placeholder for user-selected order
    private int currentDropIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level3_negative);

//        intent = getIntent();
//        user_id = intent.getIntExtra("user_id", 0);

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

        // Initialize views
        levelTitle = findViewById(R.id.level4Title);
        timerTextLevel3 = findViewById(R.id.timerTextLevel5);
        timerIcon = findViewById(R.id.timerIcon);
        timerLayout = findViewById(R.id.timerLayout);
        question = findViewById(R.id.question);
        dropArea = findViewById(R.id.dropArea);

        draggableItemsLayout = findViewById(R.id.draggableItemsLayout);
        textResultsLevel3 = findViewById(R.id.textResultsLevel3);
        draggableItem1 = findViewById(R.id.draggableItem1);
        draggableItem2 = findViewById(R.id.draggableItem2);
        draggableItem3 = findViewById(R.id.draggableItem3);
        draggableItem4 = findViewById(R.id.draggableItem4);
        draggableItem5 = findViewById(R.id.draggableItem5);
        checkButton = findViewById(R.id.checkButtonLevel3);
        clearButtonLevel3 = findViewById(R.id.buttonClearLevel3);
        proceedButtonLevel3 =  findViewById(R.id.buttonProceedLevel3);
        buttonGoBackLevel3 = findViewById(R.id.buttonGoBackLevel3);

        // Handle click for the check button
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userOrderToString = Arrays.toString(userOrder);
                    checkAnswer();

            }
        });

        // Setup drag and drop functionality (to be implemented)
        setupDragAndDrop();

        clearButtonLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDroppedArea();
            }
        });
        buttonGoBackLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Level3Negative.this, MainActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

        proceedButtonLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Level3Negative.this, ResultActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("level_id", level_id);
                intent.putExtra("score", score);
                intent.putExtra("stars", stars);
                intent.putExtra("CompletionTime", completedTime);
                startActivity(intent);
            }
        });

        proceedButtonLevel3.setVisibility(View.GONE);

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
                    handler.postDelayed(this, 1000); // Repeat every second
                } else {
                    stopTimer();
                    score = 0;
                    stars = 0;
                    Intent intent = new Intent(Level3Negative.this, ResultActivity.class);
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

        private void setupDragAndDrop () {
            setDragListener(draggableItem1, "number = int(input(&quot;Enter a number: &quot;))");
            setDragListener(draggableItem2, "if number % 2 == 0:");
            setDragListener(draggableItem3, "print(f&quot;{number} is Odd&quot;)");
            setDragListener(draggableItem4, "print(f&quot;{number} is Even&quot;)");
            setDragListener(draggableItem5, "else:");

            dropArea.setOnDragListener(new DropAreaDragListener());
        }

        // Set up long click listener to start dragging for TextView items
        private void setDragListener ( final TextView textView, final String codeSnippetLevel3){
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData data = ClipData.newPlainText("codeLevel3", codeSnippetLevel3);
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
                        dropArea.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        dropArea.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        return true;

                    case DragEvent.ACTION_DROP:
                        // Get the data and store it in the user's order
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String droppedData = item.getText().toString();

                        if (currentDropIndex < userOrder.length) {
                            userOrder[currentDropIndex] = droppedData;
                            currentDropIndex++;

                            // Display the dropped item in the drop area
                            TextView droppedView = new TextView(Level3Negative.this);
                            droppedView.setText(droppedData);
                            droppedView.setPadding(10, 10, 10, 10);
                            droppedView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                            droppedView.setTextColor(getResources().getColor(R.color.black));
                            dropArea.addView(droppedView);
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        dropArea.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        return true;
                    default:
                        return false;
                }
            }
        }

        private void clearDroppedArea () {
            // Remove all views from the dropArea
            dropArea.removeAllViews();

            // Reset the user order and the current drop index
            userOrder = new String[correctOrdder.length];
            currentDropIndex = 0;

            // Optionally, reset any result message
            textResultsLevel3.setText("");
        }

        private void checkAnswer (){
            String[] wrongArray1 = {
                    "number = int(input(&quot;Enter a number: &quot;))",
                    "if number % 2 == 0:",
                    "print(f&quot;{number} is Odd&quot;)",
                    "else:",
                    "print(f&quot;{number} is Even&quot;)",

            };

            if (Arrays.equals(userOrder, correctOrdder)){
                textResultsLevel3.setText("Correct! The sequence of code is right: \n Enter a number: ");
                proceedButtonLevel3.setVisibility(View.VISIBLE);
                stopTimer();
                completedTime = 60 - seconds;

                //Calculate the score
                if (completedTime >= 5 && completedTime <= 10){
                    toMinusTime = 0.1;
                }
                else if (completedTime >= 11 && completedTime <= 20){
                    toMinusTime = 0.3;

                } else if (completedTime >= 21 && completedTime <= 30){
                    toMinusTime = 0.6;
                } else if (completedTime >= 31 && completedTime <= 40){
                    toMinusTime = 0.9;
                } else if (completedTime >= 41 && completedTime <= 50){
                    toMinusTime = 1.2;
                } else if (completedTime >= 51 && completedTime <= 60){
                    toMinusTime = 1.5;
                }

                score = score - toMinusTime * 300;
                toMinusMistakes = mistakes * 100;
                score = score-toMinusMistakes;

                if (score >= 701){
                    stars = 3;
                } else if (score >= 301 && score <= 700){
                    stars = 2;
                } else if (score >= 1 && score <= 300){
                    stars = 1;
                } else {
                    stars = 0;
                }

                //to save user progress
                Call<JsonObject> call = accountAPI.saveProgress(user_id, level_id, status);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject jsonObject = response.body();
                            int success = jsonObject.get("success").getAsInt();
                            if (success == 1){
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
                            if (success == 1){
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

            } else if (Arrays.equals(userOrder, wrongArray1)){
                textResultsLevel3.setText(" Incorrect. The the results are swapped. ");
            } else {
                textResultsLevel3.setText(" Incorrect. The sequence is wrong");
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
    }

    private void updateTimer() {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerTextLevel3.setText(String.format("%02d:%02d", mins, secs));
    }

}