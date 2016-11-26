package rossrkk.food_alert_android.request;

import android.provider.ContactsContract;

import rossrkk.food_alert_android.Database;
import rossrkk.food_alert_android.DisplayMessageActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by rossrkk on 15/11/16.
 */

public class Request implements Runnable {
    private String message;
    private String method;
    private DisplayMessageActivity called;

    public  Request(String message, String method, DisplayMessageActivity called) {
        this.message = message;
        this.method = method;
        this.called = called;
    }

    @Override
    public void run() {
        if (method.equalsIgnoreCase("GET")) {
            int[] out = new int[Database.fieldNames.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = Database.UNKNOWN;
            }
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
            //called.setText(out);
            called.setData(out);
        }
    }

    public String readStream(BufferedReader in) {
        try {
            String out = "";
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
}
