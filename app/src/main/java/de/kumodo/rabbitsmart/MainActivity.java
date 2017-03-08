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
import java.util.List;

import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.widget.AbsListView;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ObjektMemoDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        dataSource = new ObjektMemoDataSource(this);

        activateAddButton();
        initializeContextualActionBar();
    }

    private void showAllListEntries() {
        List<ObjektMemo> shoppingMemoList = dataSource.getAllObjektMemos();

        ArrayAdapter<ObjektMemo> shoppingMemoArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                shoppingMemoList);

        ListView shoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemosListView.setAdapter(shoppingMemoArrayAdapter);
    }

    private void activateAddButton() {
        Button buttonAddProduct = (Button) findViewById(R.id.button_add_product);
        final EditText editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editTextProduct = (EditText) findViewById(R.id.editText_product);

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

        final ListView shoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        shoppingMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.cab_delete:
                        SparseBooleanArray touchedShoppingMemosPositions = shoppingMemosListView.getCheckedItemPositions();
                        for (int i=0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if(isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                ObjektMemo shoppingMemo = (ObjektMemo) shoppingMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());
                                dataSource.deleteShoppingMemo(shoppingMemo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

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