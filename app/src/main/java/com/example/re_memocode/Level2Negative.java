package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.content.ClipData;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Level2Negative extends Activity {

    Intent intent;

    AccountAPI accountAPI;
    private Handler handler;
    private Runnable runnable;
    private int seconds = 100; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;
    String status = "incomplete";
    int user_id;
    final int level_id = 2;

    private TextView textQuestionLevel2, textTimerLevel2, textResultLevel2;
    private Button btnCheckAnswerLevel2, btnproceedLevel2, btnClearLevel2, btnGoBackLevel2;
    private LinearLayout dropArea;

    private String[] correctOrder = {
            "Start",
            "IF employee gets eight or more questions correct",
            "Display 'Congratulations on passing the quiz!'",
            "Transition to certificate page",
            "ELSE",
            "Display 'Let's try again!'",
            "Transition back to first page of quiz",
            "END IF"
    };

    private String[] nullOrder = new String[7];

    private String[] userOrder = new String[7]; // Placeholder for user-selected order
    private int currentDropIndex = 0; // Keeps track of the drop index in userOrder

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level2_negative); // Assuming activity_level2.xml

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

        // Initialize UI components
        textQuestionLevel2 = findViewById(R.id.questionTextLevel2);
        textTimerLevel2 = findViewById(R.id.timer);
        textResultLevel2 = findViewById(R.id.textResultsLevel2);
        btnCheckAnswerLevel2 = findViewById(R.id.buttonCheckAnswerLevel2);
        btnproceedLevel2 = findViewById(R.id.buttonProceedLevel2Part1);
        btnClearLevel2 = findViewById(R.id.buttonClearAreaLevel2);
        btnGoBackLevel2 = findViewById(R.id.buttonGoBackLevel2);
        dropArea = findViewById(R.id.drop_areaLevel2Part1);
        intent = getIntent();
        user_id = intent.getIntExtra("user_id", 0);

        // Set up drag listeners
        setupDragAndDrop();

        btnproceedLevel2.setVisibility(View.GONE);

        btnClearLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDroppedArea();
            }
        });

        btnCheckAnswerLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (Arrays.equals(userOrder, nullOrder)){
                    textResultLevel2.setText("Please put the blocks in the box.");
                     mistakes++;
                } else {
                     dropAreaCheckAnswerLevel2();
                 }
            }
        });

        btnGoBackLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                intent = new Intent(Level2Negative.this, MainActivity.class);
