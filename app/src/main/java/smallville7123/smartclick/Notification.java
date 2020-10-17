package smallville7123.smartclick;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification {
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManager;
    Service service;
    Activity activity;

    public Notification(Activity activity) {
        this(activity, null);
    }

    public Notification(Service service) {
        this(service, null);
    }

    public Notification(Context context) {
        this(context, null);
    }

    public Notification(Activity activity, String title) {
        this(activity, title, null);
    }

    public Notification(Service service, String title) {
        this(service, title, null);
    }

    public Notification(Context context, String title) {
        this(context, title, null);
    }

    public Notification(Activity activity, String title, String description) {
        build(activity, null, activity, title, description, null, null, null, true, false, Color.GREEN);
    }

    public Notification(Service service, String title, String description) {
        build(null, service, service, title, description, null, null, null, true, false, Color.GREEN);
    }

    public Notification(Context context, String title, String description) {
        build(null, null, context, title, description, null, null, null, true, false, Color.GREEN);
    }

    void build(Activity activity, Service service, Context context, String title, String description, String expandedDescription, Integer smallIcon, Bitmap largeIcon, Boolean persistant, Boolean containsTimeSinceWhen, Integer colour) {
        this.activity = activity;
        this.service = service;
        notificationBuilder = createNotificationChannel(context, "testId");
        notificationBuilder.setSmallIcon(smallIcon != null ? smallIcon : R.drawable.ic_launcher);
        if (largeIcon != null) notificationBuilder.setLargeIcon(largeIcon);
        notificationBuilder.setContentTitle(title != null ? title : "Notification Title");
        notificationBuilder.setContentText(description != null ? description : "Notification Description\nTap to view properties");
        if (persistant) {
            notificationBuilder.setOngoing(true);
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        }
        if (containsTimeSinceWhen) notificationBuilder.setUsesChronometer(true);
        if (colour != null) {
            notificationBuilder.setColorized(true);
            notificationBuilder.setColor(colour);
        }
        boolean expandedRequired = false;
        if (description != null) expandedRequired = expandedDescription != null;
        else expandedRequired = true;
        if (activity != null) {
            Intent i = new Intent(context, activity.getClass());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationBuilder.setContentIntent(PendingIntent.getActivity(activity, 0, i, 0));
            notificationBuilder.setAutoCancel(true);
        }
        if (expandedRequired) notificationBuilder.setStyle(
                new NotificationCompat.BigTextStyle().bigText(
        expandedDescription != null ? expandedDescription
        : "Notification Properties:\n" +
                "Small icon supplied: " + (smallIcon != null) + "\n" +
                (
        smallIcon != null ? "Small icon value:" + smallIcon + "\n"
        : ""
                        ) +
                "Large icon supplied: " + (largeIcon != null) + "\n" +
                "Title supplied: " + (title != null) + "\n" +
                "Title: " + title + "\n" +
                "Description supplied: " + (description!= null) + "\n" +
                "Description: " + description + "\n" +
                "Is expandable: true\n" +
                "Expanded description supplied: " + (expandedDescription != null) + "\n" +
                "Expanded description: " +
                (
        expandedDescription != null ? expandedDescription
        : "the text you are currently reading"
                        ) + "\n" +
                "Can be dismissed: " + persistant + "\n" +
                "Can be coloured: " + (colour != null) + "\n" +
                (
        colour != null ? "colour (ARGB): " + colour + "\n"
        : ""
                        ) +
                "Contains time since when: " + containsTimeSinceWhen + "\n"
            )
        );
        notificationManager = NotificationManagerCompat.from(context);
    }

    /**
     * Create the NotificationChannel, but only on API 26+ because
     * the NotificationChannel class is new and not in the support library
     * @return
     */
    private NotificationCompat.Builder createNotificationChannel(Context context, String channelId) {
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "test notification";
            String descriptionText = "test description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(descriptionText);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            return new NotificationCompat.Builder(context, channelId);
        } else {
            Log.wtf("NotificationCreator", "could not create notification channel, API 25 and below do not support NotificationChannel class");
            return null;
        }
    }

    // notificationId is a unique int for each notification that you must define
    public void show(Integer id) {
        if (service != null) service.startForeground(id, notificationBuilder.build());
        else notificationManager.notify(id, notificationBuilder.build());
    }
}
