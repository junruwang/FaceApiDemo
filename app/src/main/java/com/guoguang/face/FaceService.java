package com.guoguang.face;

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
        return res.toString(2);
    }


}
