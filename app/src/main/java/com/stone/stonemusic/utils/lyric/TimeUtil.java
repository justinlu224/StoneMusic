package com.stone.stonemusic.utils.lyric;

public class TimeUtil {

    /**
     * 将毫秒时长，转化为 分:秒 的String
     * @param duration
     * @return 例如00:23 的String
     */
    public static String mill2mmss(long duration){

        int m,s;
        String str = "";

        int x=(int)duration/1000;
        s=x%60;
        m=x/60;
        if(m<10){
            str+="0"+m;
        }else{
            str+=m;
        }

        if(s<10){
            str+=":0"+s;
        }else{
            str+=":"+s;
        }

        return str;
    }

    public static int getLrcMillTime(String time){
        int millTime=0;
        time=time.replace(".", ":");

        String timedata[]=time.split(":");

        //Log.i("min,second,mill", timedata[0]+","+timedata[1]+","+timedata[2]);
        int min=0;
        int second=0;
        int mill=0;
        try {
            min = Integer.parseInt(timedata[0]);
            second = Integer.parseInt(timedata[1]);
            String millStr = timedata[2];
            int len = millStr.length();
            if (timedata[2]==null || len == 0)
                millStr = "000";
            if (len == 1)
                millStr += "00";
            else if (len == 2)
                millStr += "0";
            mill = Integer.parseInt(millStr);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

//        millTime=(min*60+second)*1000+mill*10;
        millTime=(min*60+second)*1000+mill; //00:12.570 最后是3位，最高999我觉得没必要乘以10了
        return millTime;
    }
}
