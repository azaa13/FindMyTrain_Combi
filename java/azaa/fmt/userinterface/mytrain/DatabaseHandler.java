package azaa.fmt.userinterface.mytrain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String dbName = "locationDB", locationMAP = "locationMAP", stationMap = "stationMap";

    InputStream is = null;
    String TAG = "DatabaseHandler";
    List<String> dataFile;
    Context cntxt;
    public DatabaseHandler(Context context) {
        super(context, dbName, null, 25);
        cntxt = context;
        Log.d("dbhandler","db");
    }
    /************* Create Table in Database **************/
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("new object being","tag");

        String sql = "CREATE TABLE " + locationMAP + " " +
                "( " +
                "routeID INTEGER NOT NULL, " +
                "operator TEXT NOT NULL, " +
                "cellID INTEGER NOT NULL, " +
                "RSSI INTEGER NOT NULL, " +
                "lat DOUBLE NOT NULL, " +
                "lon DOUBLE NOT NULL," +
                "netlat DOUBLE NULL, " +
                "netlon DOUBLE NULL" +
                " );";

        db.execSQL(sql);
        dataFile = readCsvFile(cntxt.getResources().openRawResource(R.raw.data));
        insertLocationMap(dataFile, db);
        Log.d("dbhandler", "dbinsideCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + locationMAP;
        db.execSQL(sql);
        onCreate(db);
    }


    /********** Insert GSM to GPS map from csv file to LocationDB database **********/
    public void insertLocationMap(List<String> dataFile, SQLiteDatabase db)   {
        ContentValues cv;
         Log.d("insertLocation","hatt");
        for (String line: dataFile) {
            String[] d = line.split(",");
            cv = new ContentValues();
            cv.put("routeID",Integer.parseInt(d[0]));
            cv.put("operator",d[1]);
            cv.put("cellID",Integer.parseInt(d[2]));
            cv.put("RSSI", Integer.parseInt(d[3]));
            cv.put("lat", Double.parseDouble(d[4]));
            cv.put("lon", Double.parseDouble(d[5]));
            //cv.put("netlat", Double.parseDouble(d[6]));
            //cv.put("netlon", Double.parseDouble(d[7]));
            Log.d("asa",d[4]+" "+d[5]);
            //Double a = (Double) cv.get("netlon");
            //Log.d("cv", Double.toString(a));
//           Log.d("insertLocation", Double.parseDouble(d[6]) + " " + Double.parseDouble(d[7]));
            // Toast.makeText(cntxt, d[6] + " " + d[7], Toast.LENGTH_SHORT).show();
            db.insert(locationMAP, null, cv);
        }
        Log.d(TAG, "insertLocationMap: Added data to db!!!");

    }


    /*********** Read the LocationMap table and return an ArrayList of results ***********/
    public ArrayList<ArrayList<String>> readLocationMap(String sql)    {
        Log.d("first location map", sql);
        ArrayList<ArrayList<String>> locationMapData = new ArrayList<>();
        ArrayList<String> read;
        Log.d("inside location map", sql);


        SQLiteDatabase db = getWritableDatabase();

        Log.d("SQL",sql);
        Cursor c = db.rawQuery(sql, null);
        if(c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                //Log.d("sql",c.getString());
                //
                Log.d("readLocationMap", c.getString(5));
                //Log.d("readLocationMap", c.getString(6));
                //
                read = new ArrayList<>();
                read.add(c.getString(0));
                read.add(c.getString(1));
                read.add(c.getString(2));
                read.add(c.getString(3));
                read.add(c.getString(4));
                read.add(c.getString(5));
                //read.add(c.getString(6));
                //read.add(c.getString(7));
                locationMapData.add(read);
                c.moveToNext();
            }
            c.close();
        }
        else
            Log.d("close", String.valueOf(c));
        db.close();
        return locationMapData;
    }


    /******************* Read a csv file in to a List of String ******************/
    public List<String> readCsvFile(InputStream is) {
        List<String> resultList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                resultList.add(csvLine);
                Log.d("ReadCSV", csvLine);
            }
        } catch (IOException ex) {
            Log.d(TAG, "readCsvFile: "+"Error in database file loading!!!");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                 Log.d(TAG, "readCsvFile: "+"Error in closing database file loading!!!");
            }
        }
        return resultList;
    }
}