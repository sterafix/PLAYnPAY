package com.example.jonas.facharbeit;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VorbereitungActivity extends AppCompatActivity {

    //Oberflächenelemente
    private Button m_buttonVorbereitung = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vorbereitung);

        m_buttonVorbereitung = (Button) findViewById(R.id.buttonVorbereitung);

        if (MainActivity.m_iBank == 0) {
            m_buttonVorbereitung.setText("Sichtbar machen");
        } else {
            m_buttonVorbereitung.setText("Spieler suchen");
        }
    }

    public void vorbereitung (View view) {
        if (MainActivity.m_iBank == 0) {
            Intent spieler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivity(spieler);
        } else {
            Intent bank = new Intent(VorbereitungActivity.this, SpielerSuchenActivity.class);
            startActivity(bank);
        }
    }
}
