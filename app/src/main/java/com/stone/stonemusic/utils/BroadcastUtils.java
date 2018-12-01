package com.stone.stonemusic.utils;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.stone.stonemusic.R;

public class BroadcastUtils {
    public static void sendPlayMusicBroadcast(){
        Log.d("stone1128","广播发送函数sendPlayMusicBroadcast");
        Intent intent = new Intent();
        intent.setAction(MusicAppUtils.getContext().getResources().getString(R.string.app_name));
        intent.putExtra("state", MediaStateCode.PLAY_START);
        LocalBroadcastManager
                .getInstance(MusicAppUtils.getContext())
                .sendBroadcast(intent);
    }
    public static void sendPauseMusicBroadcast(){
        Intent intent = new Intent();
        intent.setAction(MusicAppUtils.getContext().getResources().getString(R.string.app_name));
        intent.putExtra("state", MediaStateCode.PLAY_PAUSE);
        LocalBroadcastManager
                .getInstance(MusicAppUtils.getContext())
                .sendBroadcast(intent);
    }
    public static void sendContinueMusicBroadcast(){
        Intent intent = new Intent();
        intent.setAction(MusicAppUtils.getContext().getResources().getString(R.string.app_name));
        intent.putExtra("state", MediaStateCode.PLAY_CONTINUE);
        LocalBroadcastManager
                .getInstance(MusicAppUtils.getContext())
                .sendBroadcast(intent);
    }
}