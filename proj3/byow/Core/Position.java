package byow.Core;

/**
 * Class that stores the position of a specific block
 *
 * @author Medhaav Chandra Mahesh, William Nonnemaker
 */


public class Position {
    int x;
    int y;
    int turns;


    public Position(int x, int y) {
        this(x, y, 0);
    }
    public Position(int x, int y, int turns) {
        this.x = x;
        this.y = y;
        this.turns = turns;
    }

    public Position shift(int posX, int posY) {
        return new Position(this.x + posX, this.y + posY);
    }

    public Position clone() {
        return new Position(this.x, this.y);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
