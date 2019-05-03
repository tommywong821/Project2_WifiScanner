package hkcc.ccn3165.project2_wifiscanner;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int LOCATION_PERMISSION_CODE = 1;
    DatabaseHelper mDatabaseHelper;
    private WifiManager wifiManger;
    private ListView wifiListView;
    private Button btnScan, btnSaveWiFi, btnOpenWiFi, btnEmailWiFi, btnSubmit, btnStop;
    private EditText etWaitingTime;
    private TextView tvShowWaitingTime;
    private List<ScanResult> results;
    ArrayList<WIFIInformation> wifiList = new ArrayList<>();
    private wifiListadapter adapter;

    private long waitTime = 5;
    private String showWaitingTime = String.format("(Current waiting time for each scan: %d seconds)", waitTime);

    Handler scanHandler = new Handler();
    final Runnable locationUpdate = new Runnable() {
        @Override
        public void run() {
            scanWifi();
            scanHandler.postDelayed(locationUpdate, waitTime*1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPSService.TIME = waitTime;
        //checking permission
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You have already granted location permission!",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }

        mDatabaseHelper = new DatabaseHelper(this);
        btnScan = (Button)findViewById(R.id.scanWIFI);
        btnScan.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                scanHandler.post(locationUpdate);
            }
        });

        btnSaveWiFi = (Button)findViewById(R.id.saveWIFI);
        btnSaveWiFi.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean insertData = false;
                for(WIFIInformation insert:wifiList) {
                    insertData = mDatabaseHelper.addData(insert.getLongitude(), insert.getLatitude(), insert.getBSSID(), insert.getSSID(), insert.getDate(), insert.getTime());
                    if (insertData) {
                        Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Data not inserted", Toast.LENGTH_SHORT).show();
                    }
                }
                if(insertData == false)
                    Toast.makeText(MainActivity.this, "No data is inserted", Toast.LENGTH_SHORT).show();
            }
        });

        btnOpenWiFi = (Button)findViewById(R.id.openWIFI);
        btnOpenWiFi.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
                startActivity(intent);
                scanHandler.removeCallbacks(locationUpdate);
            }
        });

        btnEmailWiFi = (Button)findViewById(R.id.emailWIFI);
        btnEmailWiFi.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(MainActivity.this, SendEmailActivity.class);
                startActivity(sendIntent);
                scanHandler.removeCallbacks(locationUpdate);
            }
        });

        wifiListView = (ListView)findViewById(R.id.WIFIListView);
        wifiManger = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(!wifiManger.isWifiEnabled()){
            Toast.makeText(this, "WIFI is not enabled. We need to enable it", Toast.LENGTH_LONG).show();
            wifiManger.setWifiEnabled(true);
        }

        adapter = new wifiListadapter(this, R.layout.adapter_view_layout, wifiList);
        wifiListView.setAdapter(adapter);

        etWaitingTime = (EditText)findViewById(R.id.waitTime);

        tvShowWaitingTime = (TextView)findViewById(R.id.displayCurrentWaitingTime) ;
        tvShowWaitingTime.setText(showWaitingTime);

        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitTime = Long.parseLong(etWaitingTime.getText().toString());
                GPSService.TIME = waitTime;
                etWaitingTime.setText("");
                showWaitingTime = String.format("(Current waiting time for each scan: %d seconds)", waitTime);
                tvShowWaitingTime.setText(showWaitingTime);
            }
        });

        btnStop = (Button)findViewById(R.id.stopWIFI);
        btnStop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    scanHandler.removeCallbacks(locationUpdate);
                    unregisterReceiver(wifiReceiver);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "No WiFi is scanning", Toast.LENGTH_SHORT).show();
                }
            }
        });

        watcher(etWaitingTime, btnSubmit);
    }

    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManger.startScan();
        Toast.makeText(this, "Scanning WiFi", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManger.getScanResults();
            unregisterReceiver(this);

            for(ScanResult scanResult : results){
                double latitude;
                double longitude;
                GPSService mGPSService = new GPSService(MainActivity.this);
                mGPSService.getLocation();
                if (mGPSService.isLocationAvailable == false) {
                    // Ask the user to try again, using return; for that
                    Toast.makeText(MainActivity.this, "Your location is not available", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    latitude = mGPSService.getLatitude();
                    longitude = mGPSService.getLongitude();
                }
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String time = timeFormat.format(calendar.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
                String date = dateFormat.format(calendar.getTime());

                WIFIInformation wifi = new WIFIInformation("longitude: " + Double.toString(longitude), "latitude: " + Double.toString(latitude),"BSSID: " + scanResult.BSSID, "SSID: " +scanResult.SSID,"Date: " + date, "Time: " + time);
                wifiList.add(wifi);
                adapter.notifyDataSetChanged();
                mGPSService.closeGPS();
            }
        }
    };

    void watcher(final EditText message_body, final Button Send)
    {
        message_body.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if(message_body.length() == 0)
                    Send.setEnabled(false); //disable send button if no text entered
                else
                    Send.setEnabled(true);  //otherwise enable

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){
            }
        });
        if(message_body.length() == 0) Send.setEnabled(false);//disable at app start
    }

    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of getting the location of the wifi signal")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            scanHandler.removeCallbacks(locationUpdate);
            unregisterReceiver(wifiReceiver);
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "No receiver is registered", Toast.LENGTH_SHORT).show();
        }

    }
}
