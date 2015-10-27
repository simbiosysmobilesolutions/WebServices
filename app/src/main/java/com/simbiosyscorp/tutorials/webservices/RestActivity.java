package com.simbiosyscorp.tutorials.webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RestActivity extends Activity {

    //The following code is a REST call
    // to the Yahoo Developers' Console WebServices
    ListView addressesListView;
    Button getJSONBtn, getXmlBtn;
    EditText zipUserInput, storeUserInput;
    String zip, store, temp;
    static String queryString;
    static String xmlqueryString;
    private HandleXML xmlObj;
    ArrayList<String> xmlResultAddresses;

    static String[] TITLES = new String[10];
    URL url = null;
    HttpURLConnection urlConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        addressesListView = (ListView) findViewById(R.id.listView);
        getJSONBtn = (Button) findViewById(R.id.getJson);
        getXmlBtn = (Button) findViewById(R.id.getxml);
        zipUserInput = (EditText) findViewById(R.id.zip);
        storeUserInput = (EditText) findViewById(R.id.store);
        getJSONBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zip = zipUserInput.getText().toString();
                store = storeUserInput.getText().toString();
                queryString = " https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20local.search%20where%20query%3D%22" + store + "%22%20and%20location%3D%22" + zip + "%2C%20ca%22&format=json&diagnostics=true&callback=";      //

                try {
                    //Creating URL from query String
                    url = new URL(queryString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //Executing AsyncTask that makes the REST call
                //Parsing JSON response
                new JSONProcessorAsyncTask().execute();


            }
        });
        getXmlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zip = zipUserInput.getText().toString();
                store = storeUserInput.getText().toString();
                //query to get XML response
                xmlqueryString = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20local.search%20where%20query%3D%22"+store+"%22%20and%20location%3D%22"+zip+"%22%20&diagnostics=true";
                xmlObj = new HandleXML(xmlqueryString);
                //Function in HandleXML class that parses the xml response
                xmlObj.fetchXML();

                while (xmlObj.parsingComplete) ;
                xmlResultAddresses = xmlObj.getAddress();
                //Displaying result on Listview
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(RestActivity.this, android.R.layout.simple_list_item_1, xmlResultAddresses);
                addressesListView.setAdapter(adapter);
            }
        });
    }

    private class JSONProcessorAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... arg0) {

            urlConnection = null;
            InputStream inputStream = null;
            String result = null;

            try {
                //Http connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);


                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {//Checking for 'Success' condition

                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    //Converting response to String
                    result = convertInputStreamToString(inputStream);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {

                    if (inputStream != null)
                        inputStream.close();
                } catch (Exception e) {

                }
            }

            JSONObject jsonObject;
            try {
                Log.v("JSONParser RESULT", result);
                jsonObject = new JSONObject(result);

                JSONObject queryJSONObject = jsonObject.getJSONObject("query");
                JSONObject resultJSONObject = queryJSONObject
                        .getJSONObject("results");

                JSONArray jsonArray = resultJSONObject.getJSONArray("Result");
                int num = jsonArray.length();

                for (int i = 0; i < 10; i++) {

                    String title = (i + 1) + ". ";
                    title += jsonArray.getJSONObject(i)
                            .getString("Address");
                    title += jsonArray.getJSONObject(i)
                            .getString("Distance");
                    title += " ";
                    TITLES[i] = title;


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < 10; j++) {
                temp += TITLES[j];

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RestActivity.this, android.R.layout.simple_list_item_1, TITLES);
            addressesListView.setAdapter(adapter);
        }

    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

            /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }
        return result;
    }
}
