package com.aig.science.damagedetection.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import com.aig.science.damagedetection.controllers.LoginActivity;
import com.aig.science.damagedetection.controllers.SplashScreenActivity;
import com.aig.science.damagedetection.helper.DatabaseHelper;
import com.aig.science.damagedetection.models.UserInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceHandler {

    private final String serverUrl = "http://exalted-airfoil-768.appspot.com/damagedetection";
    private final String TAG = "ServiceHandler";
    private Context myContext;
    private String resultString = "";
    private JSONArray resultJsonArray;
    private JSONObject resultObject;
    private static String activityTAG = "";
    private String transactionType = "";

    public ServiceHandler(Context myContext) {
        this.myContext = myContext;
    }

    public void startServices(String TAG, Context context, String message) {
        new Webmsgs(TAG, context, message).execute();
    }

    public String getConnectionService(String message) {
        Log.i("to_server", message);
        if (isConnected()) {
            performServerRequest(message);
        }
        return resultString;
    }

    public void performServerRequest(String message) {
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(message.toString());
            out.close();

            int result = urlConnection.getResponseCode();
            if (result == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String data = null;
                StringBuilder sbr = new StringBuilder();
                while ((data = br.readLine()) != null) {
                    sbr.append(data);
                }
                br.close();
                handleServerResponse(message.toString(), sbr.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG,"MalformedURLException" + e.getMessage() );
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.d(TAG,"ProtocolException" + e.getMessage() );
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"IOException" + e.getMessage() );
        }
    }

    private void handleServerResponse(String line, String result) {
        DatabaseHelper db = null;
        try {
            if (db == null) {
                db = new DatabaseHelper(SplashScreenActivity.getContextOfApplication());
            }
            SharedPreferences shared = PreferenceManager
                    .getDefaultSharedPreferences(SplashScreenActivity
                            .getContextOfApplication());
            JSONObject object = new JSONObject(line);
            transactionType = object.getString("transaction_type");
            String action = object.getString("action");
            if (transactionType.equals("MODIFICATION_PUSH")) {
                if (action.equals("INSERT")) {
                    db.insertDataIntoDB(object);
                } else if (action.equals("UPDATE")) {
                    db.updateDataIntoDB(object);
                } else if (action.equals("DELETE")) {
                    db.deleteDataFromDB(object);
                }
            }
            if (transactionType.equals("MODIFICATION_PULL")) {
                if (action.equals("QUERY")) {
                    try {
                        resultObject = new JSONObject(URLDecoder.decode(result, "UTF-8"));
                        resultJsonArray = new JSONArray(resultObject.get("data").toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) myContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netwrkInfo = connMgr.getActiveNetworkInfo();
        if (netwrkInfo != null && netwrkInfo.isConnected()) {
            return true;
        }
        Log.d("connection_test", "Failed .. No Internet Access");
        return false;
    }

    private class Webmsgs extends AsyncTask<Void, Void, Void> {
        private String message;
        //private ProgressDialog dialog;
        private Context context;
        private String activityTAG;


        public Webmsgs(String tag, Context context, String message) {
            this.message = message;
            this.context = context;
            this.activityTAG = tag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void params) {
            //android.os.Debug.waitForDebugger();
            if (transactionType.equalsIgnoreCase("MODIFICATION_PULL")) {
                LoginActivity logContext = (LoginActivity) context;
                switch (activityTAG.toUpperCase()) {
                    case "LOGINACTIVITY":
                        //LoginActivity logContext = (LoginActivity)context;
                        logContext.handleRemoteAuthResponse(resultJsonArray);
                        break;
                    case "POLICYLISTACTIVITY":
                        logContext.syncPolicyInfoTable(resultJsonArray);
                        logContext.startSyncClaimsService();
                        break;

                    case "CLAIMSLISTACTIVITY":
                        logContext.syncClaimsTable(resultJsonArray);

                        /*logContext.startSyncImagesService();*/
                        logContext.startHomeIntent();
                        break;

                    case "TAKEPHOTOSACTIVITY":
                        logContext.syncImagesTable(logContext.getClaimList());
                        break;
                }
            }
        }
        @Override
        protected Void doInBackground(Void... params) {
            //android.os.Debug.waitForDebugger();
            Log.d("ServiceHandler", "doInBackground....");
            getConnectionService(message);
            return null;
        }
    }
}