package screen.utils;

import android.os.Binder;

public class MediaProjectionBinder extends Binder {

    private final FloatingViewService floatingViewService;

    public MediaProjectionBinder(FloatingViewService floatingViewService) {
        this.floatingViewService = floatingViewService;
    }
}
