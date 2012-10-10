package com.cmware.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EchoActivity extends Activity {

    private static final String LOG_TAG = "echoActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_echo, menu);
        return true;
    }

    public void submitClicked(View view) {
        Log.d(LOG_TAG, "Submit clicked");

        AsyncTask<URL, Integer, String> task = new AsyncTask<URL, Integer, String>() {
            @Override
            protected String doInBackground(URL... params) {
                if (params != null && params.length == 1) {
                    HttpURLConnection conn = null;
                    try {
                        conn = (HttpURLConnection) params[0].openConnection();
                        return getContent(conn);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "error trying to connect to the server", e);
                        return "Error, couldn't connect to the server: " + e.getMessage();
                    } catch (ClassCastException e) {
                        Log.e(LOG_TAG, "error retrieving content", e);
                        return "Error, problem retrieving content: " + e.getMessage();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                            conn = null;
                        }
                    }
                }

                return "Invalid parameters to connect to the server";
            }

            private String getContent(HttpURLConnection conn) throws IOException {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    return reader.readLine();
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }

            }

            @Override
            protected void onPostExecute(String result) {
                updateResponseView(result);
            }

        };

        try {
            task.execute(getEchoURL(getEchoParameter()));
        } catch (Exception e) {
            Toast.makeText(this, "Could not connect to server", Toast.LENGTH_SHORT).show();
        }
    }

    private URL getEchoURL(String echoParameter) throws Exception {
        String echoParamValue = null;
        try {
            echoParamValue = URLEncoder.encode(echoParameter, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        try {
            URL url = new URL("http://10.30.0.7:8080/EchoServer/echo?value=" + echoParamValue);
            return url;
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Couldn't create URL: " + e.getMessage());
            throw new Exception(e);
        }
    }

    private String getEchoParameter() {
        EditText inputView = getEditText(R.id.echoInput);
        String value = inputView.getText().toString();
        return value;
    }

    private EditText getEditText(int viewId) {
        View view = findViewById(viewId);
        return (EditText) view;
    }

    private void updateResponseView(String result) {
        EditText textView = getEditText(R.id.responseView);
        textView.append(result);
        textView.append(System.getProperty("line.separator"));
    }

}
