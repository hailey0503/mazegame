package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class Test {
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        final int width = 80;
        final int height = 40;
        ter.initialize(width, height);
        Engine engine = new Engine();
        TETile[][] world = engine.interactWithInputString("n1275834sddww");
        engine.interactWithKeyboard();
        ter.renderFrame(world);
    }

}
