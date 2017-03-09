package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 06.03.2017.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.widget.AbsListView;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ObjektMemoDataSource dataSource;
    private ListView mObjektMemosListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        dataSource = new ObjektMemoDataSource(this);

        initializeObjektMemosListView();
        activateAddButton();
        initializeContextualActionBar();
    }

    private void showAllListEntries() {
        List<ObjektMemo> ObjektMemoList = dataSource.getAllObjektMemos();

        ArrayAdapter<ObjektMemo> adapter = (ArrayAdapter<ObjektMemo>) mObjektMemosListView.getAdapter();

        adapter.clear();
        adapter.addAll(ObjektMemoList);
        adapter.notifyDataSetChanged();
    }

    private void activateAddButton() {
        Button buttonAddProduct = (Button) findViewById(R.id.button_add_objekt);
        final EditText editTextQuantity = (EditText) findViewById(R.id.editText_number);
        final EditText editTextProduct = (EditText) findViewById(R.id.editText_objekt);

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantityString = editTextQuantity.getText().toString();
                String product = editTextProduct.getText().toString();

                if (TextUtils.isEmpty(quantityString)) {
                    editTextQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (TextUtils.isEmpty(product)) {
                    editTextProduct.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                int quantity = Integer.parseInt(quantityString);
                editTextQuantity.setText("");
                editTextProduct.setText("");

                dataSource.createObjektMemo(product, quantity);

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

        final ListView ObjektMemosListView = (ListView) findViewById(R.id.listview_objekt_memos);
        ObjektMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        ObjektMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

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
                SparseBooleanArray touchedShoppingMemosPositions = ObjektMemosListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                ObjektMemo shoppingMemo = (ObjektMemo) ObjektMemosListView.getItemAtPosition(postitionInListView);
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
                                ObjektMemo shoppingMemo = (ObjektMemo) ObjektMemosListView.getItemAtPosition(postitionInListView);
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

    private AlertDialog createEditObjektMemoDialog(final ObjektMemo ObjektMemo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_objekt_memo, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(ObjektMemo.getQuantity()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(ObjektMemo.getProduct());

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
                        ObjektMemo updatedObjektMemo = dataSource.updateObjektMemo(ObjektMemo.getId(), product, quantity, ObjektMemo.isChecked());

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + ObjektMemo.getId() + " Inhalt: " + ObjektMemo.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedObjektMemo.getId() + " Inhalt: " + updatedObjektMemo.toString());

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

    private void initializeObjektMemosListView() {
        List<ObjektMemo> emptyListForInitialization = new ArrayList<>();

        mObjektMemosListView = (ListView) findViewById(R.id.listview_objekt_memos);

        // Erstellen des ArrayAdapters für unseren ListView
        ArrayAdapter<ObjektMemo> shoppingMemoArrayAdapter = new ArrayAdapter<ObjektMemo> (
                this,
                android.R.layout.simple_list_item_multiple_choice,
                emptyListForInitialization) {

            // Wird immer dann aufgerufen, wenn der übergeordnete ListView die Zeile neu zeichnen muss
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view =  super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                ObjektMemo memo = (ObjektMemo) mObjektMemosListView.getItemAtPosition(position);

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

        mObjektMemosListView.setAdapter(shoppingMemoArrayAdapter);

        mObjektMemosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ObjektMemo memo = (ObjektMemo) adapterView.getItemAtPosition(position);

                // Hier den checked-Wert des Memo-Objekts umkehren, bspw. von true auf false
                // Dann ListView neu zeichnen mit showAllListEntries()
                ObjektMemo updatedShoppingMemo = dataSource.updateObjektMemo(memo.getId(), memo.getProduct(), memo.getQuantity(), (!memo.isChecked()));
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