package pwr.ib.accelerometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class SaveResultActivity extends AppCompatActivity {

    private Double value;
    private String time;
    private EditText etName;
    private EditText etValue;
    private EditText etTime;
    private String TAG = "SAVE_TO_FILE_DEBUG";

    public static final String FOLDERNAME = "mydir";
    public static final String FILENAME = "goniometer_results.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_result);

        Bundle b = getIntent().getExtras();

        if(b != null){
            value = b.getDouble("value");
            time = b.getString("time");
        }

        etName = findViewById(R.id.etName);
        etValue = findViewById(R.id.etValue);
        etTime = findViewById(R.id.etTime);

        etValue.setText(value.toString());
        etTime.setText(time);
    }

    public void saveRecord(View view) {

        String result = (value+"; "+etName.getText()+"; "+time);


        //simple internal write
        try (FileOutputStream os = openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            os.write(result.getBytes());
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        Intent intent = new Intent(SaveResultActivity.this, HistoryActivity.class);
        startActivity(intent);
        finish();

    }
}