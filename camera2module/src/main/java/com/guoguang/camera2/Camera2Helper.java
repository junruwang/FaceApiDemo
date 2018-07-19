package com.guoguang.camera2;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * Created by allen on 2018/7/19.
 */

public class Camera2Helper {
    private static final String TAG="Camera2Helper";

    private CameraManager manager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;
    private CaptureRequest.Builder mPreviewRequestBuilder;

    private static Camera2Helper camera2Helper;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private Camera2TextureView mTextureView;
    private Activity activity;
    private Context mContext;
    //图片位置
    private File mFile;

    private int mState = STATE_PREVIEW;

    private static final int STATE_PREVIEW = 0;

    private String mCameraId;

    private Boolean mFlashSupported;

    //计数信号量 控制共享资源的访问个数
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private Camera2Helper(Activity act, Camera2TextureView view, File file, Context context) {
        manager=(CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        activity = act;
        mTextureView = view;
        mFile = file;
        mContext=context;
    }

    public static Camera2Helper getInstace(Activity act, Camera2TextureView view, File file,Context context) {
        if (camera2Helper == null) {
            synchronized (Camera2Helper.class) {
                camera2Helper = new Camera2Helper(act, view, file,context);
            }
        }
        return camera2Helper;
    }

    /**
     * TextureView监听
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    /**
     * 摄像头状态监听回调
     */
    private final CameraDevice.StateCallback mStateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            mCameraDevice=cameraDevice;
            //创建CameraPreviewSession
            createCameraCaptureSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice=null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice,  int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice=null;
        }
    };


    /**
     * 相机捕获会话回调
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback=new CameraCaptureSession.CaptureCallback() {
       private void process(CaptureResult result){
           switch (mState){
               case STATE_PREVIEW:
                   //预览则什么都不做
                   break;
           }
       }

        @Override
       public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
           process(partialResult);
            //super.onCaptureProgressed(session, request, partialResult);
       }

       @Override
       public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
           process(result);
           //super.onCaptureCompleted(session, request, result);
       }

       @Override
       public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
           super.onCaptureFailed(session, request, failure);
       }
   };

    /**
     * 为相机预览创建新的CameraCaptureSession
     */
    private void createCameraCaptureSession(){
        //设置了一个具有输出Surface的CaptureRequest.Builder
        try {
            //获取view
            SurfaceTexture texture=mTextureView.getSurfaceTexture();
            Surface surface=new Surface(texture);

            //创建预览请求
            mPreviewRequestBuilder=mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            //创建一个CameraCaptureSession来进行相机预览
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(mCameraDevice==null){
                        return;
                    }

                    //session准备好了，开始显示预览
                    mCaptureSession=cameraCaptureSession;

                    try {
                        //自动对焦
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        //设置闪光灯自动
                        setAutoFlash(mPreviewRequestBuilder);
                        //开启相机预览并添加事件
                        mPreviewRequest=mPreviewRequestBuilder.build();
                        mCaptureSession.setRepeatingRequest(mPreviewRequest,mCaptureCallback,mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    showToast("Failed");

                }
            },null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void openCamera(){
        try {
            setCameraParamters();
            manager.openCamera(mCameraId,mStateCallback,mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }


    public void startCamerapreview(){
        //开启后台线程
        startBackgroundThread();
        if(mTextureView.isAvailable()){
            //直接打开
            openCamera();
        }else {
            //未准备好，设置监听
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    /**
     * 关闭Camera2帮助类
     */
    public void onDestoryHelper(){
        stopBackgroundThread();
        closeCamera();
        activity=null;
        mTextureView=null;

    }

    /**
     * 后台线程与handler
     */
    private void startBackgroundThread(){
        mBackgroundThread=new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler=new Handler(mBackgroundThread.getLooper());
    }

    /**
     * 停止后台线程
     */
    private void stopBackgroundThread(){
        if(mBackgroundThread!=null){
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread=null;
                mBackgroundHandler=null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭相机
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            /*if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }*/
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    public void setCameraParamters(){
        try {
            //获取可用摄像头列表
            for(String cameraId:manager.getCameraIdList()){
                //获取相机的相关参数
                CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraId);
                //不适用前置摄像头
                Integer facing=characteristics.get(CameraCharacteristics.LENS_FACING);
                if(facing!=null&&facing==CameraCharacteristics.LENS_FACING_FRONT){
                    continue;
                }
                StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if(map==null){
                    continue;
                }

                // 检查闪光灯是否支持
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId=cameraId;
                Log.d(TAG,"相机可以使用");
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            //不支持Camera2API
        }


    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder){
        if(mFlashSupported){
            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    private void showToast(final String text) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
