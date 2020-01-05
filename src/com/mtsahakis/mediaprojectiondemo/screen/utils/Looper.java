package com.mtsahakis.mediaprojectiondemo.screen.utils;

import android.os.Handler;

class Looper {
    private final Variables variables;

    public Looper(Variables variables) {
        this.variables = variables;
    }

    void startLooper() {
        variables.looper = new Thread() {
            @Override
            public void run() {
                android.os.Looper.prepare();

                // prepare a handler before initiating the looper

                variables.mHandler = new Handler();

                // initiate the looper
                //
                // the Looper.loop() function blocks while looping
                android.os.Looper.loop();

                // clean up here

            }
        };
        variables.looper.start();
    }

    void stopLooper() {
        try {
            variables.mHandler.getLooper().getThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        variables.looper = null;
    }
}
