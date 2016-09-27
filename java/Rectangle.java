import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.*;


/**
 * Created by nemzer on 9/15/2016.
 */
public class Rectangle {

    private int width;
    private int height;
    private int x;
    private int y;
    private int color;
    Image brickImage;


    // colors are 1 = yellow, 2 = cream, 3 = brown
    public Rectangle(int width, int height, int level, int color, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.color = color;

        String imageFileName = constructImageFileName(level);

        try {
            this.brickImage = ImageIO.read(getClass().getResource(imageFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String constructImageFileName(int level) {
        String imageFileName = "Brick";
        switch (level) {
            case 0: imageFileName += "Pyramid";
                break;
            case 1: imageFileName += "Space";
                break;
            case 2: imageFileName += "London";
                break;
            case 3: imageFileName += "Torii";
                break;
            case 4: imageFileName += "Sea";
                break;
            default: imageFileName += "Pyramid";
        }
        imageFileName += Integer.toString(this.width);
        switch (color) {
            case 1: imageFileName += "Y";
                break;
            case 2: imageFileName += "C";
                break;
            case 3: imageFileName += "B";
        }
        imageFileName += ".png";
        return imageFileName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getBrickImage() {
        return brickImage;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void moveBrick(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public int getColor() {
        return color;
    }




    // TODO Move these two to game class
    public int getLogicalX() {
        if (x < 360) {
            return (x - 80) / 25;
        }
        else {
            return (x - 360 - 80) / 25;
        }
    }

    public int getLogicalY() {
        return (y - 60) / 25;
    }
}
