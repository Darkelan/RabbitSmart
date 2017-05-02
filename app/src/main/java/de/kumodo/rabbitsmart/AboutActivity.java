package de.kumodo.rabbitsmart;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                Context context = getApplicationContext();
                CharSequence text = "Kontakt gedrückt";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    private void activateEntwicklungButton() {
        Button buttonEntwicklung = (Button) findViewById(R.id.button_entwicklung);
        buttonEntwicklung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Entwicklung gedrückt";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    private void activateImpressumButton() {
        Button buttonImpressum = (Button) findViewById(R.id.button_impressum);
        buttonImpressum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Impressum gedrückt";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }
}
