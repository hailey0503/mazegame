package byow.Core;


public class Room {
    private Position pos;
    private Position doorPos;
    private int x;
    private int y;


    public Room(Position pos, int w, int h, Position doorPos) {
        this.pos = pos;
        this.doorPos = doorPos;
        x = w;
        y = h;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Position getPos() {
        return pos;
    }
    public Position getDoorPos() {
        return doorPos;
    }

}
