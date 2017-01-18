package rossrkk.food_alert_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import rossrkk.food_alert_android.profile.ProfileActivity;
import rossrkk.food_alert_android.request.JSONify;

import static rossrkk.food_alert_android.Reference.binaryFieldNames;
import static rossrkk.food_alert_android.Reference.data;
import static rossrkk.food_alert_android.Reference.tertiaryFieldNames;
import static rossrkk.food_alert_android.Reference.updateBackground;

public class DisplayMessageActivity extends AppCompatActivity {

    protected static final String ANY = "Contains Non-Trace Amounts";
    protected static final String TRACES = "Contains Traces";
    protected static final String NONE = "Contains None";
    protected static final String NEGATIVE = "No ";
    protected static final String POSITIVE = "Yes ";
    protected static final int MAX_NO_OF_CONDITIONS = 100;

    private static int totalLength = tertiaryFieldNames.length + binaryFieldNames.length;
    //get the group ids to clear the radio button ids
    protected static final int GROUP_OFFSET = MAX_NO_OF_CONDITIONS + totalLength * MAX_NO_OF_CONDITIONS;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        boolean auto = intent.getBooleanExtra(MainActivity.AUTO_NAME, false);
        if (auto) {
            TextView title = (TextView)findViewById(R.id.title);
            title.setText(R.string.auto_title);
        }

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.display_layout);
        updateBackground(layout);

        textView = (TextView) findViewById(R.id.title);
        textView.setTextSize(20f);
        EditText nameBox = (EditText)findViewById(R.id.name);
        nameBox.setText(Reference.name);
        generateTable();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                switchToHome();
                                break;
                            case R.id.action_profile:
                                switchToProfile();
                                break;
                            case R.id.action_reconfirm:
                                break;

                        }
                        return true;
                    }
                });
    }

    public void switchToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToHome() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    /**
     * The method run when you press submit
     *
     * @param view
     */
    public void submit(View view) {
        EditText nameBox = (EditText)findViewById(R.id.name);
        String name = "null";
        try {
            name = URLEncoder.encode(nameBox.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error Encoding Name");
        }
        String encodedData = JSONify.encode(name, data);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Reference.BASE_URL + "/" + Reference.ean + encodedData;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

        Reference.ean = null;
        Reference.name = null;
        Reference.canEat = -1;
        Reference.data = null;
        switchToHome();
    }

    /**
     * Initialise the activity with a table of all of the options
     */
    public void generateTable() {
        if (Reference.ean != null) {
            //create a new Table
            TableLayout ll = (TableLayout) findViewById(R.id.table);

            //loop through each intolerance we use
            int index = 0;

            for (int i = 0; i < Reference.binaryFieldNames.length; i++) {
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

                RadioButton yes = new RadioButton(this);
                yes.setText(POSITIVE);

                RadioButton no = new RadioButton(this);
                no.setText(NEGATIVE);

                //add the radio buttons to the group
                group.addView(yes);
                group.addView(no);


                //turn on the previous settings
                switch (data[index]) {
                    case Reference.ANY:
                        no.toggle();
                        break;
                    case Reference.NONE:
                        yes.toggle();
                        break;
                }

                //add the views to the row and then add the row
                row.addView(tv);
                row.addView(group);
                ll.addView(row, index);

                //add listener for the radio button group
                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        //figure out the row the radio button was in
                        int row = group.getId() - GROUP_OFFSET;
                        //figure out the amount this represents
                        String text = ((RadioButton)findViewById(checkedId)).getText().toString();
                        int amount = Reference.UNKNOWN;
                        //re-map the values because currently 2 is traces and 1 is any amount
                        switch (text) {
                            case POSITIVE:
                                amount = Reference.NONE;
                                break;
                            case NEGATIVE:
                                amount = Reference.ANY;
                                break;
                        }
                        //set this in the profile array
                        data[row] = amount;
                    }
                });
                index++;
            }

            for (int i = 0; i < Reference.tertiaryFieldNames.length; i++) {
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
                switch (data[index]) {
                    case Reference.ANY:
                        any.toggle();
                        break;
                    case Reference.TRACE:
                        traces.toggle();
                        break;
                    case Reference.NONE:
                        none.toggle();
                        break;
                }

                //add the views to the row and then add the row
                row.addView(tv);
                row.addView(group);
                ll.addView(row, index);

                //add listener for the radio button group
                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        //figure out the row the radio button was in
                        int row = group.getId() - GROUP_OFFSET;
                        String text = ((RadioButton)findViewById(checkedId)).getText().toString();
                        //figure out the amount this represents
                        int amount = Reference.UNKNOWN;
                        //re-map the values because currently 2 is traces and 1 is any amount
                        switch (text) {
                            case ANY:
                                amount = Reference.ANY;
                                break;
                            case TRACES:
                                amount = Reference.TRACE;
                                break;
                            case NONE:
                                amount = Reference.NONE;
                                break;
                        }
                        //figure out the row the radio butto

                        //set this in the profile array
                        data[row] = amount;
                    }
                });
                index++;
            }
        } else {
            ((TextView)findViewById(R.id.title)).setText("Scan a barcode to edit a products data");
            findViewById(R.id.push_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.name).setVisibility(View.INVISIBLE);
        }
    }
}