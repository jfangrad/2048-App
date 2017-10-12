package lab4_205_07.uwaterloo.ca.lab4_205_07;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;
/**
 * Created by Justin Fangrad
 */

public class AccelerometerSensorListener implements SensorEventListener {

    //private TextView output;
    private float[] filteredReading = {0,0};
    private FSM.State currentState = FSM.State.resting;
    //private FSM.State previousState = FSM.State.resting;
    private double VERTICAL_FILTER_CONSTANT = 3.2;
    private double HORIZONTAL_FILTER_CONSTANT = 2.2;
    private FSM fsm;
    GameLoopTask gameLoopTask;

    public AccelerometerSensorListener(Vibrator v, GameLoopTask animationHandler){
        //this.output = output;
        fsm = new FSM(v);//create FSM object passing in the vibrator object
        this.gameLoopTask = animationHandler;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            //Filter readings with low pass filter
            filteredReading[0] += (event.values[0] - filteredReading[0])/HORIZONTAL_FILTER_CONSTANT;
            filteredReading[1] += (event.values[1] - filteredReading[1])/VERTICAL_FILTER_CONSTANT;

            currentState = fsm.updateState(filteredReading);//Update the FSM with the new readings and store the result

            //output.setText(currentState.name());//Display current State value

            if(!gameLoopTask.gameOver) {
                gameLoopTask.setDirection(currentState);
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not used
    }

}