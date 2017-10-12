package lab4_205_07.uwaterloo.ca.lab4_205_07;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import lab4_205_07.uwaterloo.ca.lab4_205_07.FSM.State;
import static lab4_205_07.uwaterloo.ca.lab4_205_07.FSM.State.*;

public class GameBlock extends AbstractGameBlock {
    protected int myValue = 2;
    protected int currentSquare = 0;
    private float myCoordX;
    private float myCoordY;
    private float myTargetCoordX;
    private float myTargetCoordY;
    protected boolean toDelete = false;
    protected boolean toDouble = false;

    protected TextView tv;
    private static int TEXT_OFFSET = 100;

    private State myDirection = resting;

    private final int MAX_SPEED = 35;
    private int velocity = 0;
    private int acceleration = 5;
    private final int ACCELERATION_CONST = 5;

    //Constructor
    public GameBlock(Context gbCTX,RelativeLayout rl, int coordX, int coordY, int currentSquare, float scale) {
        super(gbCTX);
        this.myCoordX = coordX;
        this.myCoordY = coordY;
        this.setX(myCoordX);
        this.setY(myCoordY);
        this.setImageResource(R.drawable.gameblock);
        this.setScaleY(scale);
        this.setScaleX(scale);
        this.setPadding(0,0,0,0);

        myTargetCoordX = myCoordX;
        myTargetCoordY = myCoordY;
        this.currentSquare = currentSquare;

        tv = new TextView(gbCTX); //Create blocks text view to display number value
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(25f);
        tv.setText(Integer.toString(myValue));
        rl.addView(tv);
        tv.setX(myCoordX + TEXT_OFFSET);
        tv.setY(myCoordY + TEXT_OFFSET-10);
        tv.bringToFront();
    }

    //Returns true if block is done moving (myCoord == myTargetCoord)
    public boolean isDoneMoving(){
        //Make sure the block is done its last move
        return ((int)myCoordY == (int)myTargetCoordY && (int)myCoordX == (int)myTargetCoordX);
    }

    //Returns X &Y coord
    public int[] getCoord(){
        int[] coord = new int[2];
        coord[0] = (int) myCoordX;
        coord[1] = (int) myCoordY;
        return coord;
    }

    //Doubles block value and updates text view
    public void doubleValue(){
        toDouble = false;
        myValue *= 2;
        tv.setText(Integer.toString(myValue));
    }

    //Gets block ready for deletion
    public void delete(RelativeLayout rl){
        rl.removeView(this);
        rl.removeView(tv);
        myValue = -1;
        myCoordY = -1;
        myCoordX = -1;
        myTargetCoordY = -1;
        myTargetCoordX = -1;
    }

    //Calculates the number of merges will occur with it and the blocks infront of it
    protected int calculateMerges(ArrayList<GameBlock> occupants){
        int numMerges = 0;

        //loop through each block starting with the last one in the list (Farthest one from the target wall)
        for(int i = occupants.size()-1; i >= 0; i--){
            int numWithMyValue = 0;

            // loop through all blocks infront and count how many have the same value
            for(int j = i-1; j >=0; j--){
                if(occupants.get(i).myValue == occupants.get(j).myValue){
                    numWithMyValue++;
                }
            }

            if(numWithMyValue == 3){//Only occurs if all 4 are the same
                numMerges = 2;
                //double the farther block and delete the closer
                occupants.get(0).toDouble = true;
                occupants.get(2).toDouble = true;
                occupants.get(1).toDelete = true;
                occupants.get(3).toDelete = true;
                return numMerges;
            }

            else if(numWithMyValue == 2){//Only occurs if at least 3 blocks present
                if(occupants.get(i).myValue == occupants.get(i-1).myValue && occupants.get(i-1).myValue != occupants.get(i-2).myValue){//
                    numMerges++;
                    occupants.get(i-1).toDouble = true;
                    occupants.get(i).toDelete = true;
                }
            }

            else if(numWithMyValue == 1 && i > 0 && occupants.get(i).myValue == occupants.get(i-1).myValue){
                numMerges++;
                occupants.get(i-1).toDouble = true;
                occupants.get(i).toDelete = true;
            }
        }

        return numMerges;
    }

