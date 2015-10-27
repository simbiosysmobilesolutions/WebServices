package com.simbiosyscorp.tutorials.webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class SOAPActivity extends Activity {
    private static final String SOAP_ACTION = "http://footballpool.dataaccess.eu/TopGoalScorers";
    private static final String METHOD_NAME = "TopGoalScorers";
    private static final String NAMESPACE = "http://footballpool.dataaccess.eu";
    private static final String URL = "http://footballpool.dataaccess.eu/data/info.wso?WSDL";
    ListView listView;
    private ArrayList<String> response;
    SoapObject result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap);
        response = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        //Executing AsyncTask for SOAP call to get top scorers from
        //footballpool.dataaccess.eu
        myAsyncTask myRequest = new myAsyncTask();
        myRequest.execute();

    }

    private class myAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //SOAP request
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("iTopN", "5");
            //Envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(URL);

            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION, envelope);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("App", "IOException");

            }  //send request
            catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            try {
                result = (SoapObject) envelope.getResponse();

            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("App", "SoapFault");

            }

            for (int i = 0; i < 5; i++) {
                response.add(result.getProperty(i).toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //Displaying results on Listview
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SOAPActivity.this, android.R.layout.simple_list_item_1, response);
            listView.setAdapter(adapter);
        }
    }

}
