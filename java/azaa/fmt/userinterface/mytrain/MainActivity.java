package azaa.fmt.userinterface.mytrain;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {
       // implements SensorEventListener



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    /*public Sensor accelerometer;
    public SensorManager sm;
    public String operator = "", TAG = "DataCollector";
    public TelephonyManager tm;
    //public MyPhoneStateListener MyListener;
    public int cellID = 0, rssi = 0, count=0, flag=100000, dataArraySizeThresh = 20;
    public LocationManager GPSmgr;
    public static double GPSLat, GPSLong;*/

    public BufferedWriter out=null;
    public FileWriter fout=null;
    DatabaseHandler myDB;
    String results="";

    public static String fname = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream is;
        /*tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        MyListener = new MyPhoneStateListener();
        tm.listen(MyListener, PhoneStateListener.LISTEN_CELL_LOCATION);
        tm.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


//        position 0 stands for tab 1- central line
        //Fragment written = mSectionsPagerAdapter.getItem(0);


        /***** Initialise GSM 2 GPS database from csv file ******/
        //DatabaseHandler myDB = new DatabaseHandler(this);

        myDB = new DatabaseHandler(this);
        myDB.getWritableDatabase();

        //*** Insert to stationMap ***//*
        is = getResources().openRawResource(R.raw.stn);
        insertToStationMap(is);

        Log.d("station", "done");

        /*results = new RequestHandler().sendPostRequest("http://10.129.28.168:8007/FindMyTrain/Reporter", 1 + "");
        Log.d("Results", "onClick: Location :" + DataCollector.GPSLat + "," + DataCollector.GPSLong + " : Estimated : " + results);
        Log.d("result",results);*/
/*
        File root = new File(getExternalFilesDir(null).toString());
        if (!root.exists()) {
            root.mkdirs();
            //Log.d("as", "sds");
        }
        else{
            //Log.d("as", "sds");
        }
        //File file = new File(root,fname+".txt");
        File file = new File(root,fname+".txt");*/



        /*out = new BufferedWriter(fout);
        out.append(results);
*/


    }


    @Override
    protected void onStart() {
        super.onStart();

        /************* Start Data Collection on Background service ***********/
        this.startService(new Intent(this, DataCollector.class));
        Toast.makeText(getApplicationContext(), "Data Collection Started on Background!!!", Toast.LENGTH_SHORT).show();

        //-----------//
        Log.d("onstart","writeintofile");
        writeIntoFile(results);
        //locationquery();
        Log.d("onstart","afterwriteintofile");
    }


   public void writeIntoFile(String results){

      /* results = new RequestHandler().sendPostRequest("http://10.129.28.168:8007/FindMyTrain/Reporter", 1 + "");
       Log.d("Results", "onClick: Location :"+DataCollector.GPSLat+","+DataCollector.GPSLong+" : Estimated : "+results);*/

        Log.d("File writing method", results);

        File root = new File(getExternalFilesDir(null).toString());
        if (!root.exists()) {
            root.mkdirs();
            //Log.d("as", "sds");
        }
        else{
            //Log.d("as", "sds");
        }
        //File file = new File(root,fname+".txt");
        File file = new File(root,fname+".txt");


        try {
            fout = new FileWriter(file,true);
            BufferedWriter out  = new BufferedWriter(fout);
            Log.d("this", "location" + results);
            out.append(results + "\n");
            out.flush();
            out.close();

            Log.d("successfull", results);

        } catch (IOException e) {
            e.printStackTrace();
            // Log.d("as","sds");
        }
    }



    public void insertToStationMap(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] stn = csvLine.split(",");
                StationMap.mapping.add(
                        new StationMap(
                                Integer.parseInt(stn[0]),
                                Integer.parseInt(stn[1]),
                                stn[2],
                                Double.parseDouble(stn[3]),
                                Double.parseDouble(stn[4]),
                                Double.parseDouble(stn[5]),
                                Double.parseDouble(stn[6])
                        )
                );
            }
        } catch (IOException ex) {
            Toast.makeText(getApplicationContext(), "Error in database file loading!!!", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error in database file loading!!!", Toast.LENGTH_SHORT).show();
            }
        }
//        Log.d("SizeMap", new String(String.valueOf(StationMap.mapping.size())));
    }
/*
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        Log.d(TAG, "Accelerometer values: " + x + "," + y + "," + z);

    }*/


   /* public class MyPhoneStateListener extends PhoneStateListener {
        GsmCellLocation loc;

        @Override
        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);
            loc = (GsmCellLocation) tm.getCellLocation();
            operator=tm.getNetworkOperatorName();
            cellID=loc.getCid();
            Log.d(TAG, "CellID:  "+cellID + ',' + "Operator :"+operator);

        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            rssi=-113+2*signalStrength.getGsmSignalStrength();
            Log.d(TAG,"RSSI: " + String.valueOf(rssi));

        }

    }
*/


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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    Tab1 tab1 = new Tab1();
                   results = new RequestHandler().sendPostRequest("http://10.129.28.168:8007/FindMyTrain/Reporter", 1 + "");
                    //Log.d("Results", "onClick: Location :"+DataCollector.GPSLat+","+DataCollector.GPSLong+" : Estimated : "+results);
                    Log.d("result",results);

                    writeIntoFile(results);

                    return tab1;
                case 1:
                    Tab2 tab2 = new Tab2();
                    results = new RequestHandler().sendPostRequest("http://10.129.28.168:8007/FindMyTrain/Reporter", 1 + "");
                    Log.d("Results", "onClick: Location :"+DataCollector.GPSLat+","+DataCollector.GPSLong+" : Estimated : "+results);
                    //Log.d("result",results);

                    return tab2;
                case 2:
                    Tab3 tab3 = new Tab3();
                   results = new RequestHandler().sendPostRequest("http://10.129.28.168:8007/FindMyTrain/Reporter", 1 + "");
                    Log.d("Results", "onClick: Location :"+DataCollector.GPSLat+","+DataCollector.GPSLong+" : Estimated : "+results);
                    //Log.d("result",results);
                    return tab3;

                default:
                    return null;

            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Central";
                case 1:
                    return "Western";
                case 2:
                    return "Harbour";
            }
            return null;
        }
    }
}
