package screen.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;

public class ScreenUtils {

    public Variables variables = new Variables();

    public void onCreate(@NonNull Service service, final Variables.Callback runOnUiThread) {
        // service overload
        variables.service = service;
        variables.mProjectionManager = variables.mediaProjectionHelper.getMediaProjectionManager();
        variables.cacheDir = service.getCacheDir().getAbsolutePath();
        variables.layoutInflater = LayoutInflater.from(service);
        variables.setRunOnUIThread(runOnUiThread);
    }

    public void onCreate(@NonNull Activity activity) {
        variables.activity = activity;
        variables.mProjectionManager = variables.mediaProjectionHelper.getMediaProjectionManager();
        variables.cacheDir = activity.getCacheDir().getAbsolutePath();
        variables.layoutInflater = LayoutInflater.from(activity);
        variables.setRunOnUIThread(new Variables.Callback() {
            @Override
            public void run(Object o) {
                variables.activity.runOnUiThread((Runnable) o);
            }
        });
    }

    public void setImageView(ImageView imageView) {
        variables.imageView = imageView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == variables.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                variables.mediaProjectionHelper.startCapture(resultCode, data);
            }
        } else if (requestCode == variables.REQUEST_CODE_FLOATING_WINDOW) {
            if (resultCode == RESULT_OK) {
                startFloatingWindowService();
            }
        }
    }

    public void createFloatingWidget() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkDrawOverlayPermission()) {
                startFloatingWindowService();
            }
        }
    }

    void startFloatingWindowService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(variables.activity)) {
                variables.activity.startService(new Intent(variables.activity, FloatingViewService.class));
            }
        }
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!Settings.canDrawOverlays(variables.activity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + variables.activity.getPackageName()));
            variables.activity.startActivityForResult(intent, variables.REQUEST_CODE_FLOATING_WINDOW);
            return false;
        } else {
            return true;
        }
    }

    public void takeScreenShot() {
        variables.screenRecord = false;
        variables.mediaProjectionHelper.takeScreenShot();
    }

    public void startScreenMirror() {
        variables.screenRecord = false;
        variables.mediaProjectionHelper.startScreenMirror();
    }

    public void stopScreenMirror() {
        variables.mediaProjectionHelper.stopScreenMirror();
    }

    public void startScreenRecord() {
        variables.mediaProjectionHelper.startScreenRecord();
    }

    public void stopScreenRecord() {
        variables.mediaProjectionHelper.stopScreenRecord();
    }
}
