package rossrkk.food_alert_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import rossrkk.food_alert_android.profile.ProfileActivity;
import rossrkk.food_alert_android.request.JSONify;
//import rossrkk.food_alert_android.request.Request;


public class MainActivity extends AppCompatActivity {
    public final static String DATA = "rossrkk.food_alert_android.DATA";
    public final static String CAN_EAT = "rossrkk.food_alert_android.CAN_EAT";
    public final static String EAN = "rossrkk.food_alert_android.EAN";

    private int[] profile = new int[Reference.fieldNames.length];
    private int[] data;

    private int canEat;

    private String ean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadProfile();
    }


    public void sendMessage(View view)  {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        ean = editText.getText().toString();
        get(ean);
    }

    public void unknown(int canEat) {
        Intent intent = new Intent(this, rossrkk.food_alert_android.DisplayMessageActivity.class);
        intent.putExtra(EAN, ean);
        intent.putExtra(CAN_EAT, canEat);
        intent.putExtra(DATA, data);
        startActivity(intent);
    }

    public void switchToProfile(View view)  {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void loadProfile() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //set the default value
        int defaultValue = Reference.UNKNOWN;
        for (int i = 0; i < profile.length; i++) {
            //get the saved value of the profile
            profile[i] = sharedPref.getInt(Reference.fieldNames[i], defaultValue);
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

    public void setData(int[] data) {
        this.data = data;
        canEat = compareToProfile();
        if (!isComplete()) {
            unknown(canEat);
        }
    }

    /**
     * Figure out whether this food is compatible with this profile
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


    /*
    Added for barcode scanning
     */

    /**
     * event handler for scan button
     * @param view view of the activity
     */
    public void scanNow(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setResultDisplayDuration(0);
        integrator.setWide();  // Wide scanning rectangle, may work better for 1D barcodes
        //integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /**
     * function handle scan result
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();

            get(scanContent);

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
