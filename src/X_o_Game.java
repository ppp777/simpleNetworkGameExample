import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class X_o_Game {
//    private static final int DIMENTION_TABLE = 3;
    private static final boolean PRINT_LOG_ENABLE = true;
    // gameType : 0 = you vs you; 1 = you vs network; 2 = you vs comp ; 0 - default
    private static int gameType = 0;
    private boolean blockUserNextMove = false; // блокировка хода игрока на время хода оппонента
    private String x_or_0;
    private String x_or_y_Comp;
    private String x_or_y_User;
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
    private JButton[] buttons = new JButton[]{button1, button2, button3, button4, button5, button6, button7, button8, button9};

    public X_o_Game() {
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
                    setTextToButton(b, x_or_y_User);
                }
            });
        }
        x_RadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                    setX_or_0("x");
                    printLog("UP : x");
                    x_or_y_User = "x"; x_or_y_Comp = "o";
            }
        });

        O_RadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                setX_or_0("O");
                printLog( "UP : 0");
                x_or_y_User = "0"; x_or_y_Comp = "x";
            }
        });
        YourselfRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                gameType = 0;
                printLog("Gametype set to 0");
            }
        });
        CompRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                gameType = 2;
                printLog("Gametype set to 2");
            }
        });
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
    private void printLog(String s){
        if (PRINT_LOG_ENABLE){
            textArea1.setText(textArea1.getText()+ "\n" + s);
        }
    }
    private void setTextToButton(JButton button, String x_or_o){
        if (!blockUserNextMove){
            if (button.isEnabled()) button.setText(x_or_o);
                button.setEnabled(false);
            }
        if (gameType == 0 ) {

        }
        if (gameType == 1 ) {

        }
        if (gameType == 2 ) {
            compMove();
        }

        if (checkEndGame(x_or_o)) {
            printLog(x_or_o+" win game!");
            clearGame();
        }
    }
    private Boolean checkEndGame(String x_or_y){
        //123,456,789,147,258,369,159,357 = выйгрыш
        if ( (button1.getText().equals(x_or_y) && button2.getText().equals(x_or_y) && button3.getText().equals(x_or_y)) ||
                (button4.getText().equals(x_or_y) && button5.getText().equals(x_or_y) && button6.getText().equals(x_or_y)) ||
                (button7.getText().equals(x_or_y) && button8.getText().equals(x_or_y) && button9.getText().equals(x_or_y)) ||
                (button1.getText().equals(x_or_y) && button4.getText().equals(x_or_y) && button7.getText().equals(x_or_y)) ||
                (button2.getText().equals(x_or_y) && button5.getText().equals(x_or_y) && button8.getText().equals(x_or_y)) ||
                (button3.getText().equals(x_or_y) && button6.getText().equals(x_or_y) && button9.getText().equals(x_or_y)) ||
                (button1.getText().equals(x_or_y) && button5.getText().equals(x_or_y) && button9.getText().equals(x_or_y)) ||
                (button3.getText().equals(x_or_y) && button5.getText().equals(x_or_y) && button7.getText().equals(x_or_y)) 
                ) {
                  return true;
        }
        else
            return false;
    }
    private void clearGame(){
        button1.setText("*");button2.setText("*");button3.setText("*");button4.setText("*");
        button5.setText("*");button6.setText("*");button7.setText("*");button8.setText("*");
        button9.setText("*");
        button1.setEnabled(true);button2.setEnabled(true);button3.setEnabled(true);
        button4.setEnabled(true);button5.setEnabled(true);button6.setEnabled(true);
        button7.setEnabled(true);button8.setEnabled(true);button9.setEnabled(true);
        if (gameType >0 ) blockUserNextMove = false; // unblock user move
        printLog("New game start");
    }
    private void compMove(){
        //computer move
        blockUserNextMove = true;
        for (JButton b : buttons){
            if ( b.isEnabled() ) {
                b.setText(x_or_y_Comp);
                b.setEnabled(false);
                break;
            }
        }
        printLog("Comp move done");
        blockUserNextMove = false;
    }
}
