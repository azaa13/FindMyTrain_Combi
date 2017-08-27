package azaa.fmt.userinterface.mytrain;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Azaa on 11/8/17.
 */
public class Analyzer {

    public static final double locationMatchThresh = 0.7;
   // public int routeID=0, direction=-1; Initial state; but I changed it for testing purpose
    public int routeID=1, direction=1;
    public String TAG="Analyzer";
    public Context context;
    String results="";
    String url = "http://10.129.28.168:8007/FindMyTrain/RequestHandler";
    public Analyzer(Context c) {
        context = c;
    }


    public boolean getGpsFromGsm()  {

        int[] r = new int[3];
        int count=0;
        double lat=0, lon=0;
        long timeStamp;
        String sql, op;


        Log.i("length", String.valueOf(Data.location.size()));
        for (Data loc: Data.location) {
            Log.d("sfsf","hihihi");
            op = getOperator(loc.operator);
            Log.d("asdas",loc.cellID+" "+loc.RSSI);
            /*sql = "SELECT * FROM "+ DatabaseHandler.locationMAP +" WHERE cellID = "+loc.cellID+
                    " AND RSSI = "+loc.RSSI + " AND operator = \""+op+"\"";*/

            sql = "SELECT * FROM "+ DatabaseHandler.locationMAP +" WHERE cellID = "+39693+
                            " AND RSSI = "+ -85 + " AND operator = \""+op+"\"";
            DatabaseHandler myDataBaseHelper = new DatabaseHandler(context);
            ArrayList<ArrayList<String>> resultSet = myDataBaseHelper.readLocationMap(sql);
            Log.d("resultSet size: ",String.valueOf(resultSet.size()));
            if(!resultSet.isEmpty())    {
                Log.d("sfsf","hahaha");
                count++;
                lat = Double.parseDouble(resultSet.get(0).get(4));
                lon = Double.parseDouble(resultSet.get(0).get(5));

                Log.d("lat from dbhelper",String.valueOf(lat));
                Log.d("lat from dbhelper",String.valueOf(lon));
                /***** Increse route count if GSM matches the corresponding route *****/
                if(Integer.parseInt(resultSet.get(0).get(0))==0)   r[0]++;
                if(Integer.parseInt(resultSet.get(0).get(0))==1)   r[1]++;
                if(Integer.parseInt(resultSet.get(0).get(0))==2)   r[2]++;

                timeStamp = loc.timeStamp;

                /******* Send only 5 data from the entire window seperated by equal time Interval **********/
//                if(count % Data.location.size()/5 == 0)       -1
                FinalData.finalData.add(new FinalData(lat, lon, 1, timeStamp));
            }
            else {
                Log.d(TAG, "getGpsFromGsm: Data not in GSM fingerprinting db " + loc.cellID + ", " + loc.RSSI);
            }
        }


        /****** RouteID is the route with maximum number of counts *******/
        routeID = r[0]>r[1]?(r[0]>r[2]?0:2):(r[1]>r[2]?1:2);

        /****** get direction in which the trace is moving *******/
//        if(!getDirection()) return false;

        getDirection();

//        Log.d(TAG, "getGpsFromGsm: Fin Data"+lat+","+lon+","+routeID+","+direction);

        /********* If number of matches of cellID_rssi pair doest exceed a threshold, reject the data ********/
//        return (count/Data.location.size() > locationMatchThresh);
        return true;
    }

    /******* get Operator name from Operator field ***********/
    public String getOperator(String op)    {
        if(op.contains("irtel") || op.contains("IRTEL"))    return "airtel";
        if(op.contains("ircel") || op.contains("IRCEL"))  return "aircel";
        if(op.contains("dea") || op.contains("DEA"))  return "idea";
        if(op.contains("dafone") || op.contains("DAFONE"))  return "vodafone";
        return null;
    }


