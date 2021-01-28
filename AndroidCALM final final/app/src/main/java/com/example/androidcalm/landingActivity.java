package com.example.androidcalm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class landingActivity extends AppCompatActivity implements View.OnClickListener {

    MediaPlayer[] mp;
    Intent bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Button start=(Button)findViewById(R.id.start);
        start.setOnClickListener(this);
        Button pokemon=(Button)findViewById(R.id.pokemon);
        pokemon.setOnClickListener(this);

        bgm=new Intent(landingActivity.this, BackgroundMusic.class);
        bgm.setAction("start");
        startService(bgm);

        loadSound();
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();

        if(id==R.id.start)
        {   playSound(2);
            Intent intent=new Intent(this,FetchActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.pokemon)
        {
            playSound(0);
            Intent intent=new Intent(this,MainActivity.class);
            intent.putExtra("child","pokemon");
            startActivity(intent);
        }

    }
    private void playSound(int musicType){
        mp[musicType].start();
    }

    private void loadSound(){
        mp = new MediaPlayer[] {MediaPlayer.create(this, R.raw.pikachu),
                MediaPlayer.create(this, R.raw.wrong),
                MediaPlayer.create(this,R.raw.correct),
                MediaPlayer.create(this,R.raw.gameclear)};
    }

}