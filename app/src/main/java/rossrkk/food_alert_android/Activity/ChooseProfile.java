package rossrkk.food_alert_android.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import rossrkk.food_alert_android.R;
import rossrkk.food_alert_android.Reference;

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
}
