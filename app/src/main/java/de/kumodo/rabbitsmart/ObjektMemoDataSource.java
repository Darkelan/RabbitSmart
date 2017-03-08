package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 07.03.2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class ObjektMemoDataSource {

    private static final String LOG_TAG = ObjektMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ObjektMemoDbHelper dbHelper;

    private String[] columns = {
            ObjektMemoDbHelper.COLUMN_ID,
            ObjektMemoDbHelper.COLUMN_PRODUCT,
            ObjektMemoDbHelper.COLUMN_QUANTITY,
            ObjektMemoDbHelper.COLUMN_CHECKED
    };

    public ObjektMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ObjektMemoDbHelper(context);
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

    public ObjektMemo createObjektMemo(String product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ObjektMemoDbHelper.COLUMN_PRODUCT, product);
        values.put(ObjektMemoDbHelper.COLUMN_QUANTITY, quantity);

        long insertId = database.insert(ObjektMemoDbHelper.TABLE_OBJEKT_LIST, null, values);

        Cursor cursor = database.query(ObjektMemoDbHelper.TABLE_OBJEKT_LIST,
                columns, ObjektMemoDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        ObjektMemo ObjektMemo = cursorToObjektMemo(cursor);
        cursor.close();

        return ObjektMemo;
    }

    public void deleteObjektMemo(ObjektMemo objektMemo) {
        long id = objektMemo.getId();

        database.delete(ObjektMemoDbHelper.TABLE_OBJEKT_LIST,
                ObjektMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + objektMemo.toString());
    }

    public ObjektMemo updateObjektMemo(long id, String newProduct, int newQuantity, boolean newChecked) {
        int intValueChecked = (newChecked)? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ObjektMemoDbHelper.COLUMN_PRODUCT, newProduct);
        values.put(ObjektMemoDbHelper.COLUMN_QUANTITY, newQuantity);
        values.put(ObjektMemoDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(ObjektMemoDbHelper.TABLE_OBJEKT_LIST,
                values,
                ObjektMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(ObjektMemoDbHelper.TABLE_OBJEKT_LIST,
                columns, ObjektMemoDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        ObjektMemo ObjektMemo = cursorToObjektMemo(cursor);
        cursor.close();

        return ObjektMemo;
    }

    private ObjektMemo cursorToObjektMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ObjektMemoDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ObjektMemoDbHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(ObjektMemoDbHelper.COLUMN_QUANTITY);
        int idChecked = cursor.getColumnIndex(ObjektMemoDbHelper.COLUMN_CHECKED);

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);
        int intValueChecked = cursor.getInt(idChecked);

        boolean isChecked = (intValueChecked != 0);

        ObjektMemo objektMemo = new ObjektMemo(product, quantity, id, isChecked);

        return objektMemo;
    }

    public List<ObjektMemo> getAllObjektMemos() {
        List<ObjektMemo> ObjektMemoList = new ArrayList<>();

        Cursor cursor = database.query(ObjektMemoDbHelper.TABLE_OBJEKT_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        ObjektMemo ObjektMemo;

        while(!cursor.isAfterLast()) {
            ObjektMemo = cursorToObjektMemo(cursor);
            ObjektMemoList.add(ObjektMemo);
            Log.d(LOG_TAG, "ID: " + ObjektMemo.getId() + ", Inhalt: " + ObjektMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return ObjektMemoList;
    }
}
