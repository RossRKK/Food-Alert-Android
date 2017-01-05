package rossrkk.food_alert_android.request;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import rossrkk.food_alert_android.MainActivity;
import rossrkk.food_alert_android.Reference;
import rossrkk.food_alert_android.request.JSONify;


/**
 * Created by rossrkk on 15/11/16.
 */

public class Request extends AsyncTask<String, Void, int[]> {
    private String message;
    private String method;
    private MainActivity called;

    private static final String URL = "food-alert.herokuapp.com";
    private static final int PORT = 80;

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
    protected int[] doInBackground(String... json) {
        return request(json[0]);
    }


    public int[] request(String json) {
        int[] out = new int[Reference.fieldNames.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = Reference.UNKNOWN;
        }
        if (method.equalsIgnoreCase("GET")) {
            String urlStr = "http://" + URL + ":" + PORT +  "/" + message;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                out = JSONify.fromJSON(readStream(in));
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (method.equalsIgnoreCase("POST")) {
            try {
                Socket s = new Socket(URL, PORT);

                PrintWriter outputPost = new PrintWriter(s.getOutputStream());
                headers(outputPost);
                System.out.println(json);
                outputPost.print(json);

                outputPost.flush();
                outputPost.close();

                s.close();
            } catch (Exception e) {
                System.out.println("ERROR DURING POST");
                e.printStackTrace();
            }
        }
        return out;
    }

    public void headers(PrintWriter out) {
        // Send the headers
        out.print("POST /" + message + " HTTP/1.1\r\n"); // Version & status code
        out.print("Content-Type: application/JSON\r\n"); // The type of data
        out.print("Connection: close\r\n"); // Will close stream
        out.print("\r\n"); // End of headers
    }

    @Override
    protected void onPostExecute(int[] result) {
        if (called != null) {
            called.setData(result);
        }
    }
}
