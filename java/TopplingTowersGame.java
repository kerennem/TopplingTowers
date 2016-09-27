import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.*;


public class TopplingTowersGame extends JComponent implements ActionListener, KeyListener {

    private int GAME_WIDTH = 720;
    private static int GAME_HEIGHT = 540;
    private static int GRID_UNIT = 25;
    private int BUILDING_MAX_WIDTH = 8 * GRID_UNIT;
    private int BUILDING_MAX_HEIGHT = 16 * GRID_UNIT;
    private static int FLOOR_HEIGHT = 80;
    private int brickStartingX = ((GAME_WIDTH / 2) - BUILDING_MAX_WIDTH) / 2;
    private int brickStartingY = GAME_HEIGHT - FLOOR_HEIGHT - BUILDING_MAX_HEIGHT;

    private static int NUM_OF_LEVELS = 5;
    private static int level = 0;
    private int turn = 0;

    private boolean showCredits = false;
    private boolean showInstructions = false;
    private boolean isDroppingStage = false; // false for choosing brick stage, true for dropping brick stage
    private boolean isFalling = false;
    //1 for player 1 looser, 2 for player 2 looser, -1 for ongoing game
    int gameEnded = -1;

    private static Rectangle[] brickSelection = new Rectangle[6];
    Player[] players = {new Player(), new Player()};
    int selector = 0;

    Image gameEndImage;
    Image scoreImage;
    Image creditsImage;
    Image aboutImage;
    Image instructionsImage;
    Image[] backgroundImages = new Image[NUM_OF_LEVELS];


    // creates images
    public TopplingTowersGame() throws IOException {
        backgroundImages[0] = ImageIO.read(getClass().getResource("BackgroundPyramid.png"));
        backgroundImages[1] = ImageIO.read(getClass().getResource("BackgroundSpace.png"));
        backgroundImages[2] = ImageIO.read(getClass().getResource("BackgroundLondon.png"));
        backgroundImages[3] = ImageIO.read(getClass().getResource("BackgroundTorii.png"));
        backgroundImages[4] = ImageIO.read(getClass().getResource("BackgroundSea.png"));
        gameEndImage = ImageIO.read(getClass().getResource("Win.png"));
        scoreImage = ImageIO.read(getClass().getResource("Score.png"));
        creditsImage = ImageIO.read(getClass().getResource("About.png"));
        aboutImage = ImageIO.read(getClass().getResource("AboutButton.png"));
        instructionsImage = ImageIO.read(getClass().getResource("Instructions.png"));
    }

    public static void main(String[] args) throws IOException {
        fillBrickSelectionArray();
        initGame();
    }

    private static void initGame() throws IOException {
        JFrame window = new JFrame("DESTROY!!!");
        TopplingTowersGame game = new TopplingTowersGame();
        window.add(game);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
        Timer t = new Timer(10, game);
        t.start();
        window.addKeyListener(game);
    }

    private static void fillBrickSelectionArray() {
        for (int i = 0; i < brickSelection.length; i++) {
            int x = convertSelectorToPixels(i);
            brickSelection[i] = createRandomBrick(x, GAME_HEIGHT - FLOOR_HEIGHT + 40);
        }
    }

    private static int convertSelectorToPixels(int i) {
        int x;
        switch (i) {
            case 0:
                x = 25;
                break;
            case 1:
                x = 135;
                break;
            case 2:
                x = 245;
                break;
            case 3:
                x = 385;
                break;
            case 4:
                x = 495;
                break;
            case 5:
                x = 605;
                break;
            default:
                x = 25;
        }
        return x;
    }


    //**********   Graphics **************//


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GAME_WIDTH, GAME_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Background
        g.drawImage(backgroundImages[level % NUM_OF_LEVELS], 0, 0, null);

        // floor
        g.setColor(Color.black);
        g.fillRect(0, GAME_HEIGHT - FLOOR_HEIGHT, GAME_WIDTH, 2);

        // names
        if (turn == 0) {
            g.setColor(new Color(76, 158, 62));
            g.setFont(new Font("TimesRoman", Font.BOLD, 25));
            g.drawString("Player1", GAME_WIDTH / 4 - 75, GAME_HEIGHT - FLOOR_HEIGHT + 27);
            g.setColor(Color.black);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g.drawString("Player2", GAME_WIDTH * 3 / 4 - 75, GAME_HEIGHT - FLOOR_HEIGHT + 27);
        } else {
            g.setColor(new Color(76, 158, 62));
            g.setFont(new Font("TimesRoman", Font.BOLD, 25));
            g.drawString("Player2", GAME_WIDTH * 3 / 4 - 75, GAME_HEIGHT - FLOOR_HEIGHT + 27);
            g.setColor(Color.black);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g.drawString("Player1", GAME_WIDTH / 4 - 75, GAME_HEIGHT - FLOOR_HEIGHT + 27);
        }

