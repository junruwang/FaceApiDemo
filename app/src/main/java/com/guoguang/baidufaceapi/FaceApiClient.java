package com.guoguang.baidufaceapi;

import android.util.Log;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by allen on 2018/7/11.
 */

public class FaceApiClient {

    public static final String TAG="FaceApiClient";
    //百度人脸识别应用id
    public static final String APP_ID = "11517427";
    //百度人脸识别应用apikey
    public static final String API_KEY = "HaZQuIsCXNuvDMxuswBGUh8Y";
    //百度人脸识别应用sercetkey
    public static final String SERCET_KEY = "cAWf7FkBo2TG67ZxVRwlL9qTF5DsOhpG";

    static AipFace client=null;
    static {
        client=new AipFace(APP_ID,API_KEY,SERCET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
    }


    public static String detectFace(String filePath,String FileName,String maxFaceNum){

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age,beauty,expression,faceshape,gender,glasses,race,qualities");
        options.put("max_face_num", maxFaceNum);
        options.put("face_type", "LIVE");

        String imageType = "BASE64";
        String imgStr = null;
        try {
            imgStr = Base64Util.encode(FileUtil.readFileByBytes(filePath,FileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //人脸检测
        JSONObject res = client.detect(imgStr, imageType, options);
        try {
            return res.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
            return "0";
        }
    }


}
