package com.example.jerryyin.smsreaddemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by JerryYin on 11/22/15.
 * 接收系统广播开启服务
 */
public class BootReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            System.out.println("手机开机了....");
            context.startService(new Intent(context, ReceiveMsgService.class));
//            startUploadService(context);
        }
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            context.startService(new Intent(context, ReceiveMsgService.class));
//            startUploadService(context);
        }
    }
}
