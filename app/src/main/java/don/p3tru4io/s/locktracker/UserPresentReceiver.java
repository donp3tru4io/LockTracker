package don.p3tru4io.s.locktracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserPresentReceiver extends BroadcastReceiver {


    private static final int USER_PRESENTS = 201;
    private static final int SCREEN_ON = 301;
    private static String CHANNEL_ID_1 = "screenon";// The id of the channel.
    private static CharSequence name1 = "Screen On";// The user-visible name of the channel.
    private static String CHANNEL_ID_2 = "userpresents";// The id of the channel.
    private static CharSequence name2 = "User Presents";// The user-visible name of the channel.

    private static String screenOnText = "";

    public static final String CN_SCREEN_ON = "screen_on_notify";
    public static final String CN_USER_PRESENTS = "user_presents_notify";
    public static final String CT_SCREEN_ON = "screen_on_track";
    public static final String CT_USER_PRESENTS = "user_presents_track";
    public static final String C_SCREEN_ON = "screen_on";
    public static final String C_ATTEMPT = "atempts";
    public static final String CP_USER_PRESENTS = "take_photo_user_presents";
    public static final String CP_SCREEN_ON = "take_photo_screenon";

    LockDBHelper DBHelper;
    SQLiteDatabase db;

    private SharedPreferences mSettings;

    private Context context;

    public UserPresentReceiver()
    {

    }


    public UserPresentReceiver(Context _context)
    {
        context = _context;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");


        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
        {
            mSettings =  PreferenceManager.getDefaultSharedPreferences(context);
            if (mSettings.getBoolean(CN_SCREEN_ON,true)) {
                if (!mSettings.getBoolean(C_SCREEN_ON,false)) {
                    screenOnText = formatter.format(date);
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(C_SCREEN_ON, true);
                    editor.apply();
                } else {
                    screenOnText += '\n' + formatter.format(date);
                }
                postNotification(context, context.getResources().getString(R.string.screen_on), screenOnText,
                        SCREEN_ON, CHANNEL_ID_1, name1);
            }
            if (mSettings.getBoolean(CT_SCREEN_ON,true))
            {
                DBHelper = new LockDBHelper(context);
                db = DBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DBHelper.EVENT,context.getResources().getString(R.string.screen_on));
                values.put(DBHelper.DATE,formatter.format(date));
                db.insert(DBHelper.TABLE_NAME,null,values);
                db.close();
            }
            if (mSettings.getBoolean(CP_SCREEN_ON,true))
            {
                SimpleDateFormat fileFormatter = new SimpleDateFormat("HH.mm.ss_dd.MM.yyyy");
                APictureCapturingService pictureService = PictureCapturingServiceImpl.getInstance(context);
                pictureService.startCapturing(fileFormatter.format(date));
            }
        }
        else
        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT))
        {
            mSettings =  PreferenceManager.getDefaultSharedPreferences(context);
            if (mSettings.getBoolean(CN_USER_PRESENTS,true)) {
                postNotification(context, context.getResources().getString(R.string.user_presents), formatter.format(date),
                        USER_PRESENTS, CHANNEL_ID_2, name2);
            }
            if (mSettings.getBoolean(CT_USER_PRESENTS,true))
            {
                DBHelper = new LockDBHelper(context);
                db = DBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DBHelper.EVENT,context.getResources().getString(R.string.user_presents));
                values.put(DBHelper.DATE,formatter.format(date));
                db.insert(DBHelper.TABLE_NAME,null,values);
                db.close();
            }
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(C_SCREEN_ON, false);
            editor.putBoolean(C_ATTEMPT, false);
            editor.apply();
            if (mSettings.getBoolean(CP_USER_PRESENTS,true))
            {
                SimpleDateFormat fileFormatter = new SimpleDateFormat("HH.mm.ss_dd.MM.yyyy");
                APictureCapturingService pictureService = PictureCapturingServiceImpl.getInstance(context);
                pictureService.startCapturing(fileFormatter.format(date));
            }
        }

    }

    private void postNotification(Context context,String title, String text,int ID,String cID,CharSequence cName)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(cID, cName, NotificationManager.IMPORTANCE_LOW);
            //mChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(cID);
        }

        mNotificationManager.notify(ID, mBuilder.build());
    }
}
