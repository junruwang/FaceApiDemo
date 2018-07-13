package com.guoguang.baidufaceapi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.guoguang.face.FaceApiClient;
import com.guoguang.face.FaceConfig;
import com.guoguang.face.FaceService;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button detectFace;
    private TextView result;
    private static final String TAG = "FaceApiActivity";

    private String filePath = "/mnt/sdcard/face";
    private String maxFaceNum = "2";
    private String fileName = "test2.jpg";

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = (String) msg.obj;
            result.setText(data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detectFace = (Button) findViewById(R.id.detectFace);
        result = (TextView) findViewById(R.id.result);
        detectFace.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detectFace:
                MyThread myThread = new MyThread();
                myThread.start();
                break;
        }
    }

    public class MyThread extends Thread {
        @Override
        public void run() {

            try {
                //res = FaceApiClient.detectFace(filePath,fileName,maxFaceNum);
                //byte[] img1 = FaceService.readFileToBytes(filePath, fileName);
                //fileName="dlrb1.jpeg";
                //byte[] img2=FaceApiClient.readFileToBytes(filePath,fileName);
                //res = FaceApiClient.addUserFace(img,"dlrb2","user1","group1");
                //res = FaceApiClient.updateUserFace(img,"user1","group1");
                //res = FaceApiClient.deleteUserFace("user1","group1","ce27f3aff0f959730b92ccc4eb8adb77");
                //res = FaceApiClient.getUserInfo("user1","group1");
                //res = FaceApiClient.getUserFaceList("user1","group1");
                //res = FaceApiClient.getGroupUsers("group1");
                //res = FaceApiClient.copyUser("user1","group1","group2");
                //res = FaceApiClient.deleteUser("user1","group2");
                //res = FaceApiClient.deleteGroup("group3");
                //res = FaceApiClient.getGroupList();
                //res = FaceApiClient.personVerify(img,"320402199011294000","wangjr");
                //res = FaceApiClient.matchFace(img1,img2);
                //String res = FaceApiClient.faceVerify(img1).toString(2);
                String res=FaceService.faceDetect(filePath,fileName,"5");
                /*JSONObject res = FaceApiClient.searchFace(img1,null,"group1,group2");
                Log.d(TAG,"val==="+res.toString(2));
                Log.d(TAG,"val2==="+ res.getJSONObject("result").get("face_token"));*/
                Message msg = new Message();
                msg.obj = res;
                mHandler.sendMessage(msg);
                Log.d(TAG, "res==" + res);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
