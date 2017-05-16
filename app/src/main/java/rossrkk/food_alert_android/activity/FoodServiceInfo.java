package rossrkk.food_alert_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import rossrkk.food_alert_android.R;
import rossrkk.food_alert_android.Reference;
import rossrkk.food_alert_android.profile.ProfileManager;
import rossrkk.food_alert_android.request.Item;
import rossrkk.food_alert_android.request.Service;


import static rossrkk.food_alert_android.Reference.updateBackground;

public class FoodServiceInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_service_info);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C1A7B53B5BDF37B0263E126071DF1D81").build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        int index = intent.getIntExtra(SearchResults.INDEX, 0);

        displayResults(index);

        LinearLayout layout = (LinearLayout) findViewById(R.id.display_layout);
        updateBackground(layout);

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

    private void displayResults(int index) {
        Service result = SearchResults.results.get(index);
        ArrayList<Item> menu = result.getMenu();

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(result.getName());

        TextView description = (TextView) findViewById(R.id.desc);
        description.setText(result.getDescription());

        LinearLayout ll = (LinearLayout) findViewById(R.id.list);

        for (int i = 0; i < menu.size(); i++) {
            //print the item if it's compatible with the master profile
            if (ProfileManager.getProfile(ProfileManager.masterProfile).comapreToData(result.getMenu().get(i).getData(), false) == Reference.COMPATIBLE) {
                //create a new table row

                /*TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);
                row.setId(i);*/

                //create a new text view
                TextView tv = new TextView(this);
                tv.setText(menu.get(i).getName());
                tv.setTextSize(20f);
                ll.addView(tv);

                TextView desc = new TextView(this);
                desc.setText(menu.get(i).getDescription());
                desc.setTextSize(12f);
                ll.addView(desc);

                //ll.addView(row);
            }
        }
    }

    public void about() {
        Intent intent = new Intent(this, About.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToProfile() {
        Intent intent = new Intent(this, ChooseProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToHome() {
        Intent intent = new Intent(this, Main.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToReconfirm() {
        Intent intent = new Intent(this, Reconfirm.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
