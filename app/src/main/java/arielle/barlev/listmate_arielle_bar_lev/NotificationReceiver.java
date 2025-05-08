package arielle.barlev.listmate_arielle_bar_lev;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");

        Intent notification_intent = new Intent(context, Alert_Scheduling.class);

        PendingIntent content_intent = PendingIntent.getActivity(
                context,
                0,
                notification_intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        int notification_id = (int) System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Scheduled Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(content_intent)
                .setAutoCancel(true)
        ;

        NotificationManager notification_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification_manager.notify(notification_id, builder.build());
    }
}