    public boolean getDirection()  {
        Log.d("called", "get direc");
//        intitial and final stations are the two terminal stations of the given route
        double distFromInitialStn1=0, distFromInitialStnN=0, distFromFinalStn1=0, distFromFinalStnN=0;
        FinalData d;
        StationMap nearestStn;

        /******** Get distance from Initial and final station in route **********/

//        gets lattitude, longitude of the initial station
        double latInit = StationMap.terminalStnGPS[routeID][0], lonInit = StationMap.terminalStnGPS[routeID][1];

        //        gets lattitude, longitude of the final station
        double latFinal = StationMap.terminalStnGPS[routeID][2], lonFinal = StationMap.terminalStnGPS[routeID][3];


        Log.d("size of final data", String.valueOf(FinalData.finalData.size()));
//        if(FinalData.finalData.size()<2)
//            return false;

        for(Integer i=0; i<FinalData.finalData.size();i++){
            Log.d(String.valueOf(i), String.valueOf(FinalData.finalData.get(i).lat) + String.valueOf(FinalData.finalData.get(i).lon));
        }

        d = FinalData.finalData.get(0);
        distFromInitialStn1 = haversine(d.lat, d.lon, latInit, lonInit);
        Log.d("distfrominitalstn1",String.valueOf(distFromInitialStn1));
        distFromFinalStn1 = haversine(d.lat, d.lon, latFinal, lonFinal);
        Log.d("distfromfinalstn1",String.valueOf(distFromFinalStn1));
        d = FinalData.finalData.get(FinalData.finalData.size() - 1);
        distFromInitialStnN = haversine(d.lat, d.lon, latInit, lonInit);
        Log.d("distfrominitalstnN",String.valueOf(distFromInitialStnN));
        distFromFinalStnN = haversine(d.lat, d.lon, latFinal, lonFinal);
        Log.d("distfromFinalstnN",String.valueOf(distFromFinalStnN));


        boolean f1 = distFromInitialStn1 < distFromInitialStnN, f2 = distFromFinalStn1 < distFromFinalStnN, flag;


//        /******* If trace has travelled less than 100 m, data is rejected *******/
//        if(Math.abs(distFromInitialStn1 - distFromInitialStnN) < 100 || Math.abs(distFromFinalStn1 - distFromFinalStnN) < 100)
//            return false;
        Log.d("f",String.valueOf(f1) + String.valueOf(f2));
        flag = (distFromInitialStn1<distFromFinalStn1)?f1:!f2;
        direction = flag?1:0;

        /******* Find nearest station based on the nearest terminal station *******/
        nearestStn = flag?getNearestStn(distFromInitialStn1, flag):getNearestStn(distFromFinalStn1, flag);

        /******* Finally traverse the FinalData ArrayList to fill the distOrigin field *********/
        for(FinalData fd : FinalData.finalData)  {
            fd.dist = flag? nearestStn.distOrigin + haversine(fd.lat,fd.lon,nearestStn.lat,nearestStn.lon) :
                    nearestStn.distOrigin - haversine(fd.lat,fd.lon,nearestStn.lat,nearestStn.lon);
            Log.d(TAG, "Final Data : "+fd.lat+", "+fd.lon+", "+fd.dist+", "+routeID+", "+direction+", "+fd.timeStamp);
        }
//        Log.d(TAG, "Final Data : ====================================================================================");

        /******* Check if user is walking, sitting or running based on speed *******/
//        if(speedEligibility())  return false;
        return true;
    }


/*

    private boolean speedEligibility() {
        double dist = Math.abs(FinalData.finalData.get(0).dist - FinalData.finalData.get(FinalData.finalData.size()-1).dist);
        double time = Math.abs(FinalData.finalData.get(0).timeStamp - FinalData.finalData.get(FinalData.finalData.size()-1).timeStamp);
        if((dist/time) < 6 )       return true;
        return false;
    }
*/

    /******* Finds the nearest station that is less than the distance 'dis' from terminal(flag) *********/
    private StationMap getNearestStn(double dis, boolean flag) {
        StationMap res = null;
        for (StationMap d:StationMap.mapping) {
            if(d.routeID == routeID)    {
                if(flag)    {
                    if(d.distOrigin<=dis)    res = d;
                    else    return res;
                }
                else    {
                    if(d.distOrigin>=StationMap.routeLength[routeID]-dis){
                        return d;
                    }
                }
            }
        }
        return res;
    }


    /******* Calculate the straight-line distance between 2 GPS coordinates *********/
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6372800; // In meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }


    /******** Reorient Accelerometer data **********/
    public void reorient(CopyOnWriteArrayList<MotionData> data) {
        double valid, medX=0, medY=0, medZ=0, x, y, z, tmp;
        double yAngle, xAngle, cosX, cosY, sinX, sinY;
        int c=0;
        for (MotionData d: data) {
            valid = Math.sqrt(d.x*d.x + d.y*d.y + d.z*d.z);
            if(Math.abs(valid-9.8) < 1.2)   {
                medX += d.x;
                medY += d.y;
                medZ += d.z;
                c += 1;
            }
        }
        if(c==0){
            // Log.d("Accelerometer ", "Data Invalid!!!");
            return;
        }
        medX /= c;
        medY /= c;
        medZ /= c;

        /**************** Calculate the Angles based on Median*************/
        /*** Rotation across Y axis ***/
        yAngle = Math.asin(medX/Math.sqrt(medZ*medZ + medX*medX));

        /*** Rotation across X axis ***/
        xAngle = Math.atan2(Math.sqrt(medX*medX + medZ*medZ), medY);

        /*** Tweak used to handle data for different coordinate axes ***/
        if(medZ>0) {
            yAngle = -yAngle;
            xAngle = -xAngle;
        }

        cosY = Math.cos(yAngle);
        sinY = Math.sin(yAngle);
        cosX = Math.cos(xAngle);
        sinX = Math.sin(xAngle);

        /********* Finally add reoriented data to reOrientedMotion ArrayList ********/
        for (MotionData d: data) {
            tmp = -1*d.x*sinY + d.z*cosY;
            x = d.x*cosY + d.z*sinY;
            z = d.y*cosX - tmp*sinX;
            y = d.y*sinX + tmp*cosX;
            MotionData.reorientedMotion.add(new MotionData(x,y,z));
        }
        // Log.d(TAG, "reorient: Done");
    }


