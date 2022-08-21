import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Gun {
    Image gunStand, gunShot;
    JPanel game;
    int positionX = 0, positionY = 0;
    int dx = 0, dy = 30;
    boolean shooting = false;

    public Gun(JPanel game, int n) {
        try {
            this.game = game;
//            TODO: dinamizar para mais de um jogador
            gunStand = ImageIO.read(new File("Screenshot_1.png"));
            gunShot = ImageIO.read(new File("Screenshot_2.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(game, "The image cannot be loaded.\n" + e, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void position(int x, int y) {
        positionX = x;
        positionY = y;
        shooting = false;
        game.repaint();
    }

    public void shoot() {
        shooting = true;
        game.repaint();
    }

    public void draw(Graphics g) {
        if (shooting) {
            g.drawImage(gunShot, positionX - dx, positionY - dy, game);
            shooting = false;
        } else {
            g.drawImage(gunStand, positionX - dx, positionY - dy, game);
        }
    }
}
