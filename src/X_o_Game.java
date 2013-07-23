import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class X_o_Game {
    private static final int DIMENTION_TABLE = 3;
    private String x_or_0;
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

    public X_o_Game() {
        newButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button1.setText("*");button2.setText("*");button3.setText("*");button4.setText("*");
                button5.setText("*");button6.setText("*");button7.setText("*");button8.setText("*");
                button9.setText("*");
                button1.setEnabled(true);button2.setEnabled(true);button3.setEnabled(true);
                button4.setEnabled(true);button5.setEnabled(true);button6.setEnabled(true);
                button7.setEnabled(true);button8.setEnabled(true);button9.setEnabled(true);
                textArea1.setText("");
            }
        });
        x_RadioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                setX_or_0("x");
                textArea1.setText(textArea1.getText() +"\nUP : x");
            }
        });
        O_RadioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                setX_or_0("O");
                textArea1.setText(textArea1.getText() +"\nUP : 0");
            }
        });
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button1.setText(x_or_0);
                button1.setEnabled(false);
            }
        });

        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button2.setText(x_or_0);
                button2.setEnabled(false);
            }
        });

        button3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button3.setText(x_or_0);
                button3.setEnabled(false);
            }
        });

        button4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button4.setText(x_or_0);
                button4.setEnabled(false);
            }
        });

        button5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button5.setText(x_or_0);
                button5.setEnabled(false);
            }
        });

        button6.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button6.setText(x_or_0);
                button6.setEnabled(false);
            }
        });
        button7.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button7.setText(x_or_0);
                button7.setEnabled(false);
            }
        });
        button8.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button8.setText(x_or_0);
                button8.setEnabled(false);
            }
        });
        button9.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                button9.setText(x_or_0);
                button9.setEnabled(false);
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

}
