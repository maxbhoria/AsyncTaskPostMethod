package com.example.mohitbhoria.postmethod;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    ProgressDialog pDialog;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn= (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put("userId", "3");
                postRequest(params);
            }
        });
    }
    private void postRequest(final HashMap<String, String> postParams) {

        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();

            }
            @Override
            protected String doInBackground(String... params) {
                InputStream is = null;
                String responseString = "";
                try {
                    URL url = new URL("http://52.27.53.102/iot/user/PostRequest");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setDoOutput(true);
                    con.setReadTimeout(30000);
                    con.setConnectTimeout(30000);
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                   con.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    OutputStream os = con.getOutputStream();
                    os.write(getPostDataString(postParams).getBytes());
                    os.flush();
                    os.close();

                    int statusCode = con.getResponseCode();

                    if (statusCode == 200) {
                        is = con.getInputStream();
                        responseString = convertInputStreamToString(is);
                        return responseString;
                    } else {
                        return null;
                    }
                } catch (SocketTimeoutException e) {
                    return null;
                } catch (Exception ex) {
                    return null;
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        return null;
                    }
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    String response=result;
                    try {
                        JSONObject resObject=new JSONObject(response);
                   /* String errorString=resObject.getString("error_string");
                    String errorCode=resObject.getString("error_code");
                    String message=resObject.getString("message");*/
                        JSONArray post=resObject.getJSONArray("result");
                        for(int i=0;i<post.length();i++) {
                            JSONObject arrayObj = post.getJSONObject(i);
                            String postid=arrayObj.getString("postid");
                            String postmsg = arrayObj.getString("postmessage");
                            Toast.makeText(getApplicationContext(),postid+postmsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage()
                                        ,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {

                }

            }
        }.execute();

    }
    public String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder result = null;
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            result = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                result.append(line).append('\n');
            }
            return result.toString();
        } catch (Exception ex) {
            return "";
        }

    }
}
