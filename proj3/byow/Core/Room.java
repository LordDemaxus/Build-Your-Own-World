package byow.Core;


import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;


import java.util.Random;

/**
 * Creates rectangular rooms of different sizes at different positions
 * Is also used to create hallways using arguments from the VertHallway and HoriztonalHallway classes
 * @author William Nonnemaker, Medhaav Chandra Mahesh
 */


public class Room {


    private int width;
    private int height;

    private Position thisPos;

    private boolean inBounds = true;

    private boolean northExit = false;
    private boolean westExit = false;
    private boolean eastExit = false;
    private boolean southExit = false;

    public Room(TETile[][] world, Position p, Random rand, String entrance, boolean firstRoom) {
        height = rand.nextInt(6) + 4;
        width = rand.nextInt(6) + 4;
        thisPos = findNewStartPos(entrance, p, rand);
        if (!Room.inBounds(world, thisPos, width, height, entrance)) {
            inBounds = false;
            return;
        }
        BuildingBlock.makeRoom(world, thisPos, width, height);
        setDirections(rand, firstRoom);
        entranceCheck(entrance);
        floorCheck(world, p, entrance);
    }

    public static void makeFirstRoom(TETile[][] world, long seed) {
        Random rand = new Random(seed);
        int startX = rand.nextInt(30) + 20;
        int startY = rand.nextInt(15) + 10;
        Position startPos = new Position(startX, startY);
        Room r = new Room(world, startPos, rand, "door", true);
        r.buildOut(world, r.getThisPos(), rand, true);
        world[r.thisPos.getX() + r.width / 2][r.thisPos.getY()] = Tileset.LOCKED_DOOR;
    }

    public void buildOut(TETile[][] world, Position p, Random rand, boolean firstRoom) {
        if (!inBounds) {
            return;
        }
        boolean firstHall = false;
        if (firstRoom) {
            firstHall = true;
        }
        if (northExit) {
            int hallStart = rand.nextInt(width - 2);
            Position hallPos = p.shift(hallStart, height + 1);
            VertHallway h = new VertHallway(world, hallPos, true, rand, false);
            h.buildOut(world, h.getNextStart(), rand, true, firstHall);
            if (h.isInBounds()) {
                world[hallPos.getX() + 1][hallPos.getY() - 1]
                        = BuildingBlock.floorType(hallPos.getX() + hallPos.getY());
            }
        }
        if (westExit) {
            int hallStart = rand.nextInt(height - 2) + 1;
            Position hallPos = p.shift(0, hallStart);
            HorizontalHallway h = new HorizontalHallway(world, hallPos, false, rand, false);
            h.buildOut(world, h.getNextStart(), rand, false, false);
            if (h.isInBounds()) {
                world[hallPos.getX()][hallPos.getY() + 1] =
                        BuildingBlock.floorType(hallPos.getX() + hallPos.getY());
            }
        }
        if (eastExit) {
            int hallStart = rand.nextInt(height - 2) + 1;
            Position hallPos = p.shift(width - 1, hallStart);
            HorizontalHallway h = new HorizontalHallway(world, hallPos, true, rand, false);
            h.buildOut(world, h.getNextStart(), rand, true, firstHall);
            if (h.isInBounds()) {
                world[hallPos.getX()][hallPos.getY() + 1] =
                        BuildingBlock.floorType(hallPos.getX() + hallPos.getY());
            }
        }
        if (southExit) {
            int hallStart = rand.nextInt(width - 2) + 1;
            Position hallPos = p.shift(hallStart, 0);
            VertHallway h = new VertHallway(world, hallPos, false, rand, false);
            h.buildOut(world, h.getNextStart(), rand, false, false);
            if (h.isInBounds()) {
                world[hallPos.getX()][hallPos.getY()] =
                        BuildingBlock.floorType(hallPos.getX() + hallPos.getY());
            }
        }
    }

    public Position getThisPos() {
        return thisPos;
    }

    public boolean isInBounds() {
        return inBounds;
    }

    public static int getRandomWidth(Random rand) {
        return rand.nextInt(10);
    }

    public void setDirections(Random rand, boolean firstRoom) {
        int numHalls = rand.nextInt(3) + 2;
        int start = rand.nextInt(numHalls) + 1;
        while (numHalls > 0) {
            switch (start % 4) {
                case 0:
                    northExit = !northExit;
                    break;
                case 1:
                    eastExit = !eastExit;
                    break;
                case 2:
                    westExit = !westExit;
                    break;
                case 3:
                    southExit = !southExit;
                    break;
                default:
                    break;
            }
            start++;
            numHalls--;
        }
        if (firstRoom) {
            northExit = true;

        }
    }

    public void entranceCheck(String entrance) {
        switch (entrance) {
            case "north":
                if (northExit) {
                    northExit = false;
                }
                break;
            case "door":
            case "south":
                if (southExit) {
                    southExit = false;
                }
                break;
            case "east":
                if (eastExit) {
                    eastExit = false;
                }
                break;
            case "west":
                if (westExit) {
                    westExit = false;
                }
                break;
            default:
                break;
        }
    }


    public Position findNewStartPos(String entrance, Position p, Random rand) {
        switch (entrance) {
            case "north":
                int xSet = rand.nextInt(this.width - 2) + 1;
                return p.shift(-xSet, -this.height);
            case "south":
                xSet = rand.nextInt(this.width - 2);
                return p.shift(-xSet, 0);
            case "east":
                int ySet = rand.nextInt(this.height - 2) + 1;
                return p.shift(-this.width, -ySet);
            case "west":
                ySet = rand.nextInt(this.height - 2) + 1;
                return p.shift(0, -ySet);
            default:
                return p;
        }
    }

    public static boolean inBounds(TETile[][] world, Position p,
                                   int width, int height, String entrance) {
        if (entrance.equals("west") || entrance.equals("south")) {
            Position temp = p.shift(1, 1);
            boolean b = HorizontalHallway.inBoundsH(world, temp, width)
                    && VertHallway.inBoundsH(world, temp, height)
                    && HorizontalHallway.inBoundsH(world, temp.shift(0, height), width)
                    && VertHallway.inBoundsH(world, temp.shift(width, 0), height);
            return b;
        } else {
            Position temp = p.shift(-1, -1);
            boolean b = HorizontalHallway.inBoundsH(world, temp, width)
                    && VertHallway.inBoundsH(world, temp, height)
                    && HorizontalHallway.inBoundsH(world, temp.shift(0, height), width)
                    && VertHallway.inBoundsH(world, temp.shift(width, 0), height);
            return b;
        }
    }

    public void floorCheck(TETile[][] world, Position p, String entrance) {
        switch (entrance) {
            case "north":
                world[p.getX()][p.getY()] = BuildingBlock.floorType(p.getX() + p.getY());
                break;
            case "south":
                world[p.getX() + 1][p.getY()] = BuildingBlock.floorType(p.getX() + p.getY());
                break;
            case "east":
                world[p.getX() - 1][p.getY() + 1] = BuildingBlock.floorType(p.getX() + p.getY());
                break;
            case "west":
                world[p.getX()][p.getY() + 1] = BuildingBlock.floorType(p.getX() + p.getY());
                break;
            default:
                break;
        }
    }
}
