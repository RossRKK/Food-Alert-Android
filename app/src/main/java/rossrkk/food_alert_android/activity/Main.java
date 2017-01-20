package rossrkk.food_alert_android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rossrkk.food_alert_android.R;
import rossrkk.food_alert_android.Reference;
import rossrkk.food_alert_android.profile.ProfileManager;
import rossrkk.food_alert_android.request.DataObj;
import rossrkk.food_alert_android.request.JSONify;


public class Main extends AppCompatActivity {
    public static String AUTO_NAME = "rossrkk.food-alert.AUTO";

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
        ping(); //ping the server in case it needs to wake up
        ProfileManager.loadProfiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        //setup the ad
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5158131601481362/3995936333");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C1A7B53B5BDF37B0263E126071DF1D81").build();
        mAdView.loadAd(adRequest);

        ProfileManager.loadProfiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        //for barcode scanning
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);

        //the scanned data
        lastText = null;
        Reference.canEat = -1;
        Reference.reconfirm = false;
        Reference.ean = null;
        Reference.data = null;
        updateBackground();

        beepManager = new BeepManager(this);

        ((TextView)findViewById(R.id.name)).setText(Reference.name);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        View view = bottomNavigationView.findViewById(R.id.action_home);
        view.performClick();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_profile:
                                switchToProfile();
                                break;
                            case R.id.action_reconfirm:
                                reconfirm(false);
                                break;
                            case R.id.action_about:
                                about();
                                break;

                        }
                        return false;
                    }
                });
    }

    private void updateBackground() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.main_layout);
        Reference.updateBackground(layout);
    }

    public void sendMessage(View view) {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        EditText editText = (EditText) findViewById(R.id.edit_message);
        Reference.ean = editText.getText().toString();
        get(Reference.ean);
    }

    public void reconfirm(boolean automatic) {
        Intent intent = new Intent(this, Reconfirm.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(AUTO_NAME, automatic);
        startActivity(intent);
    }

    public void switchToProfile() {
        Intent intent = new Intent(this, ChooseProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void about() {
        Intent intent = new Intent(this, About.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void setName(String name) {
        ((TextView)findViewById(R.id.name)).setText(name);
    }

    public void ping() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Reference.BASE_URL;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        setName("Ready");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setName("Error Communicating with Server");
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(30),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
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
                        // Display the first 500 characters of the response string.
                        setData(JSONify.fromJSON(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setName("Error Communicating with Server");
                System.out.println("GET request error");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void setData(DataObj data) {
        Reference.data = data.getData();
        Reference.name = data.getName();
        Reference.reconfirm = data.getReconfirm();
        Reference.canEat = ProfileManager.compareToProfiles();
        ((TextView)findViewById(R.id.name)).setText(Reference.name);
        updateBackground();
        if (data.getReconfirm() || !isComplete()) {
            reconfirm(true);
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
