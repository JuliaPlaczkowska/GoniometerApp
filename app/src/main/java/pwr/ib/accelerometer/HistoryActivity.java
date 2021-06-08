package pwr.ib.accelerometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HistoryActivity extends AppCompatActivity {

    private String TAG = "SAVE_TO_FILE_DEBUG";

    public static final String FOLDERNAME = "mydir";
    public static final String FILENAME = "goniometer_results.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        String resource = " ";
        //simple internal read
        try (FileInputStream is = openFileInput(FILENAME)) {
            byte[] bytes = new byte[2048];
            is.read(bytes);
            is.close();
            String msg = new String(bytes);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            resource = msg;
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        TextView tv = findViewById(R.id.tvTitle);
        tv.setText(resource);
    }
    private class MyAdapter extends BaseAdapter {

        // override other abstract methods here

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText((Integer) getItem(position));
            return convertView;
        }
    }
}