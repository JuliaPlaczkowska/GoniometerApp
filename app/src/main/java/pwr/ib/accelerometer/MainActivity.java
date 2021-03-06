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
     * W momencie stworzenia aktywno??ci inicjalizowany jest SensorManager dla urz??dzenia na kt??rym
     * dzia??a aplikacja. SensorManager s??u??y do pobrania informacji o wybranych czujnikach ( w naszym
     * przypadku b??dzie to akcelerometr ) i zainicjalizowania metod nas??uchuj??cych. Nast??pnie
     * utworzony jest CustomDrawableView, na kt??rym b??dzie rysowany symulator gonimetru. Kolejnym
     * krokiem jest utworzenie obiektu klasy  GestureDetectorCompat, kt??ry b??dzie nas??uchiwa??
     * wydarze?? klikni??cia na ekran za pomoc?? wbudowanych metod. Pozwoli tak??e na odczytywanie
     * takich informacji jak wsp????rz??dne klikni??cia
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
     * Funkcja przesy??aj??ca warto???? wykonanego pomiaru w stopniach wraz z dat?? i godzin?? wykonania
     * do aktywno??ci SaveResultActivity, gdzie, po podaniu nazwy pomiaru i zatwierdzeniu, zostanie
     * zapisany do pliku tekstowego w poami??ci urz??dzenia.
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
     * Metoda nas??uchuj??ca zmiany po??o??enia urz??dzenia za pomoc?? warto??ci mierzonych przez sensor,
     * czyli sk??adowych si?? dzia??aj??cych na urz??dzenie (w pozycji nieruchomej jest to w przybli??eniu
     * si??a grawitacji) na kierunkach x, y i z. Celem aplikacji jest okre??lenie k??ta bocznej
     * kraw??dzi telefonu. w tym celu aplikacja korzysta tylko z dw??ch sk??adowych -
     * r??wnolog??ych do ekranu urz??dzenia, czyli x i y, kt??re s?? uaktualniane w tej metodzie
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
     * Metoda nas??uchuj??ca dotkni??cia ekranu urz??dzenia
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
     * Metoda, nas??uchuj??ca pojedy??czego klikni??cia w ekran. Gdy takie klikni??cie zostanie
     * zarejestrowane, nast??puje zmodyfikowanie flag globalnych w celu okre??lenia etapu pomiaru -
     * pierwsze klikni??cie w ekran po uruchomieniu aktywno??ci zapisuje pierwsz?? p??aszczyzne, w??wczas
     * zostaje aktywowana flaga secondMeasurement, oznaczaj??ca ??e drugi pomiar jest w toku. Kolejny
     * klik zapisuje pomiar drugiej p??aszczyzny i zostaje ustawiona flaga finished - czyli oba
     * pomiary uko??czone. W tym momencie u??ytkownik ma mo??liwo???? zapisania pomiaru poprzez
     * klikni??cie na ekran w obszarze zielonego kwadratu z namisem SAVE - jego po??o??enie jest
     * okre??lone za pomoc?? nast??puj??cego warunku:
     * (currX <= (buttonRight + 300) && currX >= buttonLeft && currY >= buttonTop && currY <= (buttonDown + 300))
     * Je??eli u??ytkownik kliknie w dowolnym miejscu niespe??niaj??cym tego warunku pomiar zostanie
     * anulowany i nie b??dzie mo??liwo??ci zapisania go do pliku. Wtedy aktywno???? wraca do stanu
     * jak przy jej uruchomieniu.
     * @param e - pojedy??cze klikni??cie na ekran
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
         * Metoda pozwala pobra?? wymiary ekranu i ustawia je jako zmienne globalne
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
         * Metoda odpowiadaj??ca za narysowanie symulacji goniometru i przycisku zatwierdzaj??cego
         * pomiar oraz oliczaj??ca k??t mi??dzy mierzonymi p??aszczyznami. Zmienne rotX i rotY okre??laj??
         * odleg??o???? punktu ko??cowego rysowanej bia??ej linii od jej punktu pocz??tkowego na
         * p??aszczy??nie x i y. Analogicznie rotX2 i rotY2 dla lini drugiego pomiaru.
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
                 * Je??li jest wykonywany drugi pomiar, narysuj drug?? "linijke", je??li jest
                 * wykonywany pierwszy pomiar, zmieniaj?? si?? warto??ci rotX i rotY. Linia pierwszego
                 * pomiaru jest rysowana zawsze, niezale??nie od etapu pomiaru.
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
             * Twierdzenie cosinus??w dla tr??jk??ta utworzonego pomi??dzy mierzonymi p??aszczyznami.
             * Result jest wynikiem pomiaru k??ta mi??dzy narysowanymi liniami w stopniach.
             */
            double c = Math.sqrt(Math.pow(rotX - rotX2, 2) + Math.pow(rotY - rotY2, 2));
            double a = Math.sqrt(Math.pow(rotX, 2) + Math.pow(rotY, 2));
            double b = Math.sqrt(Math.pow(rotX2, 2) + Math.pow(rotY2, 2));
            result = Math.acos((a * a + b * b - c * c) / (2 * a * b)) * 360 / (Math.PI * 2);

            /**
             * Je??li u??ytkownik jest w trakcie wykonywania drugiego pomiaru aplikacja wy??wietla na
             * ??rodku tymczasowy wynik pomiaru w stopniach
             */
            if (secondMeasurement) {
                p.setColor(Color.BLACK);
                p.setTextSize(45);
                canvas.drawText((String.format("%,.2f", result) + " ??"), viewWidth / 2 -50 , viewHeight / 2   , p);
            }

            /**
             * Je??li drugi pomiar zosta?? zako??czony, jest rysowany zielony kwadrat z napisem "SAVE"
             * umo??liwiaj??cy zapis wykonanego pomiaru w plku tekstowym na telefonie. Ten wynik
             * b??dzie mo??na r??wnie?? w ??atwy spos??b odczyta?? za pomoc?? tej aplikacji.
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