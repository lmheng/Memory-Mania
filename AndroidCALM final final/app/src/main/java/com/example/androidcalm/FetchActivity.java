package com.example.androidcalm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FetchActivity extends AppCompatActivity implements View.OnClickListener{

    ProgressBar progressBar;
    TextView progressBarTextView;
    int fetchCount=0;
    private long mLastClickTime=0;
    IntentFilter filter;
    ArrayList<String> selected = new ArrayList<String>();
    int numSelected =0;
    TextView selectedTextView;
    int pos=0;

    protected BroadcastReceiver receiver = new BroadcastReceiver() {

        int pos = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("download_ok")) {
                pos++;
                String filename = intent.getStringExtra("filename");
                imageToImageView(filename, pos);
            }

            if (action.equals("UrlException")) {
                Toast.makeText(FetchActivity.this,"Error connecting to the specified URL\n " +
                        "Try entering a different URL",Toast.LENGTH_LONG).show();
                stopService(new Intent(FetchActivity.this, DownloadService.class));
            }

            if (action.equals("IIException")) {
                Toast.makeText(FetchActivity.this,"Enter a website that contain at least 20 images",Toast.LENGTH_LONG).show();
                stopService(new Intent(FetchActivity.this, DownloadService.class));
                clearUI();
                pos = 0;
            }

            if (action.equals("DLException")) {
                Toast.makeText(FetchActivity.this,"Website does not allow for image download, please try another website",Toast.LENGTH_LONG).show();
                stopService(new Intent(FetchActivity.this, DownloadService.class));
                clearUI();
                pos = 0;
            }

            if (action.equals("ThreadInterupt")) {
                clearUI();
                pos = 0;
            }

            if (pos == 20 ) {
                stopService(new Intent(FetchActivity.this, DownloadService.class));
                pos=0;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        Button fetchBtn=(Button)findViewById(R.id.fetchButton);
        fetchBtn.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        selectedTextView= (TextView) findViewById(R.id.selectedtw);
        progressBar.setMax(20);
        progressBarTextView = (TextView) findViewById(R.id.progressBarTextView);
        filter = new IntentFilter();
        filter.addAction("download_ok");
        filter.addAction("UrlException");
        filter.addAction("IIException");
        filter.addAction("ThreadInterupt");
        filter.addAction("DLException");
        registerReceiver(receiver, filter);
    }



    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fetchButton) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            if (fetchCount != 0) {
                stopService(new Intent(FetchActivity.this, DownloadService.class));
            }
            clearUI();
            fetchCount++;
            mLastClickTime = SystemClock.elapsedRealtime();
            EditText et = (EditText) findViewById(R.id.urlSource);
            String website = et.getText().toString();
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra("website", website);
            startService(intent);
        }

        for(int i=1;i<=20;i++){
            if(view.getId() == getResources().getIdentifier("image"+(i),"id",getPackageName())){
                selectImage(i,(ImageView) view);
            }
        }
    }

    public void clearUI(){
        progressBarTextView.setText("");
        progressBar.setVisibility(View.GONE);
        clearImages(true,true);
    }

    public void clearImages(boolean clearSelected,boolean clearImages){
        selected.clear();
        selectedTextView.setText("");
        numSelected = 0;
        for(int i=1;i<=20;i++){
            ImageView im =(ImageView)findViewById(getResources().getIdentifier("image"+(i),"id",getPackageName()));
            if(clearImages==true)
                im.setImageDrawable(null);
            if(clearSelected==true)
                im.setForeground(null);
            im.setImageAlpha(255);
        }
    }

    public void selectImage (int i, ImageView v){
        if(v.getDrawable() != null) {
            if (!selected.contains("image" + i)) {
                System.out.println(v.getDrawable());
                selected.add("image" + i);
                numSelected++;
                v.setImageAlpha(200);
                v.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_background));
            } else {
                selected.remove("image" + i);
                numSelected--;
                v.setImageAlpha(255);
                v.setForeground(null);
            }
            selectedTextView.setText(numSelected + " / 6 selected");
        }
        if(numSelected==6){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("imageNames",selected);
            startActivityForResult(intent,1);
        }
    }

    protected void imageToImageView(String filename, int pos) {

        String path = getApplicationContext().getFilesDir()+"/images/"+filename+".jpg";
        File file = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        if (bitmap != null) {
            int id = getResources().getIdentifier("image" + pos,
                    "id", getPackageName());
            ImageView imgView = findViewById(id);
            imgView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(pos);
            progressBarTextView.setText
                    (String.format("Download %d of %d images", pos, 20));
        }
        if(pos==20){
            addListeners();
        }
    }
    public void addListeners(){
        for(int i=1;i<=20;i++){
            ImageView im =(ImageView)findViewById(getResources().getIdentifier("image"+(i),"id",getPackageName()));
            im.setOnClickListener(this);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                clearImages(true,false);
            }
        }
    }
        @Override
        public void onBackPressed () {
            stopService(new Intent(FetchActivity.this, DownloadService.class));
            finish();
            super.onBackPressed();
        }

    }