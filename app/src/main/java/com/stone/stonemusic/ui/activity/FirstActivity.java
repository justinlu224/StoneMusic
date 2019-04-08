package com.stone.stonemusic.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.stone.stonemusic.R;
import com.stone.stonemusic.model.Music;
import com.stone.stonemusic.model.SongModel;
import com.stone.stonemusic.present.MusicResources;
import com.stone.stonemusic.utils.MusicAppUtils;
import com.stone.stonemusic.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends BaseNoBarActivity {
    private static String TAG = "FirstActivity";
    private TextView tv = null;
    private ImageView iv = null;
    private Animation animation1 = null, animation2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        tv = (TextView) findViewById(R.id.first_music_text);
        iv = (ImageView) findViewById(R.id.first_music_log);


        initMusicResources();
        initAnim();
        initPermissions();

//        initJump();
    }

    private void initMusicResources() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //init音乐列表
                List<Music> musicList = new ArrayList<>();
                musicList = new MusicResources().getMusic(MusicAppUtils.getContext());
                SongModel.getInstance().setSongList(musicList);
                MusicResources.initArtistMode(); //初始化歌手列表
            }
        }).start();


        //test artistMap的取值，OK
//        Set<Map.Entry<String, ArrayList<Music>>> sAll = MusicResources.artistMap.entrySet();
//        for (Map.Entry<String, ArrayList<Music>> mE : sAll) {
//            Log.i(TAG, "artist = " + mE.getKey());
//            for (Music music : mE.getValue()) {
//                Log.i(TAG, "Music.Title=" + music.getTitle());
//            }
//            Log.i(TAG, "==========================");
//        }

    }

    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(MusicAppUtils.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            initJump();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initJump();
                }else{
                    /*没有得到许可，退出*/
                    ToastUtils.getToastShort("未许可权限，软件将自动退出");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                }
                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 初始化动画
     */
    private void initAnim() {

        animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        tv.startAnimation(animation1);

        animation2 = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        iv.startAnimation(animation2);

    }

    /**
     * 延时几秒，跳转至MainActivity
     */
    private void initJump(){
        //延迟一段时间后跳转到另一个界面
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent2 = new Intent (FirstActivity.this,LocalListActivity.class);
                startActivity(intent2);//跳转界面
                FirstActivity.this.finish();//关闭此界面
            }
        }, 2650);
    }
}
