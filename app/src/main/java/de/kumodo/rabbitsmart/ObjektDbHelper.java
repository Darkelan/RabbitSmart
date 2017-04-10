package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 07.03.2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ObjektDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = ObjektDbHelper.class.getSimpleName();

    //DB-Eigenschaften
    public static final String DB_NAME = "objekt_list.db";
    public static final int DB_VERSION = 7;

    //Tabelleneigenschaften
    public static final String TABLE_OBJEKT_LIST = "objekt_list";

    /*Definition der Spalten. Die ID-Spalte wird als Primärschlüssel genutzt werden und trägt den Namen _id.
    Es ist sehr ratsam diese Bezeichnung zu wählen, da einige Klassen aus der Android-API, bspw. die Klasse SimpleCursorAdapter, diese Bezeichnung zwingend erwarten.*/
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "product";
    public static final String COLUMN_SN = "sn";
    public static final String COLUMN_KOSTEN = "kosten";
    public static final String COLUMN_AN_DATUM = "an_datum";
    public static final String COLUMN_ANWENDER = "anwender";
    public static final String COLUMN_NUMBER = "quantity";
    public static final String COLUMN_CHECKED = "checked";

    //Definition des SQL-Strings, mit dem die Tabelle in der SQLite Datenbank erstellt wird
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_OBJEKT_LIST +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_NUMBER + " INTEGER NOT NULL, " +
                    COLUMN_SN + " TEXT, " +
                    COLUMN_KOSTEN + " TEXT, " +
                    COLUMN_AN_DATUM + " TEXT, " +
                    COLUMN_ANWENDER + " TEXT, " +
                    COLUMN_CHECKED + " BOOLEAN NOT NULL DEFAULT 0);";

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_OBJEKT_LIST;

    /*Durch den Aufruf des Super-Konstruktors wird eine Datenbank erzeugt, die ihre Daten in der
    Datei “shopping_list.db” speichert und die Versionsnummer X trägt. Die Versionsnummer ist für spätere Upgrades
    der SQLite Datenbank wichtig, wenn bspw. eine weitere Spalte der Tabelle hinzugefügt werden soll.*/
    public ObjektDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // Die onCreate-Methode wird nur aufgerufen, falls die Datenbank noch nicht existiert
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    // Die onUpgrade-Methode wird aufgerufen, sobald die neue Versionsnummer höher
    // als die alte Versionsnummer ist und somit ein Upgrade notwendig wird
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Die Tabelle mit Versionsnummer " + oldVersion + " wird entfernt.");
        db.execSQL(SQL_DROP);
        onCreate(db);
    }
}
