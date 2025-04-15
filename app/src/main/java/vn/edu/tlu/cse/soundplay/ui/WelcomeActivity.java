package vn.edu.tlu.cse.soundplay.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tlu.cse.soundplay.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {

            Intent intent;
                intent = new Intent(WelcomeActivity.this, LoginActivity.class);

            startActivity(intent);
            finish();
        });
    }
}