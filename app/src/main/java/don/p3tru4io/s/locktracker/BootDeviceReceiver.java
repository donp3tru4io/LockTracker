package don.p3tru4io.s.locktracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

public class BootDeviceReceiver extends BroadcastReceiver {


    private static final String BOOT_COMPLETED = Manifest.permission.RECEIVE_BOOT_COMPLETED;
    private SharedPreferences mSettings;
    public static final String CB_BOOT = "start_on_boot";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (isPermissionGranted(BOOT_COMPLETED,context)){
            mSettings =  PreferenceManager.getDefaultSharedPreferences(context);
            if(mSettings.getBoolean(CB_BOOT,true)) {
                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(new Intent(context, ReceiverService.class));
                    } else {
                        context.startService(new Intent(context, ReceiverService.class));
                    }
                }
            }
        }
    }

    private boolean isPermissionGranted(String permission,Context context) {
        int permissionCheck = ActivityCompat.checkSelfPermission(context, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }
}
