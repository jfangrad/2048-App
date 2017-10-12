package lab4_205_07.uwaterloo.ca.lab4_205_07;

import android.content.Context;
import android.widget.RelativeLayout;
import java.util.ArrayList;

public abstract class AbstractGameBlock extends android.support.v7.widget.AppCompatImageView{

    protected static final float SQUARE_OFFSET = 8f;//Account for top corner not being 0,0 but rather 8,8
    protected static final float SQUARE_SIZE = (Lab4_205_07.GAMEBOARD_DIMENSION-8)/4; //268px
    protected static final float X_BOUND_RIGHT = SQUARE_SIZE*3+SQUARE_OFFSET;//Lab4_205_07.GAMEBOARD_DIMENSION-263;
    protected static final float X_BOUND_LEFT = SQUARE_OFFSET;
    protected static final float Y_BOUND_TOP = SQUARE_OFFSET;
    protected static final float Y_BOUND_BOTTOM = SQUARE_SIZE*3+SQUARE_OFFSET;//Lab4_205_07.GAMEBOARD_DIMENSION-263;

    public AbstractGameBlock(Context context) {
        super(context);
    }

    public abstract boolean isDoneMoving();
    public abstract void setDirection(FSM.State direction);
    public abstract void move();
    public abstract int[] getCoord();
    public abstract void doubleValue();
    public abstract void delete(RelativeLayout rl);
    protected abstract int calculateMerges(ArrayList<GameBlock> occupants);

}
