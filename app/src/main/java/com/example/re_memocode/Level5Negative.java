package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class Level5Negative extends AppCompatActivity {

    private Intent intent;

    AccountAPI accountAPI;
    private Handler handler;
    private Runnable runnable;
    private int seconds = 120; // Timer in seconds
    int mistakes, stars;
    double completedTime = 0, toMinusTime = 0, toMinusMistakes, score = 1000;
    String status = "complete";
    int user_id;
    final int level_id = 5;

    String fileName;
    int addFileNumber = 0;

    PyObject pyObj;
    PyObject result;
    Python py;
    String internalStoragePath;

    EditText codeInputLevel5;
    Button proceedButtonLevel5, goBackButtonLevel5, clearButtonLevel5, runCodeButtonLevel5;
    TextView resultTextLevel5, timerTextLevel5, resultShowTextLevel5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level5_negative);

//        intent = getIntent();
//        user_id = intent.getIntExtra("user_id", 0);

        File internalStorageDir = getFilesDir();
        internalStoragePath = internalStorageDir.getAbsolutePath();

//        py = Python.getInstance();
//        py.getModule("sys").get("path").callAttr("append", internalStoragePath);

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

        codeInputLevel5 = findViewById(R.id.codeInputLevel6);
        proceedButtonLevel5 = findViewById(R.id.proceedButtonLevel5);
        goBackButtonLevel5 = findViewById(R.id.goBackButtonLevel5);
        clearButtonLevel5 = findViewById(R.id.clearButtonLevel5);
        runCodeButtonLevel5 = findViewById(R.id.runButtonLevel5);
        resultTextLevel5 = findViewById(R.id.resultTextLevel5);
        resultShowTextLevel5 = findViewById(R.id.textShowResultsLevel5);
        timerTextLevel5 = findViewById(R.id.timerTextLevel5);

        //python stuff
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(Level5Negative.this));
        }


        goBackButtonLevel5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Level5Negative.this, MainActivity.class);
//                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

        clearButtonLevel5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeInputLevel5.setText("");
            }
        });

        proceedButtonLevel5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Level5Negative.this, ResultActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("level_id", level_id);
                intent.putExtra("score", score);
                intent.putExtra("stars", stars);
                intent.putExtra("CompletionTime", completedTime);
                startActivity(intent);
            }
        });

        runCodeButtonLevel5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resultTextLevel5.setText("- Results - ");
                runCode();
            }
        });

        proceedButtonLevel5.setVisibility(View.GONE);

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
                    Intent intent = new Intent(Level5Negative.this, ResultActivity.class);
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

    private void runCode() {
        String filename = "compileMe" + addFileNumber;
        fileName = filename + ".py";
        String code = codeInputLevel5.getText().toString();
        String indentedCode = indentString(code, 4);
        String fileContent = "import sys\n" +
                "import io\n" +
                "\n" +
                "def main():\n" +
                indentedCode +
                "\n" +
                "def capture_output():\n" +
                "    old_stdout = sys.stdout\n" +
                "    new_stdout = io.StringIO()\n" +
                "    sys.stdout = new_stdout\n" +
                "\n" +
                "    try:\n" +
                "        main()\n" +
                "        output = new_stdout.getvalue()\n" +
                "    finally:\n" +
                "        sys.stdout = old_stdout\n" +
                "\n" +
                "    return output";

        writeToFile(fileContent);
//        String pythonPath = Python.getPlatform().getPath();

        String fileStuff = readFromFile();

//        File internalDir = getFilesDir(); // This gets the internal storage directory for your app
//        String filePath = new File(internalDir, fileName).getAbsolutePath();

//        codeInputLevel5.setText(filename);

        try {

            py = Python.getInstance();
            // Add the internal storage path to the Python sys.path to load the Python file
            py.getModule("sys").get("path").callAttr("append", internalStoragePath);

            // Load the compiled Python file from internal storage
            pyObj = py.getModule(filename);

            // Execute the function capture_output to get the result
            result = pyObj.callAttr("capture_output");

            addFileNumber++;

            resultTextLevel5.setText(result.toString());

            if (code.trim().startsWith("try:")) {
                if (resultTextLevel5.getText().toString().trim().equals("This is a try block")) {
                    //Correct Answer
                    Toast.makeText(this, "Correct Answer. Congrats!!", Toast.LENGTH_SHORT).show();
                    proceedButtonLevel5.setVisibility(View.VISIBLE);
                    codeInputLevel5.setText("");
                    stopTimer();
                    completedTime = 120 - seconds;

                    //Calculate the score
                    if (completedTime >= 15 && completedTime <= 20){
                        toMinusTime = 0.1;
                    }
                    else if (completedTime >= 21 && completedTime <= 30){
                        toMinusTime = 0.3;

                    } else if (completedTime >= 31 && completedTime <= 50){
                        toMinusTime = 0.6;
                    } else if (completedTime >= 51 && completedTime <= 70){
                        toMinusTime = 0.9;
                    } else if (completedTime >= 71 && completedTime <= 90){
                        toMinusTime = 1.2;
                    } else if (completedTime >= 91 && completedTime <= 100){
                        toMinusTime = 1.5;
                    } else if (completedTime >= 111 && completedTime <= 120){
                        toMinusTime = 1.8;
                    }else {
                        toMinusTime = 2;
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

                } else {
                    Toast.makeText(this, "Incorrect Answer. Try Again", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please put a try block", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        timerTextLevel5.setText(String.format("%02d:%02d", mins, secs));
    }

}