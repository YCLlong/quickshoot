package cn.ycl.quickshoot.task;

import android.hardware.Camera;
import android.media.MediaRecorder;

public class ShootTask implements Runnable {
    private Camera myCamera;
    private String fileName;
    /**
     * 录制视频的类
     */
    private MediaRecorder mediarecorder;


    public ShootTask( Camera myCamera,String fileName){
        this.myCamera = myCamera;
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        this.fileName = fileName;
    }

    @Override
    public void run() {
        myCamera.unlock();
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
        mediarecorder.setOutputFile(fileName);
        try {
            mediarecorder.prepare(); // 准备录制
            mediarecorder.start(); // 开始录制
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
