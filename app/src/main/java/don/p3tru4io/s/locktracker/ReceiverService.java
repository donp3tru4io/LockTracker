package don.p3tru4io.s.locktracker;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import static don.p3tru4io.s.locktracker.ForegroundApp.CHANNEL_ID;

public class ReceiverService extends Service {
    public ReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);*/

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Service")
                    .setContentText("Broadcast Receiver")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    //.setContentIntent(pendingIntent)
                    .build();

        }
        else
        {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Service")
                    .setContentText("Broadcast Receiver")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    //.setContentIntent(pendingIntent)
                    .build();
        }

        startForeground(1, notification);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(new UserPresentReceiver(getBaseContext()), filter);

        return Service.START_STICKY;
    }
}
