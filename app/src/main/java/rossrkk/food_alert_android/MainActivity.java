package rossrkk.food_alert_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

            Reference.ean = lastText;
            get(Reference.ean);
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

        ((TextView)findViewById(R.id.name)).setText(Reference.name);
        updateBackground();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_profile:
                                switchToProfile();
                                break;
                            case R.id.action_reconfirm:
                                reconfirm();
                                break;

                        }
                        return true;
                    }
                });
    }

    private void updateBackground() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_layout);
        switch (Reference.canEat) {
            case Reference.COMPATIBLE:
                layout.setBackgroundColor(Reference.GREEN);
                break;
            case Reference.INCOMPATIBLE:
                layout.setBackgroundColor(Reference.RED);
                break;
            case Reference.UNKNOWN:
                layout.setBackgroundColor(Reference.YELLOW);
                break;
        }
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        Reference.ean = editText.getText().toString();
        get(Reference.ean);
    }

    public void reconfirm() {
        Intent intent = new Intent(this, rossrkk.food_alert_android.DisplayMessageActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void loadProfile() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //set the default value
        int defaultValue = Reference.UNKNOWN;
        int index = 0;

        for (int i = 0; i < Reference.binaryFieldNames.length; i++) {
            Reference.profile[index] = sharedPref.getInt(Reference.binaryFieldNames[i], defaultValue);
            index++;
        }

        for (int i = 0; i < Reference.tertiaryFieldNames.length; i++) {
            //get the saved value of the profile
            Reference.profile[index] = sharedPref.getInt(Reference.tertiaryFieldNames[i], defaultValue);
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
        Reference.data = data.getData();
        Reference.name = data.getName();
        Reference.canEat = Reference.compareToProfile();
        ((TextView)findViewById(R.id.name)).setText(Reference.name);
        updateBackground();
        if (!isComplete()) {
            reconfirm();
        }
    }

    /**
     * Find out if the data is complete
     *
     * @return True if the data is complete else false
     */
    public boolean isComplete() {
        for (int i = 0; i < Reference.data.length; i++) {
            if (Reference.data[i] == Reference.UNKNOWN) {
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
