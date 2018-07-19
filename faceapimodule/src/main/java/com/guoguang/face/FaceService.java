package com.guoguang.face;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.guoguang.util.FileUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by allen on 2018/7/12.
 */

public class FaceService {

    public static byte[] readFileToBytes(String filePath, String FileName) {
        byte[] image = new byte[1024];
        try {
            image = FileUtil.readFileByBytes(filePath, FileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String faceDetect(String filePath,String fileName,String maxFaceNum)throws JSONException{
        byte[] img=readFileToBytes(filePath,fileName);
        JSONObject res=FaceApiClient.detectFace(img,maxFaceNum);

        JSON json=JSON.parseObject(res.toString());
        FaceConfig config=JSON.toJavaObject(json,FaceConfig.class);

        return config.getResult().getFace_list().get(0).getFace_token();
      /*  if(res.get("error_code").toString().equals("0")){
            int face_num=(int)res.getJSONObject("result").get("face_num");
            if(face_num>0){

                return res.getJSONObject("result").getJSONArray("face_list").getJSONObject(1).get("face_token").toString();

            }
        }else {
            return "0";
        }*/
        //return res.toString(2);
    }


}
