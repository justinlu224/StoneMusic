package com.stone.stonemusic.net;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadLrcFile {
    private static String TAG = "DownLoadLrcFile";
    private static DownLoadLrcFile instance;
    public static final String lrcRootPath = android.os.Environment
            .getExternalStorageDirectory().toString()
            + "/StoneMusic/Lyrics/"; //
    public static final String queryLrcURLRoot = "http://geci.me/api/lyric/";
    public static final String queryMusicID = "http://144.34.228.215:3000/search?keywords=";
    public static final String queryLrc = "http://144.34.228.215:3000/lyric?id=";

    public static DownLoadLrcFile getInstance() {
        if (null == instance) {
            instance = new DownLoadLrcFile();
        }

        return instance;
    }

    /* 拼接查询的URL */
    public String getQueryLrcURL(String title, String artist) {
        String queryUrl = queryLrcURLRoot + Encode(title) + "/" + Encode(artist);

        return queryUrl;
    }

    /**
     * 根据 音乐名称 && 歌手 查询歌曲ID
     * @param title
     * @param artist
     * @return 返回查询的URL
     */
    public String getQueryMusicIDURL(String title, String artist) {
        String queryURL = queryMusicID + Encode(title + " " +artist);
        return queryURL;
    }

    /**
     * 根据 歌曲ID 查询歌词
     * @param id
     * @return 返回查询的URL
     */
    public String getQueryLrcByIDURL(String id) {
        String queryUrl = queryLrcURLRoot + Encode(id);
        return queryUrl;
    }

    public String getQueryLrcURL(String title) {
        return queryLrcURLRoot + Encode(title) + "/";
    }

    /**
     * 获取歌词下载地址
     * @param title 歌曲名称
     * @param artist 歌手
     * @return 歌词下载地址
     */
//    public String getLrcURL(String title, String artist) {
//        String queryLrcURLStr = getQueryLrcURL(title); /*查询的地址*/
//        Log.e(TAG, "getLrcURL -> queryLrcURLStr == " + queryLrcURLStr);
//
//        try {
//
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url(queryLrcURLStr)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//            String responseData = response.body().string();
//
//            if (!TextUtils.isEmpty(responseData)) {
//
//                JSONObject AllData = new JSONObject(responseData);
//                JSONArray lrcArray = AllData.getJSONArray("result");
//
//                Log.d(TAG, "stone JSONArray大小 ==" + lrcArray.length());
//                if (lrcArray.length() == 0)
//                    return "result==0";
//
//                for (int i=0; i<lrcArray.length(); i++){
//                    JSONObject oneLrcObject = lrcArray.getJSONObject(i);
//                    String lrcUrl = oneLrcObject.getString("lrc");
//                    Log.d(TAG, "第" + i + "行：：" + lrcUrl);
//                    return lrcUrl; /*当前只返回一个测试*/
//                }
//
//            }
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 将String类型的歌词，保存为文件到本地
     * @param lrcString String类型的歌词
     * @param title 歌名
     * @param artist 歌手
     * @return false:保存失败， true:保存成功
     */
    public boolean writeLrcFromStringToFile(String lrcString, String title, String artist) {
        //需要保存的文件名
        String fileName = getLrcPath(title, artist);

        try{
            //创建关联文件的对象
            File file = new File(fileName);

            if (file.exists()) { //如果文件已经存在就结束
                Log.e(TAG, "writeLrcFromStringToFile:文件已经存在！");
                return true;
            } else { //文件不存在，就保存吧
                FileWriter fileWriter;

                if (!file.exists()) {
                    // 先得到文件的上级目录，并创建上级目录，在创建文件
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                fileWriter = new FileWriter(fileName);
                fileWriter.write(lrcString);
                fileWriter.flush();
                fileWriter.close();

                Log.e(TAG, "writeLrcFromStringToFile:文件写入成功！");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 下载网络歌词到本地
     * @param urlPath 歌词文件网络地址
     * @param lrcPath 歌词文件本地缓存地址
     * @return 下载是否成功
     */
    public boolean writeContentFromUrl(String urlPath, String lrcPath, String title, String artist) {
//        Log.d("stoneWang", "writeContentFromUrl:执行一次");
        if ( null == urlPath || null == lrcPath || null == title || null == artist)
            return false;
        Log.e(TAG, "writeContentFromUrl:urlPath ==" + urlPath +
            "\n lrcPath==" + lrcPath + " title ==" + title + " artist==" + artist);
        try {
            URL url = new URL(urlPath);
            //打开连接
            URLConnection conn = url.openConnection();
//            conn.connect();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            int contentLength = conn.getContentLength();
            Log.e(TAG, "writeContentFromUrl:文件长度=" + contentLength);
            //下载后的文件名
            String fileName = getLrcPath(title, artist);
            Log.e(TAG, "writeContentFromUrl:文件名称==" + fileName);
            //创建关联文件的对象
            File file1 = new File(fileName);

            if (file1.exists()) {
                Log.e(TAG, "writeContentFromUrl:文件已经存在！");
                return true;
            } else {
                //创建字节流
                char c[] = new char[256];
                int temp = -1;
                if (!file1.exists()) {
                    // 先得到文件的上级目录，并创建上级目录，在创建文件
                    file1.getParentFile().mkdirs();
                    file1.createNewFile();
                }
                BufferedReader bf = new BufferedReader(new InputStreamReader(
                        is, "utf-8"));
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(fileName),
                                "utf-8")));

                //写数据
                while ((temp = bf.read()) != -1) {
                    bf.read(c);
                    out.write(c);
                }
                //完成后关闭流
                bf.close();
                out.close();
                Log.e(TAG, "writeContentFromUrl:下载成功！");
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getLrcPath(String title, String artist) {
        return lrcRootPath + title + "-" + artist + ".lrc";
    }

    /* 歌手，歌曲名中的空格进行转码 */
    public String Encode(String str) {

        try {
            return URLEncoder.encode(str.trim(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;

    }

}
