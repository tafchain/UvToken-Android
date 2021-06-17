package com.yongqi.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TestMemeryActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private boolean mRunning = false;
    private Button mBtn;

    static class mHandler extends Handler {

        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_memery);

        mBtn = (Button)findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1000000; i++) {
                            String s = "";
                            String q = "";
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRunning = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}