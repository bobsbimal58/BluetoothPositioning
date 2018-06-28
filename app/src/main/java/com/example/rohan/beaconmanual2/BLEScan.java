package com.example.rohan.beaconmanual2;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Rohan on 6/29/2017.
 */

public class BLEScan extends AppCompatActivity {


    Button btnStart;
    Button btnSave;
    Button btnStop;
    Button btnLoad;
    Button btnl;


    BluetoothAdapter BA;
    BeaconManager beaconManager;
    Region region;

    DatabaseHelper dbHelper;


    TextView x, y, sample, tv;

    int SampleSize;


    public static HashMap<String, List<Beacon>> BEACON_LIST = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_main);
        dbHelper = new DatabaseHelper(this);
        tv = (TextView)findViewById(R.id.textView);




        btnLoad = (Button)findViewById(R.id.load);
        if (btnLoad != null){
            btnLoad.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadData();
                }
            });
        }


        btnStart = (Button) findViewById(R.id.button1);
        if (btnStart != null)
        {
            btnStart.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startScan();
                    onResume();
                }
            });
        }

        btnSave = (Button) findViewById(R.id.button2);
        if (btnSave != null) {
            btnSave.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {

                      saveData();

                }
            });
        }

        btnStop = (Button) findViewById(R.id.button3);
        if (btnStop != null) {
            btnStop.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                     stopScan();
                }
            });
        }

        btnl = (Button) findViewById(R.id.btnLoc);
        if (btnl != null) {
            btnl.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    localization();
                    
                }

            });
        }

    }

    private void localization() {
        Intent intent = new Intent(BLEScan.this, Localization.class);
       startActivity(intent);

    }


    private void saveData() {


       // for(Map.Entry<String, HashMap<String, List<Beacon>>> entry : BEACONS_LIST.entrySet()){
        for (Map.Entry<String, List<Beacon>> entry : BEACON_LIST.entrySet()) {
            TextView m = (TextView)findViewById(R.id.editText12);
            TextView x = (TextView)findViewById(R.id.editText10);
            TextView y = (TextView)findViewById(R.id.editText11);
            int x1 = Integer.parseInt(x.getText().toString());
            int y1 = Integer.parseInt(y.getText().toString());
            int m1 = Integer.parseInt(m.getText().toString());

            String beaconKey = entry.getKey();
            List<Beacon> aBeaconGroup = entry.getValue();
            int value = 0;
           // int filRssi = 0;
            float avgRssi = 0;
            for (Beacon b : aBeaconGroup){
              //  filRssi = (int) kalmanFilter.updateRSSI(b.getRssi());
             //   value +=filRssi;
                value += b.getRssi();


            }

            final boolean isInserted = dbHelper.insertData(m1, x1, y1, beaconKey, value/aBeaconGroup.size());

        }



    }


    public void LoadData() {
        Cursor res = dbHelper.getAllData();
        if(res.getCount() == 0) {
            showMessage("Error", "Nothing found");
            return;
        }
        StringBuffer buffer = new  StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :" +res.getInt(0) + "\n");
            buffer.append("X :" +res.getInt(1) + "\n");
            buffer.append("Y :" +res.getInt(2) + "\n");
            buffer.append("Beacon :" +res.getString(3) + "\n");
            buffer.append("Rssi :" +res.getInt(4) + "\n\n");

        }
        showMessage("Data", buffer.toString());

    }
    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();

    }

    private void stopScan() {
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 7077 , null);
        beaconManager.stopRanging(region);

    }


    @Override
    protected void onPause() {
        if(beaconManager==null) return; //prevent form below call
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(beaconManager==null) return; //prevent form below call

        if(!SystemRequirementsChecker.checkWithDefaultDialogs(this)) return;
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }



    private void startScan() {
        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_LONG).show();
        beaconManager = new BeaconManager(this);
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 7077 , null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                boolean IsDisplay = false;
                if (!list.isEmpty()) {
                    for (Beacon b : list){

                        IsDisplay = makeBeaconList(b);
                    }
                    if (IsDisplay == true) {
                        displayBeacon();
                    }
                }
            }
        });
    }



    private boolean makeBeaconList(Beacon beacon) {
      //  int value =0;
        TextView s = (TextView)findViewById(R.id.editText9) ;
        int samples = Integer.parseInt(s.getText().toString());
        SampleSize = samples;

        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        List<Beacon> aBeacongroup = new ArrayList<>();
        if (BEACON_LIST.containsKey(beaconKey))
            aBeacongroup = BEACON_LIST.get(beaconKey);
        if(aBeacongroup.size() > (SampleSize-1))
            return false;
        aBeacongroup.add(beacon);

        BEACON_LIST.put(beaconKey, aBeacongroup);


        return true;


    }


    public void maketableCaption() {
        TextView txtID = (TextView) findViewById(R.id.id);
        txtID.setText(getString(R.string.id));
        TextView txtRSSI = (TextView) findViewById(R.id.rssi);
        txtRSSI.setText(getString(R.string.rssi));

    }

    private void displayBeacon() {

        int current = 0;
        TableLayout tableLayout = (TableLayout) findViewById(R.id.beaconTable);
        tableLayout.removeAllViews();

        for (Map.Entry<String, List<Beacon>> entry : BEACON_LIST.entrySet()) {
            current++;
            List<Beacon> aBeaconGroup = entry.getValue();
            int value = 0;
            float avgRssi = 0;
          //  int filRssi = 0;
            for (Beacon b : aBeaconGroup) {
               // filRssi = (int) kalmanFilter.updateRSSI(b.getRssi());
                value = b.getRssi();
                //  distance = (float) Math.pow(10, (-50-(float)b.getRssi())/20);



            }

            String beaconKey = entry.getKey();
            Log.d("Localization", beaconKey);
            TableRow tr = new TableRow(this);
            tr.setId(current);

            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // Create a TextView for id
            TextView tvID = new TextView(this);
            tvID.setId(current);
            tvID.setText(beaconKey);
            tvID.setTextColor(Color.BLACK);
            tvID.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(tvID);

            // Create a TextView for RSSI
            TextView tvValue = new TextView(this);
            tvValue.setId(current);
            tvValue.setText("                     " + value); //to covernt into sting ""+ is used
            // tvValue.setText("RSSI");
            tvValue.setTextColor(Color.BLACK);
            tvValue.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(tvValue);

            TextView tvPower = new TextView(this);
            tvPower.setId(current);
            tvPower.setText("               " + aBeaconGroup.size()); //to covernt into sting + is used
            // tvPower.setText("Power");
            tvPower.setTextColor(Color.BLACK);
            tvPower.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(tvPower);


            tableLayout.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


        }
    }

    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }

    }
}
