import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

class GameArea extends JPanel {
    DuckHunt game;
    Network network;
    boolean gameRunning = false;
    Gun gunA = new Gun(this, 1);
    Gun gunB = new Gun(this, 2);
//    TODO: pato

    GameArea(DuckHunt game, Network network) {
        this.game = game;
        this.network = network;
        setPreferredSize(new Dimension(800, 600));

        checkMouseClick();
        checkMouseMoviment();
        startGame();
    }

    void checkMouseClick() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameRunning) {
                    network.sendPosition("MP", e.getX(), e.getY());
                    network.forceFlush();
                } else {
                    gunA.shoot();
                }
            }
        });
    }

    void checkMouseMoviment() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameRunning) {
                    network.sendPosition("MM", e.getX(), e.getY());
                    network.forceFlush();
                } else {
                    gunA.position(e.getX(), e.getY());
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        Rectangle rect = duck.sensitiveArea;
//        g.drawRect(rect.y, rect.y, rect.width, rect.height);
        gunA.draw(g);
        if (gameRunning) {
            gunB.draw(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    void startGame() {
        new Thread() {
            Position posGunA = new Position(0, 0);
            Position posGunB = new Position(0,0);

            @Override
            public void run() {
//                TODO: regressiva
                gameRunning = true;
                while(network.hasData()){
                    String eventType = network.fetchEventType();
                    network.fetchPosition(posGunA, posGunB);
                    switch (eventType){
                        case "MOVE":
                            gunA.position(posGunA.x, posGunA.y);
                            gunB.position(posGunB.x, posGunB.y);
                            break;
                        case "WINNER":
//                            TODO: implementar placar
                            break;
                    }
                    repaint();
                }
            }
        }.start();
    }

}

public class DuckHunt extends JFrame {
//    Score score new Score(0,0);  TODO
//    lPlacar  TODO
    Network network = new Network(this, "127.0.0.1", 12345);
    GameArea gameArea = new GameArea(this, network);

    public DuckHunt() {
        super("Duck Hunt");
//        TODO: adicionar placar
//        TODO: tentar adicionar background

        add(gameArea, BorderLayout.CENTER);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new DuckHunt();
    }

}
