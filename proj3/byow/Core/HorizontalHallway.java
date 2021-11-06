package byow.Core;


import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Horizontal Hallway creation
 *
 * @author William Nonnemaker
 */


public class HorizontalHallway {

    private int length;
    private Position nextStart;
    private boolean inBounds = true;

    public HorizontalHallway(TETile[][] world, Position p,
                             boolean closeStart, Random rand, boolean fromHall) {
        length = BuildingBlock.getRandomLength(rand);
        if (closeStart) {
            p = p.shift(1, 0);
            nextStart = p.shift(length, 0);
            if (!HorizontalHallway.inBounds(world, p, length, fromHall)) {
                inBounds = false;
                return;
            }
            BuildingBlock.makeHorizontalHallway(world, p, length);

        } else {
            nextStart = p.shift(-length, 0);
            if (!HorizontalHallway.inBounds(world, nextStart, length, fromHall)) {
                inBounds = false;
                return;
            }
            BuildingBlock.makeHorizontalHallway(world, nextStart, length);
        }
    }


    public void buildOut(TETile[][] world, Position p, Random rand,
                         boolean closeStart, boolean firstHall) {
        if (!inBounds) {
            return;
        }
        int halls = rand.nextInt(4);
        if (!closeStart) {
            halls = 0;
        }
        if (firstHall) {
            halls = 1;
        }
        switch (halls) {
            case 1:
                VertHallway h = new VertHallway(world, p, true, rand, true);
                h.buildOut(world, h.getNextStart(), rand, true, false);
                fillIn(world, h.isInBounds());
                inBoundsFill(world, h.isInBounds(), closeStart);
                break;
            default:
                if (closeStart) {
                    Room r = new Room(world, p.shift(-1, 0), rand, "west", false);
                    r.buildOut(world, r.getThisPos(), rand, false);
                    inBoundsFill(world, r.isInBounds(), closeStart);
                } else {
                    Room r = new Room(world, p, rand, "east", false);
                    r.buildOut(world, r.getThisPos(), rand, false);
                    inBoundsFill(world, r.isInBounds(), closeStart);
                }
        }
    }

    public Position getNextStart() {
        return nextStart;
    }

    public boolean isInBounds() {
        return inBounds;
    }

    public static boolean inBounds(TETile[][] world, Position p, int length, boolean fromHall) {
        if (fromHall) {
            return inBoundsH(world, p.shift(1, 0), length)
                    && inBoundsH(world, p.shift(2, 0), length)
                    && inBoundsH(world, p.shift(0, 2), length);
        }
        return inBoundsH(world, p.shift(0, 2), length)
                && inBoundsH(world, p.shift(0, 1), length) && inBoundsH(world, p, length);
    }

    public static boolean inBoundsH(TETile[][] world, Position p, int length) {
        if (p.getX() <= 0 || p.getX() + length > World.WIDTH - 1
                || p.getY() <= 0 || p.getY() > World.HEIGHT - 3) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (world[p.getX() + i][p.getY()] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }

    public void fillIn(TETile[][] world, boolean b) {
        Position p = this.getNextStart().shift(0, 0);
        if (b) {
            world[p.getX() + 1][p.getY()] = Tileset.MOUNTAIN;
            world[p.getX()][p.getY()] = Tileset.MOUNTAIN;
            world[p.getX()][p.getY() + 1] = BuildingBlock.floorType(p.getX() + p.getY());
            world[p.getX()][p.getY() + 2] = Tileset.MOUNTAIN;
            return;
        }
    }

    public void inBoundsFill(TETile[][] world, boolean b, boolean closeStart) {
        if (!b) {
            if (closeStart) {
                world[this.nextStart.getX() - 1][nextStart.getY() + 1] = Tileset.MOUNTAIN;
            } else {
                world[this.nextStart.getX()][nextStart.getY() + 1] = Tileset.MOUNTAIN;
            }
        }

    }
}
