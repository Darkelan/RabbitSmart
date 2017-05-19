package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 06.03.2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ObjektDataSource dataSource;
    private ListView mObjektInventurListView;
    private ImageButton scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        dataSource = new ObjektDataSource(this);

        initializeObjektInventurListView();
        activateAddButton();
        initializeContextualActionBar();
        scanBtn = (ImageButton)findViewById(R.id.button_scan);

        scanBtn.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v){
                if(v.getId()==R.id.button_scan){
                    IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                    scanIntegrator.initiateScan();
                }
            }
        });
    }

    private void showAllListEntries() {
        List<Objekte> objekteList = dataSource.getAllObjekt();

        ArrayAdapter<Objekte> adapter = (ArrayAdapter<Objekte>) mObjektInventurListView.getAdapter();

        adapter.clear();
        adapter.addAll(objekteList);
        adapter.notifyDataSetChanged();
    }

    /*Mit den folgenden Anweisungen fragen wir die Referenzen zu den Widget-Objekten an.
    Diese sind der Add-Button und die beiden EditText-Felder.*/

   private void activateAddButton() {
        ImageButton buttonAddProduct = (ImageButton) findViewById(R.id.button_add_objekt);

        final EditText editTextNumber = (EditText) findViewById(R.id.editText_number);
        final EditText editTextObjekt = (EditText) findViewById(R.id.editText_objekt);

        /*Den OnClickListener registrieren wir mit den Zeilen 87 bis 120 für das Button-Objekt.
        Dazu verwenden wir die Methode setOnClickListener(), die wir auf dem Button-Objekt aufrufen
        und ihr ein OnClickListener-Objekt übergeben.

        Das OnClickListener-Objekt ist nichts Besonderes. Es besitzt als Elternklasse die Object-Klasse
        und muss die Interface-Methode onClick() implementieren. Die onClick() Methode wird über einen
        Callback aufgerufen, der für das View-Objekt, unseren Button, mit der Methode setOnClickListener()
        registriert wurde.*/

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override

            /* Wir erzeugen das OnClickListener-Objekt mit dem new Schlüsselwort und definieren es anschließend
            mit Hilfe einer anonymen Klasse, die nur eine einzige Methode besitzt, die onClick() Methode.
            In der onClick() Methode lesen wir die beiden Textfelder aus.*/

            public void onClick(View v) {

                String numberString = editTextNumber.getText().toString();
                String objekt = editTextObjekt.getText().toString();

                /*Mit den if-Anweisungen prüfen wir, ob in dem jeweiligen Textfeld etwas
                eingetragen wurde. Falls ein Feld leer ist, geben wir über die Methode setError() eine Fehlermeldung
                aus und beenden die onClick() Methode.*/

                if (TextUtils.isEmpty(numberString)) {
                    editTextNumber.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (TextUtils.isEmpty(objekt)) {
                    editTextObjekt.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                /*Den Wert des EditText-Feldes editTextQuantity wandeln wir in einen Integer-Wert um,
                da die Werte der Textfelder nur als String-Objekte vorliegen. Mit den beiden Anweisungen in den
                Zeilen 119 und 120 setzen wir den Wert der Textfelder zurück und löschen damit die eingetragenen Werte.*/

                int id_objekt = Integer.parseInt(numberString);
                editTextNumber.setText("");
                editTextObjekt.setText("");
                String sn = "Seriennummer";
                String an_datum = "Anschaffungsdatum ";
                String kosten = "Anschaffungskosten";
                String anwender = "Anwender";

                /*Anschließend speichern wir die ausgelesenen Werte in die SQLite Datenbank.
                Die Arbeit überlassen wir unserer Datenquelle dataSource. Wir müssen ihrer Methode createShoppingMemo()
                nur die ausgelesenen Werte übergeben, den Rest übernimmt sie für uns.*/

                dataSource.createObjekt(objekt, id_objekt, sn, an_datum, kosten, anwender);

                /* Mit den folgenden Anweisungen lassen wir das Eingabefeld verschwinden, so dass unsere Einkaufsliste komplett zu sehen ist.
                Anschließend geben wir alle Einträge der SQLite Datenbank mit Hilfe des ListViews auf dem Display aus.*/

                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                showAllListEntries();
            }
        });
    }

    private void initializeContextualActionBar() {

        final ListView ObjektInventurListView = (ListView) findViewById(R.id.listview_objekt_inventur);
        ObjektInventurListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        ObjektInventurListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            int selCount = 0;

            // In dieser Callback-Methode zählen wir die ausgewählen Listeneinträge mit
            // und fordern ein Aktualisieren der Contextual Action Bar mit invalidate() an
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selCount++;
                } else {
                    selCount--;
                }
                String cabTitle = selCount + " " + getString(R.string.cab_checked_string);
                mode.setTitle(cabTitle);
                mode.invalidate();
            }

            // In dieser Callback-Methode legen wir die CAB-Menüeinträge an
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
            // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }

                return true;
            }

            // In dieser Callback-Methode reagieren wir auf Action Item-Klicks
            // Je nachdem ob das Löschen- oder Ändern-Symbol angeklickt wurde
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedObjektPositions = ObjektInventurListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedObjektPositions.size(); i++) {
                            boolean isChecked = touchedObjektPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedObjektPositions.keyAt(i);
                                Objekte objekt = (Objekte) ObjektInventurListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + objekt.toString());
                                dataSource.deleteObjekt(objekt);

                                Context context = getApplicationContext();
                                CharSequence text = objekt.getName()+ " gelöscht.";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }
                        showAllListEntries();
                        mode.finish();

                        break;

                    case R.id.cab_change:
                        Log.d(LOG_TAG, "Objekt ändern");
                        for (int i = 0; i < touchedObjektPositions.size(); i++) {
                            boolean isChecked = touchedObjektPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedObjektPositions.keyAt(i);
                                Objekte ObjektDetail = (Objekte) ObjektInventurListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + ObjektDetail.toString());

                                AlertDialog editObjektDialog = EditObjektDialog(ObjektDetail);
                                editObjektDialog.show();
                            }
                        }

                        mode.finish();
                        break;

                    case R.id.cab_copy:
                        Log.d(LOG_TAG, "Objekt ändern");
                        for (int i = 0; i < touchedObjektPositions.size(); i++) {
                            boolean isChecked = touchedObjektPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedObjektPositions.keyAt(i);
                                Objekte ObjektDetail = (Objekte) ObjektInventurListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + ObjektDetail.toString());

                                AlertDialog copyObjektDialog = CopyObjektDialog(ObjektDetail);
                                copyObjektDialog.show();
                            }
                        }

                        mode.finish();
                        break;

                    default:
                        returnValue = false;
                        break;
                }
                return returnValue;
            }

            // In dieser Callback-Methode reagieren wir auf das Schließen der CAB
            // Wir setzen den Zähler auf 0 zurück
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;
            }
        });
    }

    private AlertDialog EditObjektDialog(final Objekte Objekte) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_objekt, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(Objekte.getNumber()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(Objekte.getName());

        final EditText editTextNewSN = (EditText) dialogsView.findViewById((R.id.editText_new_serial));
        editTextNewSN.setText((Objekte.getSN()));

        final EditText editTextNewAn_Datum = (EditText) dialogsView.findViewById((R.id.editText_new_an_datum));
        editTextNewAn_Datum.setText((Objekte.getAn_datum()));

        final EditText editTextNewKosten = (EditText) dialogsView.findViewById((R.id.editText_new_kosten));
        editTextNewKosten.setText((Objekte.getKosten()));

        final EditText editTextNewAnwender = (EditText) dialogsView.findViewById((R.id.editText_new_anwender));
        editTextNewAnwender.setText((Objekte.getAnwender()));

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_edit_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String quantityString = editTextNewQuantity.getText().toString();
                        String product = editTextNewProduct.getText().toString();

                        if ((TextUtils.isEmpty(quantityString)) || (TextUtils.isEmpty(product))) {
                            Log.d(LOG_TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            return;
                        }

                        int quantity = Integer.parseInt(quantityString);

                        String sn = editTextNewSN.getText().toString();
                        String an_datum = editTextNewAn_Datum.getText().toString();
                        String kosten = editTextNewKosten.getText().toString();
                        String anwender = editTextNewAnwender.getText().toString();

                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        Objekte updatedObjekte = dataSource.updateObjekt(Objekte.getId(), product, quantity, Objekte.isChecked(),sn, an_datum,kosten, anwender);

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + Objekte.getId() + " Inhalt: " + Objekte.getName() + " Seriennummer: " + Objekte.getSN() + " Anschaffungsdatum: " + Objekte.getAn_datum()+ " Kosten: " + Objekte.getKosten()+ " Anwender: " + Objekte.getAnwender());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedObjekte.getId() + " Inhalt: " + updatedObjekte.getName() + " Seriennummer: " + updatedObjekte.getSN()+ " Anschaffungsdatum: " + updatedObjekte.getAn_datum()+ " Kosten: " + updatedObjekte.getKosten()+ " Anwender: " + updatedObjekte.getAnwender());

                        showAllListEntries();
                        dialog.dismiss();

                        Context context = getApplicationContext();
                        CharSequence text = updatedObjekte.getName() + " aktualisiert.";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                })
                .setNegativeButton(R.string.dialog_button_edit_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private AlertDialog CopyObjektDialog(final Objekte Objekte) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_copy_objekt, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(Objekte.getNumber()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(Objekte.getName());

        final EditText editTextNewSN = (EditText) dialogsView.findViewById((R.id.editText_new_serial));
        editTextNewSN.setText((Objekte.getSN()));

        final EditText editTextNewAn_Datum = (EditText) dialogsView.findViewById((R.id.editText_new_an_datum));
        editTextNewAn_Datum.setText((Objekte.getAn_datum()));

        final EditText editTextNewKosten = (EditText) dialogsView.findViewById((R.id.editText_new_kosten));
        editTextNewKosten.setText((Objekte.getKosten()));

        final EditText editTextNewAnwender = (EditText) dialogsView.findViewById((R.id.editText_new_anwender));
        editTextNewAnwender.setText((Objekte.getAnwender()));

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_copy_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String quantityString = editTextNewQuantity.getText().toString();
                        String product = editTextNewProduct.getText().toString();

                        if ((TextUtils.isEmpty(quantityString)) || (TextUtils.isEmpty(product))) {
                            Log.d(LOG_TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            return;
                        }

                        int quantity = Integer.parseInt(quantityString);

                        String sn = editTextNewSN.getText().toString();
                        String an_datum = editTextNewAn_Datum.getText().toString();
                        String kosten = editTextNewKosten.getText().toString();
                        String anwender = editTextNewAnwender.getText().toString();

                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        Objekte copiedObjekte = dataSource.createObjekt(product, quantity, sn, an_datum,kosten, anwender);

                        Log.d(LOG_TAG, "Original-Eintrag - ID: " + Objekte.getId() + " Inhalt: " + Objekte.getName() + " Seriennummer: " + Objekte.getSN() + " Anschaffungsdatum: " + Objekte.getAn_datum()+ " Kosten: " + Objekte.getKosten()+ " Anwender: " + Objekte.getAnwender());
                        Log.d(LOG_TAG, "Kopierter Eintrag - ID: " + copiedObjekte.getId() + " Inhalt: " + copiedObjekte.getName() + " Seriennummer: " + copiedObjekte.getSN()+ " Anschaffungsdatum: " + copiedObjekte.getAn_datum()+ " Kosten: " + copiedObjekte.getKosten()+ " Anwender: " + copiedObjekte.getAnwender());

                        showAllListEntries();
                        dialog.dismiss();

                        Context context = getApplicationContext();
                        CharSequence text = Objekte.getName()+ " wurde als neues Objekt " + copiedObjekte.getName() + " kopiert.";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                })
                .setNegativeButton(R.string.dialog_button_copy_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private void initializeObjektInventurListView() {
        List<Objekte> emptyListForInitialization = new ArrayList<>();

        mObjektInventurListView = (ListView) findViewById(R.id.listview_objekt_inventur);

        // Erstellen des ArrayAdapters für unseren ListView
        ArrayAdapter<Objekte> ObjektArrayAdapter = new ArrayAdapter<Objekte>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                emptyListForInitialization) {

            // Wird immer dann aufgerufen, wenn der übergeordnete ListView die Zeile neu zeichnen muss
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view =  super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                Objekte objekt = (Objekte) mObjektInventurListView.getItemAtPosition(position);

                //Hier prüfen, ob Eintrag abgehakt ist. Falls ja, Text durchstreichen
                if (objekt.isChecked()) {
                    // Toast.makeText(getApplicationContext(), "Objekt " + objekt.getNumber() +" deaktiviert", Toast.LENGTH_SHORT).show();
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.rgb(255,0,0));
                }
                else {
                    textView.setPaintFlags( textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }
                return view;
            }
        };

        mObjektInventurListView.setAdapter(ObjektArrayAdapter);

        mObjektInventurListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Objekte objekte = (Objekte) adapterView.getItemAtPosition(position);

                // Hier den checked-Wert des Memo-Objekts umkehren, bspw. von true auf false
                // Dann ListView neu zeichnen mit showAllListEntries()
                Objekte updatedObjektMemo = dataSource.updateObjekt(objekte.getId(), objekte.getName(), objekte.getNumber(), (!objekte.isChecked()), objekte.getSN(), objekte. getAn_datum(), objekte.getKosten(), objekte.getAnwender());
                Log.d(LOG_TAG, "Checked-Status von Eintrag: " + updatedObjektMemo.getName().toString() + " ist: " + updatedObjektMemo.isChecked());

                /* Intent erzeugen und Starten der ObjektDetailActivity mit explizitem Intent
                Intent objektdetailIntent = new Intent(getApplicationContext(), ObjektDetailActivity.class);
                objektdetailIntent.putExtra(Intent.EXTRA_TEXT, updatedObjektMemo.toString());
                startActivity(objektdetailIntent);
                */
                // Toast.makeText(getApplicationContext(), objekte.getId() + " -> " +  objekte.getNumber() + " -> " + objekte.getName() + " -> gedrückt", Toast.LENGTH_SHORT).show();
                showAllListEntries();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Toast.makeText(getApplicationContext(), "Einstellungen gedrückt!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, EinstellungenActivity.class));
            return true;
        }

        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(), "Über gedrückt!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }
}