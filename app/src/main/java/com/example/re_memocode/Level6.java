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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Level6 extends AppCompatActivity {

    private Intent intent;
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

    private int seconds = 220; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;
    String status = "completed";
    int user_id;
    final int level_id = 6;

    String fileName;
    int addFileNumber = 0;

    PyObject pyObj;
    PyObject result;
    Python py;
    String internalStoragePath;

    String type;
    String pluralType;
    String inputWord;
    int inputNumber;
    boolean coniditonMet;
    boolean parseable;

    //Part 1
    EditText codeInputLevel6;
    ImageButton proceedButtonLevel6, goBackButtonLevel6, clearButtonLevel6, runCodeButtonLevel6;
    TextView resultTextLevel6, timerTextLevel6, progessTextLevel6, questionTextLevel6;
    EditText textPutUserInputLevel6;
    ImageView star1Level6, star2Level6, star3Level6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level6);

        File internalStorageDir = getFilesDir();
        internalStoragePath = internalStorageDir.getAbsolutePath();

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

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(Level6.this));
        }

        if (offlineMode) {
            playerid = intent.getStringExtra("playerid");

            proceedButtonLevel6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Level6.this, ResultActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("level_id", level_id);
                    intent.putExtra("offlineMode", offlineMode);
                    intent.putExtra("score",score);
                    intent.putExtra("stars", stars);
                    intent.putExtra("CompletionTime", completedTime);
                    startActivity(intent);
                }
            });

            clearButtonLevel6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codeInputLevel6.setText("");
                    textPutUserInputLevel6.setText("");
                    resultTextLevel6.setText("- results -");
                }
            });

            goBackButtonLevel6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    intent = new Intent(Level6.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", offlineMode);
                    startActivity(intent);
                }
            });

            runCodeButtonLevel6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runCode1();
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
                        Intent intent = new Intent(Level6.this, ResultActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("level_id", level_id);
                        intent.putExtra("offlineMode", offlineMode);
                        intent.putExtra("score",score);
                        intent.putExtra("stars", stars);
                        intent.putExtra("CompletionTime", completedTime);
                        startActivity(intent);
                    }

                }
            };
            handler.post(runnable); // Start the timer

        } else {

            proceedButtonLevel6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Level6.this, ResultActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("level_id", level_id);
                    intent.putExtra("score",score);
                    intent.putExtra("stars", stars);
                    intent.putExtra("CompletionTime", completedTime);
                    startActivity(intent);
                }
            });

            clearButtonLevel6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codeInputLevel6.setText("");
                    resultTextLevel6.setText("- results -");
                }
            });

            goBackButtonLevel6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    intent = new Intent(Level6.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            runCodeButtonLevel6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runCode1();
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
                        Intent intent = new Intent(Level6.this, ResultActivity.class);
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
        timerTextLevel6.setText(String.format("%02d:%02d", mins, secs));
    }

    private void progressStars1(){
        if (seconds > 0) {

            double progressQuotient = score/ 1000;
            double progressPercentage = progressQuotient * 100;
            int intProgress = (int) progressPercentage;
            progessTextLevel6.setText("Progress: " + intProgress + "%");

            switch (seconds) {
                case 175:
                    Random random = new Random();
                    score = score - random.nextInt(5) + 1;
                    break;

                case 160:
                    score = score - 20;
                case 142:
                    score = score - 30;
                    break;
                case 122:
                    score = score - 80;
                    break;
                case 102:
                    score = score - 130;
                    break;
                case 82:
                    score = score - 180;
                    break;
                case 62:
                    score = score - 200;
                case 42:
                    score = score - 300;
                case 12:
                    score = score - 400;
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
                star3Level6.setImageResource(R.drawable.star_outline);

            } else if (score >= 100 && score <= 300) {
                stars = 1;
                star2Level6.setImageResource(R.drawable.star_outline);

            } else if(score >= 0 && score <= 100){
                stars = 0;
                star1Level6.setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void setupPart1(){
        codeInputLevel6 = findViewById(R.id.codeInputLevel6);

        proceedButtonLevel6 = findViewById(R.id.buttonProceedLevel6);
        goBackButtonLevel6 = findViewById(R.id.buttonGoBackLevel6);
        clearButtonLevel6 = findViewById(R.id.buttonClearAreaLevel6);
        runCodeButtonLevel6 = findViewById(R.id.buttonRunLevel6);

        resultTextLevel6 = findViewById(R.id.resultTextLevel6);
        timerTextLevel6 = findViewById(R.id.timerTextLevel6);
        progessTextLevel6 = findViewById(R.id.progressTextLevel6);
        questionTextLevel6 = findViewById(R.id.problemDescriptionLevel6);

        textPutUserInputLevel6 = findViewById(R.id.textPutUserInputLevel6);
        star1Level6 = findViewById(R.id.star1Level6);
        star2Level6 = findViewById(R.id.star2Level6);
        star3Level6 = findViewById(R.id.star3Level6);

        star1Level6.setImageResource(R.drawable.star_filled);
        star2Level6.setImageResource(R.drawable.star_filled);
        star3Level6.setImageResource(R.drawable.star_filled);

        proceedButtonLevel6.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
        finish();
    }

    private void runCode1() {
        String filename = "compileMe" + addFileNumber;
        fileName = filename + ".py";

        // Get the user's Python code
        String code = codeInputLevel6.getText().toString();

        // Get user-provided inputs (if any)
        String userInputs = textPutUserInputLevel6.getText().toString().trim();
        String inputSection = "";

        // Check if userInputs is not empty
        if (!userInputs.isEmpty()) {
            String[] inputLines = userInputs.split("\n");
            StringBuilder inputBuilder = new StringBuilder();
            for (String input : inputLines) {
                inputBuilder.append(input).append("\n");
            }

            // Create a simulated `sys.stdin` section
            inputSection = "sys.stdin = io.StringIO('''" + inputBuilder.toString() + "''')\n";
        } else {
            // Provide a fallback for empty input
            inputSection = "sys.stdin = io.StringIO('')\n"; // Simulate an empty stdin
        }

        // Python script to wrap user's code and capture output
        String fileContent = "import sys\n" +
                "import io\n" +
                "\n" +
                "def capture_output():\n" +
                "    " + inputSection + // Add the input simulation
                "    sys.stdout = io.StringIO()\n" +
                "    try:\n" +
                "        exec('''" + code + "''')\n" +
                "        captured_output = sys.stdout.getvalue()\n" +
                "    except Exception as e:\n" +
                "        captured_output = f\"Error during execution: {str(e)}\"\n" +
                "    finally:\n" +
                "        sys.stdout = sys.__stdout__\n" +
                "    return captured_output\n";

        writeToFile(fileContent);

        try {
            // Set up Chaquopy environment
            py = Python.getInstance();
            py.getModule("sys").get("path").callAttr("append", internalStoragePath);
            pyObj = py.getModule(filename);

            // Call the `capture_output` function in the Python script
            PyObject result = pyObj.callAttr("capture_output");

            addFileNumber++;

            // Update the UI with the result
            if (result != null) {
                resultTextLevel6.setText(result.toString());
                String inputWord = textPutUserInputLevel6.getText().toString();

                String [] words = result.toString().split("\\s+");

                if (inputWord.matches("[a-zA-Z]+")) {
                    type = "letter";
                    pluralType = "letters";
                } else if (inputWord.matches("[a-zA-Z0-9]+")) {
                    type = "mix";
                    pluralType = "mixed";
                } else if (inputWord.matches("[0-9]+")) {
                    type = "number";
                    pluralType = "numbers";
                }

                for (String word : words) {
                     if(word.toLowerCase().equals(type) || word.toLowerCase().equals(pluralType)){
                        coniditonMet = true;
                        Toast.makeText(getApplicationContext(), "Correct! That is an example of user input", Toast.LENGTH_SHORT).show();

                        Thread.sleep(1000);
                         codeInputLevel6.setText("");
                         resultTextLevel6.setText("- results -");
                         textPutUserInputLevel6.setText("");
                         questionTextLevel6.setText("Problem: Write a code that requests an input from the user then returns if the input “odd” or “even”.");

                         runCodeButtonLevel6.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 runCode2();
                             }
                         });
                     }

                }

                if(!coniditonMet){
                    mistakes++;
                }

            } else {
                resultTextLevel6.setText("No output produced by the script.");

            }
        } catch (Exception e) {
            // Display any exception as a toast and in the result field
            String errorMessage = "Error: " + e.getMessage();
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            resultTextLevel6.setText(errorMessage);
        }

    }


    // Method to write to a file in internal storage
    private void writeToFile(String data) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE); // Creates a new file or replaces the existing one
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to read from the file in internal storage
    private String readFromFile() {
        FileInputStream fis = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            fis = openFileInput(fileName);
            int content;
            while ((content = fis.read()) != -1) {
                stringBuilder.append((char) content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder.toString();
    }

    public static String indentString(String inputString, int indentSize) {
        // Define the indentation (e.g., 4 spaces)
        String indentation = " ".repeat(indentSize);
        // Split the string into lines
        String[] lines = inputString.split("\n");
        // Add indentation to each line
        StringBuilder indentedString = new StringBuilder();
        for (String line : lines) {
            indentedString.append(indentation).append(line).append("\n");
        }
        // Return the indented string
        return indentedString.toString();
    }

    private void runCode2() {
        String filename = "compileMe" + addFileNumber;
        fileName = filename + ".py";

        // Get the user's Python code
        String code = codeInputLevel6.getText().toString();

        // Get user-provided inputs (if any)
        String userInputs = textPutUserInputLevel6.getText().toString().trim();
        String inputSection = "";

        // Check if userInputs is not empty
        if (!userInputs.isEmpty()) {
            String[] inputLines = userInputs.split("\n");
            StringBuilder inputBuilder = new StringBuilder();
            for (String input : inputLines) {
                inputBuilder.append(input).append("\n");
            }

            // Create a simulated `sys.stdin` section
            inputSection = "sys.stdin = io.StringIO('''" + inputBuilder.toString() + "''')\n";
        } else {
            // Provide a fallback for empty input
            inputSection = "sys.stdin = io.StringIO('')\n"; // Simulate an empty stdin
        }

        // Python script to wrap user's code and capture output
        String fileContent = "import sys\n" +
                "import io\n" +
                "\n" +
                "def capture_output():\n" +
                "    " + inputSection + // Add the input simulation
                "    sys.stdout = io.StringIO()\n" +
                "    try:\n" +
                "        exec('''" + code + "''')\n" +
                "        captured_output = sys.stdout.getvalue()\n" +
                "    except Exception as e:\n" +
                "        captured_output = f\"Error during execution: {str(e)}\"\n" +
                "    finally:\n" +
                "        sys.stdout = sys.__stdout__\n" +
                "    return captured_output\n";

        writeToFile(fileContent);

        try {
            // Set up Chaquopy environment
            py = Python.getInstance();
            py.getModule("sys").get("path").callAttr("append", internalStoragePath);
            pyObj = py.getModule(filename);

            // Call the `capture_output` function in the Python script
            PyObject result = pyObj.callAttr("capture_output");

            addFileNumber++;

            // Update the UI with the result
            if (result != null) {
                resultTextLevel6.setText(result.toString());
                inputNumber = Integer.parseInt(textPutUserInputLevel6.getText().toString());

                String [] words = result.toString().split("\\s+");

                if (inputNumber % 2 == 0)  {
                    type = "even";

                } else  {
                    type = "odd";
                }

                for (String word : words) {
                    if(word.toLowerCase().equals(type)){
                        coniditonMet = true;

                        stopTimer();
                        Toast.makeText(getApplicationContext(), "Congrats! You made a working odd or even number detector", Toast.LENGTH_LONG).show();
                        Thread.sleep(1000);

                       if (offlineMode){
                           if(randomMode){
                               if(currentLevelRandom == 8){
                                   completedTime = 600 - seconds;

                                   intent = new Intent(Level6.this, ResultActivity.class);
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

                                   intent = new Intent(Level6.this, getLevelClass(getLevel));
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
                               sharedPreferences = getSharedPreferences("OfflineLevel6Data", MODE_PRIVATE);
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

                               proceedButtonLevel6.setVisibility(View.VISIBLE);
                           }
                       } else {
                           if (randomMode) {
                               if(currentLevelRandom == 8){
                                   completedTime = 600 - seconds;

                                   intent = new Intent(Level6.this, ResultActivity.class);
                                   intent.putExtra("user_id", user_id);
                                   intent.putExtra("level_id", "Randomness");
                                   intent.putExtra("score", score);
                                   intent.putExtra("stars", stars);
                                   intent.putExtra("CompletionTime", completedTime);
                                   startActivity(intent);


                               } else {
                                   currentLevelRandom++;
                                   int getLevel = arrayLevels[currentLevelRandom];

                                   intent = new Intent(Level6.this, getLevelClass(getLevel));
                                   intent.putExtra("score", score);
                                   intent.putExtra("arrayLevels", arrayLevels);
                                   intent.putExtra("randomMode", true);
                                   intent.putExtra("currentLevelRandom", currentLevelRandom);
                                   intent.putExtra("randomDuration", seconds);
                                   intent.putExtra("score", score);
                                   startActivity(intent);
                               }

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
                           break;
                       }
                    }

                    if(!coniditonMet){
                        mistakes++;
                    }
                }

            } else {
                resultTextLevel6.setText("No output produced by the script.");

            }
        } catch (Exception e) {
            // Display any exception as a toast and in the result field
            String errorMessage = "Error: " + e.getMessage();
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            resultTextLevel6.setText(errorMessage);
        }

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