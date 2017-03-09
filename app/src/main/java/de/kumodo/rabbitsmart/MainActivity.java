package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 06.03.2017.
 */

import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ObjektInventurDataSource dataSource;
    private ListView mObjektInventurListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        dataSource = new ObjektInventurDataSource(this);

        initializeObjektInventurListView();
        activateAddButton();
        initializeContextualActionBar();
    }

    private void showAllListEntries() {
        List<Objekte> objekteList = dataSource.getAllObjektMemos();

        ArrayAdapter<Objekte> adapter = (ArrayAdapter<Objekte>) mObjektInventurListView.getAdapter();

        adapter.clear();
        adapter.addAll(objekteList);
        adapter.notifyDataSetChanged();
    }

    private void activateAddButton() {
        Button buttonAddProduct = (Button) findViewById(R.id.button_add_objekt);
        final EditText editTextNumber = (EditText) findViewById(R.id.editText_number);
        final EditText editTextObjekt = (EditText) findViewById(R.id.editText_objekt);

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numberString = editTextNumber.getText().toString();
                String objekt = editTextObjekt.getText().toString();

                if (TextUtils.isEmpty(numberString)) {
                    editTextNumber.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (TextUtils.isEmpty(objekt)) {
                    editTextObjekt.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                int quantity = Integer.parseInt(numberString);
                editTextNumber.setText("");
                editTextObjekt.setText("");

                dataSource.createObjektMemo(objekt, quantity);

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
                SparseBooleanArray touchedShoppingMemosPositions = ObjektInventurListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                Objekte shoppingMemo = (Objekte) ObjektInventurListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());
                                dataSource.deleteObjektMemo(shoppingMemo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        break;

                    case R.id.cab_change:
                        Log.d(LOG_TAG, "Eintrag ändern");
                        for (int i = 0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                Objekte shoppingMemo = (Objekte) ObjektInventurListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());

                                AlertDialog editShoppingMemoDialog = createEditObjektMemoDialog(shoppingMemo);
                                editShoppingMemoDialog.show();
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

    private AlertDialog createEditObjektMemoDialog(final Objekte Objekte) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_objekt_memo, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(Objekte.getNumber()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(Objekte.getName());

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String quantityString = editTextNewQuantity.getText().toString();
                        String product = editTextNewProduct.getText().toString();

                        if ((TextUtils.isEmpty(quantityString)) || (TextUtils.isEmpty(product))) {
                            Log.d(LOG_TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            return;
                        }

                        int quantity = Integer.parseInt(quantityString);

                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        Objekte updatedObjekte = dataSource.updateObjektMemo(Objekte.getId(), product, quantity, Objekte.isChecked());

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + Objekte.getId() + " Inhalt: " + Objekte.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedObjekte.getId() + " Inhalt: " + updatedObjekte.toString());

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
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
        ArrayAdapter<Objekte> shoppingMemoArrayAdapter = new ArrayAdapter<Objekte>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                emptyListForInitialization) {

            // Wird immer dann aufgerufen, wenn der übergeordnete ListView die Zeile neu zeichnen muss
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view =  super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                Objekte memo = (Objekte) mObjektInventurListView.getItemAtPosition(position);

                // Hier prüfen, ob Eintrag abgehakt ist. Falls ja, Text durchstreichen
                if (memo.isChecked()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.rgb(175,175,175));
                }
                else {
                    textView.setPaintFlags( textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }

                return view;
            }
        };

        mObjektInventurListView.setAdapter(shoppingMemoArrayAdapter);

        mObjektInventurListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Objekte memo = (Objekte) adapterView.getItemAtPosition(position);

                // Hier den checked-Wert des Memo-Objekts umkehren, bspw. von true auf false
                // Dann ListView neu zeichnen mit showAllListEntries()
                Objekte updatedShoppingMemo = dataSource.updateObjektMemo(memo.getId(), memo.getName(), memo.getNumber(), (!memo.isChecked()));
                Log.d(LOG_TAG, "Checked-Status von Eintrag: " + updatedShoppingMemo.toString() + " ist: " + updatedShoppingMemo.isChecked());
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