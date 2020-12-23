package com.example.jonas.facharbeit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AuswahlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswahl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void spieler (View view) {
        MainActivity.m_iBank = 0;
        Intent spieler = new Intent(AuswahlActivity.this, VorbereitungActivity.class);
        startActivity(spieler);
    }

    public void bank (View view) {
        MainActivity.m_iBank = 1;
        Intent bank = new Intent(AuswahlActivity.this, VorbereitungActivity.class);
        startActivity(bank);
    }
}
