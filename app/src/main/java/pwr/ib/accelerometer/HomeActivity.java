package pwr.ib.accelerometer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Aktywność służąca jedynie do nawigacji do wykonania pomiaru lub wyświetlenia historii pomiarów za
 * pomocą przycisków.
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }
    public void goToHistory(View view) {
        Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
        startActivity(intent);
        finish();
    }

    public void startMeasurement(View view) {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
