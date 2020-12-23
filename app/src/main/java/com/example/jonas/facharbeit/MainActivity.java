package com.example.jonas.facharbeit;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Definition des Request Codes
    private final static int REQUEST_ENABLE_BT = 123;

    //Deklaration der Member
    public static int m_iBank = 0;
    public static ArrayList<BluetoothDevice> m_arMitspielerDevice = new ArrayList<>();
    public static ArrayList<String> m_arMitspielerName = new ArrayList<>();

    //Oberfächenelemente
    private Button m_buttonSichtbarMachen = null;
    private Button m_buttonSpielerSuchen = null;
    private Button m_buttonNeuesSpiel = null;
    private CheckBox m_checkBoxSpieler = null;
    private CheckBox m_checkBoxBank = null;

    //onCreate Methode (wird beim Start der Activity aufgerufen)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Layout und Code bekannt machen
        //Oberfächenelement wird über die id gefunden (id's sind in R.id. gespeichert)
        // und auf Art des Oberflächenelementes gecastet (umgewandelt)
        m_buttonSichtbarMachen = (Button) findViewById(R.id.buttonSichtbarMachen);
        m_buttonSichtbarMachen.setEnabled(false);
        m_buttonSpielerSuchen = (Button) findViewById(R.id.buttonSpielerSuchen);
        m_buttonSpielerSuchen.setEnabled(false);
        m_buttonNeuesSpiel = (Button) findViewById(R.id.buttonNeuesSpiel);
        m_buttonNeuesSpiel.setEnabled(false);
        m_checkBoxSpieler = (CheckBox) findViewById(R.id.checkBoxSpieler);
        m_checkBoxBank = (CheckBox) findViewById(R.id.checkBoxBank);



        //Prüfung, ob Bluetooth auf Gerät genutzt werden kann
        BluetoothAdapter m_oBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_oBluetoothAdapter == null) {
            //wenn nicht: Warnung
            //vorbereiten
            AlertDialog.Builder warnung = new AlertDialog.Builder(this);
            //Nachricht
            warnung.setMessage("Ohne Bluetooth funktioniert dies APP nicht!");
            //Bestätigungs-Knopf
            warnung.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //wenn der Knopf gedrückt wurde (evtl. Schließung der App)
                }
            });
            warnung.show();
        }

        //Prüfung, ob Bluetooth aktiv ist
        if (!m_oBluetoothAdapter.isEnabled()) {
            //falls nicht:
            //neuer Intent (Intent=Vorhaben): BT aktivieren
            Intent btAktivieren = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //Intent durchführen
            startActivityForResult(btAktivieren, REQUEST_ENABLE_BT);
            //Dialogfenster zur Bestätigung durch Nutzer öffnet sich automatisch
        }
    }

    //nach Durchführung des Intents bzw. Antwort des Nutzers auf das Dialogfenster
    //soll noch überprüft werden, ob die Aktivierung auch erfolgreich war
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //BT ist aktiviert
        } else {
            //BT wurde nicht aktiviert (evtl. Erneute Aufforderung/Schließung der App)
        }
    }

    //wenn Button "Start" geklickt: SpielerSuchenAktvity öffnen
    public void auswahl(View view) {
        //Intent: neue Activity (Fenster) öffnen
        //                      (momentane Activity, Klasse der zu öffnenden Activity)
        Intent start = new Intent(MainActivity.this, AuswahlActivity.class);
        startActivity(start);
    }

    //wenn Button "Sichtbar machen" geklickt: Gerät sichtbar machen
    public void sichtbarMachen(View view) {
        Intent sichtbar = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(sichtbar);
    }

    //wenn Button "Spieler suchen" geklickt: SpielerSuchenAktvity öffnen
    public void spielerSuchen(View view) {
        //Intent: neue Activity (Fenster) öffnen
        //                      (momentane Activity, Klasse der zu öffnenden Activity)
        Intent suche = new Intent(MainActivity.this, SpielerSuchenActivity.class);
        startActivity(suche);
    }

    //wenn Button "Neues Spiel" geklickt: SpielActivity öffnen
    //in content_main.xml wurde dem Button "Neues Spiel" ein onClick (listener) hinzugefügt,
    //der (im Falle eines Klicks) diese Methode aufruft
    public void neuesSpielStarten(View view) {
        //falls beide Checkboxen gecheckt
        if (m_checkBoxBank.isChecked() && m_checkBoxSpieler.isChecked()) {
            m_iBank = 2;
        }
        Intent spiel = new Intent(MainActivity.this, SpielActivity.class);
        startActivity(spiel);
    }

    //wenn Checkbox "Spieler" ausgeählt:
    //Nutzer soll nur für ihn bestimmte Knöpfe drücken können
    public void spielerAusgewaehlt(View view) {
        if (m_checkBoxSpieler.isChecked()) {
            m_buttonSichtbarMachen.setEnabled(true);
            m_buttonNeuesSpiel.setEnabled(true);
            m_iBank = 0;
        } else if (m_checkBoxBank.isChecked()) {
            m_buttonSichtbarMachen.setEnabled(false);
            m_iBank = 1;
        } else {
            m_buttonSichtbarMachen.setEnabled(false);
            m_buttonNeuesSpiel.setEnabled(false);
        }
    }

    //wenn Checkbox "Bank" ausgewählt:
    //Nutzer soll nur für ihn bestimmte Knöpfe drücken können
    public void bankAusgewaehlt(View view) {
        if (m_checkBoxBank.isChecked()) {
            m_buttonSpielerSuchen.setEnabled(true);
            m_buttonNeuesSpiel.setEnabled(true);
            m_iBank = 1;
        } else if (m_checkBoxSpieler.isChecked()){
            m_buttonSpielerSuchen.setEnabled(false);
            m_iBank = 0;
        } else {
            m_buttonSpielerSuchen.setEnabled(false);
            m_buttonNeuesSpiel.setEnabled(false);
        }

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
}
