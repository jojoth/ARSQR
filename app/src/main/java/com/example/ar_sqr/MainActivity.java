package com.example.ar_sqr;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ExternalTexture texture;
    //private MediaPlayer mediaPlayer,mediaP_singer,mediaP_law;
    //private List<MediaPlayer> mediaPlayer_all = new ArrayList<>();
    private ArrayMap<String,MediaPlayer> mediaPlayer_all = new ArrayMap<>();
    private CustomArFragment arFragment;
    private Scene scene;
    private ModelRenderable renderable;
    private ArrayMap<String,ModelRenderable> renderable_all = new ArrayMap<>();
    private ArrayMap<String,AnchorNode> anchorNode_all = new ArrayMap<>();


    private ImageButton imgbut,clearbut;

    private Set<String> cardset;
    //float inc_an= 5f;

    private String mode ="vdo";
    private  boolean clear = false;

    float angle= 0f;




    int[] count ;//---เก็บจำนวนในการเรียก active Model โดนจะเก็บตาม index ของชุดบัตรคำใน DB เลย

    ArrayList<String> cardname = new ArrayList<>();//-- เก็บบัตรคำทั้งหมดที่มีอยู่ใน DB

    int count_mu,count_singer= 0;

    String Ftxt= "occupationDB.imgdb-imglist.txt";//-เก็บชื่อรูปทั้งหมดที่อยู่ใน database
    String Fdb ="occupationDB.imgdb";//----เก็บ database รูป โดย index จะเรีกกตามชื่อที่อยู่ใน txtไฟล์ด้านบน

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgbut = (ImageButton)findViewById(R.id.imageButton2);
        clearbut= (ImageButton)findViewById(R.id.eraser_but);

        imgbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode= "3d";
            }
        });
        clearbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear= true;
            }
        });


        CustomArFragment ca = new CustomArFragment();




        texture = new ExternalTexture();

        //ลอง set แบบนี้ดู https://stackoverflow.com/questions/33086417/mediaplayer-setdatasourcestring-not-working-with-local-files, ** อันนี้น่าลองมากกว่า https://www.programcreek.com/java-api-examples/?class=android.media.MediaPlayer&method=setDataSource

