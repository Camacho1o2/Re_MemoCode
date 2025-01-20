package com.example.re_memocode;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.re_memocode.databinding.ActivitySignupBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Signup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText etUsername;
    EditText etPassword;
    TextView tvLogin;
    TextView btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvLogin = findViewById(R.id.tabLogIn);
        btnRegister = findViewById(R.id.btnCreateAccount);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Httplink.httpLink)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AccountAPI accountAPI = retrofit.create(AccountAPI.class);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String hashedPassword = hashPassword(etPassword.getText().toString());
                Call<JsonObject> call = accountAPI.register(
                        etUsername.getText().toString(),
                        //hashedPassword
                        etPassword.getText().toString()

                );

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject jsonObject = response.body();
                        if (jsonObject != null) {
                            int success = jsonObject.get("success").getAsInt();
                            if (success == 1) {
                                Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), Login.class);
                                i.putExtra("username", etUsername.getText().toString());
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: Empty response.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e(TAG, "Failed to register. Try again", t);
                        Toast.makeText(getApplicationContext(), "Failed to register. Try again", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }



    private String hashPassword(String password) {
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
}