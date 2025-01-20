package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.Random;

public class Level1 extends AppCompatActivity {


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
    private int seconds = 120; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;

    int num1, num2, numAnswerLevel1, user_id;
    String username, status = "completed";
    final int level_id = 1;

    TextView questionTextLevel1, timerTextlevel1, resultsTextLevel1, progressTextLevel1;
    Button btnAnswer1Level1, btnAnswer2Level1, btnAnswer3Level1, btnAnswer4Level1;
    ImageButton btnProceedLevel1, btnGoBacklevel1;
    ImageView star1Level1, star2Level1, star3Level1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level1);
        //receive intent data
//        intent = getIntent();
//        user_id = intent.getIntExtra("user_id", 0);
//        username = intent.getStringExtra("username");

        //Initialize views
        questionTextLevel1 = findViewById(R.id.questionTextLevel1);
        timerTextlevel1 = findViewById(R.id.timerTextLevel1);
        resultsTextLevel1 = findViewById(R.id.textShowResultsLevel1);

        btnAnswer1Level1 = findViewById(R.id.buttonAnswer1Level1);
        btnAnswer2Level1 = findViewById(R.id.buttonAnswer2Level1);
        btnAnswer3Level1 = findViewById(R.id.buttonAnswer3Level1);
        btnAnswer4Level1 = findViewById(R.id.buttonAnswer4Level1);
        btnProceedLevel1 = findViewById(R.id.proceedButtonLevel1);
        btnGoBacklevel1 = findViewById(R.id.goBackButtonLevel1);

        star1Level1 = findViewById(R.id.star1Level1);
        star2Level1 = findViewById(R.id.star2Level1);
        star3Level1 = findViewById(R.id.star3Level1);

        progressTextLevel1 = findViewById(R.id.progressTextLevel1);

        star1Level1.setImageResource(R.drawable.star_filled);
        star2Level1.setImageResource(R.drawable.star_filled);
        star3Level1.setImageResource(R.drawable.star_filled);

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

        if (offlineMode) {
            playerid = intent.getStringExtra("playerid");

            btnProceedLevel1.setVisibility(View.GONE);
            Random randomize = new Random();
            num1 = randomize.nextInt(20);
            num2 = randomize.nextInt(20);

            numAnswerLevel1 = num1 + num2;

            btnGoBacklevel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Level1.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                    stopTimer();
                }
            });

            btnProceedLevel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getApplicationContext(), ResultActivity.class);

                    intent.putExtra("offlineMode", true);
                    intent.putExtra("playerid", playerid);
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
                        Intent intent = new Intent(Level1.this, ResultActivity.class);
                        intent.putExtra("playerid", playerid);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("offlineMode", true);
                        intent.putExtra("score", score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }

                }
            };
            handler.post(runnable); // Start the timer

            phase1Offline();


        } else {
            //Session Token
            SessionManager sessionManager = new SessionManager(Level1.this);
            if (sessionManager.hasSessionToken()) {
                // Check if a session exists
                if (sessionManager.hasSessionToken()) {
                    String token = sessionManager.getSessionToken();
                    user_id = Integer.parseInt(sessionManager.getUserId());
                    username = sessionManager.getUsername();
                    // Use the token and userId as needed
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

            btnProceedLevel1.setVisibility(View.GONE);

            Random randomize = new Random();
            num1 = randomize.nextInt(20);
            num2 = randomize.nextInt(20);

            numAnswerLevel1 = num1 + num2;

            phase1();

            btnGoBacklevel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Level1.this, MainActivity.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                    stopTimer();
                }
            });

            btnProceedLevel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Level1.this, ResultActivity.class);
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
                        Intent intent = new Intent(Level1.this, ResultActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("score", score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }

                }
            };
            handler.post(runnable); // Start the timer
        }


    }

    private void phase1() {
        //First Question
        questionTextLevel1.setText("We need to calculate the total of two numbers: " + num1 + " and " + num2
                + " What do we want to achieve?");

        btnAnswer1Level1.setText("Find the difference of the two numbers");
        btnAnswer2Level1.setText("Find the sum of the two numbers");
        btnAnswer3Level1.setText("Find the product of the two integers");
        btnAnswer4Level1.setText("Find the child of the two numbers");

        View.OnClickListener buttonAnswer1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getText().toString();

                switch (buttonText) {
                    case "Find the difference of the two numbers":
                        resultsTextLevel1.setText("Incorrect, difference is NOT total. Try Again");
                        mistakes++;
                        break;

                    case "Find the sum of the two numbers":
                        resultsTextLevel1.setText("Correct! We're trying to find their SUM. Nice");
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Code after delay
                                phase2();
                            }
                        }, 1500);

                        break;

                    case "Find the product of the two integers":
                        resultsTextLevel1.setText("Incorrect, Product is not the same as getting their total. Try Again");
                        mistakes++;
                        break;

                    case "Find the child of the two doubles":
                        resultsTextLevel1.setText("There is no child or parent, " + username + ". Try again and think the logic");
                        mistakes++;
                        break;
                    default:
                        resultsTextLevel1.setText("What did you press " + username + "?. Try the buttons with the possible answer.");

                }
            }
        };

        btnAnswer1Level1.setOnClickListener(buttonAnswer1);
        btnAnswer2Level1.setOnClickListener(buttonAnswer1);
        btnAnswer3Level1.setOnClickListener(buttonAnswer1);
        btnAnswer4Level1.setOnClickListener(buttonAnswer1);
    }

    private void phase2() {
        //Second Question
        questionTextLevel1.setText("What numbers are we working with?");

        Random randomize = new Random();

        btnAnswer1Level1.setText(Integer.toString(num1) + " and " + randomize.nextInt(20));
        btnAnswer2Level1.setText(Integer.toString(num2) + " and " + randomize.nextInt(20));
        btnAnswer3Level1.setText(randomize.nextInt(20) + " and " + randomize.nextInt(20));
        btnAnswer4Level1.setText(Integer.toString(num2) + " and " + Integer.toString(num1));

        View.OnClickListener buttonAnswer2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getText().toString();

                if (buttonText.equals(Integer.toString(num2) + " and " + Integer.toString(num1))) {
                    resultsTextLevel1.setText("Correct! Those are the two numbers needed. ");
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code after delay
                            phase3();
                        }
                    }, 1500);

                } else {
                    resultsTextLevel1.setText("Incorrect, Those are not the numbers needed. Try Again");
                    mistakes++;
                }
            }
        };

        btnAnswer1Level1.setOnClickListener(buttonAnswer2);
        btnAnswer2Level1.setOnClickListener(buttonAnswer2);
        btnAnswer3Level1.setOnClickListener(buttonAnswer2);
        btnAnswer4Level1.setOnClickListener(buttonAnswer2);
    }

    private void phase3() {
        //Third Question
        questionTextLevel1.setText("What should we do with these numbers? ");

        btnAnswer1Level1.setText("Add the two Numbers");
        btnAnswer2Level1.setText("Combine them, side by side");
        btnAnswer3Level1.setText("Subtract the two Numbers");
        btnAnswer4Level1.setText("Divide and conquer the two numbers");

        View.OnClickListener buttonAnswer3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getText().toString();

                switch (buttonText) {
                    case "Combine them, side by side":
                        resultsTextLevel1.setText("Incorrect, that would mean you would add them side by side literally. Try Again");
                        mistakes++;
                        break;

                    case "Add the two Numbers":
                        resultsTextLevel1.setText("Correct! We add them to get the sum. Good Work");
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Code after delay
                                phase4();
                            }
                        }, 1500);
                        break;

                    case "Subtract the two Numbers":
                        resultsTextLevel1.setText("Incorrect, we're trying to get the sum of the two numbers. Try Again");
                        mistakes++;
                        break;

                    case "Divide and conquer the two numbers":
                        resultsTextLevel1.setText("We do not get the SUM from doing so. Try again and think more");
                        mistakes++;
                        break;
                    default:
                        resultsTextLevel1.setText("What did you press " + username + "?. Try the buttons with the possible answer.");

                }
            }
        };

        btnAnswer1Level1.setOnClickListener(buttonAnswer3);
        btnAnswer2Level1.setOnClickListener(buttonAnswer3);
        btnAnswer3Level1.setOnClickListener(buttonAnswer3);
        btnAnswer4Level1.setOnClickListener(buttonAnswer3);

    }

    private void phase4() {
        //Fourth Question
        questionTextLevel1.setText("What should we do with the result?");

        btnAnswer1Level1.setText("Store the result in memory");
        btnAnswer2Level1.setText("Display the result on the screen");
        btnAnswer3Level1.setText("Hide the result");
        btnAnswer4Level1.setText("Delete the result");

        View.OnClickListener buttonAnswer4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getText().toString();

                switch (buttonText) {
                    case "Store the result in memory":
                        resultsTextLevel1.setText("Incorrect, it would be gone when the memory is reset or cleared. Try Again");
                        mistakes++;
                        break;

                    case "Display the result on the screen":
                        resultsTextLevel1.setText("Correct! We show the answer. Which is: " + Integer.toString(numAnswerLevel1) + ". Congrats");
                        if(randomMode){
                            stopTimer();
                            if(currentLevelRandom == 8){
                                completedTime = 600 - seconds;

                                intent = new Intent(Level1.this, ResultActivity.class);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("level_id", "Randomness");
                                intent.putExtra("score", score);
                                intent.putExtra("stars", stars);
                                intent.putExtra("CompletionTime", completedTime);
                                startActivity(intent);

                            } else {
                                currentLevelRandom++;
                                int getLevel = arrayLevels[currentLevelRandom];

                                intent = new Intent(Level1.this, getLevelClass(getLevel));
                                intent.putExtra("score", score);
                                intent.putExtra("arrayLevels", arrayLevels);
                                intent.putExtra("randomMode", true);
                                intent.putExtra("currentLevelRandom", currentLevelRandom);
                                intent.putExtra("randomDuration", seconds);
                                intent.putExtra("score", score);
                                startActivity(intent);
                            }

                        } else {
                            //Correct Answer, calculate the scores and save user progress
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btnProceedLevel1.setVisibility(View.VISIBLE);
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

                                }
                                //delay
                            }, 1500);
                            break;
                        }

                    case "Hide the result":
                        resultsTextLevel1.setText("Incorrect, why would we hide it after going through all that work? Try Again");
                        mistakes++;
                        break;

                    case "Delete the result":
                        resultsTextLevel1.setText("Incorrect, all the work done is wasted with its results deleted. Try Again");
                        mistakes++;
                        break;
                    default:
                        resultsTextLevel1.setText("What did you press " + username + "?. Try the buttons with the possible answer.");

                }
            }
        };

        btnAnswer1Level1.setOnClickListener(buttonAnswer4);
        btnAnswer2Level1.setOnClickListener(buttonAnswer4);
        btnAnswer3Level1.setOnClickListener(buttonAnswer4);
        btnAnswer4Level1.setOnClickListener(buttonAnswer4);
    }

    private void phase1Offline() {
        //First Question
        questionTextLevel1.setText("We need to calculate the total of two numbers: " + num1 + " and " + num2
                + " What do we want to achieve?");

        btnAnswer1Level1.setText("Find the difference of the two numbers");
        btnAnswer2Level1.setText("Find the sum of the two numbers");
        btnAnswer3Level1.setText("Find the product of the two integers");
        btnAnswer4Level1.setText("Find the child of the two numbers");

        View.OnClickListener buttonAnswer1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getText().toString();

                switch (buttonText) {
                    case "Find the difference of the two numbers":
                        resultsTextLevel1.setText("Incorrect, difference is NOT total. Try Again");
                        mistakes++;
                        break;

                    case "Find the sum of the two numbers":
                        resultsTextLevel1.setText("Correct! We're trying to find their SUM. Nice");
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Code after delay
                                phase2Offline();
                            }
                        }, 1500);

                        break;

                    case "Find the product of the two integers":
                        resultsTextLevel1.setText("Incorrect, Product is not the same as getting their total. Try Again");
                        mistakes++;
                        break;

                    case "Find the child of the two doubles":
                        resultsTextLevel1.setText("There is no child or parent, " + username + ". Try again and think the logic");
                        mistakes++;
                        break;
                    default:
                        resultsTextLevel1.setText("What did you press " + username + "?. Try the buttons with the possible answer.");

                }
            }
        };

        btnAnswer1Level1.setOnClickListener(buttonAnswer1);
        btnAnswer2Level1.setOnClickListener(buttonAnswer1);
        btnAnswer3Level1.setOnClickListener(buttonAnswer1);
        btnAnswer4Level1.setOnClickListener(buttonAnswer1);
    }

    private void phase2Offline() {
        //Second Question
        questionTextLevel1.setText("What numbers are we working with?");

        Random randomize = new Random();

        btnAnswer1Level1.setText(Integer.toString(num1) + " and " + randomize.nextInt(20));
        btnAnswer2Level1.setText(Integer.toString(num2) + " and " + randomize.nextInt(20));
        btnAnswer3Level1.setText(randomize.nextInt(20) + " and " + randomize.nextInt(20));
        btnAnswer4Level1.setText(Integer.toString(num2) + " and " + Integer.toString(num1));

        View.OnClickListener buttonAnswer2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getText().toString();

                if (buttonText.equals(Integer.toString(num2) + " and " + Integer.toString(num1))) {
                    resultsTextLevel1.setText("Correct! Those are the two numbers needed. ");
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code after delay
                            phase3Offline();
                        }
                    }, 1500);

                } else {
                    resultsTextLevel1.setText("Incorrect, Those are not the numbers needed. Try Again");
                    mistakes++;
                }
            }
        };

        btnAnswer1Level1.setOnClickListener(buttonAnswer2);
        btnAnswer2Level1.setOnClickListener(buttonAnswer2);
        btnAnswer3Level1.setOnClickListener(buttonAnswer2);
        btnAnswer4Level1.setOnClickListener(buttonAnswer2);
    }

    private void phase3Offline() {
        //Third Question
        questionTextLevel1.setText("What should we do with these numbers? ");

        btnAnswer1Level1.setText("Add the two Numbers");
        btnAnswer2Level1.setText("Combine them, side by side");
        btnAnswer3Level1.setText("Subtract the two Numbers");
        btnAnswer4Level1.setText("Divide and conquer the two numbers");

        View.OnClickListener buttonAnswer3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getText().toString();

                switch (buttonText) {
                    case "Combine them, side by side":
                        resultsTextLevel1.setText("Incorrect, that would mean you would add them side by side literally. Try Again");
                        mistakes++;
                        break;

                    case "Add the two Numbers":
                        resultsTextLevel1.setText("Correct! We add them to get the sum. Good Work");
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Code after delay
                                phase4Offline();
                            }
                        }, 1500);
                        break;

                    case "Subtract the two Numbers":
                        resultsTextLevel1.setText("Incorrect, we're trying to get the sum of the two numbers. Try Again");
                        mistakes++;
                        break;

                    case "Divide and conquer the two numbers":
                        resultsTextLevel1.setText("We do not get the SUM from doing so. Try again and think more");
                        mistakes++;
                        break;
                    default:
                        resultsTextLevel1.setText("What did you press " + username + "?. Try the buttons with the possible answer.");

                }
            }
        };

        btnAnswer1Level1.setOnClickListener(buttonAnswer3);
        btnAnswer2Level1.setOnClickListener(buttonAnswer3);
        btnAnswer3Level1.setOnClickListener(buttonAnswer3);
        btnAnswer4Level1.setOnClickListener(buttonAnswer3);
    }

    private void phase4Offline() {
        //Fourth Question
        questionTextLevel1.setText("What should we do with the result?");

        btnAnswer1Level1.setText("Store the result in memory");
        btnAnswer2Level1.setText("Display the result on the screen");
        btnAnswer3Level1.setText("Hide the result");
        btnAnswer4Level1.setText("Delete the result");

        View.OnClickListener buttonAnswer4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getText().toString();

                switch (buttonText) {
                    case "Store the result in memory":
                        resultsTextLevel1.setText("Incorrect, it would be gone when the memory is reset or cleared. Try Again");
                        mistakes++;
                        break;

                    case "Display the result on the screen":
                        resultsTextLevel1.setText("Correct! We show the answer. Which is: " + Integer.toString(numAnswerLevel1) + ". Congrats");

                        if(randomMode){
                            stopTimer();
                            if(currentLevelRandom == 8){
                                completedTime = 600 - seconds;

                                intent = new Intent(Level1.this, ResultActivity.class);
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

                                intent = new Intent(Level1.this, getLevelClass(getLevel));
                                intent.putExtra("score", score);
                                intent.putExtra("arrayLevels", arrayLevels);
                                intent.putExtra("randomMode", true);
                                intent.putExtra("currentLevelRandom", currentLevelRandom);
                                intent.putExtra("randomDuration", seconds);
                                intent.putExtra("score", score);
                                intent.putExtra("playerid", playerid);
                                intent.putExtra("offlineMode", true);
                                startActivity(intent);
                            }

                        } else {
                            //Correct Answer, calculate the scores and save user progress
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btnProceedLevel1.setVisibility(View.VISIBLE);
                                    stopTimer();
                                    completedTime = 120 - seconds;

                                    try {
                                        sharedPreferences = getSharedPreferences("OfflineLevel1Data", MODE_PRIVATE);
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
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "Failed to save level progress", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                //delay
                            }, 1500);
                            break;
                        }

                    case "Hide the result":
                        resultsTextLevel1.setText("Incorrect, why would we hide it after going through all that work? Try Again");
                        mistakes++;
                        break;

                    case "Delete the result":
                        resultsTextLevel1.setText("Incorrect, all the work done is wasted with its results deleted. Try Again");
                        mistakes++;
                        break;
                    default:
                        resultsTextLevel1.setText("What did you press " + username + "?. Try the buttons with the possible answer.");

                }
            }
        };

        btnAnswer1Level1.setOnClickListener(buttonAnswer4);
        btnAnswer2Level1.setOnClickListener(buttonAnswer4);
        btnAnswer3Level1.setOnClickListener(buttonAnswer4);
        btnAnswer4Level1.setOnClickListener(buttonAnswer4);
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
        timerTextlevel1.setText(String.format("%02d:%02d", mins, secs));
    }

    private void progressStars() {
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progressTextLevel1.setText("Progress: " + intProgress + "%");

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
                star3Level1.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level1.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level1.setImageResource(R.drawable.star_outline);
            }
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