/*
        mediaPlayer = MediaPlayer.create(this, R.raw.musicial);

        //mediaPlayer.setDataSource();
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

        ModelRenderable
                .builder()
                .setSource(this,Uri.parse("screen13.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture",
                            texture);
                    //---- มันคือ choma key ซึ่งเป็นการreder video บน plan 3D
                    modelRenderable.getMaterial().setFloat4("keyColor",
                            new Color(0.01843f, 1f, 0.098f));


                    renderable = modelRenderable;
                });

 */


        arFragment = (CustomArFragment)
                getSupportFragmentManager().findFragmentById(R.id.arFragment);

        scene = arFragment.getArSceneView().getScene();

        /*
                ทำการอ่านไฟล์ txtที่มาควบคู่กับ DB เพื่อดูว่าในDB  มันเก็บรูปบัตรคำอะไรบ้างindex ที่เท่าไร  เพราะในตัว DB มันไม่อนุญาติให้เข้าไป query ใน DB แบบทั่วไปที่ใช้
                มันเลยมี txt แยกมาควบคู่เพื่อในสามารถอ่านข้อมูลเบื้องต้นแทน query ใน DB
         */
        FileManage fi = new FileManage();

        String path1 = getFilesDir().getAbsolutePath();

        cardname = fi.readFile(path1,Ftxt);

        for(int i=0; i<cardname.size();i++){
            Log.d("LOG","cardname="+cardname.get(i));

            PlayAR playAR1 = new PlayAR();

            playAR1.setScene(scene);
            playAR1.setArFragment(arFragment);
        }

         cardset = new HashSet<String>(cardname);


        count = new int[cardname.size()];



        resetCount();

        scene.addOnUpdateListener(this::onUpdate);

    }

    public  void resetCount(){

        for(int i=0; i<cardname.size();i++){
            count[i]=0;
        }

    }

    public AugmentedImageDatabase SetAugmentImgDB(Session session){

            String path= getFilesDir().getAbsolutePath()+"/"+Fdb;//ตอนนี้ไปเก็บ db ที่ /data/user/0/com.example.ar_sqr/files/occupationDB.imgdb

            Log.i("LOG","path="+path);

        File fi = new File(path);




        AugmentedImageDatabase aid= new AugmentedImageDatabase(session);

        try{
            InputStream is =  new FileInputStream(fi);

            aid = AugmentedImageDatabase.deserialize(session,is);
            Log.i("LOG","DBnum="+aid.getNumImages());


        }catch (Exception ex){

        }

        /*Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.musician_ori);
        Bitmap img_singer = BitmapFactory.decodeResource(getResources(), R.drawable.singer1);
        aid.addImage("musician", image);
        aid.addImage("singer", img_singer);*/


        return  aid;



    }

    private void onUpdate(FrameTime frameTime) {

       /* if (isImageDetected)
            return;*/


        Frame frame = arFragment.getArSceneView().getArFrame();
/*
            getcam Position
        Camera cam=  frame.getCamera();

 */

        //Log.e("LOG","--mode="+mode);

        Collection<AugmentedImage> augmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        // เป็นส่วนในการ detect รูปเป้าหมาย image target

        /*
        การให้แสดงที่ละใบให้มีการเก็บ current รูปที่ detect เจอไว้ว่าเจออะในตอนนั้น  สามารถปรับค่า getTrackingState ได้ไม เพราะถ้าอยู่ในหมวด TRACKING เมื่อไรเหมือนมันจะมี bug
         */
        for (AugmentedImage image : augmentedImages) {



            Log.d("LOG","before track="+image.getName()+"/nunm="+image.getIndex());


            if(image.getTrackingState()==TrackingState.TRACKING){//--- เมื่อเจอตำแหน่งมันจะ Tracking
                Log.d("LOG","Tracking found="+ image.getName());

               /* if(image.getName().contains("musician.jpg")){

                    Log.i("LOG","---found Musician");

                }
                if(image.getName().contains("singer.jpg")){
                    Log.e("LOG","---found Singer");
                }
                */

            }


            if(image.getTrackingState()== TrackingState.STOPPED){
                Log.e("LOG","Tracking STOP");
            }

            if(image.getTrackingState()==TrackingState.PAUSED){

                Log.e("LOG","+++Tracking Pause");

                //---ตอนนี้อยู่ในสถานะ PAUSE ทำงานถูกแต่พออยู่ใน Tracking ทำงานผิดเลยตลกเลย แปลว่าเราเก็บค่า current ใน State PAUSEลองหาวิธีในการทำให้Tracking อยู่ในสถานะ PAUSE สิว่าทำได้หรือป่าว
                //https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/TrackingState  ตรงนี้บอกว่า session current PAUSE

            }

            if(cardset.contains(image.getName())){//ตรงนี้มันจะเช็คว่ารูปที่เจอมันอยู่ในชุดบัตรคำที่อยู่ใน db ที่เราเรียกใช้หรือเปล่า

                Log.d("LOG","+++Found SET cardset  found="+image.getName());
                if(count[image.getIndex()]<2){

                    disPlayModel Pmodel =new disPlayModel();
                    Pmodel.setCardname(cardname);//---ส่งชุดบัตรคำไปที่ class เพื่อนำไปใช้งานต่อไผ
                    Pmodel.PlayModel(image.getName());//-- ส่งชื่อรูปที่มันdetectเจอเพื่อใช้สำหรับเรียกไฟล์  model หรือ video

                }

                count[image.getIndex()]++;

            }





                if(clear){

                    PlayAR playAR2 = new PlayAR();
                    playAR2.Reset_Sence();//---- ลบทุก Model ออกจาก scecen
                    //---reset count ที่set การเรียกModel ทั้งหมด
                    resetCount();

                }

         }



    }
/*
    private  void checkPlayVideo(String fname){
        MediaPlayer mediaPlayer1;

        mediaPlayer1= mediaPlayer_all.get(fname);

        if(!mediaPlayer1.isPlaying()) mediaPlayer1.start();
    }

    private  void setMediaPlayer(String fname) {

        ///---set MediaPlayer เป็น video อันใหม่ตามที่เลือก
        MediaPlayer mediaPlayer2;


        if (fname.equals("musician")) {
            mediaPlayer2 = MediaPlayer.create(this, R.raw.musicial);



        mediaPlayer2.setSurface(texture.getSurface());
        mediaPlayer2.setLooping(true);

        mediaPlayer_all.put(fname, mediaPlayer2);
    }else if(fname.equals("singer")){

            mediaPlayer2 = MediaPlayer.create(this, R.raw.singer);



            mediaPlayer2.setSurface(texture.getSurface());
            mediaPlayer2.setLooping(true);

            mediaPlayer_all.put(fname, mediaPlayer2);


        }




        ModelRenderable
                .builder()
                .setSource(this,Uri.parse("screen19.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture",
                            texture);
                    //---- มันคือ choma key ซึ่งเป็นการreder video บน plan 3D
                    modelRenderable.getMaterial().setFloat4("keyColor",
                            new Color(0.01843f, 1f, 0.098f));


                    renderable = modelRenderable;

                    renderable_all.put(fname,renderable);
                });


    }




*/

}
