package rossrkk.food_alert_android.activity;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import rossrkk.food_alert_android.R;
import rossrkk.food_alert_android.Reference;
import rossrkk.food_alert_android.profile.Profile;
import rossrkk.food_alert_android.profile.ProfileManager;

public class ChooseProfile extends AppCompatActivity {

    public static final String ID_EXTRA = "rossrkk.food_alert_android.PROFILE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_profile);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C1A7B53B5BDF37B0263E126071DF1D81").build();
        mAdView.loadAd(adRequest);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        View view = bottomNavigationView.findViewById(R.id.action_profile);
        view.performClick();

        init();

        updateBackground();

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
        LinearLayout layout = (LinearLayout) findViewById(R.id.choose_profile);
        Reference.updateBackground(layout);

        for (int i = 0; i < ProfileManager.getLength(); i++) {
            int canEat = ProfileManager.getProfile(i).comapreToData(Reference.data, Reference.reconfirm);
            TableRow row = (TableRow)findViewById(i);
            switch (canEat) {
                case Reference.COMPATIBLE:
                    row.setBackgroundColor(Reference.GREEN);
                    break;
                case Reference.INCOMPATIBLE:
                    row.setBackgroundColor(Reference.RED);
                    break;
                case Reference.UNKNOWN:
                    row.setBackgroundColor(Reference.YELLOW);
                    break;
            }
        }
    }

    public void switchToReconfirm() {
        Intent intent = new Intent(this, Reconfirm.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToMain() {
        Intent intent = new Intent(this, Main.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void reload() {
        Intent intent = new Intent(this, ChooseProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToEdit(int id) {
        Intent intent = new Intent(this, EditProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(ID_EXTRA, id);
        startActivity(intent);
    }

    public void addProfile(View view) {
        Intent intent = new Intent(this, EditProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(ID_EXTRA, ProfileManager.getLength());

        new Profile("Enter Name", new int[Reference.binaryFieldNames.length + Reference.tertiaryFieldNames.length]);

        startActivity(intent);
    }

    /**
     * Initialise the activity with a table of all of the options
     */
    public void init() {
        //create a new Table
        TableLayout ll = (TableLayout) findViewById(R.id.table);

        //loop through each intolerance we use
        for (int i = 0; i < ProfileManager.getLength(); i++) {
            //create a new table row
            TableRow row = new TableRow(this);

            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);
            row.setId(i);

            //create a new text view
            TextView tv = new TextView(this);
            tv.setText(ProfileManager.getProfile(i).getName());
            tv.setTextSize(20f);

            //Create the edit button
            Button editButton = new Button(this);
            editButton.setText("Edit");
            editButton.setId(i);
            editButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToEdit(v.getId());
                }
            });

            //Create the edit button
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setId(i);
            deleteButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileManager.deleteProfile(ProfileManager.getProfile(v.getId()));
                    ProfileManager.saveProfiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

                    reload();
                }
            });

            row.addView(tv);
            row.addView(editButton);
            row.addView(deleteButton);

            ll.addView(row);
         }
    }
}
