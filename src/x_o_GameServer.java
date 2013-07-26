import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class x_o_GameServer extends Thread{
    private InetAddress addr;
    private int port;
    private Selector selector;
    private Map<SocketChannel,List<byte[]>> dataMap;

    public x_o_GameServer(InetAddress addr, int port) throws IOException {
        this.addr = addr;
        this.port = port;
        dataMap = new HashMap<SocketChannel,List<byte[]>>();
        //startServer();
    }

//    private void startServer() throws IOException {
    public void run() {
        // create selector and channel
        try {
            this.selector = Selector.open();
            printLog("Selector.open()");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        ServerSocketChannel serverChannel = null;
        try {
            serverChannel = ServerSocketChannel.open();
            printLog("ServerSocketChannel.open()");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            serverChannel.configureBlocking(false);
            printLog("serverChannel.configureBlocking(false)");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // bind to port
        InetSocketAddress listenAddr = new InetSocketAddress(this.addr, this.port);
        try {
            serverChannel.socket().bind(listenAddr);
            printLog("serverChannel.socket().bind(listenAddr)");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            printLog("serverChannel.register(this.selector, SelectionKey.OP_ACCEPT)");
        } catch (ClosedChannelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        printLog("Echo server ready. Ctrl-C to stop.");

        // processing
        while (isInterrupted()) {
            // wait for events
            try {
                this.selector.select();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            // wakeup to work on selected keys
            Iterator keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();
                printLog("SelectionKey key = (SelectionKey) keys.next()");

                // this is necessary to prevent the same key from coming up
                // again the next time around.
                keys.remove();

                if (! key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    try {
                        this.accept(key);
                        printLog("this.accept(key)" + key);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                else if (key.isReadable()) {
                    try {
                        this.read(key);
                        printLog("this.read(key)" + key);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                else if (key.isWritable()) {
                    try {
                        this.write(key);
                        printLog("this.write(key)" + key);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);

        // write welcome message
//        channel.write(ByteBuffer.wrap("Welcome, this is the echo server\r\n".getBytes("US-ASCII")));

        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        printLog("Connected to: " + remoteAddr);

        // register channel with selector for further IO
        dataMap.put(channel, new ArrayList<byte[]>());
        channel.register(this.selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int numRead = -1;
        try {
            numRead = channel.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (numRead == -1) {
            this.dataMap.remove(channel);
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            printLog("Connection closed by client: " + remoteAddr);
            channel.close();
            key.cancel();
            return;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        printLog("Got: " + new String(data, "US-ASCII"));

        // write back to client
        doEcho(key, data);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        List<byte[]> pendingData = this.dataMap.get(channel);
        Iterator<byte[]> items = pendingData.iterator();
        while (items.hasNext()) {
            byte[] item = items.next();
            items.remove();
            channel.write(ByteBuffer.wrap(item));
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private void doEcho(SelectionKey key, byte[] data) {
        SocketChannel channel = (SocketChannel) key.channel();
        List<byte[]> pendingData = this.dataMap.get(channel);
        pendingData.add(data);
        key.interestOps(SelectionKey.OP_WRITE);
    }

/*
    public void run(){
        // Server thread
        //NIO test


// Old blocking method
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
*/
    private void printLog(String s){
        System.out.println(s);
    }
}
