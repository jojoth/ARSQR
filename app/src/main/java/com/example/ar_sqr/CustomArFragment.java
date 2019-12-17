package com.example.ar_sqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.File;
import java.io.InputStream;

public class CustomArFragment extends ArFragment {

    AugmentedImageDatabase aid;

    String Ftxt= "occupationDB.imgdb-imglist.txt";
    String Fdb ="occupationDB.imgdb";
    //static  String path;



    @Override
    protected Config getSessionConfiguration(Session session) {

       // Config config = new Config(session);
        Config config =super.getSessionConfiguration(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);

        config.setFocusMode(Config.FocusMode.AUTO);


        FileManage fi = new FileManage();









        // create DB byinsert img






        /*AugmentedImageDatabase aid = new AugmentedImageDatabase(session);

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.musician_ori);
        Bitmap img_singer = BitmapFactory.decodeResource(getResources(), R.drawable.singer1);
        aid.addImage("musician", image);
        aid.addImage("singer", img_singer);
        */
        AugmentedImageDatabase aid = ((MainActivity)getActivity()).SetAugmentImgDB(session);




        config.setAugmentedImageDatabase(aid);

        this.getArSceneView().setupSession(session);
        Log.i("LOG","DB num="+aid.getNumImages());

        return config;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);

        return frameLayout;
    }
}
