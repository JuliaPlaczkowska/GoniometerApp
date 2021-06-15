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

    /**
     * nazwa folderu w którym zostanie zapisany plik tekstowy
     */
    public static final String FOLDERNAME = "history";
    /**
     * nazwa pliku tekstowego
     */
    public static final String FILENAME = "history_results.txt";

    /**
     * W momencie tworzenia aktywności zapisywania pomiaru zostanie wyświetlona wartość pomiaru w
     * stopniach oraz data i godzina wykonania pomiaru.
     * @param savedInstanceState
     */
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

    /**
     * Metoda onClick dla przycisku SUBMIT. W tej metodzie parametry pomiaru zostają sformatowane
     * w sposób czytelny dla użytkownika oraz zapisane w pliku tekstowym w lokalizacji określonej
     * przez zmienne globalne FOLDERNAME i FILENAME.
     * @param view
     */
    public void saveRecord(View view) {

        String result = (etName.getText()+": "+value+" ° "+time+"\n");

        /**
         * pobranie ścieżki lokalzacji pliku
         */
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + File.separator + FOLDERNAME;
        File subFolder = new File(folder);

        /**
         * jeśli docelowy plik nie istnieje, to zostaje utworzony
         */
        if (!subFolder.exists())
            subFolder.mkdirs();

        /**
         * zapis do pliku za pomocą obiektu FileOutputStream. Pierwszy parametr obiektu określa
         * ścieżkę i nazwę pliku, drugi to zmienna logiczna, która - jeśli ustawiona - informuje,
         * że pierwotna treść pliku ma zostać nie zmieniona, a kolejne elementy mają być dopisywane.
         * Po poprawnym zapisaniu zostaje wyświetlony komunikat dla użytkownika "Save OK"
         */
        try (FileOutputStream os = new FileOutputStream(new File(subFolder, FILENAME), true)) {
            os.write(result.getBytes());
            Toast.makeText(this, "Save OK", Toast.LENGTH_LONG).show();
            Log.d(TAG, "save ok. "+result+"saved");
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        /**
         * przejdź do przeglądania historii
         */
        Intent intent = new Intent(SaveResultActivity.this, HistoryActivity.class);
        startActivity(intent);
        finish();

    }
}