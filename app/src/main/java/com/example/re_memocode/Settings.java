package com.example.re_memocode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    Intent intent;
    String playerid;
    boolean offlineMode;

    TextView buttonLogOut, buttonGoBackSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        buttonLogOut = findViewById(R.id.buttonLogOut);
        buttonGoBackSettings = findViewById(R.id.btnGoBackSettings);

        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        if (offlineMode){

            buttonGoBackSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Settings.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);

                }
            });

            buttonLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Session Token
                    SessionManager sessionManager = new SessionManager(Settings.this);
                    if (sessionManager.hasSessionToken()) {
                        sessionManager.clearSessionToken();
                    }
                    intent = new Intent(Settings.this, Login.class);
                    startActivity(intent);
                    finish();
                }

            });

        } else {
            buttonGoBackSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Settings.this, MainActivity.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);

                }
            });

            buttonLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Session Token
                    SessionManager sessionManager = new SessionManager(Settings.this);
                    if (sessionManager.hasSessionToken()) {
                        sessionManager.clearSessionToken();
                    }
                    intent = new Intent(Settings.this, Login.class);
                    startActivity(intent);
                    finish();
                }

            });
        }

    }
}