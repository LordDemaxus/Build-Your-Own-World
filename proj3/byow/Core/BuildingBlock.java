package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Builds the world by iteratively placing blocks on the screen.
 *
 * @author Medhaav Chandra Mahesh, William Nonnemaker
 */



public class BuildingBlock {


    private static final TETile WALL = Tileset.WALL;
    private static final TETile FLOOR = Tileset.FLOOR;

    /* Big structure formations
     *
     *
     *
     *
     */

    public static void makeRoom(TETile[][] world, Position p, int width, int height) {
        makeRowOfWall(world, p, width);
        Position nextPosition = p.shift(0, 1);
        for (int i = 1; i < height; i++) {
            makeRowOfRoom(world, nextPosition, width);
            nextPosition = nextPosition.shift(0, 1);
        }
        makeRowOfWall(world, nextPosition, width);
    }

    public static void makeVerticalHallway(TETile[][] world, Position p, int height) {
        Position nextPosition = p;
        for (int i = 0; i < height; i++) {
            makeRowOfRoom(world, nextPosition, 3);
            nextPosition = nextPosition.shift(0, 1);
        }
    }

    public static void makeHorizontalHallway(TETile[][] world, Position p, int width) {
        Position nextPosition = p;
        makeRowOfWall(world, nextPosition, width);
        nextPosition = nextPosition.shift(0, 1);
        makeRowOfFloor(world, nextPosition, width);
        nextPosition = nextPosition.shift(0, 1);
        makeRowOfWall(world, nextPosition, width);
    }

    /* Lower level row making
     *
     *
     *
     *
     */

    public static void makeRowOfWall(TETile[][] world, Position p, int width) {
        for (int i = 0; i < width; i++) {
            world[p.x + i][p.y] = Tileset.MOUNTAIN;
        }
    }

    public static void makeRowOfRoom(TETile[][] world, Position p, int width) {
        world[p.x][p.y] = Tileset.MOUNTAIN;
        for (int j = 1; j < width - 1; j++) {
            world[p.x + j][p.y] = floorType(p.x + p.y);
        }
        world[p.x + width - 1][p.y] = Tileset.MOUNTAIN;
    }

    public static void makeRowOfFloor(TETile[][] world, Position p, int width) {
        for (int i = 0; i < width; i++) {
            world[p.x + i][p.y] = Tileset.WATER;
        }
    }

    public static TETile floorType(long seed) {
        Random rand = new Random(seed);
        int type = rand.nextInt(2);
        switch (type) {
            case 0:
                return Tileset.WATER;
            default:
            case 1:
                return Tileset.GRASS;
        }
    }

    /* Random functions
     *
     *
     *
     */

    public static int getRandomLength(Random rand) {
        return rand.nextInt(10) + 2;
    }

    /* Other utility functions
     *
     *
     *
     */


}
