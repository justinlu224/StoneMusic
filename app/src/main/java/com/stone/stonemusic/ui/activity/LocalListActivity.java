package com.stone.stonemusic.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stone.stonemusic.R;
import com.stone.stonemusic.adapter.LocalMusicFragmentPagerAdapter;
import com.stone.stonemusic.model.Music;
import com.stone.stonemusic.model.SongModel;
import com.stone.stonemusic.present.MusicObserverListener;
import com.stone.stonemusic.present.MusicObserverManager;
import com.stone.stonemusic.present.PlayControl;
import com.stone.stonemusic.ui.View.ActivityView;
import com.stone.stonemusic.utils.MediaStateCode;
import com.stone.stonemusic.utils.MediaUtils;
import com.stone.stonemusic.utils.MusicApplication;
import com.stone.stonemusic.present.MusicResources;

import java.util.ArrayList;
import java.util.List;

public class LocalListActivity extends AppCompatActivity implements
        MusicObserverListener, View.OnClickListener{
    public static final String TAG = "LocalListActivity";

    private ProgressDialog mDialog;
    // 线程变量
    MyTask mTask;

    private TabLayout.Tab tabMusic;
    private TabLayout.Tab tabArtist;
    private TabLayout.Tab tabAlbum;
    private TabLayout.Tab tabFolder;
    private ViewPager vpLocalMusic;
    private LocalMusicFragmentPagerAdapter musicFragmentPagerAdapter;
    private TabLayout tabLayoutBar;

    /*几个代表Fragment页面的常量*/
    public static final int PAGE_MUSIC = 0;
    public static final int PAGE_ARTIST = 1;
    public static final int PAGE_ALBUM = 2;
    public static final int PAGE_FOLDER = 3;
//    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    private LinearLayout bottomLinearLayout;
    private ImageView mIvPlay, mIvPlayNext, mIvBottomBarImage;
    private TextView mBottomBarTitle, mBottomBarArtist;
    private List<Music> musicList = new ArrayList<>();

    /*辅助回调的set方法*/
    private CallBackInterface mCallBackInterface;
    public void setCallBackInterface(CallBackInterface myCallBackInterface){
        this.mCallBackInterface = myCallBackInterface;
    }

    private class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
//            text.setText("加载中");
            // 执行前显示提示
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                musicList = new MusicResources().getMusic(MusicApplication.getContext());
                SongModel.getInstance().setSongList(musicList);
                MusicResources.initArtistMode(); //初始化歌手列表
            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 执行完毕后，则更新UI
//            text.setText("加载完毕");

            initViews();
            initMusicPlayImg();
            mDialog.cancel();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        ActivityView.setStatusBarColor(this, R.color.colorBarBottom, true);
//        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        setContentView(R.layout.activity_local_list);

        MusicApplication.addDestroyActivity(this, TAG); /*添加到待销毁的队列*/

        musicList = SongModel.getInstance().getSongList();


        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在加载音乐文件");
        mDialog.show();

        mTask = new MyTask();
        mTask.execute();


        //添加进观察者队列
        MusicObserverManager.getInstance().add(this);
    }

    private void initViews() {
        bottomLinearLayout = (LinearLayout) findViewById(R.id.bottom_bar_layout);
        bottomLinearLayout.setOnClickListener(this);

        /*使用适配器将ViewPager与Fragment绑定在一起*/
        vpLocalMusic = (ViewPager) findViewById(R.id.vp_local_music);
        musicFragmentPagerAdapter = new LocalMusicFragmentPagerAdapter(getSupportFragmentManager());
        vpLocalMusic.setAdapter(musicFragmentPagerAdapter);

        //将TabLayout与ViewPager绑定在一起
        tabLayoutBar = (TabLayout) findViewById(R.id.tab_layout_bar);
        tabLayoutBar.setupWithViewPager(vpLocalMusic);

        //指定Tab的位置
        tabMusic = tabLayoutBar.getTabAt(PAGE_MUSIC);
        tabArtist = tabLayoutBar.getTabAt(PAGE_ARTIST);
        tabAlbum = tabLayoutBar.getTabAt(PAGE_ALBUM);
        tabFolder = tabLayoutBar.getTabAt(PAGE_FOLDER);

        mIvPlay = (ImageView) findViewById(R.id.iv_play);
        mIvPlayNext = (ImageView) findViewById(R.id.iv_play_next);
        mIvBottomBarImage = (ImageView) findViewById(R.id.bottom_bar_image);
        mBottomBarTitle = (TextView) findViewById(R.id.bottom_bar_title);
        mBottomBarArtist = (TextView) findViewById(R.id.bottom_bar_artist);
    }

    private void initMusicPlayImg() {

        try{
            int position = MediaUtils.currentSongPosition;
//        Log.d(TAG, "20190212 musicList = " + musicList.size());
            mBottomBarTitle.setText(musicList.get(position).getTitle());
            mBottomBarArtist.setText(musicList.get(position).getArtist());


            String path = MusicResources.getAlbumArt(new Long(musicList.get(position).getAlbum_id()).intValue());
//            Log.d(TAG,"path="+path);
            if (null == path){
                mIvBottomBarImage.setImageResource(R.drawable.play_background02);
            }else{
                Glide.with(MusicApplication.getContext()).load(path).into(mIvBottomBarImage);
            }

            if (MediaUtils.currentState == MediaStateCode.PLAY_PAUSE ||
                    MediaUtils.currentState == MediaStateCode.PLAY_STOP) {
                mIvPlay.setImageResource(R.drawable.ic_play_black);
            } else {
                mIvPlay.setImageResource(R.drawable.ic_pause_black);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //播放键控制
    public void play(View view){
        Log.i(TAG, "此时的状态=="+MediaUtils.currentState);
        if (musicList.size() > 0){
            PlayControl.controlBtnPlaySameSong();
        }

    }

    //播放键控制
    public void playNext(View view){
        if (musicList.size() > 0){
            PlayControl.controlBtnNext();
        }
    }

    /*收到UI界面更新的通知后，在此刷新UI*/
    private Handler LocalListActivityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            initMusicPlayImg();
        }
    };

    /*跳转到PlayActivity*/
    public void GoToPlayActivity(){
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom_bar_layout:
                if (musicList.size() > 0){
                    GoToPlayActivity();
                }
                break;
        }

    }



    /*定义回调接口*/
    public interface CallBackInterface{
        void ChangeUI();
    }

    @Override
    public void observerUpData(int content) {
        switch (content) {
            case MediaStateCode.MUSIC_INIT_FINISHED:
                Log.e(TAG, "Artist更新完毕");
                break;
            case MediaStateCode.PLAY_START:
            case MediaStateCode.MUSIC_POSITION_CHANGED:
                LocalListActivityHandler.sendEmptyMessage(1);
                /*调用回调方法ChangeUI，调用后MusicListFragment重写的回调方法会被自动执行，从而在MusicListFragment回调方法中通知handler更新UI*/
                if (null != mCallBackInterface) {
                    mCallBackInterface.ChangeUI();
                }
                break;

            case MediaStateCode.PLAY_CONTINUE:
            case MediaStateCode.PLAY_STOP:
            case MediaStateCode.PLAY_PAUSE:
                LocalListActivityHandler.sendEmptyMessage(1);
                break;
        }
        Log.i(TAG, "observerUpData->观察者类数据已刷新");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //从观察者队列中移除
        MusicObserverManager.getInstance().remove(this);
        //回调接口引用指空
        mCallBackInterface = null;
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
////        return super.onKeyDown(keyCode, event);
////        if (KeyEvent.KEYCODE_HOME == keyCode) {
////            //点击Home键所执行的代码
////        }
//        if (keyCode == KeyEvent.KEYCODE_BACK){
//            if ((System.currentTimeMillis()) > 2000) {
//
//            }
//
//        }
//        return super.onKeyDown(keyCode, event); // 不会回到 home 页面
//    }

//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        ToastUtils.getToastShort("返回键坏了");
//    }

}
