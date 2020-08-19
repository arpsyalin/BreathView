package com.lyl.demo;


import android.app.Activity;
import android.os.Bundle;

import com.lyl.breathview.BreathView;

public class MainActivity extends Activity {

    private BreathView mBv1;
    int i;
    boolean add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBv1 = findViewById(R.id.bv_1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (add) {
                        i++;
                    } else {
                        i--;
                    }
                    if (i > 100) {
                        i = 100;
                        add = false;
                    } else if (i < 0) {
                        i = 0;
                        add = true;
                    }
                    mBv1.setProgress(i);
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
