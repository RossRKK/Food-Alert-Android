package rossrkk.food_alert_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import rossrkk.food_alert_android.profile.ProfileActivity;
import rossrkk.food_alert_android.request.DataObj;
import rossrkk.food_alert_android.request.JSONify;


public class MainActivity extends AppCompatActivity {
    public final static String DATA = "rossrkk.food_alert_android.DATA";
    public final static String CAN_EAT = "rossrkk.food_alert_android.CAN_EAT";
    public final static String EAN = "rossrkk.food_alert_android.EAN";
    public final static String NAME = "rossrkk.food_alert_android.NAME";

    private int[] profile = new int[Reference.tertiaryFieldNames.length + Reference.binaryFieldNames.length];
    private int[] data;

    private int canEat;
    private String name;

    private String ean;
    /**
     * Code that handles the barcodde scanner
     */
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();

            ean = lastText;
            get(ean);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadProfile();

        //for barcode scanning
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        ean = editText.getText().toString();
        get(ean);
    }

    public void unknown(int canEat, String name) {
        Intent intent = new Intent(this, rossrkk.food_alert_android.DisplayMessageActivity.class);
        intent.putExtra(EAN, ean);
        intent.putExtra(CAN_EAT, canEat);
        intent.putExtra(DATA, data);
        intent.putExtra(NAME, name);
        startActivity(intent);
    }

    public void reconfirm(View view) {
        Intent intent = new Intent(this, rossrkk.food_alert_android.DisplayMessageActivity.class);
        intent.putExtra(EAN, ean);
        intent.putExtra(CAN_EAT, canEat);
        intent.putExtra(DATA, data);
        intent.putExtra(NAME, name);
        startActivity(intent);
    }

    public void switchToProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void loadProfile() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //set the default value
        int defaultValue = Reference.UNKNOWN;
        int index = 0;

        for (int i = 0; i < Reference.binaryFieldNames.length; i++) {
            profile[index] = sharedPref.getInt(Reference.binaryFieldNames[i], defaultValue);
            index++;
        }

        for (int i = 0; i < Reference.tertiaryFieldNames.length; i++) {
            //get the saved value of the profile
            profile[index] = sharedPref.getInt(Reference.tertiaryFieldNames[i], defaultValue);
            index++;
        }
    }

    public void get(String message) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Reference.BASE_URL + "/" + message;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        // Display the first 500 characters of the response string.
                        setData(JSONify.fromJSON(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO add an alert here
                System.out.println("GET request error");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void setData(DataObj data) {
        this.data = data.getData();
        this.name = data.getName();
        canEat = compareToProfile();
        ((TextView)findViewById(R.id.name)).setText(name);
        if (!isComplete()) {
            unknown(canEat, name);
        }
    }

    /**
     * Figure out whether this food is compatible with this profile
     *
     * @return 1 if compatible, 0 if not and -1 if unsure
     */
    private int compareToProfile() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout);
        for (int i = 0; i < profile.length; i++) {
            //if the person is intolerant and the data is unknown return unknown
            if ((profile[i] == Reference.NONE || profile[i] == Reference.TRACE) && data[i] == Reference.UNKNOWN) {
                layout.setBackgroundColor(Reference.YELLOW);
                return Reference.UNKNOWN;
            }

            //if the data matches the profiles tolerances
            System.out.println(profile[i] == Reference.NONE);
            if (profile[i] == Reference.NONE && (data[i] == Reference.TRACE || data[i] == Reference.ANY)) {
                layout.setBackgroundColor(Reference.RED);
                return Reference.INCOMPATIBLE;
            } else if (profile[i] == Reference.TRACE && data[i] == Reference.ANY) {
                layout.setBackgroundColor(Reference.RED);
                return Reference.INCOMPATIBLE;
            }
        }
        layout.setBackgroundColor(Reference.GREEN);
        return Reference.COMPATIBLE;
    }

    /**
     * Find out if the data is complete
     *
     * @return True if the data is complete else false
     */
    public boolean isComplete() {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == Reference.UNKNOWN) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
