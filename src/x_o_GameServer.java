import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class x_o_GameServer extends Thread/*implements Runnable */{

    private int port;
    public static volatile boolean stop = false;
    private final ByteBuffer buffer = ByteBuffer.allocate(16384);

    public x_o_GameServer(int port) {
        this.port = port;
    }
    public void run() {
        //server code
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            Socket socket = ss.accept();
            while (!stop){
                //Thread.sleep(50);
                ObjectInputStream oIS = new ObjectInputStream(socket.getInputStream());
                GameNetPacket pServer = (GameNetPacket) oIS.readObject();

                X_o_Game.updategame(pServer);
                prLog(pServer.toString());
                if (X_o_Game.sendServerPacket) {
                    ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
                    oOS.writeObject(X_o_Game.sp);
                    X_o_Game.sendServerPacket = false;
                }
                prLog("Stop =  " + stop);
            }
            socket.close();
            ss.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void prLog(String s){
        System.out.println(s);
    }

}
