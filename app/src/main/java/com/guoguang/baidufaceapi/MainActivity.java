package com.guoguang.baidufaceapi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button detectFace;
    private TextView result;
    private static final String TAG="FaceApiActivity";

    private String filePath="/mnt/sdcard/face";
    private String maxFaceNum="2";
    private String fileName="test.jpg";

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String data=(String)msg.obj;
            result.setText(data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detectFace=(Button)findViewById(R.id.detectFace);
        result=(TextView)findViewById(R.id.result);
        detectFace.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detectFace:
                MyThread myThread=new MyThread();
                myThread.start();
                break;
        }
    }

    public class MyThread extends Thread{
        @Override
        public void run() {
            String res=FaceApiClient.detectFace(filePath,fileName,maxFaceNum);
            Message msg=new Message();
            msg.obj=res;
            mHandler.sendMessage(msg);
            Log.d(TAG,"res=="+res);
        }
    }


    public void connectClient(AipFace client) {

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
       // client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", 127.0.0.1);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age,beauty,expression,faceshape,gender,glasses,race,qualities");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");

        String filePath = "/mnt/sdcard/face";
        String imgStr = null;
        try {
            imgStr = Base64Util.encode(FileUtil.readFileByBytes(filePath,fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imageType = "BASE64";
        JSONObject res = client.detect(imgStr, imageType, options);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