        // score
        g.setColor(Color.BLUE);
        g.drawImage(scoreImage, GAME_WIDTH / 4 + 10, GAME_HEIGHT - FLOOR_HEIGHT + 5, null);
        g.drawImage(scoreImage, GAME_WIDTH * 3 / 4 + 10, GAME_HEIGHT - FLOOR_HEIGHT + 5, null);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString(Integer.toString(players[0].getScore()), GAME_WIDTH / 4 + 90, GAME_HEIGHT - FLOOR_HEIGHT + 26);
        g.drawString(Integer.toString(players[1].getScore()), GAME_WIDTH * 3 / 4 + 90, GAME_HEIGHT - FLOOR_HEIGHT + 26);

        // credits
        g.drawImage(aboutImage, 10, 10, null);
        if (showCredits) {
            g.drawImage(creditsImage, 100, 100, null);
        }

        // Selector bricks
        for (int i = 0; i < brickSelection.length; i++) {
            g.drawImage(brickSelection[i].getBrickImage(), brickSelection[i].getX(), brickSelection[i].getY(), null);
        }

        if (selector >= 0) {
            g.setColor(Color.black);
            int x = convertSelectorToPixels(selector);
            g.fillRect(x, GAME_HEIGHT - FLOOR_HEIGHT + 68, 100, 3);

        }

        //player bricks
        for (int i = 0; i < players.length; i++) {
            for (int j = 0; j < players[i].getBricksArr().length; j++) {
                g.drawImage(players[i].getBricksArr()[j].getBrickImage(), players[i].getBricksArr()[j].getX(), players[i].getBricksArr()[j].getY(), null);
            }
        }

        // game ended screen
        if (gameEnded > 0) {
            if (gameEnded == 2) {
                g.drawImage(gameEndImage, 400, 100, null);
            } else {
                g.drawImage(gameEndImage, 40, 100, null);
            }
        }

