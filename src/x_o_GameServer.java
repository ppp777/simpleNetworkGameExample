import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class x_o_GameServer extends Thread{
    public void run(){
        // Server thread
        try {
            ServerSocket s = new ServerSocket(X_o_Game.PORT);
            try {
                // Блокирует до тех пор, пока не возникнет соединение:
                Socket socket = s.accept();
                try {
                    printLog("Connection accepted: " + socket);
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    // Вывод автоматически выталкивается из буфера PrintWriter'ом
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), true);
                    while (!isInterrupted()) {
                        String str = in.readLine();
                        printLog("Echoing: " + str);
                        out.println(str);
                    }
                    // Всегда закрываем два сокета...
                }
                finally {
                    printLog("closing...");
                    socket.close();
                }
            }
            finally {
                printLog("closing 2...");
                s.close();
            }
            printLog("Server game start, wait for client:");
        } catch (IOException e1) {
            printLog("Error server start : " + e1.getMessage());
        }

    }
    private void printLog(String s){
        System.out.println(s);
    }
}
