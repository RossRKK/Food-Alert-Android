package rossrkk.food_alert_android;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import rossrkk.food_alert_android.request.JSONify;

/**
 * Created by rossrkk on 15/11/16.
 */

public class Request extends AsyncTask<String, Void, int[]> {
    private String message;
    private String method;
    private MainActivity called;

    public  Request(String message, String method, MainActivity called) {
        this.message = message;
        this.method = method;
        this.called = called;
    }

    public String readStream(BufferedReader in) {
        try {
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }

            return lines.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
    }


    @Override
    protected int[] doInBackground(String... urls) {
        return request();
    }


    public int[] request() {
        int[] out = new int[Reference.fieldNames.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = Reference.UNKNOWN;
        }
        if (method.equalsIgnoreCase("GET")) {

            String urlStr = "http://138.251.247.74:8080/" + message;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                out = JSONify.fromJSON(readStream(in));
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    @Override
    protected void onPostExecute(int[] result) {
        called.setData(result);
    }
}
