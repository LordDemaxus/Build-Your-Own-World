package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import static byow.Core.Utils.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import static byow.Core.Avatar.*;


import java.awt.Font;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * Initizalizes the world
 *
 * @author Medhaav Chandra Mahesh
 */


public class World {
    public static final int WIDTH = 100;
    public static final int HEIGHT = 60;

    public static final File SAVEITEMS = join(new File(System.getProperty("user.dir")),
            "saved.txt");

    /* World creation
     *
     *
     *
     *
     */

    public static void fillBoardWithNothing(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }


    public static TETile[][] makeRandomWorld(TETile[][] world, long input) {
        fillBoardWithNothing(world);
        Room.makeFirstRoom(world, input);
        return world;
    }


    /* hud that shows the tile that the mouse is pointing to */

    public static void hud(TETile[][] world, TETile avatar) {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(0, HEIGHT - 1, WIDTH / 2, 1);
        int posX = Double.valueOf(StdDraw.mouseX()).intValue();
        int posY = Double.valueOf(StdDraw.mouseY()).intValue();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        String s;
        if (0 <= posX && posX < 100 && 0 <= posY && posY < 60) {
            if (world[posX][posY] == Tileset.WATER) {
                s = "water";
            } else if (world[posX][posY] == Tileset.MOUNTAIN) {
                s = "mountain";
            } else if (world[posX][posY] == Tileset.LOCKED_DOOR) {
                s = "locked door";
            } else if (world[posX][posY] == Tileset.GRASS) {
                s = "grass";
            } else if (world[posX][posY] == avatar) {
                s = "character";
            } else {
                s = "";
            }
        } else {
            s = "";
        }
        StdDraw.text(s.length() / 2, HEIGHT - 1, s);
        StdDraw.show();
    }

