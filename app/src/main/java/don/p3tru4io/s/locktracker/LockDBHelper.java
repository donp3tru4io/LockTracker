package don.p3tru4io.s.locktracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LockDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "LOCKTABLE";
    public static final String EVENT = "EVENT";
    public static final String DATE = "DATE";
    public static final String _id = "id";

    public LockDBHelper(Context context) {
        super(context, "lockdb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                _id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVENT + " TEXT, " +
                DATE + " TEXT);"
        );
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
