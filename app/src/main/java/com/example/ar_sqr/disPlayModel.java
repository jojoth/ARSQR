package com.example.ar_sqr;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.List;

public class disPlayModel {

    static ArrayList<String> cardname = new ArrayList<>();


    public void setCardname(ArrayList<String> cardname) {
        this.cardname = cardname;
    }

    public void PlayModel(String imgtarget){
//https://stackoverflow.com/questions/7976141/get-uri-of-mp3-file-stored-in-res-raw-folder-in-androidการ Uri เรียกไฟล์ ใน android
        // MediaPlayer me = MediaPlayer.create()
        Log.d("LOG","inclass imgtarget="+imgtarget);

        //String[] realname =  new String[2];
        String realname = imgtarget.split("\\.")[0];
        String MP4 = realname+".mp4";


        Log.e("LOG","+++++in class Found="+imgtarget+"/filemp4="+MP4);

    }
}
