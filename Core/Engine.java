package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import edu.princeton.cs.introcs.StdDraw;
import static byow.TileEngine.Tileset.*;

public class Engine {
    private ArrayList<Room> roomArrayList = new ArrayList<>();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private static final int HUD_HEIGHT = 2;
    private TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
    private Position avatarPos = new Position(0, 0);
    private Position portalPos1 = new Position(0, 0);
    private Position portalPos2 = new Position(0, 0);
    private Random random;
    private PrintWriter save;
    private int score = 0;
    private int coinNum = 0;
    private boolean winTheGame = false;
    private static final String SAVEDSTRING = "savedString.txt";
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    private Random getRandom(long seed) {
        return new Random(seed);
    }

    public void interactWithKeyboard() {
        makeBlank();
        makeMenu();
        StdDraw.text(WIDTH / 2 + 8, HEIGHT * 17 / 8, "FUN GAME");
        StdDraw.show();
        StdDraw.enableDoubleBuffering();
        String inputForSave = "";
        char c;
        boolean off = false;
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            c = Character.toLowerCase(StdDraw.nextKeyTyped());
            if (c == 'n') {
                inputForSave += c;
                inputForSave = makeMenuN(inputForSave);
                break;
            }
            if (c == 'h') {
                makeHelpMenu();
            }
            if (c == 'l') {
                loadGame();
            }
            if (off && c == 'q') {
                System.out.println("quit");
                break;
            }
            if (c == ':') {
                off = true;
                System.out.println("colon");
            } else {
                off = false;

            }
        }
        saveGame(inputForSave);
        exitGame();
    }
    private void loadGame() {
        String data = loadFile();
        System.out.println("loadgame");
        System.out.println(data);
        loadWorld(data);
        startPlay(data);
    }
    private String loadFile() {
        String data = "";
        try {
            data = new String(Files.readAllBytes(Paths.get(SAVEDSTRING)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    private void saveGame(String inputForSave) {
        try {
            System.out.println("SaveGame");
            System.out.println(inputForSave);
            save = new PrintWriter("savedString.txt");
            save.println(inputForSave);
            save.flush();
            save.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void exitGame() {
        System.exit(0);
    }
    //Making N Menu
    private String makeMenuN(String inputForSave) {
        String input = "";
        long seed = 0;
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        Font menu = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(font);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 50,
                "(n) Give me your favorite number");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 40, "and enter 's' in the end");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 20, "Input: " + input);
        StdDraw.show();

        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char cha = StdDraw.nextKeyTyped();
            inputForSave += cha;
            if (cha == 's') {
                break;
            }
            input += cha;
            StdDraw.clear(Color.black);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 50,
                    "(n) Give me your favorite number");
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 40, "and enter 's' in the end");
            StdDraw.text(WIDTH / 2, HEIGHT / 4, "N: " + input);
            StdDraw.show();
        }
        seed = Long.parseLong(input);
        return startNewGame(seed, inputForSave);
    }
    private String startNewGame(long seed, String inputForSave) {
        initWorld(seed);
        return startPlay(inputForSave);
    }
    private String startPlay(String inputForSave) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + HUD_HEIGHT);
        ter.renderFrame(finalWorldFrame);
        boolean off = false;
        String tileDescription = "";
        while (true) {
            Font font = new Font("Monaco", Font.BOLD, 15);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(font);
            StdDraw.text(WIDTH / 2 - 34, HEIGHT + HUD_HEIGHT / 2 - 2, score + " COINS");
            StdDraw.show();
            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();
            if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
                if (!tileDescription.equals(finalWorldFrame[mouseX][mouseY].description())) {
                    tileDescription = finalWorldFrame[mouseX][mouseY].description();
                    ter.renderFrame(finalWorldFrame);
                    //Font font = new Font("Monaco", Font.BOLD, 15);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(font);
                    StdDraw.text(WIDTH / 2, HEIGHT + HUD_HEIGHT / 2, tileDescription);
                    StdDraw.show();
                }

            }

            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char movingChar = StdDraw.nextKeyTyped();

            if ("asdw".indexOf(movingChar) >= 0) {
                inputForSave += movingChar;
                playGame(movingChar);
                ter.renderFrame(finalWorldFrame);
                if (winTheGame) {
                    makeWinMenu();
                }
            }
            if (off && movingChar == 'q') {
                System.out.println("quit");
                break;
            }
            if (movingChar == ':') {
                off = true;
                System.out.println("colon");
            } else {
                off = false;

            }
        }
        return inputForSave;
    }

    //Making the Main Menu//
    private void makeMenu() {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        Font menu = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2 + 8, HEIGHT * 9 / 8, "(n) New Game");
        StdDraw.text(WIDTH / 2 + 8, HEIGHT * 8 / 8, "(l) Load Game");
        StdDraw.text(WIDTH / 2 + 8, HEIGHT * 7 / 8, "(q) Quick World");
        StdDraw.text(WIDTH / 2 + 8, HEIGHT * 6 / 8, "(h) want some help?");
        StdDraw.show();
    }
    private void makeHelpMenu() {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 15);
        Font menu = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT + 50, "DESCRIPTION OF THE GAME");
        StdDraw.text(WIDTH / 2, HEIGHT + 45, "             Our Game Mechanic is that our Avatar is wondering around the maze. Our Avatar needs to ensure ");
        StdDraw.text(WIDTH / 2, HEIGHT + 40, "             that he get all the coins without running into the bomb! There are several bombs within the maze");
        StdDraw.text(WIDTH / 2, HEIGHT + 35, "             but with meticulous move and luck, our Avatar will safely collect all the coins and win the game!");
        StdDraw.text(WIDTH / 2, HEIGHT + 30, "              Use ‘w’, ‘a’, ’s’, ‘d’ moves to enter through the portal and the resulting outcome will be that our Avatar will appear in a room randomly placed in the maze.");

        StdDraw.text(WIDTH / 2, HEIGHT + 20, "PORTAL");
        StdDraw.text(WIDTH / 2, HEIGHT + 15, "When the Avatar enters the portal, it will come out to a random room.");

        StdDraw.text(WIDTH / 2, HEIGHT +5, "Press 'n' To Start");
        StdDraw.text(WIDTH / 2, HEIGHT -20, "Press 'n' To Start");
        StdDraw.text(WIDTH / 2, HEIGHT -30, "Press 'n' To Start");
        StdDraw.text(WIDTH / 2, HEIGHT -40, "Press 'n' To Start");
        StdDraw.text(WIDTH / 2, HEIGHT -50, "Press 'n' To Start");
        StdDraw.show();
    }

    //Making the Blank Menu//
    private void makeBlank() {
        StdDraw.clear();
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(WIDTH * 10, HEIGHT * 20);
        StdDraw.setXscale(0, 100);
        StdDraw.setYscale(0, 100);
        StdDraw.clear(Color.black);
    }
    private void makeEndMenu() {
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char cha = StdDraw.nextKeyTyped();
            if (cha == 'q') {
                exitGame();
            }

            Font font = new Font("Monaco", Font.BOLD, 100);
            Font menu = new Font("Monaco", Font.PLAIN, 100);
            StdDraw.setPenColor(Color.white);
            StdDraw.setFont(font);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Game Over");
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 50, "Press 'q' To Exit");
            StdDraw.show();
        }
    }
    private void makeWinMenu() {
        System.out.println("makeWinMenu");
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char cha = StdDraw.nextKeyTyped();
            if (cha == 'q') {
                exitGame();
            }

            Font font = new Font("Monaco", Font.BOLD, 100);
            Font menu = new Font("Monaco", Font.PLAIN, 100);
            StdDraw.setPenColor(Color.white);
            StdDraw.setFont(font);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "YaY!! You Win");
            StdDraw.show();
        }
    }
    private void avatar() {
        if (validateAvatar(avatarPos)) {
            finalWorldFrame[avatarPos.getX()][avatarPos.getY()] = Tileset.AVATAR;
        }
    }

    private boolean validateAvatar(Position pos) {
        if (pos.getX() < 0 || pos.getX() > WIDTH - 1 || pos.getY() < 0 || pos.getY() > HEIGHT - 1) {
            return  false;
        }
        if (((finalWorldFrame[pos.getX()][pos.getY()]).equals(Tileset.FLOOR))
                || (finalWorldFrame[pos.getX()][pos.getY()]).equals(Tileset.COIN)
                || (finalWorldFrame[pos.getX()][pos.getY()]).equals(MOUNTAIN)) {
            return true;
        }
        return false;
    }

    public void playGame(char moveKey) {
        int newPosX = -1;
        int newPosY = -1;

        if (moveKey == 'w') {
            newPosX = avatarPos.getX();
            newPosY = avatarPos.getY() + 1;
        }
        if (moveKey == 'a') {
            newPosX = avatarPos.getX() - 1;
            newPosY = avatarPos.getY();
        }
        if (moveKey == 'd') {
            newPosX = avatarPos.getX() + 1;
            newPosY = avatarPos.getY();
        }
        if (moveKey == 's') {
            newPosX = avatarPos.getX();
            newPosY = avatarPos.getY() - 1;
        }

        if (validateAvatar(new Position(newPosX, newPosY))) {
            finalWorldFrame[avatarPos.getX()][avatarPos.getY()] = FLOOR;
            avatarPos.setX(newPosX);
            avatarPos.setY(newPosY);
            addScore();
            if (finalWorldFrame[avatarPos.getX()][avatarPos.getY()].equals(MOUNTAIN)) {
                makeEndMenu();
            } else if (score == coinNum) {
                winTheGame = true;
            }
            finalWorldFrame[avatarPos.getX()][avatarPos.getY()] = Tileset.AVATAR;
        }
       /* if ((avatarPos.getX() == portalPos1.getX()
                && avatarPos.getY() == portalPos1.getY())
                    || (avatarPos.getX() == portalPos2.getX()
                        && avatarPos.getY() == portalPos2.getY())) {
           // portal();
        } */
    }
    private void addScore() {
        if (finalWorldFrame[avatarPos.getX()][avatarPos.getY()].equals(COIN)) {
            score += 1;
            System.out.println("score : "+score);
        }
    }
    private void portal() {
        int anotherSpot = random.nextInt(roomArrayList.size() - 2) + 1;
        int anotherSpotX = roomArrayList.get(anotherSpot).getX();
        int anotherSpotY = roomArrayList.get(anotherSpot).getY();
        if (finalWorldFrame[anotherSpotX][anotherSpotY].equals(Tileset.FLOOR)) {
            avatarPos.setX(anotherSpotX);
            avatarPos.setY(anotherSpotY);
        }
    }
    private Position makePortal() {
        int portalRand = random.nextInt(roomArrayList.size() - 2) + 1;
        int portalX = roomArrayList.get(portalRand).getPos().getX() + 1;
        int portalY = roomArrayList.get(portalRand).getPos().getY() + 1;
        Position portalPos = new Position(portalX, portalY);
        finalWorldFrame[portalPos.getX()][portalPos.getY()] = SAND;
        return portalPos;
    }

    private int makeCoin() {
        for (int i = 1; i < roomArrayList.size(); i += 20) {
            for (int x = roomArrayList.get(i).getPos().getX() + 1; x < roomArrayList.get(i).getPos().getX() + roomArrayList.get(i).getX() -1; x++) {
                for (int y = roomArrayList.get(i).getPos().getY() + 1; y < roomArrayList.get(i).getPos().getY() + roomArrayList.get(i).getY() -1; y++) {
                    if (finalWorldFrame[x][y].equals(FLOOR)) {
                        finalWorldFrame[x][y] = COIN;
                    }
                    coinNum += 1;
                }
            }
        }
        System.out.println("coinNum makeCoin: "+ coinNum);
        return coinNum;
    }

    private void makeBomb() {
        for (Room r: roomArrayList) {
            if (r.getX() > 3 && r.getY() > 3) {
                int x = RandomUtils.uniform(random, r.getX() - 1);
                int y = RandomUtils.uniform(random, r.getY() - 1);
                int xx = r.getPos().getX() + x;
                int yy = r.getPos().getY() + y;
                if (finalWorldFrame[xx][yy].equals(FLOOR)
                        || finalWorldFrame[xx][yy].equals(COIN) ) {
                    if (finalWorldFrame[xx][yy].equals(COIN)) {
                        coinNum -= 1;
                    }
                    finalWorldFrame[xx][yy] = MOUNTAIN;
                }
            }
        }
    }

    private TETile[][] initWorld(long seed) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                finalWorldFrame[x][y] = Tileset.NOTHING;
            }
        }
        random = new Random(seed);
        drawRooms();
        drawRooms();
        findPath();
        makeWall();
        avatarPos.setX(roomArrayList.get(0).getDoorPos().getX());
        avatarPos.setY(roomArrayList.get(0).getDoorPos().getY());
        portalPos1 = makePortal();
        portalPos2 = makePortal();
        avatar();
        makeCoin();
        makeBomb();
        System.out.println("coinNum AftermakeBomb: "+ coinNum);
        return finalWorldFrame;
    }
    private void moveAvatar(String moveKeys) {
        int moveKeysLength = moveKeys.length();
        for (int i = 0; i < moveKeysLength; i++) {
            char c = moveKeys.charAt(i);
            playGame(c);
        }
    }
    public TETile[][] interactWithInputString(String input) {
        long seed = 0;
        String moveKeys = "";
        String keysForSave = "";
        String lowerCaseInput = input.toLowerCase();

        if (input.toLowerCase().contains("n") && input.toLowerCase().contains("s")) {
            int start = input.toLowerCase().indexOf("n") + 1;
            int firstS = input.toLowerCase().indexOf("s");
            seed = Long.parseLong(input.substring(start, firstS));
            initWorld(seed);

            if (lowerCaseInput.contains(":q")) {
                int last = input.toLowerCase().indexOf(":q");
                moveKeys = input.toLowerCase().substring(firstS + 1, last);
                keysForSave = lowerCaseInput.substring(0, last);
            } else {
                moveKeys = input.toLowerCase().substring(firstS + 1);
                keysForSave = lowerCaseInput;
            }

        } else if (input.toLowerCase().contains("l")) {
            String data = loadFile();
            keysForSave += loadWorld(data);
            int indexOfL = input.toLowerCase().indexOf("l");
            if (lowerCaseInput.contains(":q")) {
                int last = input.toLowerCase().indexOf(":q");
                moveKeys = input.toLowerCase().substring(indexOfL + 1, last);
            } else {
                moveKeys = input.toLowerCase().substring(indexOfL + 1);
            }
            keysForSave += moveKeys;
        }

        moveAvatar(moveKeys);

        if (lowerCaseInput.contains(":q")) {
            saveGame(keysForSave);
        }

        return finalWorldFrame;
    }

    private String loadWorld(String input) {
        long seed = 0;
        String moveKeys = "";
        String keysForSave = "";
        String lowerCaseInput = input.toLowerCase();

        if (input.toLowerCase().contains("n") && input.toLowerCase().contains("s")) {
            int start = input.toLowerCase().indexOf("n") + 1;
            int firstS = input.toLowerCase().indexOf("s");
            seed = Long.parseLong(input.substring(start, firstS));
            initWorld(seed);

            if (lowerCaseInput.contains(":q")) {
                int last = input.toLowerCase().indexOf(":q");
                moveKeys = input.toLowerCase().substring(firstS + 1, last);
                keysForSave = lowerCaseInput.substring(0, last);
            } else {
                moveKeys = input.toLowerCase().substring(firstS + 1);
                keysForSave = lowerCaseInput;
            }
            moveAvatar(moveKeys);
        }

        return keysForSave;
    }

    public void drawGrass() {
        for (int i = 1; i < roomArrayList.size() - 1; i += 2) {
            int grassX = roomArrayList.get(i).getPos().getX();
            int grassY = roomArrayList.get(i).getPos().getY();
        }
    }

    public void drawRooms() {
        for (int i = 0; i < 400; i++) {
            Position p = new Position((RandomUtils.uniform(random, WIDTH - 1)),
                    (RandomUtils.uniform(random, HEIGHT - 1)));
            int w = random.nextInt((10 - 3) + 1) + 7;
            int h = random.nextInt((9 - 4) + 1) + 5;
            if (validateRoom(p, w, h)) {
                makeRoom(p, w, h);
                int d =  random.nextInt(10) % 4;
                Position doorPos = makeDoor(p, w, h, d);
                Room room = new Room(p, w, h, doorPos);
                roomArrayList.add(room);
            }
        }
    }
    public Position makeDoor(Position p, int w, int h, int d) {
        int doorX = random.nextInt(w - 2) + 1;
        int doorY = random.nextInt(h - 2) + 1;
        int doorXLocation =  p.getX() + doorX;
        int doorYLocation = p.getY() + doorY;
        int doorPosX;
        int doorPosY;
        if (d == 0) {
            doorPosX = p.getX();
            doorPosY =  doorYLocation;
        } else if (d == 1) {
            doorPosX = p.getX() + w - 1;
            doorPosY =  doorYLocation;
        } else if (d == 2) {
            doorPosX = doorXLocation;
            doorPosY = p.getY() + h - 1;
        } else {
            doorPosX = doorXLocation;
            doorPosY = p.getY();
        }
        Position doorPos = new Position(doorPosX, doorPosY);
        finalWorldFrame[doorPosX][doorPosY] = Tileset.LOCKED_DOOR;
        return doorPos;
    }
    public void findPath() {
        for (int i = 0; i < roomArrayList.size() - 1; i++) {
            Room first = roomArrayList.get(i);
            Room second = roomArrayList.get(i + 1);
            makeHallway(first, second);
        }
    }

    private void makeHallway(Room first, Room second) {
        if (first.getDoorPos().getX() - second.getDoorPos().getX() < 0) {
            for (int x = first.getDoorPos().getX(); x < second.getDoorPos().getX() + 1; x++) {
                finalWorldFrame[x][first.getDoorPos().getY()] = FLOOR;
            }
            if (first.getDoorPos().getY() - second.getDoorPos().getY() < 0) {
                for (int y = first.getDoorPos().getY(); y < second.getDoorPos().getY() + 1; y++) {
                    finalWorldFrame[second.getDoorPos().getX()][y] = FLOOR;
                }
            } else {
                for (int y = second.getDoorPos().getY(); y < first.getDoorPos().getY() + 1; y++) {
                    finalWorldFrame[second.getDoorPos().getX()][y] = FLOOR;
                }
            }
        } else {
            for (int x = second.getDoorPos().getX(); x < first.getDoorPos().getX() + 1; x++) {
                finalWorldFrame[x][second.getDoorPos().getY()] = FLOOR;
            }
            if (first.getDoorPos().getY() - second.getDoorPos().getY() < 0) {
                for (int y = first.getDoorPos().getY(); y < second.getDoorPos().getY() + 1; y++) {
                    finalWorldFrame[first.getDoorPos().getX()][y] = FLOOR;
                }
            } else {
                for (int y = second.getDoorPos().getY(); y < first.getDoorPos().getY() + 1; y++) {
                    finalWorldFrame[first.getDoorPos().getX()][y] = FLOOR;
                }
            }
        }
    }

    private void makeWall() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if ((finalWorldFrame[x][y]).equals(Tileset.FLOOR)) {
                    for (int i = x - 1; i < x + 2; i++) {
                        for (int j = y - 1; j < y + 2; j++) {
                            if (!finalWorldFrame[i][j].equals(FLOOR)
                                    && !finalWorldFrame[i][j].equals(LOCKED_DOOR)) {
                                finalWorldFrame[i][j] = WALL;
                            }
                        }
                    }
                }
            }
        }
    }


    //MAKING A ROOM ONLY IF YOU HAVE A VALID POINT//
    public void makeRoom(Position p, int width, int height) {
        for (int x = p.getX() + 1; x < p.getX() + width - 1; x++) {
            for (int y = p.getY() + 1; y < p.getY() + height - 1; y++) {
                finalWorldFrame[x][y] = FLOOR;
            }
        }
    }

    //To make sure the points are not out of bound and not overlapping//
    //VALIDATE METHOD TO MAKE SURE TO BUILD A ROOM OR NOT  ; CHECKING OVERLAP AS WELL?//
    //points you are given is valid, after that check if it is overlapping//
    public boolean validateRoom(Position p, int width, int height) {
        if (p.getX() + width > WIDTH - 3 || p.getY() + height > HEIGHT - 3 || p.getX() + width < 0
                || p.getY() + height < 0 || p.getX() < 3 || p.getY() < 3) {

            return false;
        }
        //overlapping//
        //had a wall you cannot make a room//
        //searching for the entire area, one of this is floor or wall, then it says overlaps//
        for (int x = p.getX(); x < p.getX() + width; x++) {
            for (int y = p.getY(); y < p.getY() + height; y++) {
                if (!((finalWorldFrame[x][y]).equals(Tileset.NOTHING))) {
                    return false;
                }
            }
        }
        return true;
    }

}