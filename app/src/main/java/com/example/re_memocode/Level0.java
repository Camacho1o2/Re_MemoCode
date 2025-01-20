package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.chaquo.python.Python;

import androidx.appcompat.app.AppCompatActivity;

//import com.chaquo.python.android.AndroidPlatform;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Level0 extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;
    private int seconds = 60; // Timer in seconds
    private TextView timertextView;
    int mistakes;
    double completedTime = 0;
    double toMinusTime = 0;
    double toMinusMistakes;
    double score = 1000;
    int stars;
    int user_id;
    final int level_id = 0;
    String status = "incomplete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

//        if (! Python.isStarted()) {
//            Python.start(new AndroidPlatform(this));
//        }

        // Initialize views
        Intent intent = getIntent();
        user_id = intent.getIntExtra("user_id", 0);
        final TextView codeSnippet1 = findViewById(R.id.code_snippet1);
        final TextView codeSnippet2 = findViewById(R.id.code_snippet2);
        final LinearLayout slot1 = findViewById(R.id.slot1);
        final LinearLayout slot2 = findViewById(R.id.slot2);
        final LinearLayout originalParent1 = (LinearLayout) codeSnippet1.getParent();
        final LinearLayout originalParent2 = (LinearLayout) codeSnippet2.getParent();
        Button proceed = findViewById(R.id.proceedButton);
        final TextView resultText = findViewById(R.id.resultTextLevel5);
        Button checkButton = findViewById(R.id.checkButtonLevel3);
        timertextView = findViewById(R.id.timerTextView);

        // Set long click listeners for draggable items
        codeSnippet1.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, myShadow, v, 0);
                return true;
            }
        });

        codeSnippet2.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, myShadow, v, 0);
                return true;
            }
        });

        // Drag listeners for drop targets
        slot1.setOnDragListener(new MyDragListener(originalParent1));
        slot2.setOnDragListener(new MyDragListener(originalParent2));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.53.32/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AccountAPI accountAPI = retrofit.create(AccountAPI.class);

        // Create a new CountDownTimer
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
                    Intent intent = new Intent(Level0.this, ResultActivity.class);
                    intent.putExtra("score",score);
                    intent.putExtra("stars", stars);
                    intent.putExtra("CompletionTime", completedTime);
                    startActivity(intent);
                }

            }
        };

        handler.post(runnable); // Start the timer

        // Answer button
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slot1.getChildCount() > 0 && slot2.getChildCount() > 0) {
                    TextView dropped1 = (TextView) slot1.getChildAt(0);
                    TextView dropped2 = (TextView) slot2.getChildAt(0);
                    if (dropped1.getText().toString().equals("def my_function():") &&
                            dropped2.getText().toString().equals("    return 'Hello, World!'")) {
                        //show the results and the next level button
                        resultText.setText("Hello, World!");
                        proceed.setVisibility(View.VISIBLE);
                        stopTimer();
                        completedTime = 60 - seconds;
                        Toast.makeText(Level0.this, "Correct!" + " Your completion time is: "+  completedTime + " second(s)", Toast.LENGTH_SHORT).show();

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
                        score = score - toMinusTime * 200;
                        toMinusMistakes = mistakes * 100;
                        score = score-toMinusMistakes;

                        if (score >= 601){
                            stars = 3;
                        } else if (score >= 301 && score <= 600){
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
                                        Toast.makeText(getApplicationContext(), "UserProgress Saved Successfully", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Failed to Fetch data", Toast.LENGTH_SHORT).show();
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
                                Log.e(TAG, "Failed to Fetch data", t);
                                Toast.makeText(getApplicationContext(), "Failed to Fetch data", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (dropped1.getText().toString().equals("    return 'Hello, World!'" ) &&
                            dropped2.getText().toString().equals("def my_function():")){
                        resultText.setText("Syntax Error: The line return: 'Hello World!' is not a valid Python syntax");
                        Toast.makeText(Level0.this, "Try Again!", Toast.LENGTH_SHORT).show();
                        mistakes++;
                    }
                //If the player only input certain areas
                } else if(slot1.getChildCount() == 0 && slot2.getChildCount() > 0){
                    TextView droppedonly = (TextView) slot2.getChildAt(0);
                    if (droppedonly.getText().toString().equals("    return 'Hello, World!'")){
                        resultText.setText("Syntax Error: The line return: 'Hello World!' is not a valid Python syntax");
                        Toast.makeText(Level0.this, "Try Again!", Toast.LENGTH_SHORT).show();
                        mistakes++;
                    }
                }else if(slot1.getChildCount() > 0 && slot2.getChildCount() == 0) {
                    TextView droppedonlynum2 = (TextView) slot1.getChildAt(0);
                    if (droppedonlynum2.getText().toString().equals("    return 'Hello, World!'")) {
                        resultText.setText("Syntax Error: The line return: 'Hello World!' is not a valid Python syntax");
                        Toast.makeText(Level0.this, "Try Again!", Toast.LENGTH_SHORT).show();
                        mistakes++;
                    }
                }  else {
                    Toast.makeText(Level0.this, "Input a code!", Toast.LENGTH_SHORT).show();
                    mistakes++;
                }
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Level0.this, ResultActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("level_id", level_id);
                intent.putExtra("score", score);
                intent.putExtra("stars", stars);
                intent.putExtra("CompletionTime", completedTime);
                startActivity(intent);
            }
        });


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
        timertextView.setText(String.format("%02d:%02d", mins, secs));
    }
    private class MyDragListener implements OnDragListener {

        private LinearLayout originalParent;

        // Constructor to pass original parent view
        public MyDragListener(LinearLayout originalParent) {
            this.originalParent = originalParent;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    final LinearLayout container = (LinearLayout) v;
                    if (container.getChildCount() == 0) {  // Only allow one item per slot
                        ((LinearLayout) view.getParent()).removeView(view);
                        container.addView(view);

                        // Set click listener for the dropped view to return it to its original position
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                container.removeView(v);  // Remove it from the current slot
                                originalParent.addView(v);  // Add it back to its original parent
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }



}
