package cn.ycl.quickshoot.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.concurrent.locks.LockSupport;

public class ShootService extends Service {
    public static boolean isStarted = false;

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
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
            FloatingService.button.setText("0");
            //暂停录制
        }else{
            //开始录制
            FloatingService.button.setText(""+Math.random() * 100);
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
}
