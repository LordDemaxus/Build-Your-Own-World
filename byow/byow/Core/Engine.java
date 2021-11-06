package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

/**
 * Opens the main menu and takes in arguments used to initialized the world
 * Allows user functions such as creating a new world, saving and loading the game, quitting,
 * watching your previously played game, and choosing an avatar 
 * @author Medhaav Chandra Mahesh, William Nonnemaker 
 */



public class Engine {
    TERenderer ter = new TERenderer();
    public static final int WIDTH = 100;
    public static final int HEIGHT = 60;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        World.createMainMenu();
        String avatar = "42";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char s = StdDraw.nextKeyTyped();
                if ((s == 'N' || s == 'n')) {
                    World.newWorld(avatar);
                    System.exit(0);
                } else if (s == 'L' || s == 'l') {
                    World.loadWorld(avatar);
                    System.exit(0);
                } else if (s == 'Q' || s == 'q') {
                    System.exit(0);
                } else if (s == 'R' || s == 'r') {
                    World.replayPrev();
                    System.exit(0);
                } else if (s == 'C' || s == 'c') {
                    World.createAvatarMenu();
                    avatar = Avatar.chooseAvatar();
                    World.createMainMenu();
                }
            }
        }
    }

    /**
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
       
        Character first = input.charAt(0);
        String middle = input.substring(1, input.length());
        char[] middleChars = middle.toCharArray();
        TETile[][] finalWorldFrame = null;
        if (first == 'N' || first == 'n') {
            String d = "0";
            for (int i = 0; i < middle.length(); i += 1) {
                if (middleChars[i] == 'S' || middleChars[i] == 's') {
                    finalWorldFrame =
                            World.newWorldString(null, Long.parseLong(d),
                                    middle.substring(i + 1, middle.length()), null, "");
                    break;
                } else {
                    d += middleChars[i];
                }
            }
        } else if (first == 'L' || first == 'l') {
            finalWorldFrame = World.loadWorldString(middle.substring(0, middle.length()));
        }
        return finalWorldFrame;
    }
}
