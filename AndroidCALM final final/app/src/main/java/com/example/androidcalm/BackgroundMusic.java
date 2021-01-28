package com.example.androidcalm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackgroundMusic extends Service{

    MediaPlayer mPlayer;
    Thread bkgdThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction() == "start")
            startPlayer();
        else if(intent.getAction() == "pause")
            mPlayer.pause();
        else if(intent.getAction() == "resume" && !mPlayer.isPlaying())
            mPlayer.start();

        return super.onStartCommand(intent, flags, startId);
    }

    public void startPlayer(){
        mPlayer = MediaPlayer.create(this, R.raw.bg);

        bkgdThread =
                new Thread(() ->
                {mPlayer.start();
                    mPlayer.setLooping(true);}
                );

        bkgdThread.start();
    }

    @Override
    public boolean stopService(Intent name) {
        mPlayer.stop();
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        bkgdThread.interrupt();
    }
}
