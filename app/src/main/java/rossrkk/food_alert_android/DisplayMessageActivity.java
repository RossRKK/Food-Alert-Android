package rossrkk.food_alert_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import rossrkk.food_alert_android.profile.ProfileActivity;
import rossrkk.food_alert_android.request.Request;

public class DisplayMessageActivity extends AppCompatActivity {

    private TextView textView;
    private ViewGroup layout;

    private int[] profile = new int[Database.fieldNames.length];
    private int[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = "Please Wait...";
        textView = new TextView(this);
        textView.setTextSize(16f);
        textView.setText(message);
        layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);
        get(intent.getStringExtra(MainActivity.EXTRA_MESSAGE));
        loadProfile();
    }

    public void get(String message) {
        new Thread(new Request(message, "get", this)).start();
    }

    //allow the request class to set the text displayed in this view when it gets a response
    public void setText(int canEat) {
        String text;

        switch (canEat) {
            case Database.COMPATIBLE:
                text = "You can eat this";
                break;
            case Database.INCOMPATIBLE:
                text = "You can't eat this";
                break;
            case Database.UNKNOWN:
                text = "Unknown, you might be able to eat this";
                break;
            default:
                text = "Something went wrong, please contact the developer";
                break;
        }

        textView.setText(text);
    }

    public void loadProfile() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //set the default value
        int defaultValue = Database.UNKNOWN;
        for (int i = 0; i < profile.length; i++) {
            //get the saved value of the profile
            profile[i] = sharedPref.getInt(Database.fieldNames[i], defaultValue);
        }
    }

    public void setData(int[] data) {
        this.data = data;
        setText(compareToProfile());
    }

    /**
     * Figure out whether this food is compatible with this profile
     * @return 1 if compatible, 0 if not and -1 if unsure
     */
    private int compareToProfile() {
        for (int i = 0; i < profile.length; i++) {
            System.out.println(Database.fieldNamesFormatted[i] + " Profile: " + profile[i] + " Data: " + data[i]);
            //if the person is intolerant and the data is unknown return unknown
            if ((profile[i] == Database.NONE || profile[i] == Database.TRACE) && data[i] == Database.UNKNOWN) {
                return Database.UNKNOWN;
            }

            //if the data matches the profiles tolerances
            if (profile[i] == Database.NONE && (data[i] == Database.TRACE || data[i] == Database.ANY)) {
                return Database.INCOMPATIBLE;
            } else if (profile[i] == Database.TRACE && data[i] == Database.ANY) {
                return Database.INCOMPATIBLE;
            }
        }
        return Database.COMPATIBLE;
    }
}