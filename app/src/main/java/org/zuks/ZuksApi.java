package org.zuks;

import android.location.Location;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Created by Simon Seyer on 22.12.14.
 */
public class ZuksApi {

    private static final String TAG = "ZuksApi";
    private static final String BASE_URL = "http://192.168.11.108:8000/";

    public void updateUserLocation(final int userID, final Location location) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject volunteer = get(BASE_URL + "volunteers/" + userID + "/");
                    if (volunteer != null) {

                        JSONObject jsonLocation = new JSONObject();
                        jsonLocation.put("latitude", location.getLatitude());
                        jsonLocation.put("longitude", location.getLongitude());

                        patch(BASE_URL + "locations/" + volunteer.getString("id") + "/", jsonLocation.toString());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void patch(String url, String content) {
        Log.i(TAG, "PATCH: '" + url + "': " + content);

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(content);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, e);
        }

        HttpPatch patch = new HttpPatch(url);
        patch.setEntity(stringEntity);
        patch.setHeader("Accept", "application/json");
        patch.setHeader("Content-type", "application/json");

        HttpResponse response = null;
        try {
            response = new DefaultHttpClient().execute(patch);
        } catch (IOException e) {
            Log.w(TAG, e);
        }

        try {
            String resp = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "Response: " + resp);
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    private JSONObject get(String url) {
        Log.i(TAG, "GET: '" + url);

        HttpGet patch = new HttpGet(url);
        patch.setHeader("Accept", "application/json");
        patch.setHeader("Content-type", "application/json");

        HttpResponse response = null;
        try {
            response = new DefaultHttpClient().execute(patch);
        } catch (IOException e) {
            Log.w(TAG, e);
        }

        String resp = null;
        try {
            resp = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "Response: " + resp);
        } catch (IOException e) {
            Log.w(TAG, e);
        }

        try {
            return new JSONObject(resp);
        } catch (JSONException e) {
            Log.w(TAG, e);
        }

        return null;
    }

    private class HttpPatch extends HttpEntityEnclosingRequestBase {

        public final static String METHOD_NAME = "PATCH";

        public HttpPatch() {
            super();
        }

        public HttpPatch(final URI uri) {
            super();
            setURI(uri);
        }

        public HttpPatch(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }

    }

}
