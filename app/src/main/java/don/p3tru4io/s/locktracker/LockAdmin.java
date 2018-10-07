package don.p3tru4io.s.locktracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LockAdmin extends DeviceAdminReceiver{

    private static final int PASSWORD_FAILED = 102;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    private static final SimpleDateFormat fileFormatter = new SimpleDateFormat("HH.mm.ss_dd.MM.yyyy");

    public  static String notificationText = "";

    private static String CHANNEL_ID = "failedPassword";// The id of the channel.
    private static CharSequence name = "Password Failed";// The user-visible name of the channel.
    private static int importance = NotificationManager.IMPORTANCE_LOW;


    public static final String CN_FAILED_PASSWORD = "failed_password_notify";
    public static final String CT_FAILED_PASSWORD = "failed_password_track";
    public static final String C_TAKE_PHOTO = "take_photo";
    public static final String C_ATTEMPT = "atempts";

    private static SharedPreferences mSettings;

    @Override
    public void onEnabled(Context context, Intent intent) {

    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "This is an optional message to warn the user about disabling.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {

    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        Date date = new Date();
        mSettings =  PreferenceManager.getDefaultSharedPreferences(context);
        if (mSettings.getBoolean(CN_FAILED_PASSWORD,true)) {
            if (!mSettings.getBoolean(C_ATTEMPT,false)) {
                notificationText = formatter.format(date);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putBoolean(C_ATTEMPT, true);
                editor.apply();
            } else {
                notificationText += '\n' + formatter.format(date);

            }


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle(context.getResources().getString(R.string.failed_password))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText));

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                //mChannel.setSound(null, null);
                mNotificationManager.createNotificationChannel(mChannel);
                mBuilder.setChannelId(CHANNEL_ID);
            }
            mNotificationManager.notify(PASSWORD_FAILED, mBuilder.build());
        }
        if (mSettings.getBoolean(C_TAKE_PHOTO,true)) {
            APictureCapturingService pictureService = PictureCapturingServiceImpl.getInstance(context);
            pictureService.startCapturing(fileFormatter.format(date));
        }
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        /*mSettings =  PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mReset.edit();
        editor.putBoolean(C_ATTEMPT, false);
        editor.apply();*/
        //createNotification(context,102,context.getResources().getString(R.string.succeeded_password));
    }

}