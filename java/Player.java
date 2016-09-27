
public class Player {

    private int score = 0;
    private Rectangle[] bricksArr = new Rectangle[0];
    // for a 16 X 8 player field
    private boolean[][] filledLocs = new boolean[16][8];

    public Player() {
    }

    public int getScore() {
        return score;
    }

    public Rectangle[] getBricksArr() {
        return bricksArr;
    }

    public boolean[][] getFilledLocs() {
        return filledLocs;
    }

    public void setBricksArr(Rectangle[] bricksArr) {
        this.bricksArr = bricksArr;
    }

    public boolean isSpotTaken(int y, int x) {
        return filledLocs[y][x];
    }

    public void setFilledSpot(int y, int x, boolean isFilled) {
        filledLocs[y][x] = isFilled;
    }

    public void incrementScore() {
        score++;
    }

    public void clearData() {
        bricksArr = new Rectangle[0];
        filledLocs = new boolean[16][8];
    }
}
