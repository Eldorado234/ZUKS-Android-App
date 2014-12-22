package org.zuks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
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

    public void updateUserLocation(int userID, Location location) {
        try {
            JSONObject jsonLocation = new JSONObject();
            jsonLocation.put("latitude", location.getLatitude());
            jsonLocation.put("longitude", location.getLongitude());

            JSONObject jsonData = new JSONObject();
            jsonData.put("location", jsonLocation);

            ApiPatchCall call = new ApiPatchCall();
            call.execute(BASE_URL + "volunteers/" + userID, jsonData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class ApiPatchCall extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            DefaultHttpClient client = new DefaultHttpClient();

            String url = params[0];
            HttpPatch patch = new HttpPatch(url);

            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity(params[1]);
            } catch (UnsupportedEncodingException e) {
                Log.w(TAG, e);
                return false;
            }

            patch.setEntity(stringEntity);
            patch.setHeader("Accept", "application/json");
            patch.setHeader("Content-type", "application/json");

            HttpResponse response = null;
            try {
                response = client.execute(patch);
            } catch (IOException e) {
                Log.w(TAG, e);
                return false;
            }

            try {
                String resp = EntityUtils.toString(response.getEntity());
                Log.i(TAG, resp);
            } catch (IOException e) {
                Log.w(TAG, e);
                return false;
            }

            return true;

        }

        protected void onPostExecute(String result) {

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

}