    private static void writeMessage(Color color, String message) {
        StdDraw.clear(color);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, message);
        StdDraw.show();
    }

    /* creates the main menu */
    public static void createMainMenu() {
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH / 2, 50, "Build Your Own World");
        StdDraw.setPenColor(Color.PINK);
        StdDraw.setFont(new Font("Monaco", Font.ITALIC, 20));
        StdDraw.text(WIDTH / 2, 40, "New Game(N)");
        StdDraw.text(WIDTH / 2, 35, "Load Game(L)");
        StdDraw.text(WIDTH / 2, 30, "Quit Game(Q)");
        StdDraw.text(WIDTH / 2, 25, "Watch Replay(R)");
        StdDraw.text(WIDTH / 2, 20, "Choose Character(C)");
        StdDraw.show();
    }

    /* creates a new world from keyboard input */
    public static void newWorld(String avatar) {
        writeMessage(Color.BLUE, "Turn all the Water Tiles to Grass");
        StdDraw.pause(500);
        writeMessage(Color.BLACK, "Enter seed and type S when finished");
        String d = "";
        StringBuilder movements = new StringBuilder();
        Position charPos;
        TETile avatarTile = getAvatarTile(avatar);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char s = StdDraw.nextKeyTyped();
                if (s == 'S' || s == 's') {
                    if (d.length() > 0) {
                        charPos = newWorldHelper(null, Long.parseLong(d), null,
                                avatarTile, movements);
                        break;
                    }
                    writeMessage(Color.RED, "No seed has been typed");
                    StdDraw.pause(500);
                    writeMessage(Color.BLACK, "Enter seed and type S when finished");
                } else if (s == '1' || s == '2' || s == '3' || s == '4' || s == '5'
                        || s == '6' || s == '7' || s == '8' || s == '9' || s == '0') {
                    d += s;
                } else {
                    writeMessage(Color.RED, "Only numbers allowed in seed. Retype new seed");
                    StdDraw.pause(1000);
                    d = "";
                    writeMessage(Color.BLACK,
                            "Enter seed and type S when finished");
                }
            }
        }
        saveWorld(d, charPos, avatar, movements.toString());
    }

    /* helper function to create a new world*/
    private static Position newWorldHelper(TETile[][] world, long seed, Position currPos,
                                           TETile avatar, StringBuilder movements) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        if (world == null) {
            world = new TETile[WIDTH][HEIGHT];
            makeRandomWorld(world, seed);
        }
        Position charPos;
        if (currPos == null) {
            charPos = addCharacter(world, avatar);
        } else {
            charPos = currPos.clone();
            world[charPos.x][charPos.y] = avatar;
        }
        ter.renderFrame(world);
        boolean[] over = {false, false};
        while (!over[1]) {
            hud(world, avatar);
            if (StdDraw.hasNextKeyTyped()) {
                char d = StdDraw.nextKeyTyped();
                if (d == ':') {
                    over[0] = true;
                } else if ((d == 'Q' || d == 'q') && over[0]) {
                    over[1] = true;
                } else {
                    movements.append(d);
                    charPos = moveCharacter(world, charPos, d, avatar);
                    ter.renderFrame(world);
                }
            }
        }
        return charPos;
    }

    /* saves the seed of world, avatar pos and type, and */
    private static void saveWorld(String seed, Position pos, String avatar,
                                  String movements) {
        try {
            SAVEITEMS.createNewFile();
        } catch (IOException io) {
            System.out.println("io error");
        }
        String[] items = {seed, String.valueOf(pos.x), String.valueOf(pos.y),
            "nothing", "nothing", avatar, movements};
        writeObject(SAVEITEMS, items);
    }

    /* loads the world from keyboard input */
    public static void loadWorld(String avatar) {
        if (SAVEITEMS.exists()) {
            String[] items = readObject(SAVEITEMS, String[].class);
            Position pos = new Position(Integer.parseInt(items[1]), Integer.parseInt(items[2]));
            if (avatar.equals("42")) {
                avatar = items[5];
            }
            TETile[][] world = new TETile[WIDTH][HEIGHT];
            makeRandomWorld(world, Long.parseLong(items[0]));
            TETile avatarTile = getAvatarTile(avatar);
            Position prevPos = addCharacter(world, avatarTile);
            char[] movementsChar = items[6].toCharArray();
            for (int i = 0; i < items[6].length(); i++) {
                prevPos = moveCharacter(world, prevPos, movementsChar[i], avatarTile);
            }
            StringBuilder movements = new StringBuilder();
            Position position = newWorldHelper(world, Long.parseLong(items[0]),
                    pos, avatarTile, movements);
            saveWorld(items[0], position, avatar, items[6] + movements.toString());
        } else {
            System.out.println("no world has been saved");
        }
    }

    /* creates a new world with a string*/
    public static TETile[][] newWorldString(TETile[][] world, long seed, String terms,
                                            Position currPos, String prevMoves) {
        if (world == null) {
            world = new TETile[WIDTH][HEIGHT];
            makeRandomWorld(world, seed);
        }
        Position charPos;
        if (currPos == null) {
            charPos = addCharacter(world, Tileset.AVATAR);
        } else {
            charPos = currPos.clone();
            world[charPos.x][charPos.y] = Tileset.AVATAR;
        }
        char[] termsChars = terms.toCharArray();
        for (int i = 0; i < terms.length(); i += 1) {
            if (termsChars[i] == ':') {
                if (i + 1 < terms.length() && (termsChars[i + 1] == 'q'
                        || termsChars[i + 1] == 'Q')) {
                    saveWorld(String.valueOf(seed), charPos, "avatar",
                            prevMoves + terms.substring(0, i + 1));
                    break;
                }
            }
            charPos = Avatar.moveCharacter(world, charPos, termsChars[i], Tileset.AVATAR);
        }
        return world;
    }

    /* loads the world from string input */
    public static TETile[][] loadWorldString(String terms) {
        TETile[][] world;
        if (SAVEITEMS.exists()) {
            String[] items = readObject(SAVEITEMS, String[].class);
            Position pos = new Position(Integer.parseInt(items[1]), Integer.parseInt(items[2]));
            world = new TETile[WIDTH][HEIGHT];
            makeRandomWorld(world, Long.parseLong(items[0]));
            char[] movementsChar = items[6].toCharArray();
            TETile avatarTile = getAvatarTile("avatar");
            Position prevPos = addCharacter(world, avatarTile);
            for (int i = 0; i < items[6].length(); i++) {
                prevPos = moveCharacter(world, prevPos, movementsChar[i], avatarTile);
            }
            world = newWorldString(world, Long.parseLong(items[0]), terms, pos, items[6]);
        } else {
            world = null;
            System.out.println("no world has been saved");
        }
        return world;
    }

    /* replays the previous saves from the start and loads to current
    position once replay is complete */
    public static void replayPrev() {
        writeMessage(Color.magenta, "Press Space to Skip in middle of the replay");
        StdDraw.pause(1000);
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        if (SAVEITEMS.exists()) {
            String[] items = readObject(SAVEITEMS, String[].class);
            TETile[][] world = new TETile[WIDTH][HEIGHT];
            makeRandomWorld(world, Long.parseLong(items[0]));
            TETile avatarTile = getAvatarTile(items[5]);
            Position prevPos = addCharacter(world, avatarTile);
            Position posTemp = prevPos.clone();
            char[] movementsChar = items[6].toCharArray();
            for (int i = 0; i < items[6].length(); i++) {
                ter.renderFrame(world);
                StdDraw.pause(100);
                posTemp = moveCharacter(world, posTemp, movementsChar[i], avatarTile);
                if (StdDraw.hasNextKeyTyped()) {
                    if (StdDraw.nextKeyTyped() == ' ') {
                        break;
                    }
                }
            }
            StdDraw.clear();
            loadWorld(items[5]);
        } else {
            System.out.println("no world has been saved");
        }
    }

    /* creates menu for avatar selection */
    public static void createAvatarMenu() {
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH / 2, 50, "Avatar Selection");
        StdDraw.setFont(new Font("Monaco", Font.ITALIC, 20));
        StdDraw.text(WIDTH / 2, 40, "Default (A) @");
        StdDraw.text(WIDTH / 2, 35, "Tree (T) ♠ ");
        StdDraw.text(WIDTH / 2, 30, "Flower (F) ❀");
        StdDraw.text(WIDTH / 2, 25, "Godzilla (G)");
        StdDraw.picture(WIDTH / 2 + "Godzilla".length() + 10, 25, "godzilla.jpg", 5, 5);
        StdDraw.text(WIDTH / 2, 20, "Back (B)");
        StdDraw.show();
    }
}
