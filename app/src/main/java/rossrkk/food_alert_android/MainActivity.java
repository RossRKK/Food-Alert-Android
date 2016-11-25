package rossrkk.food_alert_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import rossrkk.food_alert_android.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "rossrkk.food_alert_android.EAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view)  {
        Intent intent = new Intent(this, rossrkk.food_alert_android.DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void switchToProfile(View view)  {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
