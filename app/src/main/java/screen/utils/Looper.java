package screen.utils;

import android.os.Handler;

public class Looper {
    private final Variables variables;

    public Looper(Variables variables) {
        this.variables = variables;
    }

    public void startLooper() {
        variables.looper = new Thread() {
            @Override
            public void run() {
                variables.log.logWithClassName(Looper.this, "preparing");
                android.os.Looper.prepare();

                // prepare a handler before initiating the looper

                variables.mHandler = new Handler();

                // initiate the looper
                //
                // the Looper.loop() function blocks while looping
                variables.log.logWithClassName(Looper.this, "looping");
                android.os.Looper.loop();

                // clean up here
                variables.log.logWithClassName(Looper.this, "ended");

            }
        };
        variables.log.logWithClassName(this, "starting");
        variables.looper.start();
    }

    public void stopLooper() {
        try {
            variables.log.logWithClassName(this, "joining looper");
            variables.mHandler.getLooper().getThread().join();
            variables.log.logWithClassName(this, "joined looper");
        } catch (InterruptedException e) {
            variables.log.errorWithClassName(this, e);
        }
        variables.looper = null;
    }
}
