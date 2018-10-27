package don.p3tru4io.s.locktracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    LockDBHelper DBHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        loadList();
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

        ArrayList<Map<String,String>> data = new ArrayList<Map<String,String>>();

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


        listView = findViewById(R.id.listView);

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.row,
                new String[] {"event", "date"},
                new int[] {R.id.tEvent,
                        R.id.tDate});

        listView.setAdapter(adapter);
    }

}
