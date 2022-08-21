import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Duck {
    JPanel game;
    Image duck, duckDead;
    boolean visible = false, duckHit = false;
    final int dx = 5, dy = 5;
    int positionX = 0, positionY = 0;
    Rectangle sensitiveArea = new Rectangle(positionX, positionY, 50, 50);

    public Duck(JPanel game) {
        try {
            game = this.game;
            duck = ImageIO.read(new File("duck1.png"));
            duckDead = ImageIO.read(new File("DuckDead.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(game, "The image cannot be loaded.\n" + e, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void shows(int x, int y) {
        positionX = x;
        positionY = y;
        sensitiveArea.setLocation(positionX, positionY);
        visible = true;
    }

    public void duckHit(boolean state) {
        duckHit = state;
    }

    public void draw(Graphics g) {
        if (visible)
            if (duckHit) {
                g.drawImage(duckDead, positionX - dx, positionY - dy, game);
            } else {
                g.drawImage(duck, positionX - dx, positionY - dy, game);
            }
    }
}
