package info.nightscout.androidaps.plugins.Persistentnotification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import info.nightscout.androidaps.MainActivity;
import info.nightscout.androidaps.R;

/**
 * Created by adrian on 06/04/18.
 */

public class DummyService extends Service {
    private final IBinder mBinder = new DummyBinder();

    public static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(PersistentNotificationPlugin.ONGOING_NOTIFICATION_ID, createNotification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class DummyBinder extends Binder {
        DummyService getService() {
            return DummyService.this;
        }
    }

    private Notification createNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PersistentNotificationPlugin.CHANNEL_ID);
        builder.setOngoing(true);
        builder.setCategory(NotificationCompat.CATEGORY_STATUS);
        builder.setSmallIcon(R.drawable.ic_notification);
        Bitmap largeIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.blueowl);
        builder.setLargeIcon(largeIcon);
        builder.setContentTitle("asf");
        builder.setContentText("asf");
        builder.setSubText("asf");

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        return builder.build();
    }
}
