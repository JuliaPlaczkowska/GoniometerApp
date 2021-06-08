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
    private Button saveButton;
    private SurfaceView surfaceView;
    private double result;
    //private SurfaceHolder mHolder;


    CustomDrawableView mCustomDrawableView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();
    public static double x;
    public static double y;

    private int buttonTop = 50;
    private int buttonDown = 250;
    private int buttonLeft = 50;
    private int buttonRight = 250;

    private GestureDetectorCompat mDetector;


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveResults() {
        Intent intent = new Intent(MainActivity.this, SaveResultActivity.class);
        Bundle b = new Bundle();
        b.putDouble("value", result);
        b.putString("time", LocalDateTime.now().toString());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Log.d(TAG, "onSensorChanged: X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);

        x = event.values[0];
        if (event.values[1] != 0)
            y = event.values[1];
        else
            y = 0.01;

        if (surfaceView != null) {
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
//        float x = e.getX();
//        float y = e.getY();

//        if (!finished) {
//            if (secondMeasurement)
//                finished = true;
//            else {
//                secondMeasurement = true;
//                //Toast.makeText(this, "Click again to finish measurement", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            finished = false;
//            secondMeasurement = false;
//        }
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
                Toast.makeText(this, "Click again to finish measurement", Toast.LENGTH_LONG).show();
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

//    @Override
//    public void surfaceCreated(@NonNull SurfaceHolder holder) {
//
//    }
//
//    @Override
//    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
//        viewHeight = height;
//        viewWidth = width;
//    }
//
//    @Override
//    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//
//    }
//
//
//    private void tryDrawing(SurfaceHolder holder) {
//        Log.i(TAG, "Trying to draw...");
//
//        Canvas canvas = holder.lockCanvas();
//        mHolder = holder;
//        if (canvas == null) {
//            Log.e(TAG, "Cannot draw onto the canvas as it's null");
//        } else {
//            drawMyStuff(canvas);
//            mHolder.unlockCanvasAndPost(canvas);
//        }
//    }
//
//    protected void onDraw(Canvas canvas) {
//        canvas.drawRGB(255, 0, 255);
//    }
//
//    private void drawMyStuff(Canvas canvas) {
//
//        int width = 150;
//        int height = 150;
//
//        Log.i(TAG, "Drawing...");
//        Paint p = new Paint(); // set some paint options
//        p.setColor(Color.WHITE);
//        p.setStrokeWidth(200);
//        canvas.drawColor(Color.BLACK);
//        rect = new RectF(viewWidth / 2 - width, viewHeight / 2 - height, viewWidth / 2 + width, viewHeight / 2 + height);
//
//        if (!finished) {
//            if (secondMeasurement) {
//                rotY2 = (int) (y / (Math.abs(x) + Math.abs(y)) * 10000);
//                rotX2 = (int) (x / (Math.abs(x) + Math.abs(y)) * 10000);
//
//                canvas.drawLine(viewWidth / 2, viewHeight / 2, viewWidth / 2 + rotY2, viewHeight / 2 + rotX2, p);
//            } else {
//                rotY = (int) (y / (Math.abs(x) + Math.abs(y)) * 10000);
//                rotX = (int) (x / (Math.abs(x) + Math.abs(y)) * 10000);
//            }
//
//        } else {
//            canvas.drawLine(viewWidth / 2, viewHeight / 2, viewWidth / 2 + rotY2, viewHeight / 2 + rotX2, p);
//        }
//
//
//        canvas.drawOval(rect, p);
//        canvas.drawLine(viewWidth / 2, viewHeight / 2, viewWidth / 2 + rotY, viewHeight / 2 + rotX, p);
//
//
//        if (rotX == 0)
//            rotX = 1;
//        if (rotX2 == 0)
//            rotX2 = 1;
//
//        double c = Math.sqrt(Math.pow(rotX - rotX2, 2) + Math.pow(rotY - rotY2, 2));
//        double a = Math.sqrt(Math.pow(rotX, 2) + Math.pow(rotY, 2));
//        double b = Math.sqrt(Math.pow(rotX2, 2) + Math.pow(rotY2, 2));
//        double result = Math.acos((a * a + b * b - c * c) / (2 * a * b)) * 360 / (Math.PI * 2);
//
//        if (secondMeasurement) {
//            p.setColor(Color.BLACK);
//            p.setTextSize(45);
//            canvas.drawText((String.format("%,.2f", result) + " °"), viewWidth / 2 - 30, viewHeight / 2 - 30, p);
//        }
//
//        if (finished) {
//            saveButton.setVisibility(VISIBLE);
//        }
//
//        mHolder.unlockCanvasAndPost(canvas);
//    }


    public class CustomDrawableView extends View {
        static final int width = 150;
        static final int height = 150;

        private int viewWidth;
        private int viewHeight;


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
            // mDrawable.setBounds(x, y, x + width, y + height);

        }

        protected void onDraw(Canvas canvas) {

            Paint p = new Paint(); // set some paint options
            p.setColor(Color.WHITE);
            p.setStrokeWidth(200);
            canvas.drawColor(Color.BLACK);
            rect = new RectF(viewWidth / 2 - width, viewHeight / 2 - height, viewWidth / 2 + width, viewHeight / 2 + height);

            if (!finished) {
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

            double c = Math.sqrt(Math.pow(rotX - rotX2, 2) + Math.pow(rotY - rotY2, 2));
            double a = Math.sqrt(Math.pow(rotX, 2) + Math.pow(rotY, 2));
            double b = Math.sqrt(Math.pow(rotX2, 2) + Math.pow(rotY2, 2));
            result = Math.acos((a * a + b * b - c * c) / (2 * a * b)) * 360 / (Math.PI * 2);

            if (secondMeasurement) {
                p.setColor(Color.BLACK);
                p.setTextSize(45);
                canvas.drawText((String.format("%,.2f", result) + " °"), viewWidth / 2 - 30, viewHeight / 2 - 30, p);
            }

            if (finished) {
//                saveButton.setVisibility(VISIBLE);
                RectF button = new RectF(buttonLeft, buttonTop, buttonRight, buttonDown);
                p.setColor(Color.GREEN);
                canvas.drawRect(button, p);
                p.setColor(Color.WHITE);
                p.setTextSize(45);
                canvas.drawText("SAVE", 100, 200, p);
            }

            invalidate();
        }
    }

}