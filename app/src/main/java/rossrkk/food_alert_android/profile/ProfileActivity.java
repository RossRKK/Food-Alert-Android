package rossrkk.food_alert_android.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import rossrkk.food_alert_android.Database;
import rossrkk.food_alert_android.R;

public class ProfileActivity extends AppCompatActivity {
    private static final String ANY = "Any amount ";
    private static final String TRACES = "Traces ";
    private static final String NONE = "None ";

    private static final int MAX_NO_OF_CONDITIONS = 100;

    private static final int OPTIONS = 3;

    //get the group ids to clear the radio button ids
    private static final int GROUP_OFFSET = MAX_NO_OF_CONDITIONS + Database.fieldNames.length * MAX_NO_OF_CONDITIONS;

    private int[] profile = new int[Database.fieldNames.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    /**
     * The action that is taken when the submit button is pressed
     * @param view The view object the submit button is in
     */
    public void submit(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sharedPref.edit();
        //submit the fields to the thing
        for (int i = 0; i < Database.fieldNames.length; i++) {
            System.out.println(profile[i]);
            editor.putInt(Database.fieldNames[i], profile[i]);
            editor.commit();
        }

        //change back to the main screen
        Intent intent = new Intent(this, rossrkk.food_alert_android.MainActivity.class);
        startActivity(intent);
    }

    /**
     * Initialise the activity with a table of all of the options
     */
    public void init() {
        //get the preferences handler
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //set the default value
        int defaultValue = Database.UNKNOWN;

        //create a new Table
        TableLayout ll = (TableLayout) findViewById(R.id.table);

        //loop through each intolerance we use
        for (int i = 0; i < Database.fieldNamesFormatted.length; i++) {
            //get the saved value of the profile
            profile[i] = sharedPref.getInt(Database.fieldNames[i], defaultValue);

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


            //turn on the previous settings
            switch (profile[i]) {
                case Database.ANY: any.toggle();
                    break;
                case Database.TRACE: traces.toggle();
                    break;
                case Database.NONE: none.toggle();
                    break;
            }

            //add the views to the row and then add the row
            row.addView(tv);
            row.addView(group);
            ll.addView(row,i);

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
                        case 1: amount = Database.ANY;
                            break;
                        case 2: amount = Database.TRACE;
                            break;
                        case 0: amount = Database.NONE;
                            break;
                    }

                    //set this in the profile array
                    profile[row] = amount;
                }
            });
        }
    }

}
