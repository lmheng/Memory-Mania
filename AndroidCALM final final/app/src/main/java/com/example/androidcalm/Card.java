package com.example.androidcalm;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class Card implements Parcelable {

    private ImageView imgView;
    private String image;
    boolean flipped;
    boolean matched;

    public Card(){

    }

    public Card(ImageView imgView, String image, boolean flipped, boolean matched) {
        this.imgView = imgView;
        this.image = image;
        this.flipped = flipped;
        this.matched = matched;
    }

    public Card(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeByte((byte) (flipped ? 1 : 0));
        dest.writeByte((byte) (matched ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {

        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public ImageView getImgView() {
        return imgView;
    }

    public String getImage() {
        return image;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setImgView(ImageView imgView) {
        this.imgView = imgView;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    @Override
    public String toString() {
        return "Card{" +
                "imgView=" + imgView +
                ", image=" + image +
                ", flipped=" + flipped +
                ", matched=" + matched +
                '}';
    }
}
