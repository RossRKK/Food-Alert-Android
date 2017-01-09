package rossrkk.food_alert_android.profile;

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

import rossrkk.food_alert_android.Reference;
import rossrkk.food_alert_android.R;

public class ProfileActivity extends AppCompatActivity {
    protected static final String ANY = "Any amount ";
    protected static final String TRACES = "Traces ";
    protected static final String NONE = "None ";

    protected static final String NEGATIVE = "No ";
    protected static final String POSITIVE = "Yes ";

    protected static final int MAX_NO_OF_CONDITIONS = 100;

    protected static final int OPTIONS = 3;


    private static int totalLength = Reference.tertiaryFieldNames.length + Reference.binaryFieldNames.length;
    //get the group ids to clear the radio button ids
    protected static final int GROUP_OFFSET = MAX_NO_OF_CONDITIONS + totalLength * MAX_NO_OF_CONDITIONS;

    protected int[] profile = new int[totalLength];

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
        int index = 0;

        for (int i = 0; i < Reference.binaryFieldNames.length; i++) {
            editor.putInt(Reference.binaryFieldNames[i], profile[index]);
            editor.commit();
            index++;
        }

        for (int i = 0; i < Reference.tertiaryFieldNames.length; i++) {
            editor.putInt(Reference.tertiaryFieldNames[i], profile[index]);
            editor.commit();
            index++;
        }

        //change back to the main screen
        Intent intent = new Intent(this, rossrkk.food_alert_android.MainActivity.class);
        //startActivity(intent);
    }

    /**
     * Initialise the activity with a table of all of the options
     */
    public void init() {
        //get the preferences handler
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //set the default value
        int defaultValue = Reference.UNKNOWN;

        //create a new Table
        TableLayout ll = (TableLayout) findViewById(R.id.table);

        //loop through each intolerance we use
        int index = 0;
        for (int i = 0; i < Reference.binaryFieldNames.length; i++) {
            //get the saved value of the profile
            profile[index] = sharedPref.getInt(Reference.binaryFieldNames[i], defaultValue);

            //create a new table row
            TableRow row = new TableRow(this);

            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);

            //create a new text view
            TextView tv = new TextView(this);
            tv.setText(Reference.binaryFieldNamesFormatted[i]);

            //setup the radio buttons
            RadioGroup group = new RadioGroup(this);
            group.setId(index + GROUP_OFFSET);

            RadioButton any = new RadioButton(this);
            any.setText(POSITIVE);

            RadioButton none = new RadioButton(this);
            none.setText(NEGATIVE);

            //add the radio buttons to the group
            group.addView(any);
            group.addView(none);


            //turn on the previous settings
            switch (profile[index]) {
                case Reference.NONE: any.toggle();
                    break;
                case Reference.ANY: none.toggle();
                    break;
            }

            //add the views to the row and then add the row
            row.addView(tv);
            row.addView(group);
            ll.addView(row,index);

            //add listener for the radio button group
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //figure out the row the radio button was in
                    int row = group.getId() - GROUP_OFFSET;
                    //figure out the amount this represents
                    int in = checkedId % 2;
                    int amount = Reference.UNKNOWN;
                    //re-map the values because currently 2 is traces and 1 is any amount
                    switch (in) {
                        case 1: amount = Reference.NONE;
                            break;
                        case 0: amount = Reference.ANY;
                            break;
                    }
                    //set this in the profile array
                    profile[row] = amount;
                }
            });
            index++;
        }

        for (int i = 0; i < Reference.tertiaryFieldNames.length; i++) {
            //get the saved value of the profile
            profile[index] = sharedPref.getInt(Reference.tertiaryFieldNames[i], defaultValue);

            //create a new table row
            TableRow row = new TableRow(this);

            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            //create a new text view
            TextView tv = new TextView(this);
            tv.setText(Reference.tertiaryFieldNamesFormatted[i]);

            //setup the radio buttons
            RadioGroup group = new RadioGroup(this);
            group.setId(index + GROUP_OFFSET);


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
            switch (profile[index]) {
                case Reference.ANY: any.toggle();
                    break;
                case Reference.TRACE: traces.toggle();
                    break;
                case Reference.NONE: none.toggle();
                    break;
            }

            //add the views to the row and then add the row
            row.addView(tv);
            row.addView(group);
            ll.addView(row,index);

            //add listener for the radio button group
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //figure out the row the radio button was in
                    int row = group.getId() - GROUP_OFFSET;

                    //figure out the amount this represents
                    int in = (checkedId - (Reference.binaryFieldNames.length * 2)) % OPTIONS;
                    int amount = Reference.UNKNOWN;
                    //re-map the values because currently 2 is traces and 1 is any amount
                    switch (in) {
                        case 1: amount = Reference.ANY;
                            break;
                        case 2: amount = Reference.TRACE;
                            break;
                        case 0: amount = Reference.NONE;
                            break;
                    }
                    //figure out the row the radio butto

                    //set this in the profile array
                    profile[row] = amount;
                }
            });
            index++;
        }
    }

}
