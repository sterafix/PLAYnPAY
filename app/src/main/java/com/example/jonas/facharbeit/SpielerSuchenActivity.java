package com.example.jonas.facharbeit;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class SpielerSuchenActivity extends AppCompatActivity{

    private MyBroadcastReceiver m_oReceiver = null;

    private class MyBroadcastReceiver extends BroadcastReceiver {
        SpielerSuchenActivity m_Activity = null;
        public void SetActivity (SpielerSuchenActivity activity)
        {
            m_Activity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //wenn Bluetoothgerät gefunden
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Gerät als device in Liste der Geräte aufnehmen
                m_arBTDevices.add(device);
                // Gerätename und Adresse in Liste der Spieler aufnehmen
                m_Activity.m_arSpieler.add(device.getName() + "\n" + device.getAddress());
                m_Activity.m_oArryAdapter.notifyDataSetChanged();
            } //wenn Suche läuft
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                m_oTextViewInfo.setText(R.string.sucheLaeuft);
            } //wenn Suche beendet
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                m_oTextViewInfo.setText(R.string.sucheBeendet);
            }
        }
    }


    //Deklaration der Member
    private ArrayList<String> m_arSpieler = null;
    private ArrayList<BluetoothDevice> m_arBTDevices = null;
    private ArrayAdapter<String> m_oArryAdapter = null;
    private TextView m_oTextViewInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieler_suchen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TextViewInfo mit Code bekannt machen
        m_oTextViewInfo = (TextView) findViewById(R.id.textViewInfo);

        // Listenelemente erzeugen
        m_arSpieler = new ArrayList<>();
        m_arBTDevices = new ArrayList<>();

        //Listen leeren
        MainActivity.m_arMitspielerDevice.clear();
        MainActivity.m_arMitspielerName.clear();

        m_oArryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, m_arSpieler);
        ListView listView = (ListView)findViewById(R.id.listViewSpieler);
        listView.setAdapter(m_oArryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog alert = new AlertDialog.Builder(SpielerSuchenActivity.this).create();
                alert.setTitle("Spieler hinzufügen");
                alert.setMessage("Soll dieser Spieler hinzugefügt werden?");
                alert.setCancelable(true);
                //"Abbrechen" Knopf
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                //"OK" Knopf
                alert.setButton(DialogInterface.BUTTON_POSITIVE,"Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_arBTDevices.get(position).createBond();
                        MainActivity.m_arMitspielerDevice.add(m_arBTDevices.get(position));
                        MainActivity.m_arMitspielerName.add(m_arBTDevices.get(position).getName());
                    }
                });

                alert.show();


            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter oBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                oBluetoothAdapter.startDiscovery();

            }
        });


        BluetoothAdapter oBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        m_oReceiver = new MyBroadcastReceiver();
        m_oReceiver.SetActivity(this);
        registerReceiver(m_oReceiver, filter);
        oBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(m_oReceiver);

        super.onDestroy();
    }
}
