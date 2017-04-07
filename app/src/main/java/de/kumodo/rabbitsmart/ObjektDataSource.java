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

    //Den Spalten-Array werden wir später für Suchanfragen verwenden.
    //Er enthält die Namen der Spalten unserer Tabelle. Die Namen geben wir nicht selbst ein,
    //sondern nutzen dafür die String-Konstanten der Hilfsklasse DbHelper.

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
    /*Für das Öffnen und Schließen der SQLite Datenbankverbindung bedienen wir uns den
    beiden Methoden getWritableDatabase() und close() der Klasse SQLiteOpenHelper,
    die wir über das DbHelper-Objekt aufrufen.
    Mit dem Aufruf in Zeile 49 stellen wir die Verbindung zu unserer SQLite Datenbank her.
    Beim ersten Start unserer App, wenn also noch keine Datenbankdatei vorhanden ist,
    wird durch den Aufruf auch der Erstellungsprozess der Datenbank gestartet.
    Dabei wird die onCreate() Methode unserer ShoppingMemoDbHelper-Klasse einmalig ausgeführt
    und dadurch auch die Tabelle mit db.execSQL(SQL_CREATE); erstellt.
    In Zeile 56 schließen wir die Verbindung zur Datenbank wieder.*/

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    /*Mit der ersten Methode createShoppingMemo() können wir Datensätze in die Tabelle unserer SQLite Datenbank einfügen.
    In der Methode erzeugen wir in den Zeilen 77 bis 83 ein ContentValues-Objekt. Anschließend tragen wir diese Werte
    mit Hilfe des ContentValues-Objekts in die Tabelle ein. Dazu verwenden wir den insert-Befehl in Zeile 84,
    den wir auf dem SQLiteDatabase-Objekt ausführen. Als Argumente übergeben wir den Namen der Tabelle,
    null für den ColumnHack und das vorbereitete ContentValues-Objekt.
    War das Einfügen erfolgreich, dann erhalten wir die ID des erstellten Datensatzes zurück.

    Nun lesen wir die eingetragenen Werte zur Kontrolle mit der Anweisung in Zeile 89 bis 91 direkt wieder aus.
    Als Argumente übergeben wir den Namen der Tabelle, den Spalten-Array (die Suchanfrage
    soll die Werte für alle Spalten zurückliefern) und den Such-String mit dem wir nach dem eben eingefügten Datensatz suchen.
    Die restlichen Argumente sind alle null.

    Als Ergebnis auf die Suchanfrage erhalten wir ein Cursor-Objekt zurück, das die gefundenen Datensätze enthält.
    In unserem Fall besteht es aus genau einem Datensatz, dem eben Eingefügten.

    Mit der Anweisung in Zeile 97 bewegen wir den Cursor an die Position seines ersten Datensatzes.
    Anschließend rufen wir die cursorToObjekt() Methode auf und wandeln dadurch den Datensatz des
    Cursor-Objekts in ein Objekt-Objekt um.

    Das so erzeugte Objekt-Objekt geben wir an die aufrufende Methode zurück. Doch vorher schließen wir noch das Cursor-Objekt (Zeile 101).*/

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

    /* Die delete() Methode wird auf dem SQLiteDatabase-Objekt aufgerufen.
    Als Argumente wird ihr der Name unserer Tabelle und der Such-String mit der ID des zu löschenden
    Datensatzes übergeben. Das dritte Argument wird in diesem Fall nicht benötigt und ist null.*/

    public void deleteObjekt(Objekte objekte) {
        long id = objekte.getId();

        /*Die ID des zu löschenden Datensatzes lesen wir aus dem übergebenen Objekt-Objekt aus.
        Anschließend führen wir die Lösch-Operation auf dem SQLiteDatabase-Objekt aus.

        Zu Testzwecken geben wir die ID und den Inhalt des gelöschten Datensatzes in einer Log-Meldung aus.*/

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

    /*In den Zeilen 152 bis 160 lesen wir die Indizes unserer Tabellenspalten aus.
    Anschließend lassen wir uns in den Zeilen 163 bis 171 die Inhalte der Suchanfrage mit den get-Methoden ausgeben.
    Dazu nutzen wir die vorher erhaltenen Spaltenindizes.

    Das Cursor-Objekt zeigt dabei auf einen Datensatz der vorher ausgeführten Suchanfrage.
    Über die Spaltenindizes kann auf die Zellen des Datensatzes zugegriffen und mit der entsprechenden
    get-Methode deren Inhalt ausgelesen werden.

    In Zeile 176 erzeugen wir schließlich mit den ausgelesenen Daten das Objekt-Objekt. Dieses können
    wir nun in unserer Anwendung weiterverwenden, bspw. in einem ListView anzeigen lassen,
    und geben es daher als Rückgabewert zurück.

    Die beschriebene Hilfsmethode werden wir in unserer Android SQLite App öfter verwenden.
    Mit ihrer Hilfe könne sehr einfach und bequem Datensätze in Objekt-Objekte umgewandelt werden.*/

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

    /*Mit der Methode getAllObjekt() lesen wir alle vorhandenen Datensätze aus der Tabelle unserer SQLite Datenbank aus.
    Dazu erzeugen wir gleich zu Beginn der Methode in Zeile 182 eine Liste, die Objekt-Objekt in sich aufnehmen kann.
    Anschließend starten wir eine Suchanfrage in Zeile 183 und 184. Diesmal übergeben wir als Argumente nur den Namen
    der Tabelle und den Spalten-Array. Alle anderen Argumente sind null, d.h. auch der Such-String ist null,
    wodurch alle in der Tabelle existierenden Datensätze als Ergebnis zurückgeliefert werden.

    In den Zeilen 188 und 189 setzen wir das erhaltenen Cursor-Objekt an seine erste Position und
    deklarieren eine Variable vom Typ Objekte.

    Mit der while-Schleife in den Zeilen 192 bis 197 lesen wir alle Datensätze der Suchanfrage aus,
    wandeln sie in Objekte-Objekte um und fügen sie der Objekte-Liste hinzu. Mit der Log-Meldung
    können wir später in der Konsole überprüfen, welche Datensätze sich in der Tabelle befinden.

    In Zeile 201 schließen wir den Cursor wieder. Dies ist eine ganz wichtige Anweisung und darf nicht vergessen werden!
    Am Ende der Methode geben wir die erzeugte Objekte-Liste, die alle Datensätze der Tabelle enthält,
    an die aufrufende Methode zurück.*/

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
