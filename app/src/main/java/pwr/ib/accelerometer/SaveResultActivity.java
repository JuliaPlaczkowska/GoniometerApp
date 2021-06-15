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
import java.text.DecimalFormat;
import java.time.LocalDateTime;

public class SaveResultActivity extends AppCompatActivity {

    private String value;
    private String time;
    private EditText etName;
    private EditText etValue;
    private EditText etTime;
    private String TAG = "SAVE_TO_FILE_DEBUG";

    public static final String FOLDERNAME = "history";
    public static final String FILENAME = "history_results.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_result);

        Bundle b = getIntent().getExtras();

        DecimalFormat dec = new DecimalFormat("#0.00");

        if(b != null){
            value = dec.format(b.getDouble("value"));
            time = b.getString("time");
        }



        etName = findViewById(R.id.etName);
        etValue = findViewById(R.id.etValue);
        etTime = findViewById(R.id.etTime);

        etValue.setText(value);
        etTime.setText(time);
    }

    public void saveRecord(View view) {

        String result = (etName.getText()+": "+value+" ° "+time+"\n");

        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + File.separator + FOLDERNAME;
        File subFolder = new File(folder);

        if (!subFolder.exists())
            subFolder.mkdirs();

        //simple internal write
        try (FileOutputStream os = new FileOutputStream(new File(subFolder, FILENAME), true)) {
            os.write(result.getBytes());
            Toast.makeText(this, "Save OK", Toast.LENGTH_LONG).show();
            Log.d(TAG, "save ok. "+result+"saved");
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