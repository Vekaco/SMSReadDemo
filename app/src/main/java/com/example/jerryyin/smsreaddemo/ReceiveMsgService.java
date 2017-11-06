package com.example.jerryyin.smsreaddemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

/**
 * Created by JerryYin on 11/20/15.
 * 服务中注册广播
 */
public class ReceiveMsgService extends Service {


    private static final String TAG = "ReceiveMsgService";
    private MessageReceiver messageReceiver;
    private IntentFilter mIntentFilter;
    private Intent mIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "服务已经启动 ！");
        mIntent = intent;
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver, mIntentFilter);
        Log.d(TAG, "服务已经启动，广播注册完毕！");

        /**
         * 显示一个标题通知
         */
        Notification notification = new Notification(R.mipmap.ic_launcher,
                getString(R.string.app_name), System.currentTimeMillis());

        PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this, "SmsReceiverService", "请保持程序在后台运行",
                pendingintent);
        startForeground(0x111, notification);


        flags = START_STICKY;   //粘性
        return super.onStartCommand(intent, flags, startId);
//        return START_REDELIVER_INTENT;
    }

    /**
     * 保证服务长期运行
     */
    @Override
    public void onDestroy() {
        System.out.println("service onDestroy");
        stopForeground(true);
        unregisterReceiver(messageReceiver);

        /**
         * 保留了开启service的intent，在这里再启动一次自己，以达到长期运行的服务，不被系统杀死
         * 当使用类似口口管家等第三方应用或是在setting里-应用-强制停止时，APP进程可能就直接被干掉了，
         * onDestroy方法都进不来，所以还是无法保证~.~
         */
        if (mIntent != null) {
            System.out.println("serviceIntent not null");
            startService(mIntent);
        } else {
            startService(new Intent(this, ReceiveMsgService.class));
        }
        super.onDestroy();
    }
}
