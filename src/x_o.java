import javax.swing.*;

public class x_o {
    public static void main (String[] args){
        JFrame frame = new JFrame("MyForm");
        frame.setContentPane(new X_o_Game().getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
