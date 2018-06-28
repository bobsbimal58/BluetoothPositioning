package com.example.rohan.beaconmanual2;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Rohan on 7/18/2017.
 */

public class FinalLocalization extends AppCompatActivity {



    HashMap<PointF, Double> fData1;
    HashMap<Integer, Double> sumData1;





    Button btnStart;
    Button btnLoc;



    BluetoothAdapter BA;
    BeaconManager beaconManager;
    Region region;

    DatabaseHelper dbHelper;


    PointF point;

    TextView tv;
    public static HashMap<String, List<Beacon>> BEACON_LIST1 = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loc);
        dbHelper = new DatabaseHelper(this);
        tv = (TextView)findViewById(R.id.tv);


        FrameLayout root1 = (FrameLayout)findViewById(R.id.root1);
        ImageView img1 = new ImageView(this);
        img1.setBackgroundColor(Color.GREEN);
        FrameLayout.LayoutParams params1  = new FrameLayout.LayoutParams(20,20);
        params1.leftMargin = 0;
        params1.topMargin = 0;
        root1.addView(img1, params1);
        ImageView img2 = new ImageView(this);
        img2.setBackgroundColor(Color.GREEN);
        FrameLayout.LayoutParams params2  = new FrameLayout.LayoutParams(20,20);
        params1.leftMargin = 700;
        params1.topMargin = 0;
        root1.addView(img2, params2);
        ImageView img3 = new ImageView(this);
        img3.setBackgroundColor(Color.GREEN);
        FrameLayout.LayoutParams params3  = new FrameLayout.LayoutParams(20,20);
        params3.leftMargin = 350;
        params3.topMargin = 450;
        root1.addView(img3, params3);
        ImageView img4 = new ImageView(this);
        img4.setBackgroundColor(Color.GREEN);
        FrameLayout.LayoutParams params4  = new FrameLayout.LayoutParams(20,20);
        params4.leftMargin = 0;
        params4.topMargin = 900;
        root1.addView(img4, params4);
        ImageView img5 = new ImageView(this);
        img5.setBackgroundColor(Color.GREEN);
        FrameLayout.LayoutParams params5  = new FrameLayout.LayoutParams(20,20);
        params5.leftMargin = 700;
        params5.topMargin = 900;
        root1.addView(img5, params5);






        btnStart = (Button) findViewById(R.id.btnStartLoc);
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


        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        List<Beacon> aBeacongroup = new ArrayList<>();
        if (BEACON_LIST1.containsKey(beaconKey))
            aBeacongroup = BEACON_LIST1.get(beaconKey);
        if(aBeacongroup.size() > 1000)
            return false;
        aBeacongroup.add(beacon);

        BEACON_LIST1.put(beaconKey, aBeacongroup);


        return true;


    }

    private void displayBeacon() {
        Cursor res = dbHelper.getAllData();
        String beacon = "";
        int rssi = 0;
        int id =0;
        int preid = 1;
        double DIFF = 0;
        int presize = 0;

        double diff = 0;
        double sum = 0;
        PointF location;
        sumData1= new HashMap<>();
        fData1= new HashMap<>();
        res.moveToFirst();
        List<Double> list = new ArrayList();
        int rssisum = 0;

        int readCnt = 0;


        if (res.isLast() == false) {
            do {
                    id=res.getInt(0);
                    location = new PointF(res.getInt(1), res.getInt(2));
                    beacon = res.getString(3);
                    rssi = res.getInt(4);






                    for (Map.Entry<String, List<Beacon>> entry : BEACON_LIST1.entrySet()) {
                        String beaconKey = entry.getKey();

                        if(id != preid){
                            Log.e("Map is changed", "********************** Map is changed **********************");
                            diff = 0;
                            preid = id;
                        }

                        if(!beaconKey.equals(beacon)){
                            continue;
                        }

                        Log.e("Beacon Key", "beaconKey : " + beaconKey + ", " + beacon.toString());

                        List<Beacon> aBeaconGroup = entry.getValue();
                        int value = 0;
                        int filRssi = 0;

                        Log.e("beaconGroup", "BeaconGroup Size : " + aBeaconGroup.size());




                        for(Beacon b : aBeaconGroup){
                            value = b.getRssi();
                           // filRssi = (int) kalmanFilter.updateRSSI(b.getRssi());
                            //value =  filRssi;

                            Log.e("beaconRssi", beacon.toString() + ", " + beaconKey + ", " + value + ", " + rssi);
                            rssisum += value;
                            Log.e("check rssisum", "sum => " + rssisum);
                            readCnt++;

                            if(readCnt == aBeaconGroup.size()) {
                                Log.e("pre diff", "pre diff" + diff);
                                diff += (Math.pow((value - rssi), 2));
                                sum = Math.sqrt(diff);
                                sumData1.put(id, diff);
                                fData1.put(location, sum);

                                Log.e("put Data", "put Data : " + diff);

                                readCnt = 0;
                                rssisum = 0;
                                break;
                            }
                        }
                        // wrong

                            /*
                            for (Beacon b : aBeaconGroup) {
                                value = b.getRssi();



                                Log.e("beaconGroup", "BeaconGroup Size : " + aBeaconGroup.size());

                                Log.e("beaconRssi", beacon.toString() + ", " + beaconKey + ", " + value);
                                Log.e("id", id + ", " + preid);

                                if(beacon.equals(beaconKey)){
                                    if(preid != id){
                                        diff = 0;
                                        preid = id;
                                    }

                                    rssisum += value;
                                    Log.e("check rssisum", "sum => " + rssisum);
                                    readCnt++;

                                    if(readCnt == aBeaconGroup.size()){
                                        Log.e("pre diff", "pre diff" + diff);
                                        diff += (Math.pow((rssisum/aBeaconGroup.size() - rssi), 2));
                                        sumData.put(id, diff);
                                        Log.e("put Data", "put Data : " + diff);

                                        readCnt = 0;
                                        rssisum = 0;
                                    }
                                }

                            }
                            */
                        Log.e("beaconEnd", "------------------------------------------------------------------------");
                            /*
                            // wrong
                            for(int i = 1; i<4; i++) {
                                if (id == i && beacon.equals(beaconKey)) {
                                    diff += (Math.pow((value - rssi), 2));

                                }
                            }
                            sumData.put(id, diff);
                            Log.e("beacon", "put data : " + diff);
                            diff = 0;

                                if (beacon.equals(beaconKey)) {
                                    DIFF = (Math.pow((value - rssi), 2));
                                    list.add(DIFF);
                                }
                        */

                    }


            }while (res.moveToNext()) ;

            //tv.append(sumData.toString() + "\n\n");
            // tv.append(fData.toString() + "\n\n");

            Map.Entry<PointF, Double> min = Collections.min(fData1.entrySet(), new Comparator<Map.Entry<PointF, Double>>() {
                @Override
                public int compare(Map.Entry<PointF, Double> o1, Map.Entry<PointF, Double> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
           // tv.setText("Location is: " + min.getKey() + " --" + min.getValue());
            PointF loc = min.getKey();
            int x = (int) loc.x;
            int y = (int) loc.y;

            tv.setText("Location: " +String.valueOf(x) + " " +"," + " " +String.valueOf(y));


            FrameLayout root = (FrameLayout)findViewById(R.id.root);
            ImageView img = new ImageView(this);
            img.setBackgroundColor(Color.BLUE);
            FrameLayout.LayoutParams params  = new FrameLayout.LayoutParams(20,20);
            if (root != null){
                root.removeAllViews();
            }

            params.leftMargin = x;
            params.topMargin = y;
            root.addView(img, params);






        }


      /*  for (Map.Entry<String, List<Beacon>> entry : BEACON_LIST.entrySet()) {
            current++;
            List<Beacon> aBeaconGroup = entry.getValue();
            int value = 0;
            float avgRssi = 0;
            for (Beacon b : aBeaconGroup){

                value = b.getRssi();
              //  distance = (float) Math.pow(10, (-50-(float)b.getRssi())/20);
                tv.setText(value);




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
            tvPower.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(tvPower);




            tableLayout.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
*/

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
