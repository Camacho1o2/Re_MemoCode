package com.example.re_memocode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tutorial4 extends AppCompatActivity {
    Intent intent;
    private int user_id;

    String playerid;
    boolean offlineMode;

    Button buttonGoBackTutor4, buttonNextTutor4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial4);

//        intent = getIntent();
//        user_id = intent.getIntExtra("user_id", 0);

        buttonGoBackTutor4 = findViewById(R.id.buttonGobackTutor4);
        buttonNextTutor4 = findViewById(R.id.buttonNextTutor4);

        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        //To check if the it's in offlineMode
        if (offlineMode) {
            playerid = intent.getStringExtra("playerid");

            buttonGoBackTutor4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial4.this, TutorialsDashboard.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonNextTutor4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial4.this, Tutorial5.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

        } else {
            buttonGoBackTutor4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial4.this, TutorialsDashboard.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonNextTutor4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial4.this, Tutorial5.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });
        }

    }
}