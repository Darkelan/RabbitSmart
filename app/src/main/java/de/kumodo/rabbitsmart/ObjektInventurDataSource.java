package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 07.03.2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ObjektInventurDataSource {

    private static final String LOG_TAG = ObjektInventurDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ObjektInventurDbHelper dbHelper;

    private String[] columns = {
            ObjektInventurDbHelper.COLUMN_ID,
            ObjektInventurDbHelper.COLUMN_NAME,
            ObjektInventurDbHelper.COLUMN_NUMBER,
            ObjektInventurDbHelper.COLUMN_CHECKED
            //ObjektInventurDbHelper.COLUMN_SN,
            //ObjektInventurDbHelper.COLUMN_AN_DATUM,
            //ObjektInventurDbHelper.COLUMN_KOSTEN,
            //ObjektInventurDbHelper.COLUMN_ANWENDER
    };

    public ObjektInventurDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ObjektInventurDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public Objekte createObjektMemo(String product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ObjektInventurDbHelper.COLUMN_NAME, product);
        values.put(ObjektInventurDbHelper.COLUMN_NUMBER, quantity);

        long insertId = database.insert(ObjektInventurDbHelper.TABLE_OBJEKT_LIST, null, values);

        Cursor cursor = database.query(ObjektInventurDbHelper.TABLE_OBJEKT_LIST,
                columns, ObjektInventurDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Objekte Objekte = cursorToObjektMemo(cursor);
        cursor.close();

        return Objekte;
    }

    public void deleteObjektMemo(Objekte objekte) {
        long id = objekte.getId();

        database.delete(ObjektInventurDbHelper.TABLE_OBJEKT_LIST,
                ObjektInventurDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + objekte.toString());
    }

    public Objekte updateObjektMemo(long id, String newProduct, int newQuantity, boolean newChecked) {
        int intValueChecked = (newChecked) ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ObjektInventurDbHelper.COLUMN_NAME, newProduct);
        values.put(ObjektInventurDbHelper.COLUMN_NUMBER, newQuantity);
        values.put(ObjektInventurDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(ObjektInventurDbHelper.TABLE_OBJEKT_LIST,
                values,
                ObjektInventurDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(ObjektInventurDbHelper.TABLE_OBJEKT_LIST,
                columns, ObjektInventurDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        Objekte Objekte = cursorToObjektMemo(cursor);
        cursor.close();

        return Objekte;
    }

    private Objekte cursorToObjektMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ObjektInventurDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ObjektInventurDbHelper.COLUMN_NAME);
        int idQuantity = cursor.getColumnIndex(ObjektInventurDbHelper.COLUMN_NUMBER);
        int idChecked = cursor.getColumnIndex(ObjektInventurDbHelper.COLUMN_CHECKED);

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);
        int intValueChecked = cursor.getInt(idChecked);

        boolean isChecked = (intValueChecked != 0);

        Objekte objekte = new Objekte(product, quantity, id, isChecked);

        return objekte;
    }

    public List<Objekte> getAllObjektMemos() {
        List<Objekte> objekteList = new ArrayList<>();

        Cursor cursor = database.query(ObjektInventurDbHelper.TABLE_OBJEKT_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        Objekte Objekte;

        while (!cursor.isAfterLast()) {
            Objekte = cursorToObjektMemo(cursor);
            objekteList.add(Objekte);
            Log.d(LOG_TAG, "ID: " + Objekte.getId() + ", Inhalt: " + Objekte.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return objekteList;
    }
}
