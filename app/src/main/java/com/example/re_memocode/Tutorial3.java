package com.example.re_memocode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Tutorial3 extends AppCompatActivity {

    Intent intent;
    private int user_id;

    String playerid;
    boolean offlineMode;

    Button buttonGoBackTutor3, buttonNextTutor3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial3);

//        intent = getIntent();
//        user_id = intent.getIntExtra("user_id", 0);

        buttonGoBackTutor3 = findViewById(R.id.buttonGobackTutor3);
        buttonNextTutor3 = findViewById(R.id.buttonNextTutor3);

        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        //To check if the it's in offlineMode
        if (offlineMode) {
            playerid = intent.getStringExtra("playerid");

            buttonGoBackTutor3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial3.this, TutorialsDashboard.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonNextTutor3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial3.this, Tutorial4.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

        } else{
            buttonGoBackTutor3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial3.this, TutorialsDashboard.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonNextTutor3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial3.this, Tutorial4.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });
        }

    }
}