    //Sets the target X & Y coordinates of the block based on game direction
    public void setDirection(State direction){
        myDirection = direction;

        //temp variables for collision detection
        int numOccupants = 0;
        ArrayList<GameBlock> occupants = new ArrayList<>();
        int mergeCount = 0;
        float testCoord;
        GameBlock testBlock;

        //Update the target Coordinates and acceleration based on game direction
        switch (myDirection){
            case up:
                myTargetCoordX = myCoordX;
                myTargetCoordY = Y_BOUND_TOP;
                acceleration = -ACCELERATION_CONST;

                testCoord = GameBlock.Y_BOUND_TOP;
                while((int) testCoord != (int) myCoordY){
                    testBlock = GameLoopTask.isOccupied((int)myCoordX, (int)testCoord);

                    //If there is a block in the way add it to the arrray
                    if(testBlock != null){
                        occupants.add(testBlock);
                        numOccupants++;
                    }
                    testCoord += SQUARE_SIZE;
                }
                occupants.add(this);
                mergeCount = calculateMerges(occupants);

                myTargetCoordY = Y_BOUND_TOP + numOccupants*SQUARE_SIZE - mergeCount*SQUARE_SIZE;
                break;

            case left:
                myTargetCoordX = X_BOUND_LEFT;
                myTargetCoordY = myCoordY;
                acceleration = -ACCELERATION_CONST;

                testCoord = GameBlock.X_BOUND_LEFT;
                while((int) testCoord != (int) myCoordX){
                    testBlock = GameLoopTask.isOccupied((int)testCoord, (int)myCoordY);

                    if(testBlock != null){
                        occupants.add(testBlock);
                        numOccupants++;
                    }
                    testCoord += SQUARE_SIZE;
                }
                occupants.add(this);
                mergeCount = calculateMerges(occupants);

                myTargetCoordX = X_BOUND_LEFT + numOccupants*SQUARE_SIZE - mergeCount*SQUARE_SIZE;
                break;

            case right:
                myTargetCoordX = X_BOUND_RIGHT;//X_BOUND_RIGHT
                myTargetCoordY = myCoordY;
                acceleration = ACCELERATION_CONST;

                testCoord = GameBlock.X_BOUND_RIGHT;
                while((int) testCoord != (int) myCoordX){
                    testBlock = GameLoopTask.isOccupied((int)testCoord, (int)myCoordY);

                    if(testBlock != null){
                        occupants.add(testBlock);
                        numOccupants++;
                    }
                    testCoord -= SQUARE_SIZE;
                }
                occupants.add(this);
                mergeCount = calculateMerges(occupants);

                myTargetCoordX = X_BOUND_RIGHT - numOccupants*SQUARE_SIZE + mergeCount*SQUARE_SIZE;
                break;

            case down:
                myTargetCoordX = myCoordX;
                myTargetCoordY = Y_BOUND_BOTTOM;
                acceleration = ACCELERATION_CONST;

                testCoord = GameBlock.Y_BOUND_BOTTOM;
                while((int) testCoord != (int) myCoordY){
                    testBlock = GameLoopTask.isOccupied((int)myCoordX, (int)testCoord);

                    if(testBlock != null){
                        occupants.add(testBlock);
                        numOccupants++;
                    }
                    testCoord -= SQUARE_SIZE;
                }
                occupants.add(this);
                mergeCount = calculateMerges(occupants);

                myTargetCoordY = Y_BOUND_BOTTOM - numOccupants*SQUARE_SIZE + mergeCount*SQUARE_SIZE;
                break;

            default://If in resting state or undefined just set target to current
                velocity = 0;
                myTargetCoordX = myCoordX;
                myTargetCoordY = myCoordY;
                break;

        }

        //Just in case for Garbage collection
        Iterator<GameBlock> iterator = occupants.iterator();
        while(iterator.hasNext()){
            iterator.next();
            iterator.remove();
        }
        occupants = null;
    }

    //Moves the block to target coord
    public void move(){

        //Cap velocity at 35px / frame (Account for the direction we are moving)
        if(acceleration > 0 && velocity < MAX_SPEED)
            velocity += acceleration;
        else if(acceleration < 0 && velocity > -MAX_SPEED)
            velocity += acceleration;

        //update current X and Y coordinates based on game direction. If we are at the target coordinates  don't move and set velocity = 0
        switch (myDirection){
            case up:
                if(myCoordY > myTargetCoordY){//If haven't reached target
                    if((myCoordY + velocity) < myTargetCoordY)//make sure you don't overshoot
                        myCoordY = myTargetCoordY;
                    else
                        myCoordY += velocity;
                }else{//If reached target don't move
                    myCoordY = myTargetCoordY;
                    velocity = 0;
                }
                break;
            case left:
                if(myCoordX > myTargetCoordX){
                    if((myCoordX + velocity) < myTargetCoordX)
                        myCoordX = myTargetCoordX;
                    else
                        myCoordX += velocity;
                }else{
                    myCoordX = myTargetCoordX;
                    velocity = 0;
                }
                break;
            case right:
                if(myCoordX < myTargetCoordX){
                    if((myCoordX + velocity) > myTargetCoordX)
                        myCoordX = myTargetCoordX;
                    else
                        myCoordX += velocity;
                }else{
                    myCoordX = myTargetCoordX;
                    velocity = 0;
                }
                break;
            case down:
                if(myCoordY < myTargetCoordY){
                    if((myCoordY + velocity) > myTargetCoordY)
                        myCoordY = myTargetCoordY;
                    else
                        myCoordY += velocity;
                }else{
                    myCoordY = myTargetCoordY;
                    velocity = 0;
                }
                break;
            default://If in resting state or undefined just set target to current and velocity equal to 0
                velocity = 0;
                myTargetCoordX = myCoordX;
                myTargetCoordY = myCoordY;
                break;
        }

        if(velocity != 0){
            GameLoopTask.someoneMoved = true;
        }

        //Update the blocks position on the screen
        this.setX(myCoordX);
        this.setY(myCoordY);
        tv.setX(myCoordX + TEXT_OFFSET);
        tv.setY(myCoordY + TEXT_OFFSET-10);
        tv.bringToFront();

        //Update current square value (1-16)
        /*for (int i = 0; i < 16; i++) {
            if(myCoordX == GameLoopTask.squareCoordinateLookUp[i].x && myCoordY == GameLoopTask.squareCoordinateLookUp[i].y){
                currentSquare = i+1;
                break;
            }
        }*/

        //tv.setText(Integer.toString(currentSquare));
        tv.setText(Integer.toString(myValue));
    }
}
