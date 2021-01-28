package com.example.androidcalm;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class timerFragment extends Fragment implements View.OnClickListener {
    private int seconds=0;
    private boolean start;
    // to determine if the timer was still running before the onpause.
    private boolean running;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            seconds=savedInstanceState.getInt("seconds");
            start=savedInstanceState.getBoolean("start");
            running=savedInstanceState.getBoolean("running");
            setRetainInstance(true);
        }


    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds",seconds);
        savedInstanceState.putBoolean("start",start);
        savedInstanceState.putBoolean("running",running);
    }

    public void startTimer(View view)
    {
        final TextView timeView=(TextView)view.findViewById(R.id.time_view);
        final Handler handler=new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                int hour=seconds/3600;
                int minute=(seconds%3600)/60;
                int secs= seconds%60;

                String time = String.format("%d:%02d:%02d",hour,minute,secs);
                timeView.setText(time);
                if(start)
                {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }


    public void onStopTimer() {
        start=false;
    }

    public void onStartTimer() {
        start=true;
    }

    @Override
    public void onPause() {
        super.onPause();
        running=start;
        start=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(running)
        {
            start=true;
        }
    }

    public void onRestartTimer() {
        start=false;
        seconds=0;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Layout = inflater.inflate(R.layout.fragment_timer,container,false);
        startTimer(Layout);
        /*Button startbtn=(Button)Layout.findViewById(R.id.start);
        startbtn.setOnClickListener(this);
        Button stopbtn=(Button)Layout.findViewById(R.id.stop);
        stopbtn.setOnClickListener(this);
        Button restartbtn=(Button)Layout.findViewById(R.id.restart);
        restartbtn.setOnClickListener(this);*/
        return Layout;
    }

    @Override
    public void onClick(View v) {

      /*  int view = v.getId();
       switch(view) {
           case R.id.start:
               onStartTimer();
               break;
           case R.id.stop:
               onStopTimer();
               break;
           case R.id.restart:
               onRestartTimer();
               break;
           default:
               break;
          }*/
    }


}