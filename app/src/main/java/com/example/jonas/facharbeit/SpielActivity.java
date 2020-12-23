package com.example.jonas.facharbeit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SpielActivity extends AppCompatActivity implements Runnable {

    //neuer Thread für das ClientSocket der Bank (da dieses den Thread blockiert)
    private class ClientThread extends Thread {

        private BluetoothSocket m_soClient = null;

        @Override
        public void run() {
            if (m_oArryAdapter.getCount() < 1)
                return;

            int iIndex = m_spinnerMitspieler.getSelectedItemPosition();
            if (iIndex < AdapterView.INVALID_POSITION)
                return;

            try {
                //device = das im Spinner ausgewählte Element
                BluetoothDevice device = MainActivity.m_arMitspielerDevice.get(iIndex);
                //mithilfe des soeben geholten devices ClientSocket erstellen
                m_soClient = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }

            try {
                //soeben erstelltes Socket mit ServerSocket (Spieler) verbinden
                m_soClient.connect();
            } catch (IOException e) {
                //keine Verbindung möglich, Socket schließen
                try {
                    if (m_soClient != null) {
                        m_soClient.close();
                    }
                } catch (IOException e1) {}
                return;
            }

            //falls  ClientSocket erfolgreich verbunden
            if (m_soClient != null) {
                try {
                    InputStream inputStream = m_soClient.getInputStream();
                    OutputStream outputStream = m_soClient.getOutputStream();

                    byte [] sendenBytes;

                    //wenn Button "Senden" gedrückt wurde: Geld an Spieler überweisen
                    if (m_iZahlung == 1) {
                        String senden = "SENDEN:" + m_editTextBetrag.getText();
                        sendenBytes = senden.getBytes("UTF-8");
                    } else {
                        //Betrag von Spieler abbuchen (da button "Abbuchen" gedrückt wurde)
                        String abbuchen = "ABBUCHEN:" + m_editTextBetrag.getText();
                        sendenBytes = abbuchen.getBytes("UTF-8");
                    }
                    //Bytes über den Output-Stream senden
                    outputStream.write(sendenBytes);

                    //Socket schließen
                    m_soClient.close();
                } catch (IOException e) {}
            }
        }
    }

    //Deklaration der Member
    private Integer m_iKontostand = 5000;
    private BluetoothServerSocket m_soServer = null;
    public static int m_iZahlung = 0;
    public static BluetoothDevice m_oSpieler = null;

    //UUID (Universally Unique Identifier) definieren
    private static final UUID MY_UUID = UUID.fromString("06a031f1-f010-1e30-8500-00803e4c68ab");

    //Oberflächenelemente
    private TextView m_textViewPruefung = null;
    private TextView m_textViewKonto = null;
    private TextView m_textViewSendenAbbuchen = null;
    private TextView m_textViewAnVon = null;
    private EditText m_editTextBetrag = null;
    private Spinner m_spinnerMitspieler = null;
    private Button m_buttonSenden = null;
    private Button m_buttonAbbuchen  = null;
    private ArrayAdapter<String> m_oArryAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Layout und Code bekannt machen
        m_textViewPruefung = (TextView) findViewById(R.id.textViewPruefung);
        m_textViewKonto = (TextView) findViewById(R.id.textViewKonto);
        m_textViewSendenAbbuchen = (TextView) findViewById(R.id.textViewSendenAbbuchen);
        m_textViewAnVon = (TextView) findViewById(R.id.textViewAnVon);
        m_editTextBetrag = (EditText) findViewById(R.id.editTextBetrag);
        m_spinnerMitspieler = (Spinner) findViewById(R.id.spinnerMitspieler);
        m_buttonSenden = (Button) findViewById(R.id.buttonSenden);
        m_buttonAbbuchen = (Button) findViewById(R.id.buttonAbbuchen);

        //textView, das auf m_iBank reagiert
        if (MainActivity.m_iBank == 0) {
            m_textViewPruefung.setText(R.string.pruefungSpieler);
        } else if (MainActivity.m_iBank == 1) {
            m_textViewPruefung.setText(R.string.pruefungBank);
        } else if (MainActivity.m_iBank == 2) {
            m_textViewPruefung.setText(R.string.pruefungSpielerUndBank);
        }


        //wenn Nutzer = Spieler: Nur Konto zeigen
        if (MainActivity.m_iBank == 0) {
            m_textViewSendenAbbuchen.setVisibility(View.INVISIBLE);
            m_textViewAnVon.setVisibility(View.INVISIBLE);
            m_editTextBetrag.setVisibility(View.INVISIBLE);
            m_spinnerMitspieler.setVisibility(View.INVISIBLE);
            m_buttonSenden.setVisibility(View.INVISIBLE);
            m_buttonAbbuchen.setVisibility(View.INVISIBLE);
        } //wenn Nutzer = Bank: alles zeigen, außer Konto
        else {
            m_textViewKonto.setVisibility(View.INVISIBLE);
            m_oArryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MainActivity.m_arMitspielerName);
            m_spinnerMitspieler.setAdapter (m_oArryAdapter);
        }

        //wenn Nutzer = Spieler: Neuen Thread starten (da die ServerSocket den Thread mit Suche blockiert)
        if (MainActivity.m_iBank == 0) {
            BluetoothAdapter oBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            try {
                // ServerSocket erstellen
                m_soServer = oBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothPP", MY_UUID);
            } catch (IOException e) {}

            //Thread starten, in dem die ServerSocket auf eingehende Verbindungen warten soll
            Thread m_thServer = new Thread(SpielActivity.this);
            m_thServer.start();
        }
    }


    @Override
    public void onDestroy ()
    {
        //wenn Activity beendet wird: serverSocket schließen, damit auch m_thServer ausläuft
        if (m_soServer != null) {
            try {
                m_soServer.close();
            } catch (Exception e) {}
        }
        super.onDestroy();
    }

    //Thread für ServerSocket eines Spielers
    @Override
    public void run(){
        BluetoothSocket socket;

        if (m_soServer == null){
            return;
        }

        while (true) {
            try {
                //wartet so lange, bis sich ein anderes Socket verbindet, oder eine IOException passiert
                socket = m_soServer.accept();
            } catch (IOException e) {
                break;
            }
            if (socket != null) {
                try {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    byte[] bEmpfangen = new byte[100];
                    int iLen = inputStream.read(bEmpfangen);
                    String strEmpfangen = new String(bEmpfangen, 0, iLen, "UTF-8");
                    try {
                        if (strEmpfangen.startsWith("SENDEN")) {
                            int iWert = Integer.parseInt(strEmpfangen.substring(7));
                            m_iKontostand += iWert;
                        } else if ((strEmpfangen.startsWith("ABBUCHEN"))) {
                            int iWert = Integer.parseInt(strEmpfangen.substring(9));
                            m_iKontostand -= iWert;
                        }
                        //folgendes im (G)UI Thread durchführen, da man aus einem Nebenthread nicht auf (G)UI zugreifen kann
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                m_textViewKonto.setText(Integer.toString(m_iKontostand));
                            }
                        });
                    } catch (Exception e) {}

                    socket.close();
                } catch (Exception e) {}
            }
        }
    }

    //wenn Button "Senden" geklickt
    public void senden(View view) {
        m_iZahlung = 1;
        ClientThread thClient = new ClientThread();
        thClient.start();
    }

    //wenn Button "Abbuchen" geklickt
    public void abbuchen(View view) {
        m_iZahlung = 0;
        ClientThread thClient = new ClientThread();
        thClient.start();
    }
}
