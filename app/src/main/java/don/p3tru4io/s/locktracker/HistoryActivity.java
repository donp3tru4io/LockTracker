package don.p3tru4io.s.locktracker;

import android.app.ActionBar;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.DHGenParameterSpec;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private LockDBHelper DBHelper;
    private SQLiteDatabase db;
    private AlertDialog.Builder ad;
    public static final int REQUEST_DROP = 300;
    private static ArrayList<Map<String,String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getResources().getString(R.string.title_activity_database));
        data = new ArrayList<Map<String,String>>();
        listView = findViewById(R.id.listView);
        loadList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    void loadList()
    {
        DBHelper = new LockDBHelper(this);

        /*db = DBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHelper.EVENT,"EVENT "+i);
            values.put(DBHelper.DATE,"DATE "+i);
            db.insert(DBHelper.TABLE_NAME,null,values);
        */

        db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+DBHelper.TABLE_NAME+";",null);

        data.clear();

        if(cursor.moveToLast())
        {
            while (!cursor.isBeforeFirst()) {

                Map<String,String> row = new HashMap<String,String>(2);
                row.put("event",cursor.getString(cursor.getColumnIndex(DBHelper.EVENT)));
                row.put("date",cursor.getString(cursor.getColumnIndex(DBHelper.DATE)));
                data.add(row);
                cursor.moveToPrevious();
            }
        }
        db.close();

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.row,
                new String[] {"event", "date"},
                new int[] {R.id.tEvent,
                        R.id.tDate});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eDate = data.get(i).get("date");
                //18:43:23 29/11/2018
                //18.04.31_19.11.2018
                String fileName = eDate.replace(":",".").
                        replace("/",".").
                        replace(" ","_");
                showPhoto(fileName);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.hDrop:
                ad = new AlertDialog.Builder(this);
                ad.setTitle(getString(R.string.clear_history));  // заголовок
                ad.setMessage(getString(R.string.are_you_sure)); // сообщение
                ad.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        if (isDeviceSecure()) {
                            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getString(R.string.auth),
                                    getString(R.string.enter_code));
                            startActivityForResult(i, REQUEST_DROP);
                        }
                        else
                        {
                            DBHelper = new LockDBHelper(getApplicationContext());
                            db = DBHelper.getWritableDatabase();
                            db.execSQL("DROP "+DBHelper.TABLE_NAME+";");
                            db.close();
                            loadList();
                        }
                    }
                });
                ad.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                ad.setCancelable(true);
                ad.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_DROP:
                if (resultCode == RESULT_OK) {
                    DBHelper = new LockDBHelper(getApplicationContext());
                    db = DBHelper.getWritableDatabase();
                    db.delete(DBHelper.TABLE_NAME,null,null);
                    db.close();
                    loadList();
                } else{
                    Toast.makeText(getApplicationContext(), getString(R.string.not_authenticated), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        return keyguardManager.isKeyguardSecure();
    }

    private void showPhoto(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory() +
                "/LockTracker/"+fileName+"_pic.jpg");
        if (!file.exists()) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/jpeg");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startActivity(intent);
    }
}
