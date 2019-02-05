package com.stone.stonemusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.stone.stonemusic.R;
import com.stone.stonemusic.model.SongModel;
import com.stone.stonemusic.service.MusicService;
import com.stone.stonemusic.utils.MediaStateCode;
import com.stone.stonemusic.utils.MediaUtils;
import com.stone.stonemusic.utils.MusicAppUtils;

public class MusicBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "MusicBroadCastReceiver";

    private static MusicBroadCastReceiver sReceiver;
    private MusicBroadCastReceiver() {}

    public static MusicBroadCastReceiver getInstance(){
        if (sReceiver == null) {
            sReceiver = new MusicBroadCastReceiver();
        }
        return sReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"广播接收器onReceive");
        String action = intent.getAction();
        Log.d(TAG, "action = " + action);

        if (action.equals(MusicAppUtils.getContext().
                getResources().getString(R.string.app_name))) {
            /*利用广播操作服务*/
            Log.d(TAG,"接收到的intent的state值为："+
                    intent.getIntExtra("state", 0));
            intent.setClass(context, MusicService.class);
            context.startService(intent);
        }
//        else if (action.equals(MediaStateCode.ACTION_PLAY)) {
//            MediaUtils.prepare(
//                    SongModel.getInstance().getSongList().
//                            get(MediaUtils.currentSongPosition).getFileUrl());
//            MediaUtils.start();
//        } else if (action.equals(MediaStateCode.ACTION_PAUSE)) {
//            MediaUtils.pause();
//        } else if (action.equals(MediaStateCode.ACTION_CONTINUE)) {
//            MediaUtils.continuePlay();
//        } else if (action.equals(MediaStateCode.ACTION_STOP)) {
//            MediaUtils.stop();
//        }

    }
}