    /****** Detects whether person is on train *******/
    public String isOnTrain() {

        String jsonArray;
        int i=0;

       /*** Calculate sqrt(x2 + y2) for accelerometer data analysis ***/
        double[] dataXY = new double[MotionData.reorientedMotion.size()];
        for (MotionData d: MotionData.reorientedMotion) {
            dataXY[i] = Math.sqrt(d.x*d.x + d.y*d.y);
        }

        /**** Calculate the feature values fluctuation, mean, var, numThresh ******/
        double thres = 4;
        double mean = 0, var= 0, fluc=0, valThres=0, prev = 0;
        for (int j = 0; j < dataXY.length; j++) {
            mean += dataXY[j];
            if(dataXY[j] > thres)  valThres++;
            fluc += (dataXY[j]-prev);
            prev = dataXY[j];
        }
        mean /= dataXY.length;
        for (int j = 0; j < dataXY.length; j++) {
            var += (dataXY[j] - mean) * (dataXY[j] - mean);
        }
        var /= dataXY.length;

            /*
        ***** Create test data to be classified by svm ****
        double[][] xTest = new double[1][4];
        xTest[0][0] = fluc;
        xTest[0][1] = mean;
        xTest[0][2] = var;
        xTest[0][3] = valThres;

        **** Classify data from SVM. 1: train, 0: Others ******

        // ---------------- //

        double[] yPred = new Classify().svmPredict(xTest);

       /**** Delete old Reoriented accelerometer data *****/
        //MotionData.reorientedMotion.clear();

        /***** If svm classifier detects data on Train, Send data to server ******/
        //if(yPred[0] == 1) {

        //try {
            //Log.d(TAG, "isOnTrain: True...... Yes");
            /****** Create json array of Location data ******/
            //jsonArray = getJsonArray();
            // Log.d("size", String.valueOf(jsonArray.length()) + " --> "+jsonArray);
            /****** Send json Array to server ******/
/*
            RequestHandler request = new RequestHandler();
            request.sendPostRequest("http://10.129.28.97:8007/FindMyTrain/RequestHandler", jsonArray);

        //} catch (JSONException e) {
            Log.d(TAG, "isOnTrain: json exception");
        }*/
        //}
        //else Log.d(TAG, "isOnTrain: Not on Train");

        // --------------- //



        try{
            jsonArray = getJsonArray();
            Log.d("size", String.valueOf(jsonArray.length()) + " --> " + jsonArray);
            RequestHandler request = new RequestHandler();
            request.sendPostRequest(url, jsonArray);
            Log.d("sent", "to the server");

            /*results = new RequestHandler().sendPostRequest("http://10.129.28.168:8007/FindMyTrain/Reporter", 1 + "");
            Log.d("Results", "onClick: Location :"+DataCollector.GPSLat+","+DataCollector.GPSLong+" : Estimated : "+results);
*/

        }
        catch (JSONException e)
        {
            Log.d(TAG, "isOnTrain: json exception");
        }

        return results;
    }



    /****** Reads FinalData Object and creates a json array of it ******/
    public String getJsonArray() throws JSONException {
        JSONObject jObj;
        JSONArray jArray = new JSONArray();
        for (int i=0; i<FinalData.finalData.size(); i++) {
            FinalData d = FinalData.finalData.get(i);
            jObj = new JSONObject();
            jObj.put("lat",d.lat);
            jObj.put("lon",d.lon);
            jObj.put("dist",d.dist);
            jObj.put("routeID",routeID);
            jObj.put("direction",direction);
            jObj.put("timeStamp",d.timeStamp);
            jArray.put(jObj);
        }
        FinalData.finalData.clear();
        JSONObject data = new JSONObject();
        data.put("data", jArray);
        Log.d(TAG, "getJsonArray: "+data.toString());
        return (data.toString());
    }


}
