package com.example.re_memocode;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.IntentFilter;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Login extends AppCompatActivity {


    EditText etUserName;
    EditText etPassword;

    TextView btLogin;
    TextView btRegister;

    AccountAPI accountAPI;
    SessionManager sessionManager;

    Boolean internetStatus;
    String name;
    String Username;
    String Password;
    private NetworkChangeReceiver networkChangeReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String username;
        Intent intent = getIntent();

        networkChangeReceiver = new NetworkChangeReceiver(isConnected -> {
            if (isConnected) {
                Toast.makeText(this, "Internet Connected", Toast.LENGTH_SHORT).show();

                //Session Token
                sessionManager = new SessionManager(Login.this);
                if (sessionManager.hasSessionToken()) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Httplink.httpLink)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                accountAPI = retrofit.create(AccountAPI.class);

            } else {

                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                delayedOfflineDialogMethod();
            }
        });

        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);

        username = intent.getStringExtra("username");

        etUserName = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btnLogin);
        btRegister = findViewById(R.id.tabCreateAccount);

        if (username != null){
            etUserName.setText(username);
        }

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etUserName.getText()) || TextUtils.isEmpty(etPassword.getText())) {
                    Toast.makeText(getApplicationContext(), "Please fill in both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //String hashedPassword = hashPassword(etPassword.getText().toString());
                Call<JsonObject> call = accountAPI.login(
                        etUserName.getText().toString(),
                        //hashedPassword
                        etPassword.getText().toString()

                );

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject jsonObject = response.body();
                            int success = jsonObject.get("success").getAsInt();
                            if (success == 1) {
                                Toast.makeText(getApplicationContext(), "Log In successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                                i.putExtra("user_id", jsonObject.get("user_id").getAsInt());
                                sessionManager.createSessionToken(jsonObject.get("user_id").getAsString());
                                startActivity(i);

                            } else {
                                String utilErrorrMsg = "Error: Invalid Username or Password";
                                Toast.makeText(getApplicationContext(), utilErrorrMsg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Server Error: Empty response.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e(TAG, "Failed to Login. Try again", t);
                        Toast.makeText(getApplicationContext(), "Failed to Login. Try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Signup.class);
                startActivity(i);
            }
        });



    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void showOfflineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you wanna go to offline mode?");


        // Add an input field
        final EditText input = new EditText(this);
        builder.setView(input);

        // Add buttons
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent i = new Intent(getApplicationContext(), PlayerSelect.class);
            internetStatus = false;
            i.putExtra("internetStatus", internetStatus);
            startActivity(i);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void delayedOfflineDialogMethod() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showOfflineDialog();
            }
        }, 3000); // 3000ms delay (3 seconds)
    }

}