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
            while (!stop){
                Socket socket = ss.accept();
                Thread.sleep(50);
                ObjectInputStream oIS = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
                GameNetPacket pServer = (GameNetPacket) oIS.readObject();
                System.out.println(pServer);
//                oOS.writeObject(new GameNetPacket(1,"Return from server "+pServer));
                oOS.writeObject(X_o_Game.sp);
                socket.close();
                prLog("Stop =  " + stop);
            }
            ss.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void prLog(String s){
        System.out.println(s);
    }

}
