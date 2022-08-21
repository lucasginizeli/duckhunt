import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Network {
    Socket socket = null;
    DataInputStream is = null;
    DataOutputStream os = null;
    DuckHunt game;
    boolean hasData = true;

    public Network(DuckHunt game, String IP, int port) {
        this.game = game;
        try {
            socket = new Socket(IP, port);
            os = new DataOutputStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException e ) {
            JOptionPane.showMessageDialog(game, "Server not found.\n" + e, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(game, "Can't reach the server\n" + e, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void sendPosition(String eventType, int x, int y) {
        try {
            os.writeUTF(eventType);
            os.writeInt(x);
            os.writeInt(y);
        } catch (IOException e) {
            hasData = false;
        }
    }

    public void fetchPosition(Position positionGunA, Position positionGunB) {
        try {
//            TODO: implementar posicao do pato
            positionGunA.x = is.readInt();
            positionGunA.y = is.readInt();
            positionGunB.x = is.readInt();
            positionGunB.y = is.readInt();
        } catch (IOException e) {
            hasData = false;
        }
    }

    public void forceFlush() {
        try {
            os.flush();
        } catch (IOException e) {
            hasData = false;
        }
    }

    public boolean hasData() {
        return hasData;
    }

    public String fetchEventType() {
        try {
            return is.readUTF();
        } catch (IOException e) {
            hasData = false;
            return "";
        }
    }

}
