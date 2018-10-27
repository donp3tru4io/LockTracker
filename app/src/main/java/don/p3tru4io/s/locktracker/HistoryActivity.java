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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
                ad.setTitle("Clear history?");  // заголовок
                ad.setMessage("Are you sure?"); // сообщение
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        if (isDeviceSecure()) {
                            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                            Intent i = keyguardManager.createConfirmDeviceCredentialIntent("Please, authenticate",
                                    "Enter unlock code");
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
                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                    Toast.makeText(getApplicationContext(), "Not authenticated", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        return keyguardManager.isKeyguardSecure();
    }

    private String checkPhoto(String date)
    {
        return "";
    }
}
