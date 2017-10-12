package lab4_205_07.uwaterloo.ca.lab4_205_07;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

public class Lab4_205_07 extends AppCompatActivity {

    //TextView directionText;
    AccelerometerSensorListener accelerometerSensorListener;
    Sensor accelerometerSensor;
    SensorManager sensorManager;
    public static int GAMEBOARD_DIMENSION = 900;//Set default value in case it fails

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab4_205_07);

        RelativeLayout relativeLayout = (android.widget.RelativeLayout) findViewById(R.id.relativeLayout);

        GAMEBOARD_DIMENSION = getScreenWidth();

        relativeLayout.getLayoutParams().height = GAMEBOARD_DIMENSION;
        relativeLayout.getLayoutParams().width = GAMEBOARD_DIMENSION;
        relativeLayout.setPadding(0,0,0,0);
        relativeLayout.setY((getScreenHeight()-GAMEBOARD_DIMENSION)/3);
        relativeLayout.setBackgroundResource(R.drawable.gameboard);

        TextView resultText = new TextView(this);
        resultText.setText("");
        relativeLayout.addView(resultText);

        GameLoopTask gameLoopTask = new GameLoopTask(this, this, relativeLayout, resultText);
        Timer myTimer = new Timer();
        myTimer.schedule(gameLoopTask,16,16);

        //Create vibrator variable to alert when user makes a mistake
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        //Set  up accelerometer and its listener
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensorListener = new AccelerometerSensorListener(v, gameLoopTask);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor,SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerometerSensorListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(accelerometerSensorListener);
    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
    private int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}
