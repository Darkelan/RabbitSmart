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

public class ObjektDataSource {

    private static final String LOG_TAG = ObjektDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ObjektDbHelper dbHelper;

    private String[] columns = {
            ObjektDbHelper.COLUMN_ID,
            ObjektDbHelper.COLUMN_NAME,
            ObjektDbHelper.COLUMN_NUMBER,
            ObjektDbHelper.COLUMN_CHECKED,
            ObjektDbHelper.COLUMN_SN,
            ObjektDbHelper.COLUMN_AN_DATUM,
            ObjektDbHelper.COLUMN_KOSTEN,
            ObjektDbHelper.COLUMN_ANWENDER
    };

    public ObjektDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ObjektDbHelper(context);
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

    public Objekte createObjekt(String product, int quantity, String sn, String an_datum, String kosten, String anwender) {
        ContentValues values = new ContentValues();
        values.put(ObjektDbHelper.COLUMN_NAME, product);
        values.put(ObjektDbHelper.COLUMN_NUMBER, quantity);
        values.put(ObjektDbHelper.COLUMN_SN, sn);
        values.put(ObjektDbHelper.COLUMN_AN_DATUM, an_datum);
        values.put(ObjektDbHelper.COLUMN_KOSTEN, kosten);
        values.put(ObjektDbHelper.COLUMN_ANWENDER, anwender);

        long insertId = database.insert(ObjektDbHelper.TABLE_OBJEKT_LIST, null, values);

        Cursor cursor = database.query(ObjektDbHelper.TABLE_OBJEKT_LIST,
                columns, ObjektDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Objekte Objekte = cursorToObjekt(cursor);
        cursor.close();

        return Objekte;
    }

    public void deleteObjekt(Objekte objekte) {
        long id = objekte.getId();

        database.delete(ObjektDbHelper.TABLE_OBJEKT_LIST,
                ObjektDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + objekte.toString());
    }

    public Objekte updateObjekt(long id, String newProduct, int newQuantity, boolean newChecked, String newsn, String newan_datum, String newkosten, String newanwender) {
        int intValueChecked = (newChecked) ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ObjektDbHelper.COLUMN_NAME, newProduct);
        values.put(ObjektDbHelper.COLUMN_NUMBER, newQuantity);
        values.put(ObjektDbHelper.COLUMN_CHECKED, intValueChecked);
        values.put(ObjektDbHelper.COLUMN_SN, newsn);
        values.put(ObjektDbHelper.COLUMN_AN_DATUM, newan_datum);
        values.put(ObjektDbHelper.COLUMN_KOSTEN, newkosten);
        values.put(ObjektDbHelper.COLUMN_ANWENDER, newanwender);

        database.update(ObjektDbHelper.TABLE_OBJEKT_LIST,
                values,
                ObjektDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(ObjektDbHelper.TABLE_OBJEKT_LIST,
                columns, ObjektDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        Objekte Objekte = cursorToObjekt(cursor);
        cursor.close();

        return Objekte;
    }

    private Objekte cursorToObjekt(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ObjektDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ObjektDbHelper.COLUMN_NAME);
        int idQuantity = cursor.getColumnIndex(ObjektDbHelper.COLUMN_NUMBER);
        int idChecked = cursor.getColumnIndex(ObjektDbHelper.COLUMN_CHECKED);
        int idSN = cursor.getColumnIndex(ObjektDbHelper.COLUMN_SN);
        int idAn_datum = cursor.getColumnIndex(ObjektDbHelper.COLUMN_AN_DATUM);
        int idKosten = cursor.getColumnIndex(ObjektDbHelper.COLUMN_KOSTEN);
        int idAnwender = cursor.getColumnIndex(ObjektDbHelper.COLUMN_ANWENDER);

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);
        int intValueChecked = cursor.getInt(idChecked);
        String sn = cursor.getString(idSN);
        String an_datum = cursor.getString(idAn_datum);
        String kosten = cursor.getString(idKosten);
        String anwender = cursor.getString(idAnwender);

        boolean isChecked = (intValueChecked != 0);

        Objekte objekte = new Objekte(product, quantity, id, isChecked, sn, an_datum, kosten, anwender);

        return objekte;
    }

    public List<Objekte> getAllObjekt() {
        List<Objekte> objekteList = new ArrayList<>();

        Cursor cursor = database.query(ObjektDbHelper.TABLE_OBJEKT_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        Objekte Objekte;

        while (!cursor.isAfterLast()) {
            Objekte = cursorToObjekt(cursor);
            objekteList.add(Objekte);
            Log.d(LOG_TAG, "ID: " + Objekte.getId() + ", Inhalt: " + Objekte.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return objekteList;
    }
}
