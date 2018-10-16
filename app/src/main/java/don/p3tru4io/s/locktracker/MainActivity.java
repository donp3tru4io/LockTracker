package don.p3tru4io.s.locktracker;

import android.Manifest;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int ADMIN_INTENT = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponent;

    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String CAMERA = Manifest.permission.CAMERA;
    private static final String BOOT_COMPLETED = Manifest.permission.RECEIVE_BOOT_COMPLETED;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1002;
    private static final int REQUEST_CAMERA = 1003;
    private static final int REQUEST_BOOT = 1004;

    private Switch sAdmin;
    private Button bCamera, bStorage,bBoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        mAdminComponent = new ComponentName(this, LockAdmin.class);

        sAdmin = findViewById(R.id.sAdmin);
        bCamera = findViewById(R.id.bCamera);
        bStorage =findViewById(R.id.bStorage);
        bBoot =findViewById(R.id.bBoot);


        if (isAdmin())
        {
           sAdmin.setChecked(true);
        } else {
            sAdmin.setChecked(false);
        }

        if (isPermissionGranted(CAMERA))
        {
            bCamera.setText("Camera permission granted");
            bCamera.setEnabled(false);
        }
        else
        {
            bCamera.setText("Grant camera permission");
            bCamera.setEnabled(true);
        }

        if (isPermissionGranted(WRITE_EXTERNAL_STORAGE))
        {
            bStorage.setText("Storage permission granted");
            bStorage.setEnabled(false);
        }
        else
        {
            bStorage.setText("Grant storage permission");
            bStorage.setEnabled(true);
        }


        if (isPermissionGranted(BOOT_COMPLETED))
        {
            bBoot.setText("Boot receiving permission granted");
            bBoot.setEnabled(false);
        }
        else
        {
            bBoot.setText("Grant boot receiving permission");
            bBoot.setEnabled(true);
        }

        sAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminComponent);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Administrator description");
                    startActivityForResult(intent, ADMIN_INTENT);
                }
                else
                {
                    mDevicePolicyManager.removeActiveAdmin(mAdminComponent);
                    Toast.makeText(getApplicationContext(), "LockAdmin disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(CAMERA, REQUEST_CAMERA);
            }
        });

        bStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        });

        bBoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(BOOT_COMPLETED, REQUEST_BOOT);
            }
        });


        if (!isMyServiceRunning(ReceiverService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ReceiverService.class));
            } else {
                startService(new Intent(this, ReceiverService.class));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "LockAdmin enabled", Toast.LENGTH_SHORT).show();
                sAdmin.setChecked(true);
            }
            else {
                Toast.makeText(getApplicationContext(), "LockAdmin error", Toast.LENGTH_SHORT).show();
                sAdmin.setChecked(false);
            }
        }
    }

    public boolean isAdmin()
    {
        if( mDevicePolicyManager != null && mDevicePolicyManager.isAdminActive(mAdminComponent))
        {
            return true;
        }
        return false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    private boolean isPermissionGranted(String permission) {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
       if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Write permission granted",Toast.LENGTH_SHORT).show();
                bStorage.setText("Storage permission granted");
                bStorage.setEnabled(false);
            } else {
                Toast.makeText(this,"Write denied",Toast.LENGTH_SHORT).show();
                bStorage.setText("Grant storage permission");
                bStorage.setEnabled(true);
            }
       } else if (requestCode == REQUEST_CAMERA) {

           if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
               bCamera.setText("Camera permission granted");
               bCamera.setEnabled(false);
           } else {
               Toast.makeText(this, "Camera denied", Toast.LENGTH_SHORT).show();
               bCamera.setText("Grant camera permission");
               bCamera.setEnabled(true);
           }
       } else if (requestCode == REQUEST_BOOT) {

           if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               Toast.makeText(this, "Boot permission granted", Toast.LENGTH_SHORT).show();
               bBoot.setText("Boot receiving permission granted");
               bBoot.setEnabled(false);
           } else {
               Toast.makeText(this, "Boot denied", Toast.LENGTH_SHORT).show();
               bBoot.setText("Grant boot receiving permission");
               bBoot.setEnabled(true);
           }
       }
       else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       }
    }

}
