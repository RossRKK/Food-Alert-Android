package rossrkk.food_alert_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import rossrkk.food_alert_android.profile.ProfileActivity;
import rossrkk.food_alert_android.request.JSONify;

import static rossrkk.food_alert_android.Reference.YELLOW;

public class MainActivity extends AppCompatActivity {
    public final static String DATA = "rossrkk.food_alert_android.DATA";
    public final static String CAN_EAT = "rossrkk.food_alert_android.CAN_EAT";

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
        //new Thread(new Request(message, "get", this)).start();
        Request r = new Request(message, "get", this);
        r.execute();
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
                layout.setBackgroundColor(YELLOW);
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
}
