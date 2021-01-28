package com.example.androidcalm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    TextView text_1;
    ArrayList<Card> cards = new ArrayList<>();
    ArrayList<String> imageLink = new ArrayList<>();
    ArrayList<String> selectedImages = new ArrayList<>();

    Card firstCard, secondCard;
    int cardNumber = 1;
    int playerPoints = 0;
    private timerFragment fragment;

    MediaPlayer[] mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            assignDefaultValues();
            timerFragment();

        for(Card card:cards) {
//            ImageView im = (ImageView) findViewById(card.getImgView().getId());
//            card.setImgView(im);
            card.getImgView().setOnClickListener(view -> assignImage(card));
        }
    }

    private void timerFragment(){
        fragment = new timerFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.stopwatch_container, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void assignDefaultValues(){

        Intent gameIntent = getIntent();
        selectedImages = gameIntent.getStringArrayListExtra("imageNames");

        loadText();
        loadSound();
        ImageView[] imageViews = {findViewById(R.id.img_1), findViewById(R.id.img_2),
                findViewById(R.id.img_3), findViewById(R.id.img_4),
                findViewById(R.id.img_5), findViewById(R.id.img_6),
                findViewById(R.id.img_7), findViewById(R.id.img_8),
                findViewById(R.id.img_9), findViewById(R.id.img_10),
                findViewById(R.id.img_11), findViewById(R.id.img_12)};

        //shuffle images in list
        loadImageFromPath();
        Collections.shuffle(imageLink);

        //create 12 cards and load into card list, attach imageView and images with default status flipped = false & matched = false
        for(int i = 0; i < 12; i++){
            cards.add(new Card(imageViews[i], imageLink.get(i), false, false));
        }
    }

    private void assignImage(Card card) {

        //prevent users from clicking another card during delay set for wrong match
        for(Card c : cards){
            if(c.isFlipped() == true && c.isMatched() == false && cardNumber == 1)
                return;
        }

        //to replace placeholder with actual image assigned
        if(card.isFlipped() == false) {
            flipAnimation(card.getImgView());
            if (card.getImage().contains("button")) {
                int id = getResources().getIdentifier(card.getImage(), "drawable", getPackageName());
                card.getImgView().setImageResource(id);
            } else {
                card.getImgView().setImageBitmap(BitmapFactory.decodeFile(card.getImage()));
            }

            if (cardNumber == 1) {
                firstCard = card;
                cardNumber = 2;
                card.setFlipped(true);
                fragment.onStartTimer();
                playSound(0);
            } else if (cardNumber == 2) {
                secondCard = card;
                cardNumber = 1;
                card.setFlipped(true);
                match_check();
            }

        }
    }

    private void loadText(){
        text_1 = findViewById(R.id.text_1);
    }

    private void loadSound(){
        mp = new MediaPlayer[] {MediaPlayer.create(this, R.raw.flip),
                                MediaPlayer.create(this, R.raw.wrong),
                                MediaPlayer.create(this,R.raw.correct),
                                MediaPlayer.create(this,R.raw.gameclear)};
    }

    private void retrieveImages(String path, int task){
        File file = new File(path);
        File[] listOfFiles = file.listFiles();

        if(task==1) {
            for (int i=1;i<=6;i++) {
                imageLink.add("button_"+i);
                imageLink.add("button_"+i);
            }
        }else{
            for (File f : listOfFiles) {
                if (selectedImages.contains(f.getPath().substring(path.length() + 1, f.getPath().length() - 4))) {
                    imageLink.add(f.getPath());
                    imageLink.add(f.getPath());
                }
            }
        }
    }

    private void loadImageFromPath(){
        if(getIntent().getStringExtra("child") != null) {
            String path ="";
            retrieveImages(path,1);
        }
        else{
            String path = getApplicationContext().getFilesDir()+"/images";
            retrieveImages(path,2);
        }
    }

    private void match_check() {

        //on match, add player point and set Matched state to card pair
        if (firstCard.getImage().equals(secondCard.getImage())) {
            playerPoints++;
            text_1.setText(playerPoints + " / 6 Matches");
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            playSound(2);
        }
        else{

            playSound(1);

            //delay flip of card to allow users to see actual image than disappear
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                flipAnimation(firstCard.getImgView());
                firstCard.getImgView().setImageResource(R.drawable.illus);
                firstCard.setFlipped(false);
                flipAnimation(secondCard.getImgView());
                secondCard.getImgView().setImageResource(R.drawable.illus);
                secondCard.setFlipped(false);
            }, 1000);

        }

        checkGameEnd();
    }

    private void checkGameEnd() {
        //each run, check if all cards have been matched
        if(playerPoints == 6)
        {
            fragment.onStopTimer();
            playSound(3);
            redirectToHome("Congratulations, you have won!", "Press OK to restart the game");
        }
    }

    private void redirectToHome(String title, String msg){
        AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    cards.clear();
                    imageLink.clear();
                    Intent response = new Intent();
                    setResult(RESULT_OK, response);
                    finish();
                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> {
                    return;
                });

        dlg.show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        cards.clear();
        imageLink.clear();
        Intent response = new Intent();
        setResult(RESULT_OK, response);
        finish();
    }



    private void playSound(int musicType){
        mp[musicType].start();
    }

    private void flipAnimation(ImageView im){

        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.animator);
        anim.setTarget(im);
        anim.setDuration(500);
        anim.start();

    }

}