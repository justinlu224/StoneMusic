package com.stone.stonemusic.net;

import android.text.TextUtils;
import android.util.Log;

import com.stone.stonemusic.model.Music;
import com.stone.stonemusic.model.PlayListBean;
import com.stone.stonemusic.utils.URLProviderUtils;
import com.stone.stonemusic.utils.code.PlayType;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: stoneWang
 * @CreateDate: 2019/8/6 20:52
 * @Description: 将Json数据转化为Bean
 */
public class JsonToResult implements PlayType {
    private static final String TAG = "JsonToResult";

    /**
     *
     * @param jsonData 从接口返回的json字符串
     * @return null：代表获取数据表失败， YueDanBean2类型数据，表示返回数据成功
     */
    public static List<PlayListBean> getYueDanBean2FromJson(String jsonData){

        if (!TextUtils.isEmpty(jsonData)) { //返回数据非空
            try{

                JSONObject allNews = new JSONObject(jsonData);
                int code = allNews.getInt("code");

                while(200 == code) {
                    String playLists = allNews.getString("playlists"); //playList数组的字符串
                    JSONArray playListsArray = new JSONArray(playLists);

                    List<PlayListBean> listBeans = new ArrayList<>();
                    for (int i = 0; i < playListsArray.length(); i++) { //遍历playList数组,添加进对象
                        JSONObject playListObject = playListsArray.getJSONObject(i);
                        PlayListBean playListBean = getPlayListBeanInstance(
                                playListObject.getString("name"),
                                playListObject.getString("id"),
                                playListObject.getString("coverImgUrl"),
                                playListObject.getString("description"));
                        listBeans.add(playListBean);
                    }
                    return listBeans;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    private static PlayListBean getPlayListBeanInstance(
            String name,
            String id,
            String coverImgUrl,
            String description) {
        PlayListBean playListBean = new PlayListBean();
        playListBean.setName(name);
        playListBean.setId(id);
        playListBean.setCoverImgUrl(coverImgUrl);
        playListBean.setDescription(description);
        return playListBean;
    }

    /**
     *
     * @param jsonData 从接口返回的json字符串
     * @return null：代表获取数据表失败， Music类型数据，表示返回数据成功
     */
    public static List<Music> getOnLineListBeanFromJson(String jsonData){
        if (!TextUtils.isEmpty(jsonData)) { //返回数据非空
            try{

                JSONObject allNews = new JSONObject(jsonData);
                int code = allNews.getInt("code");

                while(200 == code){
                    String playLists = allNews.getString("playlist"); //playList的字符串
                    JSONObject playListsObj = new JSONObject(playLists);
                    String tracks = playListsObj.getString("tracks"); //tracks数组的字符串

                    JSONArray tracksArray = new JSONArray(tracks);


                    List<Music> listBeans = new ArrayList<>();
                    for (int i = 0; i < tracksArray.length(); i++) { //遍历playList数组,添加进对象
                        JSONObject OnLineListObject = tracksArray.getJSONObject(i);
                        //ar
                        JSONArray OnLineListArrayAr = new JSONArray(OnLineListObject.getString("ar"));
                        JSONObject OnLineListObjectAr0 = OnLineListArrayAr.getJSONObject(0);
                        String ar_name = OnLineListObjectAr0.getString("name");
                        //al
                        JSONObject OnLineListArrayAl = new JSONObject(OnLineListObject.getString("al"));
                        String al_picUrl = OnLineListArrayAl.getString("picUrl");
                        Music music = getOnLineMusicInstance(
                                OnLineListObject.getString("name"),
                                OnLineListObject.getString("id"),
                                ar_name,
                                al_picUrl,
                                OnLineListObject.getInt("dt")
                        );
                        listBeans.add(music);
                    }
                    Log.i(TAG, "listBeans.size=" + listBeans.size());
                    return listBeans;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }



    /**
     * 传入返回的歌词JSON数据，解析出需要的歌词部分，返回这部分的String
     * @param jsonData 传入返回的歌词JSON数据，原始数据
     * @return null,表示获取歌词失败; String,代表正常歌词
     */
    public static String getOnlineLyricFromJson(String jsonData) {
        if (!TextUtils.isEmpty(jsonData)) { //返回数据非空
            try {
                JSONObject allNews = new JSONObject(jsonData);
                int code = allNews.getInt("code");

                //200,成功码
                while (200 == code) {
                    String lrcObjectString = allNews.getString("lrc"); //lrc object 的字符串
                    JSONObject lrcObject = new JSONObject(lrcObjectString);
                    String lrcString = lrcObject.getString("lyric");
                    return lrcString;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Music getOnLineMusicInstance(
            String name,
            String id,
            String ar_name,
            String al_picUrl,
            int dt) {
        Music music = new Music();
        music.setId(0); //网络歌曲设置id为0
        music.setMusicType(PlayType.OnlineType); //设置歌曲类型->在线歌曲
        music.setTitle(name);
        music.setMusicId(id);
        music.setArtist(ar_name);
        music.setPicUrl(al_picUrl);
        music.setFileUrl(URLProviderUtils.getSingleSong(id));
        music.setDuration(dt);
        return music;
    }

    /**
     * 返回单曲查询结果
     * @param jsonData 原始JSON
     * @return null:查询失败  List<Music>：查询到的单曲列表
     */
    public static List<Music> getFindResultFromJson(String jsonData) {
        if (!TextUtils.isEmpty(jsonData)) { //返回数据非空
            try {
                JSONObject allResult = new JSONObject(jsonData);
                int code = allResult.getInt("code");
                if (200 == code) {
                    String resultObjectString = allResult.getString("result"); //lrc object 的字符串
                    JSONObject resultObject = new JSONObject(resultObjectString);

                    String songsArrayString = resultObject.getString("songs");
                    JSONArray songsArray = new JSONArray(songsArrayString);

                    List<Music> listBeans = new ArrayList<>();
                    for (int i = 0; i < songsArray.length(); i++) { //遍历playList数组,添加进对象
                        JSONObject songListObject = songsArray.getJSONObject(i);
                        String id = songListObject.getString("id"); //歌曲id
                        String name = songListObject.getString("name"); //歌曲名
                        int duration = songListObject.getInt("duration"); //歌曲时长

                        //artists
                        JSONArray songArrayArtists = new JSONArray(songListObject.getString("artists"));
                        JSONObject artistObject = songArrayArtists.getJSONObject(0);
                        String artist_name = artistObject.getString("name"); //歌手
                        String artists_pic = artistObject.getString("img1v1Url"); //图片

                        Music music = getOnLineMusicInstance(
                                name, //歌曲名
                                id, //网络歌曲id
                                artist_name, //歌手
                                artists_pic, //歌曲图片
                                duration //歌曲时长
                        );
                        listBeans.add(music);
                    }
                    Log.i(TAG, "listBeans.size=" + listBeans.size());
                    return listBeans;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
