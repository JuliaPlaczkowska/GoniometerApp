package pwr.ib.accelerometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HistoryActivity extends AppCompatActivity {


    private String TAG = "SAVE_TO_FILE_DEBUG";


    public static final String FOLDERNAME = "history";
    public static final String FILENAME = "history_results.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + File.separator + FOLDERNAME;
        File subFolder = new File(folder);

        String resource = " ";
        //simple internal read
        try (FileInputStream is = new FileInputStream(new File(subFolder, FILENAME))) {
            byte[] bytes = new byte[2048];
            is.read(bytes);
            is.close();
            String msg = new String(bytes);
            //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            resource =  msg;
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        TextView tv = findViewById(R.id.tvTitle);
        tv.setText("HISTORY");

        TextView sv = findViewById(R.id.textHistory);
        sv.setText(resource);

        Button buttonHome = findViewById(R.id.buttonHome);

    }

    public void goHome(View view) {
        Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}