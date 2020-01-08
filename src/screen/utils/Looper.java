package screen.utils;

import android.os.Handler;
import android.util.Log;

public class Looper {
    private final Variables variables;

    public Looper(Variables variables) {
        this.variables = variables;
    }

    public void startLooper() {
        variables.looper = new Thread() {
            @Override
            public void run() {
                Log.e("Looper", "preparing");
                android.os.Looper.prepare();

                // prepare a handler before initiating the looper

                variables.mHandler = new Handler();

                // initiate the looper
                //
                // the Looper.loop() function blocks while looping
                Log.e("Looper", "looping");
                android.os.Looper.loop();

                // clean up here
                Log.e("Looper", "ended");

            }
        };
        Log.e("Looper", "starting");
        variables.looper.start();
    }

    public void stopLooper() {
        try {
            Log.e("Looper", "joining looper");
            variables.mHandler.getLooper().getThread().join();
            Log.e("Looper", "joined looper");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        variables.looper = null;
    }
}
