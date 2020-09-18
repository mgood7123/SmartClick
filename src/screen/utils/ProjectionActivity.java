package screen.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ProjectionActivity extends Activity {
    private static final int REQUEST_CODE_MEDIA_PROJECTION = 4578;
    private static MediaProjectionHelper m_mediaProjectionHelper;

    public static void requestProjectionIntentActivity(Context ctx, MediaProjectionHelper mediaProjectionHelper) {
        m_mediaProjectionHelper = mediaProjectionHelper;
        Intent pIntent = new Intent(ctx, ProjectionActivity.class);
        pIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY);
        ctx.startActivity(pIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_mediaProjectionHelper.requestCapturePermission(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        m_mediaProjectionHelper.variables.log.log("onActivityResult");
        if (requestCode == m_mediaProjectionHelper.variables.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //send screen capture intent (data) to service
                m_mediaProjectionHelper.startCapture(resultCode, data);
            } else {
                //FAIL
            }
            finishAndRemoveTask();
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
}