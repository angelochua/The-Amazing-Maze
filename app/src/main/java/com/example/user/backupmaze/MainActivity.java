package com.example.user.backupmaze;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private Button pBtn, hBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      playmusic();

        pBtn = findViewById(R.id.playBtn);
        hBtn = findViewById(R.id.HelpBtn);

        pBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), startGame.class);
                startActivity(intent);
            }


        });

        hBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), help.class);
                startActivity(intent);
            }


        });






    }

    public void playmusic() {
        mp = MediaPlayer.create(MainActivity.this, R.raw.bg);
        mp.setLooping(true);
        mp.start();
    }
}
