package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 07.03.2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ObjektMemoDataSource {

    private static final String LOG_TAG = ObjektMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ObjektMemoDbHelper dbHelper;


    public ObjektMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ObjektMemoDbHelper(context);
    }
}
