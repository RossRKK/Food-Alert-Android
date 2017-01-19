package rossrkk.food_alert_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C1A7B53B5BDF37B0263E126071DF1D81").build();
        mAdView.loadAd(adRequest);

        TextView dataPolicy = (TextView) findViewById(R.id.data_policy);
        dataPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        updateBackground();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        View view = bottomNavigationView.findViewById(R.id.action_about);
        view.performClick();;

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
        Intent intent = new Intent(this, AboutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void updateBackground() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.about_layout);
        Reference.updateBackground(layout);
    }

    public void switchToReconfirm() {
        Intent intent = new Intent(this, rossrkk.food_alert_android.DisplayMessageActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void switchToMain() {
        Intent intent = new Intent(this, rossrkk.food_alert_android.MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
