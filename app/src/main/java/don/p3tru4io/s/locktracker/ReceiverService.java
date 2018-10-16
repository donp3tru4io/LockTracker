package don.p3tru4io.s.locktracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
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

    private static UserPresentReceiver userPresentReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);*/

        start();
        return Service.START_STICKY;
        //return Service.START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        unregisterReceiver(userPresentReceiver);
        restart();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(userPresentReceiver);
        restart();
        super.onDestroy();
    }

    void start()
    {
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

        startForeground((int)System.currentTimeMillis(), notification);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        userPresentReceiver = new UserPresentReceiver(getBaseContext());
        registerReceiver(userPresentReceiver, filter);
    }

    private void restart()
    {
        AlarmManager am =( AlarmManager)getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(),ReceiverService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 3000, pi);
        am.set(AlarmManager.RTC_WAKEUP,1000,pi);
    }

}
