package com.example.re_memocode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Tutorial5 extends AppCompatActivity {

    Intent intent;
    private int user_id;

    String playerid;
    boolean offlineMode;

    Button buttonGobackTutor5, buttonNextTutor5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial5);

//        intent = getIntent();
//        user_id = intent.getIntExtra("user_id", 0);

        buttonGobackTutor5 = findViewById(R.id.buttonGobackTutor5);
        buttonNextTutor5 = findViewById(R.id.buttonNextTutor5);

        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        //To check if the it's in offlineMode
        if (offlineMode){
            playerid = intent.getStringExtra("playerid");
            buttonGobackTutor5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial5.this, TutorialsDashboard.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonNextTutor5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial5.this, Tutorial6.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

        } else {
            buttonGobackTutor5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial5.this, TutorialsDashboard.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonNextTutor5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Tutorial5.this, Tutorial6.class);
//                intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });
        }
    }
}