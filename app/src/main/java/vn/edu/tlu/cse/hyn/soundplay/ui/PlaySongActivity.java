package vn.edu.tlu.cse.hyn.soundplay.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tlu.cse.hyn.soundplay.R;

public class PlaySongActivity extends AppCompatActivity {

    private TextView txtLyrics;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        txtLyrics = findViewById(R.id.txtLyrics);

    }
}


