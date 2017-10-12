package lab4_205_07.uwaterloo.ca.lab4_205_07;

import android.os.Vibrator;
import android.provider.Settings;

/**
 * Created by Justin
 */

public class FSM {

    //Variables for keeping track of the state
    enum State {up, down, right, left, resting,undefined}
    private State currentState = State.resting;

    //Constant threshold values
    private final double HORIZONTAL_THRESHOLD = 4.5;
    private final double VERTICAL_THRESHOLD = 3.0;
    private final double RESTING_THRESHOLD = 0.5;


    private long lastMilli = -1;
    private long movementStartMilli = -1;
    private final long TIMEOUT_MILLIS = 1500;
    private boolean seen = false;
    private Vibrator v;

    //Constructor
    public FSM(Vibrator v){
        this.v = v;
    }

    public State updateState(float[] filteredReading){

        //If in resting state
        if(currentState == State.resting) {
            //If both of the values exceed the threshold the it is undefined
            if(Math.abs(filteredReading[0]) >= HORIZONTAL_THRESHOLD && Math.abs(filteredReading[1]) >= VERTICAL_THRESHOLD){
                currentState = State.undefined;
                v.vibrate(250);
            }
            //If only the Horizontal threshold has been exceeded (Left or Right Movement)
            else if (Math.abs(filteredReading[0]) >= HORIZONTAL_THRESHOLD) {
                if (filteredReading[0] > 0) //Positive reading (right)
                    currentState = State.right;
                else //negative reading (left)
                    currentState = State.left;
            }
            //If only the Vertical threshold has been exceeded (Up or Down Movement)
            else if(Math.abs(filteredReading[1]) >= VERTICAL_THRESHOLD){
                if(filteredReading[1] > 0) //Up
                    currentState = State.up;
                else //Down
                    currentState = State.down;
            }

            if(currentState != State.resting)
                movementStartMilli = System.currentTimeMillis();
        }

        //In A movement state
        else{
            long curMilli = System.currentTimeMillis();// Get current time in millis for comparison

            if(curMilli - movementStartMilli > TIMEOUT_MILLIS){//Been to long put back into resting state
                currentState = State.resting;
                v.vibrate(800);
            }


            if((curMilli - lastMilli) > 50) { //If time since last check has been over 50 milli
                lastMilli = curMilli;

                //If currently in right or left state
                if (currentState == State.left || currentState == State.right) {
                    //If a value close to zero has not been seen before and we see one now
                    if (filteredReading[0] > -RESTING_THRESHOLD && filteredReading[0] < RESTING_THRESHOLD && !seen) {
                        seen = true;
                    }
                    //If we have seen a value close to zero before and now we see another
                    else if (filteredReading[0] > -RESTING_THRESHOLD && filteredReading[0] < RESTING_THRESHOLD && seen){
                        seen = false;
                        currentState = State.resting;
                    }
                    //If neither of the above are true... Set seen to false
                    else{
                        seen = false;
                    }

                }
                //If currently in up or down state
                else if (currentState == State.up || currentState == State.down) {
                    //If a value close to zero has not been seen before and we see one now
                    if (!seen && filteredReading[1] > -RESTING_THRESHOLD && filteredReading[1] < RESTING_THRESHOLD) {
                        seen = true;
                    }
                    //If we have seen a value close to zero before and now we see another
                    else if (filteredReading[1] > -RESTING_THRESHOLD && filteredReading[1] < RESTING_THRESHOLD && seen){
                        seen = false;
                        currentState = State.resting;
                    }
                    //If neither of the above are true... Set seen to false
                    else{
                        seen = false;
                    }
                }
                //If currently in undefined state
                else if(currentState == State.undefined){
                    //If a value close to zero in BOTH axis has not been seen before and we see one now
                    if (!seen && filteredReading[1] > -RESTING_THRESHOLD && filteredReading[1] < RESTING_THRESHOLD && filteredReading[0] > -RESTING_THRESHOLD && filteredReading[0] < RESTING_THRESHOLD) {
                        seen = true;
                    }
                    //If we have seen a value close to zero in BOTH axis before and now we see another
                    else if (filteredReading[1] > -RESTING_THRESHOLD && filteredReading[1] < RESTING_THRESHOLD && filteredReading[0] > -RESTING_THRESHOLD && filteredReading[0] < RESTING_THRESHOLD && seen){
                        seen = false;
                        currentState = State.resting;
                    }
                    //If neither of the above are true... Set seen to false
                    else{
                        seen = false;
                    }
                }
            }
        }

        return currentState;
    }

}