        // instructions
        if (showInstructions) {
            g.drawImage(instructionsImage, 100, 0, null);
        }

    }

    public void actionPerformed(ActionEvent e) {
        if (players[turn].getBricksArr().length > 0) {
            Rectangle currBrick = players[turn].getBricksArr()[players[turn].getBricksArr().length - 1];
            //TODO change to logical units (make moveBrickLogical?)
            if (isFalling) {
                boolean shouldStop = false;

                for (int i = 0; i < currBrick.getWidth() / 25; i++) {
                    if (players[turn].isSpotTaken(currBrick.getLogicalY() + 1, currBrick.getLogicalX() + i)) {
                        shouldStop = true;
                    }
                }
                if (shouldStop) {
                    isFalling = false;
                } else {
                    currBrick.moveBrick(currBrick.getX(), currBrick.getY() + 25);
                }

                // stop falling if reached floor
                if (currBrick.getLogicalY() == 15) {
                    isFalling = false;
                }

                if (!isFalling) {
                    for (int i = 0; i < currBrick.getWidth() / 25; i++) {
                        players[turn].setFilledSpot(currBrick.getLogicalY(), currBrick.getLogicalX() + i, true);
                    }

                    turn = (turn + 1) % 2;
                    isDroppingStage = false;
                    checkStability(turn);
                    if (turn == 0) {
                        selector = 0;
                    }
                    else {
                        selector = 3;
                    }
                }
                repaint();
            }

        }
    }

    public void keyTyped(KeyEvent e) {

    }


    public void keyPressed(KeyEvent e) {

        int turnBasedStartingX = brickStartingX;
        if (turn == 1) {
            turnBasedStartingX += 360;
        }

        if (e.getKeyCode() == KeyEvent.VK_ALT) {
            showCredits = false;
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            showCredits = !showCredits;
            repaint();
        }

        //****** Brick Selection Stage *****//
        if (!isDroppingStage) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (gameEnded > 0) {
                    gameEnded = -1;
                    repaint();
                }
                addBrickToPlayer(brickSelection[selector]);
                Rectangle currBrick = players[turn].getBricksArr()[players[turn].getBricksArr().length - 1];
                currBrick.moveBrick(turnBasedStartingX, brickStartingY);

                for (int i=0; i<players[(turn + 1) % 2].getBricksArr().length; i++) {
                    if (currBrick.getWidth() == players[(turn + 1) % 2].getBricksArr()[i].getWidth() && currBrick.getColor() == players[(turn + 1) % 2].getBricksArr()[i].getColor()) {
                        removeFromFilled(players[(turn + 1) % 2].getBricksArr()[i], (turn + 1) % 2);
                        removeBrick(i, (turn + 1) % 2);
                    }
                }

                int x = convertSelectorToPixels(selector);
                brickSelection[selector] = createRandomBrick(x, GAME_HEIGHT - FLOOR_HEIGHT + 40);
                selector = -1; // selector not showing
                repaint();
                isDroppingStage = !isDroppingStage;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if (turn == 0 && selector > 0) {
                    selector -= 1;
                    repaint();
                }
                if (turn == 1 && selector > 3) {
                    selector -= 1;
                    repaint();
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (turn == 0 && selector < 2) {
                    selector += 1;
                    repaint();
                }
                if (turn == 1 && selector < 5) {
                    selector += 1;
                    repaint();
                }
            }
        }

        //****** Brick Dropping Stage *****//
        else {

            if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                isFalling = true;
                isDroppingStage = true;
            }
            if (players[turn].getBricksArr().length > 0) {
                Rectangle currBrick = players[turn].getBricksArr()[players[turn].getBricksArr().length - 1];

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (currBrick.getX() > turnBasedStartingX) {
                        currBrick.moveBrick(currBrick.getX() - GRID_UNIT, currBrick.getY());
                    }
                    repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (currBrick.getX() + currBrick.getWidth() < turnBasedStartingX + BUILDING_MAX_WIDTH) {
                        currBrick.moveBrick(currBrick.getX() + GRID_UNIT, currBrick.getY());
                    }
                    repaint();
                }
            }

        }

    }

    // the random function is for 1 of 3 brick colors and for 1 of 3 brick sizes
    public static Rectangle createRandomBrick(int x, int y) {
        Random randomno = new Random();
        int color = 1 + randomno.nextInt(3);
        int width = ((1 + randomno.nextInt(3)) * GRID_UNIT) + GRID_UNIT;
        return new Rectangle(width, GRID_UNIT, level, color, x, y);
    }

    public void keyReleased(KeyEvent e) {

    }


    private void addBrickToPlayer(Rectangle brick) {
        Rectangle[] newBricks = new Rectangle[players[turn].getBricksArr().length + 1];
        for (int i = 0; i < players[turn].getBricksArr().length; i++) {
            newBricks[i] = players[turn].getBricksArr()[i];
        }
        newBricks[players[turn].getBricksArr().length] = brick;
        players[turn].setBricksArr(newBricks);
    }



    private void removeBrick(int index, int player) {
        Rectangle[] newBricks = new Rectangle[players[player].getBricksArr().length - 1];
        int j = 0;
        for (int i = 0; i < newBricks.length; i++) {
            if (j == index) {
                j++;
            }
            newBricks[i] = players[player].getBricksArr()[j];
            j++;
        }
        players[player].setBricksArr(newBricks);
    }



    public void removeFromFilled(Rectangle rec, int player) {
        int brickY, brickX;
            for (int j = 0; j < (rec.getWidth()) / 25; j++) {
                brickY = rec.getLogicalY();
                brickX = rec.getLogicalX();
                players[player].setFilledSpot(brickY, brickX + j, false);
        }
    }

    private void checkStability(int player) {
        boolean stable = false;

        for (int i=0; i<players[player].getBricksArr().length; i++) {
            if (players[player].getBricksArr()[i].getLogicalY() == 15) {
                continue;
            }
            for (int j=0; j<players[player].getBricksArr()[i].getWidth()/25; j++) {
                System.out.println("started printing");
                System.out.println(players[player].getFilledLocs()[players[player].getBricksArr()[i].getLogicalY() + 1][players[player].getBricksArr()[i].getLogicalX() + j]);
                stable |= players[player].getFilledLocs()[players[player].getBricksArr()[i].getLogicalY() + 1][players[player].getBricksArr()[i].getLogicalX() + j];
            }
        }
        if (players[player].getBricksArr().length == 0) {
            stable = true;
        }
        if (players[player].getBricksArr().length == 1 && players[player].getBricksArr()[0].getLogicalY() == 15) {
            stable = true;
        }
        if (!stable) {
            endGame(player);
        }
    }

    private void endGame(int looser ) {
        gameEnded = looser + 1;
        players[(looser + 1) % 2].incrementScore();
        level = (level +1 ) % 5;
        for (int i=0; i<players.length; i++) {
            players[i].clearData();
        }
        fillBrickSelectionArray();
    }
}
