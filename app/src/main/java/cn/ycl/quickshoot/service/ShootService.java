package cn.ycl.quickshoot.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ShootService extends Service {
    /**
     * 文件根目录
     */
    private final String filePath = Environment.getExternalStorageDirectory().getPath() + "/10101010";
    /**
     * 录制视频的类
     */
    private static MediaRecorder mediarecorder;

    /**
     * 摄像头对象
     */
    @SuppressWarnings({ "deprecation", "unused" })
    private static Camera myCamera = null;
    @SuppressWarnings({ "deprecation", "unused" })

    /**
     * 摄像参数对象
     */
    private Camera.Parameters myParameters;

    /**
     * 标记是否在录制
     */
    public static boolean isStarted = false;


    private Timer timer = new Timer();

    private int second = 0;

    private Handler handler;


    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        /**
         * 获取SD卡或者内置存储空间可以保存资源的路径.
         * <em>此处未实现对存储空间是否充足进行判断</em>.
         *
         * @return 返回保存数据的路径, 有SD卡则是SD上的路径, 反之内置存储空间上的路径.
         */

        File file = new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }
        handler = new Handler(){
            public void handleMessage(Message msg) {
                FloatingService.button.setText(msg.getData().getString("s"));
                super.handleMessage(msg);
            }

        };
        super.onCreate();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 每次通过startService()方法启动Service时都会被回调。
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isStarted = !isStarted;
        if(!isStarted){
            //暂停录制
            stopShoot();
            if(timer != null){
                timer.cancel();
            }
            FloatingService.button.setText("0");
        }else{
            //开始录制
            startShoot();
            //计时
            second = 0;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    Bundle b = new Bundle();
                    b.putString("s", String.valueOf(second));
                    message.setData(b);
                    handler.sendMessage(message);

                    second++;
                }
            },0,1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 销毁服务时调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startShoot(){
        if(!initCamera()){
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }
        myCamera.unlock();
        if (null == mediarecorder) {
            mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        }
        mediarecorder.setCamera(myCamera);
        // 设置录制视频源为Camera(相机)
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoSize(1920, 1080);

        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoFrameRate(30);


        // 设置视频文件输出的路径
        mediarecorder.setOutputFile(filePath +"/" + System.nanoTime() + ".shoot");
        try {
            mediarecorder.prepare(); // 准备录制
            mediarecorder.start(); // 开始录制
        } catch (Exception e) {
            Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public void stopShoot(){
        if (mediarecorder != null) {
            mediarecorder.stop();  // 停止录制
            mediarecorder.release();   // 释放资源
            mediarecorder = null;
        }
    }

    private boolean initCamera(){
        if(null == myCamera) {
            // 打开后置摄像头
            @SuppressWarnings("unused")
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras(); // get cameras number
            if(cameraCount > 0){
                for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {
                    Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo
                    // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                    if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_BACK ) {
                        try {
                            myCamera = Camera.open(camIdx);
                            return true;
                        } catch (RuntimeException e) {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
}
