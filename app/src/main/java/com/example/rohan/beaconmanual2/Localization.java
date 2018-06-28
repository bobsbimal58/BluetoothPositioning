package com.example.rohan.beaconmanual2;

import android.database.Cursor;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Rohan on 7/11/2017.
 */

public class Localization extends AppCompatActivity {
    BeaconManager beaconManager;
    Region region;


    BLEScan ble;
    DatabaseHelper dbHelper;
    HashMap<PointF, Double> fData;
    HashMap<String, List<Beacon>> BEACON_LIST = new HashMap<>();
    HashMap<Integer, Double> sumData;




    TextView tv;
    Button btn, btnLoad;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localization_main);
        tv = (TextView) findViewById(R.id.loc);
        ble = new BLEScan();
        dbHelper = new DatabaseHelper(this);


        btn = (Button) findViewById(R.id.btnStartLoc);
        if (btn != null) {
            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLocalization();
                }

            });
        }

        btnLoad = (Button) findViewById(R.id.btnLoad);
        if (btnLoad != null) {
            btnLoad.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                   //LoadData();
                }

            });
        }
    }










    public void startLocalization() {
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
        sumData= new HashMap<>();
        fData= new HashMap<>();
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


                for (Map.Entry<String, List<Beacon>> entry : BEACON_LIST.entrySet()) {
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

                    Log.e("beaconGroup", "BeaconGroup Size : " + aBeaconGroup.size());




                    for(Beacon b : aBeaconGroup){
                        value = b.getRssi();

                        Log.e("beaconRssi", beacon.toString() + ", " + beaconKey + ", " + value + ", " + rssi);
                        rssisum += value;
                        Log.e("check rssisum", "sum => " + rssisum);
                        readCnt++;

                        if(readCnt == aBeaconGroup.size()) {
                            Log.e("pre diff", "pre diff" + diff);
                            diff += (Math.pow((value - rssi), 2));
                            sum = Math.sqrt(diff);
                            sumData.put(id, diff);
                            fData.put(location, sum);

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

            Map.Entry<PointF, Double> min = Collections.min(fData.entrySet(), new Comparator<Map.Entry<PointF, Double>>() {
                @Override
                public int compare(Map.Entry<PointF, Double> o1, Map.Entry<PointF, Double> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
            tv.setText("Location is: " + min.getKey() + " --" + min.getValue());

        }


    }



}




