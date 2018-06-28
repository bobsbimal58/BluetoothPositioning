package com.example.rohan.beaconmanual2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity_Beacon extends AppCompatActivity {

    Button btnScan, btnLoc, btnAcc;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_beacon);
         dbHelper = new DatabaseHelper(this);
        btnScan = (Button) findViewById(R.id.button1);
        if (btnScan != null) {
            btnScan.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startBLEScan();
                }
            });
        }
        btnLoc = (Button) findViewById(R.id.localize);
        if (btnLoc != null) {
            btnLoc.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    localize();
                }
            });
        }





    }



    private void localize() {
        Intent intent = new Intent(MainActivity_Beacon.this, FinalLocalization.class);
        startActivity(intent);
    }

    public void startBLEScan() {
        Intent intent = new Intent(MainActivity_Beacon.this, BLEScan.class);
        startActivity(intent);
    }
}
