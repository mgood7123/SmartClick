package screen.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 通知栏 帮助类
 * Created by lishilin on 2017/7/31.
 */
public class NotificationHelper {

    private static final String CHANNEL_ID_OTHER = "other";
    private static final String CHANNEL_NAME_OTHER = "其他消息";
    @TargetApi(Build.VERSION_CODES.O)
    private static final int CHANNEL_IMPORTANCE_OTHER = NotificationManager.IMPORTANCE_MIN;

    private static final String CHANNEL_ID_SYSTEM = "system";
    private static final String CHANNEL_NAME_SYSTEM = "系统通知";
    @TargetApi(Build.VERSION_CODES.O)
    private static final int CHANNEL_IMPORTANCE_SYSTEM = NotificationManager.IMPORTANCE_HIGH;

    private static class InstanceHolder {
        private static final NotificationHelper instance = new NotificationHelper();
    }

    public static NotificationHelper getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 创建通知渠道
     */
    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        createChannel(context, CHANNEL_ID_OTHER, CHANNEL_NAME_OTHER, CHANNEL_IMPORTANCE_OTHER, false);
        createChannel(context, CHANNEL_ID_SYSTEM, CHANNEL_NAME_SYSTEM, CHANNEL_IMPORTANCE_SYSTEM, true);
    }

    /**
     * 创建通知渠道
     *
     * @param channelId   channelId
     * @param channelName channelName
     * @param importance  importance
     * @param isShowBadge 是否显示角标
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(Context context, String channelId, String channelName, int importance, boolean isShowBadge) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(isShowBadge);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 创建通知栏 Builder
     *
     * @return NotificationCompat.Builder
     */
    public NotificationCompat.Builder create(Context context, String channelId, String title, int smallDrawableIcon, int mipmapIcon) {
        createChannel(context);
        return new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(smallDrawableIcon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), mipmapIcon));
    }

    /**
     * 创建通知栏 Builder
     *
     * @return NotificationCompat.Builder
     */
    public NotificationCompat.Builder createOther(Context context, String title, int smallDrawableIcon, int mipmapIcon) {
        return create(context, CHANNEL_ID_OTHER, title, smallDrawableIcon, mipmapIcon);
    }

    /**
     * 创建通知栏 Builder
     *
     * @return NotificationCompat.Builder
     */
    public NotificationCompat.Builder createSystem(Context context, String title, int smallDrawableIcon, int mipmapIcon) {
        return create(context, CHANNEL_ID_SYSTEM, title, smallDrawableIcon, mipmapIcon);
    }

    /**
     * 显示通知栏
     *
     * @param id           id
     * @param notification notification
     */
    public void show(Context context, int id, Notification notification) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(id, notification);
        }
    }

}