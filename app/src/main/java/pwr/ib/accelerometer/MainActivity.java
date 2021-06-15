package pwr.ib.accelerometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements SensorEventListener,
        GestureDetector.OnGestureListener {

    private static final String TAG = "MainActivity";


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean secondMeasurement;
    private boolean finished;
    private RectF rect;
    private int rotY;
    private int rotX;
    private int rotY2;
    private int rotX2;
    private double result;

    CustomDrawableView mCustomDrawableView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();
    public static double x;
    public static double y;

    private int buttonTop = 50;
    private int buttonDown = 250;
    private int buttonLeft = 50;
    private int buttonRight = 250;

    private GestureDetectorCompat mDetector;


    /**
     * W momencie stworzenia aktywności inicjalizowany jest SensorManager dla urządzenia na którym
     * działa aplikacja. SensorManager służy do pobrania informacji o wybranych czujnikach ( w naszym
     * przypadku będzie to akcelerometr ) i zainicjalizowania metod nasłuchujących. Następnie
     * utworzony jest CustomDrawableView, na którym będzie rysowany symulator gonimetru. Kolejnym
     * krokiem jest utworzenie obiektu klasy  GestureDetectorCompat, który będzie nasłuchiwał
     * wydarzeń kliknięcia na ekran za pomocą wbudowanych metod. Pozwoli także na odczytywanie
     * takich informacji jak współrzędne kliknięcia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        Log.d(TAG, "onCreate: Register accelerometer listener");

        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);

        secondMeasurement = false;
        mDetector = new GestureDetectorCompat(this, this);
    }

    /**
     * Funkcja przesyłająca wartość wykonanego pomiaru w stopniach wraz z datą i godziną wykonania
     * do aktywności SaveResultActivity, gdzie, po podaniu nazwy pomiaru i zatwierdzeniu, zostanie
     * zapisany do pliku tekstowego w poamięci urządzenia.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveResults() {
        Intent intent = new Intent(MainActivity.this, SaveResultActivity.class);
        Bundle b = new Bundle();
        b.putDouble("value", result);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = LocalDateTime.now().format(formatter);

        b.putString("time", formatDateTime);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda nasłuchująca zmiany położenia urządzenia za pomocą wartości mierzonych przez sensor,
     * czyli składowych sił działających na urządzenie (w pozycji nieruchomej jest to w przybliżeniu
     * siła grawitacji) na kierunkach x, y i z. Celem aplikacji jest określenie kąta bocznej
     * krawędzi telefonu. w tym celu aplikacja korzysta tylko z dwóch składowych -
     * równologłych do ekranu urządzenia, czyli x i y, które są uaktualniane w tej metodzie
     * i zapisywane do zmiennych globalnych.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        if (event.values[1] != 0)
            y = event.values[1];
        else
            y = 0.01;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Metoda nasłuchująca dotknięcia ekranu urządzenia
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (this.mDetector.onTouchEvent(e)) {
            return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }


    /**
     * Metoda, nasłuchująca pojedyńczego kliknięcia w ekran. Gdy takie kliknięcie zostanie
     * zarejestrowane, następuje zmodyfikowanie flag globalnych w celu określenia etapu pomiaru -
     * pierwsze kliknięcie w ekran po uruchomieniu aktywności zapisuje pierwszą płaszczyzne, wówczas
     * zostaje aktywowana flaga secondMeasurement, oznaczająca że drugi pomiar jest w toku. Kolejny
     * klik zapisuje pomiar drugiej płaszczyzny i zostaje ustawiona flaga finished - czyli oba
     * pomiary ukończone. W tym momencie użytkownik ma możliwość zapisania pomiaru poprzez
     * kliknięcie na ekran w obszarze zielonego kwadratu z namisem SAVE - jego położenie jest
     * określone za pomocą następującego warunku:
     * (currX <= (buttonRight + 300) && currX >= buttonLeft && currY >= buttonTop && currY <= (buttonDown + 300))
     * Jeżeli użytkownik kliknie w dowolnym miejscu niespełniającym tego warunku pomiar zostanie
     * anulowany i nie będzie możliwości zapisania go do pliku. Wtedy aktywność wraca do stanu
     * jak przy jej uruchomieniu.
     * @param e - pojedyńcze kliknięcie na ekran
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        int currX = Math.round(e.getX());
        int currY = Math.round(e.getY());

        if (!finished) {
            if (secondMeasurement) {
                finished = true;
            } else {
                secondMeasurement = true;
                Toast.makeText(this, "Click again to finish measurement", Toast.LENGTH_SHORT).show();
            }
        } else {

            if (currX <= (buttonRight + 300) && currX >= buttonLeft && currY >= buttonTop && currY <= (buttonDown + 300)) {
                saveResults();
            } else {
                finished = false;
                secondMeasurement = false;
            }
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }


    public class CustomDrawableView extends View {
        static final int width = 150;
        static final int height = 150;

        private int viewWidth;
        private int viewHeight;


        /**
         * Metoda pozwala pobrać wymiary ekranu i ustawia je jako zmienne globalne
         * @param w
         * @param h
         * @param oldw
         * @param oldh
         */
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            viewWidth = w;
            viewHeight = h;
        }

        public CustomDrawableView(Context context) {
            super(context);

            mDrawable = new ShapeDrawable(new RectShape());
            mDrawable.getPaint().setColor(0xff74AC23);

        }

        /**
         * Metoda odpowiadająca za narysowanie symulacji goniometru i przycisku zatwierdzającego
         * pomiar oraz oliczająca kąt między mierzonymi płaszczyznami. Zmienne rotX i rotY określają
         * odległość punktu końcowego rysowanej białej linii od jej punktu początkowego na
         * płaszczyźnie x i y. Analogicznie rotX2 i rotY2 dla lini drugiego pomiaru.
         * @param canvas
         */
        protected void onDraw(Canvas canvas) {

            Paint p = new Paint(); // set some paint options
            p.setColor(Color.WHITE);
            p.setStrokeWidth(200);
            canvas.drawColor(Color.BLACK);
            rect = new RectF(viewWidth / 2 - width, viewHeight / 2 - height, viewWidth / 2 + width, viewHeight / 2 + height);

            if (!finished) {

                /**
                 * Jeśli jest wykonywany drugi pomiar, narysuj drugą "linijke", jeśli jest
                 * wykonywany pierwszy pomiar, zmieniają się wartości rotX i rotY. Linia pierwszego
                 * pomiaru jest rysowana zawsze, niezależnie od etapu pomiaru.
                 */
                if (secondMeasurement) {
                    rotY2 = (int) (y / (Math.abs(x) + Math.abs(y)) * 10000);
                    rotX2 = (int) (x / (Math.abs(x) + Math.abs(y)) * 10000);

                    canvas.drawLine(viewWidth / 2, viewHeight / 2, viewWidth / 2 + rotY2, viewHeight / 2 + rotX2, p);
                } else {
                    rotY = (int) (y / (Math.abs(x) + Math.abs(y)) * 10000);
                    rotX = (int) (x / (Math.abs(x) + Math.abs(y)) * 10000);
                }

            } else {
                canvas.drawLine(viewWidth / 2, viewHeight / 2, viewWidth / 2 + rotY2, viewHeight / 2 + rotX2, p);
            }


            canvas.drawOval(rect, p);
            canvas.drawLine(viewWidth / 2, viewHeight / 2, viewWidth / 2 + rotY, viewHeight / 2 + rotX, p);


            if (rotX == 0)
                rotX = 1;
            if (rotX2 == 0)
                rotX2 = 1;

            /**
             * Twierdzenie cosinusów dla trójkąta utworzonego pomiędzy mierzonymi płaszczyznami.
             * Result jest wynikiem pomiaru kąta między narysowanymi liniami w stopniach.
             */
            double c = Math.sqrt(Math.pow(rotX - rotX2, 2) + Math.pow(rotY - rotY2, 2));
            double a = Math.sqrt(Math.pow(rotX, 2) + Math.pow(rotY, 2));
            double b = Math.sqrt(Math.pow(rotX2, 2) + Math.pow(rotY2, 2));
            result = Math.acos((a * a + b * b - c * c) / (2 * a * b)) * 360 / (Math.PI * 2);

            /**
             * Jeśli użytkownik jest w trakcie wykonywania drugiego pomiaru aplikacja wyświetla na
             * środku tymczasowy wynik pomiaru w stopniach
             */
            if (secondMeasurement) {
                p.setColor(Color.BLACK);
                p.setTextSize(45);
                canvas.drawText((String.format("%,.2f", result) + " °"), viewWidth / 2 -50 , viewHeight / 2   , p);
            }

            /**
             * Jeśli drugi pomiar został zakończony, jest rysowany zielony kwadrat z napisem "SAVE"
             * umożliwiający zapis wykonanego pomiaru w plku tekstowym na telefonie. Ten wynik
             * będzie można również w łatwy sposób odczytać za pomocą tej aplikacji.
             */
            if (finished) {
                RectF button = new RectF(buttonLeft, buttonTop, buttonRight, buttonDown);
                p.setColor(Color.GREEN);
                canvas.drawRect(button, p);
                p.setColor(Color.WHITE);
                p.setTextSize(45);
                canvas.drawText("SAVE", 100, 170, p);
            }

            invalidate();
        }
    }

}