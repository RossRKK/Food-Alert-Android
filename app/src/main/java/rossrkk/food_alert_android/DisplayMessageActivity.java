package rossrkk.food_alert_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import rossrkk.food_alert_android.request.Request;

public class DisplayMessageActivity extends AppCompatActivity {

    private TextView textView;

    private int[] profile = new int[Database.fieldNames.length];
    private int[] data;

    protected static final String ANY = "Contains ";
    protected static final String TRACES = "Traces ";
    protected static final String NONE = "None ";

    private static final String ADD_DATA_TEXT = "Please tell us whether this food contains: ";

    protected static final int MAX_NO_OF_CONDITIONS = 100;

    protected static final int OPTIONS = 3;

    private static final int RED = Color.rgb(255, 84, 104);
    private static final int GREEN = Color.rgb(77, 250, 144);
    private static final int YELLOW = Color.rgb(250, 190, 77);


    //get the group ids to clear the radio button ids
    protected static final int GROUP_OFFSET = MAX_NO_OF_CONDITIONS + Database.fieldNames.length * MAX_NO_OF_CONDITIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = "Please Wait...";
        textView = (TextView) findViewById(R.id.title);
        textView.setTextSize(32f);
        textView.setText(message);
        //textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

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
        if (!isComplete()) {
            generateTable();
        } else {
            Button button = (Button) findViewById(R.id.push_button);
            button.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Figure out whether this food is compatible with this profile
     * @return 1 if compatible, 0 if not and -1 if unsure
     */
    private int compareToProfile() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.display_layout);
        for (int i = 0; i < profile.length; i++) {
            //if the person is intolerant and the data is unknown return unknown
            if ((profile[i] == Database.NONE || profile[i] == Database.TRACE) && data[i] == Database.UNKNOWN) {
                layout.setBackgroundColor(YELLOW);
                return Database.UNKNOWN;
            }

            //if the data matches the profiles tolerances
            if (profile[i] == Database.NONE && (data[i] == Database.TRACE || data[i] == Database.ANY)) {
                layout.setBackgroundColor(RED);
                return Database.INCOMPATIBLE;
            } else if (profile[i] == Database.TRACE && data[i] == Database.ANY) {
                layout.setBackgroundColor(RED);
                return Database.INCOMPATIBLE;
            }
        }
        layout.setBackgroundColor(GREEN);
        return Database.COMPATIBLE;
    }

    /**
     * Find out if the data is complete
     * @return True if the data is complete else false
     */
    public boolean isComplete() {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * The method run when you press submit
      * @param view
     */
    public void submit(View view) {
        //TODO send the push request
    }

    /**
     * Initialise the activity with a table of all of the options
     */
    public void generateTable() {
        //create a new Table
        TableLayout ll = (TableLayout) findViewById(R.id.table);

        textView.setText(textView.getText() + "\n\n" + ADD_DATA_TEXT);

        //loop through each intolerance we use
        for (int i = 0; i < Database.fieldNamesFormatted.length; i++) {
            if (data[i] == -1) {
                //create a new table row
                TableRow row = new TableRow(this);

                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                //create a new text view
                TextView tv = new TextView(this);
                tv.setText(Database.fieldNamesFormatted[i]);

                //setup the radio buttons
                RadioGroup group = new RadioGroup(this);
                group.setId(i + GROUP_OFFSET);

                RadioButton any = new RadioButton(this);
                any.setText(ANY);
                RadioButton traces = new RadioButton(this);
                traces.setText(TRACES);
                RadioButton none = new RadioButton(this);
                none.setText(NONE);

                //add the radio buttons to the group
                group.addView(any);
                group.addView(traces);
                group.addView(none);

                //add the views to the row and then add the row
                row.addView(tv);
                row.addView(group);
                ll.addView(row, i);

                //add listener for the radio button group
                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        //figure out the row the radio button was in
                        int row = group.getId() - GROUP_OFFSET;

                        //figure out the amount this represents
                        int in = checkedId % OPTIONS;
                        int amount = Database.UNKNOWN;
                        //re-map the values because currently 2 is traces and 1 is any amount
                        switch (in) {
                            case 1:
                                amount = Database.ANY;
                                break;
                            case 2:
                                amount = Database.TRACE;
                                break;
                            case 0:
                                amount = Database.NONE;
                                break;
                        }

                        //set this in the data array
                        data[row] = amount;
                    }
                });
            }
        }
    }
}