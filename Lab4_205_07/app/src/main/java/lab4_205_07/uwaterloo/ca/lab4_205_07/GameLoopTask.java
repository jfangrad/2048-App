package lab4_205_07.uwaterloo.ca.lab4_205_07;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;
import lab4_205_07.uwaterloo.ca.lab4_205_07.FSM.State;
import static lab4_205_07.uwaterloo.ca.lab4_205_07.FSM.State.*;


public class GameLoopTask extends TimerTask {

    private Activity myActivity;
    private Context context;
    private RelativeLayout rl;
    private TextView resultText;
    private State current = resting;
    private boolean newBlockMade = false;
    protected static boolean someoneMoved = true;
    boolean gameOver = false;


    //Square coordinates lookup table
    protected static Point[] squareCoordinateLookUp = {new Point((int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE*2+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE*3+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_OFFSET),
            new Point((int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE+(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE+(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE*2+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE+(int)GameBlock.SQUARE_OFFSET),new Point((int)GameBlock.SQUARE_SIZE*3+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE+(int)GameBlock.SQUARE_OFFSET),
            new Point((int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE*2+(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE*2+(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE*2+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE*2+(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE*3+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE*2+(int)GameBlock.SQUARE_OFFSET),
            new Point((int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE*3+(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE*3+(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE*2+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE*3+(int)GameBlock.SQUARE_OFFSET), new Point((int)GameBlock.SQUARE_SIZE*3+(int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_SIZE*3+(int)GameBlock.SQUARE_OFFSET)};

    //Arraylist to hold all block objects
    protected static LinkedList<GameBlock> blocks = new LinkedList<>();

    GameLoopTask(Activity myActivity, Context context, RelativeLayout rl, TextView result){
        this.myActivity = myActivity;
        this.rl = rl;
        this.context = context;
        resultText = result;
        resultText.setTextSize(80f);
        resultText.setX(GameBlock.SQUARE_SIZE/3 - 20);
        resultText.setY(GameBlock.SQUARE_SIZE*3/2 - 10);
        resultText.setVisibility(View.INVISIBLE);
        //blocks.add(createBlock((int)GameBlock.SQUARE_OFFSET,(int)GameBlock.SQUARE_OFFSET,1,2));
    }

    @Override
    public void run() {

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(!gameOver) {
                    for (GameBlock block : blocks) {
                        block.move(); //Update the block

                        //If the game has ended
                        if (block.myValue == 64) {
                            endGame(true);
                        }
                    }
                }

            }
        });

    }

    //Displays information to user when game ends
    private void endGame(boolean isEnd){
        gameOver = true;
        if(isEnd){
            resultText.setTextColor(Color.BLUE);
            resultText.setText("YOU WIN!");
        }else{
            resultText.setTextColor(Color.RED);
            resultText.setText("YOU LOSE!");
        }
        resultText.bringToFront();
        resultText.setVisibility(View.VISIBLE);
    }

    //Function creates a new GameBlock object and returns its reference
    private GameBlock createBlock(int x, int y, int currentSquare, int valueOfBlock){
        //Log.i("new Block position: " , currentSquare+ "");
        GameBlock gameBlock = new GameBlock(context, rl, x, y, currentSquare, 0.9f);
        gameBlock.myValue = valueOfBlock;
        rl.addView(gameBlock);
        return gameBlock;
    }

    //generates a new block at a random square with a value of either a 2 or 4
    private GameBlock generateRandom(){
        boolean done = false;
        int randNum=0;
        Random rand = new Random();
        int blockValue;
        //While the number we generate corresponds to a occupied space keep looping
        while(!done){
            randNum = rand.nextInt(16);

            if(isOccupied(squareCoordinateLookUp[randNum].x,squareCoordinateLookUp[randNum].y) == null){
                done = true;
            }
        }

        //chose to generate a 2 or 4
        blockValue = rand.nextInt(12);
        if(blockValue == 3 || blockValue == 7) //Currently 1 in 6 chance of generating a 4
            blockValue = 4;
        else
            blockValue = 2;

        return createBlock(squareCoordinateLookUp[randNum].x,squareCoordinateLookUp[randNum].y, randNum+1, blockValue);
    }

    //Checks to see if board is full (Can't just see if the size is 16 cause that could go wrong)
    private boolean isGameBoardFull(){
        for(int i = 0; i < 16; i++){
            if(isOccupied(squareCoordinateLookUp[i].x, squareCoordinateLookUp[i].y) == null){
                return false;
            }
        }
        return true;
    }

    //Checks to see if any moves are possible on the gameboard by seeing if any adjacent blocks have the same value
    private boolean possibleMove(){

        boolean moveAvailable = false;

        //Loop through each block on the board and check 4 possible spaces around it
        for(GameBlock block: blocks){
            int x = block.getCoord()[0];
            int y = block.getCoord()[1];
            int value = block.myValue;
            GameBlock test;
            if((test = isOccupied(x+(int)GameBlock.SQUARE_SIZE, y)) != null && test.myValue == value){
                moveAvailable = true;
                break;
            }
            if((test = isOccupied(x-(int)GameBlock.SQUARE_SIZE, y)) != null && test.myValue == value){
                moveAvailable = true;
                break;
            }
            if((test = isOccupied(x, y-(int)GameBlock.SQUARE_SIZE)) != null && test.myValue == value){
                moveAvailable = true;
                break;
            }
            if((test = isOccupied(x, y+(int)GameBlock.SQUARE_SIZE)) != null && test.myValue == value){
                moveAvailable = true;
                break;
            }
        }

        return moveAvailable;
    }

    //Function sets the games direction
    public void setDirection(State direction){
        current = direction;

        //Check if all blocks are done moving
        boolean movementDone = true;
        for(GameBlock block: blocks){
            if(!block.isDoneMoving())
                movementDone = false;
        }

        //Generates New random GameBlock and deletes any blocks that need to be deleted
        if(movementDone && current == resting && !newBlockMade) {//Move has been made and all blocks have updated (Waiting for next move)

            //Either make a new block or end the game
            if(!isGameBoardFull() || (isGameBoardFull() && possibleMove())){
                //Add new Block to Screen
                if(someoneMoved) { //Make sure a movement has actually occured before creating a new one
                    blocks.add(generateRandom());
                    newBlockMade = true;
                    someoneMoved = false;
                }else{//If no one moved just fake it
                    newBlockMade = true;
                }
            }else{
                //If gameboard is full and no moves are possible end game
                if(!possibleMove())
                    endGame(false);
            }

            //Go through and delete any blocks that have delete flag and double any that have double flag
            Iterator<GameBlock> iterator = blocks.iterator();
            while(iterator.hasNext()){
                GameBlock block = iterator.next();
                if(block.toDouble){
                    block.doubleValue();
                }
                if(block.toDelete){
                    block.delete(rl);
                    iterator.remove();
                }
            }
        }

        //Sets direction
        if(movementDone && current != resting && current != undefined && newBlockMade){//Blocks are not moving, new block has been made, and state says to move
            for (GameBlock block: blocks) {
                block.setDirection(current);
            }
            newBlockMade = false;
        }
    }

    //Function checks if specified coordinates are occupied by another block
    @Nullable
    public static GameBlock isOccupied(int x, int y){
        //int buffer = 20;

        for(GameBlock block:blocks){
            if(block.getCoord()[0] == x && block.getCoord()[1] == y)
                return block;
        }

        return null;
    }
}
