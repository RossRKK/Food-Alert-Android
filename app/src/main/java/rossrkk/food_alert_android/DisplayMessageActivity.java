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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import rossrkk.food_alert_android.request.JSONify;

import static rossrkk.food_alert_android.Reference.binaryFieldNames;
import static rossrkk.food_alert_android.Reference.tertiaryFieldNames;

public class DisplayMessageActivity extends AppCompatActivity {

    private TextView textView;

    protected static final String ANY = "Contains Non-Trace Amounts";
    protected static final String TRACES = "Contains Traces";
    protected static final String NONE = "Contains None";

    private static final String ADD_DATA_TEXT = "Please tell us what this food contains: ";

    protected static final int MAX_NO_OF_CONDITIONS = 100;

    protected static final int OPTIONS = 3;

    private int[] data;
    private String ean;

    private static int totalLength = tertiaryFieldNames.length + binaryFieldNames.length;

    //get the group ids to clear the radio button ids
    protected static final int GROUP_OFFSET = MAX_NO_OF_CONDITIONS + totalLength * MAX_NO_OF_CONDITIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        data = intent.getIntArrayExtra(MainActivity.DATA);
        ean = intent.getStringExtra(MainActivity.EAN);
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
        String encodedData = JSONify.encode(data);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Reference.BASE_URL + "/" + ean + encodedData;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Initialise the activity with a table of all of the options
     */
    public void generateTable() {
        //create a new Table
        TableLayout ll = (TableLayout) findViewById(R.id.table);

        textView.setText(ADD_DATA_TEXT);

        //loop through each intolerance we use
        for (int i = 0; i < Reference.tertiaryFieldNamesFormatted.length; i++) {
            if (data[i] == Reference.UNKNOWN) {
                //create a new table row
                TableRow row = new TableRow(this);

                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                //create a new text view
                TextView tv = new TextView(this);
                tv.setText(Reference.tertiaryFieldNamesFormatted[i]);

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