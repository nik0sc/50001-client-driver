package com.example.kohseukim.clientside;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlay {
    public static MediaPlayer mediaPlayer;

    public static boolean isplayingAudio=false;


    public static void playAudio(Context c, int id){
        mediaPlayer = MediaPlayer.create(c,id);
        mediaPlayer.setVolume(80,80);
        if(!mediaPlayer.isPlaying())
        {
            isplayingAudio=true;
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
    }
    public static void stopAudio(){
        isplayingAudio=false;
        mediaPlayer.stop();
    }
}
