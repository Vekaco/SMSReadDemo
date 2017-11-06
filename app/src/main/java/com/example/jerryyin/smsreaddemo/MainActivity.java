package com.example.jerryyin.smsreaddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final String TAG2 = "SQLiteException in getSmsInPhone";
    private StringBuilder smsBuilder;

    final String SMS_URI_ALL = "content://sms/";
    final String SMS_URI_INBOX = "content://sms/inbox";
    final String SMS_URI_SEND = "content://sms/sent";
    final String SMS_URI_DRAFT = "content://sms/draft";
    final String SMS_URI_OUTBOX = "content://sms/outbox";
    final String SMS_URI_FAILED = "content://sms/failed";
    final String SMS_URI_QUEUED = "content://sms/queued";


    private IntentFilter mIntentFilter;
    private MessageReceiver messageReceiver;

    private TextView mTxtAddress;
    private TextView mTxtMsgBody;

    public static String mMessageBody;
    public static String mAddress;

    public static File mFile = null;
    public static String mDir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtAddress = (TextView) findViewById(R.id.txt_address);
        mTxtMsgBody = (TextView) findViewById(R.id.txt_message_body);

//        createFile();
        createDir();

        Intent intent = new Intent(this, ReceiveMsgService.class);
        startService(intent);

//        Log.d(TAG, "smsMsg = " + ReadSmsMsg());
//
//        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
////        mIntentFilter.setPriority(100);
//        messageReceiver = new MessageReceiver();
//        registerReceiver(messageReceiver, mIntentFilter);
    }

    private void createFile() {
        if (mFile == null){
            String dir = String.valueOf(Environment.getExternalStorageDirectory());
            String dataName = "AMessage.txt";
            mFile = new File(dir, dataName);
            Log.d(TAG, "文件已经创建完毕 ！");
            Toast.makeText(MainActivity.this, "文件已经创建完毕!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 读取手机短信数据库的操作
     *
     * @return
     */
    private String ReadSmsMsg() {
        smsBuilder = new StringBuilder();

        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] destination = new String[]{"_id", "address", "person", "body", "date"};
            Cursor cur = getContentResolver().query(uri, destination, null, null, "date desc");

            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
//                int index_Type = cur.getColumnIndex("TYPE");

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strBody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
//                    int intType = cur.getInt(index_Type);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);

                    String strType = "";
//                    if (intType == 1) {
//                        strType = "接收";
//                    } else if (intType == 2) {
//                        strType = "发送";
//                    } else {
//                        strType = "null";
//                    }

                    smsBuilder.append("[ ");
                    smsBuilder.append(strAddress + ", ");
                    smsBuilder.append(intPerson + ", ");
                    smsBuilder.append(strBody + ", ");
                    smsBuilder.append(strDate + ", ");
                    smsBuilder.append(strType);
                    smsBuilder.append(" ]\n\n");
                } while (cur.moveToNext());
                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            } // end if
            smsBuilder.append("getSmsInPhone has executed!");
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }

        return smsBuilder.toString();
    }



    //判断有无sd_card(是否插入)
    public boolean haveSdCard(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    //然后根据是否插入状态指定目录
    public String toDir(){
        if (haveSdCard()) {
            mDir = Environment.getExternalStorageDirectory() + "/AMessage";
        }
//        else {
//            dir = NOSDCARD_DIR;
//        }
        return mDir;
    }

    //创建文件夹
    public void createDir(){
        File destDir = new File(toDir());
        if (!destDir.exists()) {
            destDir.mkdirs();
            Toast.makeText(MainActivity.this, "文件已经创建完毕!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(messageReceiver);
    }
}