//                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });



        btnproceedLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Level2Negative.this, ResultActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("level_id", level_id);
                intent.putExtra("score", score);
                intent.putExtra("stars", stars);
                intent.putExtra("CompletionTime", completedTime);
                startActivity(intent);
                startActivity(intent);
            }
        });

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
                    Intent intent = new Intent(Level2Negative.this, ResultActivity.class);
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


    // Set up drag-and-drop functionality for pseudocode snippets
    private void setupDragAndDrop() {
        // Drag items (TextView elements)
        TextView dragItemIf = findViewById(R.id.drag_item_if);
        TextView dragItemDisplayCongrats = findViewById(R.id.drag_item_display_congrats);
        TextView dragItemTransitionCert = findViewById(R.id.drag_item_transition_certificate);
        TextView dragItemElse = findViewById(R.id.drag_item_else);
        TextView dragItemDisplayTryAgain = findViewById(R.id.drag_item_display_try_again);
        TextView dragItemTransitionBackQuiz = findViewById(R.id.drag_item_transition_back_quiz);
        TextView dragItemEndIf = findViewById(R.id.drag_item_end_if);
        TextView dragItemStart = findViewById(R.id.drag_item_start);

        // Set up drag listeners for each drag item
        setDragListener(dragItemIf, "IF employee gets eight or more questions correct");
        setDragListener(dragItemDisplayCongrats, "Display 'Congratulations on passing the quiz!'");
        setDragListener(dragItemTransitionCert, "Transition to certificate page");
        setDragListener(dragItemElse, "ELSE");
        setDragListener(dragItemDisplayTryAgain, "Display 'Let's try again!'");
        setDragListener(dragItemTransitionBackQuiz, "Transition back to first page of quiz");
        setDragListener(dragItemEndIf, "END IF");
        setDragListener(dragItemStart, "Start");

        // Set up drop area listener
        dropArea.setOnDragListener(new DropAreaDragListener());
    }

    // Set up long click listener to start dragging for TextView items
    private void setDragListener(final TextView textView, final String pseudocodeSnippet) {
        textView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("pseudocode", pseudocodeSnippet);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
        });
    }

    // DropAreaDragListener handles dropping in the drop area
    private class DropAreaDragListener implements OnDragListener {
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
                        TextView droppedView = new TextView(Level2Negative.this);
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
    //To clear the drop area
    private void clearDroppedArea(){
        // Remove all views from the dropArea
        dropArea.removeAllViews();

        // Reset the user order and the current drop index
        userOrder = new String[correctOrder.length];
        currentDropIndex = 0;

        // Optionally, reset any result message
        textResultLevel2.setText("");
    }
    //To check if the user is correct

    private void dropAreaCheckAnswerLevel2() {
        String[] wrongArray1 = {
                "Start",
                "IF employee gets eight or more questions correct",
                "Transition to certificate page",
                "Display 'Congratulations on passing the quiz!'",
                "ELSE",
                "Transition back to first page of quiz",
                "Display 'Let's try again!'",
                "END IF"
        };

        String[] wrongArray2 = {
                "Start",
                "IF employee gets eight or more questions correct",
                "Display 'Congratulations on passing the quiz!'",
                "Transition to certificate page",
                "END IF",
                "ELSE",
                "Display 'Let's try again!'",
                "Transition back to first page of quiz"


        };

        String[] wrongArray3 = {
                "Start",
                "IF employee gets eight or more questions correct",
                "Display 'Congratulations on passing the quiz!'",
                "ELSE",
                "Transition to certificate page",
                "Display 'Let's try again!'",
                "Transition back to first page of quiz",
                "END IF"

        };

        String[] wrongArray4 = {
                "Start",
                "IF employee gets eight or more questions correct",
                "Display 'Congratulations on passing the quiz!'",
                "Transition to certificate page",
                "Display 'Let's try again!'",
                "ELSE",
                "Transition back to first page of quiz",
                "END IF"

        };
        String[] wrongArray5 = {
                "Start",
                "IF employee gets eight or more questions correct",
                "Display 'Congratulations on passing the quiz!'",
                "Transition to certificate page",
                "Display 'Let's try again!'",
                "Transition back to first page of quiz",
                "ELSE",
                "END IF"

        };

        String[] wrongArray6 = {
                "Start",
                "Display 'Congratulations on passing the quiz!'",
                "Transition to certificate page",
                "IF employee gets eight or more questions correct",
                "ELSE",
                "Display 'Let's try again!'",
                "Transition back to first page of quiz",
                "END IF"

        };

        String[] wrongArray7 = {
                "Start",
                "IF employee gets eight or more questions correct",
                "Display 'Let's try again!'",
                "Transition back to first page of quiz",
                "ELSE",
                "Display 'Congratulations on passing the quiz!'",
                "Transition to certificate page",
                "END IF"

        };

        String[] wrongArray8 = {
                "END IF",
                "Transition back to first page of quiz",
                "Display 'Let's try again!'",
                "ELSE",
                "Transition to certificate page",
                "Display 'Congratulations on passing the quiz!'",
                "IF employee gets eight or more questions correct",
                "Start",

        };

        String[] wrongArray9 = {
                "Start",
                "Display 'Let's try again!'",
                "Transition back to first page of quiz",
                "ELSE",
                "IF employee gets eight or more questions correct",
                "Display 'Congratulations on passing the quiz!'",
                "Transition to certificate page",
                "END IF"

        };

        String[] wrongArray10 = {
                "IF employee gets eight or more questions correct",
                "Display 'Congratulations on passing the quiz!'",
                "Transition back to first page of quiz",
                "ELSE",
                "Display 'Let's try again!'",
                "Transition to certificate page",
                "END IF"

        };

        String [] notEnoughBlocks1 = new String[7];

        notEnoughBlocks1 [0] = "Start";
        notEnoughBlocks1 [1] = "IF employee gets eight or more questions correct";
        notEnoughBlocks1 [2] = "Display 'Congratulations on passing the quiz!'";
        notEnoughBlocks1 [3] = "Transition to certificate page";
        notEnoughBlocks1 [4] = "END IF";

        String [] notEnoughBlocks2 = new String[7];

        notEnoughBlocks1 [0] = "Start";
        notEnoughBlocks2 [1] = "ELSE";
        notEnoughBlocks2 [2] = "Display 'Let's try again!'";
        notEnoughBlocks2 [3] = "Transition back to first page of quiz";
        notEnoughBlocks2 [4] = "END IF";

        String [] notEnoughBlocks3 = new String[7];

        notEnoughBlocks1 [0] = "Start";
        notEnoughBlocks3 [1] = "IF employee gets eight or more questions correct";
        notEnoughBlocks3 [2] = "ELSE";
        notEnoughBlocks3 [3] = "END IF";

        String [] notEnoughBlocks4 = new String[7];

        notEnoughBlocks4 [0] = "Display 'Congratulations on passing the quiz!'";
        notEnoughBlocks4 [1] = "Transition to certificate page";

        String [] notEnoughBlocks5 = new String[7];

        notEnoughBlocks5 [0] = "Display 'Let's try again!'";
        notEnoughBlocks5 [1] = "Transition back to first page of quiz";


        String [] notEnoughBlocks6 = new String[7];

        notEnoughBlocks6 [0] = "IF employee gets eight or more questions correct";
        notEnoughBlocks6 [1] = "Display 'Let's try again!'";


        if(Arrays.equals(userOrder, correctOrder)){
            //The player is correct
            textResultLevel2.setText("Correct! That is a correct sequence of an if-else statement. Good Job.");
            btnproceedLevel2.setVisibility(View.VISIBLE);
            stopTimer();
            completedTime = 100 - seconds;

            //Calculate the score
            if (completedTime >= 5 && completedTime <= 20){
                toMinusTime = 0.1;
            }
            else if (completedTime >= 21 && completedTime <= 30){
                toMinusTime = 0.3;

            } else if (completedTime >= 31 && completedTime <= 40){
                toMinusTime = 0.6;
            } else if (completedTime >= 41 && completedTime <= 50){
                toMinusTime = 0.9;
            } else if (completedTime >= 51 && completedTime <= 70){
                toMinusTime = 1.2;
            } else if (completedTime >= 71 && completedTime <= 100){
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

        }else if (Arrays.equals(userOrder, wrongArray1)) {
            textResultLevel2.setText("Incorrect Why would you display the result after going to the next page? Try again");
            mistakes++;
        } else if (Arrays.equals(userOrder, wrongArray2)){
            textResultLevel2.setText("Incorrect. 'END IF' means it's the end of the if-else structure. Try Again");
            mistakes++;
        } else if (Arrays.equals(userOrder, wrongArray3)){
            textResultLevel2.setText("Incorrect. What a weird travel. Going from the ceritificate page for no reason then back to the quiz if the employee fails. Try Again");
            mistakes++;
        } else if(Arrays.equals(userOrder, wrongArray4)){
            textResultLevel2.setText("Incorrect. You congratulated them for passing the quiz and then told them to try again. Try Again..the pseudocode");
            mistakes++;
        } else if(Arrays.equals(userOrder, wrongArray5)){
            textResultLevel2.setText("Incorrect. What would happen if the employee didn't get eight or more questions correct? Try Again.");
            mistakes++;
        } else if(Arrays.equals(userOrder, wrongArray6)){
            textResultLevel2.setText("Incorrect. The IF part goes first but nice try. Go Try Again.");
            mistakes++;
        } else if(Arrays.equals(userOrder, wrongArray7)){
            textResultLevel2.setText("Incorrect. The employee is supposed to be congratulated for passing, not the other way around. Did you do this on purpose? Go try the other way.");
            mistakes++;
        } else if(Arrays.equals(userOrder, wrongArray8)){
            textResultLevel2.setText("A complete topsy turvy. Well done. It's still wrong though, try doing the opposite.");
            mistakes++;
        } else if(Arrays.equals(userOrder, wrongArray9)){
            textResultLevel2.setText("Incorrect. This could work, if there's another IF before the ELSE.Anyway, try Again.");
            mistakes++;
        } else if(Arrays.equals(userOrder, wrongArray10)){
            textResultLevel2.setText("Incorrect. The employee fails the test and you give him/her a certificate. Try Again.");
            mistakes++;

        } else if(Arrays.equals(userOrder, notEnoughBlocks1)){
            textResultLevel2.setText("It actually works but what about the others?. Try Again");
            mistakes++;
        } else if (Arrays.equals(userOrder, notEnoughBlocks2)) {
            textResultLevel2.setText("Incorrect. 'ELSE' cannot go alone. Try Again");
            mistakes++;
        } else if (Arrays.equals(userOrder, notEnoughBlocks3)) {
            textResultLevel2.setText("Incorrect. Sure there are the conditions but there's nothing to do. Try Again");
            mistakes++;
        } else if (Arrays.equals(userOrder, notEnoughBlocks4)) {
            textResultLevel2.setText("Incorrect. What quiz? And how did they pass? Also, a free certificate. Try Again");
            mistakes++;
        } else if (Arrays.equals(userOrder, notEnoughBlocks5)) {
            textResultLevel2.setText("Incorrect. What do you mean 'again'? Please arrange the events properly. Try Again");
            mistakes++;
        }  else if (Arrays.equals(userOrder, notEnoughBlocks6)) {
        textResultLevel2.setText("Incorrect. Poor employee, he/she passed the quiz but you prompt them to try again. Try Again");
            mistakes++;
        }else {
            textResultLevel2.setText("Incorrect sequence of blocks. try again");
            mistakes++;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
        finish();
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
        textTimerLevel2.setText(String.format("%02d:%02d", mins, secs));
    }
}
