package rossrkk.food_alert_android.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import rossrkk.food_alert_android.R;
import rossrkk.food_alert_android.Reference;
import rossrkk.food_alert_android.profile.Profile;
import rossrkk.food_alert_android.profile.ProfileManager;

public class EditProfile extends AppCompatActivity {
    protected static final String ANY = "Any amount ";
    protected static final String TRACES = "Traces ";
    protected static final String NONE = "None ";

    protected static final String NEGATIVE = "No ";
    protected static final String POSITIVE = "Yes ";

    protected static final int MAX_NO_OF_CONDITIONS = 100;

    private int id;

    private Profile p;


    private static int totalLength = Reference.tertiaryFieldNames.length + Reference.binaryFieldNames.length;
    //get the group ids to clear the radio button ids
    protected static final int GROUP_OFFSET = MAX_NO_OF_CONDITIONS + totalLength * MAX_NO_OF_CONDITIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        Intent intent = getIntent();
        id = intent.getIntExtra(ChooseProfile.ID_EXTRA, -1);

        p = ProfileManager.getProfile(id);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C1A7B53B5BDF37B0263E126071DF1D81").build();
        mAdView.loadAd(adRequest);

        init();
        updateBackground();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        View view = bottomNavigationView.findViewById(R.id.action_profile);
        view.performClick();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                switchToMain();
                                break;
                            case R.id.action_profile:
                                break;
                            case R.id.action_reconfirm:
                                switchToReconfirm();
                                break;
                            case R.id.action_about:
                                about();
                                break;

                        }
                        return false;
                    }
                });
    }

    public void about() {
        Intent intent = new Intent(this, About.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void updateBackground() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.profile_layout);
        Reference.updateBackground(layout);
    }

    public void switchToReconfirm() {
        Intent intent = new Intent(this, Reconfirm.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToMain() {
        Intent intent = new Intent(this, Main.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    /**
     * The action that is taken when the submit button is pressed
     */
    public void submit() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ProfileManager.saveProfiles(sharedPref);

        Reference.canEat = ProfileManager.compareToProfiles();
        updateBackground();
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
            //get the saved value of the p.getSettings()
            p.getSettings()[index] = sharedPref.getInt(Reference.binaryFieldNames[i], defaultValue);

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


            //turn on the previous p.getSettings()
            switch (p.getSettings()[index]) {
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
                    //set this in the p.getSettings() array
                    p.getSettings()[row] = amount;
                    submit();
                }
            });
            index++;
        }

        for (int i = 0; i < Reference.tertiaryFieldNames.length; i++) {
            //get the saved value of the p.getSettings()
            p.getSettings()[index] = sharedPref.getInt(Reference.tertiaryFieldNames[i], defaultValue);

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


            //turn on the previous p.getSettings()
            switch (p.getSettings()[index]) {
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

                    //set this in the p.getSettings() array
                    p.getSettings()[row] = amount;
                    submit();
                }
            });
            index++;
        }
    }

}
