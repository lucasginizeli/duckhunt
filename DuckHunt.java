import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;

class GameArea extends JPanel {
    DuckHunt game;
    Network network;
    boolean gameRunning = false;
    Gun gunA = new Gun(this, 1);
    Gun gunB = new Gun(this, 2);
    Duck duck = new Duck(this);
    Image background;

    GameArea(DuckHunt game, Network network) {
        this.game = game;
        this.network = network;
        setPreferredSize(new Dimension(800, 600));
        loadBackground();

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

    void loadBackground() {
        try {
            background = ImageIO.read(new File("background.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot load background.\n" + e, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getSize().width, getSize().height, this);
        duck.draw(g);
        Rectangle rect = duck.sensitiveArea;
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
        gunA.draw(g);
        if (gameRunning) {
            gunB.draw(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    public void countdown(Score score) {
        game.updateScore("Starts in 3  ", score);
        network.fetchEventType();
        game.updateScore("Starts in 2  ", score);
        network.fetchEventType();
        game.updateScore("Starts in 1  ", score);
        network.fetchEventType();
        game.updateScore(score);
    }

    void startGame() {
        new Thread() {
            Position posGunA = new Position(0, 0);
            Position posGunB = new Position(0,0);
            Position posDuck = new Position(0, 0);
            Score score = game.score();

            @Override
            public void run() {
                countdown(score);
                gameRunning = true;
                while(network.hasData()) {
                    String eventType = network.fetchEventType();
                    network.fetchPosition(posGunA, posGunB, posDuck);
                    try {
                        switch (eventType) {
                            case "MOVE":
                                duck.shows(posDuck.x, posDuck.y);
                                gunA.position(posGunA.x, posGunA.y);
                                gunB.position(posGunB.x, posGunB.y);
                                duck.duckHit(false);
                                break;
                            case "WINNER":
                                gunA.shoot();
                                duck.duckHit(true);
                                network.fetchScore(score);
                                game.updateScore(score);
                                game.updateScore("You WIN! ", score);
                                sleep(3000);
                                score = game.newScore();
                                game.updateScore(score);
                                break;
                            case "LOSER":
                                gunB.shoot();
                                duck.duckHit(true);
                                network.fetchScore(score);
                                game.updateScore(score);
                                game.updateScore("You LOSE!  ", score);
                                sleep(3000);
                                score = game.newScore();
                                game.updateScore(score);
                                break;
                            case "HIT SHOT":
                                gunA.shoot();
                                duck.duckHit(true);
                                network.fetchScore(score);
                                game.updateScore(score);
                                break;
                            case "OPPONENT HIT SHOT":
                                gunB.shoot();
                                duck.duckHit(true);
                                network.fetchScore(score);
                                game.updateScore(score);
                            case "MISS":
                                gunA.shoot();
                                break;
                            case "OPPONENT MISS":
                                gunB.shoot();
                                break;
                        }
                    } catch (InterruptedException e) {

                    }
                    repaint();
                }
            }
        }.start();
    }

}

public class DuckHunt extends JFrame {
    Score score = new Score(0,0);
    JLabel labelScore = new JLabel("", JLabel.CENTER);
    Network network = new Network(this, "127.0.0.1", 12345);
    GameArea gameArea = new GameArea(this, network);

    public DuckHunt() {
        super("Duck Hunt");
        updateScore(new Score(0, 0));
        add(labelScore, BorderLayout.NORTH);
        add(gameArea, BorderLayout.CENTER);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void updateScore(Score score1) {
        score = score1;
        labelScore.setText("Eu: " + score1.pointsA + "x Advers??rio: " + score1.pointsB);
    }

    public void updateScore(String winner, Score score1) {
        labelScore.setText(winner + "Eu: " + score.pointsA + "x Advers??rio: " + score.pointsB);
    }

    public Score score() {
        return score;
    }

    public Score newScore(){
        score = new Score(0,0);
        return score;
    }


    public static void main(String[] args) {
        new DuckHunt();
    }

}
