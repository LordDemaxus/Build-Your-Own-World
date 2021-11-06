package byow.Core;


import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Creates vertical hallways
 *
 * @author William Nonnemaker
 */


public class VertHallway {

    private int length;
    private Position nextStart;
    private boolean inBounds = true;

    public VertHallway(TETile[][] world, Position p,
                       boolean closeStart, Random rand, boolean fromHall) {
        length = BuildingBlock.getRandomLength(rand);
        if (closeStart) {
            nextStart = p.shift(0, length);
            if (!VertHallway.inBounds(world, p, length, fromHall)) {
                inBounds = false;
                return;
            }
            BuildingBlock.makeVerticalHallway(world, p, length);
        } else {
            Position buildSpot = p.shift(-1, -length);
            nextStart = buildSpot;
            if (!VertHallway.inBounds(world, buildSpot, length, fromHall)) {
                inBounds = false;
                return;
            }
            BuildingBlock.makeVerticalHallway(world, buildSpot, length);
        }
    }


    public void buildOut(TETile[][] world, Position p, Random rand,
                         boolean closeStart, boolean firstHall) {
        if (!inBounds) {
            return;
        }
        int halls = rand.nextInt(4);
        if (firstHall) {
            halls = 1;
        }
        switch (halls) {
            case 1:
                HorizontalHallway h = new HorizontalHallway(world, p.shift(0, 0), true, rand, true);
                h.buildOut(world, h.getNextStart(), rand, true, false);
                fillIn(world, h.isInBounds());
                inBoundsFill(world, h.isInBounds(), closeStart);
                break;
            default:
                if (closeStart) {
                    Room r = new Room(world, p, rand, "south", false);
                    r.buildOut(world, r.getThisPos(), rand, false);
                    inBoundsFill(world, r.isInBounds(), closeStart);
                } else {
                    Room r = new Room(world, p.shift(1, 0), rand, "north", false);
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
            return inBoundsH(world, p, length) && inBoundsH(world, p, length);
        }
        return inBoundsH(world, p.shift(1, 0), length)
                && inBoundsH(world, p.shift(-1, 0), length) && inBoundsH(world, p, length);
    }

    public static boolean inBoundsH(TETile[][] world, Position p, int length) {
        if (p.getX() <= 0 || p.getX() > World.WIDTH - 3
                || p.getY() <= 0 || p.getY() + length > World.HEIGHT - 5) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (world[p.getX()][p.getY() + i] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }

    public void fillIn(TETile[][] world, boolean b) {
        Position p = this.getNextStart();
        if (b) {
            world[p.getX() + 1][p.getY()] = BuildingBlock.floorType(p.getX() + p.getY());
            world[p.getX()][p.getY()] = Tileset.MOUNTAIN;
            world[p.getX()][p.getY() + 1] = Tileset.MOUNTAIN;
            world[p.getX()][p.getY() + 2] = Tileset.MOUNTAIN;
            return;
        }
    }

    public void inBoundsFill(TETile[][] world, boolean b, boolean closeStart) {
        if (!b) {
            if (closeStart) {
                world[nextStart.getX() + 1][nextStart.getY() - 1] = Tileset.MOUNTAIN;
            } else {
                world[nextStart.getX() + 1][nextStart.getY()] = Tileset.MOUNTAIN;
            }
        }
    }

}
