import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class X_o_Game extends Thread {
//    private static final int DIMENTION_TABLE = 3;
    private static final boolean PRINT_LOG_ENABLE = true;
    // gameType : 0 = you vs you; 1 = you vs network; 2 = you vs comp ; 0 - default
    private static int gameType = 0;
    private boolean blockUserNextMove = false; // блокировка хода игрока на время хода оппонента
    private String x_or_0;
    private String x_or_o_Comp;
    private String x_or_o_User;
    private JPanel panel1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JButton button9;
    private JButton newButton;
    private JRadioButton x_RadioButton;
    private JRadioButton O_RadioButton;
    private JTextArea textArea1;
    private JRadioButton YourselfRadioButton;
    private JRadioButton CompRadioButton;
    private JRadioButton networkGameRadioButton;
    private JTextField textField1;
    private JRadioButton serverRadioButton;
    private JRadioButton clientRadioButton;
    private JButton connectButton;
    private JButton stopSrvButton;
    private JButton[] buttons = new JButton[]{button1, button2, button3, button4, button5, button6, button7, button8, button9};
    private String endGameString = "";
    private InetAddress serverAddress;
    public static final int PORT = 8080;
    public static boolean runServer = false;
    public x_o_GameServer t;
    public static boolean waitForNextTurn = false;
    Socket socket;
    protected static volatile GameNetPacket sp = null;


    public X_o_Game() throws IOException {
        textField1.setEnabled(false);
        endGameString = "";
        serverRadioButton.setEnabled(false);
        clientRadioButton.setEnabled(false);
        //x_o_GameServer t = new x_o_GameServer(PORT);

        newButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                clearGame();
            }
        });

        for(final JButton b : buttons){
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if ( b.isEnabled() && !waitForNextTurn )
                        setTextToButton( b, x_or_o_User );
                }
            });
        }
        x_RadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                    setX_or_0("x");
                    printLog("UP : x");
                    x_or_o_User = "x"; x_or_o_Comp = "o";
                if ( gameType != 0 ){
                    x_RadioButton.setEnabled(false);
                    O_RadioButton.setEnabled(false);
                }
            }
        });

        O_RadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                setX_or_0("o");
                printLog( "UP : 0");
                x_or_o_User = "o"; x_or_o_Comp = "x";
                if ( gameType != 0 ){
                    x_RadioButton.setEnabled(false);
                    O_RadioButton.setEnabled(false);
                }
                if (gameType == 2) compMove();
            }
        });
        YourselfRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                gameType = 0;
                waitForNextTurn = false;
                x_RadioButton.setEnabled(true);
                O_RadioButton.setEnabled(true);
                serverRadioButton.setEnabled(false);
                clientRadioButton.setEnabled(false);
                connectButton.setEnabled(false);
                textField1.setEnabled(false);
                clearGame();
                printLog("Gametype set to 0");
            }
        });
        CompRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                serverRadioButton.setEnabled(false);
                clientRadioButton.setEnabled(false);
                x_RadioButton.setEnabled(true);
                O_RadioButton.setEnabled(true);
                clearGame();
                connectButton.setEnabled(false);
                gameType = 2;
                waitForNextTurn = false;
                textField1.setEnabled(false);
                printLog("Gametype set to 2");
            }
        });
        networkGameRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                textField1.setEnabled(true);
                serverRadioButton.setEnabled(true);
                clientRadioButton.setEnabled(true);
                clearGame();
            }
        });
        serverRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start server
                gameType = 1;
                clientRadioButton.setEnabled(false);
                connectButton.setEnabled(false);
                printLog("Start server on port " + PORT);
                x_o_GameServer t = new x_o_GameServer(PORT);
                x_o_GameServer.stop = false;
                O_RadioButton.doClick();
                t.start();
            }
        });
        clientRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Start client connection
                gameType = 1;
                connectButton.setEnabled(true);
                serverRadioButton.setEnabled(false);
                x_RadioButton.doClick();
                waitForNextTurn = true;
                printLog("Client game choisen");
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Client connect's to server
                try {
                    socket = new Socket("127.0.0.1",PORT);
                    //exchangePacket();
                    //socket.close();
                }
                catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });


        stopSrvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                printLog("Try to interrupt server...");
                x_o_GameServer.stop = true;
                printLog("After try to interrupt server...");
            }
        });
    }

    private GameNetPacket exchangePacket(GameNetPacket packet){
        GameNetPacket pClient = null;
        try {
            ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream oIS = new ObjectInputStream(socket.getInputStream());
            oOS.writeObject(packet);
            pClient = (GameNetPacket) oIS.readObject();
            System.out.println(pClient);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return pClient;
    }

    public String getX_or_0() {
        return x_or_0;
    }

    public void setX_or_0(String x_or_0) {
        this.x_or_0 = x_or_0;
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public void setPanel1(JPanel panel1) {
        this.panel1 = panel1;
    }
    public void printLog(String s){
        if (PRINT_LOG_ENABLE){
            //textArea1.setText(textArea1.getText() + "\n" + s);
            System.out.println(s);
        }
    }
    private void setTextToButton(JButton button, String x_or_o){
        if (gameType == 2){
            if (x_RadioButton.isEnabled()) x_RadioButton.setEnabled(false);
            if (O_RadioButton.isEnabled()) O_RadioButton.setEnabled(false);
        }
        if (!blockUserNextMove){
            if (button.isEnabled()) button.setText(x_or_o);
                button.setEnabled(false);
            }
        if (gameType == 0 ) {

        }
        if (gameType == 1 ) {
            if (x_or_o.equals("x")) { //client
                try{
                    printLog(exchangePacket(new GameNetPacket(button, button.getText())).getDataString());

                } catch (Exception e1){
                    e1.printStackTrace();
                }
            }
            else { //server
                sp = new GameNetPacket(button,button.getText());
            }
        }
        if (gameType == 2 ) {
            compMove();
        }

        if (checkEndGame(x_or_o)) {
            printLog(endGameString);
            stopGame();
        }
    }
    private Boolean checkEndGame(String x_or_o){
        //123,456,789,147,258,369,159,357 = выйгрыш
        if ( (button1.getText().equals(x_or_o) && button2.getText().equals(x_or_o) && button3.getText().equals(x_or_o)) ||
                (button4.getText().equals(x_or_o) && button5.getText().equals(x_or_o) && button6.getText().equals(x_or_o)) ||
                (button7.getText().equals(x_or_o) && button8.getText().equals(x_or_o) && button9.getText().equals(x_or_o)) ||
                (button1.getText().equals(x_or_o) && button4.getText().equals(x_or_o) && button7.getText().equals(x_or_o)) ||
                (button2.getText().equals(x_or_o) && button5.getText().equals(x_or_o) && button8.getText().equals(x_or_o)) ||
                (button3.getText().equals(x_or_o) && button6.getText().equals(x_or_o) && button9.getText().equals(x_or_o)) ||
                (button1.getText().equals(x_or_o) && button5.getText().equals(x_or_o) && button9.getText().equals(x_or_o)) ||
                (button3.getText().equals(x_or_o) && button5.getText().equals(x_or_o) && button7.getText().equals(x_or_o))
                ) {
                  endGameString = x_or_o + " win game!";
                  return true;
        }
        else
            if  (!button1.isEnabled() && !button2.isEnabled() && !button3.isEnabled() && !button4.isEnabled() &&
                    !button5.isEnabled() && !button6.isEnabled() && !button7.isEnabled() && !button8.isEnabled() &&
                    !button9.isEnabled() && endGameString.equals("")){
                endGameString = "Ничья";
                return true;
            }
        else
            return false;
    }
    private void clearGame(){
        endGameString = "";
        for (JButton b : buttons){
            b.setText("*");
            b.setEnabled(true);
        }
        x_RadioButton.setEnabled(true);
        O_RadioButton.setEnabled(true);
        waitForNextTurn = false;
        printLog("New game start");
    }

    private void compMove(){
        //computer move
        if (endGameString.equals("")){
            if (x_RadioButton.isEnabled()) x_RadioButton.setEnabled(false);
            if (O_RadioButton.isEnabled()) O_RadioButton.setEnabled(false);
            JButton b;
            if ((b = compChoiseNextCell()) != null){
                b.setText(x_or_o_Comp);
                b.setEnabled(false);
                printLog("Comp move done");
            }
            if (checkEndGame(x_or_o_Comp)) {
                printLog(endGameString);
                stopGame();
            }
        }
    }
    private JButton compChoiseNextCell(){
            //123,456,789,147,258,369,159,357 = выйгрыш

            if (button1.getText().equals(x_or_o_Comp) && button2.getText().equals(x_or_o_Comp) && button3.isEnabled()) return button3;
            if (button1.getText().equals(x_or_o_Comp) && button3.getText().equals(x_or_o_Comp) && button2.isEnabled()) return button2;
            if (button2.getText().equals(x_or_o_Comp) && button3.getText().equals(x_or_o_Comp) && button1.isEnabled()) return button1;

            if (button4.getText().equals(x_or_o_Comp) && button5.getText().equals(x_or_o_Comp) && button6.isEnabled()) return button6;
            if (button4.getText().equals(x_or_o_Comp) && button6.getText().equals(x_or_o_Comp) && button5.isEnabled()) return button5;
            if (button5.getText().equals(x_or_o_Comp) && button6.getText().equals(x_or_o_Comp) && button4.isEnabled()) return button4;

            if (button7.getText().equals(x_or_o_Comp) && button8.getText().equals(x_or_o_Comp) && button9.isEnabled()) return button9;
            if (button7.getText().equals(x_or_o_Comp) && button9.getText().equals(x_or_o_Comp) && button8.isEnabled()) return button8;
            if (button9.getText().equals(x_or_o_Comp) && button8.getText().equals(x_or_o_Comp) && button7.isEnabled()) return button7;

            if (button1.getText().equals(x_or_o_Comp) && button4.getText().equals(x_or_o_Comp) && button7.isEnabled()) return button7;
            if (button1.getText().equals(x_or_o_Comp) && button7.getText().equals(x_or_o_Comp) && button4.isEnabled()) return button4;
            if (button7.getText().equals(x_or_o_Comp) && button4.getText().equals(x_or_o_Comp) && button1.isEnabled()) return button1;

            if (button2.getText().equals(x_or_o_Comp) && button5.getText().equals(x_or_o_Comp) && button8.isEnabled()) return button8;
            if (button2.getText().equals(x_or_o_Comp) && button8.getText().equals(x_or_o_Comp) && button5.isEnabled()) return button5;
            if (button8.getText().equals(x_or_o_Comp) && button5.getText().equals(x_or_o_Comp) && button2.isEnabled()) return button2;

            if (button3.getText().equals(x_or_o_Comp) && button6.getText().equals(x_or_o_Comp) && button9.isEnabled()) return button9;
            if (button3.getText().equals(x_or_o_Comp) && button9.getText().equals(x_or_o_Comp) && button6.isEnabled()) return button6;
            if (button9.getText().equals(x_or_o_Comp) && button6.getText().equals(x_or_o_Comp) && button3.isEnabled()) return button3;

            if (button1.getText().equals(x_or_o_Comp) && button5.getText().equals(x_or_o_Comp) && button9.isEnabled()) return button9;
            if (button1.getText().equals(x_or_o_Comp) && button9.getText().equals(x_or_o_Comp) && button5.isEnabled()) return button5;
            if (button9.getText().equals(x_or_o_Comp) && button5.getText().equals(x_or_o_Comp) && button1.isEnabled()) return button1;

            if (button3.getText().equals(x_or_o_Comp) && button5.getText().equals(x_or_o_Comp) && button7.isEnabled()) return button7;
            if (button3.getText().equals(x_or_o_Comp) && button7.getText().equals(x_or_o_Comp) && button5.isEnabled()) return button5;
            if (button7.getText().equals(x_or_o_Comp) && button5.getText().equals(x_or_o_Comp) && button3.isEnabled()) return button3;
            //------------
            if (button1.getText().equals(x_or_o_User) && button2.getText().equals(x_or_o_User) && button3.isEnabled()) return button3;
            if (button1.getText().equals(x_or_o_User) && button3.getText().equals(x_or_o_User) && button2.isEnabled()) return button2;
            if (button2.getText().equals(x_or_o_User) && button3.getText().equals(x_or_o_User) && button1.isEnabled()) return button1;
            
            if (button4.getText().equals(x_or_o_User) && button5.getText().equals(x_or_o_User) && button6.isEnabled()) return button6;
            if (button4.getText().equals(x_or_o_User) && button6.getText().equals(x_or_o_User) && button5.isEnabled()) return button5;
            if (button5.getText().equals(x_or_o_User) && button6.getText().equals(x_or_o_User) && button4.isEnabled()) return button4;
            
            if (button7.getText().equals(x_or_o_User) && button8.getText().equals(x_or_o_User) && button9.isEnabled()) return button9;
            if (button7.getText().equals(x_or_o_User) && button9.getText().equals(x_or_o_User) && button8.isEnabled()) return button8;
            if (button9.getText().equals(x_or_o_User) && button8.getText().equals(x_or_o_User) && button7.isEnabled()) return button7;
            
            if (button1.getText().equals(x_or_o_User) && button4.getText().equals(x_or_o_User) && button7.isEnabled()) return button7;
            if (button1.getText().equals(x_or_o_User) && button7.getText().equals(x_or_o_User) && button4.isEnabled()) return button4;
            if (button7.getText().equals(x_or_o_User) && button4.getText().equals(x_or_o_User) && button1.isEnabled()) return button1;
            
            if (button2.getText().equals(x_or_o_User) && button5.getText().equals(x_or_o_User) && button8.isEnabled()) return button8;
            if (button2.getText().equals(x_or_o_User) && button8.getText().equals(x_or_o_User) && button5.isEnabled()) return button5;
            if (button8.getText().equals(x_or_o_User) && button5.getText().equals(x_or_o_User) && button2.isEnabled()) return button2;
            
            if (button3.getText().equals(x_or_o_User) && button6.getText().equals(x_or_o_User) && button9.isEnabled()) return button9;
            if (button3.getText().equals(x_or_o_User) && button9.getText().equals(x_or_o_User) && button6.isEnabled()) return button6;
            if (button9.getText().equals(x_or_o_User) && button6.getText().equals(x_or_o_User) && button3.isEnabled()) return button3;
            
            if (button1.getText().equals(x_or_o_User) && button5.getText().equals(x_or_o_User) && button9.isEnabled()) return button9;
            if (button1.getText().equals(x_or_o_User) && button9.getText().equals(x_or_o_User) && button5.isEnabled()) return button5;
            if (button9.getText().equals(x_or_o_User) && button5.getText().equals(x_or_o_User) && button1.isEnabled()) return button1;
            
            if (button3.getText().equals(x_or_o_User) && button5.getText().equals(x_or_o_User) && button7.isEnabled()) return button7;
            if (button3.getText().equals(x_or_o_User) && button7.getText().equals(x_or_o_User) && button5.isEnabled()) return button5;
            if (button7.getText().equals(x_or_o_User) && button5.getText().equals(x_or_o_User) && button3.isEnabled()) return button3;

            if (button1.getText().equals(x_or_o_User) && button9.getText().equals(x_or_o_User) && button2.isEnabled()) return button2;
            if (button1.getText().equals(x_or_o_User) && button9.getText().equals(x_or_o_User) && button4.isEnabled()) return button4;

            if (button3.getText().equals(x_or_o_User) && button7.getText().equals(x_or_o_User) && button2.isEnabled()) return button2;
            if (button3.getText().equals(x_or_o_User) && button7.getText().equals(x_or_o_User) && button6.isEnabled()) return button6;

            if (button5.isEnabled()) return button5;
            if (button1.isEnabled()) return button1;
            if (button3.isEnabled()) return button3;
            if (button7.isEnabled()) return button7;
            if (button9.isEnabled()) return button9;
            if (button2.isEnabled()) return button2;
            if (button4.isEnabled()) return button4;
            if (button6.isEnabled()) return button6;
            if (button8.isEnabled()) return button8;


       
        return null;
    }
    public void stopGame(){
        for (JButton b : buttons){
            if ( b.isEnabled() ) b.setEnabled(false);
        }
        if (socket.isConnected()) {
            try {
                socket.close();
            }
            catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }
}
