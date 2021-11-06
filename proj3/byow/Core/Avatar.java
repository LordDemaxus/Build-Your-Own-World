package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;


/**
 * Allows user to choose characters
 *
 * @author Medhaav Chandra Mahesh 
 */


public class Avatar {

    /* gets tile of corresponding avatar */
    public static TETile getAvatarTile(String avatar) {
        switch (avatar) {
            case "tree":
                return Tileset.TREE;
            case "flower":
                return Tileset.FLOWER;
            case "godzilla":
                return Tileset.GODZILLA;
            case "avatar":
            default:
                return Tileset.AVATAR;
        }
    }

    /* allows user to choose avatar */
    public static String chooseAvatar() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char s = StdDraw.nextKeyTyped();
                if ((s == 'A' || s == 'a')) {
                    return "avatar";
                } else if ((s == 'T' || s == 't')) {
                    return "tree";
                } else if ((s == 'F' || s == 'f')) {
                    return "flower";
                } else if ((s == 'G' || s == 'g')) {
                    return "godzilla";
                } else if ((s == 'B' || s == 'b')) {
                    return "42";
                }
            }
        }
    }

    /* adds character to the world */
    public static Position addCharacter(TETile[][] world, TETile avatar) {
        for (int x = 0; x < World.WIDTH; x += 1) {
            for (int y = 0; y < World.HEIGHT; y += 1) {
                if (world[x][y] == Tileset.GRASS || world[x][y] == Tileset.WATER) {
                    world[x][y] = avatar;
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    /* moves character based on user input - WASD keys */
    public static Position moveCharacter(TETile[][] world, Position charPos,
                                         char s, TETile avatar) {
        world[charPos.x][charPos.y] = Tileset.GRASS;
        if ((s == 'W' || s == 'w') && floorChecker(world[charPos.x][charPos.y + 1])) {
            world[charPos.x][charPos.y + 1] = avatar;
            charPos = charPos.shift(0, 1);
        } else if ((s == 'S' || s == 's') && floorChecker(world[charPos.x][charPos.y - 1])) {
            world[charPos.x][charPos.y - 1] = avatar;
            charPos = charPos.shift(0, -1);
        } else if ((s == 'D' || s == 'd') && floorChecker(world[charPos.x + 1][charPos.y])) {
            world[charPos.x + 1][charPos.y] = avatar;
            charPos = charPos.shift(1, 0);
        } else if ((s == 'A' || s == 'a') && floorChecker(world[charPos.x - 1][charPos.y])) {
            world[charPos.x - 1][charPos.y] = avatar;
            charPos = charPos.shift(-1, 0);
        } else {
            world[charPos.x][charPos.y] = avatar;
        }
        return charPos;
    }

    /* checks if tile is a floor */
    private static boolean floorChecker(TETile tile) {
        if (tile == Tileset.GRASS || tile == Tileset.WATER) {
            return true;
        }
        return false;
    }
}
