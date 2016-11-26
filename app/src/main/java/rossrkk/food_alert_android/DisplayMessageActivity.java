package rossrkk.food_alert_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    private TextView textView;

    protected static final String ANY = "Contains ";
    protected static final String TRACES = "Traces ";
    protected static final String NONE = "None ";

    private static final String ADD_DATA_TEXT = "Please tell us whether this food contains: ";

    protected static final int MAX_NO_OF_CONDITIONS = 100;

    protected static final int OPTIONS = 3;

    private int[] data;


    //get the group ids to clear the radio button ids
    protected static final int GROUP_OFFSET = MAX_NO_OF_CONDITIONS + Reference.fieldNames.length * MAX_NO_OF_CONDITIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        data = intent.getIntArrayExtra(MainActivity.DATA);
        int canEat = intent.getIntExtra(MainActivity.CAN_EAT, Reference.UNKNOWN);

        LinearLayout layout = (LinearLayout) findViewById(R.id.display_layout);
        switch (canEat) {
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

        textView = (TextView) findViewById(R.id.title);
        textView.setTextSize(32f);
        generateTable();
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

        textView.setText(ADD_DATA_TEXT);

        //loop through each intolerance we use
        for (int i = 0; i < Reference.fieldNamesFormatted.length; i++) {
            if (data[i] == -1) {
                //create a new table row
                TableRow row = new TableRow(this);

                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                //create a new text view
                TextView tv = new TextView(this);
                tv.setText(Reference.fieldNamesFormatted[i]);

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
                        int amount = Reference.UNKNOWN;
                        //re-map the values because currently 2 is traces and 1 is any amount
                        switch (in) {
                            case 1:
                                amount = Reference.ANY;
                                break;
                            case 2:
                                amount = Reference.TRACE;
                                break;
                            case 0:
                                amount = Reference.NONE;
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