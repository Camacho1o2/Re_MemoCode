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

public class TutorialsDashboard extends AppCompatActivity {

    Intent intent;
    private int user_id;

    String playerid;
    boolean offlineMode;

    Button buttonTutorial1, buttonTutorial2, buttonTutorial3, buttonTutorial4, buttonTutorial5, buttonTutorial6, buttonTutorial7,
            buttonTutorial8;
    TextView buttonGoBackTutorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorials_dashboard);

        intent = getIntent();
        //user_id = intent.getIntExtra("user_id", 0);

        buttonTutorial1 = findViewById(R.id.buttonGoToLevel1Tutorial);
        buttonTutorial2 = findViewById(R.id.buttonGoToLevel2Tutorial);
        buttonTutorial3 = findViewById(R.id.buttonGoToLevel3Tutorial);
        buttonTutorial4 = findViewById(R.id.buttonGoToLevel4Tutorial);
        buttonTutorial5 = findViewById(R.id.buttonGoToLevel5Tutorial);
        buttonTutorial6 = findViewById(R.id.buttonGoToLevel6Tutorial);
        buttonTutorial7 = findViewById(R.id.buttonGoToLevel7Tutorial);
        buttonTutorial8 = findViewById(R.id.buttonGoToLevel8Tutorial);
        buttonGoBackTutorDB = findViewById(R.id.btnGoBackTutorialsDB);

        intent = getIntent();
        offlineMode = intent.getBooleanExtra("offlineMode", false);

        //To check if the it's in offlineMode
        if (offlineMode){
            playerid = intent.getStringExtra("playerid");

            buttonTutorial1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial1.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonTutorial2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial2.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonTutorial3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial3.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonTutorial4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial4.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });


            buttonTutorial5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial5.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonTutorial6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial6.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonTutorial7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial7.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonTutorial8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial8.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

            buttonGoBackTutorDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, MainActivity.class);
                    intent.putExtra("playerid", playerid);
                    intent.putExtra("offlineMode", true);
                    startActivity(intent);
                }
            });

        } else {
            buttonTutorial1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial1.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonTutorial2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial2.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonTutorial3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial3.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonTutorial4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial4.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonTutorial5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial5.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonTutorial6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial6.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonTutorial7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial7.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });

            buttonTutorial8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, Tutorial8.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });


            buttonGoBackTutorDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TutorialsDashboard.this, MainActivity.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            });
        }
    }
}