package de.kumodo.rabbitsmart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by l.schmidt on 26.04.2017.
 */

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        activateKontaktButton();
        activateEntwicklungButton();
        activateImpressumButton();
    }

    private void activateKontaktButton() {
        Button buttonKontakt = (Button) findViewById(R.id.button_kontakt);
        buttonKontakt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertKontakt = new AlertDialog.Builder(AboutActivity.this);

                // set title
                alertKontakt.setTitle("Kontaktinformationen");

                // set dialog message
                alertKontakt
                        .setMessage("Click yes to exit!")
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                AboutActivity.this.finish();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertKontakt.create();

                // show it
                alertDialog.show();
            }
        });
    }

    private void activateEntwicklungButton() {
        Button buttonEntwicklung = (Button) findViewById(R.id.button_entwicklung);
        buttonEntwicklung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertEntwicklung = new AlertDialog.Builder(AboutActivity.this);

                // set title
                alertEntwicklung.setTitle("Entwicklerinformationen");

                // set dialog message
                alertEntwicklung
                        .setMessage("Click yes to exit!")
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                AboutActivity.this.finish();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertEntwicklung.create();

                // show it
                alertDialog.show();
            }
        });
    }

    private void activateImpressumButton() {
        Button buttonImpressum = (Button) findViewById(R.id.button_impressum);
        buttonImpressum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertImpressum = new AlertDialog.Builder(AboutActivity.this);

                // set title
                alertImpressum.setTitle("Impressum und AGB");

                // set dialog message
                alertImpressum
                        .setMessage("Click yes to exit!")
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                AboutActivity.this.finish();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertImpressum.create();

                // show it
                alertDialog.show();
            }
        });
    }
}
